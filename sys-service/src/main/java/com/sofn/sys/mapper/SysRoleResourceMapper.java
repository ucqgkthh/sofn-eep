package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysRoleResource;
import com.sofn.sys.model.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by heyongjie on 2019/6/11 9:59
 */
public interface SysRoleResourceMapper extends BaseMapper<SysRoleResource> {

    /**
     * 批量添加角色的资源信息
     * @param sysRoleResources   角色拥有的资源信息
     */
    void batchSaveSysRoleResource(@Param("sysRoleResources") List<SysRoleResource> sysRoleResources);

    /**
     * 解除角色和资源的关系
     * @param roleId
     */
    void dropRoleResourcesByRoleId(String roleId);

}
