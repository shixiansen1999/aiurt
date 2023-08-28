package com.aiurt.modules.train.mistakes.service;

import com.aiurt.modules.train.mistakes.dto.other.QuestionDetailDTO;
import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesReqDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesAppDetailRespDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesRespDTO;
import com.aiurt.modules.train.mistakes.entity.BdExamMistakes;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 错题集的service
 *
 * @author 华宜威
 * @date 2023-08-25 08:51:49
 */
public interface IBdExamMistakesService extends IService<BdExamMistakes> {

    /**
     * 根据考试记录的id生成一条错题集数据
     *
     * @param examRecordId 考试记录的id，也就是bd_exam_record表的id
     */
    void generateMistakesByExamRecodeId(String examRecordId);

    /**
     * 错题集，分页列表查询
     *
     * @param bdExamMistakesReqDTO 错题集接口的列表请求DTO
     * @return
     */
    IPage<BdExamMistakesRespDTO> pageList(BdExamMistakesReqDTO bdExamMistakesReqDTO);

    /**
     * 错题集，根据错题集id获取要审核的内容，主要是工班长进行审核
     *
     * @param id 错题集id
     * @return
     */
    List<QuestionDetailDTO> getReviewById(String id);

    /**
     * 工班长审核错题集
     *
     * @param id 错题集id
     * @param isPass 是否通过，1通过 0驳回(其他也是驳回，不等于1就驳回)
     */
    void auditById(String id, Integer isPass);

    /**
     * app端，查看错题集详情
     *
     * @param id 错题集id
     * @param examRecordId 考生答题记录id
     * @return
     */
    BdExamMistakesAppDetailRespDTO getAppMistakesDetail(String id, String examRecordId);
}
