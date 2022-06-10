package com.aiurt.boot.modules.repairManage.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.InspectionContant;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.appMessage.constant.MessageConstant;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.param.UserTaskAddParam;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.utils.NumberGenerateUtils;
import com.aiurt.boot.modules.repairManage.entity.RepairPool;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.aiurt.boot.modules.repairManage.mapper.RepairPoolMapper;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskMapper;
import com.aiurt.boot.modules.repairManage.service.IRepairPoolService;
import com.aiurt.boot.modules.repairManage.vo.AssignVO;
import com.aiurt.boot.modules.standardManage.inspectionSpecification.entity.InspectionCode;
import com.aiurt.boot.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.aiurt.boot.modules.standardManage.inspectionStrategy.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.mapper.SysUserMapper;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 检修计划池
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Service
public class RepairPoolServiceImpl extends ServiceImpl<RepairPoolMapper, RepairPool> implements IRepairPoolService {

    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;

    @Resource
    private RepairTaskMapper repairTaskMapper;

    @Resource
    private NumberGenerateUtils numberGenerateUtils;

    @Autowired
    private IStationService stationService;

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private UserTaskService userTaskService;

    @Resource
    private IMessageService messageService;

    /**
     * 生成检修计划池任务
     *
     * @param inspectionCode
     * @return
     */
    @Transactional
    public Result generateTask(InspectionCode inspectionCode) throws Exception {
        List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectListById(inspectionCode.getId().toString());
        if (inspectionCodeContentList.size() == 0) {
            return Result.error("请先设置策略");
        }
        for (InspectionCodeContent inspectionCodeContent : inspectionCodeContentList) {
            Integer type = inspectionCodeContent.getType();
            //周检
            if (type.equals(InspectionContant.WEEK)) {
                weekPlan(inspectionCode, inspectionCodeContent);
            }
            //月检
            if (type.equals(InspectionContant.MONTH)) {
                monthPlan(inspectionCode, inspectionCodeContent);
            }
            //双月检
            if (type.equals(InspectionContant.DOUBLEMONTH)) {
                doubleMonthPlan(inspectionCode, inspectionCodeContent);
            }
            //季检
            if (type.equals(InspectionContant.QUARTER)) {
                quarterPlan(inspectionCode, inspectionCodeContent);
            }
            //半年检
            if (type.equals(InspectionContant.SEMIANNUAL)) {
                semiAnnualPlan(inspectionCode, inspectionCodeContent);
            }
            //年检
            if (type.equals(InspectionContant.ANNUAL)) {
                annualPlan(inspectionCode, inspectionCodeContent);
            }
        }
        return Result.ok();
    }

    /**
     * 重新生成检修计划
     *
     * @param inspectionCode
     * @return
     */
    public Result generateReNewTask(InspectionCode inspectionCode) throws Exception {
        //查询检修策略是否全部设置
        final QueryWrapper<InspectionCodeContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inspection_code_id",inspectionCode.getId())
                .isNull("tactics")
                    .eq("del_flag", 0);
        final Integer count = Math.toIntExact(inspectionCodeContentMapper.selectCount(queryWrapper));
        if (count > 0){
            return Result.error("请先完成检修策略的设置");
        }
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        wrapper.eq("inspection_code_id", inspectionCode.getId()).eq("del_flag", 0).ge("end_time", DateUtil.now());
        List<RepairPool> list = this.baseMapper.selectList(wrapper);
        //if (list.size() == 0) {
        //    return Result.error("检修池任务暂无任务,无法重新生成新任务");
        //}
        list.forEach(x -> {
            x.setDelFlag(1);
//            final QueryWrapper<RepairTask> taskQueryWrapper = new QueryWrapper<>();
//            taskQueryWrapper.eq("del_flag",0).apply("find_in_set('" + x.getId() + "',repair_pool_ids)");
//            final List<RepairTask> taskList = repairTaskMapper.selectList(taskQueryWrapper);
//            taskList.forEach(y->{
//                repairTaskMapper.deleteById(y.getId());
//                userTaskService.removeUserTaskWork(Convert.convert(List.class, y.getStaffIds()),y.getId(),2);
//            });
        });
        if (CollectionUtils.isNotEmpty(list)) {
            this.updateBatchById(list);
        }
        return this.generateTask(inspectionCode);
    }

