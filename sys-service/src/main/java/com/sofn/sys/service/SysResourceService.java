package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysResource;
import com.sofn.sys.vo.SysResourceForm;
import java.util.Set;
import java.util.Map;
import java.util.List;

/**
 * Created by sofn
 */
public interface SysResourceService extends IService<SysResource> {

   /* *//**
     * 根据资源名查询资源
     * @param resourceName
     * @return
     *//*
    SysResource findByResourceName(String resourceName);

    *//**
     * 查询所有资源
     * @return
     *//*
    PageUtils<SysResource> findAllResourceList(Map<String, Object> params);*/

    void createResource(SysResource resource);
    void updateResource(SysResource resource);
    void deleteResource(String id);

    PageUtils<SysResourceForm> getSysResourceByContionPage(Map<String,Object> paramas,Integer pageNo,Integer pageSize);

    /**
     * 得到资源对应的权限字符串
     * @param resourceIds
     * @return
     */
    Set<String> queryPermissions(Set<String> resourceIds);

    /**
     * 根据用户权限得到菜单
     * @param permissions
     * @return
     */
    List<SysResourceForm> findMenus(Set<String> permissions);

    /**
     * get所有菜单
     */
    List<SysResourceForm> getAllResource();
    /**
            * 根据子系统ID获取所属的所有菜单
     */
    List<SysResourceForm> getAllResourceBySubsystemId(String id);
    /**
     * 根据roleID获取所属的所有菜单
     */
    List<SysResourceForm> getAllResourceByRoleId(String id);

    List<SysResourceForm> getChildPerms(List<SysResourceForm> list, String parentId);
}
