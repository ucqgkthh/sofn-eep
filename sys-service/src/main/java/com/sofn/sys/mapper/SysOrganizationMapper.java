package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysOrganization;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by sofn
 */
public interface SysOrganizationMapper extends BaseMapper<SysOrganization> {
    int updateSalefParentIds(String makeSelfAsParentIds);

    Integer getSysOrganizationByName(@Param("orgname") String orgname, @Param("id") String id);
    Integer getUserOfOrganization(@Param("id") String id);
    List<SysOrganization> selectOrganizationListByPriority();
    List<SysOrganization> getSysOrganizationByContion(Map<String,Object> params);
}
