package com.aiurt.boot.modules.system.controller;

import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/27 15:21
 */
@RestController
public class FilterErrorController {

    @RequestMapping("/filterError/{code}/{message}")
    public Result error(@PathVariable("code")Integer code, @PathVariable("message")String message){
        return Result.error(code,message);
    }
}

