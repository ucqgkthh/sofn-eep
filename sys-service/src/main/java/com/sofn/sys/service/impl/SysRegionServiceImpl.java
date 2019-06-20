package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.PageUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysRegionMapper;
import com.sofn.sys.model.SysRegion;
import com.sofn.sys.service.SysRegionService;
import com.sofn.sys.util.UserUtil;
import com.sofn.sys.vo.SysRegionForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 行政区划CRUD 服务层
 * Created by heyongjie on 2019/5/29 11:15
 */
@Service
@Slf4j
public class SysRegionServiceImpl extends ServiceImpl<SysRegionMapper, SysRegion> implements SysRegionService {

    @Autowired
    private SysRegionMapper sysRegionMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSysRegion(SysRegion sysRegion) {
        // 1. 校验内容是否重复
        checkSysRegionIsExists(sysRegion.getRegionName(), sysRegion.getRegionCode(), null);
        // 2. 设置默认值
        sysRegion.setId(sysRegion.getRegionCode());
        //
        sysRegion.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());
        sysRegion.setStatus(SysManageEnum.STATUS_Y.getCode());

        // 如果父ID为空 那么就是一级菜单 在顶级菜单之下
        if (StringUtils.isBlank(sysRegion.getParentId())) {
            sysRegion.setParentId(SysManageEnum.ROOT_LEVEL.getCode());
        }
        if (sysRegion.getRegionCode().equals(sysRegion.getParentId())) {
            throw new SofnException("自己不能作为自己的父节点");
        }
        sysRegion.setCreateTime(new Date());
        sysRegion.setCreateUserId(UserUtil.getLoginUserId());

