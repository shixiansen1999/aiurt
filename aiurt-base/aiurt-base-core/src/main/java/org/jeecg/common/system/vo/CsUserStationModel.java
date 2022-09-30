package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *  用户站点model
 * @author lkj
 */
@Data
public class CsUserStationModel {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**站所id*/
    @Excel(name = "站所id", width = 15)
    @ApiModelProperty(value = "站所id")
    private String stationId;

    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**站点*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
    private String stationName;

    /**站点编号*/
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private String stationCode;

    @ApiModelProperty(value = "线路")
    private String lineCode;
}
