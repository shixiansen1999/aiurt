package com.aiurt.boot.weeklyplan.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: construction_command_assist
 * @Author: aiurt
 * @Date:   2022-11-22
 * @Version: V1.0
 */
@Data
@TableName("construction_command_assist")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="construction_command_assist对象", description="construction_command_assist")
public class ConstructionCommandAssist implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**周计划令id*/
	@Excel(name = "周计划令id", width = 15)
    @ApiModelProperty(value = "周计划令id")
    private String planId;
	/**辅助站点code*/
	@Excel(name = "辅助站点code", width = 15)
    @ApiModelProperty(value = "辅助站点code")
    private String stationCode;
	/**负责人ID*/
	@Excel(name = "负责人ID", width = 15)
    @ApiModelProperty(value = "负责人ID")
    private String userId;
	/**负责人号码*/
	@Excel(name = "负责人号码", width = 15)
    @ApiModelProperty(value = "负责人号码")
    private String phone;
}
