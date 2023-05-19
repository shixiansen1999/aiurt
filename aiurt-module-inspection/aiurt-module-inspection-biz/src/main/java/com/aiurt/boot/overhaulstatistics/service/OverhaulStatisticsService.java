package com.aiurt.boot.overhaulstatistics.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.personnelteam.mapper.PersonnelTeamMapper;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTOS;
import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.stream.Collectors;

/**
 * @author zwl
 * @Title:
 * @Description: 检修统计分析业务层
 * @date 2022/9/2011:14
 */
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
        if(StrUtil.isEmpty(condition.getOrgCode()))
        {
            if (CollUtil.isNotEmpty(dtoList2)) {
                List<String> collect1 = dtoList2.stream().map(OverhaulStatisticsDTOS::getOrgCode).collect(Collectors.toList());
                condition.setOrgCodeList(collect1);
            } else {
                return pageList;
            }
        }

        Page<OverhaulStatisticsDTOS> allTaskList = repairTaskMapper.getAllTaskList(pageList, condition);
        List<OverhaulStatisticsDTOS> records = allTaskList.getRecords();
        //查询管理负责人检修班组的信息
        List<String> collect1 = records.stream().map(OverhaulStatisticsDTOS::getOrgCode).collect(Collectors.toList());
        condition.setOrgCodeList(collect1);
        List<OverhaulStatisticsDTOS> statisticsDTOList = repairTaskMapper.readTeamList(condition);

        //查询班组下所有人员
        List<OverhaulStatisticsDTO> dtoList1 = repairTaskMapper.realNameList(condition);

        //查询班组下检修人员
        List<OverhaulStatisticsDTO> nameList = repairTaskMapper.readNameList(condition);

        if(CollectionUtil.isNotEmpty(statisticsDTOList)){
            for (OverhaulStatisticsDTOS statisticsDTOS : records) {
                OverhaulStatisticsDTOS dtos = statisticsDTOList.stream().filter(s -> s.getOrgCode().equals(statisticsDTOS.getOrgCode())).findFirst().orElse(new OverhaulStatisticsDTOS());
                if (ObjectUtil.isNotEmpty(dtos.getId())) {
                    BeanUtil.copyProperties(dtos,statisticsDTOS);
                }
            }
        }
        if(CollectionUtil.isNotEmpty(nameList)) {
            nameList.addAll(dtoList1);
        }
        //去重处理
        //ArrayList<OverhaulStatisticsDTOS> distinct1 = CollectionUtil.distinct(CollectionUtil.isNotEmpty(statisticsDTOList) ? statisticsDTOList : dtoList2);

        //去重处理
        ArrayList<OverhaulStatisticsDTO> distinct = CollectionUtil.distinct(CollectionUtil.isNotEmpty(nameList) ? nameList : dtoList1);

        if (CollectionUtil.isNotEmpty(distinct)){
            distinct.forEach(q->{
                OverhaulStatisticsDTO overhaulStatisticsDTO = new OverhaulStatisticsDTO();

                if (q.getUserId()!=null){
                    //姓名
                    String userId = q.getUserId();
                    q.setUserName(repairTaskMapper.getRealName(userId));
                    //班组编码
                    String orgCode = repairTaskMapper.getOrgCode(userId);
                    q.setOrgCodeId(orgCode+userId);
                    q.setOrgCode(orgCode);
                }

                //查询已完成的人员信息
                overhaulStatisticsDTO.setStatus(8L);
                if (q.getOrgCode()!=null){
                    overhaulStatisticsDTO.setOrgCode(q.getOrgCode());
                }if (q.getUserId()!=null){
                    overhaulStatisticsDTO.setUserId(q.getUserId());
                }if (condition.getStartDate()!=null){
                    overhaulStatisticsDTO.setStartDate(condition.getStartDate());
                }if (condition.getEndDate()!=null){
                    overhaulStatisticsDTO.setEndDate(condition.getEndDate());
                }
                List<OverhaulStatisticsDTO> readNameList = repairTaskMapper.readNameLists(overhaulStatisticsDTO);

                //已完成数
                int size5 = readNameList.size();
                q.setCompletedNumber(Integer.valueOf(size5).longValue());

                if (q.getTaskTotal()!=null){
                    //未完成数
                    long l = q.getTaskTotal()-Integer.valueOf(size5).longValue();
                    q.setNotCompletedNumber(l);
                }else {
                    q.setNotCompletedNumber(0L);
                    q.setTaskTotal(0L);
                }
                PersonnelTeamDTO userPeerTime = personnelTeamMapper.getUserPeerTime(StrUtil.isNotEmpty(q.getUserId()) ? q.getUserId() : null, condition.getStartDate(), condition.getEndDate());

                    if (q.getMaintenanceDuration()!=0.0f && userPeerTime.getCounter()!=null){
                        q.setMaintenanceDuration(q.getMaintenanceDuration()+(float) userPeerTime.getCounter());
                    }else {
                        q.setMaintenanceDuration(0.0f);
                    }
                    //完成率
                    getCompletionRate(q, size5);

                //异常数量
                if (q.getTaskId()!=null) {
                    List<Integer> status1 = repairTaskMapper.getStatus(q.getTaskId());
                    long count = CollUtil.isNotEmpty(status1) ? status1.stream().filter(InspectionConstant.NO_RESULT_STATUS::equals).count() : 0L;
                    q.setAbnormalNumber(count);
                }else {
                    q.setAbnormalNumber(0L);
                }
            });
        }
        if (CollectionUtil.isNotEmpty(records)){
            OverhaulStatisticsDTOS overhaulStatisticsDTO = new OverhaulStatisticsDTOS();
            records.forEach(e->{
                //查询已完成的班组信息
                overhaulStatisticsDTO.setStatus(8L);
                if (e.getTaskId()!=null){
                    overhaulStatisticsDTO.setTaskId(e.getTaskId());
                }  if (e.getOrgCode()!=null){
                    overhaulStatisticsDTO.setOrgCode(e.getOrgCode());
                }if (condition.getStartDate()!=null){
                    overhaulStatisticsDTO.setStartDate(condition.getStartDate());
                }if (condition.getEndDate()!=null){
                    overhaulStatisticsDTO.setEndDate(condition.getEndDate());
                }
                List<OverhaulStatisticsDTOS> dtoList = repairTaskMapper.readTeamLists(overhaulStatisticsDTO);
                //查询管理负责人检修班组本周内的任务总数
                Long tasksList = repairTaskMapper.readTaskList(overhaulStatisticsDTO);
                e.setTaskTotal(tasksList);
                //已完成数
                int size2 = dtoList.size();
                e.setCompletedNumber(Integer.valueOf(size2).longValue());

                //未完成数
                if (e.getTaskTotal()!=null){
                    long l = e.getTaskTotal()-Integer.valueOf(size2).longValue();
                    e.setNotCompletedNumber(l);
                }else {
                    e.setNotCompletedNumber(0L);
                    e.setTaskTotal(0L);
                }
                if (e.getMaintenanceDuration()==0.0f){
                    e.setMaintenanceDuration(0.0f);
                }
                //完成率
                getCompletionRate(e, size2);

                //异常数量
                if (e.getTaskId()!=null) {
                    List<Integer> status = repairTaskMapper.getStatus(e.getTaskId());
                    long count = CollUtil.isNotEmpty(status) ? status.stream().filter(InspectionConstant.NO_RESULT_STATUS::equals).count() : 0L;
                    e.setAbnormalNumber(count);
                }else {
                    e.setAbnormalNumber(0L);
                }
                //人员是否属于该班组
                List<OverhaulStatisticsDTO> collect = distinct.stream().filter(p->StrUtil.isNotBlank(p.getOrgCode())).filter(y -> y.getOrgCode().equals(e.getOrgCode())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)){
                    e.setNameList(collect);
                }
                List<OverhaulStatisticsDTO> nameList1 = e.getNameList();
                if (CollUtil.isNotEmpty(nameList1)){
                List<String> collect2 = nameList1.stream().map(OverhaulStatisticsDTO::getUserId).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect2)){
                    //获取所有检修任务人员总工时和所有同行人总工时
                    List<PersonnelTeamDTO> teamTime = personnelTeamMapper.getTeamTime(collect2,condition.getStartDate() , condition.getEndDate());
                    List<PersonnelTeamDTO> teamPeerTime = personnelTeamMapper.getTeamPeerTime(collect2, condition.getStartDate(), condition.getEndDate());
                    List<String> collect5 = teamTime.stream().map(PersonnelTeamDTO::getTaskId).collect(Collectors.toList());
                    //若同行人和指派人同属一个班组，则该班组只取一次工时，不能累加
                    List<PersonnelTeamDTO> dtos = teamPeerTime.stream().filter(t -> !collect5.contains(t.getTaskId())).collect(Collectors.toList());
                    dtos.addAll(teamTime);
                    BigDecimal sum = new BigDecimal("0.00");
                    if (CollUtil.isNotEmpty(dtos)) {
                        for (PersonnelTeamDTO dto : dtos) {
                            if (ObjectUtil.isNotEmpty(dto.getInspecitonTotalTime())) {
                                sum = sum.add(dto.getInspecitonTotalTime());
                            }
                        }
                    }
                    //秒转时
                    BigDecimal decimal = sum.divide(new BigDecimal("3600"),1, BigDecimal.ROUND_HALF_UP);
                    e.setMaintenanceDuration(decimal.longValue());
                }else {
                    e.setMaintenanceDuration(0L);
                }
                }else {
                    e.setMaintenanceDuration(0L);
                }

                //父级编码id
                if (e.getOrgCode()!=null){
                    e.setOrgCodeId(e.getOrgCode());
                    //班组名称
                    e.setOrgName(manager.translateOrg(Arrays.asList(e.getOrgCode())));
                }
            });
        }
        return allTaskList;
    }

    private void getCompletionRate(OverhaulStatisticsDTO e, int size2) {
        if (size2!=0 && e.getTaskTotal()!=0){
            double div = NumberUtil.div(size2, e.getTaskTotal().longValue());
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
    private void getCompletionRate(OverhaulStatisticsDTOS e, int size2) {
        if (size2!=0 && e.getTaskTotal()!=0){
            double div = NumberUtil.div(size2, e.getTaskTotal().longValue());
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
