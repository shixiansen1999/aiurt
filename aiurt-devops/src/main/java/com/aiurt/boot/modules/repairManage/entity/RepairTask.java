package com.aiurt.boot.modules.repairManage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 检修单列表
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Data
@TableName("repair_task")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "repair_task对象", description = "检修单列表")
public class RepairTask {

    /**
     * 主键id,自动递增
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    /**
     * 检修计划池id,repair_pool.id
     */
//    @Excel(name = "检修计划池id,repair_pool.id", width = 15)
    @ApiModelProperty(value = "检修计划池id,repair_pool.id")
    private String repairPoolIds;


    @Excel(name = "线路",width = 30)
    @TableField(exist = false)
    @ApiModelProperty(value = "线路")
    private String lineName;

    /**
     * 编号,示例:JX20211105
     */
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "编号,示例:JX20211105 ")
    private String code;


    @Excel(name = "检修周", width = 15)
    @ApiModelProperty(value = "周数")
    private Integer weeks;

    @Excel(name = "适用站点", width = 15,replace = {"控制中心_1","车辆段_2","班组_3"})
    @ApiModelProperty(value = "类型(1-控制中心 2-车辆段 3-班组)")
    private Integer icType;

//    @Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
//    @Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @Excel(name = "班组",width = 30)
    @TableField(exist = false)
    @ApiModelProperty(value = "检修班组")
    private String teamName;

    @TableField(exist = false)
    @ApiModelProperty(value = "站点")
    private String stationName;

    /**
     * 原因，不予确认/验收 原因
     */
//    @Excel(name = "原因，不予确认/验收 原因", width = 15)
    @ApiModelProperty(value = "原因，不予确认/验收 原因")
    private String errorContent;

    /**
     * 检修人，检修人ids
     */
//    @Excel(name = "检修人，检修人ids", width = 15)
    @ApiModelProperty(value = "检修人，检修人ids")
    private String staffIds;

    @Excel(name = "检修人", width = 15)
    @ApiModelProperty(value = "检修人，检修人names")
    private String staffNames;

    /**
     * 检修记录
     */
//    @Excel(name = "检修记录", width = 15)
    @ApiModelProperty(value = "检修记录")
    private String content;

    @ApiModelProperty(value = "检修地点")
    private String position;

    /**
     * 处理结果
     */
//    @Excel(name = "处理结果", width = 15)
    @ApiModelProperty(value = "处理结果")
    private String processContent;

    /**
     * 提交人id
     */
    @ApiModelProperty(value = "提交人id")
    private String submitUserId;

    /**
     * 提交人
     */
//    @Excel(name = "提交人", width = 15)
    @ApiModelProperty(value = "提交人")
    private String sumitUserName;

    /**
     * 提交时间
     */
    @Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    /**
     * 检修状态：0.未检修
     * 1.已检修
     * 2.确认
     * 3.不予确认
     * 4.验收
     * 5.不予验收
     */
    @Excel(name = "状态", width = 15,replace = {"未检修_0","确认中_1","已确认_2","不予确认_3","已验收_4","不予验收_5"})
    @ApiModelProperty(value = "检修状态 0.未检修 1.已检修 2.确认 3.不予确认 4.验收 5.不予验收")
    private Integer status;

    /**
     * 检修状态：0待执行 1已完成 2漏检
     */
    @TableField(exist = false)
    private Integer repairStatus;

    /**
     * 确认人id
     */
    @ApiModelProperty(value = "确认人")
    private String confirmUserId;

    /**
     * 确认人
     */
    @Excel(name = "确认人", width = 15)
    @ApiModelProperty(value = "确认人")
    private String confirmUserName;

    /**
     * 确认时间
     */
    @Excel(name = "确认时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;

    @ApiModelProperty(value = "确认签名url")
    private String confirmUrl;

    /**
     * 验收人Id
     */
    @ApiModelProperty(value = "验收人Id")
    private String receiptUserId;

    /**
     * 验收人
     */
    @Excel(name = "验收人", width = 15)
    @ApiModelProperty(value = "验收人")
    private String receiptUserName;

    /**
     * 验收时间
     */
    @Excel(name = "验收时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "验收时间")
    private Date receiptTime;

    @ApiModelProperty(value = "验收签名url")
    @Excel(name = "验收时间",type = 2)
    private String receiptUrl;


    @ApiModelProperty(value = "回调故障编号")
    private String faultCode;

    /**
     * 删除状态：0.未删除 1已删除
     */
//    @Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private Integer delFlag;

    /**
     * 创建时间，CURRENT_TIMESTAMP
     */
//    @Excel(name = "创建时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * 更新时间
     */
//    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 创建者
     */
//    @Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
    private String createBy;

    /**
     * 更新者
     */
//    @Excel(name = "更新者", width = 15)
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "作业类型")
    private String workType;

    @ApiModelProperty(value = "计划令编码")
    private String planOrderCode;

    @ApiModelProperty(value = "计划令图片")
    private String planOrderCodeUrl;

    @ApiModelProperty(value = "组织id")
    private String organizationId;

    @ApiModelProperty(value = "是否验收")
    private Integer isReceipt;

    @Excel(name = "查看", width = 15 ,type = 5)
    @TableField(exist = false)
    private String des;

    private String deviceIds;

    public static final String ID = "id";

    public static final String REPAIR_POOL_ID = "repair_pool_id";

    public static final String CODE = "code";

    public static final String STATUS = "status";

    public static final String ERROR_CONTENT = "error_content";

    public static final String STAFF_IDS = "staff_ids";

    public static final String CONTENT = "content";

    public static final String POSITION = "position";

    public static final String PROCESS_CONTENT = "process_content";

    public static final String SUMIT_USER_NAME = "sumit_user_name";

    public static final String SUBMIT_TIME = "submit_time";

    public static final String CONFIRM_USER_NAME = "confirm_user_name";

    public static final String CONFIRM_TIME = "confirm_time";

    public static final String RECEIPT_USER_NAME = "receipt_user_name";

    public static final String RECEIPT_TIME = "receipt_time";

    public static final String DEL_FLAG = "del_flag";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_BY = "create_by";

    public static final String UPDATE_BY = "update_by";


}
