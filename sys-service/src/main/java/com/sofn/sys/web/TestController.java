package com.sofn.sys.web;

import com.sofn.common.model.Result;
import com.sofn.common.utils.DictUtils;
import com.sofn.common.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "测试")
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @Autowired
    RedisUtils redisUtils;

    @ApiOperation(value = "hello")
    @RequiresPermissions("test:hello")
    @PostMapping("/hello")
    public Result hello() {
        return Result.ok("hello,world!");
    }

    @ApiOperation(value = "hi")
    @RequiresPermissions("test:hi")
    @PostMapping("/hi")
    public Result hi() {
        return Result.ok("hi,world!");
    }

    @ApiOperation(value = "error")
    @PostMapping("/error")
    public void error() {
        boolean is = 1/0>0;
    }


    @ApiOperation(value = "redis")
    @PostMapping("/redis")
    public Result redis() {
        redisUtils.set("hello","world");
        return Result.ok(redisUtils.get("hello"));
    }

    @PostMapping("/notneed")
    public Result notNeedPermission() {
        DictUtils.getByType("sex");
        DictUtils.getByTypeAndKey("sex","man");
        return Result.ok("不需要权限可访问");
    }
}
