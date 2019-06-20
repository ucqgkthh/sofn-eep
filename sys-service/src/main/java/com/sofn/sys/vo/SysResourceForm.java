package com.sofn.sys.vo;


import com.sofn.sys.enums.ResourceType;
import com.sofn.sys.model.SysResource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by sofn
 */
@ApiModel
@Data
@AllArgsConstructor
public class SysResourceForm {

    /**
     * 编号
     */
    @ApiModelProperty("菜单ID")
    @Length(max = 32,message = "菜单ID超长")
    private String id;
    @NotBlank(message = "菜单名称不能为空")
    /**
     * 资源名称
     */
    @ApiModelProperty("菜单编号")
    private String resourceNo;
    @Length(max = 50,message = "菜单名称超长")
    @ApiModelProperty("菜单名称")
    private String resourceName;
    /**
     * 资源类型
     */
    @ApiModelProperty("菜单类型")
    @NotNull(message = "菜单类型不能为空")
    private ResourceType type;
    /**
     * 资源路径
     */
    @ApiModelProperty("菜单url")
    private String resourceUrl;
    /**
     * 权限字符串
     */

    private String permission;
    /**
     * 父编号
     */
    @ApiModelProperty("父菜单ID")
    @Length(max = 32,message = "父菜单ID超长")
    @NotNull(message = "父编号不能为空")
    private String parentId;
    /**
     * 父编号列表
     */

    private String parentIds;
    private String parentName;


    private String delFlag;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序
     */
    private Long priority;
    private String subsystemId;
    /**
     * 叶子节点
     */
    private Boolean leaf;

    private String sysSubsystemName;

   /* private List<SysResourceForm> children;*/

    public SysResourceForm(SysResource resource) {
        this.id = resource.getId();
        this.resourceNo=resource.getResourceNo();
        this.resourceName = resource.getResourceName();
        this.type = resource.getType();
        this.resourceUrl = resource.getResourceUrl();
        this.permission = resource.getPermission();
        this.parentId = resource.getParentId();
        this.parentIds = resource.getParentIds();
        this.delFlag = resource.getDelFlag();
        this.icon = resource.getIcon();
        this.priority = resource.getPriority();
        this.leaf = resource.getLeaf();
        this.subsystemId=resource.getSubsystemId();
    }


    public String makeSelfAsParentIds() {
        return getParentIds() + getId() + "/";
    }
    public String getParentIds() {
        return parentIds;
    }
    public String getId() {
        return id;
    }
    public boolean isRootNode() {
        return parentId == "0";
    }

    public static SysResource getSysResource(SysResourceForm sysResourceForm){
        SysResource sysResource = new SysResource();
        BeanUtils.copyProperties(sysResourceForm,sysResource);
        return sysResource;
    }


}
