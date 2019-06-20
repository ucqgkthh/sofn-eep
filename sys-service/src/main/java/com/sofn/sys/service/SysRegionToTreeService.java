package com.sofn.sys.service;

import com.sofn.sys.vo.SysRegionTreeVo;

import java.util.List;

/**
 * 将行政区划形成树结构服务层
 * Created by heyongjie on 2019/5/29 15:19
 */
public interface SysRegionToTreeService {

    /**
     * 将行政区划形成树结构
     *
     * @return
     */
    SysRegionTreeVo getSysRegionTree();

    /**
     * 从缓存中获取行政区划树结构
     * @return
     */
    SysRegionTreeVo getSysRegionTreeByCache();

    /**
     * 根据id获取下级子菜单
     * @return
     */
    List<SysRegionTreeVo> getSysRegionTreeById(String id);

    List<SysRegionTreeVo> getSysRegionTreeByIdAndCache(String id);

}
