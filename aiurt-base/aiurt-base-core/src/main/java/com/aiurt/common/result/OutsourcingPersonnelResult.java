package com.aiurt.common.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class OutsourcingPersonnelResult {

	/**主键id，自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
	private  Long  id;

	@Excel(name="序号",width = 15)
	@TableField(exist = false)
	private Integer serialNumber;

	/**名称*/
	@Excel(name = "人员名称", width = 15)
    @ApiModelProperty(value = "名称")
	private  String  name;

	/**所属单位*/
	@Excel(name = "所属单位", width = 15)
    @ApiModelProperty(value = "所属单位")
	private  String  company;

	/**职位名称*/
	@Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
	private  String  position;

	/**所属专业系统编号*/
    @ApiModelProperty(value = "所属专业系统编号")
	private  String  systemCode;

	/**所属专业系统*/
	@Excel(name = "所属专业系统", width = 15)
	@ApiModelProperty(value = "所属专业系统")
	private  String  systemName;

	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
	@ApiModelProperty(value = "联系方式")
	private  String  connectionWay;

	/**施工证编号*/
	@Excel(name = "施工证编号", width = 15)
	@ApiModelProperty(value = "施工证编号")
	private  String  certificateCode;

	/**创建时间，CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
	private  Date  createTime;

	/**删除状态：0-未删除 1-已删除*/
    @ApiModelProperty(value = "删除状态：0-未删除 1-已删除")
	private  Integer  delFlag;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**修改时间，根据当前时间戳更新*/
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
