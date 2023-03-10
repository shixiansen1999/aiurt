package com.aiurt.boot.drools.util;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

public class DroolsUtil {
    private static final KieServices kieServices = KieServices.Factory.get();
    private static final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

    private static KieContainer kieContainer;
    private static KieSession kieSession;  // 主要使用的这个kieSession

    private static final String RULES_PATH = "rules/";  // 规则文件目录
    private static final String MYSQL_RULE_PATH = "src/main/resources/rules/rules.drl"; // MySQL加载的规则的虚拟文件

    static {
        System.setProperty("drools.dateformat", "yyyy-MM-dd");
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] files = new Resource[0];
        try {
            files = resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "*.*");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = null;
        for (Resource file : files) {
            path = RULES_PATH + file.getFilename();
            kieFileSystem.write(ResourceFactory.newClassPathResource(path, "UTF-8"));
        }

        KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        kieSession = kieContainer.newKieSession();
    }

    public static KieContainer getKieContainer() {
        return kieContainer;
    }

    public static void setKieContainer(KieContainer kieContainer) {
        DroolsUtil.kieContainer = kieContainer;
        kieSession = kieContainer.newKieSession();
    }

    public static KieSession getKieSession() {
        return kieSession;
    }

    public static void setKieSession(KieSession kieSession) {
        DroolsUtil.kieSession = kieSession;
    }

    public static KieSession reload(String rule) throws Exception {
        if (rule == null){
            throw new Exception("规则不能为空");
        }
        kieFileSystem.write(MYSQL_RULE_PATH, rule.getBytes());
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("加载规则失败！！！");
        }
        setKieContainer(kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId()));
        return kieSession;
    }

}
