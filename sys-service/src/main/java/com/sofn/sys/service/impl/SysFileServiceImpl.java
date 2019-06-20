package com.sofn.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sofn.common.fileutil.model.CommonSysFile;
import com.sofn.common.fileutil.util.FastFileUtil;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.mapper.SysFileMapper;
import com.sofn.sys.model.SysFile;
import com.sofn.sys.service.SysFileService;
import com.sofn.sys.vo.SysFileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件相关实现
 * Created by heyongjie on 2019/6/5 16:00
 */
@Service
@Slf4j
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {


    @Autowired
    private SysFileMapper sysFileMapper;

    /**
     * 批量下载时同时下载的文件数量
     */
    private static final Integer DOWNLOAD_FILE_NUM = 5;


    @Override
    @Transactional
    public SysFileVo uploadFile(MultipartFile multipartFile) {

       CommonSysFile sysFile =  FastFileUtil.updateFile(multipartFile);

        SysFileVo sysFileVo = SysFileVo.getSysFileVo(sysFile);
        return sysFileVo;
    }

    @Override
    @Transactional
    public List<SysFileVo> batchUploadFile(List<MultipartFile> files) {
        // 这里采用单个单个文件上传
        List<SysFileVo> list = Lists.newArrayList();
        List<CommonSysFile>  commonSysFiles =  FastFileUtil.batchUploadFile(files);
        if(!CollectionUtils.isEmpty(commonSysFiles)){
            commonSysFiles.forEach((commonSysFile -> {
                SysFileVo sysFileVo  = SysFileVo.getSysFileVo(commonSysFile);
                list.add(sysFileVo);
            }));
        }

        return list;
    }

    @Override
    public PageUtils<SysFileVo> getSysFileByContitionAndPage(Map<String, Object> params, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<SysFile> sysFiles = sysFileMapper.getSysFileByCondition(params);
        PageInfo pageInfo = new PageInfo(sysFiles);
        // 转为VO
        PageInfo sysFileVoPageInfo = new PageInfo();
        BeanUtils.copyProperties(pageInfo,sysFileVoPageInfo);
        // 设置值
        if(!CollectionUtils.isEmpty(sysFiles)){
            List<SysFileVo> sysFileVos = Lists.newArrayList();
            sysFiles.forEach((sysFile)->{
                SysFileVo sysFileVo = SysFileVo.getSysFileVo(sysFile);
                sysFileVos.add(sysFileVo);
            });
            sysFileVoPageInfo.setList(sysFileVos);
        }
        return PageUtils.getPageUtils(sysFileVoPageInfo);
    }

    @Override
    @Transactional
    public void deleteFile(String id) {
       FastFileUtil.deleteFile(id);
    }

    @Override
    public Map<String,Object> downloadFile(String id) {
       return FastFileUtil.downloadFile(id);
    }

    @Override
    public String batchDownloadFileByZip(String strIds) throws Exception {
       return FastFileUtil.batchDownloadFileByZip(strIds);
    }

    @Override
    public String getFilePathById(String id) {
       return FastFileUtil.getFileAbsolutePath(id);
    }


}
