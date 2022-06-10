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
 * @Description: 备件申领物资
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("spare_part_apply_material")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="spare_part_apply_material对象", description="备件申领物资")
public class SparePartApplyMaterial {

	/**自增主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "自增主键id")
	private  Long  id;

	/**申领编号*/
	@Excel(name = "申领编号", width = 15)
    @ApiModelProperty(value = "申领编号")
	private  String  applyCode;

	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
	private  String  materialCode;

	/**申请出库数量*/
	@Excel(name = "申请出库数量", width = 15)
    @ApiModelProperty(value = "申请出库数量")
	private  Integer  applyNum;

	/**实际出库数量*/
	@Excel(name = "实际出库数量", width = 15)
    @ApiModelProperty(value = "实际出库数量")
	private  Integer  actualNum;

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
    private static final String APPLY_CODE = "apply_code";
    private static final String MATERIAL_CODE = "material_code";
    private static final String APPLY_NUM = "apply_num";
    private static final String ACTUAL_NUM = "actual_num";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
