package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysSubsystem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysSubsystemMapper extends BaseMapper<SysSubsystem> {

    Integer getSysSubsystemByNameOrAppId(@Param("name") String name, @Param("appId") String appId, @Param("id") String id);

    List<SysSubsystem> getSonSysSubsystem(String parentId);
    List<SysSubsystem> getAllSysSubsystem();
    List<SysSubsystem> getSysSubsystemByContion(Map<String, Object> params);
}
