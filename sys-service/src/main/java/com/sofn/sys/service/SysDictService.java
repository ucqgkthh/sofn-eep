package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.sys.model.SysDict;

import java.util.List;

public interface SysDictService extends IService<SysDict> {

    public List<SysDict> getDictInfo();

    public void saveDictInfo(SysDict sysDict);

    public void updateDictInfo(String id, String enable);

    public void deleteDictInfo(String id);

    public List<SysDict> getDictByName(String dictname);

    public List<SysDict> getDictByValueAndType(String dicttype, String dictcode);
}
