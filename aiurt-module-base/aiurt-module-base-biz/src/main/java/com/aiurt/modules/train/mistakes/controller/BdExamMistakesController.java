package com.aiurt.modules.train.mistakes.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.train.mistakes.dto.other.QuestionDetailDTO;
import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesReqDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesRespDTO;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错题集接口
 *
 * @author 华宜威
 * @date 2023-08-25 09:08:37
 */
@RestController
@RequestMapping("/bdExamMistakes")
public class BdExamMistakesController {

    @Autowired
    private IBdExamMistakesService examMistakesService;

    /**
     * 错题集 分页列表查询
     * @param bdExamMistakesReqDTO 错题集接口的列表请求DTO
     * @return
     */
    @AutoLog(value = "培训管理-错题集-分页列表查询")
    @ApiOperation(value="培训管理-错题集-分页列表查询", notes="培训管理-错题集-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<BdExamMistakesRespDTO>> queryPageList(BdExamMistakesReqDTO bdExamMistakesReqDTO){
        IPage<BdExamMistakesRespDTO> pageList = examMistakesService.pageList(bdExamMistakesReqDTO);
        return Result.ok(pageList);
    }

    /**
     * 错题集，根据错题集id获取要审核的内容，主要是工班长进行审核
     * @param id 错题集id
     * @return
     */
    @GetMapping("/getReviewById")
    public Result<List<QuestionDetailDTO>> getReviewById(@RequestParam(value = "id") String id){
        List<QuestionDetailDTO> list = examMistakesService.getReviewById(id);
        return Result.ok(list);
    }

}
