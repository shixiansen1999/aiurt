package com.aiurt.modules.faultenclosure.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: fault_enclosure
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Data
@TableName("fault_enclosure")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_enclosure对象", description="fault_enclosure")
public class FaultEnclosure implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id，自动递增*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id，自动递增")
    private Integer id;
	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
    private String code;
	/**类型*/
	@Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private Integer type;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String name;
	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private String url;
	/**删除状态：0.未删除 1已删除*/
	@Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
