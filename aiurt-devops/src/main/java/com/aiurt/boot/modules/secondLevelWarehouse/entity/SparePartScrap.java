package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("spare_part_scrap")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="spare_part_scrap对象", description="备件报损")
public class SparePartScrap {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
	@NotBlank(message = "备件编号不能为空")
	private  String  materialCode;

	@ApiModelProperty(value = "线路编号")
	private  String  lineCode;

	@ApiModelProperty("站点编号")
	private String stationCode;

	/**报废数量*/
	@Excel(name = "报废数量", width = 15)
    @ApiModelProperty(value = "报废数量")
	@NotNull(message = "报废数量不能为空")
	private  Integer  num;

	/**报损原因*/
	@Excel(name = "报损原因", width = 15)
    @ApiModelProperty(value = "报损原因")
	@NotNull(message = "报损原因不能为空")
	private  String  reason;

	/**保管人*/
	@Excel(name = "保管人", width = 15)
	@ApiModelProperty(value = "保管人")
	private  String  keepPerson;

	/**报修/报废原因*/
	@Excel(name = "报修/报废原因", width = 15)
	@ApiModelProperty(value = "报修/报废原因")
	private  String  scrapReason;

	/**送修时间*/
	@Excel(name = "送修时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "送修/报废时间")
	private  java.util.Date  repairTime;

	/**送修部门*/
	@Excel(name = "送修部门", width = 15)
	@ApiModelProperty(value = "送修部门")
	private  String  scrapDepart;

	/**购置日期*/
	@Excel(name = "购置日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "购置日期")
	private  java.util.Date  buyTime;

	/**规定年限*/
	@Excel(name = "规定年限", width = 15)
	@ApiModelProperty(value = "规定年限")
	private  Integer  serviceLife;

	/**使用年限*/
	@Excel(name = "使用年限", width = 15)
	@ApiModelProperty(value = "使用年限")
	private  Integer  useLife;

	/**报损时间*/
	@Excel(name = "报损时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "报损时间")
	@NotNull(message = "报损时间不能为空")
	private  java.util.Date  scrapTime;

	/**状态（0-未处理 1-报修 2-报废）*/
	@Excel(name = "状态（0-未处理 1-报修 2-报废）", width = 15)
    @ApiModelProperty(value = "状态（0-未处理 1-报修 2-报废）")
	private  Integer  status;

	/**存放位置*/
	@Excel(name = "存放位置", width = 15)
	@ApiModelProperty(value = "存放位置")
	private  String  orgId;

	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
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


    public static final String ID = "id";
    public static final String MATERIAL_CODE = "material_code";
    public static final String NUM = "num";
    public static final String REASON = "reason";
    public static final String STATUS = "status";
    public static final String DEL_FLAG = "del_flag";
    public static final String CREATE_BY = "create_by";
    public static final String UPDATE_BY = "update_by";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";


}
