package com.sofn.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sofn.common.utils.PageUtils;
import com.sofn.sys.model.SysOrganization;
import com.sofn.sys.dto.TreeDto;
import java.util.Map;
import java.util.List;

/**
 * Created by sofn
 */
public interface SysOrganizationService extends IService<SysOrganization> {


    void updateOrganization(SysOrganization organization);
    void deleteOrganization(String id);
    PageUtils<SysOrganization> getSysOrganizationByContion(Map<String,Object> paramas, Integer pageNo, Integer pageSize);

    void createOrganization(SysOrganization organization);

  /*  List<TreeDto> queryOrgTree(Long pId);*/
    SysOrganization getSysOrganizationById(String id);

}
