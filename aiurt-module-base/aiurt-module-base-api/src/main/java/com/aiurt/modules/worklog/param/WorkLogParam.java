package com.aiurt.modules.worklog.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class WorkLogParam {

    /**开始时间，首页的工作日志模块所用*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间")
    private Date startDate;
    /**结束时间，首页的工作日志模块所用*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间")
    private Date endDate;
    /**提交状态，0已提交 1未提交，首页的工作日志模块所用*/
    @ApiModelProperty(value = "提交状态，0已提交 1未提交")
    private Integer status;
    /**是否获取本班组数据的权限控制,修改departList,0或空为否，1为是，首页的工作日志模块所用*/
    @ApiModelProperty(value = "否获取本班组数据的权限控制,0或空为否，1为是")
    private Integer isMyTeam;

    private String day;

    /**
     * 审核状态 其他-未审核 3-已审核
     */
    private String checkStatus;

    /**
     * 确认状态:0-未确认 1-已确认
     */
    private  Integer  confirmStatus;

    /**
     * 提交人id
     */
    private  String  submitId;
    /**
     * 接班人id
     */
    private  String  successorId;

    /**
     * 提交人班组id
     */
    private  String  departId;

    /**
     * 提交人班组code
     */
    private  String  departCode;

    @ApiModelProperty(value = "权限班组集合(后台处理)")
    private List<String> departList;

    private List<String> selections;
}

