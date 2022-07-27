package com.aiurt.boot.pool;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.service.*;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.service.IPatrolStandardItemsService;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.service.*;
import com.aiurt.boot.utils.PatrolCodeUtil;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskPool implements Job {

    @Autowired
    private IPatrolPlanService patrolPlanService;
    @Autowired
    private IPatrolPlanStandardService patrolPlanStandardService;
    @Autowired
    private IPatrolPlanOrganizationService patrolPlanOrganizationService;
    @Autowired
    private IPatrolTaskOrganizationService patrolTaskOrganizationService;
    @Autowired
    private IPatrolPlanStationService patrolPlanStationService;
    @Autowired
    private IPatrolTaskStationService patrolTaskStationService;
    @Autowired
    private IPatrolTaskService patrolTaskService;
    @Autowired
    private IPatrolPlanStrategyService patrolPlanStrategyService;
    @Autowired
    private IPatrolTaskStandardService patrolTaskStandardService;
    @Autowired
    private IPatrolTaskDeviceService patrolTaskDeviceService;
    @Autowired
    private IPatrolStandardService patrolStandardService;
    @Autowired
    private IPatrolPlanDeviceService patrolPlanDeviceService;
    @Autowired
    private IPatrolStandardItemsService patrolStandardItemsService;
    @Autowired
    private IPatrolCheckResultService patrolCheckResultService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("******正在生成巡检任务记录...******");
        generateTaskData();
        log.info("******巡检任务记录生成完成！*******");
    }

    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        log.info("******正在生成巡检任务记录...******");
        generateTaskData();
        log.info("******巡检任务记录生成完成！*******");
    }


    /**
     * 每天生成巡检任务池数据
     */
    private void generateTaskData() {

        // 获取计划启用的并且在有效期内的计划列表
        List<PatrolPlan> planList = Optional.ofNullable(patrolPlanService.list())
                .orElseGet(Collections::emptyList).stream()
                // 启用状态的计划
                .filter(l -> PatrolConstant.PLAN_STATUS_ENABLE.equals(l.getStatus()))
                // 未删除的计划
                .filter(l -> 0 == l.getDelFlag())
                // 在有效范围内的计划
                .filter(l -> {
                    if (ObjectUtil.isEmpty(l.getStartDate()) || ObjectUtil.isEmpty(l.getEndDate())) {
                        return false;
                    }
                    Date nowDate = DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    Date startDate = DateUtil.parse(DateUtil.format(l.getStartDate(), "yyyy-MM-dd 00:00:00"));
                    Date endDate = DateUtil.parse(DateUtil.format(l.getEndDate(), "yyyy-MM-dd 23:59:59"));
                    return DateUtil.isIn(nowDate, startDate, endDate);

                })
                .collect(Collectors.toList());

        // 根据计划列表生成任务
        planList.parallelStream().forEach(l -> {
            PatrolTask task = new PatrolTask();
            String taskCode = PatrolCodeUtil.getTaskCode();    // 任务编号
            task.setCode(taskCode);
            task.setPlanCode(l.getCode());  // 计划编号
            task.setName(l.getName() + "任务");   // 任务名称
            task.setType(l.getType());  // 作业类型
            task.setOutsource(l.getOutsource());    // 是否委外
            task.setPeriod(l.getPeriod());  // 巡检频次
            task.setAuditor(l.getConfirm());    // 是否需要审核
            task.setStatus(PatrolConstant.TASK_INIT);   // 任务状态
            task.setAbnormalState(PatrolConstant.TASK_UNABNORMAL);  // 任务异常状态
            task.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);   // 任务作废状态
            task.setDisposeStatus(PatrolConstant.TASK_UNDISPOSE);   // 任务处置状态
            task.setRebuild(PatrolConstant.TASK_UNREBUILD);   // 任务重新生成状态

            // 获取计划的巡检策略
            List<PatrolPlanStrategy> strategyList = patrolPlanStrategyService.lambdaQuery()
                    .eq(PatrolPlanStrategy::getPlanId, l.getId()).list();
            Optional.ofNullable(strategyList).orElseGet(Collections::emptyList).stream()
                    .forEach(strategy -> {

                        DateTime date = DateUtil.date();
                        task.setPatrolDate(date);    // 巡检日期
                        task.setStartTime(strategy.getStartTime()); // 巡检开始时间
                        task.setEndTime(strategy.getEndTime()); // 巡检结束时间

                        if (PatrolConstant.STRATEGY_DAY.equals(strategy.getType())) {
                            // 保存任务记录并复制相关关联表数据
                            saveAndCopyData(task, l);
                        } else if (PatrolConstant.STRATEGY_WEEK.equals(strategy.getType())) {
                            // 判断今天是一周中的星期几
                            int week = DateUtil.dayOfWeek(date) == 1 ? 7 : DateUtil.dayOfWeek(date) - 1;
                            if (week == strategy.getWeek()) {
                                // 保存任务记录并复制相关关联表数据
                                saveAndCopyData(task, l);
                            }
                        } else if (PatrolConstant.STRATEGY_MONTH.equals(strategy.getType())) {
                            // 一个月中的第几周
                            int weekOfMonth = date.weekOfMonth();
                            // 一周中的星期几，工具类中以星期日为一周的开始，现以星期一为一周的开始
                            int week = DateUtil.dayOfWeek(date) == 1 ? 7 : DateUtil.dayOfWeek(date) - 1;
                            // 现以星期一为一周的开始
                            if (week == 7) {
                                weekOfMonth -= 1;
                            }
                            if (weekOfMonth == strategy.getTime() && week == strategy.getWeek()) {
                                // 保存任务记录并复制相关关联表数据
                                saveAndCopyData(task, l);
                            }
                        }
                    });
        });
    }

    /**
     * 保存任务记录并复制相关关联表数据
     *
     * @param task
     * @param plan
     */
    private void saveAndCopyData(PatrolTask task, PatrolPlan plan) {
        // 添加一条巡检任务
        patrolTaskService.save(task);

        // 将复制巡检计划的组织机构和站点关联信息
        log.info("正在复制组织机构、站所任务的关联信息...");
        copyOrgAndStationInfo(task, plan);
        log.info("组织机构、站所任务的关联信息复制完毕！");

//        // 添加任务与标准关联表信息
//        log.info("添加任务与标准关联表信息...");
//        saveRelationData(task, plan);
//        log.info("任务与标准关联表信息添加完毕！");

        // 生成巡检单，即巡检任务与标准以及设备关联表数据
        log.info("添加任务与标准以及设备的关联表信息...");
        copyPatrolBill(task, plan);
        log.info("任务与标准以及设备的关联表信息添加完毕！...");
    }

    /**
     * 生成巡检单
     *
     * @param task
     * @param plan
     */
    private void copyPatrolBill(PatrolTask task, PatrolPlan plan) {

        //根据计划ID获取计划标准表数据
        QueryWrapper<PatrolPlanStandard> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolPlanStandard::getPlanId, plan.getId());
        List<PatrolPlanStandard> planStandardList = Optional.ofNullable(patrolPlanStandardService.list(wrapper))
                .orElseGet(Collections::emptyList).stream().collect(Collectors.toList());


        // 遍历计划中的每一个标准生成工单数据
        planStandardList.stream().forEach(l -> {
            // 根据巡检标准编号获取巡检标准记录
            QueryWrapper<PatrolStandard> standardWrapper = new QueryWrapper<>();
            standardWrapper.lambda().eq(PatrolStandard::getCode, l.getStandardCode());
            PatrolStandard standard = patrolStandardService.getOne(standardWrapper);

            // 保存巡检任务标准关联数据，并获取对应的任务标准关联表ID
            String taskStandardId = saveTaskStandardData(task, l, standard);

            Integer deviceType = standard.getDeviceType();

            if (PatrolConstant.DEVICE_INDEPENDENCE.equals(deviceType)) {
                // 与设备无关
                // 根据计划ID获取计划
                PatrolPlan patrolPlan = patrolPlanService.getById(l.getPlanId());
                QueryWrapper<PatrolPlanStation> planWrapper = new QueryWrapper<>();
                planWrapper.lambda().eq(PatrolPlanStation::getPlanCode, patrolPlan.getCode());
                List<PatrolPlanStation> list = Optional.ofNullable(patrolPlanStationService.list(planWrapper)).orElseGet(Collections::emptyList);

                // 根据选择的站点和标准生成巡检单数据
                list.stream().forEach(station -> {

                    // 生成巡检单数据
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    // 任务表ID
                    patrolTaskDevice.setTaskId(task.getId());
                    //检查状态
                    patrolTaskDevice.setStatus(0);
                    // 任务标准表ID
                    patrolTaskDevice.setTaskStandardId(taskStandardId);
                    // 巡检单号生成
                    String patrolNumber = PatrolCodeUtil.getBillCode();
                    patrolTaskDevice.setPatrolNumber(patrolNumber);
                    // 线路编号
                    patrolTaskDevice.setLineCode(station.getLineCode());
                    // 站点编号
                    patrolTaskDevice.setStationCode(station.getStationCode());
                    if (ObjectUtil.isEmpty(station.getLineCode())) {
                        // 根据站点编号获取线路编号
                        String lineCode = patrolTaskService.getLineCode(station.getStationCode());
                        patrolTaskDevice.setLineCode(lineCode);
                    }
                    // 位置编号
                    patrolTaskDevice.setPositionCode(station.getPositionCode());
                    // 保存巡检单信息
                    patrolTaskDeviceService.save(patrolTaskDevice);

                    String taskDeviceId = patrolTaskDevice.getId();
                    // 生成巡检单的检查项内容
                    List<PatrolCheckResult> resultList = copyCheckItems(taskStandardId, taskDeviceId);
                    patrolCheckResultService.saveBatch(resultList);

                });
            } else {
                // 与设备相关，根据设备和标准生成巡检单数据
                // 根据计划ID获取计划设备关联表记录
                QueryWrapper<PatrolPlanDevice> planDeviceWrapper = new QueryWrapper<>();
                planDeviceWrapper.lambda().eq(PatrolPlanDevice::getPlanId, plan.getId());
                List<PatrolPlanDevice> planDeviceList = patrolPlanDeviceService.list(planDeviceWrapper);
                // 遍历设备列表信息
                Optional.ofNullable(planDeviceList).orElseGet(Collections::emptyList)
                        .stream().forEach(
                                // ps 表示巡检计划标准对象
                                ps -> {

                                    // 生成巡检单数据
                                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                                    // 任务表ID
                                    patrolTaskDevice.setTaskId(task.getId());
                                    // 任务标准表ID
                                    patrolTaskDevice.setTaskStandardId(taskStandardId);
                                    //检查状态
                                    patrolTaskDevice.setStatus(0);
                                    // 巡检单号生成
                                    String patrolNumber = PatrolCodeUtil.getBillCode();
                                    patrolTaskDevice.setPatrolNumber(patrolNumber);
                                    // 设备编号
                                    patrolTaskDevice.setDeviceCode(ps.getDeviceCode());
                                    // 根据设备编号获取设备的线路、站点，位置等信息
                                    QueryWrapper<Device> deviceWrapper = new QueryWrapper<>();
                                    deviceWrapper.lambda().eq(Device::getCode, ps.getDeviceCode());
                                    if (ObjectUtil.isNotEmpty(ps.getDeviceCode())) {
                                        // 根据设备编号获取设备信息
                                        Device device = patrolTaskDeviceService.getDeviceInfoByCode(ps.getDeviceCode());
                                        // 线路编号
                                        patrolTaskDevice.setLineCode(device.getLineCode());
                                        // 站点编号
                                        patrolTaskDevice.setStationCode(device.getStationCode());
                                        // 位置编号
                                        patrolTaskDevice.setPositionCode(device.getPositionCode());
                                    }
                                    // 保存巡检单信息
                                    patrolTaskDeviceService.save(patrolTaskDevice);

                                    String taskDeviceId = patrolTaskDevice.getId();
                                    // 生成巡检单的检查项内容
                                    List<PatrolCheckResult> resultList = copyCheckItems(taskStandardId, taskDeviceId);
                                    patrolCheckResultService.saveBatch(resultList);
                                });

            }
        });
    }

    /**
     * 生成巡检单的检查项内容
     *
     * @param taskStandardId
     * @param taskDeviceId
     * @return
     */
    private List<PatrolCheckResult> copyCheckItems(String taskStandardId, String taskDeviceId) {

        //根据任务设备的任务ID获取任务标准表主键和巡检标准表ID
        PatrolTaskStandard taskStandard = patrolTaskStandardService.getById(taskStandardId);
        String standardId = taskStandard.getStandardId();

        //根据巡检标准表ID获取巡检标准项目列表并添加到结果表中
        List<PatrolStandardItems> patrolStandardItems = patrolStandardItemsService.lambdaQuery()
                .eq(PatrolStandardItems::getStandardId, standardId).list();

        List<PatrolCheckResult> addResultList = new ArrayList<>();
        Optional.ofNullable(patrolStandardItems).orElseGet(Collections::emptyList).stream().forEach(l -> {
            PatrolCheckResult result = new PatrolCheckResult();
            result.setTaskStandardId(taskStandard.getId());   // 任务标准关联表ID
            result.setTaskDeviceId(taskDeviceId); // 任务设备关联表ID
            result.setCode(l.getCode());    // 巡检项编号
            result.setContent(l.getContent());  // 巡检项内容
            result.setQualityStandard(l.getQualityStandard());  // 质量标准
            result.setHierarchyType(l.getHierarchyType());  // 层级类型
            result.setOldId(l.getId()); // 原标准项目表ID
            result.setParentId(l.getParentId()); //父级ID
            result.setOrder(l.getOrder());  // 内容排序
            result.setCheck(l.getCheck());  // 是否为巡检项目
            result.setInputType(l.getInputType());  // 填写数据类型
            result.setDictCode(l.getDictCode());    // 关联的数据字典
            result.setRegular(l.getRegular());  // 数据校验表达式
            result.setDelFlag(0);  // 数据校验表达式
            addResultList.add(result);
        });
        return addResultList;
    }

    /**
     * 保存巡检任务标准关联数据
     *
     * @param task
     * @param planStandard
     * @param standard
     */
    private String saveTaskStandardData(PatrolTask task, PatrolPlanStandard planStandard, PatrolStandard standard) {
        // 生成任务标准关联表数据
        PatrolTaskStandard patrolTaskStandard = new PatrolTaskStandard();
        // 任务表ID
        patrolTaskStandard.setTaskId(task.getId());
        // 标准表ID
        patrolTaskStandard.setStandardId(standard.getId());
        // 标准编号
        patrolTaskStandard.setStandardCode(planStandard.getStandardCode());
        // 专业编号
        patrolTaskStandard.setProfessionCode(planStandard.getProfessionCode());
        // 子系统编号
        patrolTaskStandard.setSubsystemCode(planStandard.getSubsystemCode());
        // 设备类型编号
        patrolTaskStandard.setDeviceTypeCode(planStandard.getDeviceTypeCode());
        // 保存任务标准关联表数据
        patrolTaskStandardService.save(patrolTaskStandard);
        return patrolTaskStandard.getId();
    }

    /**
     * 复制关联的组织机构和站点信息
     *
     * @param task
     * @param plan
     */
    private void copyOrgAndStationInfo(PatrolTask task, PatrolPlan plan) {

        // 根据计划编码获取组织机构列表
        List<PatrolPlanOrganization> planOrgList = patrolPlanOrganizationService.lambdaQuery()
                .eq(PatrolPlanOrganization::getPlanCode, plan.getCode()).list();
        String taskCode = task.getCode();
        List<PatrolTaskOrganization> orgList = new ArrayList<>();
        Optional.ofNullable(planOrgList).orElseGet(Collections::emptyList).forEach(l -> {
            PatrolTaskOrganization organization = new PatrolTaskOrganization();
            organization.setTaskCode(taskCode);
            organization.setOrgCode(l.getOrganizationCode());
            orgList.add(organization);
        });
        patrolTaskOrganizationService.saveBatch(orgList);

        // 根据计划编码获取组织机构列表
        List<PatrolPlanStation> planStationList = patrolPlanStationService.lambdaQuery()
                .eq(PatrolPlanStation::getPlanCode, plan.getCode()).list();
        List<PatrolTaskStation> stationList = new ArrayList<>();
        Optional.ofNullable(planStationList).orElseGet(Collections::emptyList).forEach(l -> {
            PatrolTaskStation station = new PatrolTaskStation();
            station.setTaskCode(taskCode);
            station.setStationCode(l.getStationCode());
            stationList.add(station);
        });
        patrolTaskStationService.saveBatch(stationList);
    }

    /**
     * 保存与任务相关联的标准和巡检设备信息
     */
    private void saveRelationData(PatrolTask task, PatrolPlan plan) {
        // 将巡检计划标准关联表数据获取一份到巡检任务标准关联表中
        String planId = plan.getId();
        String taskId = task.getId();
        List<PatrolPlanStandard> planStandardList = patrolPlanStandardService.lambdaQuery()
                .eq(PatrolPlanStandard::getPlanId, planId).list();
        List<PatrolTaskStandard> taskStandardList = new ArrayList<>();
        planStandardList.stream().forEach(l -> {
            PatrolTaskStandard taskStandard = new PatrolTaskStandard();
            taskStandard.setTaskId(taskId);
            PatrolStandard standard = patrolStandardService.lambdaQuery()
                    .eq(PatrolStandard::getCode, l.getStandardCode()).one();
            taskStandard.setStandardId(standard.getId());
            taskStandard.setStandardCode(l.getStandardCode());
            taskStandard.setProfessionCode(l.getProfessionCode());
            taskStandard.setSubsystemCode(l.getSubsystemCode());
            taskStandard.setDeviceTypeCode(l.getDeviceTypeCode());
            taskStandardList.add(taskStandard);
        });
        // 保存任务标准关联表数据
        patrolTaskStandardService.saveBatch(taskStandardList);
    }
}
