package com.aiurt.modules.search.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.search.service.ISearchRecordsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import search.dto.HotKeywordResDTO;
import search.dto.SearchRecordResDTO;
import search.entity.SearchRecords;

import java.util.List;

/**
 * @author
 * @description 故障管理-搜索记录和热搜
 */
@Slf4j
@Api(tags = "故障管理-搜索记录和热搜")
@RestController
@RequestMapping("/searchrecords")
public class SearchRecordsController extends BaseController<SearchRecords, ISearchRecordsService> {


    @Autowired
    private ISearchRecordsService searchRecordsService;

    /**
     * 搜索记录
     */
    @ApiOperation(value = "搜索记录(最多返回5条)", notes = "搜索记录(最多返回5条)")
    @GetMapping(value = "/list")
    public Result<List<SearchRecordResDTO>> searchRecords() {
        List<SearchRecordResDTO> searchRecords = searchRecordsService.searchRecords();
        return Result.OK(searchRecords);
    }

    /**
     * 热门关键词
     */
    @ApiOperation(value = "热门关键词(最多返回5条)", notes = "热门关键词(最多返回5条)")
    @GetMapping(value = "/keyword")
    public Result<List<HotKeywordResDTO>> hotKeyword() {
        List<HotKeywordResDTO> hotKeywords = searchRecordsService.hotKeyword();
        return Result.OK(hotKeywords);
    }

    /**
     * 智能助手热门关键词
     */
    @ApiOperation(value = "智能助手热门关键词", notes = "智能助手热门关键词")
    @GetMapping(value = "/cutehand/keyword")
    public Result<IPage<HotKeywordResDTO>> cuteHandHotKeyword(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize) {
        IPage<HotKeywordResDTO> pageList = searchRecordsService.cuteHandHotKeyword(pageNo, pageSize);
        return Result.OK(pageList);
    }

}
