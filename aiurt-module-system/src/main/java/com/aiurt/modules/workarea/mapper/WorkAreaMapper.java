package com.aiurt.modules.workarea.mapper;

import com.aiurt.modules.workarea.dto.MajorDTO;
import com.aiurt.modules.workarea.dto.MajorUserDTO;
import com.aiurt.modules.workarea.dto.SubSystem;
import com.aiurt.modules.workarea.dto.WorkAreaDTO;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SiteModel;
import org.jeecg.common.system.vo.SysDepartModel;

import java.util.List;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
public interface WorkAreaMapper extends BaseMapper<WorkArea> {

    /**
     * 工区列表查询
     * @param pageList
     * @param workArea
     * @return
     */
    List<WorkAreaDTO> getWorkAreaList(@Param("pageList") Page<WorkAreaDTO> pageList, @Param("workArea")WorkAreaDTO workArea);

    /**
     * 根据专业code,获取专业名称
     * @param majorCode
     * @return
     */
    String getMajorName(String majorCode);

    /**
     * 根据专业id,查询专业下的全部用户
     * @param pageList
     * @param majorId
     * @param name
     * @param orgId
     * @return
     */
    List<MajorUserDTO> getMajorAllUser(@Param("pageList")Page<MajorUserDTO> pageList, @Param("majorId")String majorId,@Param("name")String name,@Param("orgId")String orgId);

    /**
     * 根据用户id,查询该用户下的所有专业
     * @param userId
     * @return
     */
    List<MajorDTO> getUserAllMajor(String userId);

    /**
     * 根据专业code,查询该专业下的所有子系统
     * @param majorCode
     * @return
     */
    List<SubSystem> getMajorAllSubSystem(String majorCode);

    /**
     * 根据组织机构查找对应的工区信息
     * @param orgCode
     * @return
     */
    List<SiteModel> getSiteByOrgCode(String orgCode);

    /**
     * 通过线路和专业过滤出班组
     * @param lineCodeList
     * @param majorList
     * @return
     */
    List<String> getTeamBylineAndMajor(@Param("lineCodeList") List<String> lineCodeList, @Param("majorList") List<String> majorList);
    /**
     * 通过线路和专业过滤出班组详细信息
     * @param lineCodeList
     * @param majorList
     * @return
     */
    List<SysDepartModel> getTeamBylineAndMajors(@Param("lineCodeList") List<String> lineCodeList,@Param("majorList") List<String> majorList);
}
