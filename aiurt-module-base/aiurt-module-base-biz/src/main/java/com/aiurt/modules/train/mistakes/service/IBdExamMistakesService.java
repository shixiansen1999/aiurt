package com.aiurt.modules.train.mistakes.service;

import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesReqDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesRespDTO;
import com.aiurt.modules.train.mistakes.entity.BdExamMistakes;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
