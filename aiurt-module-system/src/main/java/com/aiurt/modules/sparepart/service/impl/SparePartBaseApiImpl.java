package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.sparepart.dto.SparePartMalfunctionDTO;
import com.aiurt.modules.sparepart.dto.SparePartReplaceDTO;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import org.jeecg.common.system.api.ISparePartBaseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author fgw
 */
@Service
public class SparePartBaseApiImpl implements ISparePartBaseApi {

    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;

    @Override
    public void updateSparePartMalfunction(List<SparePartMalfunctionDTO> malfunctionList) {

    }

    @Override
    public void updateSparePartReplace(List<SparePartReplaceDTO> replaceList) {

    }

    /**
     * 更新出库单未使用的数量
     * @param updateMap
     */
    @Override
    public void updateSparePartOutOrder(Map<String, Integer> updateMap) {
        if (Objects.nonNull(updateMap) && updateMap.size()>0) {
            updateMap.forEach((id, num)->{
                if (StrUtil.isNotBlank(id) && Objects.nonNull(num)) {
                    sparePartOutOrderMapper.updateSparePartOutOrderUnused(id, num);
                }
            });
        }
    }
}