    /**
     * 判断是否是今年 true是，false否
     *
     * @param years
     * @return
     */
    private boolean isThisYear(String years) {
        if (Integer.valueOf(DateUtils.getYear()).equals(Integer.valueOf(years))) {
            return true;
        }
        return false;
    }

    /**
     * 周检任务
     *
     * @param inspectionCode
     * @param inspectionCodeContent
     * @return
     */
    private Result weekPlan(InspectionCode inspectionCode, InspectionCodeContent inspectionCodeContent) {
        //获取当前规范的年份,如果是今年就生成今年剩下的任务，如果是明年，则生成明年的所有任务
        Date date;
        if (Integer.valueOf(DateUtils.getYear()).equals(Integer.valueOf(inspectionCode.getYears()))) {
            date = DateUtils.getDate();
        } else {
            date = DateUtils.getNextYearFirstDay();
        }
        //生成时间限制
        List<Date[]> list = DateUtils.yearWeekList(date);
        if (list.size() == 0) {
            return Result.error("本年度最后一周无法生成周检");
        }
        for (int i = 0; i < list.size(); i++) {
            addEveryWeekTask(1, inspectionCode.getType(), inspectionCodeContent,
                    list.get(i)[0], list.get(i)[1], inspectionCode.getOrganizationIds());
        }
        return Result.ok();
    }

