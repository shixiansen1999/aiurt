package com.aiurt.boot.overhaulstatistics.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.personnelteam.mapper.PersonnelTeamMapper;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTOS;
import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zwl
 * @Title:
 * @Description: 检修统计分析业务层
 * @date 2022/9/2011:14
 */
@Slf4j
@Service
public class OverhaulStatisticsService{

    @Autowired
    private RepairTaskMapper repairTaskMapper;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Resource
    private InspectionManager manager;

    @Autowired
    private PersonnelTeamMapper personnelTeamMapper;


    public Page<OverhaulStatisticsDTOS> getOverhaulList(Page<OverhaulStatisticsDTOS> pageList, OverhaulStatisticsDTOS condition) {
        List<OverhaulStatisticsDTOS> dtoList2 = this.selectDepart(condition.getOrgCode());
        if (CollUtil.isNotEmpty(dtoList2)) {
            List<String> collect1 = dtoList2.stream().map(OverhaulStatisticsDTOS::getOrgCode).collect(Collectors.toList());
            condition.setOrgCodeList(collect1);
        } else {
            return pageList;
        }

        Page<OverhaulStatisticsDTOS> allTaskList = repairTaskMapper.getAllTaskList(pageList, condition);
        List<OverhaulStatisticsDTOS> records = allTaskList.getRecords();
        //查询管理负责人检修班组的信息
        List<String> collect1 = records.stream().map(OverhaulStatisticsDTOS::getOrgCode).collect(Collectors.toList());
        condition.setOrgCodeList(collect1);

        List<OverhaulStatisticsDTOS> statisticsDTOList = repairTaskMapper.countTeamList(condition);

        //查询班组下所有人员
        List<OverhaulStatisticsDTO> dtoList1 = repairTaskMapper.realNameList(condition);

        //查询班组下检修人员
        List<OverhaulStatisticsDTO> nameList = repairTaskMapper.countUserList(condition);

        if(CollectionUtil.isNotEmpty(statisticsDTOList)){
            for (OverhaulStatisticsDTOS statisticsDTOS : records) {
                OverhaulStatisticsDTOS dtos = statisticsDTOList.stream().filter(s -> s.getOrgCode().equals(statisticsDTOS.getOrgCode())).findFirst().orElse(new OverhaulStatisticsDTOS());
                if (ObjectUtil.isNotEmpty(dtos)) {
                    BeanUtil.copyProperties(dtos,statisticsDTOS);
                }
            }
        }
        if(CollectionUtil.isNotEmpty(nameList)) {
            dtoList1.removeAll(nameList);
            nameList.addAll(dtoList1);
        }

        countOverhaulStatisticList(condition, nameList);
        buildStatistic(condition, records, nameList);
        return allTaskList;
    }

    private void countOverhaulStatisticList(OverhaulStatisticsDTOS condition, List<OverhaulStatisticsDTO> nameList) {
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        if (CollectionUtil.isNotEmpty(nameList)){
            nameList.forEach(q->{
                threadPoolExecutor.execute(() -> {
                    countOverhaulStatistic(condition, q);
                });
            });
        }
        threadPoolExecutor.shutdown();
        try {
            // 等待线程池中的任务全部完成
            threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 处理中断异常
            log.info("循环方法的线程中断异常", e.getMessage());
        }
    }

