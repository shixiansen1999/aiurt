package com.aiurt.boot.statistics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexScheduleDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 日期
     */
    @ApiModelProperty(value = "日期，格式yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "日期不能为空")
    private Date date;
    /**
     * 站点编号
     */
    @ApiModelProperty(value = "站点编号")
    private java.lang.String stationCode;
    /**
     * 任务获取方式：1 个人领取、2常规指派、3 手工下发
     */
    @ApiModelProperty(value = "任务获取方式：1 个人领取、2常规指派、3 手工下发")
    private java.lang.Integer source;
    /**
     * 数据权限过滤，0按当前登录用户所管理的组织机构来进行过滤，1不进行过滤
     */
    @ApiModelProperty(value = "数据权限过滤，0按当前登录用户所管理的组织机构来进行过滤，1不进行过滤")
    private Integer isAllData;
}
