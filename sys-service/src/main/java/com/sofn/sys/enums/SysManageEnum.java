package com.sofn.sys.enums;

import lombok.Getter;

/**
 *
 * 行政区划中的常量
 *
 * Created by heyongjie on 2019/5/29 11:27
 */
@Getter
public enum SysManageEnum {

    DEL_FLAG_Y("Y","已删除"),
    DEL_FLAG_N("N","未删除"),

    STATUS_Y("Y","有效"),
    STATUS_N("N","无效"),

    REGION_TYPE_PROVINCE("PROVINCE","省"),
    REGION_TYPE_CITY("CITY","市"),
    REGION_TYPE_COUNTY("COUNTY","县"),

    ROOT_LEVEL("100000","根节点代码"),
    SUBSYSTEM_ROOT_LEVEL("10000","子系统根节点代码"),
    SYS_GROUP_CACHE_KEY("sys_resource_cache_key","用户组缓存键"),
    SYS_RESOURCE_CACHE_KEY("sys_resource_cache_key","资源缓存键"),
    SYS_ROLE_CACHE_KEY("sys_role_cache_key","角色缓存键"),
    SYS_ORGANIZATION_CACHE_KEY("sys_resource_cache_key","组织缓存键"),
    SYS_REGION_CACHE_KEY("sys_region_cache_key","行政区划树结构缓存键"),
    SYS_SUBSYSTEM_CACHE_KEY("sys_subsystem_cache_key","子系统缓存键"),
    // 超时设置为4个小时
    SYS_MANAGE_CACHE_TIMEOUT((1000* 60 * 60 * 4) + "","系统管理超时时间");


    private String code;
    private String description;

    private SysManageEnum(String code, String description){
        this.code = code;
        this.description = description;
    }

}
