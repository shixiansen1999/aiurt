package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import java.io.Serializable;
import java.util.Date;

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
 * @Description: 二级库盘点列表记录
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
@Data
@TableName("stock_level2_check_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="stock_level2_check_detail对象", description="二级库盘点列表记录")
public class StockLevel2CheckDetail {

	/**主键自增id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键自增id")
	private  Long  id;


	/**盘点任务单号*/
	@Excel(name = "盘点任务单号", width = 15)
    @ApiModelProperty(value = "盘点任务单号")
	private  String  stockCheckCode;

	/**物资单号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
	private  String  materialCode;

	/**仓库编号*/
	@Excel(name = "仓库编号", width = 15)
    @ApiModelProperty(value = "仓库编号")
	private  String  warehouseCode;

	/**实盘数量*/
	@Excel(name = "实盘数量", width = 15)
    @ApiModelProperty(value = "实盘数量")
	private  Integer  actualNum;

	/**盘盈数量*/
	@Excel(name = "盘盈数量", width = 15)
    @ApiModelProperty(value = "盘盈数量")
	private  Integer  profitNum;

	/**盘亏数量*/
	@Excel(name = "盘亏数量", width = 15)
    @ApiModelProperty(value = "盘亏数量")
	private  Integer  lossNum;

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
	private  Date  createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  Date  updateTime;


    private static final String ID = "id";
    private static final String STOCK_CHECK_CODE = "stock_check_code";
    private static final String MATERIAL_CODE = "material_code";
    private static final String WAREHOUSE_CODE = "warehouse_code";
    private static final String ACTUAL_NUM = "actual_num";
    private static final String PROFIT_NUM = "profit_num";
    private static final String LOSS_NUM = "loss_num";
    private static final String NOTE = "note";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
