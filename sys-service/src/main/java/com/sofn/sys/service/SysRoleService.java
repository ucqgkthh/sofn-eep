package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.vo.SysRoleForm;

import java.util.Set;
import java.util.Map;

/**
 * Created by sofn
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据角色名查询角色
     * @param roleName
     * @return
     */
    /*SysRole findByRoleName(String roleName);*/

    /**
     * 查询所有角色
     * @return
     */

   /* PageUtils<SysRole> findAllRoleList(Map<String, Object> params);*/
    /**
     * 根据角色编号得到角色标识符列表
     * @param roleIds
     * @return
     */
    Set<String> queryRoles(Long... roleIds);

    /**
     * 根据角色编号得到权限字符串列表
     * @param
     * @return
     */
    /*Set<String> queryPermissions(Long[] roleIds);*/

    void createRole(SysRole Role);
    void updateRole(SysRole Role);
    void deleteRole(String id);

    PageUtils<SysRoleForm> getSysRoleByContion(Map<String,Object> paramas, Integer pageNo, Integer pageSize);

}
