package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.plan.dto
 * @className: PatrolPlanDto
 * @author: life-0
 * @date: 2022/6/23 17:24
 * @description: TODO
 * @version: 1.0
 */
@Data
public class PatrolPlanDto  extends PatrolPlan {
   @ApiModelProperty(value = "选择的巡检标准集合")
   @TableField(exist = false)
   List<PatrolStandardDto> patrolStandards;
   @ApiModelProperty(value = "选择的设备集合")
   @TableField(exist = false)
   List<Device> devices;
   /**巡检类型：1周巡、2月巡*/
   @Excel(name = "巡检类型：1周巡、2月巡", width = 15)
   @ApiModelProperty(value = "巡检类型：1周巡、2月巡")
   @TableField(exist = false)
   private Integer strategyType;
   /**巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日*/
   @Excel(name = "巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日", width = 15)
   @ApiModelProperty(value = "巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日")
   @TableField(exist = false)
   List<String> week;
   /**巡检周次：1第一周、2第二周、3第三周、4第四周*/
   @Excel(name = "巡检周次：1第一周、2第二周、3第三周、4第四周", width = 15)
   @ApiModelProperty(value = "巡检周次：1第一周、2第二周、3第三周、4第四周")
   @TableField(exist = false)
   List<String> time;
   /**巡检周次：开始时间*/
   @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
   @DateTimeFormat(pattern="HH:mm")
   @ApiModelProperty(value = "开始时间")
   @TableField(exist = false)
   private Date strategyStartTime;
   /**巡检周次：结束时间*/
   @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
   @DateTimeFormat(pattern="HH:mm")
   @ApiModelProperty(value = "结束时间")
   @TableField(exist = false)
   private Date strategyEndTime;
   /**专业code*/
   @Excel(name = "专业code", width = 15)
   @ApiModelProperty(value = "专业code")
   private java.lang.String professionCode;
   /**适用系统code*/
   @Excel(name = "适用系统code", width = 15)
   @ApiModelProperty(value = "适用系统code")
   private java.lang.String subsystemCode;
   @Excel(name = "适用系统名称", width = 15)
   @ApiModelProperty(value = "适用系统名称")
   @TableField(exist = false)
   private java.lang.String subsystemName;
   @Excel(name = "专业名称", width = 15)
   @ApiModelProperty(value = "专业名称")
   @TableField(exist = false)
   private java.lang.String professionName;
   @Excel(name = "站点名称", width = 15)
   @ApiModelProperty(value = "站点名称")
   @TableField(exist = false)
   private java.lang.String siteName;
   @Excel(name = "组织名称", width = 15)
   @ApiModelProperty(value = "组织名称")
   @TableField(exist = false)
   private java.lang.String mechanismName;
   @ApiModelProperty(value = "站点Code")
   @TableField(exist = false)
   private String siteCode;
   @Excel(name = "组织名称", width = 15)
   @ApiModelProperty(value = "组织code")
   @TableField(exist = false)
   private String mechanismCode;
   @Excel(name = "多选站点集合", width = 15)
   @ApiModelProperty(value = "多选站点集合")
   @TableField(exist = false)
   List<String> siteCodes;
   @Excel(name = "多选组织集合", width = 15)
   @ApiModelProperty(value = "多选组织集合")
   @TableField(exist = false)
   List<String> mechanismCodes;
   @Excel(name = "标准表Ids", width = 15)
   @ApiModelProperty(value = "标准表Ids")
   @TableField(exist = false)
   private String ids;
   private String ws;
   private String ts;
}
