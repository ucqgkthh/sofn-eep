package com.sofn.sys.web;
import com.google.common.collect.Maps;
import com.sofn.common.log.SofnLog;
import com.sofn.common.model.Result;
import com.sofn.sys.model.SysResource;
import com.sofn.sys.model.SysSubsystem;
import com.sofn.sys.service.SysResourceService;
import com.sofn.sys.service.SysSubsystemService;
import com.sofn.sys.util.Constants;
import com.sofn.sys.vo.SysResourceForm;
import com.sofn.sys.enums.ResourceType;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.sofn.common.utils.PageUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
/**
 * @author cjbi
 */

@Api(tags = "菜单接口")
@RestController
@RequestMapping("/sysResource")
public class SysResourceController extends BaseController{

    @Autowired
    private SysResourceService resourceService;

    @Autowired
    private SysSubsystemService sysSubsystemService;
    @ModelAttribute("/types")
    public ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    /*@GetMapping
    public String resourcePage(Model model) {
        PageHelper.orderBy("priority");
        model.addAttribute("resourceList", resourceService.queryAll());
        return "system/resource";
    }*/
    @SofnLog("按条件分页获取菜单信息")
    @ApiOperation(value = "按条件分页获取菜单信息")
    @PostMapping("/getSysRsourceByPage")
    public Result<List<SysResourceForm>>  getSysSubsystemByPage(@RequestParam(required = false) @ApiParam(value = "菜单名称") String resourceName,
                                                          @RequestParam(required = false) @ApiParam(value = "菜单编号") String resourceNo,
                                                          @ApiParam(name = "pageNo",value = "分页序号",required = true)  @RequestParam("pageNo")Integer pageNo,
                                                          @ApiParam(name = "pageSize",value = "每页数量",required = true)@RequestParam("pageSize")Integer pageSize) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("resourceName",resourceName);
        params.put("resourceNo",resourceNo);
        // 获取分页内容
        PageUtils<SysResourceForm> sysResourcePageUtils = resourceService.getSysResourceByContionPage(params,pageNo,pageSize);
        return Result.ok(sysResourcePageUtils);
    }
    @SofnLog("添加菜单信息")
    @ApiOperation(value = "添加菜单信息")
    @PostMapping("/createResource")
    public Result createResource(@Validated @RequestBody  @ApiParam(name = "菜单对象",
            value = "传入json格式", required = true)SysResourceForm resourceForm, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象r
        SysResource resource =  resourceForm.getSysResource(resourceForm);

        resourceService.createResource(resource);
        return Result.ok();
    }

    @SofnLog("修改菜单信息")
    @ApiOperation(value = "修改菜单信息")
    @PostMapping("/updateResource")
    public Result update(@Validated @RequestBody @ApiParam(name = "菜单对象",
            value = "传入json格式", required = true) SysResourceForm sysResourceForm, BindingResult result) {

        if (result.hasErrors()) {
            return Result.error(getErrMsg(result));
        }
        // 转化为MODEL对象
        SysResource resource =  sysResourceForm.getSysResource(sysResourceForm);
        resourceService.updateResource(resource);
        return Result.ok();
    }
    @SofnLog("删除菜单信息")
    @ApiOperation(value = "删除菜单信息")
    @PostMapping("/deleteResource")
    public Result delete(@ApiParam(name = "id",value = "菜单ID",required = true) @RequestParam(value = "id")String id) {
        if(id == null  || "".equals(id)){
            return Result.error("ID不能为空");
        }
        resourceService.deleteResource(id);
        return Result.ok();
    }
    @SofnLog("获取一个菜单信息")
    @ApiOperation(value = "根据ID获取一个")
    @PostMapping(value = "/getResourceOne")
    public Result<SysResourceForm> getSysResourceOne(@ApiParam(name = "id",value = "菜单ID",required = true)
        @RequestParam(value = "id") String id){
        SysResource sysResource = resourceService.getById(id);
        SysResource sysResourceParent= resourceService.getById(sysResource.getParentId());
        SysResourceForm sysResourceForm=new SysResourceForm(sysResource);
        sysResourceForm.setParentName(sysResourceParent.getResourceName());
        return Result.ok(sysResourceForm);
    }
    @ApiOperation(value = "根据子系统ID获取所属的所有菜单")
    @PostMapping(value = "/getAllResourceBySubsystemId")
    public Result<SysResourceForm> getAllResourceBySubsystemId(@ApiParam(name = "id",value = "菜单子系统ID",required = true) @RequestParam(value = "id") String id){
        List<SysResourceForm> sysResourceList = resourceService.getAllResourceBySubsystemId(id);
        if(sysResourceList==null){
            return Result.ok("子系统下还没有新建菜单");
        }
        return Result.ok(sysResourceList);
    }

    @ApiOperation(value = "根据角色ID获取权限菜单")
    @PostMapping(value = "/getAllResourceByRoleId")
    public Result<SysResourceForm> getAllResourceByRoleId(@ApiParam(name = "id",value = "角色ID",required = true) @RequestParam(value = "id") String id){
        List<SysResourceForm> sysResourceList = resourceService.getAllResourceByRoleId(id);
        if(sysResourceList==null){
            return Result.ok("改角色没有权限");
        }
        return Result.ok(sysResourceList);
    }

    @ApiOperation(value = "获取菜单树结构")
    @PostMapping(value = "/getSysResourceTree")
    public Result<SysResourceForm> getSysResourceTree(){
        List<SysResourceForm> allSysSubsystem= resourceService.getAllResource();
        List<SysResourceForm> treeSysSubsystem= resourceService.getChildPerms(allSysSubsystem, Constants.MENU_ROOT_ID );
        List<SysResourceForm> resourceFormList=new ArrayList<SysResourceForm>();
        for(int i=0;i<treeSysSubsystem.size();i++){
            //获取父菜单名
            SysResourceForm sysResourceForm=treeSysSubsystem.get(i);
            //BeanUtils.copyProperties(treeSysSubsystem.get(i),sysResourceForm);
            SysSubsystem sysSubsystem = sysSubsystemService.getById(treeSysSubsystem.get(i).getSubsystemId());
            SysResource sysResourceParent= resourceService.getById(treeSysSubsystem.get(i).getParentId());
            //如果为一级菜单,父菜单名称为子系统名称
            if(treeSysSubsystem.get(i).getParentId().equals(Constants.MENU_ROOT_ID)){
                sysResourceForm.setParentName(sysSubsystem.getSubsystemName());
            }else{
                sysResourceForm.setParentName(sysResourceParent.getResourceName());
            }
            sysResourceForm.setSysSubsystemName(sysSubsystem.getSubsystemName());
            resourceFormList.add(sysResourceForm);
        }
        return Result.ok(resourceFormList);
    }
}
