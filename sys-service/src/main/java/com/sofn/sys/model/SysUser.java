package com.sofn.sys.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import com.sofn.sys.vo.SysResourceForm;
import lombok.Data;

import java.util.List;

/**
 * Created by sofn
 */
@TableName("SYS_USER")
@Data
public class SysUser extends BaseModel<SysUser> {

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态 启用、禁用
     */
    private String status;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    private String password;

    /**
     * 初始密码(未加密)
     */
    private String initPassword;

    /**
     * 加密盐值
     */
    @JSONField(serialize = false)
    private String salt;

    /**
     * 所属机构ID
     */
    private String organizationId;

    /* ---------- 以下字段来自联表查询 ------------*/
    /**
     * 用户的角色信息
     */
    @TableField(exist = false)
    private List<SysRole> roleList;

    /**
     * 用户的角色对应的权限
     */
    @TableField(exist = false)
    private List<String> resourceList;


    public SysUser(String username,String password) {
        this.username = username;
        this.password = password;
    }

    public SysUser(String username) {
        this.username = username;
    }
    public SysUser() {
    }
}
