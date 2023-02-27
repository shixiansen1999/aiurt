package com.aiurt.modules.faultproducereportlinedetail.service.impl;

import com.aiurt.modules.faultproducereportlinedetail.dto.FaultProduceReportLineDetailDTO;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.aiurt.modules.faultproducereportlinedetail.mapper.FaultProduceReportLineDetailMapper;
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 专业故障清单
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
@Service
public class FaultProduceReportLineDetailServiceImpl extends ServiceImpl<FaultProduceReportLineDetailMapper, FaultProduceReportLineDetail> implements IFaultProduceReportLineDetailService {

    @Override
    @Transactional
    public void updateListByIds(List<FaultProduceReportLineDetailDTO> reportLineDetailDTOList) {
        for (FaultProduceReportLineDetail reportLineDetailDTO: reportLineDetailDTOList) {
            LambdaUpdateWrapper<FaultProduceReportLineDetail> updateWrapper = new LambdaUpdateWrapper<>();
            // 只更新【处理情况及管控措施】字段；
            updateWrapper.set(FaultProduceReportLineDetail::getMaintenanceMeasures, reportLineDetailDTO.getMaintenanceMeasures());
            updateWrapper.eq(FaultProduceReportLineDetail::getId, reportLineDetailDTO.getId());
            baseMapper.update(null, updateWrapper);
        }


    }
}
