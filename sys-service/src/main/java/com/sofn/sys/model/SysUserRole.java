package com.sofn.sys.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import lombok.Data;

/**
 * 用户角色关系
 * Created by heyongjie on 2019/6/11 9:57
 */
@TableName("SYS_USER_ROLE")
@Data
public class SysUserRole {
    // id
    private String id;

    private String userId;

    private String roleId;

}
