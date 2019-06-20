package com.sofn.sys.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import com.sofn.sys.enums.ResourceType;
import com.sofn.sys.vo.SysResourceForm;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
/**
 * Created by sofn
 */
@TableName("SYS_RESOURCE")
@Data
public class SysResource extends BaseModel<SysResource> {

    /**
     * 资源名称
     */
    private String resourceName;
    private String resourceNo;
    /**
     * 资源类型
     */

    private ResourceType type;
    /**
     * 资源路径
     */
    private String resourceUrl;
    /**
     * 权限字符串
     */
    private String permission;
    /**
     * 父编号
     */
    private String parentId;

    private String subsystemId;

    /**
     * 父编号列表
     */
    private String parentIds;
   /* private List<SysResource> parentList;*/
    private String delFlag;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序
     */
    private Long priority;
    /**
     * 叶子节点
     */
    private Boolean leaf;

    public String makeSelfAsParentIds() {
        return getParentIds() + getId() + "/";
    }

    public boolean isRootNode() {
        return parentId == "0";
    }


    public SysResource(String resourceName) {
        this.resourceName = resourceName;

    }
    public SysResource() {
    }

}
