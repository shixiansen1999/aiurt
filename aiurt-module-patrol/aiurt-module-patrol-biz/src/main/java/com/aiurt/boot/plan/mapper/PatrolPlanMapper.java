package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolPlanMapper extends BaseMapper<PatrolPlan> {
    /**
     * 分页
     * @param page
     * @param patrolPlan
     * @return
     */
    List<PatrolPlanDto> list(@Param("page") Page<PatrolPlanDto> page,@Param("patrolPlan") PatrolPlanDto patrolPlan);

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
    PatrolPlanDto selectId(@Param("id")String id);

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
     * @param code
     */
    void deleteIdorCode(@Param("id")String id,@Param("code") String code);

    /**
     *
     * @param planStandardCode
     * @return
     */
    String byCode(@Param("planStandardCode")String planStandardCode);

    /**
     *
     * @param standardCode
     * @return
     */
    List<Device> viewDetails(@Param("standardCode")String standardCode);
}
