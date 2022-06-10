package com.aiurt.boot.modules.fault.dto;

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
import java.util.List;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("fault_repair_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault_repair_record对象", description="故障维修记录表")
public class FaultRepairRecordDTO {

	/**主键id，自动递增*/
	private Long id;
	/**故障编号，示例：G101.2109.001*/
	private String faultCode;
	/**指派人*/
	private String appointUserId;
	/**作业类型*/
	private String workType;
	/**计划令编码*/
	private String planOrderCode;
	/**计划令图片*/
	private String planOrderImg;
	/**参与人id集合*/
	private String participateIds;
	/**委外人员人id集合*/
	private String outsourcingIds;
	/**故障现象*/
	private String faultPhenomenon;
	/**故障分析*/
	private String faultAnalysis;
	/**维修措施*/
	private String maintenanceMeasures;
	/**问题解决状态：0-未解决 1-已解决*/
	private Integer solveStatus;
	/**状态：0-未提交 1-已提交*/
	private Integer status;
	/**删除状态：0.未删除 1已删除*/
	private Integer delFlag;
	/**创建人*/
	private String createBy;
	/**修改人*/
	private String updateBy;
	/**维修结束时间*/
	private Date overTime;
	/**创建时间，CURRENT_TIMESTAMP*/
	private Date createTime;
	/**修改时间，根据当前时间戳更新*/
	private Date updateTime;
	/**附件列表*/
	List<String> urlList;
	/**原备件编号*/
	private String oldSparePartCode;
	/**原备件数量*/
	private Integer oldSparePartNum;
	/**新备件编号*/
	private String newSparePartCode;
	/**新备件数量*/
	private Integer newSparePartNum;
	/**签名*/
	private String signature;
}
