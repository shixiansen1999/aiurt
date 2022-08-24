package com.aiurt.modules.workarea.service;

import com.aiurt.modules.workarea.dto.MajorUserDTO;
import com.aiurt.modules.workarea.dto.WorkAreaDTO;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * 工区详情
     * @param id
     * @return
     */
    WorkAreaDTO getWorkAreaDetail(String id);

    /**
     *
     * 根据专业id,查询专业下的全部用户
     * @param pageList
     * @param majorCode
     * @param name
     * @param orgName
     * @return
     */
    Page<MajorUserDTO> getMajorUser(Page<MajorUserDTO> pageList, String majorCode,String name,String orgName);
}
