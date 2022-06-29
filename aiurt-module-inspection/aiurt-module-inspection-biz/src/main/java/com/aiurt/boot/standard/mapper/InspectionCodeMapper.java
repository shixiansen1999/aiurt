package com.aiurt.boot.standard.mapper;


import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface InspectionCodeMapper extends BaseMapper<InspectionCode> {
    /**
     * 分页
     * @param inspectionCodeDTO
     * @return
     */
    List<InspectionCodeDTO> pageList(@Param("inspectionCodeDTO") InspectionCodeDTO inspectionCodeDTO);

    /**
     * 查询是否可以删除
     * @param code
     * @return
     */
    Integer number(@Param("code")String code);
}
