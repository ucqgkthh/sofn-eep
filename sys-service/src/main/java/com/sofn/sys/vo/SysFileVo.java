package com.sofn.sys.vo;

import com.sofn.common.fileutil.model.CommonSysFile;
import com.sofn.common.fileutil.util.FastDFSClient;
import com.sofn.common.utils.DateUtils;
import com.sofn.sys.model.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 文件VO对象
 * Created by heyongjie on 2019/6/18 16:15
 */
@ApiModel(value = "文件VO对象")
@Data
public class SysFileVo {

    /**
     * ID
     */
    @ApiModelProperty(value = "文件ID")
    private String id;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    private long fileSize;

    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型，pdf、word、txt由上传时的后缀名确认")
    private String fileType;

    @ApiModelProperty(value = "上传用户")
    private String createUserId;

    @ApiModelProperty(value = "上传时间")
    private String createTime;

    /**
     * 将持久层对象转为VO对象
     * @param commonSysFile  持久层对象
     * @return  VO对象
     */
    public static SysFileVo getSysFileVo(CommonSysFile commonSysFile){
        SysFileVo sysFileVo = new SysFileVo();
        BeanUtils.copyProperties(commonSysFile,sysFileVo);
        // 时间格式转换
        sysFileVo.setCreateTime(DateUtils.format(commonSysFile.getCreateTime(),DateUtils.DATE_TIME_PATTERN));
        // 路径拼接  http://127.0.0.1:8080/path
        String path  = FastDFSClient.httpPrefix + FastDFSClient.trackerIp+ ":" + FastDFSClient.httpTrackerHttpPort+ "/" + commonSysFile.getFilePath();
        sysFileVo.setFilePath(path);
        return sysFileVo;
    }

    /**
     * 将持久层对象转为VO对象
     * @param sysFile  持久层对象
     * @return  VO对象
     */
    public static SysFileVo getSysFileVo(SysFile sysFile){
        SysFileVo sysFileVo = new SysFileVo();
        BeanUtils.copyProperties(sysFile,sysFileVo);
        // 时间格式转换
        sysFileVo.setCreateTime(DateUtils.format(sysFile.getCreateTime(),DateUtils.DATE_TIME_PATTERN));
        // 路径拼接  http://127.0.0.1:8080/path
        String path  = FastDFSClient.httpPrefix + FastDFSClient.trackerIp+ ":" + FastDFSClient.httpTrackerHttpPort+ "/" + sysFileVo.getFilePath();
        sysFileVo.setFilePath(path);
        return sysFileVo;
    }

}
