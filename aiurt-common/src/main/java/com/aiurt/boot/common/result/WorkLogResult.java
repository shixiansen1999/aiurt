package com.aiurt.boot.common.result;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
@TableName("work_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="work_log对象", description="工作日志")
public class WorkLogResult {

    /**主键id*/
    private  Long  id;

    /**巡检编号*/
    private  String  patrolCode;

    /**巡检内容*/
    private  String  patrolCodeDesc;

    /**检修编号*/
    private  String  repairCode;

    /**检修内容*/
    private  String  repairCodeDesc;

    /**故障编号*/
    private  String  faultCode;

    /**故障内容*/
    private  String  faultDesc;

    /**提交状态:0-未提交 1-已提交*/
    private  Integer  status;

    /**提交状态描述:0-未提交 1-已提交*/
    private  String  statusDesc;

    /**审核状态:0-未审核 1-已审核*/
    private  Integer  checkStatus;

    /**确认状态:0-未确认 1-已确认*/
    private  Integer  confirmStatus;

    /**提交人id*/
    private  String  submitId;

    /**提交人*/
    private  String  submitName;

    /**提交时间*/
    private  Date  submitTime;

    /**日期*/
    private  Date  logTime;

    /**工作内容*/
    private  Object  workContent;

    /**交接班内容*/
    private  Object  content;

    /**接班人id*/
    private  String  succeedId;

    /**接班人*/
    private  String  succeedName;

    /**确认状态描述*/
    private  String  confirmStatusDesc;

    /**审核状态描述*/
    private  String  checkStatusDesc;

    public List<String> urlList;


}
