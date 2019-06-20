package com.sofn.sys.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel
public class Auth {

    @ApiModelProperty(name = "token",value = "token")
    private String token;
    @ApiModelProperty(name = "sysUser",value = "用户")
    private SysUser sysUser;

}
