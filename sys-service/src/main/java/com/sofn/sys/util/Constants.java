package com.sofn.sys.util;

/**
 * Created by sofn
 */
public final class Constants {

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    /**
     * 当前用户
     */
    public static final String CURRENT_USER = "user";

    /**
     * 权限集合
     */
    public static final String PERMISSIONS = "permissions";
    public static final String SYSTEM_ROOT= "1";
    public static final String SYSTEM_NAME= "XXX系统";

    /**
     * 菜单根id
     */
    public static final String MENU_ROOT_ID = "0";

    /**
     * 菜单树
     */
    public static final String MENU_TREE = "menuTree";

    public static final String SHARP = "#";
    /**
     * 组织机构根id
     */
    public static final Long ORG_ROOT_ID = 0L;


    /**
     * 构造函数私有化，避免被实例化
     */
    private Constants() {
    }

}
