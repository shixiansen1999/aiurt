package com.aiurt.boot.rehearsal.vo;

import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordDept;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author
 * @date 2022/11/30 15:02
 * @description: 返回给前端渲染的VO对象
 */
@Data
public class EmergencyImplementationRecordVO extends EmergencyImplementationRecord {
    /**
     * 关联计划编码
     */
    @ApiModelProperty(value = "关联计划编码")
    private String planCode;
    /**
     * 演练科目
     */
    @ApiModelProperty(value = "演练科目")
    private String subject;
    /**
     * 依托预案
     */
    @ApiModelProperty(value = "依托预案")
    private String name;
    /**
     * 计划演练日期，格式yyyy-MM
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM")
    @DateTimeFormat(pattern = "yyyy-MM")
    @ApiModelProperty(value = "计划演练日期，格式yyyy-MM")
    private Date planRehearsalTime;
    /**
     * 组织部门编码
     */
    @ApiModelProperty(value = "组织部门编码")
    private String deptCode;
    /**
     * 组织部门编码
     */
    @ApiModelProperty(value = "组织部门编码")
    private String deptName;
//    /**
//     * 参与部门
//     */
//    @ApiModelProperty(value = "计划演练日期，格式yyyy-MM")
//    List<EmergencyRecordDept> deptList;

}
