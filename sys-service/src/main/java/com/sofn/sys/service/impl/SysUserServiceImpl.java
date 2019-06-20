package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysRoleMapper;
import com.sofn.sys.mapper.SysUserMapper;
import com.sofn.sys.mapper.SysUserRoleMapper;
import com.sofn.sys.model.SysResource;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.model.SysUserRole;
import com.sofn.sys.service.SysUserService;
import com.sofn.sys.util.IdUtil;
import com.sofn.sys.util.UUIDTool;
import com.sofn.sys.util.UserUtil;
import com.sofn.sys.util.shiro.ShiroUtils;
import com.sofn.sys.vo.SysUserForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sofn
 */
@SuppressWarnings("ALL")
@Service(value = "sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

//    @Autowired
//    private SysRoleResourceMapper sysRoleResourceMapper;

    @Override
    public SysUser findByUserName(String username) {
        SysUser sysUser = this.baseMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        return sysUser;
    }

    @Override
    public PageUtils<SysUserForm> findAllUserList(Map<String, Object> params, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<SysUser> sysUsers = sysUserMapper.getSysUserByCondition(params);
        List<SysUserForm> sysUserFormList = new ArrayList<>();
        for(SysUser sysUser: sysUsers){
            SysUserForm sysUserForm = new SysUserForm();
            BeanUtils.copyProperties(sysUser,sysUserForm);
            sysUserFormList.add(sysUserForm);
        }

        PageInfo<SysUserForm> sysUserPageInfo = new PageInfo<SysUserForm>(sysUserFormList);
        return PageUtils.getPageUtils(sysUserPageInfo);
    }


    @Override
    @Transactional
    public void saveSysUser(SysUser sysUser) {
        // 添加用戶
        //1.检查账号是否重复
        checkExists(sysUser);
        //2. 设置常规值
        sysUser.setId(IdUtil.getUUId());
        sysUser.setCreateTime(new Date());
        sysUser.setCreateUserId(UserUtil.getLoginUserId());
//        sysUser.setStatus(SysManageEnum.STATUS_Y.getCode());
        sysUser.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());
        this.save(sysUser);

        //3.插入用户角色关联表
        List<SysUserRole> sysUserRoles = getSysUserRoleRelations(sysUser);
        sysUserRoleMapper.batchSaveSysUserRole(sysUserRoles);
    }

    @Override
    @Transactional
    public void updateSysUser(SysUser sysUser) {
        // 修改用户

        SysUser sysUser1 =  this.baseMapper.selectById(sysUser.getId());
        if(sysUser1 == null){
            throw new SofnException("待修改内容不存在");
        }
        //检查用户名是否重复
        checkExists(sysUser);

        sysUser.setUpdateTime(new Date());
        sysUser.setUpdateUserId(UserUtil.getLoginUserId());
        this.baseMapper.updateById(sysUser);

        //先解除用户跟角色的关系,
        sysUserRoleMapper.dropUserRolesByUserId(sysUser.getId());
        //再重新绑定用户角色关系
        List<SysUserRole> sysUserRoles = getSysUserRoleRelations(sysUser);
        if(null != sysUserRoles && sysUserRoles.size()>0){
            sysUserRoleMapper.batchSaveSysUserRole(sysUserRoles);
        }
    }

    /**
     * 检查是否重复
     *
     * @param sysUserForm 待检查信息
     * @return false 没有重复
     */
    private boolean checkExists(SysUser sysUser) {
        boolean flag = false;
        SysUser sysUser2 = this.findByUserName(sysUser.getUsername());
        if (StringUtils.isBlank(sysUser.getId())) {
            // 新增
            if(sysUser2 != null){
                throw new SofnException("账号重复");
            }
        } else {
            // 修改
            if(sysUser2 != null && !sysUser2.getId().equals(sysUser.getId())){
                throw new SofnException("账号重复");
            }
        }
        return flag;
    }

    @Override
    @Transactional
    public void batchSave(List<SysUser> sysUsers) {
        if(!CollectionUtils.isEmpty(sysUsers)){
            int addNum = 0;
            List<SysUser> sysUserList = Lists.newArrayList();
            // 每100条添加一次
            for (SysUser sysUser:  sysUsers) {
                if(sysUser != null){
                    sysUserList.add(sysUser);
                    addNum ++;
                    if(addNum % 100 == 0){
                        sysUserMapper.batchSave(sysUserList);
                        sysUserList.clear();
                        addNum = 0;
                    }
                }
            }
            if(!CollectionUtils.isEmpty(sysUserList)){
                sysUserMapper.batchSave(sysUserList);
            }
        }
    }

    @Override
    @Transactional
    public void deleteSysUser(String id) {
        // 1. 查询当前数据是否存在
        SysUser sysUser = this.getById(id);
        if (sysUser == null) {
            throw new SofnException("待删除内容不存在");
        }
        sysUser.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        this.updateSysUser(sysUser);
    }

    @Override
    @Transactional
    public void batchDelete(List<String> ids) {
        if(!CollectionUtils.isEmpty(ids)){
            ids.forEach((id) ->{
                deleteSysUser(id);
            });
        }
    }

    @Override
    @Transactional
    public void updatePassword(String id, String oldPassword, String newPassword) {
        //1 判断用户是否存在
        SysUser sysUser = this.getById(id);
        if(sysUser == null){
            throw  new SofnException("待修改用户不存在");
        }
        //2 判断密码是否正确
        String checkPassword  = ShiroUtils.sha256(oldPassword,sysUser.getSalt());
        if(!checkPassword.equals(sysUser.getPassword())){
            throw new SofnException("密码不正确，更改失败");
        }
        //3 更改密码
        String salt = IdUtil.getUUId();
        sysUser.setPassword(ShiroUtils.sha256(newPassword,salt));
        sysUser.setSalt(salt);
        this.updateSysUser(sysUser);
    }

    @Override
    public List<SysRole> loadRolesByUserId(String userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<SysResource> loadResourceByRoleIds(List<String> roleIds) {

        //TODO
        return new ArrayList<>();
    }


    private List<SysUserRole> getSysUserRoleRelations(SysUser sysUser) {
        if(sysUser.getRoleList()!=null){
            List<SysUserRole> sysUserRoles = new ArrayList<>();
            sysUser.getRoleList().forEach(role -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setId(UUIDTool.getUUID());
                sysUserRole.setUserId(sysUser.getId());
                sysUserRole.setRoleId(role.getId());
                sysUserRoles.add(sysUserRole);
            });
            return sysUserRoles;
        }
        return null;
    }
}
