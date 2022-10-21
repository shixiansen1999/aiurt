package com.aiurt.modules.train.task.service;


import com.aiurt.modules.train.task.entity.BdTrainPlan;
import com.aiurt.modules.train.task.vo.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @Description: 年计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
public interface IBdTrainPlanService extends IService<BdTrainPlan> {


    /**
     * 发布
     * @param id
     */
    void publish(String id);

    /**
     * 考试结果发布
     * @param reCheckVOList
     */
    void reCheckPublish(List<ReCheckVO> reCheckVOList);

    /**
     *报表管理
     * @param page
     * @param reportReqVO
     * @return
     */
    IPage<ReportVO> report(Page<ReportVO> page, ReportReqVO reportReqVO);

    /**
     *查询
     * @param reportReqVO
     * @return
     */
    TitleReportVo reportTitle(ReportReqVO reportReqVO);

    /**
     * 培训报表导出
     * @param request
     * @param reportReqVO
     * @return
     */
    ModelAndView reportExport(HttpServletRequest request, ReportReqVO reportReqVO);

    /**
     * 培训复核管理
     * @param page
     * @param reCheckReqVo
     * @return
     */
    IPage<ReCheckVO> getReCheckList(Page<ReCheckVO> page, ReCheckReqVo reCheckReqVo);

    /**
     * 培训年计划导入
     * @param request
     * @throws IOException
     * @return
     */
    BdTrainPlan yearPlanImport(HttpServletRequest request) throws IOException;

    /**
     * 培训年计划保存
     * @param bdTrainPlan
     * @return
     */
    Result<?> yearPlanSave(BdTrainPlan bdTrainPlan);

    /**
     * 培训复核管理-复核
     * @param id
     * @return
     */
    List<QuestionReCheckVO> reCheck(String id);

    /**
     * 提交审核结果
     * @param reqList
     */
    void submitReCheck(List<ShortQuesReqVo> reqList);

    /**
     * 培训报表管理-部门下拉数据
     * @return
     */
    List<String> getDept();
}
