package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/23
 * @desc
 */
@Data
public class PatrolTaskUserContentDTO {
    /***id*/
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /***真实姓名*/
    @Excel(name = "真实姓名", width = 15)
    @ApiModelProperty(value = "真实姓名")
    private String realname;
    /***部门code(当前选择登录部门)*/
    @Excel(name = "部门code", width = 15)
    @ApiModelProperty(value = "部门code")
    private String orgCode;
    /**部门名称*/
    @Excel(name = "部门名称", width = 15)
    @ApiModelProperty(value = "部门名称")
    private transient String orgCodeTxt;
    /**线路*/
    @ApiModelProperty(value = "线路")
    @TableField(exist = false)
    private String lineCodes;
    /**部门*/
    @ApiModelProperty(value = "部门")
    @TableField(exist = false)
    private String departCodes;
    /**站点*/
    @ApiModelProperty(value = "站点")
    @TableField(exist = false)
    private String stationCodes;
}
