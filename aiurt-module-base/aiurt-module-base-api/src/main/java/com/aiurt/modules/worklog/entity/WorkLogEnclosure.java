package com.aiurt.modules.worklog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Data
@TableName("work_log_enclosure")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="work_log_enclosure对象", description="日志附件")
public class WorkLogEnclosure {

    /**主键id,自动递增*/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id,自动递增")
    private  Long  id;

    /**类型 0-附件 1-签名*/
    @Excel(name = "类型 0-附件 1-签名", width = 15)
    @ApiModelProperty(value = "类型 0-附件 1-签名")
    private  Integer  type;

    /**父类库id*/
    @Excel(name = "父类库id", width = 15)
    @ApiModelProperty(value = "父类库id")
    private  String  parentId;

    /**url地址*/
    @Excel(name = "url地址", width = 15)
    @ApiModelProperty(value = "url地址")
    private  String  url;

    /**删除状态:0.未删除 1已删除*/
    @Excel(name = "删除状态:0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态:0.未删除 1已删除")
    private  Integer  delFlag;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private  String  createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private  String  updateBy;

    /**创建时间,CURRENT_TIMESTAMP*/
    @Excel(name = "创建时间,CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间,CURRENT_TIMESTAMP")
    private Date createTime;

    /**修改时间,根据当前时间戳更新*/
    @Excel(name = "修改时间,根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间,根据当前时间戳更新")
    private  Date  updateTime;


    private static final String ID = "id";
    private static final String PARENT_ID = "parent_id";
    private static final String URL = "url";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}

