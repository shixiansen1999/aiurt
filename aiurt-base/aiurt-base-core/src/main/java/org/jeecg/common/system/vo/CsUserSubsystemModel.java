package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户的子系统权限model
 * @author lkj
 */
public class CsUserSubsystemModel {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**子系统id*/
    @Excel(name = "子系统id", width = 15)
    @ApiModelProperty(value = "子系统id")
    private String systemId;

    /**名称*/
    @Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String systemName;

    /**编号*/
    @Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
    private String systemCode;
}
