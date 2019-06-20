package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.PageUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysOrganizationMapper;
import com.sofn.sys.model.SysOrganization;
import com.sofn.sys.model.SysUser;
import com.sofn.sys.service.SysOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by sofn
 */
@Service
@Slf4j
public class SysOrganizationServiceImpl extends ServiceImpl<SysOrganizationMapper,
        SysOrganization> implements SysOrganizationService {

    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public SysOrganization getSysOrganizationById(String id) {
        SysOrganization sysOrganization = sysOrganizationMapper.selectById(id);
        return sysOrganization;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrganization(SysOrganization organization) {
        // 1. 校验内容是否重复
        checkSysOrganizationIsExists(organization.getOrganizationName(), null);
        // 2. 设置默认值
        organization.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());

        organization.setCreateTime(new Date());
        organization.setCreateUserId(getLoginUserId());

        // 3. 保存
        sysOrganizationMapper.insert(organization);

        // 删除缓存
//        redisUtils.delete(SysManageEnum.SYS_ORGANIZATION_CACHE_KEY.getCode());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrganization(SysOrganization organization) {

        SysOrganization selectSysOrganization = sysOrganizationMapper.selectById(organization.getId());
        if (selectSysOrganization == null || SysManageEnum.DEL_FLAG_Y.getCode().equals(selectSysOrganization.getDelFlag())) {
            throw new SofnException("待修改内容不存在");
        }
        // 2. 校验内容是否重复
        checkSysOrganizationIsExists(organization.getOrganizationName(), organization.getId());
        // 3. 更改内容
        BeanUtils.copyProperties(organization, selectSysOrganization);
        selectSysOrganization.setUpdateTime(new Date());
        selectSysOrganization.setUpdateUserId(getLoginUserId());
        sysOrganizationMapper.updateById(selectSysOrganization);
        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_ORGANIZATION_CACHE_KEY.getCode());
    }

    @Override
    public void deleteOrganization(String id) {
        SysOrganization selectSysOrganization = sysOrganizationMapper.selectById(id);
        // 有子节点的不能删
        Integer countUser = sysOrganizationMapper.getUserOfOrganization(selectSysOrganization.getId());
        if (countUser > 0) {
            throw new SofnException("删除失败");
        }
        // 使用软删除
        selectSysOrganization.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        updateOrganization(selectSysOrganization);
    }

    @Override
    public PageUtils<SysOrganization> getSysOrganizationByContion(Map<String, Object> paramas, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<SysOrganization> sysOrganizationList = sysOrganizationMapper.getSysOrganizationByContion(paramas);
        PageInfo<SysOrganization> pageInfo = new PageInfo<SysOrganization>(sysOrganizationList);
        return PageUtils.getPageUtils(pageInfo);
    }

    public String getLoginUserId() {
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser == null) {
            // TODO 这里测试接口，先写一个默认值，最后需要改为抛出未登录异常
            return "defaultUserId";
        } else {
            return sysUser.getId();
        }

    }


    public boolean checkSysOrganizationIsExists(String name, String id) {
        //检查是否重复
        if (id != null) {

            // 修改
            // 检查名称是否重复
            Integer number = sysOrganizationMapper.getSysOrganizationByName(name, id);
            if (number > 0) throw new SofnException("组织名称重复");
        } else {
            // 添加
            // 检查名称是否重复
            Integer number = sysOrganizationMapper.getSysOrganizationByName(name, null);
            if (number > 0) throw new SofnException("组织名称重复");
        }
        return false;
    }
}
