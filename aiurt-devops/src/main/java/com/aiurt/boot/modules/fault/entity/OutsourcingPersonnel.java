package com.aiurt.boot.modules.fault.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
import java.util.Date;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
@Data
@TableName("outsourcing_personnel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="outsourcing_personnel对象", description="委外人员")
public class OutsourcingPersonnel {

	/**主键id，自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
	private  Long  id;

	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	@NotBlank(message = "人员名称不能为空")
	private  String  name;

	/**所属单位*/
	@Excel(name = "所属单位", width = 15)
    @ApiModelProperty(value = "所属单位")
	@NotBlank(message = "所属单位不能为空")
	private  String  company;

	/**职位名称*/
	@Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
	@NotBlank(message = "职位名称不能为空")
	private  String  position;

	/**所属专业系统编号*/
	@Excel(name = "所属专业系统编号", width = 15)
    @ApiModelProperty(value = "所属专业系统编号")
	@NotBlank(message = "所属专业系统不能为空")
	private  String  systemCode;

	/**施工证编号*/
	@Excel(name = "施工证编号", width = 15)
	@ApiModelProperty(value = "施工证编号")
	@NotBlank(message = "施工证编号不能为空")
	private  String  certificateCode;

	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
    @ApiModelProperty(value = "联系方式")
	private  String  connectionWay;

	/**删除状态：0-未删除 1-已删除*/
	@Excel(name = "删除状态：0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0-未删除 1-已删除")
	@TableLogic
	private  Integer  delFlag;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间，CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
	private  Date  createTime;

	/**修改时间，根据当前时间戳更新*/
	@Excel(name = "修改时间，根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
	private  Date  updateTime;


    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COMPANY = "company";
    private static final String POSITION = "position";
    private static final String SYSTEM_CODE = "system_code";
    private static final String CONNECTION_WAY = "connection_way";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
