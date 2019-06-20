package com.sofn.sys.vo;

import com.sofn.sys.enums.GroupType;
import com.sofn.sys.model.SysGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by sofn
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SysGroupForm  {

    /**
     * 编号
     */
    private String id;
    /**
     * 组名称
     */
    @NotBlank(message = "组名称不能为空")
    private String groupName;

    /**
     * 组类型
     */
    @NotNull(message = "组类型不能为空")
    private GroupType type;
    private String delFlag;
    /**
     * 描述
     */
    private String description;

    public static SysGroup getSysGroup(SysGroupForm SysGroupForm){
        SysGroup SysGroup = new SysGroup();
        BeanUtils.copyProperties(SysGroupForm,SysGroup);
        return SysGroup;
    }
}
