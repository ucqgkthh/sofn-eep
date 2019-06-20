package com.sofn.sys.vo;

import com.sofn.sys.enums.SysManageEnum;
import com.sofn.sys.model.SysSubsystem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import javax.validation.constraints.NotBlank;
@ApiModel
@Data
@AllArgsConstructor
public class SysSubsystemForm {
    @ApiModelProperty("子系统ID")
    @Length(max = 32,message = "父节点ID超长")
    private String id;
    @Length(max = 32,message = "父节点ID超长")
    @ApiModelProperty("子系统父类ID")
    private String parentId;
    @NotBlank(message = "子系统名称不能为空")
    @Length(max = 50,message = "子系统名称超长")
    @ApiModelProperty("子系统名称")
    private String subsystemName;
    @NotBlank(message = "子系统appId不能为空")
    @Length(max = 50,message = "子系统appId超长")
    @ApiModelProperty("子系统APPID")
    private String appId;

    @Length(max=1,message = "是否删除标志超长")
    private String delFlag = SysManageEnum.DEL_FLAG_N.getCode();

    private String description;
    /**
     * 将表单对象转为持久层对象
     * @param sysSubsystemForm  表单对象
     * @return 持久层对象
     */
    public static SysSubsystem getSysSubsystem(SysSubsystemForm sysSubsystemForm){
        SysSubsystem sysSubsystem = new SysSubsystem();
        BeanUtils.copyProperties(sysSubsystemForm,sysSubsystem);
        return sysSubsystem;
    }

}
