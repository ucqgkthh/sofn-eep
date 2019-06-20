package com.sofn.sys.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sofn.common.model.BaseModel;
import lombok.Data;

/**
 * 文件
 * Created by heyongjie on 2019/6/5 15:52
 */
@Data
@TableName("sys_files")
public class SysFile extends BaseModel<SysFile> {

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
     * 文件类型
     */
    private String fileType;

    /**
     * 所属机构  由上传用户所属机构确认
     */
    private String org;


}
