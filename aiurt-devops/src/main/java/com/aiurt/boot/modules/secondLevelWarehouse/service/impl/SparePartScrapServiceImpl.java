package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.enums.MaterialTypeEnum;
import com.swsc.copsms.common.enums.SpareScrapStatusEnums;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.SparePartScrapMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.SparePartStockMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartScrapService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class SparePartScrapServiceImpl extends ServiceImpl<SparePartScrapMapper, SparePartScrap> implements ISparePartScrapService {

    @Resource
    private SparePartScrapMapper sparePartScrapMapper;

    @Override
    public IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page, SparePartScrapQuery sparePartScrapQuery) {
        IPage<SparePartScrapVO> list = sparePartScrapMapper.queryPageList(page, sparePartScrapQuery);
        return list;
    }

    @Override
    public List<SparePartScrapExcel> exportXls(SparePartScrapQuery sparePartScrapQuery) {
        List<SparePartScrapExcel> list = sparePartScrapMapper.exportXls(sparePartScrapQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
            list.get(i).setStatusString(SpareScrapStatusEnums.getNameByCode(list.get(i).getStatus()));
        }
        return list;
    }
}
