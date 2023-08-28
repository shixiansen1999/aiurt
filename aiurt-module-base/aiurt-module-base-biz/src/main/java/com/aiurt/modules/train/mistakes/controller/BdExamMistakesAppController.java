package com.aiurt.modules.train.mistakes.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesAppDetailRespDTO;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @AutoLog(value = "培训管理-错题集-app获取详情")
    @ApiOperation(value="培训管理-错题集-app获取详情", notes="培训管理-错题集-app获取详情")
    @GetMapping("/getAppMistakesDetail")
    public Result<BdExamMistakesAppDetailRespDTO> getAppMistakesDetail(String id, String examRecordId){
        BdExamMistakesAppDetailRespDTO bdExamMistakesAppDetailRespDTO = examMistakesService.getAppMistakesDetail(id, examRecordId);
        return Result.ok(bdExamMistakesAppDetailRespDTO);
    }
}
