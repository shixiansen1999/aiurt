package com.aiurt.modules.train.trainarchive.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.train.traindegreerecord.entity.TrainDegreeRecord;
import com.aiurt.modules.train.trainjobchangerecord.entity.TrainJobChangeRecord;
import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: train_archive
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Data
@TableName("train_archive")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="train_archive对象", description="train_archive")
public class TrainArchiveDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private String id;

	/**用户id*/
    @ApiModelProperty(value = "用户id")
    private String userId;

	/**名字*/
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty("姓名")
    private String realname;

    /**工资编号*/
    @ApiModelProperty(value = "工资编号")
    @Excel(name = "工资编号", width = 15)
    private String salaryCode;

    /**头像*/
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**出生日期*/
    @Excel(name = "出生日期", width = 15)
    @ApiModelProperty("出生日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**出生日期*/
    @ApiModelProperty("出生日期")
    private String excelBirthday;
    /**出生日期*/
    @ApiModelProperty("入企时间")
    private String excelEntryDate;
    /**出生日期*/
    @ApiModelProperty("参加工作时间")
    private String excelWorkTime;

    /**入企时间*/
    @ApiModelProperty(value = "入企时间")
    @Excel(name = "入企时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;
    /**入企时间*/
    @ApiModelProperty(value = "参加工作时间")
    @Excel(name = "参加工作时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workingTime;

    /**部门*/
    @Excel(name = "部门", width = 15)
    @ApiModelProperty(value = "部门")
    private String departName;

    /**性别（1：男 2：女）*/
    @Excel(name = "性别")
    @Dict(dicCode = "sex")
    @ApiModelProperty("性别")
    private Integer sex;
    /**性别（1：男 2：女）*/
    @Excel(name = "性别")
    @Dict(dicCode = "sex")
    @ApiModelProperty("性别")
    private String sexName;
    /**手机号码*/
    @ApiModelProperty(value = "手机号码")
    @Excel(name = "手机号码", width = 15)
    private String phone;

    /**状态：1是正常，2是冻结*/
    @Excel(name = "状态", width = 15)
    @Dict(dicCode = "status")
    @ApiModelProperty(value = "状态：1是正常，2是冻结")
    private Integer status;

    /**工号*/
    @ApiModelProperty("工号")
    private String workNo;

    @ApiModelProperty(value = "部门code")
    private String orgCode;

	/**delFlag*/
    @ApiModelProperty(value = "delFlag")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**岗位变动记录*/
    @ApiModelProperty(value = "岗位变动记录")
    @TableField(exist = false)
    private List<TrainJobChangeRecord> changeRecordList;
    /**培训记录*/
    @ApiModelProperty(value = "培训记录")
    @TableField(exist = false)
    private List<TrainRecord> trainRecordList;
    /**培训记录*/
    @ApiModelProperty(value = "部门集合")
    @TableField(exist = false)
    private List<String> orgCodeList;

    @ApiModelProperty(value = "第一学历")
    private  TrainDegreeRecord firstDegree;
    @ApiModelProperty(value = "最高学历")
    private  TrainDegreeRecord highestDegree;
}
