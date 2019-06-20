package com.sofn.sys.web;

import com.google.common.collect.Maps;
import com.sofn.common.model.Result;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.model.SysRegion;
import com.sofn.sys.service.SysRegionService;
import com.sofn.sys.service.SysRegionToTreeService;
import com.sofn.sys.util.IdUtil;
import com.sofn.sys.vo.SysRegionForm;
import com.sofn.sys.vo.SysRegionTreeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 行政区划接口
 * Created by heyongjie on 2019/5/29 16:27
 */
@Api(tags = "行政区划接口")
@RestController
@RequestMapping("/sys/sysRegion")
@Slf4j
public class SysRegionController extends BaseController {

    @Autowired
    private SysRegionToTreeService sysRegionToTreeService;

    @Autowired
    private SysRegionService sysRegionService;

    @ApiOperation(value = "按条件分页获取信息")
    @PostMapping("/getSysRegionByPage")
    public Result<List<SysRegionForm>> getSysRegionByPage(@RequestParam(required = false) @ApiParam(value = "行政区划名称") String regionName,
                                     @RequestParam(required = false) @ApiParam(value = "行政区划代码") String regionCode,
                                     @RequestParam(required = false) @ApiParam(value = "父ID") String parentId,
                                     @RequestParam @ApiParam(value = "当前页数", required = true) Integer pageNo,
                                     @RequestParam @ApiParam(value = "每页显示条数", required = true) Integer pageSize) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("regionName", regionName);
        params.put("regionCode", regionCode);
        params.put("parentId", parentId);
        params.put("delFlag",SysManageEnum.DEL_FLAG_N.getCode());
        // 获取分页内容
        PageUtils<SysRegionForm> sysRegionPageUtils = sysRegionService.getSysRegionByContionPage(params, pageNo, pageSize);
        return Result.ok(sysRegionPageUtils);
    }


    @ApiOperation(value = "添加行政区划信息")
    @PostMapping(value = "/saveSysRegion")
    public Result saveSysRegion(@Validated @RequestBody @ApiParam(name = "行政区划对象", value = "传入json格式", required = true)
                                        SysRegionForm sysRegionForm, BindingResult result) {
        // 校验参数
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysRegion sysRegion = SysRegionForm.getSysRegion(sysRegionForm);

        sysRegionService.addSysRegion(sysRegion);
        return Result.ok();
    }

    @ApiOperation(value = "修改行政区划信息")
    @PostMapping(value = "/updateSysRegion")
    public Result updateSysRegion(@Validated @RequestBody @ApiParam(name = "行政区划对象", value = "传入json格式", required = true)
                                          SysRegionForm sysRegionForm, BindingResult result) {
        // 校验参数
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysRegion sysRegion = SysRegionForm.getSysRegion(sysRegionForm);
        sysRegionService.updateSysRegion(sysRegion);
        return Result.ok();
    }

    @ApiOperation(value = "删除行政区划信息")
    @PostMapping(value = "/deleteSysRegion/{id}")
    public Result deleteSysRegion(@ApiParam(value = "行政区划ID", required = true) @PathVariable(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.error("ID不能为空");
        }
        sysRegionService.deleteSysRegion(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取行政区划树结构，获取整个行政区划树结构,从顶级100000开始")
    @GetMapping(value = "/getSysRegionTree")
    public Result<SysRegionTreeVo> getSysRegionTree() {
        SysRegionTreeVo treeVo = sysRegionToTreeService.getSysRegionTreeByCache();
        return Result.ok(treeVo);
    }

    @ApiOperation(value = "根据ID获取一个")
    @GetMapping(value = "/getSysRegionOne/{id}")
    public Result<SysRegionForm> getSysRegionOne(@ApiParam(value = "行政区划ID", required = true) @PathVariable(value = "id") String id) {
        if(StringUtils.isBlank(id)){
            return Result.error("ID不能为空");
        }
        SysRegion sysRegion = sysRegionService.getOneById(id);

        if(sysRegion != null){
            SysRegionForm sysRegionForm = SysRegionForm.getSysRegionForm(sysRegion);
            return Result.ok(sysRegionForm);
        }
        return Result.error("未找到数据");
    }

    @ApiOperation(value = "根据父ID获取当前ID下面的树结构，只返回一层,如果为空默认为100000")
    @GetMapping("/getSysRegionTreeById/{parentId}")
    public Result<List<SysRegionTreeVo>> getSysRegionTreeById(@ApiParam(value = "父ID", required = true) @PathVariable("parentId") String parentId) {
        if(StringUtils.isBlank(parentId)){
           parentId = SysManageEnum.ROOT_LEVEL.getCode();
        }
        List<SysRegionTreeVo> sysRegionTreeVos = sysRegionToTreeService.getSysRegionTreeByIdAndCache(parentId);
        return Result.ok(sysRegionTreeVos);
    }

    @ApiOperation(value = "根据IDS批量删除")
    @PostMapping("/batchDeleteRegion/{ids}")
    public Result batchDeleteRegion(@ApiParam(value = "待删除IDS，ID用英文,号分隔") @PathVariable(value = "ids") String ids){
        if(StringUtils.isBlank(ids)){
            return Result.ok();
        }
        List<String> idList = IdUtil.getIdsByStr(ids);
        sysRegionService.batchDeleteSysRegion(idList);
        return Result.ok();
    }

    @ApiOperation(value = "根据ID获取名字")
    @GetMapping("/getSysRegionName/{id}")
    public Result getSysRegionName(@PathVariable(value = "id")String id){
        if(StringUtils.isBlank(id)){
            return Result.error("ID不能为空");
        }
        SysRegion sysRegion =   sysRegionService.getOneById(id);
        if(sysRegion == null) return Result.ok("");
        return Result.ok((Object)sysRegion.getRegionName());
    }

    @ApiOperation(value = "根据父ID获取下级节点列表，多用于列表联动操作")
    @GetMapping("/getListByParentId/{parentId}")
    public Result<List<SysRegionForm>> getListByParentId(@ApiParam(value = "父节点ID,如果不传入，默认为100000") @PathVariable("parentId") String parentId){
        List<SysRegionForm> sysRegionForms = sysRegionService.getListByRegionCode(parentId);
        return Result.ok(sysRegionForms);
    }


}
