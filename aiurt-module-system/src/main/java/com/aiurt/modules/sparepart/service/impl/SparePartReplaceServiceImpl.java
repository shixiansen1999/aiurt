package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.modules.sparepart.entity.SparePartReplace;
import com.aiurt.modules.sparepart.mapper.SparePartReplaceMapper;
import com.aiurt.modules.sparepart.service.ISparePartReplaceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_replace
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartReplaceServiceImpl extends ServiceImpl<SparePartReplaceMapper, SparePartReplace> implements ISparePartReplaceService {

    @Autowired
    private IDeviceChangeSparePartService deviceChangeSparePartService;

    @Override
    public IPage<SparePartReplace> pageList(Page<SparePartReplace> page, SparePartReplace sparePartReplace, HttpServletRequest req) {
        QueryWrapper<SparePartReplace> queryWrapper = QueryGenerator.initQueryWrapper(sparePartReplace, req.getParameterMap());
        LambdaQueryWrapper<DeviceChangeSparePart> changeSparePartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 根据处置记录id查询故障更换附件表信息
        String scrapId = sparePartReplace.getScrapId();
        if (StrUtil.isNotBlank(scrapId)) {
            changeSparePartLambdaQueryWrapper.eq(DeviceChangeSparePart::getScrapId, scrapId);
        }
        List<DeviceChangeSparePart> deviceChangeSpareParts = deviceChangeSparePartService.list(changeSparePartLambdaQueryWrapper);
        // 两种情况的出库单id
        List<String> borrowIds = deviceChangeSpareParts.stream().map(DeviceChangeSparePart::getBorrowingOutOrderId).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        List<String> lendIds = deviceChangeSpareParts.stream().map(DeviceChangeSparePart::getLendOutOrderId).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        // 根据处置记录id查询备件故障记录
        queryWrapper.lambda().and(wq -> {
            wq.in(CollUtil.isNotEmpty(borrowIds), SparePartReplace::getOutOrderId, borrowIds)
                    .or()
                    .in(CollUtil.isNotEmpty(lendIds), SparePartReplace::getOutOrderId, lendIds);
        });
        return this.page(page, queryWrapper);
    }
}
