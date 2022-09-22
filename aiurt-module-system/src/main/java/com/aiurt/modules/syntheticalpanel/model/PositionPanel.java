package com.aiurt.modules.syntheticalpanel.model;

import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author lkj
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="综合看板线路信息", description="综合看板线路信息")
public class PositionPanel implements Serializable {

    /**id*/
    @ApiModelProperty(value = "id")
    private String id;

    /**线路编号*/
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;

    /**线路名称*/
    @ApiModelProperty(value = "线路名称")
    private String lineName;

    /**站点*/
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    /**站点编号*/
    @ApiModelProperty(value = "站点编号")
    private String stationCode;

    /**序号*/
    @ApiModelProperty(value = "序号")
    private Integer sort;

    @ApiModelProperty(value = "工区编号")
    @TableField(value = "`code`")
    private String code;

    /**工区名称*/
    @ApiModelProperty(value = "工区名称")
    @TableField(value = "`name`")
    private String name;

    /**关联班组集合*/
    @ApiModelProperty(value = "lineId")
    private List<CsStation> csStationList;

    /**查询站点id集合*/
    @ApiModelProperty(value = "ids")
    private List<String> ids;

    /**站点电话号码*/
    @ApiModelProperty(value = "站点电话号码")
    private String stationPhoneNum;

    /**机构/部门编码*/
    @ApiModelProperty(value = "机构/部门编码")
    private String orgCode;

    /**机构/部门名称*/
    @ApiModelProperty(value = "机构/部门名称")
    private String departName;

    /**机构电话*/
    @ApiModelProperty(value = "机构电话")
    private String departPhoneNum;

    /**预警信息状态*/
    @ApiModelProperty(value = "预警信息状态（0 开启 ，1 关闭）")
    private Integer warningStatus;

    /**开关站状态*/
    @ApiModelProperty(value = "开关站状态（0 开启 ，1 关闭）")
    private Integer openStatus;

    private List<SysUser> userList;

}
