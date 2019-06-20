package com.sofn.common.fileutil.model;

import lombok.Data;

import java.util.Date;

/**
 * 文件
 * Created by heyongjie on 2019/6/5 15:52
 */
@Data
public class CommonSysFile{

    // id
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 文件类型S
     */
    private String fileType;

    /**
     * 所属机构  由上传用户所属机构确认
     */
    private String org;

    // 创建人
    private String createUserId;
    // 创建时间
    private Date createTime;


}
