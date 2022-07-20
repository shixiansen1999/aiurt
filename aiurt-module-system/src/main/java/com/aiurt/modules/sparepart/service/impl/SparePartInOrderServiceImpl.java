package com.aiurt.modules.sparepart.service.impl;


import com.aiurt.common.enums.MaterialTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.dto.SparePartInExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartInQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartInVO;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件入库表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartInOrderServiceImpl extends ServiceImpl<SparePartInOrderMapper, SparePartInOrder> implements ISparePartInOrderService {

    @Resource
    private SparePartInOrderMapper sparePartInOrderMapper;

    @Override
    public IPage<SparePartInVO> queryPageList(Page<SparePartInVO> page, SparePartInQuery sparePartInQuery) {

        return sparePartInOrderMapper.queryPageList(page,sparePartInQuery);
    }

    @Override
    public List<SparePartInExcel> exportXls(SparePartInQuery sparePartInQuery) {
        List<SparePartInExcel> list = sparePartInOrderMapper.exportXls(sparePartInQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
        }
        return list;

    }
}
