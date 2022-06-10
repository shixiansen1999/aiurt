package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotNull;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
@TableName("spare_part_lend")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="spare_part_lend对象", description="备件借出表")
public class SparePartLend {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Long  id;

	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "备件物资编号")
	private  String  materialCode;

	/**借入数量*/
	@Excel(name = "借入数量", width = 15)
    @ApiModelProperty(value = "借入数量")
	private  Integer  lendNum;

	/**还回数量*/
	@Excel(name = "还回数量", width = 15)
    @ApiModelProperty(value = "还回数量")
	private  Integer  backNum;

	/**申请时间*/
	@Excel(name = "申请时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请时间")
	@NotNull(message = "申请时间不能为空")
	private  java.util.Date  lendTime;

	/**借入部门*/
	@Excel(name = "借入部门", width = 15)
	@ApiModelProperty(value = "借入部门")
	private  String  lendDepart;

	/**借用人*/
	@Excel(name = "借用人", width = 15)
	@ApiModelProperty(value = "借用人")
	private  String  lendPerson;

	/**借出部门*/
	@Excel(name = "借出部门", width = 15)
	@ApiModelProperty(value = "借出部门")
	private  String  outDepart;

	/**还回时间*/
	@Excel(name = "还回时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "还回时间")
	private  java.util.Date  backTime;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  remarks;

	/**状态（0-未还 1-已还）*/
	@Excel(name = "状态（0-未还 1-已还）", width = 15)
    @ApiModelProperty(value = "状态（0-未还 1-已还）")
	private  Integer  status;

	/**所在班组*/
	@Excel(name = "所在班组", width = 15)
    @ApiModelProperty(value = "所在班组")
	private  String  orgId;

	/**借出确认(0-未确认 1-已确认)*/
	@Excel(name = "借出确认(0-未确认 1-已确认)", width = 15)
	@ApiModelProperty(value = "借出确认(0-未确认 1-已确认)")
	private  Integer  lendConfirm;

	/**确认数量*/
	@Excel(name = "确认数量", width = 15)
	@ApiModelProperty(value = "确认数量")
	private  Integer  confirmNum;

	/**借出时间*/
	@Excel(name = "借出时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "借出时间")
	private  java.util.Date  outTime;

	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
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

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  java.util.Date  createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  java.util.Date  updateTime;


    private static final String ID = "id";
    private static final String MATERIAL_CODE = "material_code";
    private static final String LEND_NUM = "lend_num";
    private static final String BACK_NUM = "back_num";
    private static final String LENG_TIME = "leng_time";
    private static final String BACK_TIME = "back_time";
    private static final String REMARKS = "remarks";
    private static final String STATUS = "status";
    private static final String WAREHOUSE_CODE = "warehouse_code";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
