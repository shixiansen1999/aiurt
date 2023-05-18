package com.aiurt.boot.index.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.index.dto.*;
import com.aiurt.boot.index.mapper.IndexPlanMapper;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.mapper.RepairTaskStationRelMapper;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import com.aiurt.common.aspect.annotation.DisableDataFilter;
import com.aiurt.common.util.DateUtils;
import com.aiurt.config.datafilter.constant.DataPermRuleType;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.config.datafilter.utils.ContextUtil;
import com.aiurt.config.datafilter.utils.SqlBuilderUtil;
import com.aiurt.modules.common.api.DailyFaultApi;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description: 首页检修接口处理类
 * @date 2022/9/515:04
 */
@Service
public class IndexPlanService {
    @Resource
    private RepairPoolMapper repairPoolMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private InspectionManager manager;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private IndexPlanMapper indexPlanMapper;
    @Resource
    private RepairTaskMapper repairTaskMapper;
    @Resource
    private RepairTaskUserMapper repairTaskUserMapper;
    @Resource
    private RepairTaskStationRelMapper repairTaskStationRelMapper;
    @Resource
    private IBaseApi baseApi;
    @Resource
    private PatrolApi patrolApi;
    @Resource
    private DailyFaultApi dailyFaultApi;
    @Resource
    private RepairPoolOrgRelMapper orgRelMapper;
    @Resource
    private RepairPoolRelMapper poolRelMapper;
    @Resource
    private RepairPoolCodeMapper poolCodeMapper;
    @Autowired
    private ISysParamAPI iSysParamAPI;

    /**
     * 获取计划概览信息
     *
     * @param isAllData 是否查询全部数据
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 计划概览信息
     */
    public PlanIndexDTO getOverviewInfo(Integer isAllData, Date startDate, Date endDate, HttpServletRequest request) {
        PlanIndexDTO result = new PlanIndexDTO();
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return setDefaultResultValues(result);
        }

        // 构建查询条件
        Date[] dates = doQuery(startDate, endDate);

        // 查询检修计划列表
        List<RepairPool> repairPoolList = repairPoolMapper.getOverviewInfo(dates[0], dates[1]);

