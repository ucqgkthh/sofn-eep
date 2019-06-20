package com.sofn.sys.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import lombok.Data;

import java.util.List;

/**
 * Created by sofn
 */
@TableName("SYS_ROLE")
@Data
public class SysRole  extends BaseModel<SysRole>{

    /**
     * 角色标识 程序中判断使用,如"admin"
     */
    private String roleName;

    /**
     * 角色描述,UI界面显示使用
     */
    private String describe;


    /* ---------- 以下字段来自联表查询 ------------*/

    /**
     * 角色对应的权限列表
     */
    @TableField(exist = false)
    private List<SysResource> resourceList;


    public SysRole(String roleName) {
        this.roleName = roleName;
    }
    public SysRole() {
    }
}
