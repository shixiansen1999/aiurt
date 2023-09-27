package com.aiurt.modules.material.mapper;

import com.aiurt.modules.material.dto.MaterialRequisitionDetailInfoDTO;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 领料单的mapper
 *
 * @author 华宜威
 * @date 2023-09-18 16:32:31
 */
public interface MaterialRequisitionMapper extends BaseMapper<MaterialRequisition> {
    Page<MaterialRequisitionDetailInfoDTO> queryPageDetail(Page page,@Param(value = "code") String code);
    List<MaterialRequisitionDetailInfoDTO> queryDetailList(@Param(value = "id") String id);
}
