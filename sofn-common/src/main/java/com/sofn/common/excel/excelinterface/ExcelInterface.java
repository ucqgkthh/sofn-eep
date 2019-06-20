package com.sofn.common.excel.excelinterface;

import com.sofn.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * Excel 接口
 * Created by heyongjie on 2019/6/13 17:13
 */
public interface ExcelInterface {

    /**
     * 批量新增
     * @param datas   校验过后的数据
     * @param <E>
     */
    <E> void batchSave(List<E> datas);

    /**
     * 分页获取数据
     * @param params  查询条件
     * @param pageNo  页数
     * @param pageSize  每页显示数量
     * @return
     */
    PageUtils getDataByConditionAndPage(Map<String,Object> params, Integer pageNo, Integer pageSize);

}
