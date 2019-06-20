package com.sofn.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.PageUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.sofn.sys.model.SysGroup;
import com.sofn.sys.service.SysGroupService;
import com.sofn.sys.model.SysUser;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SysGroupServiceImpl extends ServiceImpl<SysGroupMapper, SysGroup> implements SysGroupService {

    @Autowired
    private SysGroupMapper sysGroupMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createGroup(SysGroup group) {
        // 1. 校验内容是否重复
        checkSysGroupIsExists(group.getGroupName(), null);
        // 2. 设置默认值
        group.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());

        group.setCreateTime(new Date());
        group.setCreateUserId(getLoginUserId());

        // 3. 保存
        sysGroupMapper.insert(group);

        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_GROUP_CACHE_KEY.getCode());
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(SysGroup group) {

        SysGroup selectSysGroup = sysGroupMapper.selectById(group.getId());
        if (selectSysGroup == null || SysManageEnum.DEL_FLAG_Y.getCode().equals(selectSysGroup.getDelFlag())) {
            throw new SofnException("待修改内容不存在");
        }
        // 2. 校验内容是否重复
        checkSysGroupIsExists(group.getGroupName(),group.getId());
        // 3. 更改内容
        BeanUtils.copyProperties(group, selectSysGroup);
        selectSysGroup.setUpdateTime(new Date());
        selectSysGroup.setUpdateUserId(getLoginUserId());
        sysGroupMapper.updateById(selectSysGroup);
        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_GROUP_CACHE_KEY.getCode());
    }
    @Override
    public void deleteGroup(String id) {
        SysGroup selectSysGroup = sysGroupMapper.selectById(id);
        //角色下面有用户的不能删
        Integer  countUser= sysGroupMapper.getUserOfGroup(selectSysGroup.getId());
        if( countUser > 0){
            throw new SofnException("删除失败");
        }
        // 使用软删除
        selectSysGroup.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        updateGroup(selectSysGroup);
    }

    @Override
    public PageUtils<SysGroup> getSysGroupByContion(Map<String, Object> paramas, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<SysGroup> sysGroupList = sysGroupMapper.getSysGroupByContion(paramas);
        PageInfo<SysGroup> pageInfo = new PageInfo<SysGroup>(sysGroupList);
        return PageUtils.getPageUtils(pageInfo);
    }
    public String getLoginUserId(){
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if(sysUser == null){
            // TODO 这里测试接口，先写一个默认值，最后需要改为抛出未登录异常
            return "defaultUserId";
        }else{
            return sysUser.getId();
        }

    }
    public boolean checkSysGroupIsExists(String name,String id) {
        //检查是否重复
        if (id != null) {
            // 修改
            // 检查名称是否重复
            Integer number = sysGroupMapper.getSysGroupByName(name,id);
            if (number > 0) throw new SofnException("角色名称重复");
        } else {
            // 添加
            // 检查名称是否重复
            Integer number = sysGroupMapper.getSysGroupByName(name,null);
            if (number > 0) throw new SofnException("角色名称重复");
        }
        return false;
    }

}
