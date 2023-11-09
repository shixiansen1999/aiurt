package com.aiurt.boot.pool;

import cn.hutool.core.collection.CollUtil;
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
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysParamAPI;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 */
@Slf4j
@Component
public class TaskPoolQuarter implements Job {

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
    @Autowired
    private IPatrolDeviceService patrolDeviceService;
    @Autowired
    private ISysParamAPI iSysParamAPI;

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
     * 每三个月开始月第一天零点生成巡检任务池数据
     */
    public void generateTaskData() {
        // 获取计划启用的并且在有效期内的计划列表
        List<PatrolPlan> planList = patrolPlanService.lambdaQuery()
                // 未删除的计划
                .eq(PatrolPlan::getDelFlag, CommonConstant.DEL_FLAG_0)
                // 启用状态的计划
                .eq(PatrolPlan::getStatus, PatrolConstant.PLAN_STATUS_ENABLE)
                .eq(PatrolPlan::getPeriod,PatrolConstant.PLAN_PERIOD_THREE_MONTH)
                .list()
                .stream()
                // 在有效范围内的计划
                .filter(l -> {
                    if (ObjectUtil.isEmpty(l.getStartDate()) || ObjectUtil.isEmpty(l.getEndDate())) {
                        return false;
                    }
                    Date nowDate = DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    Date startDate = DateUtil.parse(DateUtil.format(l.getStartDate(), "yyyy-MM-dd 00:00:00"));
                    Date endDate = DateUtil.parse(DateUtil.format(l.getEndDate(), "yyyy-MM-dd 23:59:59"));
                    return DateUtil.isIn(nowDate, startDate, endDate);
                }).collect(Collectors.toList());

        for (PatrolPlan plan : planList) {
            // 创建回滚点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            try {
                // 根据计划列表生成任务
                this.createTaskByPlan(plan);
            } catch (Exception e) {
                // 回滚当前事务,不影响之前的保存和后续的循环
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                String message = "计划生成任务失败！计划ID为：【{}】,编号为【{}】";
                log.error(message, plan.getId(), plan.getCode(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createTaskByPlan(PatrolPlan plan) {
        PatrolTask task = new PatrolTask();
        String taskCode = PatrolCodeUtil.getTaskCode();    // 任务编号
        task.setCode(taskCode);
        task.setPlanCode(plan.getCode());  // 计划编号
        task.setName(plan.getName() + "任务");   // 任务名称
        task.setType(plan.getType());  // 作业类型
        task.setOutsource(plan.getOutsource());    // 是否委外
        task.setPeriod(plan.getPeriod());  // 巡检频次
        task.setAuditor(plan.getConfirm());    // 是否需要审核
        task.setStatus(PatrolConstant.TASK_INIT);   // 任务状态
        task.setAbnormalState(PatrolConstant.TASK_UNABNORMAL);  // 任务异常状态
        task.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);   // 任务作废状态
        task.setDisposeStatus(PatrolConstant.TASK_UNDISPOSE);   // 任务处置状态
        task.setRebuild(PatrolConstant.TASK_UNREBUILD);   // 任务重新生成状态
        // 标准工时
        task.setStandardDuration(plan.getStandardDuration());

        // 获取计划的巡检策略
        List<PatrolPlanStrategy> strategyList = patrolPlanStrategyService.lambdaQuery()
                .eq(PatrolPlanStrategy::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(PatrolPlanStrategy::getPlanId, plan.getId())
                .list();
        strategyList.forEach(strategy -> {
            DateTime date = DateUtil.date();
            task.setPatrolDate(date);    // 巡检日期
            task.setStartTime(strategy.getStartTime()); // 巡检开始时间
            task.setEndTime(strategy.getEndTime()); // 巡检结束时间

            // 保存任务记录并复制相关关联表数据
            saveAndCopyData(task, plan);
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
        // 更新计划已生成任务状态
        if (ObjectUtil.isNotEmpty(plan.getCreated()) && 0 == plan.getCreated()) {
            plan.setCreated(PatrolConstant.PLAN_CREATED);
            patrolPlanService.updateById(plan);
        }

        // 将复制巡检计划的组织机构和站点关联信息
        log.info("正在复制组织机构、站所任务的关联信息...");
        copyOrgAndStationInfo(task, plan);
        log.info("组织机构、站所任务的关联信息复制完毕！");
        log.info("添加任务与标准以及设备的关联表信息...");
        // 生成巡检单，即巡检任务与标准以及设备关联表数据
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
        wrapper.lambda().eq(PatrolPlanStandard::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(PatrolPlanStandard::getPlanId, plan.getId());
        List<PatrolPlanStandard> planStandardList = patrolPlanStandardService.list(wrapper);

        // 遍历计划中的每一个标准生成工单数据
        planStandardList.forEach(l -> {
            // 根据巡检标准编号获取巡检标准记录
            QueryWrapper<PatrolStandard> standardWrapper = new QueryWrapper<>();
            standardWrapper.lambda().eq(PatrolStandard::getCode, l.getStandardCode());
            PatrolStandard standard = patrolStandardService.getOne(standardWrapper);
            if (ObjectUtil.isEmpty(standard)) {
                log.error("计划关联的标准已不存在！计划ID为:{},标准编号为：{}", l.getPlanId(), l.getStandardCode());
                throw new AiurtBootException("未找到计划关联的标准数据！");
            }

            // 保存巡检任务标准关联数据，并获取对应的任务标准关联表ID
            String taskStandardId = saveTaskStandardData(task, l, standard);
            // 根据计划ID获取计划设备关联表记录
            QueryWrapper<PatrolPlanDevice> planDeviceWrapper = new QueryWrapper<>();
            planDeviceWrapper.lambda()
                    .eq(PatrolPlanDevice::getDelFlag, CommonConstant.DEL_FLAG_0)
                    // 对应计划的id
                    .eq(PatrolPlanDevice::getPlanId, plan.getId())
                    // 对应计划标准下的设备
                    .eq(PatrolPlanDevice::getPlanStandardId, l.getId());
            List<PatrolPlanDevice> planDeviceList = patrolPlanDeviceService.list(planDeviceWrapper);
            // 保存巡视任务设备关联表
            if (CollUtil.isNotEmpty(planDeviceList)) {
                ArrayList<PatrolDevice> patrolDeviceList = new ArrayList<>();
                planDeviceList.forEach(pd -> {
                    PatrolDevice patrolDevice = new PatrolDevice();
                    patrolDevice.setTaskId(task.getId());
                    patrolDevice.setTaskStandardId(taskStandardId);
                    patrolDevice.setDeviceCode(pd.getDeviceCode());
                    patrolDeviceList.add(patrolDevice);
                });
                patrolDeviceService.saveBatch(patrolDeviceList);
            }

            Integer deviceType = standard.getDeviceType();
            Integer isMergeDevice = standard.getIsMergeDevice();
            //通过配置去掉需要指定设备的限制，如果和并工单，则和与设备类型无关一样，只根据站点生成工单

            if (PatrolConstant.DEVICE_INDEPENDENCE.equals(deviceType) || 1 == isMergeDevice) {
                // 与设备无关
                // 根据计划ID获取计划
                PatrolPlan patrolPlan = patrolPlanService.getById(l.getPlanId());
                if (ObjectUtil.isEmpty(patrolPlan)) {
                    throw new AiurtBootException("计划不存在！计划ID：" + l.getPlanId());
                }
                QueryWrapper<PatrolPlanStation> planWrapper = new QueryWrapper<>();
                planWrapper.lambda().eq(PatrolPlanStation::getPlanCode, patrolPlan.getCode());
                List<PatrolPlanStation> list = Optional.ofNullable(patrolPlanStationService.list(planWrapper)).orElseGet(Collections::emptyList);

                // 根据选择的站点和标准生成巡检单数据
                list.forEach(station -> {
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
                // 遍历设备列表信息
                planDeviceList.forEach(
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
                                if (ObjectUtil.isEmpty(device)) {
                                    log.error("设备编号为{}的设备未找到！对应的计划ID为：{},", ps.getDeviceCode(), ps.getPlanId());
                                    throw new AiurtBootException("未找到对应设备信息！");
                                }
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
                        }
                );
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
                .eq(PatrolStandardItems::getDelFlag, CommonConstant.DEL_FLAG_0)
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
            result.setSpecialCharacters(l.getSpecialCharacters());  // 特殊字符输入
            result.setDelFlag(0);  // 数据校验表达式
            result.setRequired(l.getRequired()); // 检查值是否必填
            result.setProcMethods(l.getProcMethods()); // 程序及方法
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
