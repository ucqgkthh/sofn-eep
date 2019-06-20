package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.sys.model.SysDictType;

import java.util.List;

public interface SysDictTypeService extends IService<SysDictType> {

    public List<SysDictType> getDictTypeByName(String dictname);

    public List<SysDictType> saveDictType(SysDictType sysDictType);

    public List<SysDictType> delDictType(String id);

    public List<SysDictType> updateDictType(String id,String dictname);
}
