package com.aiurt.modules.fault.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 故障参与人
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Data
@TableName("fault_repair_participants")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_repair_participants对象", description="故障参与人")
public class FaultRepairParticipants implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**故障报修编码*/
	@Excel(name = "故障报修编码", width = 15)
    @ApiModelProperty(value = "故障报修编码")
    private String faultCode;
	/**用户账号*/
	@Excel(name = "用户账号", width = 15)
    @ApiModelProperty(value = "用户账号")
    private String userName;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**人员类型*/
	@Excel(name = "人员类型", width = 15)
    @ApiModelProperty(value = "人员类型")
    private Integer userType;
	/**维修记录id*/
	@Excel(name = "维修记录id", width = 15)
    @ApiModelProperty(value = "维修记录id")
    private String faultRepairRecordId;
}
