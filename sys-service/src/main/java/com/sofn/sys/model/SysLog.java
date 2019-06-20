package com.sofn.sys.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.log.Log;
import com.sofn.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * Created by sofn
 */
@TableName("sys_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class SysLog  extends BaseModel<SysLog>{

    /**
     * 编号
     */
    private String id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户ip
     */
    private String ip;

    /**
     * 请求方法
     */
    private String reqMethod;

    /**
     * 执行方法
     */
    private String execMethod;

    /**
     * 响应时间
     */
    private Long execTime;

    /**
     * 描述
     */
    private String execDesc;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 请求URL
     */
    private String reqUri;

    /**
     * 参数
     */
    private String args;

    /**
     * 返回值
     */
    private String returnVal;

    public SysLog(){

    }

    public static SysLog getSysLog(Log log){
        SysLog sysLog = new SysLog();
        BeanUtils.copyProperties(log,sysLog);
        return sysLog;
    }


}
