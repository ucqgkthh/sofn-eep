package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysDictType;

import java.util.List;

public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    List<SysDictType> getDictTypeByName(String dictname);

    void saveDictType(SysDictType sysDictType);

    void delDictType(String id);

    void updateDictType(String id,String dictname);

}
