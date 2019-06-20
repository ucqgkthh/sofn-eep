package com.sofn.common.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 通用实体（通用字段）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseModel<T extends BaseModel> extends Model<BaseModel> implements Serializable {
    private static final long serialVersionUID = 1L;

    // id
    private String id;
    // 创建人
    private String createUserId;
    // 创建时间
    private Date createTime;
    // 更新人
    private String updateUserId;
    // 更新时间
    private Date updateTime;

    /**
     * 是否删除
     */
    private String delFlag;
}
