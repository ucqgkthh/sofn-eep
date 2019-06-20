package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysRegionMapper;
import com.sofn.sys.model.SysRegion;
import com.sofn.sys.service.SysRegionToTreeService;
import com.sofn.sys.vo.SysRegionTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 生成行政区划树Service
 * Created by heyongjie on 2019/5/29 15:21
 */
@Service
public class SysRegionToTreeServiceImpl extends ServiceImpl<SysRegionMapper, SysRegion> implements SysRegionToTreeService {

    @Autowired
    private SysRegionMapper sysRegionMapper;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public SysRegionTreeVo getSysRegionTree() {
        // 1. 查询出所有的结构
        List<SysRegion> sysRegionList = sysRegionMapper.selectList(null);

        SysRegionTreeVo sysRegionTreeVo = null;

        // 2. 找到所有的一级节点
        List<SysRegionTreeVo> rootSysRegion = new ArrayList<SysRegionTreeVo>();
        // 存储的是String -> List结构  方便后面使用递归查找子节点
        Multimap<String, SysRegionTreeVo> multimap = ArrayListMultimap.create();

        if (sysRegionList != null && sysRegionList.size() > 0) {
            for (SysRegion sysRegion : sysRegionList) {
                // 如果已删除  当前数据不处理
                if(SysManageEnum.DEL_FLAG_Y.getCode().equals(sysRegion.getDelFlag())) continue;

                SysRegionTreeVo tempSysRegionTreeVo = SysRegionTreeVo.getSysRegionTreeVo(sysRegion);
                if (SysManageEnum.ROOT_LEVEL.getCode().equals(sysRegion.getId())) {
                    // 根节点
                    sysRegionTreeVo = tempSysRegionTreeVo;
                } else if (SysManageEnum.ROOT_LEVEL.getCode().equals(sysRegion.getParentId())) {
                    // 一级节点
                    rootSysRegion.add(tempSysRegionTreeVo);
                } else {
                    // 一级以上节点
                    multimap.put(sysRegion.getParentId(), tempSysRegionTreeVo);
                }
            }
        }
        if (sysRegionTreeVo == null) {
            throw new SofnException("请设置根节点");
        }
        // 3. 排序
        if (rootSysRegion != null && rootSysRegion.size() > 0) {
            Collections.sort(rootSysRegion, comparator);
        }

        rootSysRegion = toTree(rootSysRegion, multimap);
        sysRegionTreeVo.setChildren(rootSysRegion);
        return sysRegionTreeVo;
    }

    @Override
    public SysRegionTreeVo getSysRegionTreeByCache() {
        // 1. 从缓存中获取数据
        SysRegionTreeVo sysRegionTreeVo  =  redisUtils.get(SysManageEnum.SYS_REGION_CACHE_KEY.getCode(),SysRegionTreeVo.class);
        //  缓存中没有值
        if (sysRegionTreeVo == null ) {
            sysRegionTreeVo = this.getSysRegionTree();
            if(sysRegionTreeVo != null){
                // 将对象存入缓存中
                redisUtils.set(SysManageEnum.SYS_REGION_CACHE_KEY.getCode(), sysRegionTreeVo, Long.parseLong(
                        SysManageEnum.SYS_MANAGE_CACHE_TIMEOUT.getCode()));
            }
        }
        return sysRegionTreeVo;
    }

    @Override
    public List<SysRegionTreeVo> getSysRegionTreeById(String id) {
        // 查找数据
        Map<String,Object> params = Maps.newHashMap();
        params.put("parentId",id);
        params.put("delFlag",SysManageEnum.DEL_FLAG_N.getCode());
        List<SysRegion> sysRegionList = sysRegionMapper.getSysRegionByContion(params);
        // 转换为VO
        List<SysRegionTreeVo> sysRegionTreeVos = new ArrayList<SysRegionTreeVo>();
        if(!CollectionUtils.isEmpty(sysRegionList)){
            sysRegionList.forEach((sysRegion) -> {
                SysRegionTreeVo sysRegionTreeVo = SysRegionTreeVo.getSysRegionTreeVo(sysRegion);
                sysRegionTreeVos.add(sysRegionTreeVo);
            });
            // 排序
            Collections.sort(sysRegionTreeVos, comparator);
        }
        return sysRegionTreeVos;
    }

    @Override
    public List<SysRegionTreeVo> getSysRegionTreeByIdAndCache(String id) {
        String key = SysManageEnum.SYS_REGION_CACHE_KEY.getCode() + "_" + id ;
        List<SysRegionTreeVo> sysRegionTreeVos = null;
        try{
            sysRegionTreeVos = redisUtils.get(key,List.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(CollectionUtils.isEmpty(sysRegionTreeVos)){
            // 1.从数据库中查找数据
            sysRegionTreeVos =  getSysRegionTreeById(id);
            // 2.将数据放入缓存中
            if(sysRegionTreeVos != null &&sysRegionTreeVos.size() > 0){
                redisUtils.set(key,sysRegionTreeVos,Long.parseLong(
                        SysManageEnum.SYS_MANAGE_CACHE_TIMEOUT.getCode()));
            }
        }
        return sysRegionTreeVos;
    }

    /**
     * 递归查询子节点
     *
     * @param rootSysRegion
     * @param multimap
     * @return
     */
    private List<SysRegionTreeVo> toTree(List<SysRegionTreeVo> rootSysRegion, Multimap<String, SysRegionTreeVo> multimap) {
        if (rootSysRegion != null && rootSysRegion.size() > 0) {
            for (SysRegionTreeVo sysRegionTreeVo : rootSysRegion) {
                // 1. 找到所有的子节点
                List<SysRegionTreeVo> sonSysRegionList = (List<SysRegionTreeVo>) multimap.get(sysRegionTreeVo.getId());
                // 2. 排序
                if (sonSysRegionList != null && sonSysRegionList.size() > 0) {
                    Collections.sort(sonSysRegionList, comparator);
                }
                sysRegionTreeVo.setChildren(sonSysRegionList);
                // 递归查找子节点
                toTree(sonSysRegionList, multimap);
            }

        }
        return rootSysRegion;
    }


    /**
     * 行政区划排序
     */
    public Comparator comparator = new Comparator<SysRegionTreeVo>() {
        public int compare(SysRegionTreeVo o1, SysRegionTreeVo o2) {
            if(o1 == null || o2 == null){
                return 0;
            }
            if(o1.getSort() == null || o2.getSort() == null){
                return 0;
            }
            return o1.getSort() - o2.getSort();
        }
    };
}
