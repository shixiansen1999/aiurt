package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionAddReqDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionListReqDTO;
import com.aiurt.modules.sparepart.entity.dto.resp.SparePartRequisitionListRespDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.text.ParseException;

/**
 * 三级库申领的service，因为用到的实体类是领料单，因此不继承IService
 *
 * @author 华宜威
 * @date 2023-09-21 09:48:07
 */
public interface SparePartRequisitionService {


    /**
     * 三级库申领-添加一条申领数据
     * @param sparePartRequisitionAddReqDTO 三级库申领的添加、编辑等请求DTO
     */
    void add(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO) throws ParseException;

    /**
     * 三级库申领-编辑
     * @param sparePartRequisitionAddReqDTO 三级库申领的添加、编辑等请求DTO
     */
    void edit(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO);

    /**
     * 三级库管理-分页列表查询
     * @param sparePartRequisitionListReqDTO 三级库申领分页列表查询的请求DTO
     * @return Page<SparePartRequisitionListRespDTO> 返回分页列表查询结果
     */
    Page<SparePartRequisitionListRespDTO> pageList(SparePartRequisitionListReqDTO sparePartRequisitionListReqDTO);

    /**
     * 三级库管理-提交
     *
     * @param id 领料单id
     * @return Result<String> 返回编辑成功提示
     */
    void submit(String id);
}
