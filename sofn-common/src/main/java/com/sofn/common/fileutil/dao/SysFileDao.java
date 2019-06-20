package com.sofn.common.fileutil.dao;

import com.sofn.common.db.BeanHandler;
import com.sofn.common.db.JdbcTemplate;
import com.sofn.common.fileutil.model.CommonSysFile;
import com.sofn.common.fileutil.util.FastDFSClient;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 使用JDBC连接数据库获取文件信息
 * Created by heyongjie on 2019/6/19 17:21
 */
public class SysFileDao {

    /**
     * 根据ID 获取文件信息
     *
     * @param id 文件ID
     * @return SysFile
     */
    public CommonSysFile getSysFileById(String id) {
        String sql = "SELECT    " +
                "  ID                   id ,  " +
                "  FILE_NAME        fileName     ,  " +
                "  FILE_PATH        filePath     ,  " +
                "  FILE_SIZE        fileSize     ,  " +
                "  FILE_TYPE        fileType      ,  " +
                "  ORG              org      ,  " +
                "  CREATE_USER_ID   createUserId     ,  " +
                "  CREATE_TIME      createTime           " +
                "from sys_files  where id = ?";
        List<CommonSysFile> datas = JdbcTemplate.query(sql, new BeanHandler<>(CommonSysFile.class), id);
        CommonSysFile commonSysFile = null;
        if (!CollectionUtils.isEmpty(datas)) {
            commonSysFile = datas.get(0);
        }
        return commonSysFile;
    }

    /**
     * 获取文件的绝对路径
     *
     * @return http://127.0.0.1:80/filePath
     */
    public String getFileAbsolutePath(String id) {
        CommonSysFile commonSysFile = getSysFileById(id);
        if (commonSysFile != null) {
            String path = FastDFSClient.httpPrefix + FastDFSClient.trackerIp + ":" + FastDFSClient.httpTrackerHttpPort + "/" + commonSysFile.getFilePath();
            return path;
        }
        return null;
    }

    /**
     * 存储文件信息
     *
     * @param commonSysFile
     */
    public void saveFileInfo(CommonSysFile commonSysFile) throws Exception {
        String sql = " " +
                "INSERT INTO sys_files ( ID, FILE_NAME, FILE_PATH, FILE_SIZE, FILE_TYPE, ORG, CREATE_USER_ID, CREATE_TIME, DEL_FLAG) " +
                "VALUES " +
                " ( " +
                " ?, " +
                " ?, " +
                " ?, " +
                " ?, " +
                " ?, " +
                " ?, " +
                " ?, " +
                " ?, " +
                " ?  " +
                " )";
        JdbcTemplate.update(sql, commonSysFile.getId(), commonSysFile.getFileName(), commonSysFile.getFilePath(),
                commonSysFile.getFileSize(), commonSysFile.getFileType(), commonSysFile.getOrg(), commonSysFile.getCreateUserId(), commonSysFile.getCreateTime(), "N");
    }

    /**
     * 删除文件
     *
     * @param id 要删除的文件ID
     */
    public void deleteFileInfo(String id) {
        String sql = "DELETE from sys_files where id = ?";
        JdbcTemplate.update(sql, id);
    }

    public static void main(String[] args) {

        SysFileDao sysFileDao = new SysFileDao();
        // 获取文件信息
//        CommonSysFile commonSysFile = sysFileDao.getSysFileById("b7b89dab32b04bb6ac58dfb4cd53435c");

        // 添加文件
       /* CommonSysFile commonSysFile1 = new CommonSysFile();
        commonSysFile1.setId("test");
        commonSysFile1.setFileName("test");
        commonSysFile1.setFilePath("test");
        commonSysFile1.setFileType("other");
        commonSysFile1.setFileSize(563);
        commonSysFile1.setCreateUserId("defaultUserId");
        commonSysFile1.setCreateTime(new Date());
        try {
            sysFileDao.saveFileInfo(commonSysFile1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

       // 删除文件
        sysFileDao.deleteFileInfo("33a611ad4d9343f3aa7a669d32a0a643");

    }


}
