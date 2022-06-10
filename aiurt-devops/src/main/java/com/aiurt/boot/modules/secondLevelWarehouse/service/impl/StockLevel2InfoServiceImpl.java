package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.CsStockLevelTwoVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2InfoMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2InfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 二级库仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class StockLevel2InfoServiceImpl extends ServiceImpl<StockLevel2InfoMapper, CsStockLevelTwoVO> implements IStockLevel2InfoService {

    @Resource
    private StockLevel2InfoMapper stockLevel2InfoMapper;

    @Override
    public List<CsStockLevelTwoVO> selectStockList() {
        return  stockLevel2InfoMapper.selectStockList();
    }
}
