package com.aiurt.boot.modules.worklog.dto;

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
public class WorkLogDTO {

	/**主键id,自动递增*/
	private  Long  id;

	/**巡检编号*/
	private  String  patrolCode;

	/**检修编号*/
	private  String  repairCode;

	/**故障编号*/
	private  String  faultCode;

	/**保存状态:0.保存 1.提交 2.确认 3.审阅*/
	private  Integer  status;

	/**提交人id*/
	private  String  submitId;

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

	/**审批人*/
	private  String  approverId;

	/**删除状态:0.未删除 1已删除*/
	private  Integer  delFlag;

	/**创建时间,CURRENT_TIMESTAMP*/
	private  Date  createTime;

	/**修改时间,根据当前时间戳更新*/
	private  Date  updateTime;

	/**创建人*/
	private  String  createBy;

	/**修改人*/
	private  String  updateBy;

	/**附件列表*/
	public List<String> urlList;

    private static final String ID = "id";
    private static final String PATROL_CODE = "patrol_code";
    private static final String REPAIR_CODE = "repair_code";
    private static final String FAULT_CODE = "fault_code";
    private static final String STATUS = "status";
    private static final String SUBMIT_ID = "submit_id";
    private static final String SUBMIT_TIME = "submit_time";
    private static final String WORK_CONTENT = "work_content";
    private static final String CONTENT = "content";
    private static final String URL = "url";
    private static final String SUCCEED_ID = "succeed_id";
    private static final String SUCCEED_TIME = "succeed_time";
    private static final String APPROVER_ID = "approver_id";
    private static final String APPROVAL_TIME = "approval_time";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";


}
