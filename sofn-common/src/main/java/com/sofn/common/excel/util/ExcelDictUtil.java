package com.sofn.common.excel.util;

import com.google.common.collect.Lists;
import com.sofn.common.dao.DictDao;
import com.sofn.common.model.Dict;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Excel  字典相关工具
 * Created by heyongjie on 2019/6/14 10:45
 */
public class ExcelDictUtil {

    /**
     * 获取字典中文注解
     * @param dictType   字典类型
     * @param dictVal    字典值
     */
    public static String getDescription(String dictType,String dictVal){
//        Dict dict = new DictDao.getByTypeAndKey(dictType,dictVal);
        DictDao dictDao = new DictDao();
        Dict dict =  dictDao.getByTypeAndKey(dictType,dictVal);
        if(dict == null){
            return dictVal;
        }
        return dict.getDictName();
    }

    /**
     * 获取某个类别的中文注解列表
     * @param type   类别
     * @return  中文注解列表
     */
    public static List<String> getDescriptions(String type){

        DictDao dictDao = new DictDao();
        List<Dict > dicts = dictDao.getByType(type);
        if(dicts == null){
            return Lists.newArrayList();
        }
        List<String> types = dicts.stream().map(Dict::getDictName).collect(Collectors.toList());
//        List<String> temp = Lists.newArrayList(Arrays.asList("列表1","列表2"));
        return types;
    }

    /**
     * 根据类型和中文注解获得字典值
     * @param dictType  字典类型
     * @param description  中文说明
     * @return   字典值
     */
    public static String getValue(String dictType,String description){
        DictDao dictDao = new DictDao();
        Dict dict = dictDao.getByTypeAndValue(dictType,description);
        if(dict == null) return description;
        return dict.getDictCode();
    }

}
