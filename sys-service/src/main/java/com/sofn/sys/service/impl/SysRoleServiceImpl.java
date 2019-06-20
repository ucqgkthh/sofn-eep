package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.PageUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.mapper.SysRoleMapper;
import com.sofn.sys.mapper.SysRoleResourceMapper;
import com.sofn.sys.mapper.SysUserRoleMapper;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.model.SysRoleResource;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.model.SysUserRole;
import com.sofn.sys.service.SysRoleService;
import com.sofn.sys.util.UUIDTool;
import com.sofn.sys.vo.SysRoleForm;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sofn.sys.enums.SysManageEnum;
import java.util.*;

/**
 * Created by sofn
 */
@SuppressWarnings("ALL")
@Service(value = "sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper,SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleResourceMapper sysRoleResourceMapper;

    @Autowired
    private RedisUtils redisUtils;
   /* @Override
    public SysRole findByRoleName(String roleName) {
        List<SysRole> list = list(new QueryWrapper<SysRole>(new SysRole(roleName)));
        if (list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public PageUtils<SysRole> findAllRoleList(Map<String, Object> params) {
        IPage<SysRole> page = this.page(
                new Query<SysRole>().getPage(params),
                null
        );

        return new PageUtils(page);
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role) {
        // 1. 校验内容是否重复
        checkSysRoleIsExists(role.getRoleName(), null);
        // 2. 设置默认值
        role.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());

        role.setCreateTime(new Date());
        role.setCreateUserId(getLoginUserId());

        // 3. 保存
        sysRoleMapper.insert(role);
        //插入角色资源关联表
        List<SysRoleResource> sysRoleResources = getSysRoleResourceRelations(role);
        sysRoleResourceMapper.batchSaveSysRoleResource(sysRoleResources);

        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_ROLE_CACHE_KEY.getCode());
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role) {

        SysRole selectSysRole = sysRoleMapper.selectById(role.getId());
        if (selectSysRole == null || SysManageEnum.DEL_FLAG_Y.getCode().equals(selectSysRole.getDelFlag())) {
            throw new SofnException("待修改内容不存在");
        }
        // 2. 校验内容是否重复
        checkSysRoleIsExists(role.getRoleName(),role.getId());
        // 3. 更改内容
        BeanUtils.copyProperties(role, selectSysRole);
        selectSysRole.setUpdateTime(new Date());
        selectSysRole.setUpdateUserId(getLoginUserId());
        sysRoleMapper.updateById(selectSysRole);

        //先解除角色和资源的关系,
        sysRoleResourceMapper.dropRoleResourcesByRoleId(role.getId());
        //再重新绑定角色和资源关系
        List<SysRoleResource> sysRoleResources = getSysRoleResourceRelations(role);
        if(null != sysRoleResources && sysRoleResources.size()>0){
            sysRoleResourceMapper.batchSaveSysRoleResource(sysRoleResources);
        }


        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_ROLE_CACHE_KEY.getCode());
    }


    @Override
    public void deleteRole(String id) {
        SysRole selectSysRole = sysRoleMapper.selectById(id);
       //角色下面有用户的不能删
        Integer  countUser= sysUserRoleMapper.getUserCountOfRole(selectSysRole.getId());
        if( countUser > 0){
            throw new SofnException("角色下面有用户, 删除失败");
        }
        // 使用软删除
        selectSysRole.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        updateRole(selectSysRole);
    }

    @Override
    public PageUtils<SysRoleForm> getSysRoleByContion(Map<String, Object> paramas, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<SysRole> sysRoleList = sysRoleMapper.getSysRoleByCondition(paramas);
        List<SysRoleForm> sysRoleFormList = new ArrayList<>();
        for(SysRole sysRole: sysRoleList){
            SysRoleForm sysRoleForm = new SysRoleForm();
            BeanUtils.copyProperties(sysRole,sysRoleForm);
            sysRoleFormList.add(sysRoleForm);
        }

        PageInfo<SysRoleForm> pageInfo = new PageInfo<SysRoleForm>(sysRoleFormList);
        return PageUtils.getPageUtils(pageInfo);
    }


    public String getLoginUserId(){
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if(sysUser == null){
            // TODO 这里测试接口，先写一个默认值，最后需要改为抛出未登录异常
            return "defaultUserId";
        }else{
            return sysUser.getId();
        }
    }

    @Override
    public Set<String> queryRoles(Long... roleIds) {
        Set<String> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            SysRole role = sysRoleMapper.selectById(roleId);
            if (role != null) {
                roles.add(role.getRoleName());
            }
        }
        return roles;
    }

/*    @Override
    public Set<String> queryPermissions(Long[] roleIds) {
        Weekend<Role> weekend = Weekend.of(Role.class);
        weekend.weekendCriteria().andIn(Role::getId, Arrays.asList(roleIds));
        return sysRoleService.queryPermissions(
                roleMapper.selectByExample(weekend).stream().flatMap(r ->
                Arrays.asList(r.getRoleIds().split(",")).stream()
        ).map(String::valueOf).collect(Collectors.toSet()));
    }*/



    public boolean checkSysRoleIsExists(String name,String id) {
        //检查是否重复
        if (id != null) {
            // 修改
            // 检查名称是否重复
            Integer number = sysRoleMapper.getSysRoleByName(name,id);
            if (number > 0) throw new SofnException("角色名称重复");
        } else {
            // 添加
            // 检查名称是否重复
            Integer number = sysRoleMapper.getSysRoleByName(name,null);
            if (number > 0) throw new SofnException("角色名称重复");
        }
        return false;
    }


    private List<SysRoleResource> getSysRoleResourceRelations(SysRole sysRole) {
        if(sysRole.getResourceList()!=null){
            List<SysRoleResource> sysRoleResources = new ArrayList<>();
            sysRole.getResourceList().forEach(resource -> {
                SysRoleResource sysRoleResource = new SysRoleResource();
                sysRoleResource.setId(UUIDTool.getUUID());
                sysRoleResource.setRoleId(sysRole.getId());
                sysRoleResource.setResourceId(resource.getId());
                sysRoleResources.add(sysRoleResource);
            });
            return sysRoleResources;
        }
        return null;
    }
}
