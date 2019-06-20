package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sofn.common.utils.DictUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.mapper.SysDictMapper;
import com.sofn.sys.model.SysDict;
import com.sofn.sys.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("ALL")
@Service(value = "sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Autowired
    private SysDictMapper sysDictDao;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<SysDict> getDictInfo() {
        List<SysDict> res = sysDictDao.getDictInfo();
        System.out.println(res.size());
        return res;
    }

    @Override
    public void saveDictInfo(SysDict sysDict) {
        sysDictDao.saveDictInfo(sysDict);
    }

    @Override
    public void updateDictInfo(String id, String enable) {
        sysDictDao.updateDictInfo(id,enable);
        String type = "";
        DictUtils.deleteCacheByType(type);
    }

    @Override
    public void deleteDictInfo(String id) {
        sysDictDao.deleteDictInfo(id);
        String type = "";
        DictUtils.deleteCacheByType(type);
    }

    @Override
    public List<SysDict> getDictByName(String dictname) {
        List<SysDict> res = sysDictDao.getDictByName(dictname);
        return res;
    }

    @Override
    public List<SysDict> getDictByValueAndType(String dicttype, String dictcode) {
        List<SysDict> res = sysDictDao.getDictByValueAndType(dicttype,dictcode);
        return res;
    }
}
