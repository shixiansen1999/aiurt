package com.aiurt.boot.check.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
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
 * @Description: fixed_assets_check_category
 * @Author: aiurt
 * @Date:   2023-01-17
 * @Version: V1.0
 */
@Data
@TableName("fixed_assets_check_category")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fixed_assets_check_category对象", description="fixed_assets_check_category")
public class FixedAssetsCheckCategory implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**任务表主键*/
	@Excel(name = "任务表主键", width = 15)
    @ApiModelProperty(value = "任务表主键")
    private String checkId;
	/**资产类别编码*/
	@Excel(name = "资产类别编码", width = 15)
    @ApiModelProperty(value = "资产类别编码")
    private String categoryCode;
    /**资产类别名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "资产类别名称")
    private String categoryName;
}
