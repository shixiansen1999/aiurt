package com.aiurt.modules.stock.service.impl;

import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.mapper.StockLevel2InfoMapper;
import com.aiurt.modules.stock.service.IStockLevel2InfoService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockLevel2InfoServiceImpl extends ServiceImpl<StockLevel2InfoMapper, StockLevel2Info> implements IStockLevel2InfoService {

	@Autowired
	private StockLevel2InfoMapper materialBaseMapper;
	@Autowired
	private SysBaseApiImpl sysBaseApi;

}
