package com.sofn.sys.web;


import com.sofn.common.log.SofnLog;
import com.sofn.common.model.Result;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.service.SysRoleService;
import com.sofn.sys.util.IdUtil;
import com.sofn.sys.util.UUIDTool;
import com.sofn.sys.vo.SysRoleForm;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * @author cjbi
 */
@RestController
@RequestMapping("/sysRole")
@Api(tags = "角色相关接口")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService roleService;

   /* @GetMapping
    public String rolePage(Model model) {
        model.addAttribute("resourceList", resourceService.queryAll());
        return "system/role";
    }*/
    @RequiresPermissions("sysRole:list")
    @ApiOperation(value = "按条件分页获取角色信息")
    @PostMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "delFlag", value = "状态"),
            @ApiImplicitParam(name = "roleName", value = "角色名"),
            @ApiImplicitParam(name = "describe", value = "角色描述"),
    })
    @SofnLog("按条件分页获取角色信息")
    public Result<SysRoleForm> getSysRoleByPage(@RequestBody Map<String,Object> params,
                                   @ApiParam(name = "pageNo",value = "分页序号",required = true) @RequestParam("pageNo")Integer pageNo,
                                   @ApiParam(name = "pageSize",value = "每页记录数量",required = true) @RequestParam("pageSize")Integer pageSize) {
        // 获取分页内容
        PageUtils<SysRoleForm> sysRolePageUtils = roleService.getSysRoleByContion(params,pageNo,pageSize);
        return Result.ok(sysRolePageUtils);
    }

    @RequiresPermissions("sysRole:getOne")
    @ApiOperation(value = "根据ID查询角色信息")
    @PostMapping("/getOne")
    @SofnLog("根据ID获取角色信息")
    public Result<SysRoleForm> getOne(@ApiParam(name = "id",value = "角色ID",required = true) @RequestParam("id")String id) {
        SysRole sysRole = roleService.getById(id);
        SysRoleForm sysRoleForm = new SysRoleForm();
        BeanUtils.copyProperties(sysRole,sysRoleForm);
        return Result.ok(sysRoleForm);
    }

    @RequiresPermissions("sysRole:create")
    @ApiOperation(value = "创建用户角色")
    @PostMapping("/createSysRole")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName",value = "角色名称",required = true),
            @ApiImplicitParam(name = "describe",value = "角色描述",required = true),
            @ApiImplicitParam(name = "resourceIds",value = "拥有资源ID(多个用\",\"分隔)",required = true),
    })
    @SofnLog("创建角色")
    public Result createSysRole(@Validated @RequestBody SysRoleForm sysRoleForm, BindingResult result) {
        sysRoleForm.setId(IdUtil.getUUId());
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysRole sysRole =  sysRoleForm.getSysRole(sysRoleForm);
        roleService.createRole(sysRole);
        return Result.ok();
    }


    @RequiresPermissions("sysRole:update")
    @ApiOperation(value = "更新用户角色")
    @PostMapping("/updateRole")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "ID",required = true),
            @ApiImplicitParam(name = "roleName",value = "角色名称",required = true),
            @ApiImplicitParam(name = "describe",value = "角色描述",required = true),
            @ApiImplicitParam(name = "resourceIds",value = "拥有资源ID(多个用\",\"分隔)",required = true),
    })
    @SofnLog("修改角色信息")
    public Result updateSysRole(@Validated @RequestBody SysRoleForm sysRoleForm, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysRole role =  sysRoleForm.getSysRole(sysRoleForm);
        roleService.updateRole(role);
        return Result.ok();
    }

    @RequiresPermissions("sysRole:delete")
    @ApiOperation(value = "删除角色")
    @PostMapping("/deleteRole")
    @SofnLog("删除角色")
    public Result delete(@ApiParam(name = "id",value = "角色ID",required = true) @RequestParam(value = "id")String id) {
        if(id == null  || "".equals(id)){
            return Result.error("ID不能为空");
        }
        roleService.deleteRole(id);
        return Result.ok();
    }

}
