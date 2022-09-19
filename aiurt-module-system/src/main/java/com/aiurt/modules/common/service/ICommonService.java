package com.aiurt.modules.common.service;

import com.aiurt.modules.common.entity.SelectTable;

import java.util.List;

/**
 * @author fgw
 * @date 2022-09-19
 */
public interface ICommonService {

    /**
     *
     * @param orgIds 机构id
     * @param ignoreUserId 忽略的用户id
     * @return
     */
    public List<SelectTable> queryDepartUserTree(List<String> orgIds, String ignoreUserId);
}