        // 计算结果值
        return calculateResultValues(result, repairPoolList);
    }

    /**
     * 处理查询参数
     *
     * @param startDate 开始日期
     * @param endDate   借宿日期
     */
    public Date[] doQuery(Date startDate, Date endDate) {
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return null;
        }

        // 用于判断是否是一整月的查询
        // 如果是一整个月查询，那么返回的dayBegin是这个月的第一周的开始时间，dayEnd是这个月最后一周的结束时间
        JudgeIsMonthQuery judgeIsMonthQuery = new JudgeIsMonthQuery(startDate, endDate).invoke();
        startDate = judgeIsMonthQuery.getDayBegin();
        endDate = judgeIsMonthQuery.getDayEnd();

        // 返回一个包含 startDate 和 endDate 的数组
        return new Date[]{startDate, endDate};
    }

    /**
     * 获取任务详情信息
     *
     * @param taskDetailsReq 任务详情请求对象
     * @return IPage<TaskDetailsDTO> 任务详情分页数据
     */
    @DisableDataFilter
    public IPage<TaskDetailsDTO> getOverviewInfoDetails(TaskDetailsReq taskDetailsReq, HttpServletRequest request) {
        Map<String, String> dataRules = (Map<String, String>) request.getAttribute(ContextUtil.FILTER_DATA_AUTHOR_RULES);
        Map<String, String> columnMapping = this.getColumnMapping();
        String filterConditions = SqlBuilderUtil.buildSql(dataRules, columnMapping);

        // 初始化结果对象
        IPage<TaskDetailsDTO> result = new Page<>();

        // 验证请求参数是否为空，为空则直接返回空结果
        if (ObjectUtil.isEmpty(taskDetailsReq)
                || ObjectUtil.isEmpty(taskDetailsReq.getType())
                || ObjectUtil.isEmpty(taskDetailsReq.getStartTime())
                || ObjectUtil.isEmpty(taskDetailsReq.getEndTime())) {
            return result;
        }

        // 创建分页对象
        Page<TaskDetailsDTO> page = new Page<>(taskDetailsReq.getPageNo(), taskDetailsReq.getPageSize());

        // 设置默认值
        if (ObjectUtil.isEmpty(taskDetailsReq.getIsAllData())) {
            taskDetailsReq.setIsAllData(InspectionConstant.IS_ALL_DATA_0);
        }

        // 判断是否是一个月的查询
        JudgeIsMonthQuery judgeIsMonthQuery = new JudgeIsMonthQuery(taskDetailsReq.getStartTime(), taskDetailsReq.getEndTime()).invoke();
        taskDetailsReq.setStartTime(judgeIsMonthQuery.getDayBegin());
        taskDetailsReq.setEndTime(judgeIsMonthQuery.getDayEnd());

        // 获取分组数据
        List<TaskDetailsDTO> detailsDTOList = indexPlanMapper.getGropuByData(taskDetailsReq.getType(), page, taskDetailsReq, filterConditions);

        // 如果存在符合条件的检修详情数据
        if (CollUtil.isNotEmpty(detailsDTOList)) {
            for (TaskDetailsDTO taskDetailsDTO : detailsDTOList) {
                List<String> codeList = new ArrayList<>();
                if (StrUtil.isNotEmpty(taskDetailsDTO.getCodeStr())) {
                    codeList = StrUtil.split(taskDetailsDTO.getCodeStr(), ',');
                }

                // 获取提交时间
                if (CollUtil.isNotEmpty(codeList)) {
                    LambdaQueryWrapper<RepairTask> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.in(RepairTask::getCode, codeList);
                    queryWrapper.isNotNull(RepairTask::getSubmitTime);
                    queryWrapper.orderByDesc(RepairTask::getAssignTime);
                    queryWrapper.select(RepairTask::getSubmitTime);
                    List<RepairTask> repairTasks = repairTaskMapper.selectList(queryWrapper);
                    if (CollUtil.isNotEmpty(repairTasks)) {
                        taskDetailsDTO.setSubmitTime(repairTasks.get(0).getSubmitTime());
                    }
                }

                // 获取检修人员和所属班组
                if (CollUtil.isNotEmpty(codeList)) {
                    getRealNameAndTeanName(taskDetailsDTO, codeList);
                }
            }
        }

        // 设置结果数据
        page.setRecords(detailsDTOList);
        return page;
    }


    /**
     * 获取检修人员和所属班组
     *
     * @param taskDetailsDTO
     * @param codeList
     */
    public void getRealNameAndTeanName(TaskDetailsDTO taskDetailsDTO, List<String> codeList) {
        LambdaQueryWrapper<RepairTaskUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(RepairTaskUser::getRepairTaskCode, codeList);
        lambdaQueryWrapper.select(RepairTaskUser::getUserId);
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(lambdaQueryWrapper);

        LambdaQueryWrapper<RepairTask> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.in(RepairTask::getCode, codeList);
        taskQueryWrapper.select(RepairTask::getRepairPoolId);
        List<RepairTask> repairTasks = repairTaskMapper.selectList(taskQueryWrapper);

        if (CollUtil.isNotEmpty(repairTaskUsers)) {
            Set<String> userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(userIds)) {
                String userStr = StrUtil.join(",", userIds);
                List<JSONObject> jsonObjects = sysBaseApi.queryUsersByIds(userStr);

                if (CollUtil.isNotEmpty(jsonObjects)) {
                    // 检修人员名称
                    taskDetailsDTO.setRealName(jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；")));
                }
            }
        }

        if (CollUtil.isNotEmpty(repairTasks)) {
            Set<String> orgCodes = new HashSet<>();
            List<String> ids = repairTasks.stream().map(RepairTask::getRepairPoolId).collect(Collectors.toList());
            LambdaQueryWrapper<RepairPool> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(RepairPool::getId, ids);
            queryWrapper.select(RepairPool::getCode);
            List<RepairPool> repairPools = repairPoolMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(repairPools)) {
                for (RepairPool repairPool : repairPools) {
                    List<String> code = repairPoolMapper.selectOrgByCode(repairPool.getCode());
                    orgCodes.addAll(code);
                }
            }
            List<JSONObject> deptList = sysBaseApi.queryDepartsByOrgcodes(StrUtil.join(",", orgCodes));
            if (CollUtil.isNotEmpty(deptList)) {
                taskDetailsDTO.setTeamName(deptList.stream().map(dept -> dept.getString("departName")).collect(Collectors.joining("；")));
            }
        }
    }

    /**
     * 根据管理部门过滤检修计划数据
     *
     * @return
     */
    public List<String> getCodeByOrgCode() {
        List<CsUserDepartModel> departByUserId = sysBaseApi.getDepartByUserId(manager.checkLogin().getId());
        List<RepairPoolOrgRel> repairPoolOrgRels = new ArrayList<>();
        if (CollUtil.isNotEmpty(departByUserId)) {
            LambdaQueryWrapper<RepairPoolOrgRel> relQueryWrapper = new LambdaQueryWrapper<>();
            relQueryWrapper.in(RepairPoolOrgRel::getOrgCode, departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList()));
            repairPoolOrgRels = orgRelMapper.selectList(relQueryWrapper);
        }

        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            return repairPoolOrgRels.stream().map(RepairPoolOrgRel::getRepairPoolCode).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    /**
     * 获取首页的日代办事项
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    public List<DayTodoDTO> getUserSchedule(Integer year, Integer month) {
        List<DayTodoDTO> result = new ArrayList<>();
        if (ObjectUtil.isEmpty(year) || ObjectUtil.isEmpty(month)) {
            return result;
        }

        // 获取某年某月的开始时间
        Date beginDate = beginOfMonth(year, month);

        // 计算某年某月一共有多少天
        int dayNum = getMonthDays(year, month);

        // 检修(key是日期，value是数量)
        Map<String, Integer> inspectionMap = MapUtil.isNotEmpty(this.inspectionNumByDay(beginDate, dayNum)) ? this.inspectionNumByDay(beginDate, dayNum) : new HashMap<>(32);
        // 巡检
        Map<String, Integer> patrolMap = CollUtil.isNotEmpty(patrolApi.getPatrolFinishNumber(year, month)) ? patrolApi.getPatrolFinishNumber(year, month) : new HashMap<>(32);
        // 故障
        Map<String, Integer> faultMap = CollUtil.isNotEmpty(dailyFaultApi.getDailyFaultNum(year, month)) ? dailyFaultApi.getDailyFaultNum(year, month) : new HashMap<>(32);
        // 施工
        Map<String, Integer> constructionMap = new HashMap<>(32);
        // 日程信息
        Map<String, List<DailySchedule>> scheduleMap = MapUtil.isNotEmpty(baseApi.queryDailyScheduleList(year, month)) ? baseApi.queryDailyScheduleList(year, month) : new HashMap<>(32);

        // 组装数据
        if (ObjectUtil.isNotEmpty(beginDate)) {
            for (int i = 0; i < dayNum; i++) {
                // 偏移日期
                String currDateStr = DateUtil.format(DateUtil.offsetDay(beginDate, i), "yyyy/MM/dd");
                DayTodoDTO dayTodoDTO = new DayTodoDTO();
                dayTodoDTO.setCurrDate(currDateStr);
                dayTodoDTO.setConstructionNum(ObjectUtil.isEmpty(constructionMap.get(currDateStr)) ? 0 : constructionMap.get(currDateStr));
                dayTodoDTO.setFaultNum(ObjectUtil.isEmpty(faultMap.get(currDateStr)) ? 0 : faultMap.get(currDateStr));
                dayTodoDTO.setInspectionNum(ObjectUtil.isEmpty(inspectionMap.get(currDateStr)) ? 0 : inspectionMap.get(currDateStr));
                dayTodoDTO.setPatrolNum(ObjectUtil.isEmpty(patrolMap.get(currDateStr)) ? 0 : patrolMap.get(currDateStr));
                dayTodoDTO.setDailyScheduleList(CollUtil.isEmpty(scheduleMap.get(currDateStr)) ? new ArrayList<>() : scheduleMap.get(currDateStr));
                dayTodoDTO.setIsAgenda(CollUtil.isEmpty(scheduleMap.get(currDateStr)) ? 0 : 1);
                result.add(dayTodoDTO);
            }
        }

        return result;
    }

    /**
     * 按天查询检修任务完成数
     *
     * @param beginDate
     * @param dayNum
     * @return
     */
    private Map<String, Integer> inspectionNumByDay(Date beginDate, int dayNum) {
        Map<String, Integer> result = new HashMap<>(32);
        //根据配置决定统计的维保数按照维保开始时间还是提交时间进行筛选
        SysParamModel sysParam = iSysParamAPI.selectByCode(SysParamCodeConstant.AUTO_CC);
        boolean autoCc = "1".equals(sysParam.getValue());
        List<RepairTaskNum> repairTaskNums = new ArrayList<>();
        if (autoCc) {
            repairTaskNums = repairTaskMapper.selectRepairPoolListSpecial(DateUtil.beginOfMonth(beginDate), DateUtil.endOfMonth(beginDate));
        } else {
            repairTaskNums = repairTaskMapper.selectRepairPoolList(DateUtil.beginOfMonth(beginDate), DateUtil.endOfMonth(beginDate));
        }
        if (CollUtil.isNotEmpty(repairTaskNums)) {
            result = repairTaskNums.stream().collect(Collectors.toMap(RepairTaskNum::getCurrDateStr, RepairTaskNum::getNum, (v1, v2) -> v1));
        }
        return result;
    }


    /**
     * 计算某年某月一共有多少天
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    private int getMonthDays(Integer year, Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 获取某年某月的开始时间
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    private Date beginOfMonth(Integer year, Integer month) {
        // 获取当前分区的日历信息(这里可以使用参数指定时区)
        Calendar calendar = Calendar.getInstance();
        // 设置年
        calendar.set(Calendar.YEAR, year);
        // 设置月，月份从0开始
        calendar.set(Calendar.MONTH, month - 1);
        // 设置为指定月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 获取指定月第一天的时间
        Date start = calendar.getTime();
        return start;
    }

    /**
     * 点击站点获取检修数据
     *
     * @param taskDetailsReq 查询条件
     * @return
     */
    @DisableDataFilter
    public IPage<RepairPoolDetailsDTO> getMaintenancDataByStationCode(TaskDetailsReq taskDetailsReq, HttpServletRequest request) {
        Map<String, String> dataRules = (Map<String, String>) request.getAttribute(ContextUtil.FILTER_DATA_AUTHOR_RULES);
        Map<String, String> columnMapping = this.getColumnMapping();
        String filterConditions = SqlBuilderUtil.buildSql(dataRules, columnMapping);

        Page<RepairPoolDetailsDTO> page = new Page<>(taskDetailsReq.getPageNo(), taskDetailsReq.getPageSize());
        if (ObjectUtil.isEmpty(taskDetailsReq.getType())
                || ObjectUtil.isEmpty(taskDetailsReq)
                || StrUtil.isEmpty(taskDetailsReq.getStationCode())
                || ObjectUtil.isEmpty(taskDetailsReq.getStartTime())
                || ObjectUtil.isEmpty(taskDetailsReq.getEndTime())) {
            return new Page<>();
        }

        // 数据过滤
        if (ObjectUtil.isEmpty(taskDetailsReq.getIsAllData())) {
            taskDetailsReq.setIsAllData(InspectionConstant.IS_ALL_DATA_0);
        }

        // 用于判断是否是一整月的查询
        // 如果是一整个月查询，那么返回的dayBegin是这个月的第一周的开始时间，dayEnd是这个月最后一周的结束时间
        JudgeIsMonthQuery judgeIsMonthQuery = new JudgeIsMonthQuery(taskDetailsReq.getStartTime(), taskDetailsReq.getEndTime()).invoke();
        taskDetailsReq.setStartTime(judgeIsMonthQuery.getDayBegin());
        taskDetailsReq.setEndTime(judgeIsMonthQuery.getDayEnd());

        List<RepairPoolDetailsDTO> maintenancDataByStationCode = indexPlanMapper.getMaintenancDataByStationCode(page, taskDetailsReq.getType(), taskDetailsReq, filterConditions);
        if (CollUtil.isNotEmpty(maintenancDataByStationCode)) {

            // 获取检查周期类型字典映射
            Map<String, String> inspectionCycleTypeMap = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_CYCLE_TYPE)
                    .stream()
                    .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));

            // 获取检查任务状态字典映射
            Map<String, String> inspectionTaskStateMap = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_TASK_STATE)
                    .stream()
                    .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));

            List<String> poolCodes = maintenancDataByStationCode.stream().map(RepairPoolDetailsDTO::getCode).collect(Collectors.toList());

            // 根据任务编码获取组织机构编码映射
            Map<String, String> orgCodeMap = orgRelMapper.selectOrgByCode(poolCodes)
                    .stream()
                    .collect(Collectors.toMap(MapDTO::getValue, MapDTO::getText, (v1, v2) -> v1));

            // 根据任务编码获取站点编码映射
            Map<String, String> stationCodeMap = repairPoolStationRelMapper.selectStationToMapByPlanCode(poolCodes)
                    .stream()
                    .collect(Collectors.toMap(MapDTO::getValue, MapDTO::getText, (v1, v2) -> v1));

            for (RepairPoolDetailsDTO repairPool : maintenancDataByStationCode) {
                // 设置组织机构名称
                repairPool.setOrgName(manager.translateOrg(StrUtil.split(orgCodeMap.get(repairPool.getCode()), ',')));

                // 设置站点名称
                repairPool.setStationName(manager.translateStation(stationCodeMap.get(repairPool.getCode())));

                // 设置周期类型名称
                repairPool.setTypeName(inspectionCycleTypeMap.get(String.valueOf(repairPool.getType())));

                // 设置状态名称
                repairPool.setStatusName(inspectionTaskStateMap.get(String.valueOf(repairPool.getStatus())));

                if (ObjectUtil.isNotEmpty(repairPool.getStartTime()) && ObjectUtil.isNotEmpty(repairPool.getEndTime())) {
                    if (repairPool.getWeeks()==null){
                        int week = DateUtils.getWeekOfYear(repairPool.getEndTime());
                        Date[] dateByWeek = DateUtils.getDateByWeek(DateUtil.year(repairPool.getStartTime()), week);
                        String weekName = String.format("第%d周(%s~%s)", week, DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                        repairPool.setWeekName(weekName);
                    } else {
                        repairPool.setWeekName(String.format("第%s周(%s~%s)", repairPool.getWeeks(), DateUtil.format(repairPool.getStartTime(), "yyyy/MM/dd"), DateUtil.format(repairPool.getEndTime(), "yyyy/MM/dd")));
                    }
                }
            }
        }
        return page.setRecords(maintenancDataByStationCode);
    }

    /**
     * 获取维修任务池的维修情况列表。
     *
     * @param page        分页对象，用于指定分页参数。
     * @param startDate   查询起始日期，用于筛选维修任务。
     * @param stationCode 站点编码，用于筛选与指定站点相关的维修任务。
     * @return 包含维修任务详细信息的分页对象。
     */
    public IPage<RepairPoolDetailsDTO> getMaintenanceSituation(Page<RepairPoolDetailsDTO> page, Date startDate, String stationCode) {
        // //根据配置决定统计的维保数按照维保开始时间还是提交时间进行筛选
        SysParamModel sysParam = iSysParamAPI.selectByCode(SysParamCodeConstant.AUTO_CC);
        // 查询维修任务池的维修情况列表
        List<RepairPoolDetailsDTO> result = repairTaskMapper.getMaintenanceSituation(page, startDate, stationCode,sysParam.getValue());

        // 禁用数据过滤
        boolean dataFilterEnable = GlobalThreadLocal.setDataFilter(false);

        if (CollUtil.isNotEmpty(result)) {
            // 获取检查周期类型字典映射
            Map<String, String> inspectionCycleTypeMap = getInspectionCycleTypeMap();

            // 获取检查任务状态字典映射
            Map<String, String> inspectionTaskStateMap = getInspectionTaskStateMap();

            // 获取任务编码列表
            List<String> taskCodes = result.stream().map(RepairPoolDetailsDTO::getCode).collect(Collectors.toList());

            // 根据任务编码获取组织机构编码映射
            Map<String, String> orgCodeMap = getOrgCodeMap(taskCodes);

            // 根据任务编码获取站点编码映射
            Map<String, String> stationCodeMap = getStationCodeMap(taskCodes);

            // 填充维修任务池详细信息
            for (RepairPoolDetailsDTO repairPool : result) {
                String planCode = repairPool.getCode();

                // 设置组织机构名称
                repairPool.setOrgName(manager.translateOrg(StrUtil.split(orgCodeMap.get(planCode), ',')));

                // 设置站点名称
                repairPool.setStationName(manager.translateStation(stationCodeMap.get(planCode)));

                // 设置周期类型名称
                repairPool.setTypeName(inspectionCycleTypeMap.get(String.valueOf(repairPool.getType())));

                // 设置状态名称
                repairPool.setStatusName(inspectionTaskStateMap.get(String.valueOf(repairPool.getStatus())));

                // 设置所属周（相对年）信息
                if (repairPool.getYear() != null && repairPool.getWeeks() != null) {
                    Date[] dateByWeek = DateUtils.getDateByWeek(repairPool.getYear(), Integer.parseInt(repairPool.getWeeks()));
                    if (dateByWeek.length != 0) {
                        String weekName = String.format("第%s周(%s~%s)", repairPool.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                        repairPool.setWeekName(weekName);
                    }
                }
            }
        }

        // 恢复数据过滤状态
        GlobalThreadLocal.setDataFilter(dataFilterEnable);

        // 将查询结果设置到分页对象并返回
        return page.setRecords(result);
    }

    /**
     * 初始化并返回一个预定义的列映射。
     *
     * @return 返回一个包含预定义列映射的 Map 对象
     */
    public Map<String, String> getColumnMapping() {
        Map<String, String> columnMapping = new HashMap<>(8);
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_DEPT, "rpor.org_code");
        columnMapping.put(DataPermRuleType.TYPE_DEPT_ONLY, "rpor.org_code");
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_STATION_ONLY, "rpsr.station_code");
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_MAJOR_ONLY, "rpc.major_code");
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_SYSTEM_ONLY, "rpc.subsystem_code");
        return columnMapping;
    }

    /**
     * 设置默认的结果值
     *
     * @param result 计划概览信息
     * @return 设置默认值后的计划概览信息
     */
    private PlanIndexDTO setDefaultResultValues(PlanIndexDTO result) {
        result.setSum(0L);
        result.setFinish(0L);
        result.setUnfinish(0L);
        result.setOverhaul(0L);
        result.setOmit(0L);
        result.setOmitRate("0%");
        return result;
    }

    /**
     * 计算计划概览的各项结果值
     *
     * @param result         计划概览信息
     * @param repairPoolList 检修计划列表
     * @return 计算结果值后的计划概览信息
     */
    private PlanIndexDTO calculateResultValues(PlanIndexDTO result, List<RepairPool> repairPoolList) {
        // 检修总数
        result.setSum(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.size() : 0L);
        // 已检修数
        result.setFinish(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.stream().filter(re -> InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);
        // 未检修数量
        result.setUnfinish(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.stream().filter(re -> !InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);
        // 检修中的数量
        result.setOverhaul(CollUtil.isNotEmpty(repairPoolList) ? repairPoolList.stream().filter(re -> !InspectionConstant.COMPLETED.equals(re.getStatus()) && !InspectionConstant.TO_BE_ASSIGNED.equals(re.getStatus())).count() : 0L);
        // 已检修率
        if (result.getSum() <= 0 || result.getFinish() <= 0) {
            result.setFinishRate("0%");
        } else {
            double d = new BigDecimal((double) result.getFinish() * 100 / result.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            result.setFinishRate(d + "%");
        }
        // 漏检数量
        result.setOmit(0L);
        // 漏检率
        if (result.getSum() <= 0 || result.getOmit() <= 0) {
            result.setOmitRate("0%");
        } else {
            double d = new BigDecimal((double) result.getOmit() * 100 / result.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            result.setOmitRate(d + "%");
        }
        //年
        int year = DateUtil.year(new Date());
        //月份
        int month = DateUtil.month(new Date())+1;

        //当前时间
        Date date = new Date();

        //计算每个月第一个完整周的开始时间
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate firstDayOfFirstFullWeek = firstDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        Date from = Date.from(firstDayOfFirstFullWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        String format = DateUtil.format(from, "yyyy-MM-dd");
        String format1 = DateUtil.format(date, "yyyy-MM-dd");

        //如果当前时间在本月第一个完整周的开始时间之前或者当前时间等于第一个完整周的开始时间未维保数量就是0
        if (date.before(from) || format.equals(format1)){
            result.setQuantity(0L);
        }else {
            LambdaQueryWrapper<RepairPool> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RepairPool::getDelFlag,0)
                    .eq(RepairPool::getStatus,InspectionConstant.COMPLETED)
                    .ge(RepairPool::getStartTime,DateUtil.beginOfDay(from))
                    .le(RepairPool::getStartTime,DateUtil.endOfDay(date));
            List<RepairPool> repairPools = repairPoolMapper.selectList(lambdaQueryWrapper);


            long total  = 36;
            long between = DateUtil.between(from, date, DateUnit.DAY);
            long between1 = DateUtil.between(from, date, DateUnit.DAY)*2;
            if (CollUtil.isNotEmpty(repairPools)){
                if (between>15 && between<=21){
                    long count = 15*2+between-15;
                    int size = repairPools.size();
                    //实际维保数量-已完成的维保数量=未完成的维保数量
                    result.setQuantity(count-size);
                }
                int size = repairPools.size();
                if (between>21){
                    result.setQuantity(total-size);
                }
                if(between<15 && between>1){
                    //实际维保数量-已完成的维保数量=未完成的维保数量
                    result.setQuantity(between1-size);
                }
            }
        }
        return result;
    }

    /**
     * 获取检查周期类型字典映射
     * @return 映射的 Map
     */
    private Map<String, String> getInspectionCycleTypeMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_CYCLE_TYPE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取检查任务状态字典映射
     * @return 映射的 Map
     */
    private Map<String, String> getInspectionTaskStateMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_TASK_STATE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 根据任务编码获取组织机构编码映射
     * @param poolCodes 任务编码列表
     * @return 映射的 Map
     */
    private Map<String, String> getOrgCodeMap(List<String> poolCodes) {
        if (poolCodes == null || poolCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        return orgRelMapper.selectOrgByCode(poolCodes)
                .stream()
                .filter(mapDTO -> mapDTO.getValue() != null && mapDTO.getText() != null)
                .collect(Collectors.toMap(MapDTO::getValue, MapDTO::getText, (v1, v2) -> v1));
    }

    /**
     * 根据任务编码获取站点编码映射
     * @param poolCodes 任务编码列表
     * @return 映射的 Map
     */
    private Map<String, String> getStationCodeMap(List<String> poolCodes) {
        if (poolCodes == null || poolCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        return repairPoolStationRelMapper.selectStationToMapByPlanCode(poolCodes)
                .stream()
                .filter(mapDTO -> mapDTO.getValue() != null && mapDTO.getText() != null)
                .collect(Collectors.toMap(MapDTO::getValue, MapDTO::getText, (v1, v2) -> v1));
    }

}
