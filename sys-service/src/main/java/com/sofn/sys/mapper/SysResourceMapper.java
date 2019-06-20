package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysResource;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
/**
 * Created by sofn
 */
public interface SysResourceMapper extends BaseMapper<SysResource> {
    List<SysResource> getAllResourceByRoleId(String roleId);
    List<SysResource>  getAllResource();
    Integer getSysResourceByNameOrUrl(@Param("name") String name, @Param("url") String url,@Param("id") String id);
    List<SysResource> getAllResourceBySubsystemId(String subsystemId);
    List<SysResource> getSonSysResource(String parentId);
    List<SysResource> selectResourceListByPriority();
    List<SysResource> getSysResourceByContion(Map<String,Object> params);

}
