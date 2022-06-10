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
 * @Description: cs_station
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("cs_station")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="cs_station对象", description="cs_station")
public class Station {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**站点*/
	@Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
	private String stationName;
	/**站点编号*/
	@Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
	private String stationCode;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
	private Integer sort;
	/**线路id*/
	@Excel(name = "线路id", width = 15)
    @ApiModelProperty(value = "线路id")
	private Integer lineId;
	/**线路名称*/
	@Excel(name = "线路名称", width = 15)
    @ApiModelProperty(value = "线路名称")
	private String lineName;
	/**班组id*/
	@Excel(name = "班组id", width = 15)
    @ApiModelProperty(value = "班组id")
	private String teamId;
	/**班组名称*/
	@Excel(name = "班组名称", width = 15)
    @ApiModelProperty(value = "班组名称")
	private String teamName;
	/**站点位置*/
	@Excel(name = "站点位置", width = 15)
    @ApiModelProperty(value = "站点位置")
	private String position;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
	private String description;
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
