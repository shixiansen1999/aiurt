package com.aiurt.boot.plan.mapper;


import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.boot.plan.dto.StandardDTO;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.task.dto.MajorDTO;
import com.aiurt.boot.task.dto.SubsystemDTO;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@InterceptorIgnore(tenantLine = "1")
public interface PatrolPlanMapper extends BaseMapper<PatrolPlan> {
    /**
     * 分页
     * @param page
     * @param patrolPlan
     * @return
     */
  IPage<PatrolPlanDto> list(@Param("page") Page<PatrolPlanDto> page, @Param("patrolPlan") PatrolPlanDto patrolPlan);

    /**
     * 逻辑删除
     * @param id
     */
    void updates(@Param("id")String id);

  /**
   * 根据任务id获取任务的专业信息
   *
   * @param planId
   * @return
   */
  List<String> getMajorInfoByPlanId(@Param("planId") String planId);

  /**
   * 根据计划id获取任务的子系统信息
   * @param planId
   * @return
   */
    List<String> getSubsystemInfoByPlanId(@Param("planId") String planId);

    /**
     * 查询详情
     * @param id
     * @param code
     * @return
     */
    PatrolPlanDto selectId(@Param("id")String id,@Param("code") String code);
  /**
   * 查询周期
   * @param id
   * @param code
   * @return
   */
  List<Integer> selectWeek(@Param("id")String id,@Param("code") String code);

    /**
     *根据code查询
     * @param code
     * @return
     */
    PatrolPlan selectByCode(@Param("code")String code);

    /**
     * 查询站点
     * @return
     */
    List<QuerySiteDto> querySite();

    /**
     * 删除主表和所有关联表
     * @param id
     * @param
     */
    void deleteIdorCode(@Param("id")String id);

  /**
   * 根据code查询
   * @param planStandardCode
   * @param planId
   * @return
   */
    String byCode(@Param("planStandardCode")String planStandardCode,@Param("planId") String planId);

  /**
   * 分页查询详情
   * @param page
   * @param standardCode
   * @param planId
   * @return
   */
    IPage<Device> viewDetails(@Param("page")Page<Device> page,@Param("standardCode")String standardCode,@Param("planId")String planId);

  /**
   * 查询详情集合
   * @param standardCode
   * @param planId
   * @return
   */
    List<Device> viewDetails(@Param("standardCode")String standardCode,@Param("planId")String planId);
  /**
   * 查询哪个星期
   * @param id
   * @param code
   * @return
   */
  List<Integer> selectTime(@Param("id") String id, @Param("code") String code);
  /**
   * 查询编码信息
   * @param planId
   * @param majorCode
   * @param subsystemCode
   * @return
   */
  List<PatrolPlanDto> selectCodeList(@Param("planId")String planId, @Param("majorCode")String majorCode, @Param("subsystemCode")String subsystemCode);
  /**
   * 翻译专业信息
   * @param list
   * @return
   */
  List<MajorDTO> translateMajor(@Param("list") List<String> list);

  /**
   * 翻译子系统信息
   * @param majorCode
   *  @param systemCode
   * @return
   */
  List<SubsystemDTO> translateSubsystem(@Param("majorCode")String majorCode,List<String> systemCode);

  /**
   * 查询标准表下拉框
   * @param planId
   * @param majorCode
   * @param subsystemCode
   * @return
   */
    List<StandardDTO> selectStandardList(@Param("planId")String planId, @Param("majorCode")String majorCode, @Param("subsystemCode")String subsystemCode);
  /**
   * 待条件查询设备
   * @param siteCodes
   * @param page
   * @param majorCode
   * @param subsystemCode
   * @param name
   * @param code
   * @param deviceTypeCode
   * @return
   */
    IPage<Device> deviceList(Page<Device> page, @Param("siteCodes")List<String> siteCodes,@Param("subsystemCode") String subsystemCode, @Param("majorCode")String majorCode,@Param("deviceTypeCode")String deviceTypeCode,@Param("code") String code,@Param("name") String name);

  /**
   * 查询站点信息
   * @param asList
   * @return
   */
  List<StationDTO> selectStations(List<String> asList);
  /**
   * 查询线路下站点
   * @param siteCode
   * @return
   */
    List<String> selectBySite(String siteCode);
  /**
   * 查询子系统名称
   * @param systemCode
   * @return
   */
  String systemCodeName(@Param("subsystemCode")String systemCode);
  /**
   * 设备类型名字
   * @param deviceTypeCode
   * @return
   */
  String deviceTypeCodeName(@Param("deviceTypeCode")String deviceTypeCode);
  /**
   * 查询状态
   * @param status
   * @return
   */
  String statusDesc(@Param("status")Integer status);
  /**
   * 翻译
   * @param temporary
   * @return
   */
  String temporaryName(@Param("temporary")String temporary);

  /**
   * 翻译
   * @param majorCode
   * @return
   */
  String majorName(@Param("majorCode")String majorCode);
}
