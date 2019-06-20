package com.sofn.sys.vo;

import com.sofn.sys.model.SysResource;
import com.sofn.sys.model.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by sofn
 */
@ApiModel
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SysRoleForm {

    /**
     * 编号
     */
    @ApiModelProperty("角色ID")
    @Length(max = 32,message = "角色ID超长")
    private String id;

    /**
     * 角色标识 程序中判断使用,如"admin"
     */
    @ApiModelProperty("角色名称")
    @NotBlank(message = "角色标识不能为空")
    private String roleName;
    /**
     * 角色描述,UI界面显示使用
     */
    @ApiModelProperty("角色描述")
    @NotBlank(message = "角色描述不能为空")
    private String describe;
    /**
     * 拥有的资源
     */
    @ApiModelProperty("角色对应的资源Id(用\",\"分隔)")
    @NotBlank(message = "拥有的资源不能为空")
    private String resourceIds;

    /**
     * 是否可用,如果不可用将不会添加给用户
     */
    @ApiModelProperty("删除标识Y/N")
    private String delFlag;

    // 创建人
    @ApiModelProperty("创建人ID")
    private String createUserId;
    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;
    // 更新人
    @ApiModelProperty("修改人ID")
    private String updateUserId;
    // 更新时间
    @ApiModelProperty("修改时间")
    private Date updateTime;

    public SysRoleForm() {}
    public SysRoleForm(SysRole role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
        this.describe = role.getDescribe();
        this.delFlag = role.getDelFlag();
    }

    public static SysRole getSysRole(SysRoleForm sysRoleForm){
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleForm,sysRole);

        String[] resourceIds = sysRoleForm.getResourceIds().split(",");
        List<SysResource> sysResourceList = new ArrayList<>();
        for(String resourceId:resourceIds){
            SysResource sysResource = new SysResource();
            sysResource.setId(resourceId);
            sysResourceList.add(sysResource);
        }
        sysRole.setResourceList(sysResourceList);
        return sysRole;
    }

}
