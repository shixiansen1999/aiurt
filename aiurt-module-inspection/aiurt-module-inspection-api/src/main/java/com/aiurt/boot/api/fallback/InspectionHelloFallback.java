//package com.aiurt.boot.api.fallback;
//
//import org.springframework.cloud.openfeign.FallbackFactory;
//import com.aiurt.boot.api.InspectionHelloApi;
//import lombok.Setter;
//import org.springframework.stereotype.Component;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @author JeecgBoot
// */
//@Slf4j
//@Component
//public class InspectionHelloFallback implements FallbackFactory<InspectionHelloApi> {
//    @Setter
//    private Throwable cause;
//
//    @Override
//    public InspectionHelloApi create(Throwable throwable) {
//        log.error("微服务接口调用失败： {}", cause);
//        return null;
//    }
//
//}
