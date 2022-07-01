//package com.aiurt.boot.strategy.service;
//
//import com.aiurt.boot.constant.InspectionConstant;
//import com.aiurt.boot.standard.dto.InspectionCodeDTO;
//import com.aiurt.boot.strategy.service.impl.StrategyService;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Function;
//
///**
// * @author wgp
// * @Title:
// * @Description: 生成年计划策略注册类
// * @date 2022/6/3011:54
// */
//@Component
//public abstract class StrategyRegister {
//    @Resource
//    private StrategyService strategyService;
//    /**
//     * 定义策略map缓存
//     */
//    private static final Map<Integer, Function<InspectionCodeDTO, String>> shareStrategies = new ConcurrentHashMap<>(16);
//
//    @PostConstruct
//    public void init() {
//        shareStrategies.put(InspectionConstant.WEEK, e -> strategyService.weekPlan(e));
//        shareStrategies.put(InspectionConstant.MONTH, e -> strategyService.monthPlan(e));
//        shareStrategies.put(InspectionConstant.DOUBLEMONTH, e -> strategyService.doubleMonthPlan(e));
//        shareStrategies.put(InspectionConstant.QUARTER, e -> strategyService.quarterPlan(e));
//        shareStrategies.put(InspectionConstant.SEMIANNUAL, e -> strategyService.semiAnnualPlan(e));
//        shareStrategies.put(InspectionConstant.ANNUAL, e -> strategyService.annualPlan(e));
//    }
//
//    /**
//     * 周检
//     *
//     * @param param
//     * @return
//     */
//    public abstract String weekPlan(InspectionCodeDTO param);
//
//    /**
//     * 月检
//     *
//     * @param param
//     * @return
//     */
//    public abstract String monthPlan(InspectionCodeDTO param);
//
//    /**
//     * 双月检
//     *
//     * @param param
//     * @return
//     */
//    public abstract String doubleMonthPlan(InspectionCodeDTO param);
//
//    /**
//     * 季检
//     *
//     * @param param
//     * @return
//     */
//    public abstract String quarterPlan(InspectionCodeDTO param);
//
//    /**
//     * 半年检
//     *
//     * @param param
//     * @return
//     */
//    public abstract String semiAnnualPlan(InspectionCodeDTO param);
//
//    /**
//     * 年检
//     *
//     * @param param
//     * @return
//     */
//    public abstract String annualPlan(InspectionCodeDTO param);
//
//    /**
//     * 获取指定策略
//     */
//    public Function<InspectionCodeDTO, String> getStrategy(Integer type) {
//        if (type == null) {
//            throw new IllegalArgumentException("type should not be empty.");
//        }
//        Function<InspectionCodeDTO, String> ins = shareStrategies.get(type);
//        if (ins == null) {
//            throw new IllegalArgumentException("strateg Method should not be empty.");
//        }
//        return ins;
//    }
//}
//
