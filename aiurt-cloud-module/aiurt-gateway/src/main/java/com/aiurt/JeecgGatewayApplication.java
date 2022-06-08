package com.aiurt;

import com.aiurt.loader.DynamicRouteLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

/**
 * @author jeecg
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.aiurt","org.jeecg"})
public class JeecgGatewayApplication  implements CommandLineRunner {

    @Resource
    private DynamicRouteLoader dynamicRouteLoader;

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(JeecgGatewayApplication.class, args);
        System.out.println("网关启动成功");
    }

    /**
     * 容器初始化后加载路由
     * @param strings
     */
    @Override
    public void run(String... strings) {
        dynamicRouteLoader.refresh(null);
    }
}
