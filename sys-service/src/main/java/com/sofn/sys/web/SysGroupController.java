package com.sofn.sys.web;

import com.sofn.common.exception.SofnException;
import com.sofn.common.model.Result;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysGroup;
import com.sofn.sys.service.SysGroupService;
import com.sofn.sys.util.UUIDTool;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.sofn.sys.vo.SysGroupForm;
import java.util.Map;

/**
 * @author cjbi
 */
@Controller
@RequestMapping("/sysGroup")
public class SysGroupController extends BaseController {

    @Autowired
    private SysGroupService groupService;

 /*   @GetMapping
    public String groupPage(Model model) {
        model.addAttribute("groupTypeList", GroupType.values());
        return "system/group";
    }*/
    @ApiOperation(value = "按条件分页获取用户组信息")
    @PostMapping("/getSysGroupByPage")
    public Result getSysGroupByPage(@RequestBody Map<String,Object> params, Integer pageNo, Integer pageSize) {
        // 获取分页内容
        PageUtils<SysGroup> sysRolePageUtils = groupService.getSysGroupByContion(params,pageNo,pageSize);
        return Result.ok(sysRolePageUtils);
    }
    @ApiOperation(value = "创建用户组信息")
    @ResponseBody
    @PostMapping("/createGroup")
    public Result createGroup(@Validated SysGroupForm groupForm ,BindingResult result) {
        groupForm.setId(UUIDTool.getTimeStamp());
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysGroup sysGroup =  groupForm.getSysGroup(groupForm);
        groupService.createGroup(sysGroup);
        return Result.ok();
    }
    @ApiOperation(value = "更新用户组信息")
    @ResponseBody
    @PostMapping("/updateGroup")
    public Result updateGroup(@Validated @RequestBody  SysGroupForm groupForm ,BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysGroup group =  groupForm.getSysGroup(groupForm);
        groupService.updateGroup(group);
        return Result.ok();
    }
    @ApiOperation(value = "删除用户组信息")
    @PostMapping("/deleteGroup")
    public Result deleteGroup(@RequestParam(value = "id")String id) {
        if(id == null  || "".equals(id)){
            return Result.error("ID不能为空");
        }
        groupService.deleteGroup(id);
        return Result.ok();
    }



  /*  @ResponseBody
    @PostMapping("/delete-batch")
    public Result deleteBatchByIds(@NotNull @RequestParam("id")  Object[] ids) {
        super.deleteBatchByIds(ids);
        return Result.success();
    }
*/
}
