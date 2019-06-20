package com.sofn.sys.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import lombok.Data;


/**
 * Created by sofn
 */
@TableName("SYS_ORGANIZATION")
@Data
public class SysOrganization extends BaseModel<SysOrganization> {

    private String id;
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

   /* public SysOrganization (){

    }*/
}
