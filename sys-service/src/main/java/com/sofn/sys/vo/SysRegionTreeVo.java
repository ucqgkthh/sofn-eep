package com.sofn.sys.vo;

import com.sofn.sys.model.SysRegion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 *
 * 行政区划代码树结构
 * Created by heyongjie on 2019/5/29 15:02
 */
@Data
@ApiModel
public class SysRegionTreeVo {

//    @JsonProperty("rowid")  序列化别名
    @ApiModelProperty(value = "行政区划ID")
    private String id;

    @ApiModelProperty(value = "行政区划名称")
//    @JsonProperty("name")   未生效  因为项目引用了fastjson的包，如果要改的话需直接将字段改了
    private String regionName;

    @ApiModelProperty(value = "行政区划排序")
    private Integer sort ;

    @ApiModelProperty(value = "父行政区划ID")
    private String parentId;

    @ApiModelProperty(value = "行政区划下的行政区划")
    private List<SysRegionTreeVo> children;

    /**
     * 将持久对象转化为VO
     * @param sysRegion
     * @return
     */
    public static SysRegionTreeVo getSysRegionTreeVo(SysRegion sysRegion){
        SysRegionTreeVo sysRegionTreeVo = new SysRegionTreeVo();
        BeanUtils.copyProperties(sysRegion,sysRegionTreeVo);
        sysRegionTreeVo.setSort(sysRegion.getSortid());
        return sysRegionTreeVo;
    }

}
