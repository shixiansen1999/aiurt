//package org.jeecg.modules.erp.api.fallback;
//
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.jeecg.modules.erp.api.ErpHelloApi;
//import org.springframework.cloud.openfeign.FallbackFactory;
//import org.springframework.stereotype.Component;
//
///**
// * @author JeecgBoot
// */
//@Slf4j
//@Component
//public class ErpHelloFallback implements FallbackFactory<ErpHelloApi> {
//    @Setter
//    private Throwable cause;
//
//    @Override
//    public ErpHelloApi create(Throwable throwable) {
//        log.error("微服务接口调用失败： {}", cause);
//        return null;
//    }
//
//}
