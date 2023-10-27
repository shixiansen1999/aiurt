package com.aiurt.modules.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
import org.jeecg.common.api.vo.Result;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlowableExceptionHandler {

    private static final String SEQUENCE_FLOW = "No outgoing sequence flow";

    @ExceptionHandler(value = FlowableException.class)
    public Result<String> handleFlowableException(FlowableException e) {
        // 从异常对象中拿到约束违例信息，并返回其中的第一个错误消息
        String message = e.getMessage();
        if (StrUtil.startWith(message, SEQUENCE_FLOW)) {
            return Result.error("无法找到下一步办理节点，请联系管理员！");
        }
        // 从异常对象中拿到约束违例信息，并返回其中的第一个错误消息
        String errorMessage = "未知错误，请联系管理员！";
        log.error(message, e);
        return Result.error(errorMessage);
    }
}
