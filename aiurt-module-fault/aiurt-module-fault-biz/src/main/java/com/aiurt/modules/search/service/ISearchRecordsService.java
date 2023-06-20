package com.aiurt.modules.search.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import search.dto.HotKeywordResDTO;
import search.dto.SearchRecordResDTO;
import search.entity.SearchRecords;

import java.util.List;

/**
 * @author
 * @description
 */
public interface ISearchRecordsService extends IService<SearchRecords> {

    /**
     * 搜索记录
     *
     * @return
     */
    List<SearchRecordResDTO> searchRecords();

    /**
     * 热门关键词
     *
     * @return
     */
    List<HotKeywordResDTO> hotKeyword();

    /**
     * 智能助手热门关键词
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<HotKeywordResDTO> cuteHandHotKeyword(Integer pageNo, Integer pageSize);
}
