package com.sofn.sys.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关系
 * Created by heyongjie on 2019/6/11 9:57
 */
@TableName("SYS_ROLE_RESOURCE")
@Data
public class SysRoleResource {
    // id
    private String id;

    private String roleId;

    private String resourceId;

}