    private void countOverhaulStatistic(OverhaulStatisticsDTOS condition, OverhaulStatisticsDTO q) {


        if (q.getUserId()!=null){
            //姓名
            String userId = q.getUserId();
            q.setUserName(repairTaskMapper.getRealName(userId));
            //班组编码
            String orgCode = repairTaskMapper.getOrgCode(userId);
            q.setOrgCodeId(orgCode+userId);
            q.setOrgCode(orgCode);
        }

        if (q.getTaskTotal()!=null){
            //未完成数
            long l = q.getTaskTotal()-q.getCompletedNumber();
            q.setNotCompletedNumber(l);
        }else {
            q.setNotCompletedNumber(0L);
            q.setTaskTotal(0L);
        }
        //作为同行人的工时
        PersonnelTeamDTO userPeerTime = personnelTeamMapper.getUserPeerTime(StrUtil.isNotEmpty(q.getUserId()) ? q.getUserId() : null, condition.getStartDate(), condition.getEndDate());
        BigDecimal time1 = q.getMaintenanceDuration() != null ? q.getMaintenanceDuration() : new BigDecimal(0);
        BigDecimal time2 = userPeerTime.getCounter() != null ? userPeerTime.getInspecitonTotalTime() : new BigDecimal(0);
        BigDecimal sum = time1.add(time2);
        BigDecimal bigDecimal = sum.divide(new BigDecimal(3600), 2, BigDecimal.ROUND_HALF_UP);
        q.setMaintenanceDuration(bigDecimal);
        //完成率
        getCompletionRate(q);

    }

