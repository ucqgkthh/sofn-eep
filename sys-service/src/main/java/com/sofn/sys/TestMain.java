package com.sofn.sys;


import com.alibaba.druid.filter.config.ConfigTools;
import com.sofn.sys.util.shiro.ShiroUtils;

public class TestMain {

    public static void main(String[] args){
        System.out.println(ShiroUtils.sha256("superadmin","1234567890"));
    }

}
