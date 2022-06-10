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

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("stock_level2_check")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="stock_level2_check对象", description="二级库盘点列表")
public class StockLevel2Check {

	/**自增id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "自增id")
	private  Long  id;

	/**盘点任务单号*/
    @ApiModelProperty(value = "盘点任务单号")
	private  String  stockCheckCode;

	/**盘点仓库编号*/
    @ApiModelProperty(value = "盘点仓库编号")
	private  String  warehouseCode;

	/**盘点数量*/
	@Excel(name = "盘点数量", width = 15)
    @ApiModelProperty(value = "盘点数量")
	private  Integer  checkNum;

	/**盘点人id*/
	@Excel(name = "盘点人id", width = 15)
    @ApiModelProperty(value = "盘点人id")
	private  String  checkerId;

	/**盘点开始时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "盘点开始时间")
	private  java.util.Date  checkStartTime;

	/**盘点结束时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "盘点结束时间")
	private  java.util.Date  checkEndTime;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  note;

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
    public static final String STOCK_CHECK_CODE = "stock_check_code";
    private static final String WAREHOUSE_CODE = "warehouse_code";
    private static final String CHECK_NUM = "check_num";
    private static final String CHECKER_ID = "checker_id";
    private static final String CHECKER_NAME = "checker_name";
    private static final String CHECK_START_TIME = "check_start_time";
    private static final String CHECK_END_TIME = "check_end_time";
    private static final String NOTE = "note";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";


}
