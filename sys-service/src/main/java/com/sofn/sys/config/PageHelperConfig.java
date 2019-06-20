package com.sofn.sys.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * mybaits 分页插件 PageHelper配置类
 *
 * Created by heyongjie on 2019/5/29 17:15
 */
@Configuration
public class PageHelperConfig {

    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        properties.setProperty("dialect","oracle");    //配置oracle数据库的方言
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
