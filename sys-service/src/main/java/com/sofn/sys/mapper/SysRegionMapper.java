package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysRegion;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 行政区划Mapper
 * Created by heyongjie on 2019/5/29 11:14
 */
public interface SysRegionMapper extends BaseMapper<SysRegion> {

    /**
     * 根据名字和ID获取数量
     * @param name  行政区划名称
     * @param regionCode    行政区划代码
     * @param id    如果传入，那么将不会包含当前ID对应的数据
     * @return  Integer
     */
    Integer getSysRegionByNameOrRegionCode(@Param("name") String name,@Param("regionCode") String regionCode,@Param("id") String id);


    List<SysRegion> getSysRegionByContion(Map<String,Object> params);

    /**
     * 批量删除
     * @param ids   待删除ID
     * @param updateUserId   操作用户
     * @param updateTime   操作时间
     */
    void batchDelete(@Param("ids") List<String> ids,@Param("updateUserId") String updateUserId,@Param("updateTime") Date updateTime);
}