    private void buildStatistic(OverhaulStatisticsDTOS condition, List<OverhaulStatisticsDTOS> records, List<OverhaulStatisticsDTO> nameList) {
        if (CollectionUtil.isNotEmpty(records)){

            records.forEach(e->{
                //未完成数
                if (e.getTaskTotal()!=null){
                    long l = e.getTaskTotal()-e.getCompletedNumber();
                    e.setNotCompletedNumber(l);
                }else {
                    e.setNotCompletedNumber(0L);
                    e.setTaskTotal(0L);
                }
                if (e.getMaintenanceDuration()==null){
                    e.setMaintenanceDuration(new BigDecimal(0));
                }
                //完成率
                getCompletionRate(e);

                //找出该班组的人员
                List<OverhaulStatisticsDTO> collect = nameList.stream().filter(p->StrUtil.isNotBlank(p.getOrgCode())).filter(y -> y.getOrgCode().equals(e.getOrgCode())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)){
                    e.setNameList(collect);
                    List<String> collect2 = collect.stream().map(OverhaulStatisticsDTO::getUserId).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(collect2)){
                        //班组总工时等于所有人员工时累加
                        BigDecimal reduce = collect.stream().map(OverhaulStatisticsDTO::getMaintenanceDuration).reduce(BigDecimal.ZERO, BigDecimal::add);
                        e.setMaintenanceDuration(reduce);
                    }else {
                        e.setMaintenanceDuration(new BigDecimal(0));
                    }

                }
                //父级编码id
                if (e.getOrgCode()!=null){
                    e.setOrgCodeId(e.getOrgCode());
                    //班组名称
                    e.setOrgName(manager.translateOrg(Arrays.asList(e.getOrgCode())));
                }
            });
        }
    }

    private void getCompletionRate(OverhaulStatisticsDTO e) {
        if (e.getCompletedNumber() != null && e.getTaskTotal() != null && e.getTaskTotal() != 0) {
            double div = NumberUtil.div(e.getCompletedNumber().longValue(), e.getTaskTotal().longValue());
            double i = div * 100;
            if (i == 0) {
                e.setCompletionRate("0");
            } else {
                String string = NumberUtil.round(i, 2).toString();
                e.setCompletionRate(string);
            }
        } else {
            e.setCompletionRate("0");
            e.setCompletedNumber(0L);
        }
    }
    private void getCompletionRate(OverhaulStatisticsDTOS e) {
        if (e.getCompletedNumber() != null && e.getTaskTotal() != null && e.getTaskTotal() != 0){
            double div = NumberUtil.div(e.getCompletedNumber().longValue(), e.getTaskTotal().longValue());
            double i = div*100;
            if (i==0){
                e.setCompletionRate("0");
            }else {
                String string = NumberUtil.round(i, 2).toString();
                e.setCompletionRate(string);
            }
        }else {
            e.setCompletionRate("0");
            e.setCompletedNumber(0L);
        }
    }

    public List<OverhaulStatisticsDTOS> selectDepart (String orgCode) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据当前登录人班组权限获取班组,管理员获取全部
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        List<OverhaulStatisticsDTOS> list = new ArrayList<>();
        if (!admin) {
            List<CsUserDepartModel>  departByUserId = sysBaseAPI.getDepartByUserId(sysUser.getId());
            if (CollUtil.isNotEmpty(departByUserId)) {
                for (CsUserDepartModel csUserDepartModel : departByUserId) {
                    OverhaulStatisticsDTOS overhaulStatisticsDTOS = new OverhaulStatisticsDTOS();
                    overhaulStatisticsDTOS.setOrgId(csUserDepartModel.getDepartId());
                    overhaulStatisticsDTOS.setOrgCode(csUserDepartModel.getOrgCode());
                    overhaulStatisticsDTOS.setOrgName(csUserDepartModel.getDepartName());
                    if (StrUtil.isNotEmpty(orgCode) && csUserDepartModel.getOrgCode().equals(orgCode)) {
                        List<OverhaulStatisticsDTOS> one = new ArrayList<>();
                        one.add(overhaulStatisticsDTOS);
                        return one;
                    } else {
                        list.add(overhaulStatisticsDTOS);
                    }

                }
            }

        } else {
            List<SysDepartModel> allSysDepart = sysBaseAPI.getAllSysDepart();
            if (CollUtil.isNotEmpty(allSysDepart)) {
                for (SysDepartModel sysDepartModel : allSysDepart) {
                    OverhaulStatisticsDTOS overhaulStatisticsDTOS = new OverhaulStatisticsDTOS();
                    overhaulStatisticsDTOS.setOrgId(sysDepartModel.getId());
                    overhaulStatisticsDTOS.setOrgCode(sysDepartModel.getOrgCode());
                    overhaulStatisticsDTOS.setOrgName(sysDepartModel.getDepartName());
                    if (StrUtil.isNotEmpty(orgCode) && sysDepartModel.getOrgCode().equals(orgCode)) {
                        List<OverhaulStatisticsDTOS> one = new ArrayList<>();
                        one.add(overhaulStatisticsDTOS);
                        return one;
                    } else {
                        list.add(overhaulStatisticsDTOS);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 统计分析-检修报表导出
     *
     * @param request
     * @return
     */
    public ModelAndView reportExport(HttpServletRequest request, OverhaulStatisticsDTOS overhaulStatisticsDTO) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

        Page<OverhaulStatisticsDTOS> page = new Page<>(overhaulStatisticsDTO.getPageNo(), overhaulStatisticsDTO.getPageSize());
        Page<OverhaulStatisticsDTOS> overhaulList = this.getOverhaulList(page, overhaulStatisticsDTO);
        List<OverhaulStatisticsDTOS> records = overhaulList.getRecords();
        List<OverhaulStatisticsDTOS> dtos = new ArrayList<>();

        for (OverhaulStatisticsDTOS statisticsDTO : records) {
            dtos.add(statisticsDTO);
           List<OverhaulStatisticsDTO> nameList = statisticsDTO.getNameList();
           List<OverhaulStatisticsDTOS> dtoNameList = new ArrayList<>();
            for (OverhaulStatisticsDTO dto : nameList) {
                OverhaulStatisticsDTOS overhaulstatisticsdtos = new OverhaulStatisticsDTOS();
                BeanUtil.copyProperties(dto,overhaulstatisticsdtos);
                dtoNameList.add(overhaulstatisticsdtos);
            }
            if (CollUtil.isNotEmpty(dtoNameList)) {
                dtos.addAll(dtoNameList);
            }
        }
        if (CollectionUtil.isNotEmpty(records)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "检修报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, OverhaulStatisticsDTOS.class);
            //自定义导出列表
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS,overhaulStatisticsDTO.getExportParameters());
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-检修报表", "检修报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, dtos);
        }
        return mv;
    }

}
