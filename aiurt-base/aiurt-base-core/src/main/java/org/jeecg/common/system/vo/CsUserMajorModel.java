package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户专业model
 * @author: lkj
 */
public class CsUserMajorModel {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**专业id*/
    @Excel(name = "专业id", width = 15)
    @ApiModelProperty(value = "专业id")
    private String majorId;

    /**专业编码*/
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    private String majorCode;

    /**专业名称*/
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private String majorName;
}
