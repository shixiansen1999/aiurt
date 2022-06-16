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
 * @Description: 检修计划池
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Data
@TableName("repair_pool")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "repair_pool对象", description = "检修计划池")
public class RepairPool {

//    @Excel(name = "线路",width = 30)
    @TableField(exist = false)
    private String lineName;

//    @Excel(name = "站点",width = 30)
    @TableField(exist = false)
    private String stationName;

//    @Excel(name = "检修班组",width = 30)
    @TableField(exist = false)
    private String teamName;

    /**
     * 主键id，自动递增
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
    private Long id;

    /**
     * 检修规范ID
     */
//    @Excel(name = "检修规范ID", width = 15)
    @ApiModelProperty(value = "检修规范ID")
    private Integer inspectionCodeId;

    @Excel(name = "周数", width = 20,needMerge = true)
    @ApiModelProperty(value = "周数")
    private Integer weeks;

    /**
     * 检修类型
     */
    @Excel(name = "检修类型",replace = {"周检_1","月检_2","双月检_3","季检_4","半年检_5","年检_6"})
    @ApiModelProperty(value = "检修类型")
    private Integer type;

    @ApiModelProperty(value = "类型(1-控制中心 2-车辆段 3-车站)")
    private Integer icType;

    /**
     * 检修内容表id
     */
    @Excel(name = "检修内容", width = 50)
    @ApiModelProperty(value = "检修内容表")
    private String repairPoolContent;

    /**
     * 开始时间
     */
//    @Excel(name = "开始时间", width = 30, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
//    @Excel(name = "结束时间", width = 30, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 组织id
     */
//    @Excel(name = "组织id", width = 15)
    @ApiModelProperty(value = "组织id")
    private String organizationId;

    /**
     * 检修人员id
     */
//    @Excel(name = "检修人员id", width = 15)
//    @ApiModelProperty(value = "检修人员id")
//    private String repairUserIds;

    /**
     * 状态，0.未指派 1.已指派
     */
//    @Excel(name = "状态，0.未指派 1.已指派", width = 15)
//    @ApiModelProperty(value = "状态，0.未指派 1.已指派")
//    private Integer status;

    /**
     * 删除状态，0.未删除 1.已删除
     */
//    @Excel(name = "删除状态，0.未删除 1.已删除", width = 15)
    @ApiModelProperty(value = "删除状态，0.未删除 1.已删除")
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
     * 修改时间，根据当前时间戳更新
     */
//    @Excel(name = "修改时间，根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
    private Date updateTime;

    /**
     * 创建人
     */
//    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 更新人
     */
//    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "是否验收")
    private Integer isReceipt;

    public static final String ID = "id";

    public static final String TYPE = "type";

    public static final String REPAIR_POOL_CONTENT = "repair_pool_content";

    public static final String START_TIME = "start_time";

    public static final String END_TIME = "end_time";

    public static final String ORGANIZATION_ID = "organization_id";

    public static final String REPAIR_USER_IDS = "repair_user_ids";

    public static final String INSPECTION_CODE_ID = "inspection_code_id";

    public static final String STATUS = "status";

    public static final String DEL_FLAG = "del_flag";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_BY = "create_by";

    public static final String UPDATE_BY = "update_by";


}
