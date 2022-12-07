package com.aiurt.boot.team.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: emergency_training_process_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_training_process_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_training_process_record对象", description="emergency_training_process_record")
public class EmergencyTrainingProcessRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 新增保存时的校验分组
     */
    public interface Save {}

    /**
     * 修改时的校验分组
     */
    public interface Update {}
	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**训练记录id*/
	@Excel(name = "训练记录id", width = 15)
    @ApiModelProperty(value = "训练记录id")
    private String emergencyTrainingRecordId;
	/**时间*/
	@Excel(name = "时间", width = 15, format = "HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:ss")
    @ApiModelProperty(value = "时间")
    @NotBlank(message = "时间不能为空", groups = {Save.class, Update.class})
    private java.util.Date trainingTime;
	/**是否次日： 0是 1否*/
	@Excel(name = "是否次日： 0是 1否", width = 15)
    @ApiModelProperty(value = "是否次日： 0是 1否")
    @NotBlank(message = "是否次日不能为空", groups = {Save.class, Update.class})
    private Integer nextDay;
	/**训练内容*/
	@Excel(name = "训练内容", width = 15)
    @ApiModelProperty(value = "训练内容")
    private String trainingContent;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
