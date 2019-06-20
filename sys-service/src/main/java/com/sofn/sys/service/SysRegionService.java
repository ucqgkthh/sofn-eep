package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysRegion;
import com.sofn.sys.vo.SysRegionForm;

import java.util.List;
import java.util.Map;

/**
 *
 * 行政区划代码服务层
 *
 * Created by heyongjie on 2019/5/29 11:15
 */
public interface SysRegionService  extends IService<SysRegion> {

    /**
     * 添加行政区划内容
     *
     * @param sysRegion
     */
    void addSysRegion(SysRegion sysRegion);

    /**
     * 修改行政区划内容
     * @param sysRegion
     */
    void updateSysRegion(SysRegion sysRegion);

    /**
     * 删除行政区划内容
     * @param id
     */
    void deleteSysRegion(String id);

    /**
     * 检查SysRegion是否重复
     * @param name  名称
     * @param code  代码
     * @return  true 重复 false 不重复
     */
    boolean checkSysRegionIsExists(String name,String code,String id);

    /**
     * 批量删除
     * @param ids   要删除的IDs
     */
    void batchDeleteSysRegion(List<String>ids);

    /**
     * 按条件分页查询
     * @param paramas
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageUtils<SysRegionForm> getSysRegionByContionPage(Map<String,Object> paramas, Integer pageNo, Integer pageSize);

    /**
     * 根据ID获取一个  因为使用的是软删除，使用默认的获取一个会出现错误
     * @param id  要查询的ID
     * @return  ID对应的数据
     */
    SysRegion getOneById(String id);

    /**
     * 获取列表根据父ID查询
     * @param parentId  父ID
     * @return
     */
    List<SysRegionForm> getListByRegionCode(String parentId);
}
