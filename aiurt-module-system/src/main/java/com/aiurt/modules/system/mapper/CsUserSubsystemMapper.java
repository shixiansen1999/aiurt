package com.aiurt.modules.system.mapper;

import com.aiurt.modules.subsystem.dto.ListDTO;
import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.dto.SystemByCodeDTO;
import com.aiurt.modules.subsystem.dto.YearFaultDTO;
import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.CsUserSubsystemModel;

import java.util.List;

/**
 * @Description: 用户子系统表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserSubsystemMapper extends BaseMapper<CsUserSubsystem> {
    /**
     * 获取子系统
     * @param userId
     * @return
     */
    List<String> getSubsystemIds(@Param("userId") String userId);
    /**
     * 根据用户id获取站点
     * @param id
     * @return
     */
    List<CsUserSubsystemModel> getSubsystemByUserId(@Param("id") String id);

    /**
     * 根据用户Id获取子系统
     *
     * @param page
     * @param id
     * @return
     */
    List<SubsystemFaultDTO> selectByUserId(Page<SubsystemFaultDTO> page, @Param("id") String id);

    /**
     * 查询
     * @param startTime
     * @param endTime
     * @param subsystemCode
     * @return
     */
    SubsystemFaultDTO getSubsystemFaultDTO(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("subsystemCode") String subsystemCode);

    /**
     * 查询
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    List<SubsystemFaultDTO> getSubsystemByDeviceTypeCode( @Param("subsystemCode") List<String> subsystemCode,@Param("deviceTypeCode") List<String> deviceTypeCode);

    /**
     *  设备类型数据
     * @param startTime
     * @param endTime
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    SubsystemFaultDTO getSubsystemByDeviceType(@Param("startTime") String startTime, @Param("endTime")String endTime,@Param("subsystemCode") String subsystemCode, @Param("deviceTypeCode") String deviceTypeCode, @Param("filterValue")boolean filterValue);

    /**
     * 查询次数
     * @param startTime
     * @param endTime
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    Integer getNum(@Param("startTime") String startTime, @Param("endTime")String endTime,@Param("subsystemCode") String subsystemCode, @Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 年次数
     * @param id
     * @return
     */
    List<YearFaultDTO> getYearNumFault(@Param("id") String id);

    /**
     * 设备类型年次数
     * @param code
     * @param deviceTypeCode
     * @return
     */
    YearFaultDTO getDeviceTypeYearFault(@Param("code") String code,@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 年分钟
     * @param code
     * @return
     */
    List<ListDTO> sysTemYearFault(@Param("code") String code);

    /**
     * 设备类型数据查询
     * @param code
     * @param deviceTypeCode
     * @return
     */
    List<ListDTO> deviceTypeFault(@Param("code")String code,@Param("deviceTypeCode") String deviceTypeCode);

    /**
     * 根据code查询
     * @param subsystemCode
     * @return
     */
    SystemByCodeDTO getSystemByCodeDTO(@Param("subsystemCode") String subsystemCode);

    /**
     * 查询备件跟换次数
     * @param subsystemCode
     * @return
     */
    Integer getReplacementNum(String subsystemCode);

    /**
     * 查询子系统名称code id
     * @param subsystemCode
     * @return
     */
    List<SubsystemFaultDTO> selectSubSystem(@Param("subsystemCode")SubsystemFaultDTO subsystemCode);

    /**
     * 查询子系统的维修时长（过滤已挂起的）
     * @param startTime
     * @param endTime
     * @param subsystemCode
     * @return
     */
    SubsystemFaultDTO  getSubsystemFilterFaultDTO(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("subsystemCode")String subsystemCode);

    /**
     * 查询子系统的下的设备分类的维修时长（过滤已挂起的）
     * @param startTime
     * @param endTime
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    Integer getFilterNum(@Param("startTime") String startTime, @Param("endTime")String endTime,@Param("subsystemCode") String subsystemCode, @Param("deviceTypeCode") String deviceTypeCode);
    /**
     * 年分钟
     * @param systemCode
     * @return
     */
    List<ListDTO> sysTemYearAllFault(@Param("systemCode") String systemCode);

    /**
     * @param startTime
     * @param endTime
     * @param systemCode
     * @param filterValue
     * @return
     */
    List<SubsystemFaultDTO> yearTrendChartFault(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("systemCode")String systemCode, @Param("filterValue")boolean filterValue);

    /**
     * @param id
     * @param systemCodes
     * @return
     */
    List<SubsystemFaultDTO> selectSystem(@Param("id")String id, @Param("systemCodes")List<String> systemCodes);

}
