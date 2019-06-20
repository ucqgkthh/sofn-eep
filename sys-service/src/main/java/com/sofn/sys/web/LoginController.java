package com.sofn.sys.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import com.sofn.common.constants.Constants;
import com.sofn.common.exception.ExceptionType;
import com.sofn.common.exception.SofnException;
import com.sofn.common.log.SofnLog;
import com.sofn.common.model.Result;
import com.sofn.common.utils.BoolUtils;
import com.sofn.common.utils.JsonUtils;
import com.sofn.common.utils.JwtUtils;
import com.sofn.common.utils.RedisHelper;
import com.sofn.sys.model.Auth;
import com.sofn.sys.model.SysResource;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.service.SysResourceService;
import com.sofn.sys.service.SysUserService;
import com.sofn.sys.util.shiro.ShiroUtils;
import com.sofn.sys.vo.SysCaptchaVo;
import com.sofn.sys.vo.SysResourceForm;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.management.relation.Role;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by sofn
 */
@SuppressWarnings("ALL")
@Api(tags = "登录相关接口")
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysResourceService sysResourceService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private Producer producer;

    @SofnLog("获取验证码")
    @ApiOperation(value = "获取验证码")
    @GetMapping("/captcha")
    public Result<SysCaptchaVo> captcha(HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        // 生成文字验证码
        String text = producer.createText();
        // 生成图片验证码
        BufferedImage image = producer.createImage(text);
        String captchaBase64 = encodeBase64ImgCode(image);
        String id = UUID.randomUUID().toString();
        // 验证码存储在redis中，有效时间5分钟
        redisHelper.set(id, text, 5*60);

        return Result.ok(new SysCaptchaVo(id,captchaBase64));
    }

    @SofnLog("登录")
    @GetMapping("/login")
    @ApiOperation(value = "登录")
    public Result<Auth> login(
            @ApiParam(value = "用户名") @RequestParam String username,
            @ApiParam(value = "密码") @RequestParam String password,
            @ApiParam(value = "验证码id") @RequestParam String captchaId,
            @ApiParam(value = "验证码") @RequestParam String captcha,
            @ApiParam(value = "记住密码,Y:记住 N:不记住") @RequestParam(required = false,defaultValue = "N") String rememberMe) {

        if (StringUtils.isBlank(username)){
            throw new SofnException("参数username未找到");
        }

        if (StringUtils.isBlank(password)){
            throw new SofnException("password");
        }

        if (StringUtils.isBlank(captchaId)){
            throw new SofnException("captchaId");
        }

        if (StringUtils.isBlank(captcha)){
            throw new SofnException("captcha");
        }

        if (StringUtils.isBlank(captchaId) || StringUtils.isBlank(captcha)) {
            throw new SofnException(ExceptionType.CAPTCHA_NOT_INPUT);
        }

        // 获取redis中缓存的验证码
        String cacheCaptcha = (String) redisHelper.get(captchaId);
        if (!StringUtils.equalsIgnoreCase(captcha, cacheCaptcha)){
            throw new SofnException(ExceptionType.CAPTCHA_CORRECT);
        }

        SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            throw new SofnException(ExceptionType.USER_NOT_EXIST);
        }

        String saltPassword = ShiroUtils.sha256(password,user.getSalt());
        if (!StringUtils.equals(saltPassword, user.getPassword())){
            throw new SofnException(ExceptionType.PASSWORD_ERROR);
        }

        List<String> resourcePermissionList = new ArrayList<>();
        // 超级管理员拥有全部权限
        if (Constants.UserSession.superAdminId.equals(user.getId())){
            List<SysResourceForm> list = sysResourceService.getAllResource();
            list.forEach(sysResource -> {
                resourcePermissionList.add(sysResource.getPermission());
            });
        }else {
            // 查询角色权限列表
            List<SysRole> roleList= sysUserService.loadRolesByUserId(user.getId());
            List<SysResourceForm> resourceList = new ArrayList<>();
            roleList.forEach(sysRole -> {
                List<SysResourceForm> resourceTempList= sysResourceService.getAllResourceByRoleId(sysRole.getId());
                if (resourceTempList != null && resourceTempList.size()>0){
                    resourceList.addAll(resourceTempList);
                }
            });

            resourceList.forEach(SysResourceForm -> {
                resourcePermissionList.add(SysResourceForm.getPermission());
            });
        }

        user.setResourceList(resourcePermissionList);
        // 生成token
        String token = JwtUtils.generateToken(username);
        // 缓存登录信息
        redisHelper.set(user.getUsername(), token);
        redisHelper.hset(token, Constants.UserSession.id, user.getId());
        redisHelper.hset(token, Constants.UserSession.username, username);
        redisHelper.hset(token, Constants.UserSession.permissions, JsonUtils.obj2json(resourcePermissionList));

        // 设置过期时间
        if (BoolUtils.isTrue(rememberMe)) {
            redisHelper.hset(token, Constants.UserSession.rememberMe, rememberMe);
            redisHelper.expire(token, Constants.UserSession.rememberExpireTime);
            redisHelper.expire(user.getUsername(), Constants.UserSession.rememberExpireTime);
        }else {
            redisHelper.expire(token, Constants.UserSession.expireTime);
            redisHelper.expire(user.getUsername(), Constants.UserSession.expireTime);
        }

        return Result.ok(new Auth(token, user));
    }

    @SofnLog("退出登录")
    @ApiOperation("退出登录")
    @GetMapping("/signout")
    public Result logout() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isBlank(token)){
            throw new SofnException("token为空");
        }

        Object usernameObj = redisHelper.hget(token, Constants.UserSession.username);
        String username = usernameObj==null?null:usernameObj.toString();
        if (!StringUtils.isBlank(username)) {
            redisHelper.del(username,token);
            return Result.ok();
        }

        return Result.error();
    }

    /**
     * 验证码转base64字符串
     */
    private String encodeBase64ImgCode(BufferedImage image) throws ServletException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean flag = ImageIO.write(image, "JPEG", out);
        byte[] b = out.toByteArray();
        String imgString = Base64.encodeBase64String(b);
        return "data:image/JPEG;base64," + imgString;
    }
}
