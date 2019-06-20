package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysDict;

import java.util.List;

public interface SysDictMapper  extends BaseMapper<SysDict> {

    List<SysDict> getDictInfo();

    void saveDictInfo(SysDict sysDict);

    void updateDictInfo(String id,String enable);

    void deleteDictInfo(String id);

    List<SysDict> getDictByName(String dictname);

    List<SysDict> getDictByValueAndType(String dicttype, String dictcode);
}
