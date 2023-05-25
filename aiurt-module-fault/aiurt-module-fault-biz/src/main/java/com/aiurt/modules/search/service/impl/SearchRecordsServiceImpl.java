package com.aiurt.modules.search.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.search.mapper.SearchRecordsMapper;
import com.aiurt.modules.search.service.ISearchRecordsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import search.dto.HotKeywordResDTO;
import search.dto.SearchRecordResDTO;
import search.entity.SearchRecords;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @description
 */
@Slf4j
@Service
public class SearchRecordsServiceImpl extends ServiceImpl<SearchRecordsMapper, SearchRecords> implements ISearchRecordsService {

    @Override
    public List<SearchRecordResDTO> searchRecords() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        List<SearchRecords> searchRecords = this.lambdaQuery()
                .eq(SearchRecords::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SearchRecords::getUserId, loginUser.getId())
                .orderByDesc(SearchRecords::getSearchTime)
                .last("limit 5")
                .list();
        List<SearchRecordResDTO> list = new ArrayList<>();
        SearchRecordResDTO searchRecord = null;
        for (SearchRecords record : searchRecords) {
            searchRecord = new SearchRecordResDTO();
            BeanUtils.copyProperties(record, searchRecord);
            list.add(searchRecord);
        }
        return list;
    }

    @Override
    public List<HotKeywordResDTO> hotKeyword() {
        List<SearchRecords> searchRecords = this.lambdaQuery()
                .eq(SearchRecords::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(SearchRecords::getResultCount, SearchRecords::getSearchTime)
                .last("limit 5")
                .list();
        List<HotKeywordResDTO> hotKeywords = new ArrayList<>();
        HotKeywordResDTO hotKeyword = null;
        for (SearchRecords record : searchRecords) {
            hotKeyword = new HotKeywordResDTO();
            BeanUtils.copyProperties(record, hotKeyword);
            hotKeywords.add(hotKeyword);
        }
        return hotKeywords;
    }
}
