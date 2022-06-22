package com.aiurt.modules.fault.api;

import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(value = "jeecg-erp", fallbackFactory = ErpHelloFallback.class)
public interface ErpHelloApi {

    /**
     * erp hello 微服务接口
     * @param
     * @return
     */
    @GetMapping(value = "/erp/hello")
    String callHello();
}
