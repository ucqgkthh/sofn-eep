package com.sofn.common.fileutil.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sofn.common.exception.SofnException;
import com.sofn.common.fileutil.dao.SysFileDao;
import com.sofn.common.fileutil.model.CommonSysFile;
import com.sofn.common.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件工具
 * Created by heyongjie on 2019/6/20 9:47
 */
@Slf4j
public class FastFileUtil {

    /**
     * 上传文件
     */
    public static CommonSysFile updateFile(MultipartFile multipartFile) {
        if (multipartFile == null) return null;
        // 1. 上传文件   因为不管文件名是否重复，上传之后文件服务器都会返回一个唯一的地址，所以不用判断文件名是否重复  直接上传文件
        FastDFSClient fastDFSClient = new FastDFSClient();
        String filePath = fastDFSClient.uploadFile(multipartFile);
        log.info("上传成功，获取的文件路径{}", filePath);
        if (StringUtils.isEmpty(filePath)) {
            throw new SofnException("上传文件失败");
        }
        // 2、获取文件信息
        CommonSysFile commonSysFile = new CommonSysFile();
        String id = IdUtil.getUUId();
        String fileName = multipartFile.getOriginalFilename();
        commonSysFile.setId(id);
        // 文件名称
        commonSysFile.setFileName(fileName);
        // 文件类型
        if (fileName.indexOf(".") != -1) {
            commonSysFile.setFileType(fileName.substring(fileName.lastIndexOf('.'), fileName.length()));
        } else {
            commonSysFile.setFileType("other");
        }
        commonSysFile.setFileSize(multipartFile.getSize());
        commonSysFile.setFilePath(filePath);
        commonSysFile.setCreateTime(new Date());
        commonSysFile.setCreateUserId("defaultUserId");
        SysFileDao sysFileDao = new SysFileDao();
        try {
            sysFileDao.saveFileInfo(commonSysFile);
        }catch (Exception e){
            e.printStackTrace();
            // 出现异常后删除文件服务器上的文件
            fastDFSClient.delete_file(filePath);
            return  null;
        }

        return commonSysFile;
    }

    /**
     * 批量上传文件
     *
     * @param files 要上传的文件集合
     * @return
     */
    public static List<CommonSysFile> batchUploadFile(List<MultipartFile> files) {
        // 这里采用单个单个文件上传
        List<CommonSysFile> commonSysFiles = Lists.newArrayList();
        if (files != null && files.size() > 0) {

            try{
                for (MultipartFile multipartFile : files) {
                    if (multipartFile != null) {
                        CommonSysFile commonSysFile = updateFile(multipartFile);
                        if (commonSysFile == null) {
                            log.info("【批量上传文件】{}文件上传失败", multipartFile.getOriginalFilename());
                            throw new SofnException("批量上传文件失败");
                        } else {
                            commonSysFiles.add(commonSysFile);
                        }
                    }
                }
            }catch (Exception e){
                // 如果出现错误，回滚
                e.printStackTrace();
                if(!CollectionUtils.isEmpty(commonSysFiles)){
                    for(CommonSysFile sysFile : commonSysFiles){
                        deleteFile(sysFile.getId());
                    }
                }
            }

        }
        return commonSysFiles;
    }

    /**
     * 获取文件绝对路径 多用于文件回显
     * @param id  要获取的文件ID
     * @return
     */
    public static String getFileAbsolutePath(String id) {
        SysFileDao sysFileDao = new SysFileDao();
        return sysFileDao.getFileAbsolutePath(id);
    }

    /**
     * 下载文件
     * @param id 要下载的文件ID
     * @return  fileName -> string 文件名称
     *          fileInfo -> byte[] 文件内容
     */
    public static Map<String,Object> downloadFile(String id){
        SysFileDao sysFileDao = new SysFileDao();
        CommonSysFile commonSysFile = sysFileDao.getSysFileById(id);
        if(commonSysFile == null){
            return null;
        }
        String path = commonSysFile.getFilePath();
        FastDFSClient fastDFSClient = new FastDFSClient();
        byte[] fileInfo = fastDFSClient.download_bytes(path);
        Map<String,Object> resultInfo = Maps.newHashMap();
        resultInfo.put("fileName",getFileName(commonSysFile.getFileName()));
        resultInfo.put("fileInfo",fileInfo);
        return resultInfo;
    }





    /**
     * 根据文件名获取添加UUID后的文件名 如文件名为test.xml 返回test-UUID.xml
     * @param fileName String  test.xml
     * @return test-UUID.xml
     */
    private static String getFileName(String fileName){
        String toPrefix = fileName.substring(0,fileName.lastIndexOf("."));
        // 获取文件后缀
        String toSuffix = fileName.substring(fileName.lastIndexOf("."),fileName.length());

        fileName = toPrefix+ "-" + IdUtil.getUUId() + "." + toSuffix;
        return fileName;
    }


