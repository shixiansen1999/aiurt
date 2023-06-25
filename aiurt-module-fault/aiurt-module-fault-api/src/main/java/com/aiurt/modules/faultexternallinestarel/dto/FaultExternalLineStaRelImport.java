package com.aiurt.modules.faultexternallinestarel.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 生产调度-线路站点关联表
 * @Author: hlq
 * @Date:   2023-06-19
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FaultExternalLineStaRelImport implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**云轨线路id*/
	@Excel(name = "调度子系统线路ID", width = 15)
    @ApiModelProperty(value = "云轨线路id")
    private Integer iline;
	/**车站/工区 id*/
	@Excel(name = "调度子系统车站/工区ID", width = 15)
    @ApiModelProperty(value = "车站/工区 id")
    private Integer ipos;
	/**线路code*/
    @ApiModelProperty(value = "线路code")
    private String lineCode;
	/**站点code*/
	@Excel(name = "站点code", width = 15)
    @ApiModelProperty(value = "站点code")
    private String stationCode;
	/**调度子系统线路/站点名称*/
	@Excel(name = "调度子系统线路/站点名称", width = 15)
    @ApiModelProperty(value = "云轨线路/站点")
    private String scc;
	/**通信线路/站点*/
    @ApiModelProperty(value = "线路/站点")
    private String correspondenceScc;
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    private String wrongReason;
}
