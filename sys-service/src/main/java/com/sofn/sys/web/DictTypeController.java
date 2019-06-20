package com.sofn.sys.web;

import com.sofn.common.model.Result;
import com.sofn.sys.model.SysDictType;
import com.sofn.sys.service.SysDictTypeService;
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
 * 2019/06/12
 */
@RestController
@Api(tags = "字典类型管理接口")
@RequestMapping("/DictType")
public class DictTypeController extends BaseController{

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @ApiOperation(value = "查询字典类型数据接口")
    @RequestMapping(value = "/getDictTypeByName",method = RequestMethod.GET)
    public Result getDictTypeByName(@ApiParam(required = true, value = "字典名称") @RequestParam(value = "dictname") String dictname){
        List<SysDictType> res = sysDictTypeService.getDictTypeByName(dictname);
        return Result.ok(res);
    }

    @ApiOperation(value = "保存字典类型数据接口")
    @RequestMapping(value = "/saveDictType",method = RequestMethod.POST)
    public Result saveDictType(@ApiParam(required = true, value = "字典名称") @RequestParam(value = "dictname") String dictname){
        SysDictType sysDictType = new SysDictType();
        sysDictType.setDictName(dictname);
        List<SysDictType> res = sysDictTypeService.saveDictType(sysDictType);
        return Result.ok(res);
    }

    @ApiOperation(value = "删除字典类型数据接口")
    @RequestMapping(value = "/delDictType",method = RequestMethod.DELETE)
    public Result delDictType(@ApiParam(required = true, value = "字典ID") @RequestParam(value = "id") String id){
        List<SysDictType> res = sysDictTypeService.delDictType(id);
        return Result.ok(res);
    }

    @ApiOperation(value = "修改字典类型数据接口")
    @RequestMapping(value = "/updateDictType",method = RequestMethod.PUT)
    public Result updateDictType(@ApiParam(required = true, value = "字典ID") @RequestParam(value = "id") String id,
                               @ApiParam(required = true, value = "字典类型名称") @RequestParam(value = "dictname") String dictname){
        List<SysDictType> res = sysDictTypeService.updateDictType(id,dictname);
        return Result.ok(res);
    }

}
