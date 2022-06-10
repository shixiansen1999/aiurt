package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.MaterialVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Data
@TableName("stock_in_order_level2")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="stock_in_order_level2对象", description="二级入库单信息")
public class StockInOrderLevel2 {

	/**自增主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "自增主键id")
	private  Long  id;

	/**入库单号*/
	@Excel(name = "入库单号", width = 15)
    @ApiModelProperty(value = "入库单号")
	private  String  orderCode;

	/**仓库编号*/
    @ApiModelProperty(value = "仓库编号")
	private  String  warehouseCode;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  note;

	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
	@TableLogic
	private  Integer  delFlag;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**入库时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "入库时间")
	private  Date  stockInTime;

	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  Date  createTime;

	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  Date  updateTime;


    private static final String ID = "id";
    private static final String ORDER_CODE = "order_code";
    private static final String WAREHOUSE_CODE = "warehouse_code";
    private static final String WAREHOUSE_NAME = "warehouse_name";
    private static final String NUM = "num";
    private static final String NOTE = "note";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