        // 3. 保存
        sysRegionMapper.insert(sysRegion);

        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_REGION_CACHE_KEY.getCode());
        // 删除父ID的缓存
        redisUtils.delete(SysManageEnum.SYS_REGION_CACHE_KEY.getCode() + "_" + sysRegion.getParentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysRegion(SysRegion sysRegion) {
        // 1. 校验内容是否存在
        SysRegion selectSysRegion = getOneById(sysRegion.getId());
        if (selectSysRegion == null) {
            throw new SofnException("数据不存在");
        }
        // 2. 校验内容是否重复
        checkSysRegionIsExists(sysRegion.getRegionName(), sysRegion.getRegionCode(), sysRegion.getId());

        // 3. 更改内容
        BeanUtils.copyProperties(sysRegion, selectSysRegion);
        selectSysRegion.setUpdateTime(new Date());
        selectSysRegion.setUpdateUserId(UserUtil.getLoginUserId());
        // 只更改未删除的
        sysRegionMapper.update(selectSysRegion, new QueryWrapper<SysRegion>()
                .eq("del_flag", SysManageEnum.DEL_FLAG_N.getCode()).eq("id", selectSysRegion.getId()));

        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_REGION_CACHE_KEY.getCode());
        redisUtils.delete(SysManageEnum.SYS_REGION_CACHE_KEY.getCode() + "_" + sysRegion.getParentId());
    }

    @Override
    public void deleteSysRegion(String id) {
        SysRegion selectSysRegion = this.getOneById(id);
        if (selectSysRegion == null) {
            throw new SofnException("待删除内容不存在");
        }

        // 2. 如果下面有子节点的不能删除
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentId", selectSysRegion.getId());
        params.put("delFlag", SysManageEnum.DEL_FLAG_N.getCode());
        List<SysRegion> sysRegionList = sysRegionMapper.getSysRegionByContion(params);
        if (sysRegionList != null && sysRegionList.size() > 0) {
            throw new SofnException("当前行政区划下有子行政区划，删除失败");
        }
        // 使用软删除
        selectSysRegion.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        updateSysRegion(selectSysRegion);
    }


    @Override
    public boolean checkSysRegionIsExists(String name, String code, String id) {
        //检查是否重复
        if (id != null) {
            log.info("【修改行政区划信息】参数：name={},code={},id={}", name, code, id);
            // 修改
            // 检查名称是否重复
            Integer number = sysRegionMapper.getSysRegionByNameOrRegionCode(name, null, id);
            if (number > 0) throw new SofnException("行政区划名称重复");
            // 检查Code是否重复
            number = sysRegionMapper.getSysRegionByNameOrRegionCode(null, code, id);
            if (number > 0) throw new SofnException("行政区划代码重复");
        } else {
            // 添加
            log.info("【添加行政区划信息】参数：name={},code={}", name, code);
            // 检查名称是否重复
            Integer number = sysRegionMapper.getSysRegionByNameOrRegionCode(name, null, null);
            if (number > 0) throw new SofnException("行政区划名称重复");
            // 检查Code是否重复
            number = sysRegionMapper.getSysRegionByNameOrRegionCode(null, code, null);
            if (number > 0) throw new SofnException("行政区划代码重复");
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysRegion(List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            // 父ID 用于清理缓存
            Set<String> parentIds = Sets.newHashSet();
            // 如果有子节点不让删除
            ids.forEach((id) -> {
                Map<String, Object> params = Maps.newHashMap();
                params.put("parentId", id);
                params.put("delFlag", SysManageEnum.DEL_FLAG_N.getCode());
                List<SysRegion> sysRegionList = sysRegionMapper.getSysRegionByContion(params);
                if (sysRegionList != null && sysRegionList.size() > 0) {
                    throw new SofnException("行政区划[" + id + "]下有子行政区划，删除失败");
                }
                SysRegion sysRegion = this.getOneById(id);
                if (sysRegion != null) {
                    parentIds.add(sysRegion.getParentId());
                }
            });
            sysRegionMapper.batchDelete(ids, UserUtil.getLoginUserId(), new Date());

            // 删除缓存
            redisUtils.delete(SysManageEnum.SYS_REGION_CACHE_KEY.getCode());
            // 删除父ID的缓存
            if (!CollectionUtils.isEmpty(parentIds)) {
                parentIds.forEach((str) -> {
                    redisUtils.delete(SysManageEnum.SYS_REGION_CACHE_KEY.getCode() + "_" + str);
                });
            }

        }
    }


    @Override
    public PageUtils<SysRegionForm> getSysRegionByContionPage(Map<String, Object> paramas, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        // 查询的时候查询未删除的
        List<SysRegion> sysRegionList = sysRegionMapper.getSysRegionByContion(paramas);
        PageInfo<SysRegion> pageInfo = new PageInfo<SysRegion>(sysRegionList);

        // 转为VO
        PageInfo<SysRegionForm> sysRegionFormPageInfo = new PageInfo<SysRegionForm>();
        BeanUtils.copyProperties(pageInfo, sysRegionFormPageInfo);
        // 设置值
        if (!CollectionUtils.isEmpty(sysRegionList)) {
            List<SysRegionForm> sysRegionForms = Lists.newArrayList();
            sysRegionList.forEach((sysRegion -> {
                SysRegionForm sysRegionForm = SysRegionForm.getSysRegionForm(sysRegion);
                sysRegionForms.add(sysRegionForm);
            }));
            sysRegionFormPageInfo.setList(sysRegionForms);
        }
        return PageUtils.getPageUtils(sysRegionFormPageInfo);
    }

    @Override
    public SysRegion getOneById(String id) {
        // 1. 校验内容是否存在
        List<SysRegion> sysRegionList = sysRegionMapper.selectList(new QueryWrapper<SysRegion>().eq("del_Flag", SysManageEnum.DEL_FLAG_N.getCode())
                .eq("id", id));
        // 如果没有查询到或者查询到了不止一个就抛出数据异常
        if (sysRegionList == null || sysRegionList.size() == 0) {
            return null;

        }
        if (sysRegionList.size() != 1) {
            log.info("当前ID{}对应了多条数据，请检查数据", id);
            throw new SofnException("该条数据异常");
        }
        return sysRegionList.get(0);
    }

    @Override
    public List<SysRegionForm> getListByRegionCode(String parentId) {
        Map<String, Object> paramas = Maps.newHashMap();
        if(StringUtils.isBlank(parentId)){
            parentId = SysManageEnum.ROOT_LEVEL.getCode();
        }
        paramas.put("parentId",parentId);
        paramas.put("delFlag",SysManageEnum.DEL_FLAG_N.getCode());
        List sysRegionList = sysRegionMapper.getSysRegionByContion(paramas);
        return sysRegionList;
    }


}
