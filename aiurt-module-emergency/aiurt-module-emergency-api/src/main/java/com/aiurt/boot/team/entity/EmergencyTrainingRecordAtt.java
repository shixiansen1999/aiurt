package com.aiurt.boot.team.entity;

import com.aiurt.common.aspect.annotation.Dict;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: emergency_training_record_att
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_training_record_att")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_training_record_att对象", description="emergency_training_record_att")
public class EmergencyTrainingRecordAtt implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 新增保存时的校验分组
     */
    public interface Save {}

    /**
     * 修改时的校验分组
     */
    public interface Update {}
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**训练记录id*/
	@Excel(name = "训练记录id", width = 15)
    @ApiModelProperty(value = "训练记录id")
    private String emergencyTrainingRecordId;
	/**附件路径*/
	@Excel(name = "附件路径", width = 15)
    @ApiModelProperty(value = "附件路径")
    @NotBlank(message = "附件路径不能为空", groups = {Save.class, Update.class})
    private String path;
	/**附件名称*/
	@Excel(name = "附件名称", width = 15)
    @ApiModelProperty(value = "附件名称")
    @NotBlank(message = "附件名称不能为空", groups = {Save.class, Update.class})
    private String name;
	/**附件大小*/
	@Excel(name = "附件大小", width = 15)
    @ApiModelProperty(value = "附件大小")
    @NotBlank(message = "附件大小不能为空", groups = {Save.class, Update.class})
    private String size;
	/**附件类型*/
	@Excel(name = "附件类型", width = 15)
    @ApiModelProperty(value = "附件类型")
    @NotBlank(message = "附件类型不能为空", groups = {Save.class, Update.class})
    private String type;
    /**上传人id*/
    @Excel(name = "上传人id", width = 15)
    @ApiModelProperty(value = "上传人id")
    @NotBlank(message = "上传人id不能为空", groups = {Save.class, Update.class})
    @Dict(dictTable = "sys_user", dicCode = "id", dicText ="realname")
    private String uploadBy;
    /**上传日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上传日期")
    @NotNull(message = "上传日期不能为空", groups = {Save.class, Update.class})
    private java.util.Date uploadTime;

	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
}
