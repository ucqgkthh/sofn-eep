package com.sofn.common.constants;

public class Constants {

    private Constants(){}

    /**
     * 用户登录成功后缓存的数据键
     */
    public static class UserSession {
        public static final String id = "id";
        public static final String username = "username";
        public static final String role = "role";
        public static final String permissions = "permissions";
        public static final String rememberMe = "rememberMe";
        public static final String superAdminId = "0";
        public static final int expireTime = 15*60;
        public static final int rememberExpireTime = 30*24*60*60;
    }

}
