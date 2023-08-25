package com.aiurt.modules.train.mistakes.mapper;

import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesReqDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesRespDTO;
import com.aiurt.modules.train.mistakes.entity.BdExamMistakes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 错题集的mapper
 *
 * @author 华宜威
 * @date 2023-08-25 08:47:27
 */
public interface BdExamMistakesMapper extends BaseMapper<BdExamMistakes> {

    /**
     * 错题集 分页列表查询
     * @param page 分页查询数据的page对象
     * @param bdExamMistakesReqDTO 错题集接口的列表请求DTO
     * @return
     */
    Page<BdExamMistakesRespDTO> pageList(@Param("page") Page<BdExamMistakesRespDTO> page,
                                         @Param("bdExamMistakesReqDTO") BdExamMistakesReqDTO bdExamMistakesReqDTO);
}
