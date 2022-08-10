package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartScrapMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Service
public class SparePartScrapServiceImpl extends ServiceImpl<SparePartScrapMapper, SparePartScrap> implements ISparePartScrapService {
    @Autowired
    private SparePartScrapMapper sparePartScrapMapper;
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartScrap
     * @return
     */
    @Override
    public List<SparePartScrap> selectList(Page page, SparePartScrap sparePartScrap){
        return sparePartScrapMapper.readAll(page,sparePartScrap);
    }
    /**
     * 修改
     *
     * @param sparePartScrap
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartScrap sparePartScrap) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartScrap scrap = getById(sparePartScrap.getId());
        if(sparePartScrap.getStatus().equals(CommonConstant.SPARE_PART_SCRAP_STATUS_3)){
            sparePartScrap.setConfirmId(user.getUsername());
            sparePartScrap.setConfirmTime(new Date());
            //更新已出库库存数量,做减法
            SparePartOutOrder sparePartOutOrder = sparePartOutOrderMapper.selectOne(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getId,scrap.getOutOrderId()));
            if(null!=sparePartOutOrder){
                Integer number = Integer.parseInt(sparePartOutOrder.getUnused())-scrap.getNum();
                sparePartOutOrder.setUnused(number+"");
                sparePartOutOrderMapper.updateById(sparePartOutOrder);
            }
        }
        sparePartScrapMapper.updateById(sparePartScrap);
        return Result.OK("编辑成功！");
    }
}