    /**
     * 月检
     *
     * @param inspectionCode
     * @param inspectionCodeContent
     * @return
     */
    private Result monthPlan(InspectionCode inspectionCode, InspectionCodeContent inspectionCodeContent) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }
        for (int j = month; j <= InspectionContant.MONTHAMOUNT; j++) {
            Integer tactics = inspectionCodeContent.getTactics();
            //如果是今年,这个月的周数 大于 策略的周数 跳出当前循环
            if (thisYear && j == month && week > tactics) {
                continue;
            }
            //获取j月第tactics周的时间
            Date[] date = DateUtils.getDateByMonthAndWeek(Integer.parseInt(inspectionCode.getYears()),j, tactics);
            if (date == null) {
                return Result.error("无可生成计划");
            }
            //设置每个月的第几周新增任务
            addEveryWeekTask(2, inspectionCode.getType(), inspectionCodeContent, date[0], date[1], inspectionCode.getOrganizationIds());
        }
        return Result.ok();
    }

    /**
     * 双月检
     *
     * @param inspectionCode
     * @param inspectionCodeContent
     * @return
     */
    private Result doubleMonthPlan(InspectionCode inspectionCode, InspectionCodeContent inspectionCodeContent) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }
        //两个月两个月来操作
        for (int j = month; j <= InspectionContant.MONTHAMOUNT; j++) {
            if ((j & 1) == 0) {
                continue;
            }
            //注意:他的策略 一个月是按4周来算的
            Integer tactics = inspectionCodeContent.getTactics();
            //计算这个策略是 第几月
            int monthnum = (tactics - 1) / 4;
            //计算这个策略是 第几周
            int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;
            if (thisYear && j + monthnum == month && week > weeknum) {
                continue;
            }
            Date[] date = DateUtils.getDateByMonthAndWeek(Integer.parseInt(inspectionCode.getYears()),j + monthnum, weeknum);
            if (date == null) {
                return Result.error("无可生成计划");
            }
            addEveryWeekTask(3, inspectionCode.getType(), inspectionCodeContent, date[0], date[1], inspectionCode.getOrganizationIds());
        }
        return Result.ok();
    }

    /**
     * 季检
     *
     * @param inspectionCode
     * @param inspectionCodeContent
     * @return
     */
    private Result quarterPlan(InspectionCode inspectionCode, InspectionCodeContent inspectionCodeContent) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }
        int quarter = DateUtils.getQuarter(month);
        for (int y = quarter; y <= InspectionContant.QUARTERAMOUNT; y++) {
//            for (int i = 0; i < list.size(); i++) {
            Integer tactics = inspectionCodeContent.getTactics();
            //计算这个策略是第几月
            int monthnum = (tactics - 1) / 4 ;
            //计算这个策略是第几周
            int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;

            //获取季度的开始月份
            int monthStart = (y - 1) * 3 + 1;
            if (thisYear) {
                if (monthStart + monthnum < month) {
                    continue;
                }
                if (monthStart + monthnum == month && week > weeknum) {
                    continue;
                }
            }
            Date[] date = DateUtils.getDateByMonthAndWeek(Integer.parseInt(inspectionCode.getYears()),monthStart + monthnum, weeknum);
            if (date == null) {
                return Result.error("无可生成计划");
            }
            addEveryWeekTask(4, inspectionCode.getType(), inspectionCodeContent, date[0], date[1], inspectionCode.getOrganizationIds());
//            }
        }
        return Result.ok();
    }

    /**
     * 半年检
     *
     * @param inspectionCode
     * @param inspectionCodeContent
     * @return
     */
    private Result semiAnnualPlan(InspectionCode inspectionCode, InspectionCodeContent inspectionCodeContent) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        if (!thisYear) {
            month = 1;
        }
        int isFirstHalfYear = 0;
        if (month > InspectionContant.SEMIANNUALAMOUNT) {
            isFirstHalfYear = 1;
        }

        for (int y = isFirstHalfYear; y < InspectionContant.HALFYEARAMOUNT; y++) {
            Integer tactics = inspectionCodeContent.getTactics();
            //计算这个策略是第几月
            int monthnum = (tactics - 1) / 4 + 1;
            //计算这个策略是第几周
            int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;
            if (y == 1) {
                monthnum = monthnum + 6;
            }
            if (thisYear && monthnum < month) {
                continue;
            }
            if (thisYear && monthnum == month && week > weeknum) {
                continue;
            }
            Date[] date = DateUtils.getDateByMonthAndWeek(Integer.parseInt(inspectionCode.getYears()),monthnum, weeknum);
            if (date == null) {
                return Result.error("无可生成计划");
            }
            addEveryWeekTask(5, inspectionCode.getType(), inspectionCodeContent, date[0], date[1], inspectionCode.getOrganizationIds());
        }
        return Result.ok();
    }

    /**
     * 年检
     *
     * @param inspectionCode
     * @param inspectionCodeContent
     * @return
     */
        private Result annualPlan(InspectionCode inspectionCode, InspectionCodeContent inspectionCodeContent) throws Exception {
        boolean thisYear = isThisYear(inspectionCode.getYears());
        //获取当前月
        int month = DateUtils.getMonth();
        //获取当前周数
        int week = DateUtils.getWeekOfYear(new Date());
        Integer tactics = inspectionCodeContent.getTactics();
        //计算这个策略是第几月
        int monthnum = (tactics - 1) / 4 + 1;
        //计算这个策略是第几周
        int weeknum = tactics % 4 == 0 ? 4 : tactics % 4;
        if (thisYear && monthnum < month) {
            return Result.error("");
        }
        if (thisYear && monthnum == month && week > weeknum) {
            return Result.error("");
        }
        Date[] date = DateUtils.getDateByMonthAndWeek(Integer.parseInt(inspectionCode.getYears()),monthnum, weeknum);
        if (date == null) {
            return Result.error("无可生成计划");
        }
        //设置每个月的第几周新增任务
        addEveryWeekTask(6, inspectionCode.getType(), inspectionCodeContent, date[0], date[1], inspectionCode.getOrganizationIds());
        return Result.ok();
    }


    /**
     * 创建每周的任务
     */
    private void addEveryWeekTask(Integer type, Integer icType, InspectionCodeContent inspectionCodeContent, Date startTime, Date endTime, String organizationIds) {
        String[] split = organizationIds.split(",");
        for (String organizationId : split) {
            RepairPool repairPool = new RepairPool();
            repairPool.setType(type);
            repairPool.setIcType(icType);
            repairPool.setWeeks(DateUtils.getWeekOfYear(endTime));
            repairPool.setInspectionCodeId(inspectionCodeContent.getInspectionCodeId());
            repairPool.setRepairPoolContent(inspectionCodeContent.getContent());
            repairPool.setStartTime(startTime);
            repairPool.setEndTime(endTime);
//            repairPool.setStatus(0);
            repairPool.setOrganizationId(organizationId);
            repairPool.setIsReceipt(inspectionCodeContent.getIsReceipt());
            this.baseMapper.insert(repairPool);
        }
    }

    @Override
    @Transactional
    public Result updateTime(String ids, String startTime, String endTime) {
        String[] split = ids.split(",");
        for (String id : split) {
            int week = DateUtils.getWeekOfYear(DateUtil.parse(startTime));
            RepairPool repairPool = this.baseMapper.selectById(id);
//            if (repairPool.getStatus().equals(RepairContant.ASSIGNEDSTATUS[1])) {
//                throw new SwscException("已指派无法修改时间");
//            }
            if (repairPool.getType().equals(1)){
                throw new SwscException("周检无法修改时间");
            }
            repairPool.setWeeks(week);
            repairPool.setStartTime(DateUtil.parse(startTime.concat(" 00:00:00")));
            repairPool.setEndTime(DateUtil.parse(endTime.concat(" 23:59:59")));
            this.baseMapper.updateById(repairPool);
        }
        return Result.ok();
    }


    @Override
    public Result getRepairTask(String startTime, String endTime) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (StrUtil.isBlank(startTime) || StrUtil.isBlank(endTime)) {
            //获取当前周
            Date date = new Date();
            Date st = DateUtils.getWeekStartTime(date);
            Date et = DateUtils.getWeekEndTime(date);
            startTime = DateUtil.format(st,"yyyy-MM-dd HH:mm:ss");
            endTime = DateUtil.format(et,"yyyy-MM-dd HH:mm:ss");
        }
        //查询检修池任务
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", 0).eq("organization_id", user.getOrgId())
                .ge("start_time", startTime).le("end_time", endTime);
        List<RepairPool> list = this.baseMapper.selectList(wrapper);
        return Result.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result assigned(AssignVO assignVO) {

        final String ids = assignVO.getIds();
        String[] split = ids.split(",");
        final String userIds = assignVO.getUserIds();

        //如果有一条记录需要验收则所有的记录需要验收
        Integer isReceipt = 0;
        for (String id : split) {
            RepairPool repairPool = this.baseMapper.selectById(id);
            if (repairPool.getIsReceipt() == 1){
                isReceipt=1;
                break;
            }
        }

        //如果这个任务和站点被指派过则无法指派
        QueryWrapper<RepairTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0).eq("repair_pool_ids", ids)
                .eq("organization_id",assignVO.getStationId()).last("limit 1");
        RepairTask queryTask = repairTaskMapper.selectOne(queryWrapper);
        if (queryTask != null){
            return Result.error("当前站点已被指派该任务，请勿重复指派");
        }

        //查询站点信息
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<Station>().eq(Station::getId,assignVO.getStationId()).eq(Station::getDelFlag, 0).last("limit 1");
        Station station = stationService.getOne(wrapper);
        if (station == null) {
            throw new SwscException("非法站点信息");
        }
        String codeNo = numberGenerateUtils.getCodeNo("J".concat(station.getLineId().toString().concat(station.getStationCode())));

        //查询用户names
        final List<?> userIDs = Convert.toList(userIds);
        final List<SysUser> sysUsers = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIDs));
        final List<String> userNamesList = sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList());
        String userNames = Convert.toStr(userNamesList);
        userNames = userNames.substring(1, userNames.length() - 1);

        RepairPool repairPool = this.baseMapper.selectById(split[0]);
        //添加检修单记录
        RepairTask repairTask = new RepairTask();
        repairTask.setRepairPoolIds(ids);
        repairTask.setCode(codeNo);
        repairTask.setWeeks(repairPool.getWeeks());
        repairTask.setStartTime(repairPool.getStartTime());
        repairTask.setEndTime(repairPool.getEndTime());
        repairTask.setIcType(repairPool.getIcType());
        repairTask.setOrganizationId(assignVO.getStationId());
        repairTask.setStatus(0);
        repairTask.setStaffIds(userIds);
        repairTask.setStaffNames(userNames);
        repairTask.setIsReceipt(isReceipt);
        repairTask.setWorkType(assignVO.getWorkType());
        repairTask.setPlanOrderCode(assignVO.getPlanOrderCode());
        repairTask.setPlanOrderCodeUrl(assignVO.getPlanOrderCodeUrl());
        repairTaskMapper.insert(repairTask);

        //添加UserTaskParam记录
        final UserTaskAddParam param = new UserTaskAddParam();
        String[] userId = userIds.split(",");
        param.setUserIds(Arrays.asList(userId));
        param.setWorkCode(repairTask.getCode());
        param.setType(UserTaskConstant.USER_TASK_TYPE_2);
        param.setLevel(2);
        param.setRecordId(repairTask.getId());
        param.setTitle("检修");
        param.setContent(station.getLineName().concat(station.getStationName()));
        userTaskService.add(param);

        //发送app消息
        Message message = new Message();
        message.setTitle("消息通知").setContent("您有一条新的检修任务!").setType(MessageConstant.MESSAGE_TYPE_0).setCode(repairTask.getId().toString());
        messageService.addMessage(MessageAddParam.builder().message(message).userIds(param.getUserIds()).build());


        return Result.ok();
    }

    @Override
    @SneakyThrows
    public Result getTimeInfo(int year) {
        LocalDateTime yearFirst = DateUtils.getYearFirst(year);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = yearFirst.atZone(zoneId);
        Date date = Date.from(zonedDateTime.toInstant());
        ArrayList<Object> list = DateUtils.getWeekAndTime(date);
        return Result.ok(list);
    }

    //    @Override
