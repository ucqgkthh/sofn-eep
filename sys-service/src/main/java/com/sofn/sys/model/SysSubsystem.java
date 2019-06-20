package com.sofn.sys.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import com.sofn.sys.enums.GroupType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by sofn
 */
@TableName("SYS_SUBSYSTEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class SysSubsystem extends BaseModel<SysSubsystem> {
    private String subsystemName;
    private String delFlag;
    private String parentId;
    private String description;
    private String appId;

}
