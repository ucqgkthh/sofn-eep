package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysSubsystem;

import java.util.*;

/**
 *
 * 行政区划代码服务层
 *
 * Created by heyongjie on 2019/5/29 11:15
 */
public interface SysSubsystemService extends IService<SysSubsystem> {

    List<SysSubsystem> getSysSubsystemTreeById(String subsystemId);
    void addSysSubsystem(SysSubsystem sysSubsystem);


    void updateSysSubsystem(SysSubsystem sysSubsystem);

    /**
     * 删除行政区划内容
     * @param id
     */
    void deleteSysSubsystem(String id);

    /**
     * 检查SysSubsystem是否重复
     * @param name  名称
     * @param code  代码
     * @return  true 重复 false 不重复
     */
    boolean checkSysSubsystemIsExists(String name, String code, String id);

    /**
     *
     * 按条件分页查询
     *
     *
     * @param paramas
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageUtils<SysSubsystem> getSysSubsystemByContion(Map<String, Object> paramas, Integer pageNo, Integer pageSize);
    List<SysSubsystem> getChildPerms(List<SysSubsystem> subsystemList,String parentId);
    List<SysSubsystem> getAllSysSubsystem();
}
