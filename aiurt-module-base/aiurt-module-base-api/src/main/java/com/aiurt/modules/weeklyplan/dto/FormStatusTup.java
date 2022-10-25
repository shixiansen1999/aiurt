package com.aiurt.modules.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class FormStatusTup {

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "type")
    private String type;

    private FormStatusTup(String description, int status, String type) {
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public static List<FormStatusTup> initWeekly(Integer isAdditional) {
        List<FormStatusTup> result = new ArrayList<>();
        result.add(new FormStatusTup("所有", -1, "formStatus"));
        result.add(new FormStatusTup("已申请", 0, "formStatus"));
        result.add(new FormStatusTup("同意", 1, "formStatus"));
        result.add(new FormStatusTup("驳回", 2, "formStatus"));
        result.add(new FormStatusTup("草稿保存", 3, "formStatus"));
        result.add(new FormStatusTup("线路负责人驳回", 2, "lineFormStatus"));
        result.add(new FormStatusTup("生产调度驳回", 2, "dispatchFormStatus"));
        result.add(new FormStatusTup("已取消", 4, "formStatus"));
        if (isAdditional == 1) {
            result.add(new FormStatusTup("主任驳回", 2, "directorFormStatus"));
            result.add(new FormStatusTup("经理驳回", 2, "managerFormStatus"));
        }
        return result;
    }

}
