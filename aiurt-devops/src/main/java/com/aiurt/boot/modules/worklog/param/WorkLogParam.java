package com.aiurt.boot.modules.worklog.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author: swsc
 * 工作日志查询参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class WorkLogParam {

    /**
     * 开始时间
     */
    private String dayStart;

    /**
     * 结束时间
     */
    private String dayEnd;

    /**
     * 审核状态 其他-未审核 3-已审核
     */
    private String checkStatus;

    /**
     * 确认状态:0-未确认 1-已确认
     */
    private  Integer  confirmStatus;

    /**
     * 提交人id
     */
    private  String  submitId;

    /**
     * 提交人班组id
     */
    private  String  departId;

    @ApiModelProperty(value = "权限班组集合(后台处理)")
    private List<String> departList;
}
