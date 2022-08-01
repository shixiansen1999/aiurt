package com.aiurt.boot.plan.mapper;


import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.boot.plan.dto.StandardDTO;
import com.aiurt.boot.plan.entity.PatrolPlan;
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
    List<String> getMajorInfoByPlanId(@Param("planId") String planId);

    List<String> getSubsystemInfoByPlanId(@Param("planId") String planId);

    /**
     * 查询详情
     * @param id
     * @return
     */
    PatrolPlanDto selectId(@Param("id")String id,@Param("code") String code);
  /**
   * 查询周期
   * @param id
   * @return
   */
  List<Integer> selectWeek(@Param("id")String id,@Param("code") String code);

    /**
     *
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
     *
     * @param planStandardCode
     * @return
     */
    String byCode(@Param("planStandardCode")String planStandardCode,@Param("planId") String planId);

    /**
     *
     * @param standardCode
     * @return
     */
    IPage<Device> viewDetails(@Param("page")Page<Device> page,@Param("standardCode")String standardCode,@Param("planId")String planId);
  /**
   * 查询哪个星期
   * @param id
   * @return
   */
  List<Integer> selectTime(String id, String code);
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
   * @param codeList
   * @return
   */
  List<MajorDTO> translateMajor(List<String> codeList);

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
   * @return
   */
    IPage<Device> deviceList(Page<Device> page, @Param("siteCodes")List<String> siteCodes,@Param("subsystemCode") String subsystemCode, @Param("majorCode")String majorCode,@Param("deviceTypeCode")String deviceTypeCode);
}
