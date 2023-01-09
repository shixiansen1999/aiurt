package com.aiurt.modules.common.service;

import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;

import java.util.List;

/**
 * @author fgw
 * @date 2022-09-19
 */
public interface ICommonService {

    /**
     *根据机构人员树
     *
     * @param orgIds 机构id
     * @param ignoreUserId 忽略的用户id
     * @param majorId 专业id
     * @return
     */
    public List<SelectTable> queryDepartUserTree(List<String> orgIds, String ignoreUserId,String majorId);

    /**
     * 查询设备
     * @param deviceDTO
     * @return
     */
    List<SelectTable> queryDevice(DeviceDTO deviceDTO);
}
