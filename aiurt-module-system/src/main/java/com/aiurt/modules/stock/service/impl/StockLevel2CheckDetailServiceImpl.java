package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.stock.entity.StockLevel2CheckDetail;
import com.aiurt.modules.stock.mapper.StockLevel2CheckDetailMapper;
import com.aiurt.modules.stock.service.IStockLevel2CheckDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockLevel2CheckDetailServiceImpl extends ServiceImpl<StockLevel2CheckDetailMapper, StockLevel2CheckDetail> implements IStockLevel2CheckDetailService {

}
