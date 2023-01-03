package com.aiurt.boot.strategy.mapper;


import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.strategy.dto.DeviceExcelDTO;
import com.aiurt.boot.strategy.dto.InspectionExcelDTO;
import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.dto.InspectionStrategyExcelDTO;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface InspectionStrategyMapper extends BaseMapper<InspectionStrategy> {
    /**
     * 分页查询
     *
     * @param page
     * @param inspectionStrategyDTO
     * @param orgCodes
     * @param majorCodes
     * @return
     */
    IPage<InspectionStrategyDTO> selectPageList(Page<InspectionStrategyDTO> page,
                                                @Param("inspectionStrategyDTO") InspectionStrategyDTO inspectionStrategyDTO,
                                                @Param("orgCodes") List<String> orgCodes,
                                                @Param("majorCodes") List<String> majorCodes,
                                                @Param("userName") String userName);

    /**
     * 根据id或code进行删除
     *
     * @param id
     * @param code
     */
    void deleteIdOrCode(@Param("id") String id, @Param("code") String code);

    /**
     * 删除关联数据
     *
     * @param id
     */
    void removeId(String id);

    /**
     * @param id
     * @return
     */
    InspectionStrategyDTO getId(@Param("id") String id);

    /**
     * 查询选择的标准表
     *
     * @param codes
     * @return
     */
    List<InspectionCodeDTO> selectbyCodes(@Param("codes") List<String> codes);

    /**
     * 查询标准表选择的设备
     *
     * @param id
     * @return
     */
    Device viewDetail(@Param("id") String id);

    /**
     * 查询标准表选择的设备
     *
     * @param id
     * @return
     */
    List<Device> viewDetails(@Param("id") String id);

    /**
     * 查询编码信息
     *
     * @param strategyId
     * @param majorCode
     * @param subsystemCode
     * @return
     */
    List<InspectionStrategyDTO> selectCodeList(@Param("strategyId") String strategyId, @Param("majorCode") String majorCode, @Param("subsystemCode") String subsystemCode);

    String translateMajor(@Param("majorCode") String majorCode);

    /**
     * 查询子系统
     *
     * @param majorCode
     * @param systemCode
     * @return
     */
    List<SubsystemDTO> translateSubsystem(@Param("majorCode") String majorCode, List<String> systemCode);

    /**
     * 查询线路下的站点
     *
     * @param siteCode
     * @return
     */
    List<String> selectBySite(@Param("siteCode") String siteCode);

    /**
     * 查询子系统名称
     *
     * @param systemCode
     * @return
     */
    String systemCodeName(@Param("subsystemCode") String systemCode);

    /**
     * 设备类型名字
     *
     * @param deviceTypeCode
     * @return
     */
    String deviceTypeCodeName(@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 查询状态
     *
     * @param status
     * @return
     */
    String statusDesc(@Param("status") Integer status);

    /**
     * 翻译
     *
     * @param temporary
     * @return
     */
    String temporaryName(@Param("temporary") String temporary);

    /**
     * 按条件查询检修策略
     *
     * @param inspectionStrategyDTO
     * @return
     */
    List<InspectionStrategyExcelDTO> selectListNoPage(@Param("inspectionStrategyDTO") InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 按田间查询
     *
     * @param code
     * @return
     */
    List<InspectionExcelDTO> selectInspectionCode(String code);

    /**
     * 查询设备信息
     *
     * @param id
     * @return
     */
    List<DeviceExcelDTO> selectDevice(String id);

    /**
     * 查询线路站点信息是否存在
     *
     * @param lineName
     * @param stationName
     * @return
     */
    List<StationDTO> getStation(@Param("lineName") String lineName, @Param("stationName") String stationName);

    /**
     * 根据组织机构名称查询是否存在系统中
     * @param org
     * @return
     */
    List<String> getOrgs(String org);

    /**
     * 根据设备编码查询系统中是否存在此设备
     * @param device
     * @return
     */
    Device getIsExistDevice(String device);
}
