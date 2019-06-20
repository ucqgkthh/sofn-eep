package com.sofn.sys.service.impl;

import com.sofn.KingAdminJavaApplicationTests;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.service.SysRegionToTreeService;
import com.sofn.sys.vo.SysRegionTreeVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by heyongjie on 2019/5/29 15:44
 */
public class SysRegionToTreeServiceImplTest extends KingAdminJavaApplicationTests {

    @Autowired
    private SysRegionToTreeService sysRegionToTreeService;

    @Test
    public void getSysRegionTree() {
        sysRegionToTreeService.getSysRegionTree();
    }


    @Test
    public void getSysRegionById() {
        List<SysRegionTreeVo> sysRegionTreeVos = sysRegionToTreeService.getSysRegionTreeById(SysManageEnum.ROOT_LEVEL.getCode());
        System.out.println(sysRegionTreeVos.size());
    }

    @Test
    public void testGetSysRegionTreeByIdAndCache(){
        List<SysRegionTreeVo> sysRegionTreeVos = sysRegionToTreeService.getSysRegionTreeByIdAndCache(SysManageEnum.ROOT_LEVEL.getCode());
        System.out.println(sysRegionTreeVos.size());

    }
}