package com.aiurt.modules.workarea.service;

import com.aiurt.boot.weeklyplan.entity.BdSite;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.workarea.dto.MajorUserDTO;
import com.aiurt.modules.workarea.dto.WorkAreaDTO;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.SiteModel;
import org.jeecg.common.system.vo.SysDepartModel;

import java.util.List;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
public interface IWorkAreaService extends IService<WorkArea> {

    /**
     * 工区列表查询
     * @param pageList
     * @param workArea
     * @return
     */
    Page<WorkAreaDTO> getWorkAreaList(Page<WorkAreaDTO> pageList, WorkAreaDTO workArea);

    /**
     * 添加工区
     * @param workAreaDTO
     */
    void addWorkArea(WorkAreaDTO workAreaDTO);

    /**
     * 编辑工区
     * @param workAreaDTO
     */
    void updateWorkArea(WorkAreaDTO workAreaDTO);

    /**
     * 删除工区
     * @param id
     */
    void deleteWorkArea(String id);


    /**
     *
     * 根据专业id,查询专业下的全部用户
     * @param pageList
     * @param majorCode
     * @param name
     * @param orgId
     * @return
     */
    Page<MajorUserDTO> getMajorUser(Page<MajorUserDTO> pageList, String majorCode,String name,String orgId);

    /**
     * 根据组织结构编码查找对应的工区信息
     * @param orgCode
     * @return
     */
    List<SiteModel> getSiteByOrgCode(String orgCode);
    /**
     * 根据线路获取班组(根据登录用户专业过滤)
     *
     * @param lineCode 线路code
     * @return
     */
    List<SysDepartModel> getTeamBylineAndMajors(String lineCode);

    /**
     * 查询本工区的站所
     * @param param 标志: 1:全部,0:本工区
     * @return
     */
    List<CsStation> queryOriginStation(String param);

    /**
     * 查询本工区的人员
     * @param param 标志: 1:全部,0:本工区
     * @return
     */
    List<SysUser> queryOriginUser(String param);

    /**
     * 获取当前用户管辖班组下工区
     * @return
     */
    List<SiteModel>querySiteByTeam();
}
