package com.aiurt.modules.common.service;

import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
    public List<SelectTable> queryDepartUserTree(List<String> orgIds, String ignoreUserId,String majorId,List<String> keys, List<String> values, Boolean isSelectOrg);

    /**
     * 查询设备
     * @param deviceDTO
     * @return
     */
    List<SelectTable> queryDevice(DeviceDTO deviceDTO);

    /**
     * 异步加载树形结构
     * @param name
     * @param pid
     * @return
     */
    List<SelectTable> queryPositionTreeAsync(String name, String pid, String queryAll);

    /**
     * 分页查询设备
     * @param deviceDTO
     * @return
     */
    IPage<Device> queryPageDevice(DeviceDTO deviceDTO);

    /**
     * 线路站点
     * @param name
     * @param queryAll
     * @return
     */
    List<SelectTable> queryStationTree(String name, String queryAll);
}
