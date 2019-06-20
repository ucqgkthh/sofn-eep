package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.mapper.SysUserRoleMapper;
import com.sofn.sys.model.SysRole;
import com.sofn.sys.model.SysUserRole;
import com.sofn.sys.service.SysRoleService;
import com.sofn.sys.service.SysUserRoleService;
import com.sofn.sys.util.IdUtil;
import com.sofn.sys.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户角色关系维护
 * Created by heyongjie on 2019/6/11 10:08
 */
@Service
@Slf4j
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public String getRoleName(List<String> roleIds) {
        log.info("【查询角色名称】此次要查询的集合为{}",roleIds);
        if(CollectionUtils.isEmpty(roleIds)){
            return "";
        }
        List<String> roleNames = Lists.newArrayList();
        roleIds.forEach((roleId) -> {
           SysRole sysRole =  sysRoleService.getById(roleId);
           // 如果不为空或者未删除
           if(sysRole != null && !SysManageEnum.DEL_FLAG_Y.getCode().equals(sysRole.getDelFlag())){
               roleNames.add(sysRole.getRoleName());
           }
        });
        return IdUtil.getStrIdsByList(roleNames);
    }

    @Override
    public Set<String> getRoleIdsByUserId(String userId) {
        // 根据用户ID查询出用户有的角色ID集合
        List<SysUserRole> sysUserRoles =  this.baseMapper.selectList(new QueryWrapper<SysUserRole>().eq("userId",userId));
        Set<String> roleIds = Sets.newHashSet();
        if(!CollectionUtils.isEmpty(sysUserRoles)){
            sysUserRoles.forEach((sysUserRole -> {
                if(sysUserRole != null) {
                    roleIds.add(sysUserRole.getRoleId());
                }
            }));
        }
        return roleIds;
    }

    @Override
    public Set<String> getUserIdByRoleId(String roleId) {
        // 根据角色ID查询出当前角色所对应的用户ID集合
        List<SysUserRole> sysUserRoles = this.baseMapper.selectList(new QueryWrapper<SysUserRole>().eq("roleId",roleId));
        Set<String> userIds = Sets.newHashSet();
        if(!CollectionUtils.isEmpty(sysUserRoles)){
            sysUserRoles.forEach((sysUserRole -> {
                if(sysUserRole != null){
                    userIds.add(sysUserRole.getUserId());
                }
            }));
        }
        return userIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRelationshipByUserId(List<String> roleIds,String userId) {
        // 根据用户ID更改用户角色关系
        // 1. 查询出当前用户的角色信息
        Set<String> oldRoleIds =  getRoleIdsByUserId(userId);
        log.info("【更新用户对应的角色信息】用户{}新的角色信息{},以前的角色信息{}",userId,roleIds,oldRoleIds );
        // 2. 将roleIds去重
        Set<String> newRoleIds = Sets.newHashSet(roleIds);
        // 3. 比较是否相同
        boolean flag = SetUtils.isEqualSet(oldRoleIds,newRoleIds);
        if(!flag){
            // 4. 更改用户对应的角色信息
            // 4.1 删除之前的角色信息
            this.baseMapper.delete(new QueryWrapper<SysUserRole>().eq("userId",userId));
            // 4.2 批量添加角色信息
            if(!CollectionUtils.isEmpty(roleIds)){
                List<SysUserRole> sysUserRoles =  getSysUserRole(roleIds,userId,null);
                if(!CollectionUtils.isEmpty(sysUserRoles)){
                    sysUserRoleMapper.batchSaveSysUserRole(sysUserRoles);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRelationshipByRoleId(List<String> userIds,String roleId) {
        // 根据角色ID更改角色用户关系
        // 1. 查询出当前角色的用户信息
        Set<String> oldUserIds =  getUserIdByRoleId(roleId);
        log.info("【更新角色对应的用户信息】角色{}新的用户信息{},以前的用户信息{}",roleId,userIds,oldUserIds );
        // 2. 将userIds去重
        Set<String> newUserIds = Sets.newHashSet(userIds);
        // 3. 比较是否相同
        boolean flag = SetUtils.isEqualSet(oldUserIds,newUserIds);
        if(!flag){
            // 4. 更改当前角色的用户信息
            // 4.1 删除之前的用户信息
            this.baseMapper.delete(new QueryWrapper<SysUserRole>().eq("roleId",roleId));
            // 4.2 批量添加角色信息
            if(!CollectionUtils.isEmpty(userIds)){
                List<SysUserRole> sysUserRoles =  getSysUserRole(userIds,null,roleId);
                if(!CollectionUtils.isEmpty(sysUserRoles)){
                    sysUserRoleMapper.batchSaveSysUserRole(sysUserRoles);
                }
            }
        }
    }

    /**
     * 根据ID集合获取SysUserRoles
     * @param infos  UserIDs OR RoleIds
     * @param userId  用户ID
     * @param roleId  角色ID
     * @return   List<SysUserRole>
     */
    private List<SysUserRole> getSysUserRole(List<String> infos,String userId,String roleId){
        List<SysUserRole> sysUserRoles = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(infos)){
            infos.forEach((info) -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setId(IdUtil.getUUId());
                // 设置信息
                if(!StringUtils.isEmpty(roleId)){
                    sysUserRole.setRoleId(roleId);
                }else{
                    sysUserRole.setRoleId(info);
                }
                if(!StringUtils.isEmpty(userId)){
                    sysUserRole.setUserId(userId);
                }else{
                    sysUserRole.setUserId(info);
                }
                sysUserRoles.add(sysUserRole);

            });

        }
        return sysUserRoles;
    }
}
