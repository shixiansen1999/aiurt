//package com.aiurt.modules.listener;
//
//import org.flowable.engine.delegate.DelegateExecution;
//import org.flowable.engine.delegate.ExecutionListener;
//
///**
// * @author:wgp
// * @create: 2023-08-15 09:29
// * @Description:
// */
//public class NodeActionListener implements ExecutionListener {
//    /**
//     * 节点前
//     */
//    final static String EVENTNAME_START = "start";
//    /**
//     * 节点后
//     */
//    final static String EVENTNAME_END = "end";
//    /**
//     * 从流程实例的变量中获取状态更新标志
//     */
//    final String STATE_UPDATE = "stateUpdate";
//    /**
//     * 从流程实例的变量中获取自定义接口信息
//     */
//    final String CUSTOM_INTERFACE = "customInterface";
//    /**
//     * 从流程实例的变量中获取自定义 SQL 语句
//     */
//    final String CUSTOM_SQL = "customSql";
//
//    @Override
//    public void notify(DelegateExecution execution) {
//        String eventName = execution.getEventName();
//        boolean shouldExecute = (boolean) execution.getVariable("shouldExecute");
//
//        // 获取配置的属性值
//        String stateUpdate = (String) execution.getVariable("stateUpdate");
//        String customInterface = (String) execution.getVariable("customInterface");
//        String customSql = (String) execution.getVariable("customSql");
//
//        if (EVENTNAME_END.equals(eventName) && shouldExecute) {
//            // 节点开始前的操作逻辑
//            List<ExtensionElement> preNodeActionElements = userTask.getExtensionElements().get("preNodeAction");
//            // ...
//        } else if ("end".equals(eventName) && shouldExecute) {
//            // 节点结束后的操作逻辑
//            // ...
//        }
//    }
//}
