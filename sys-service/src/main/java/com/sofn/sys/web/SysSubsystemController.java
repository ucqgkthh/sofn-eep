package com.sofn.sys.web;

import com.google.common.collect.Maps;
import com.sofn.common.model.Result;
import com.sofn.common.log.SofnLog;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.model.SysSubsystem;
import com.sofn.sys.service.SysSubsystemService;
import com.sofn.sys.vo.SysSubsystemForm;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 子系统接口
 * Created by sofn 2019/5/29 16:27
 */
@Api(tags = "子系统接口")
@RestController
@RequestMapping("/sysSubsystem")
@Slf4j
public class SysSubsystemController extends BaseController {
    

    @Autowired
    private SysSubsystemService sysSubsystemService;

    @SofnLog("获取子系统信息列表")
    @ApiOperation(value = "按条件分页获取子系统信息")
    @GetMapping("/getSysSubsystemByPage")
    public Result<List<SysSubsystem>> getSysSubsystemByPage(@RequestParam(required = false) @ApiParam(value = "子系统名称") String systemName,
                                        @RequestParam(required = false) @ApiParam(value = "AppId") String appId,
                                        @ApiParam(name = "pageNo",value = "分页序号",required = true)  @RequestParam("pageNo")Integer pageNo,
                                        @ApiParam(name = "pageSize",value = "每页数量",required = true)@RequestParam("pageSize")Integer pageSize) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemName",systemName);
        params.put("appId",appId);

        // 获取分页内容
        PageUtils<SysSubsystem> SysSubsystemPageUtils = sysSubsystemService.getSysSubsystemByContion(params,pageNo,pageSize);
        return Result.ok(SysSubsystemPageUtils);
    }
    @SofnLog("添加子系统信息")
    @ApiOperation(value = "添加子系统信息")
    @PostMapping(value = "/saveSysSubsystem")
    public Result<SysSubsystem> saveSysSubsystem(@Validated @RequestBody @ApiParam(name = "子系统对象", value = "传入json格式", required = true)
                                                             SysSubsystemForm sysSubsystemForm, BindingResult result) {
        // 校验参数
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysSubsystem sysSubsystem =  SysSubsystemForm.getSysSubsystem(sysSubsystemForm);

        sysSubsystemService.addSysSubsystem(sysSubsystem);
        return Result.ok();
    }
    @SofnLog("修改子系统信息")
    @ApiOperation(value = "修改子系统信息")
    @PostMapping(value = "/updateSysSubsystem")
    public Result updateSysSubsystem(@Validated @RequestBody  @ApiParam(name = "子系统对象", value = "传入json格式", required = true)
                                                 SysSubsystemForm sysSubsystemForm, BindingResult result) {
        // 校验参数
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysSubsystem sysSubsystem =  SysSubsystemForm.getSysSubsystem(sysSubsystemForm);
        sysSubsystemService.updateSysSubsystem(sysSubsystem);
        return Result.ok();
    }
    @SofnLog("删除子系统信息")
    @ApiOperation(value = "删除子系统信息")
    @PostMapping(value = "/deleteSysSubsystem")
    public Result deleteSysSubsystem(@ApiParam(name = "id",value = "子系统ID",required = true) @RequestParam(value = "id")String id){
        if(id == null  || "".equals(id)){
            return Result.error("ID不能为空");
        }
        sysSubsystemService.deleteSysSubsystem(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取子系统树结构")
    @PostMapping(value = "/getSysSubsystemTree")
    public Result<SysSubsystem> getSysSubsystemTree(){
        List<SysSubsystem> allSysSubsystem= sysSubsystemService.getAllSysSubsystem();
        List<SysSubsystem> treeSysSubsystem= sysSubsystemService.getChildPerms(allSysSubsystem,SysManageEnum.SUBSYSTEM_ROOT_LEVEL.toString());
        return Result.ok(treeSysSubsystem);
    }
    @SofnLog("获取子系统信息")
    @ApiOperation(value = "根据ID获取一个")
    @GetMapping(value = "/getSysSubsystemOne")
    public Result<SysSubsystem> getSysSubsystemOne(@ApiParam(name = "id",value = "子系统ID",required = true) @RequestParam(value = "id") String id){
        SysSubsystem sysSubsystem =  sysSubsystemService.getById(id);
        return Result.ok(sysSubsystem);
    }

    @ApiOperation(value = "根据ID获取当前ID下面的一层树结构")
    @PostMapping("/getSysSubsystemTreeById")
    public Result<SysSubsystem> getSysSubsystemTreeById(@ApiParam(name = "id",value = "子系统ID",required = true) @RequestParam("id") String id){
       List<SysSubsystem> SysSubsystemTreeVos = sysSubsystemService.getSysSubsystemTreeById(id);
       return Result.ok(SysSubsystemTreeVos);
    }


}
