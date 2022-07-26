package com.aiurt.modules.train.eaxm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.train.exam.entity.BdExamPaper;

/**
 * @Description: 试卷库表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdExamPaperService extends IService<BdExamPaper> {
    /**
     * 培训题库详情
     * @param id
     * @return
     */
    BdExamPaper questionDetail(String id);
    /**
     * 培训题库-列表查看（其他模板用到）
     * @param pageList
     * @param bdExamPaper
     * @param userId
     * @param examClassify
     * @return
     */
    Page<BdExamPaper> queryPageList(Page<BdExamPaper> pageList,BdExamPaper bdExamPaper,String userId,String examClassify);

    /**
     * 添加试卷
     * @param bdExamPaper
     * @return
     */
    BdExamPaper addDetail(BdExamPaper bdExamPaper);

    /**
     * 试卷作废
     * @param id
     */
    void updateState(String id);

    /**
     * 培训题库-列表
     * @param pageList
     * @param bdExamPaper
     * @return
     */
    Page<BdExamPaper> examPaperList(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper);

    /**
     * 培训题库-打印
     * @param pageList
     * @param bdExamPaper
     * @return
     */
    Page<BdExamPaper> examPaperPrintList(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper);

    /**
     * 培训题库详情
     * @param pageList
     * @param bdExamPaper
     * @return
     */
    Page<BdExamPaper> examPaperDetail(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper);

    /**
     * app-查看考试详情
     * @param pageList
     * @param bdExamPaper
     * @return
     */
    Page<BdExamPaper> getStudentAppExamRecord(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper);
}
