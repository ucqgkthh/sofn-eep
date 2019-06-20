package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by sofn
 */
public interface SysGroupMapper extends BaseMapper<SysGroup> {
    Integer getSysGroupByName(@Param("name") String name, @Param("id") String id);
    Integer getUserOfGroup(String id);
   /* List<SysGroup> selectRoleListByPriority();*/
    List<SysGroup> getSysGroupByContion(Map<String,Object> params);
}
