package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 仓库基础信息
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("warehouse")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="warehouse对象", description="仓库基础信息")
public class Warehouse {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**二级仓库编号*/
	@Excel(name = "二级仓库编号", width = 15)
    @ApiModelProperty(value = "二级仓库编号")
	private String warehouseCode;
	/**二级仓库名称*/
	@Excel(name = "二级仓库名称", width = 15)
    @ApiModelProperty(value = "二级仓库名称")
	private String warehouseName;
	/**所属班组*/
	@Excel(name = "所属班组", width = 15)
    @ApiModelProperty(value = "所属班组")
	private String warehouseDepartment;
}
