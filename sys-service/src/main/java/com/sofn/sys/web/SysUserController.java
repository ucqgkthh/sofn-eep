package com.sofn.sys.web;

import com.sofn.common.log.SofnLog;
import com.sofn.common.model.Result;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.service.SysUserService;
import com.sofn.sys.util.IdUtil;
import com.sofn.sys.util.shiro.ShiroUtils;
import com.sofn.sys.vo.SysUserForm;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sysUser")
@Api(tags = "用户相关接口")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService userService;

    @RequiresPermissions("sysUser:list")
    @PostMapping("/list")
    @ApiOperation(value = "分页查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "状态"),
            @ApiImplicitParam(name = "nickname", value = "昵称"),
            @ApiImplicitParam(name = "username", value = "用户名"),
            @ApiImplicitParam(name = "email", value = "邮箱"),
            @ApiImplicitParam(name = "mobile", value = "手机号")
    })
    @SofnLog("按条件分页获取用户信息")
    public Result<SysUserForm> queryList(@RequestBody Map<String,Object> params,
                            @ApiParam(name = "pageNo",value = "分页序号",required = true) @RequestParam("pageNo")Integer pageNo,
                            @ApiParam(name = "pageSize",value = "每页记录数量",required = true)@RequestParam("pageSize")Integer pageSize) {
        PageUtils<SysUserForm> pageUtils = userService.findAllUserList(params, pageNo, pageSize);
        return Result.ok(pageUtils);
    }


    @RequiresPermissions("sysUser:getOne")
    @PostMapping("/getOne")
    @ApiOperation(value = "根据ID查询用户")
    @SofnLog("按ID获取用户信息")
    public Result<SysUserForm> getOne(@ApiParam(name = "id",value = "用户ID",required = true) @RequestParam("id")String id) {
        SysUser sysUser = userService.getById(id);
        SysUserForm sysUserForm = new SysUserForm();
        BeanUtils.copyProperties(sysUser, sysUserForm);
        return Result.ok(sysUserForm);
    }


    /**
     * 添加用户
     * @return
     */
    @RequiresPermissions("sysUser:create")
    @ApiOperation(value = "创建用户")
    @PostMapping("/create")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nickname",value = "昵称",required = true),
            @ApiImplicitParam(name = "organizationId",value = "组织机构ID",required = true),
            @ApiImplicitParam(name = "username",value = "用户名",required = true),
            @ApiImplicitParam(name = "roleIds",value = "角色ID信息(多个角色用\",\"分隔)",required = true),
            @ApiImplicitParam(name = "status",value = "状态",required = true),
            @ApiImplicitParam(name = "email",value = "邮箱"),
            @ApiImplicitParam(name = "mobile",value = "手机号"),
    })
    @SofnLog("新建用户")
    public Result create(@Validated @RequestBody SysUserForm sysUserForm, BindingResult result) {
        String errMsg = this.getErrMsg(result);
        if (!StringUtils.isEmpty(errMsg)) {
            return Result.error(errMsg);
        }
        SysUser sysUser = SysUserForm.getSysUser(sysUserForm);

        // 生成密码和盐值
        String initPassword = IdUtil.getSixNumCode();
        String salt = IdUtil.getUUId();
        String password  = ShiroUtils.sha256(initPassword, salt);
        sysUser.setPassword(password);
        sysUser.setInitPassword(initPassword);
        sysUser.setSalt(salt);

        userService.saveSysUser(sysUser);
        return Result.ok();
    }

    /**
     * 修改用户的基本信息(不含密码)
     *
     * @param result 用户信息
     * @return
     */
    @RequiresPermissions("sysUser:update")
    @ApiOperation(value = "修改用户基本信息(不含密码)")
    @PostMapping("/update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "ID",required = true),
            @ApiImplicitParam(name = "nickname",value = "昵称",required = true),
            @ApiImplicitParam(name = "organizationId",value = "组织机构ID",required = true),
            @ApiImplicitParam(name = "username",value = "用户名",required = true),
            @ApiImplicitParam(name = "roleIds",value = "角色ID信息(多个角色用\",\"分隔)",required = true),
            @ApiImplicitParam(name = "status",value = "状态",required = true),
            @ApiImplicitParam(name = "email",value = "邮箱"),
            @ApiImplicitParam(name = "mobile",value = "手机号"),
    })
    @SofnLog("修改用户基本信息")
    public Result update(@Validated @RequestBody SysUserForm sysUserForm, BindingResult result) {
        String errMsg = this.getErrMsg(result);
        if (!StringUtils.isEmpty(errMsg)) {
            return Result.error(errMsg);
        }
        SysUser sysUser = SysUserForm.getSysUser(sysUserForm);
        userService.updateSysUser(sysUser);
        return Result.ok();
    }

    /**
     * 批量删除用户
     *
     * @param strIds
     * @return
     */
    @RequiresPermissions("sysUser:delete-batch")
    @ApiOperation(value = "批量删除用户")
    @PostMapping("/delete-batch")
    @SofnLog("批量删除用户")
    public Result deleteBatchByIds(@ApiParam(name = "ids",value = "用户ID(多个ID之间用\",\"分隔)",required = true) @RequestParam("ids") String strIds) {
       List<String> ids =  IdUtil.getIdsByStr(strIds);
        if(CollectionUtils.isEmpty(ids)){
            return Result.ok("没有需要删除的数据");
        }
        userService.batchDelete(ids);
        return Result.ok();
    }

    /**
     * 更改用户密码
     *
     * @param id          用户ID
     * @param newPassword 新的密码
     * @return
     */
    @RequiresPermissions("sysUser:change-pwd")
    @ApiOperation(value = "更新密码")
    @PostMapping("/change/password")
    @SofnLog("修改密码")
    public Result changePassword(@ApiParam(name = "id",value = "用户ID",required = true) @RequestParam("id") String id,
                                 @ApiParam(name = "oldPassword",value = "原始密码",required = true) @RequestParam("oldPassword")String oldPassword,
                                 @ApiParam(name = "newPassword",value = "新密码",required = true) @RequestParam("newPassword") String newPassword) {
        userService.updatePassword(id,oldPassword,newPassword);
        return Result.ok();
    }

}
