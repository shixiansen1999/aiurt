package com.aiurt.boot.rehearsal.dto;

import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordQuestion;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordStep;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author
 * @date 2022/11/30 11:25
 * @description: 演练登记DTO
 */
@Data
public class EmergencyRehearsalRegisterDTO extends EmergencyImplementationRecord {
    /**
     * 参与部门编码列表
     */
    @ApiModelProperty(value = "参与部门编码列表")
    private List<String> orgCodes;
    /**
     * 登记演练步骤列表
     */
    @ApiModelProperty(value = "登记演练步骤列表")
    private List<EmergencyRecordStep> steps;
    /**
     * 登记问题列表
     */
    @ApiModelProperty(value = "登记问题列表")
    private List<EmergencyRecordQuestion> questions;
}
