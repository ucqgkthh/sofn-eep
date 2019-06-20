package com.sofn.sys.service;

import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 组服务接口
 * @author cjbi
 */
public interface SysGroupService extends IService<SysGroup> {
    void updateGroup(SysGroup group);
    void deleteGroup(String id);
    void createGroup(SysGroup group);
    PageUtils<SysGroup> getSysGroupByContion(Map<String,Object> paramas, Integer pageNo, Integer pageSize);

}
