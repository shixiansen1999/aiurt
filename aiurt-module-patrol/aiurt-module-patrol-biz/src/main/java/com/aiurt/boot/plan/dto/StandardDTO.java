package com.aiurt.boot.plan.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.plan.dto
 * @className: StandardDTO
 * @author: life-0
 * @date: 2022/7/15 17:33
 * @description: TODO
 * @version: 1.0
 */
@Data
public class StandardDTO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
    /**巡检表名*/
    @Excel(name = "巡检表名", width = 15)
    @ApiModelProperty(value = "巡检表名")
    private java.lang.String name;
}
