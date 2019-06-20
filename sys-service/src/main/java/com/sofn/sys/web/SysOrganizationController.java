package com.sofn.sys.web;

import com.sofn.common.model.Result;
import com.sofn.sys.dto.TreeDto;
import com.sofn.sys.model.SysOrganization;
import com.sofn.sys.vo.SysOrganizationForm;

import com.sofn.common.utils.PageUtils;
import com.sofn.sys.service.SysOrganizationService;
import com.sofn.sys.util.UUIDTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * zhouqingchun
 * 2019/06/14
 */
@Controller
@Api(tags = "组织管理接口")
@RequestMapping("/sysOrganization")
public class SysOrganizationController extends BaseController {

    @Autowired
    private SysOrganizationService organizationService;

    /*@ApiOperation(value = "按条件分页获取组织机构信息")
    @PostMapping("/getSysOrganizationByPage")
    public Result getSysRoleByPage(@RequestBody Map<String,Object> params,Integer pageNo,Integer pageSize) {
        // 获取分页内容
        PageUtils<SysOrganization> sysOrganizationPageUtils = organizationService.getSysOrganizationByContion(params,pageNo,pageSize);
        return Result.ok(sysOrganizationPageUtils);
    }*/

    @ApiOperation(value = "加载组织树")
    @ResponseBody
    @RequestMapping(value = "{id}/load", method = RequestMethod.POST)
    public Result load(@PathVariable String id) {
        SysOrganization organization = organizationService.getSysOrganizationById(id);
        return Result.ok(organization);
    }

    @ResponseBody
    @PostMapping("/createOrganization")
    @ApiOperation(value = "添加组织树节点")
    public Result createOrganization(@Validated @RequestBody SysOrganizationForm organizationForm, BindingResult result) {
        organizationForm.setId(UUIDTool.getTimeStamp());
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysOrganization organization =  organizationForm.getSysOrganization(organizationForm);
        organizationService.createOrganization(organization);
        return Result.ok();
    }
    @ApiOperation(value = "更新组织树节点信息")
    @ResponseBody
    @PostMapping("/updateOrganization")
    public Result updateOrganization(@Validated @RequestBody SysOrganizationForm organizationForm, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysOrganization sysOrganization =  organizationForm.getSysOrganization(organizationForm);
        organizationService.updateOrganization(sysOrganization);
        return Result.ok();
    }
    @ApiOperation(value = "删除组织树节点")
    @ResponseBody
    @PostMapping("/deleteOrganization")
    public Result deleteOrganization(@NotNull @RequestParam("id") String id) {
        if(id == null  || "".equals(id)){
            return Result.error("ID不能为空");
        }
        organizationService.deleteOrganization(id);
        return Result.ok();
    }

}
