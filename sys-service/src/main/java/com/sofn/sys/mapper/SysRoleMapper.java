package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by sofn
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

        /**
         * 新增或修改角色时，判断角色名是否重复
         * @param roleName 角色名
         * @param id 角色id(新增时为null)
         * @return 大于0，表示重复
         */
        Integer getSysRoleByName(@Param("roleName") String roleName, @Param("id") String id);

        List<SysRole> getSysRoleByCondition(Map<String,Object> params);
        List<SysRole> selectRolesByUserId(String userId);

        int insertSelective(SysRole record);
        SysRole selectByPrimaryKey(String id);
        int updateByPrimaryKeySelective(SysRole record);
}
