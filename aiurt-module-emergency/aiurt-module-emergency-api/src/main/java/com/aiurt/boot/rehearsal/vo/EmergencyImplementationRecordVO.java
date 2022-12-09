package com.aiurt.boot.rehearsal.vo;

import com.aiurt.boot.rehearsal.dto.EmergencyDeptDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordDept;
import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value="返回给前端渲染的VO对象", description="返回给前端渲染的VO对象")
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
     * 依托预案名称
     */
    @ApiModelProperty(value = "依托预案名称")
    private String schemeName;
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
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText = "depart_name")
    private String deptCode;
    /**
     * 参与部门信息
     */
    @ApiModelProperty(value = "参与部门信息")
    private List<EmergencyDeptDTO> deptList;
    /**
     * 参与部门信息名称
     */
    @ApiModelProperty(value = "参与部门信息名称")
    private String deptNames;

}