    /**
     * 批量下载文件，最终获得一个压缩包
     * @param strIds
     * @return
     */
    public static String batchDownloadFileByZip(String strIds)throws Exception{
        // 1. 将ids转换为List
        List<String> ids = IdUtil.getIdsByStr(strIds);
        if (ids == null || ids.size() == 0) {
            return null;
        }
        SysFileDao sysFileDao = new SysFileDao();
        // 2. 找到有效的Path
        Map<String, String> effectivePaths = Maps.newHashMap();
        ids.forEach((id) -> {
            CommonSysFile commonSysFile = sysFileDao.getSysFileById(id);
            if (commonSysFile != null) {
                // 防止文件重复  在文件名中间加一个UUID
                effectivePaths.put(getFileName(commonSysFile.getFileName()), commonSysFile.getFilePath());
            } else {
                log.info("【批量下载文件】{}不存在", id);
            }
        });
        if (effectivePaths == null || effectivePaths.size() == 0) {
            log.info("【批量下载文件】没有有效文件，不处理");
            return null;
        }
        // 3. 创建临时文件 如C:\Users\heyongjie\Desktop\UUID    当前批次的所有文件都将下载到当前目录下
        String tempFilePath = FastDFSClient.tempFilePath + File.separator + IdUtil.getUUId();
        File file = new File(tempFilePath);
        if (file.exists()) {
            log.info("【批量下载文件】当前目录{}已经存在，正在删除...", tempFilePath);
            file.delete();
        }
        file.mkdirs();
        // 4. 下载文件
        effectivePaths.forEach((k,v) ->{
            try {
                execuDownloadFile(k,v,tempFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 5. 压缩文件
        String zipFilePath = tempFilePath + ".zip";
        ZipUtil.compress(tempFilePath,zipFilePath);
        // 6. 删除之前下载的文件
        deleteDirOrFile(tempFilePath);
        // 7. 返回这个压缩文件路径以供下载
        return zipFilePath;
    }

    /**
     * 下载文件到指定目录
     *
     * @param fileName     文件名称
     * @param filePath     文件在文件服务器上存储的路径
     * @param tempFilePath 文件下载到本地之后的路径    这个目录必须存在，如果不存在就不执行下载
     */
    private static void execuDownloadFile(String fileName, String filePath, String tempFilePath) throws Exception {
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            // 创建文件  如:C:\Users\heyongjie\Desktop\UUID\aaaUUID.txt
            File file = new File(tempFilePath + File.separator + fileName );
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            FastDFSClient fastDFSClient = new FastDFSClient();
            int code = fastDFSClient.download_file(filePath,bos);
            if(code == -1){
                log.info("【批量下载文件】文件{}下载失败，可能是文件服务器上不存在，或者其他，请检查",fileName);
            }
        }else{
            log.info("临时文件夹{}不存在",tempFilePath);
        }
    }

    /**
     * 删除文件夹或者文件  注意：会删除当前目录和当前目录下的所有文件和文件夹
     * @param path
     */
    public static void deleteDirOrFile(String path){
        File file = new File(path);
        if(file.exists()){
            // 如果是文件直接删除
            if(file.isFile()){
                file.delete();
            }else{
                // 如果是文件夹获取下面所有的文件和文件夾
                File [] listFile =  file.listFiles();
                log.info("{}当前目录下的文件数目:{}",path,listFile.length);
                if(listFile != null && listFile.length > 0){
                    // 1.这里删除当前文件下的所有文件
                    for (File file1 : listFile) {
                        if(file1.isFile()){
                            file1.delete();
                        }else{
                            deleteDirOrFile(file1.getAbsolutePath());
                        }
                    }
                    // 2.删除当前文件
                    file.delete();
                }else if(listFile.length == 0){
                    // 如果文件夹下没有内容直接删除
                    log.info("删除文件夹");
                    file.delete();
                }
            }
        }
    }

    /**
     * 删除文件
     * @param id  待删除文件ID
     */
    public static void deleteFile(String id){
        SysFileDao sysFileDao = new SysFileDao();
        CommonSysFile commonSysFile = sysFileDao.getSysFileById(id);
        if (commonSysFile == null) {
            throw new SofnException("待删除文件不存在");
        }
        String path = commonSysFile.getFilePath();
        FastDFSClient fastDFSClient = new FastDFSClient();
        Integer code = fastDFSClient.delete_file(path);
        if (code == -1) {
            throw new SofnException("删除失败");
        }
        // 文件直接使用硬删除
        sysFileDao.deleteFileInfo(id);
    }

}
