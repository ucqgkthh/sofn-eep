package com.sofn.sys.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import com.sofn.sys.enums.GroupType;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * Created by sofn
 */
@TableName("SYS_GROUP")
@Data
@EqualsAndHashCode(callSuper = false)
public class SysGroup extends BaseModel<SysGroup> {


    /**
     * 组名称
     */
    private String groupName;

    /**
     * 组类型
     */
    private GroupType type;
    private String delFlag;
    /**
     * 描述
     */
    private String description;


}
