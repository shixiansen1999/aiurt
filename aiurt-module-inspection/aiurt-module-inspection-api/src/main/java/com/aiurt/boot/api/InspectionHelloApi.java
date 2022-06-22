//package com.aiurt.boot.api;
//import com.aiurt.boot.api.fallback.InspectionHelloFallback;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(value = "jeecg-inspection", fallbackFactory = InspectionHelloFallback.class)
//public interface InspectionHelloApi {
//
//    /**
//     * inspection hello 微服务接口
//     * @param
//     * @return
//     */
//    @GetMapping(value = "/inspection/hello")
//    String callHello();
//}
