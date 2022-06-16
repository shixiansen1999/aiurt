package com.aiurt.boot.modules.manage.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: cs_station_position
 * @Author: swsc
 * @Date:   2021-09-26
 * @Version: V1.0
 */
@Data
@TableName("cs_station_position")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="站点位置对象", description="站点位置对象")
public class StationPosition {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**站点ID*/
	@Excel(name = "站点ID", width = 15)
    @ApiModelProperty(value = "站点ID")
	private Integer stationId;
	/**站点位置*/
	@Excel(name = "站点位置", width = 15)
    @ApiModelProperty(value = "站点位置")
	private String position;
	/**说明*/
	@Excel(name = "说明", width = 15)
	@ApiModelProperty(value = "说明")
	private String remark;
	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
	private Integer delFlag;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}
