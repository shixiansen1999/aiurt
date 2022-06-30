package com.aiurt.boot.standard.service.impl;


import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class InspectionCodeServiceImpl extends ServiceImpl<InspectionCodeMapper, InspectionCode> implements IInspectionCodeService {

    @Override
    public IPage<InspectionCodeDTO> pageList(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO) {
        List<InspectionCodeDTO> inspectionCodeDTOS = baseMapper.pageList(inspectionCodeDTO);
        return page.setRecords(inspectionCodeDTOS);
    }

    @Override
    public void updateDelFlag(String id) {
       InspectionCode inspectionCode =baseMapper.selectById(id);
       inspectionCode.setDelFlag(1);
       baseMapper.updateById(inspectionCode);
    }
}
