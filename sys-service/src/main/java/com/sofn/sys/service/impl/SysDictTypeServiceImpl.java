package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.mapper.SysDictTypeMapper;
import com.sofn.sys.model.SysDictType;
import com.sofn.sys.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * zhouqingchun
 * 20190613
 */
@SuppressWarnings("ALL")
@Service(value = "sysDictTypeService")
public class SysDictTypeServiceImpl  extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    @Autowired
    private SysDictTypeMapper sysDictTypeDao;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<SysDictType> getDictTypeByName(String dictname) {
        List<SysDictType> resList = sysDictTypeDao.getDictTypeByName(dictname);
        System.out.println(resList.size());
        return resList;
    }

    @Override
    public List<SysDictType> saveDictType(SysDictType sysDictType) {
        sysDictTypeDao.saveDictType(sysDictType);
        return null;
    }

    @Override
    public List<SysDictType> delDictType(String id) {
        sysDictTypeDao.delDictType(id);
        return null;
    }

    @Override
    public List<SysDictType> updateDictType(String id,String dictname) {
        sysDictTypeDao.updateDictType(id,dictname);
        return null;
    }
}
