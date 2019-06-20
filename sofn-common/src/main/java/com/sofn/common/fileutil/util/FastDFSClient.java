package com.sofn.common.fileutil.util;

import com.sofn.common.exception.SofnException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * FastDFS Client
 * Created by heyongjie on 2019/6/5 14:03
 */
@Slf4j
public class FastDFSClient {
    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient1 storageClient = null;


    // FastDFSClient 配置
    public static String trackerIp = "";
    public static String trackerPort = "";
    private static String charset = "";
    private static String connectTimeoutInSeconds = "";
    private static String networkTimeoutInSeconds = "";
    public static String httpTrackerHttpPort = "";
    public static String httpPrefix = "";
    public static String tempFilePath = "";

    static {
        // 加载配置文件
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/fastdfs.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
            trackerIp =  properties.getProperty("trackerIp").trim();
            trackerPort = properties.getProperty("trackerPort").trim();
            charset = properties.getProperty("charset").trim();
            connectTimeoutInSeconds = properties.getProperty("connectTimeoutInSeconds").trim();
            networkTimeoutInSeconds = properties.getProperty("networkTimeoutInSeconds").trim();
            httpTrackerHttpPort = properties.getProperty("httpTrackerHttpPort").trim();
            httpPrefix = properties.getProperty("httpPrefix").trim();
            tempFilePath =  properties.getProperty("tempFilePath").trim();
        } catch (IOException e) {
            log.info("fastdfs.properties配置文件不存在");
            e.printStackTrace();
        }

    }


    /**
     *  初始化配置
     */
    public FastDFSClient()  {
        Properties properties = new Properties();
        if(StringUtils.isEmpty(trackerIp) || StringUtils.isEmpty(trackerPort)){
            throw new SofnException("请检查FastDFS配置：tracker_ip和tracker_port为必须项");
        }
        String trackerServers = trackerIp + ":" + trackerPort;
        properties.setProperty("fastdfs.tracker_servers",trackerServers);
        properties.setProperty("fastdfs.connect_timeout_in_seconds",connectTimeoutInSeconds);
        properties.setProperty("fastdfs.network_timeout_in_seconds",networkTimeoutInSeconds);
        properties.setProperty("fastdfs.charset",charset);
        // 如果FASTDFS 需要密码访问 请配置下面二个参数
//        properties.setProperty("fastdfs.http_anti_steal_token",fastDFSProperties.getHttp_anti_steal_token());
//        properties.setProperty("fastdfs.http_secret_key",fastDFSProperties.getHttp_secret_key());
        properties.setProperty("fastdfs.http_tracker_http_port",httpTrackerHttpPort);
        try {
            ClientGlobal.initByProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SofnException("初始化FastDFS配置异常");
        }
        trackerClient = new TrackerClient();
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SofnException("连接FastDFS服务器异常");
        }
        storageServer = null;
        storageClient = new StorageClient1(trackerServer, storageServer);
    }

    /**
     * 上传文件方法
     *
     * @param fileName 文件全路径
     * @param extName  文件扩展名，不包含（.）
     * @param metas    文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) {
        String result = null;
        try {
            result = storageClient.upload_file1(fileName, extName, metas);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  上传文件
     * @param multipartFile   MultipartFile
     * @return  null为失败
     */
    public String uploadFile(MultipartFile multipartFile){
        byte [] filsBytes = null;
        try {
            filsBytes =  multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SofnException("获取文件内容异常");
        }
        // 获取后缀
        String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        NameValuePair[] meta_list = new NameValuePair[3];
        meta_list[0] = new NameValuePair("fileName",multipartFile.getOriginalFilename());
        meta_list[1] = new NameValuePair("fileExt",ext);
        meta_list[2] = new NameValuePair("fileSize",String.valueOf(multipartFile.getSize()));

        if(filsBytes != null){
            try {
                String msg = this.uploadFile(filsBytes,ext,meta_list);
               return  msg;
            } catch (Exception e) {
                throw new SofnException("上传文件异常");
            }
        }
        return null;

    }

    /**
     * 上传文件,传fileName
     *
     * @param fileName 文件的磁盘路径名称 如：D:/image/aaa.jpg
     * @return null为失败
     */
    public String uploadFile(String fileName) {
        return uploadFile(fileName, null, null);
    }

    /**
     * @param fileName 文件的磁盘路径名称 如：D:/image/aaa.jpg
     * @param extName  文件的扩展名 如 txt jpg等
     * @return null为失败
     */
    public String uploadFile(String fileName, String extName) {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     *
     * @param fileContent 文件的内容，字节数组
     * @param extName     文件扩展名
     * @param metas       文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) {
        String result = null;
        try {
            result = storageClient.upload_file1(fileContent, extName, metas);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 上传文件
     *
     * @param fileContent 文件的字节数组
     * @return null为失败
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    /**
     * 上传文件
     *
     * @param fileContent 文件的字节数组
     * @param extName     文件的扩展名 如 txt  jpg png 等
     * @return null为失败
     */
    public String uploadFile(byte[] fileContent, String extName) {
        return uploadFile(fileContent, extName, null);
    }

    /**
     * 文件下载到磁盘
     *
     * @param path   图片路径
     * @param output 输出流 中包含要输出到磁盘的路径
     * @return -1失败,0成功
     */
    public int download_file(String path, BufferedOutputStream output) {
        int result = -1;
        try {
            byte[] b = storageClient.download_file1(path);
            try {
                if (b != null) {
                    output.write(b);
                    result = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } //用户可能取消了下载
            finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取文件数组
     *
     * @param path 文件的路径 如group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return
     */
    public byte[] download_bytes(String path) {
        byte[] b = null;
        try {
            b = storageClient.download_file1(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 删除文件
     *
     * @param group       组名 如：group1
     * @param storagePath 不带组名的路径名称 如：M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return -1失败,0成功
     */
    public Integer delete_file(String group, String storagePath) {
        int result = -1;
        try {
            result = storageClient.delete_file(group, storagePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param storagePath 文件的全部路径 如：group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return -1失败,0成功
     * @throws IOException
     * @throws Exception
     */
    public Integer delete_file(String storagePath) {
        int result = -1;
        try {
            result = storageClient.delete_file1(storagePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取远程服务器文件资源信息
     *
     * @param groupName      文件组名 如：group1
     * @param remoteFileName M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return
     */
    public FileInfo getFile(String groupName, String remoteFileName) {
        try {
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
