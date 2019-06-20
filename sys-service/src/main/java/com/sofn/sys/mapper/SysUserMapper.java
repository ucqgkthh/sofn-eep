package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysUser;

import java.util.List;
import java.util.Map;

/**
 * Created by sofn
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 按条件查询用户
     * @param params  条件
     * @return
     */
    List<SysUser> getSysUserByCondition(Map<String,Object> params);

    /**
     * 批量添加用户
     * @param sysUsers   需要添加的用户信息
     */
    void batchSave(List<SysUser> sysUsers);

    SysUser selectByPrimaryKey(String id);
    int insertSelective(SysUser record);
    int updateByPrimaryKeySelective(SysUser record);
}
