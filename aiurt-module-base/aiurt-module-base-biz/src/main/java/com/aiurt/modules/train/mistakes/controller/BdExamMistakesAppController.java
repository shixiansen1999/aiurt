package com.aiurt.modules.train.mistakes.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesAppSubmitReqDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesAppDetailRespDTO;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 错题集 app接口
 *
 * @author 华宜威
 * @date 2023-08-28 09:18:01
 */
@Api(tags = "培训管理-错题集app接口")
@RestController
@RequestMapping("/bdExamMistakes/app")
public class BdExamMistakesAppController {

    @Autowired
    private IBdExamMistakesService examMistakesService;

    /**
     * 培训管理-错题集-app获取详情
     *
     * @param id
     * @param examRecordId
     * @param isGetError 是否只获取错题，1是0否
     * @return
     */
    @AutoLog(value = "培训管理-错题集-app获取详情")
    @ApiOperation(value="培训管理-错题集-app获取详情", notes="培训管理-错题集-app获取详情")
    @GetMapping("/getAppMistakesDetail")
    public Result<BdExamMistakesAppDetailRespDTO> getAppMistakesDetail(@RequestParam(value = "id", required = true) String id,
                                                                       @RequestParam(value = "examRecordId", required = false) String examRecordId,
                                                                       @RequestParam(value = "isGetError", required = false) Integer isGetError){
        BdExamMistakesAppDetailRespDTO bdExamMistakesAppDetailRespDTO = examMistakesService.getAppMistakesDetail(id, examRecordId, isGetError);
        return Result.ok(bdExamMistakesAppDetailRespDTO);
    }

    /**
     * app提交错题
     * @param bdExamMistakesAppSubmitReqDTO 错题集-app端填写错题后提交的请求DTO
     * @return
     */
    @AutoLog(value = "培训管理-错题集-app提交错题")
    @ApiOperation(value="培训管理-错题集-app提交错题", notes="培训管理-错题集-app提交错题")
    @PostMapping(value = "/submit")
    public Result<String> submit (@RequestBody BdExamMistakesAppSubmitReqDTO bdExamMistakesAppSubmitReqDTO){
        examMistakesService.submit(bdExamMistakesAppSubmitReqDTO);
        return  Result.OK("提交成功!");
    }
}
