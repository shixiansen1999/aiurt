package com.aiurt.modules.material.service;

import com.aiurt.modules.material.dto.MaterialRequisitionDetailInfoDTO;
import com.aiurt.modules.material.dto.MaterialRequisitionInfoDTO;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 领料单的service
 *
 * @author 华宜威
 * @date 2023-09-18 16:38:06
 */
public interface IMaterialRequisitionService extends IService<MaterialRequisition> {
    /**
     * 三级库管理-三级库申领-根据申领单号查询详情
     * @param code
     * @return
     */
    MaterialRequisitionInfoDTO queryByCode(String code);

    void queryPageDetail(Page<MaterialRequisitionDetailInfoDTO> page, String code);

    /**
     * 根据申领单id获取详情
     * @param id 申领单id
     * @return MaterialRequisitionInfoDTO 领料单详情的响应DTO
     */
    MaterialRequisitionInfoDTO getDetailById(String id);

    /**
     * 申领单-根据id删除
     * @param id 申领单id
     */
    void deleteById(String id);
}