//    @Transactional
//    public Result assigned(String ids, String userIds, String userNames, String stationId, String workType, String planOrderCode, String planOrderCodeUrl) {
//        String[] split = ids.split(",");
//        Integer isReceipt = 0;
//        for (String id : split) {
//            RepairPool repairPool = this.baseMapper.selectById(id);
//            if (repairPool == null) {
//                throw new SwscException("非法参数");
//            }
//            if (repairPool.getStatus().equals(RepairContant.ASSIGNEDSTATUS[1])) {
//                throw new SwscException("请勿重复指派");
//            }
//            if (repairPool.getIsReceipt() == 1){
//                isReceipt=1;
//            }
//            repairPool.setRepairUserIds(userIds);
//            repairPool.setStatus(1);
//            this.baseMapper.updateById(repairPool);
//        }
//        RepairPool repairPool = this.baseMapper.selectById(split[0]);
//        //根据当前用户获取线路和站点
//        String[] userId = userIds.split(",");
//        String uid = userId[0];
//        SysUser sysUser = userMapper.selectById(uid);
//        if (sysUser == null) {
//            throw new SwscException("非法用户");
//        }
//        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<Station>().eq(Station::getTeamId, sysUser.getOrgId()).eq(Station::getDelFlag, 0).last("limit 1");
//        Station station = stationService.getOne(wrapper);
//        if (station == null) {
//            throw new SwscException("非法站点信息");
//        }
//        String codeNo = numberGenerateUtils.getCodeNo("J".concat(station.getLineId().toString().concat(station.getStationCode())));
//        //添加检修单记录
//        RepairTask repairTask = new RepairTask();
//        repairTask.setRepairPoolIds(ids);
//        repairTask.setCode(codeNo);
//        repairTask.setWeeks(repairPool.getWeeks());
//        repairTask.setStartTime(repairPool.getStartTime());
//        repairTask.setEndTime(repairPool.getEndTime());
//        repairTask.setIcType(repairPool.getIcType());
//        repairTask.setOrganizationId(repairPool.getOrganizationId());
//        repairTask.setStatus(0);
//        repairTask.setStaffIds(userIds);
//        repairTask.setStaffNames(userNames);
//        repairTask.setIsReceipt(isReceipt);
//
//        repairTask.setWorkType(workType);
//        repairTask.setPlanOrderCode(planOrderCode);
//        repairTask.setPlanOrderCodeUrl(planOrderCodeUrl);
//        repairTaskMapper.insert(repairTask);
//
//        final UserTaskAddParam param = new UserTaskAddParam();
//        param.setUserIds(Arrays.asList(userId));
//        param.setType(2);
//        param.setLevel(2);
//        param.setRecordId(repairPool.getId());
//        param.setTitle("检修");
//        param.setContent(station.getLineName().concat(station.getStationName()));
//        userTaskService.add(param);
//
//        return Result.ok();
//    }
}
