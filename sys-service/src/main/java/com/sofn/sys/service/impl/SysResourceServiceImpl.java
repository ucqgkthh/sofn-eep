package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sofn.common.exception.SofnException;
import com.sofn.sys.enums.ResourceType;
import com.sofn.sys.enums.SysManageEnum;
import java.util.UUID;
import com.sofn.sys.util.Constants;
import com.sofn.sys.vo.SysResourceForm;
import com.sofn.common.utils.PageUtils;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.util.StringUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.mapper.SysResourceMapper;
import com.sofn.sys.mapper.SysSubsystemMapper;
import com.sofn.sys.model.SysResource;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.model.SysSubsystem;
import com.sofn.sys.service.SysResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.HashSet;
import java.util.*;
/**
 * Created by sofn
 */

@Service(value = "sysResourceService")
@Slf4j
public class SysResourceServiceImpl extends
        ServiceImpl<SysResourceMapper,SysResource> implements SysResourceService {
    @Autowired
    private SysResourceMapper sysResourceMapper;
    @Autowired
    private SysSubsystemMapper sysSubsystemMapper;

    @Autowired
    private RedisUtils redisUtils;
/*
    public SysResource findByResourceName(String resourceName) {
        List<SysResource> list = list(new QueryWrapper<SysResource>(new SysResource(resourceName)));
        if (list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public PageUtils<SysResource> findAllResourceList(Map<String, Object> params) {
        IPage<SysResource> page = this.page(
                new Query<SysResource>().getPage(params),
                null
        );

        return new PageUtils(page);
    }*/

    @Override
    public PageUtils<SysResourceForm> getSysResourceByContionPage(Map<String, Object> paramas, Integer pageNo, Integer pageSize) {

        PageHelper.startPage(pageNo,pageSize);
        List<SysResource> sysResourceList = sysResourceMapper.getSysResourceByContion(paramas);
        List<SysResourceForm> resourceFormList=new ArrayList<SysResourceForm>();
        for(int i=0;i<sysResourceList.size();i++){
            //获取父菜单名
            SysResourceForm sysResourceForm=new SysResourceForm(sysResourceList.get(i));
            BeanUtils.copyProperties(sysResourceList.get(i),sysResourceForm);
            SysSubsystem sysSubsystem = sysSubsystemMapper.selectById(sysResourceList.get(i).getSubsystemId());
            SysResource sysResourceParent= sysResourceMapper.selectById(sysResourceList.get(i).getParentId());
            //如果为一级菜单,父菜单名称为子系统名称
            if(sysResourceList.get(i).getParentId().equals(Constants.MENU_ROOT_ID)){
                sysResourceForm.setParentName(sysSubsystem.getSubsystemName());
            }else{
                sysResourceForm.setParentName(sysResourceParent.getResourceName());
            }
            sysResourceForm.setSysSubsystemName(sysSubsystem.getSubsystemName());
            resourceFormList.add(sysResourceForm);
        }
        PageInfo<SysResource> pageInfo = new PageInfo<SysResource>(sysResourceList);
        return PageUtils.getPageUtils(pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createResource(SysResource resource) {
        // 1. 校验内容是否重复
        checkSysResourceIsExists(resource.getResourceName(), resource.getResourceUrl(),null);
        // 2. 设置默认值
        resource.setId(UUID.randomUUID().toString());
        resource.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());
        SysResource rs=new SysResource();
        rs.setParentId(resource.getParentId());
        SysResource parent = sysResourceMapper.selectOne(new QueryWrapper<>(rs));
        resource.setParentIds(parent.makeSelfAsParentIds());
        if (resource.getType() == ResourceType.MENU) {
            if (StringUtils.isEmpty(resource.getResourceUrl())) {
                resource.setResourceUrl("#");
            }
        }
        resource.setCreateTime(new Date());
        resource.setCreateUserId(getLoginUserId());
//        sysResource.setReservedField1("TEMP");
        // 3. 保存
        sysResourceMapper.insert(resource);
        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_RESOURCE_CACHE_KEY.getCode());
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResource(SysResource resource) {

        SysResource selectSysResource = sysResourceMapper.selectById(resource.getId());
        if (selectSysResource == null) {
            throw new SofnException("待修改内容不存在");
        }
        checkSysResourceIsExists(resource.getResourceName(), resource.getResourceUrl(),resource.getId());
        BeanUtils.copyProperties(resource,selectSysResource);
        selectSysResource.setUpdateTime(new Date());
        selectSysResource.setUpdateUserId(getLoginUserId());
        sysResourceMapper.updateById(selectSysResource);
        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_RESOURCE_CACHE_KEY.getCode());
    }

    public boolean checkSysResourceIsExists(String name, String url,String id) {
        //检查是否重复
        if (id != null) {
            // 修改
            // 检查名称是否重复
            Integer number = sysResourceMapper.getSysResourceByNameOrUrl(name, null,id);
            if (number > 1) throw new SofnException("菜单名称重复");
            // 检查Code是否重复
            number = sysResourceMapper.getSysResourceByNameOrUrl(null, url,id);
            if (number > 1) throw new SofnException("菜单URL重复");
        } else {
            // 添加
            // 检查名称是否重复
            Integer number = sysResourceMapper.getSysResourceByNameOrUrl(name, null,null);
            if (number > 0) throw new SofnException("菜单名称重复");
            // 检查Code是否重复
            number = sysResourceMapper.getSysResourceByNameOrUrl(null, url,null);
            if (number > 0) throw new SofnException("菜单URL重复");
        }
        return false;
    }

    @Override
    public Set<String> queryPermissions(Set<String> resourceIds) {
        Set<String> permissions = new HashSet<>();
        for (String resourceId : resourceIds) {
            SysResource resource = sysResourceMapper.selectById(resourceId);
            if (resource != null && !StringUtils.isEmpty(resource.getPermission())) {
                permissions.add(resource.getPermission());
            }
        }
        return permissions;
    }

    @Override
    public List<SysResourceForm> findMenus(Set<String> permissions) {
   /*     Weekend weekend = Weekend.of(Resource.class);
        weekend.setOrderByClause("priority");*/
        List<SysResource> allResources = sysResourceMapper.selectResourceListByPriority();
        List<SysResourceForm> menus = new ArrayList<>();
        for (SysResource resource : allResources) {
            if (resource.isRootNode()) {
                continue;
            }
            if (resource.getType() != ResourceType.MENU) {
                continue;
            }
            if (!hasPermission(permissions, resource)) {
                continue;
            }
            menus.add(new SysResourceForm(resource));
        }
        return menus;
    }

    private boolean hasPermission(Set<String> permissions, SysResource resource) {
        if (StringUtils.isEmpty(resource.getPermission())) {
            return true;
        }
        for (String permission : permissions) {
            WildcardPermission p1 = new WildcardPermission(permission);
            WildcardPermission p2 = new WildcardPermission(resource.getPermission());
            if (p1.implies(p2) || p2.implies(p1)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void deleteResource(String id) {
        SysResource selectSysResource = sysResourceMapper.selectById(id);
        // 2. 如果下面有子节点的不能删除
        List<SysResource> sysResourceList = sysResourceMapper.getSonSysResource(selectSysResource.getId());
        if(sysResourceList != null && sysResourceList.size() > 0){
            throw new SofnException("当前菜单下有子菜单，删除失败");
        }
        // 使用软删除
        selectSysResource.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        updateResource(selectSysResource);
    }
    @Override
    public List<SysResourceForm> getAllResourceByRoleId(String RoleId){
        List<SysResource> sysResourceList = sysResourceMapper.getAllResourceByRoleId(RoleId);
        List<SysResourceForm> resourceFormList=new ArrayList<SysResourceForm>();
        for(int i=0;i<sysResourceList.size();i++){
            //获取父菜单名
            SysResourceForm sysResourceForm=new SysResourceForm(sysResourceList.get(i));
            BeanUtils.copyProperties(sysResourceList.get(i),sysResourceForm);
            SysSubsystem sysSubsystem = sysSubsystemMapper.selectById(sysResourceList.get(i).getSubsystemId());
            SysResource sysResourceParent= sysResourceMapper.selectById(sysResourceList.get(i).getParentId());
            //如果为一级菜单,父菜单名称为子系统名称
            if(sysResourceList.get(i).getParentId().equals(Constants.MENU_ROOT_ID)){
                sysResourceForm.setParentName(sysSubsystem.getSubsystemName());
            }else{
                sysResourceForm.setParentName(sysResourceParent.getResourceName());
            }
            sysResourceForm.setSysSubsystemName(sysSubsystem.getSubsystemName());
            resourceFormList.add(sysResourceForm);
        }

        return resourceFormList;
    }
    @Override
    public List<SysResourceForm> getAllResourceBySubsystemId(String subsystemId){
        List<SysResource> sysResourceList = sysResourceMapper.getAllResourceBySubsystemId(subsystemId);
        List<SysResourceForm> resourceFormList=new ArrayList<SysResourceForm>();
        for(int i=0;i<sysResourceList.size();i++){
            //获取父菜单名
            SysResourceForm sysResourceForm=new SysResourceForm(sysResourceList.get(i));
            BeanUtils.copyProperties(sysResourceList.get(i),sysResourceForm);
            SysSubsystem sysSubsystem = sysSubsystemMapper.selectById(sysResourceList.get(i).getSubsystemId());
            SysResource sysResourceParent= sysResourceMapper.selectById(sysResourceList.get(i).getParentId());
            //如果为一级菜单,父菜单名称为子系统名称
            if(sysResourceList.get(i).getParentId().equals(Constants.MENU_ROOT_ID)){
                sysResourceForm.setParentName(sysSubsystem.getSubsystemName());
            }else{
                sysResourceForm.setParentName(sysResourceParent.getResourceName());
            }
            sysResourceForm.setSysSubsystemName(sysSubsystem.getSubsystemName());
            resourceFormList.add(sysResourceForm);
        }

        return resourceFormList;
    }

/*    SysUser sysUser = this.getById(id);
        if (sysUser == null) {
        throw new SofnException("待删除内容不存在");
    }
        sysUser.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        this.updateSysUser(sysUser);*/
    /**
     * 获取当前登录用户ID
     * @return
     */
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
    public List<SysResourceForm> getAllResource(){
        List<SysResource> sysResourceList= sysResourceMapper.getAllResource();
        List<SysResourceForm> resourceFormList=new ArrayList<SysResourceForm>();
        for(int i=0;i<sysResourceList.size();i++) {
            //获取父菜单名
            SysResourceForm sysResourceForm = new SysResourceForm(sysResourceList.get(i));
            BeanUtils.copyProperties(sysResourceList.get(i), sysResourceForm);
            SysSubsystem sysSubsystem = sysSubsystemMapper.selectById(sysResourceList.get(i).getSubsystemId());
            SysResource sysResourceParent = sysResourceMapper.selectById(sysResourceList.get(i).getParentId());
            //如果为一级菜单,父菜单名称为子系统名称
            if (sysResourceList.get(i).getParentId().equals(Constants.MENU_ROOT_ID)) {
                sysResourceForm.setParentName(sysSubsystem.getSubsystemName());
            } else {
                sysResourceForm.setParentName(sysResourceParent.getResourceName());
            }
            sysResourceForm.setSysSubsystemName(sysSubsystem.getSubsystemName());
            resourceFormList.add(sysResourceForm);
        }
        return resourceFormList;
    }
    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysResourceForm> getChildPerms(List<SysResourceForm> list, String parentId) {
        List<SysResourceForm> returnList = new ArrayList<SysResourceForm>();
        for (Iterator<SysResourceForm> iterator = list.iterator(); iterator.hasNext();)
        {
            SysResourceForm t = (SysResourceForm) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId().equals(parentId)) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysResourceForm> list, SysResourceForm t)
    {
        // 得到子节点列表
        List<SysResourceForm> childList = getChildList(list, t);
      /*  t.setChildren(childList);*/
        for (SysResourceForm tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                // 判断是否有子节点
                Iterator<SysResourceForm> it = childList.iterator();
                while (it.hasNext())
                {
                    SysResourceForm n = (SysResourceForm) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysResourceForm> getChildList(List<SysResourceForm> list, SysResourceForm t)
    {
        List<SysResourceForm> tlist = new ArrayList<SysResourceForm>();
        Iterator<SysResourceForm> it = list.iterator();
        while (it.hasNext())
        {
            SysResourceForm n = (SysResourceForm) it.next();
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysResourceForm> list, SysResourceForm t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}
