package com.aiurt.modules.sparepart.entity;

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
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("spare_part_apply")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="spare_part_apply对象", description="备件申领")
public class SparePartApply {

	/**自增主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "自增主键id")
	private  Long  id;


	/**申领编号*/
	@Excel(name = "申领编号", width = 15)
    @ApiModelProperty(value = "申领编号")
	private  String  code;

	/**申领仓库 备件库*/
	@Excel(name = "申领仓库 备件库", width = 15)
    @ApiModelProperty(value = "申领仓库 备件库")
	private  String  warehouseCode;

	/**出库仓库 二级库*/
    @ApiModelProperty(value = "出库仓库 二级库")
	private  String  outWarehouseCode;

    @ApiModelProperty("备件类型")
	@TableField(exist = false)
	private Integer materialType;

    @ApiModelProperty("备件类型名称")
	@TableField(exist = false)
	private String materialTypeName;

    @ApiModelProperty("申领总数量")
	@TableField(exist = false)
	private Integer applyAllNum;

    @ApiModelProperty("所属班组")
	@TableField(exist = false)
	private String warehouseDepartment;

	@ApiModelProperty("保管人")
	@TableField(exist = false)
	private String keeperName;


	/**提交状态（0-未提交 1-已提交）*/
	@Excel(name = "提交状态（0-未提交 1-已提交）", width = 15)
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）(二级库出库列表页面就查询已提交的)")
	private  Integer  commitStatus;

	/**状态（0-未审核 1-已审核）*/
	@Excel(name = "状态（0-未审核 1-已审核）", width = 15)
    @ApiModelProperty(value = "状态（0-未审核 1-已审核）")
	private  Integer  status;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  remarks;

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
    private static final String CODE = "code";
    private static final String WAREHOUSE_CODE = "warehouse_code";
    private static final String OUT_WAREHOUSE_CODE = "out_warehouse_code";
    private static final String COMMIT_STATUS = "commit_status";
    private static final String STATUS = "status";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
