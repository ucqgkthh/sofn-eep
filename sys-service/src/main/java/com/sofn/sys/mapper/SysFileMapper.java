package com.sofn.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sofn.sys.model.SysFile;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by heyongjie on 2019/6/5 15:56
 */
public interface SysFileMapper  extends BaseMapper<SysFile> {


    List<SysFile> getSysFileByCondition(Map<String,Object> params);

}
