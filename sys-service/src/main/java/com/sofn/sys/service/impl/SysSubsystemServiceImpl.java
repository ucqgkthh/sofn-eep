package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sofn.common.exception.SofnException;
import com.sofn.common.utils.PageUtils;
import com.sofn.common.utils.RedisUtils;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysSubsystemMapper;
import com.sofn.sys.model.SysSubsystem;
import com.sofn.sys.service.SysSubsystemService;

import java.util.*;

import com.sofn.sys.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行政区划CRUD 服务层
 * Created by heyongjie on 2019/5/29 11:15
 */
@Service
@Slf4j
public class SysSubsystemServiceImpl extends ServiceImpl<SysSubsystemMapper, SysSubsystem> implements SysSubsystemService {

    @Autowired
    private SysSubsystemMapper sysSubsystemMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSysSubsystem(SysSubsystem sysSubsystem) {
        // 1. 校验内容是否重复
        checkSysSubsystemIsExists(sysSubsystem.getSubsystemName(),sysSubsystem.getAppId(), null);
        // 2. 设置默认值
        sysSubsystem.setId(UUID.randomUUID().toString());
        sysSubsystem.setDelFlag(SysManageEnum.DEL_FLAG_N.getCode());
        // 如果父ID为空 那么就是一级系统 在顶级系统之下
        if(sysSubsystem.getParentId() == null || "".equals(sysSubsystem.getParentId())){
            sysSubsystem.setParentId(SysManageEnum.SUBSYSTEM_ROOT_LEVEL.getCode());
        }
        sysSubsystem.setCreateTime(new Date());
        sysSubsystem.setCreateUserId(UserUtil.getLoginUserId());

        // 3. 保存
        sysSubsystemMapper.insert(sysSubsystem);

        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_SUBSYSTEM_CACHE_KEY.getCode());
        // 删除父ID的缓存
        redisUtils.delete(SysManageEnum.SYS_SUBSYSTEM_CACHE_KEY.getCode() + sysSubsystem.getParentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysSubsystem(SysSubsystem sysSubsystem) {
        // 1. 校验内容是否存在
        SysSubsystem selectSysSubsystem = sysSubsystemMapper.selectById(sysSubsystem.getId());
        if (selectSysSubsystem == null || SysManageEnum.DEL_FLAG_Y.getCode().equals(selectSysSubsystem.getDelFlag())) {
            throw new SofnException("待修改内容不存在");
        }
        // 2. 校验内容是否重复
        checkSysSubsystemIsExists(sysSubsystem.getSubsystemName(), sysSubsystem.getAppId(), sysSubsystem.getId());

        // 3. 更改内容
        BeanUtils.copyProperties(sysSubsystem, selectSysSubsystem);
        selectSysSubsystem.setUpdateTime(new Date());
        selectSysSubsystem.setUpdateUserId(UserUtil.getLoginUserId());
        sysSubsystemMapper.updateById(selectSysSubsystem);

        // 删除缓存
        redisUtils.delete(SysManageEnum.SYS_SUBSYSTEM_CACHE_KEY.getCode());
        redisUtils.delete(SysManageEnum.SYS_SUBSYSTEM_CACHE_KEY.getCode() + sysSubsystem.getParentId());
    }

    @Override
    public void deleteSysSubsystem(String id) {
        SysSubsystem selectSysSubsystem = sysSubsystemMapper.selectById(id);
        // 2. 如果下面有子节点的不能删除
        List<SysSubsystem> SysSubsystemList = sysSubsystemMapper.getSonSysSubsystem(selectSysSubsystem.getId());
        if(SysSubsystemList != null && SysSubsystemList.size() > 0){
            throw new SofnException("当前系统下有子系统，删除失败");
        }
        // 使用软删除
        selectSysSubsystem.setDelFlag(SysManageEnum.DEL_FLAG_Y.getCode());
        updateSysSubsystem(selectSysSubsystem);
    }


    @Override
    public boolean checkSysSubsystemIsExists(String name, String appId, String id) {
        //检查是否重复
        if (id != null) {
            // 修改
            // 检查名称是否重复
            Integer number =sysSubsystemMapper.getSysSubsystemByNameOrAppId(name, null,id);
            if (number > 0) throw new SofnException("系统名称重复");
            // 检查Code是否重复
            number = sysSubsystemMapper.getSysSubsystemByNameOrAppId(null, appId,id);
            if (number > 0) throw new SofnException("系统APPID重复");
        } else {
            // 添加
            // 检查名称是否重复
            Integer number = sysSubsystemMapper.getSysSubsystemByNameOrAppId(name, null,null);
            if (number > 0) throw new SofnException("系统名称重复");
            // 检查Code是否重复
            number = sysSubsystemMapper.getSysSubsystemByNameOrAppId(null, appId,null);
            if (number > 0) throw new SofnException("系统APPID重复");
        }
        return false;
    }

    @Override
    public PageUtils<SysSubsystem> getSysSubsystemByContion(Map<String, Object> paramas, Integer pageNo, Integer pageSize) {
       System.out.println(pageNo+":"+pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<SysSubsystem> SysSubsystemList = sysSubsystemMapper.getSysSubsystemByContion(paramas);
        PageInfo<SysSubsystem> pageInfo = new PageInfo<SysSubsystem>(SysSubsystemList);
        return PageUtils.getPageUtils(pageInfo);
    }
    @Override
    public  List<SysSubsystem> getAllSysSubsystem(){

        List<SysSubsystem> SysSubsystemList = sysSubsystemMapper.getAllSysSubsystem();
        return   SysSubsystemList;
    }
    @Override
    public  List<SysSubsystem> getChildPerms (List<SysSubsystem> subsystemList,String parentId){
        List<SysSubsystem> SysSubsystemList = sysSubsystemMapper.getAllSysSubsystem();
            List<SysSubsystem> returnList = new ArrayList<SysSubsystem>();
            for (Iterator<SysSubsystem> iterator = subsystemList.iterator(); iterator.hasNext();)
            {
                SysSubsystem t = (SysSubsystem) iterator.next();
                // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
                if (t.getParentId().equals(parentId)) {
                    recursionFn(subsystemList, t);
                    returnList.add(t);
                }
            }
            return returnList;
        }

/**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysSubsystem> list, SysSubsystem t)
    {
        // 得到子节点列表
        List<SysSubsystem> childList = getChildList(list, t);
        /*  t.setChildren(childList);*/
        for (SysSubsystem tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                // 判断是否有子节点
                Iterator<SysSubsystem> it = childList.iterator();
                while (it.hasNext())
                {
                    SysSubsystem n = (SysSubsystem) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysSubsystem> getChildList(List<SysSubsystem> list, SysSubsystem t)
    {
        List<SysSubsystem> tlist = new ArrayList<SysSubsystem>();
        Iterator<SysSubsystem> it = list.iterator();
        while (it.hasNext())
        {
            SysSubsystem n = (SysSubsystem) it.next();
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysSubsystem> list, SysSubsystem t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    @Override
    public List<SysSubsystem> getSysSubsystemTreeById(String parentId){
            return sysSubsystemMapper.getSonSysSubsystem(parentId);
    }
}
