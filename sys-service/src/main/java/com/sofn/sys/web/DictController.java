package com.sofn.sys.web;

import com.sofn.common.model.Result;
import com.sofn.sys.model.SysDict;
import com.sofn.sys.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * zhouqingchun
 * 2019/5/29
 */
@RestController
@Api(tags = "字典管理接口")
@RequestMapping("/dict")
public class DictController extends BaseController{

    @Autowired
    private SysDictService sysDictService;

    @ApiOperation(value = "查询字典数据接口")
    @RequestMapping(value = "/getDictInfo",method = RequestMethod.GET)
    public Result getDictInfo(){
        List<SysDict> res = sysDictService.getDictInfo();
        return Result.ok(res);
    }

    @ApiOperation(value = "根据字典名称查询字典数据接口")
    @RequestMapping(value = "/getDictByName",method = RequestMethod.GET)
    public Result getDictByName(@ApiParam(required = true, value = "字典名称") @RequestParam(value = "dictname") String dictname){
        List<SysDict> res = sysDictService.getDictByName(dictname);
        return Result.ok(res);
    }

    @ApiOperation(value = "根据字典值和类型查询字典数据接口")
    @RequestMapping(value = "/getDictByValueAndType",method = RequestMethod.GET)
    public Result getDictByValueAndType(@ApiParam(required = true, value = "字典类型") @RequestParam(value = "dicttype") String dicttype,
                                        @ApiParam(required = true, value = "字典值") @RequestParam(value = "dictcode") String dictcode){
        List<SysDict> res = sysDictService.getDictByValueAndType(dicttype,dictcode);
        return Result.ok(res);
    }

    @ApiOperation(value = "增加字典数据接口")
    @RequestMapping(value = "/saveDictInfo",method = RequestMethod.POST)
    public Result saveDictInfo(@ApiParam(required = true, value = "字典类型") @RequestParam(value = "dicttype") String dicttype,
                               @ApiParam(required = true, value = "字典名字") @RequestParam(value = "dictname") String dictname,
                               @ApiParam(required = true, value = "字典代码") @RequestParam(value = "dictcode") String dictcode,
                               @ApiParam(required = true, value = "字典状态") @RequestParam(value = "enable") String enable,
                               @ApiParam(required = true, value = "备注") @RequestParam(value = "remark") String remark){

        SysDict sysDict = new SysDict();
        sysDict.setDictType(dicttype);
        sysDict.setDictName(dictname);
        sysDict.setDictCode(dictcode);
        sysDict.setEnable(enable);
        sysDict.setRemark(remark);
        sysDictService.saveDictInfo(sysDict);

        return Result.ok();
    }

    @ApiOperation(value = "修改字典数据接口")
    @RequestMapping(value = "/updateDictInfo",method = RequestMethod.PUT)
    public Result updateDictInfo(@ApiParam(required = true, value = "字典ID") @RequestParam(value = "id") String id,
                                 @ApiParam(required = true, value = "字典状态") @RequestParam(value = "enable") String enable){
        sysDictService.updateDictInfo(id,enable);
        return Result.ok();
    }

    @ApiOperation(value = "刪除字典数据接口")
    @RequestMapping(value = "/deleteDictInfo",method = RequestMethod.DELETE)
    public Result deleteDictInfo(@ApiParam(required = true, value = "字典ID") @RequestParam(value = "id") String id){
        sysDictService.deleteDictInfo(id);
        return Result.ok();
    }

}
