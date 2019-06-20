package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysResource;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.vo.SysUserForm;

import java.util.List;
import java.util.Map;

/**
 * Created by sofn
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    SysUser findByUserName(String username);

    /**
     * 按条件查询用户
     * @param params  条件
     * @param pageNo  页数
     * @param pageSize  每页显示多少条
     * @return
     */
    PageUtils<SysUserForm> findAllUserList(Map<String,Object> params, Integer pageNo, Integer pageSize );

    /**
     * 添加用户
     * @param sysUserForm 传入的用户信息
     */
    void saveSysUser(SysUser sysUserForm);

    /**
     * 修改用户
     * @param sysUserForm  修改的用户信息
     */
    void updateSysUser(SysUser sysUserForm);

    /**
     * 批量添加用户
     * @param sysUsers    用户信息集合
     */
    void batchSave(List<SysUser> sysUsers);

    /**
     * 删除用户
     * @param id  要删除用户ID
     */
    void deleteSysUser(String id);

    /**
     * 批量删除用户
     * @param ids  用户ID
     */
    void batchDelete(List<String> ids);

    /**
     * 更改密码
     * @param  id   ID
     * @param oldPassword  旧密码
     * @param newPassword  新密码
     */
    void updatePassword(String id,String oldPassword,String newPassword);


    /**
     * 加载用户角色列表
     * @param id
     * @return
     */
    List<SysRole> loadRolesByUserId(String id);

    /**
     * 加载各角色对应的权限列表
     * @param roleIds
     * @return
     */
    List<SysResource> loadResourceByRoleIds(List<String> roleIds);
}
