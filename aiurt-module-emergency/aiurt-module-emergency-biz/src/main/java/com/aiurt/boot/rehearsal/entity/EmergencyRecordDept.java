package com.aiurt.boot.rehearsal.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: emergency_record_dept
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_record_dept")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_record_dept对象", description="emergency_record_dept")
public class EmergencyRecordDept implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**实施记录ID*/
	@Excel(name = "实施记录ID", width = 15)
    @ApiModelProperty(value = "实施记录ID")
    private java.lang.String recordId;
	/**参与部门编码*/
	@Excel(name = "参与部门编码", width = 15)
    @ApiModelProperty(value = "参与部门编码")
    private java.lang.String orgCode;
}
