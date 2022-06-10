package com.aiurt.boot.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/20
 */

@Data
public class LogSubmitCount {
    @ApiModelProperty(value = "工作日志提交数量")
    private Integer submitNum;
}
