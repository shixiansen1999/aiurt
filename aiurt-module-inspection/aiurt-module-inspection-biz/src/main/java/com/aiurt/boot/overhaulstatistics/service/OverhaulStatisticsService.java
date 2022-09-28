package com.aiurt.boot.overhaulstatistics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    public Page<OverhaulStatisticsDTO> getOverhaulList(Page<OverhaulStatisticsDTO> pageList,OverhaulStatisticsDTO condition) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //管理负责人组织机构编码
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(sysUser.getId());
        List<String> collect1 = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        if(CollUtil.isEmpty(collect1))
        {
            condition.setOrgCode("null");
        }
        else
        {
            condition.setOrgCodeList(collect1);
        }

        //查询管理负责人检修班组的信息
        List<OverhaulStatisticsDTO> statisticsDTOList = repairTaskMapper.readTeamList(pageList,condition);

        //查询班组下所有人员
        List<OverhaulStatisticsDTO> dtoList1 = repairTaskMapper.realNameList(condition);

        //查询班组下检修人员
        List<OverhaulStatisticsDTO> nameList = repairTaskMapper.readNameList(condition);
        nameList.addAll(dtoList1);

        //去重处理
        ArrayList<OverhaulStatisticsDTO> distinct = CollectionUtil.distinct(nameList);

        if (CollectionUtil.isNotEmpty(distinct)){
            distinct.forEach(q->{
                OverhaulStatisticsDTO overhaulStatisticsDTO = new OverhaulStatisticsDTO();

                if (q.getUserId()!=null){
                    //姓名
                    String userId = q.getUserId();
                    q.setUserName(repairTaskMapper.getRealName(userId));
                    //班组编码
                    String orgCode = repairTaskMapper.getOrgCode(userId);
                    String id = q.getId();
                    q.setOrgCodeId(orgCode+id);
                    q.setOrgCode(orgCode);
                }

                //查询已完成的班组信息
                overhaulStatisticsDTO.setStatus(8L);
                if (q.getTaskId()!=null){
                    overhaulStatisticsDTO.setTaskId(q.getTaskId());
                }  if (q.getOrgCode()!=null){
                    overhaulStatisticsDTO.setOrgCode(q.getOrgCode());
                }if (q.getUserId()!=null){
                    overhaulStatisticsDTO.setUserId(q.getUserId());
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
        if (CollectionUtil.isNotEmpty(statisticsDTOList)){
            OverhaulStatisticsDTO overhaulStatisticsDTO = new OverhaulStatisticsDTO();
            statisticsDTOList.forEach(e->{
                //查询已完成的班组信息
                overhaulStatisticsDTO.setStatus(8L);
                if (e.getTaskId()!=null){
                    overhaulStatisticsDTO.setTaskId(e.getTaskId());
                }  if (e.getOrgCode()!=null){
                    overhaulStatisticsDTO.setOrgCode(e.getOrgCode());
                }
                List<OverhaulStatisticsDTO> dtoList = repairTaskMapper.readTeamLists(overhaulStatisticsDTO);

                //已完成数
                int size2 = dtoList.size();
                e.setCompletedNumber(Integer.valueOf(size2).longValue());

                //未完成数
                long l = e.getTaskTotal()-Integer.valueOf(size2).longValue();
                e.setNotCompletedNumber(l);

                //班组名称
                e.setOrgName(manager.translateOrg(Arrays.asList(e.getOrgCode())));

                //完成率
                getCompletionRate(e, size2);

                //异常数量
                List<Integer> status = repairTaskMapper.getStatus(e.getTaskId());
                long count = CollUtil.isNotEmpty(status) ? status.stream().filter(InspectionConstant.NO_RESULT_STATUS::equals).count() : 0L;
                e.setAbnormalNumber(count);

                //人员是否属于该班组
                List<OverhaulStatisticsDTO> collect = distinct.stream().filter(y -> y.getOrgCode().equals(e.getOrgCode())).collect(Collectors.toList());
                e.setNameList(collect);

                //父级编码id
                if (e.getOrgCode()!=null){
                    e.setOrgCodeId(e.getOrgCode());
                }
            });
        }
        return pageList.setRecords(statisticsDTOList);
    }

    private void getCompletionRate(OverhaulStatisticsDTO e, int size2) {
        if (size2!=0 && e.getTaskTotal()!=null){
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
        }
    }

    /**
     * 统计分析-检修报表导出
     *
     * @param request
     * @return
     */
    public ModelAndView reportExport(HttpServletRequest request, OverhaulStatisticsDTO overhaulStatisticsDTO) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

        Page<OverhaulStatisticsDTO> page = new Page<>(overhaulStatisticsDTO.getPageNo(), overhaulStatisticsDTO.getPageSize());
        Page<OverhaulStatisticsDTO> overhaulList = this.getOverhaulList(page, overhaulStatisticsDTO);
        List<OverhaulStatisticsDTO> records = overhaulList.getRecords();
        List<OverhaulStatisticsDTO> dtos = new ArrayList<>();
        for (OverhaulStatisticsDTO statisticsDTO : records) {
            dtos.add(statisticsDTO);
           List<OverhaulStatisticsDTO> nameList = statisticsDTO.getNameList();
            if (CollUtil.isNotEmpty(nameList)) {
                dtos.addAll(nameList);
            }
        }
        if (CollectionUtil.isNotEmpty(records)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "检修报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, OverhaulStatisticsDTO.class);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-检修报表", "检修报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, dtos);
        }
        return mv;
    }

}
