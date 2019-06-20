package com.sofn.sys.web;

import com.google.common.collect.Maps;
import com.sofn.common.exception.SofnException;
import com.sofn.common.model.Result;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.service.SysFileService;
import com.sofn.sys.vo.SysFileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 文件管理接口
 * Created by heyongjie on 2019/6/6 11:19
 */
@RequestMapping("/sys/file")
@Api(tags = "文件管理接口")
@Controller
public class SysFileController {

    @Autowired
    private SysFileService sysFileService;

    @PostMapping("/getSysFileByPage")
    @ApiOperation(value = "按条件分页查询文件")
    @ResponseBody
    public Result<SysFileVo> getSysFileByPage(@RequestParam(required = false) @ApiParam("文件名称（模糊查询）") String fileName,
                                              @RequestParam(required = false) @ApiParam("文件类别pdf、word...（模糊查询）") String fileType,
                                              @RequestParam(required = false) @ApiParam("创建用户ID") String createUserId,
                                              @RequestParam(value = "pageNo")Integer pageNo,
                                              @RequestParam(value = "pageSize")Integer pageSize) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileName",fileName);
        params.put("fileType",fileType);
        params.put("createUserId",createUserId);
        PageUtils<SysFileVo> pageUtils = sysFileService.getSysFileByContitionAndPage(params, pageNo, pageSize);
        return Result.ok(pageUtils);
    }

    @GetMapping("/getFileAbsolutePath/{id}")
    @ApiOperation(value = "根据文件ID获取文件绝对路径")
    @ResponseBody
    public Result getFileAbsolutePath(@ApiParam(value = "文件ID")
                                      @PathVariable(value = "id") String id) {
        if(StringUtils.isBlank(id)){
            return Result.error("ID必传");
        }
        String path = sysFileService.getFilePathById(id);
        return Result.ok((Object) path);
    }

    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传文件,返回文件信息")
    @ResponseBody
    public Result<SysFileVo> uploadFile(@ApiParam(value = "文件") @RequestParam("file")MultipartFile multipartFile) {
        SysFileVo sysFileVo = sysFileService.uploadFile(multipartFile);
        return Result.ok(sysFileVo);
    }

    @PostMapping(value = "/batchUploadFile",headers = {"content-type=multipart/form-data"})
    @ApiOperation(value = "批量上传文件，返回上传成功的文件信息集合")
    @ResponseBody
    public Result<List<SysFileVo>> batchUploadFile(@ApiParam(value = "文件集合") @RequestParam(value = "files") MultipartFile[] multipartFiles) {
        List<SysFileVo> sysFileVos = sysFileService.batchUploadFile(Arrays.asList(multipartFiles));
        return Result.ok(sysFileVos);
    }

    @ApiOperation(value = "下载文件")
    @GetMapping(value = "/downloadFile/{id}")
    public void downloadFile(@ApiParam(value = "要下载的文件ID",required = true) @PathVariable(value = "id") String id, HttpServletResponse response) {
        Map<String,Object> resultInfo  = sysFileService.downloadFile(id);
        byte [] bytes = (byte[]) resultInfo.get("fileInfo");
        String fileName = (String) resultInfo.get("fileName");
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/batchDownloadFile/{ids}")
    @ApiOperation(value = "批量下载文件，最终得到的是一个压缩包")
    public void batchDownloadFile(@ApiParam("需要下载的文件ID，多个用英文,号分隔")@PathVariable("ids") String ids, HttpServletResponse response){
        BufferedInputStream br = null;
        OutputStream outputStream = null;
        File file = null;
        try {
            String filePath = sysFileService.batchDownloadFileByZip(ids);
            file = new File(filePath);
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + file.getName());
            if(file.exists()){
                br = new BufferedInputStream(new FileInputStream(file));
                byte [] bytes = new byte[1024];
                int read = 0;
                outputStream = response.getOutputStream();
                while((read = (br.read(bytes,0,bytes.length))) != -1){
                    outputStream.write(bytes,0,read);
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SofnException("下载文件失败");
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 删除压缩的文件
            if(file.exists()){
                file.delete();
            }
        }

    }

    @PostMapping("/deleteFile/{id}")
    @ApiOperation(value = "根据ID删除文件")
    @ResponseBody
    public Result deleteFile(@ApiParam("要删除的文件ID")@PathVariable("id") String id){
        sysFileService.deleteFile(id);
        return Result.ok();
    }

}
