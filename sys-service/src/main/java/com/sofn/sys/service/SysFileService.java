package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysFile;
import com.sofn.sys.vo.SysFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件相关接口
 * Created by heyongjie on 2019/6/5 15:58
 */
public interface SysFileService  extends IService<SysFile> {

    /**
     * 单个文件上传 上传成功后返回当前文件所对应的ID
     * @param multipartFile  需要上传的文件
     * @return SysFile  上传的文件信息
     */
    SysFileVo uploadFile(MultipartFile multipartFile);


    /**
     * 批量上传文件，上传成功后将ID返回
     * @param files List<MultipartFile>  文件集合
     * @return  List<SysFile>  上传成功的文件信息
     */
    List<SysFileVo> batchUploadFile(List<MultipartFile> files);

    /**
     * 按条件分页查询文件信息
     * @param params   条件
     * @param pageNo   页数
     * @param pageSize 每页显示条数
     * @return
     */
    PageUtils<SysFileVo> getSysFileByContitionAndPage(Map<String,Object> params,Integer pageNo,Integer pageSize);

    /**
     * 删除文件
     * @param id  ID
     */
    void deleteFile(String id);


    /**
     * 单个文件下载
     * @param id 要下载文件ID
     */
    Map<String,Object> downloadFile(String id);

    /**
     * 批量下载文件
     * @param strIds String id,id,id
     */
    String batchDownloadFileByZip(String strIds) throws Exception;

    /**
     * 根据ID 获取文件全路径  如 IP:port/group01/MD01/....
     * @param id  ID
     * @return  文件全路径 包括IP
     */
    String  getFilePathById(String id);
}
