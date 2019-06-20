package com.sofn.sys.vo;


import com.sofn.sys.model.SysOrganization;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by sofn
 */

@Data
public class SysOrganizationForm  {
    private String id;
    /**
     * 组织机构名称
     */

    private String organizationName;
    /**
     * 父编号
     */
    private String parentId;
    /**
     * 父编号列表，如1/2/
     */
    private String parentIds;
    private String delFlag;

    /**
     * 叶子节点
     */
    private Boolean leaf;
    /**
     * 排序
     */
    private Long priority;

    public String makeSelfAsParentIds() {
        return getParentIds() + getId() + "/";
    }
    public String getId() {
        return id;
    }
    public String getParentIds() {
        return parentIds;
    }
    public static SysOrganization getSysOrganization(SysOrganizationForm sysOrganizationForm){
        SysOrganization sysOrganization = new SysOrganization();
        BeanUtils.copyProperties(sysOrganizationForm,sysOrganization);
        return sysOrganization;
    }
    /*public SysOrganizationForm(SysOrganization sysOrganization) {
        this.id = sysOrganization.getId();
        this.roleName = sysOrganization.getRoleName();
        this.description = sysOrganization.getDescription();
        this.resourceIds = sysOrganization.getResourceIds();
        this.resourceIdList = Arrays.asList(role.getResourceIds().split(",")).stream().map(Long::valueOf).collect(Collectors.toList());
        this.delFlag = role.getDelFlag();
    }*/

}
