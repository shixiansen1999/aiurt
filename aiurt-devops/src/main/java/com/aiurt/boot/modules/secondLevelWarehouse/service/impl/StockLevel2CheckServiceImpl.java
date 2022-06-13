package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2CheckDetailMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2CheckMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2Mapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2CheckService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class StockLevel2CheckServiceImpl extends ServiceImpl<StockLevel2CheckMapper, StockLevel2Check> implements IStockLevel2CheckService {
    @Resource
    private StockLevel2Mapper stockLevel2Mapper;
    @Resource
    private StockLevel2CheckMapper stockLevel2CheckMapper;
    @Resource
    private StockLevel2CheckDetailMapper stockLevel2CheckDetailMapper;
    @Resource
    private ISysBaseAPI iSysBaseAPI;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCheck(StockLevel2Check check, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        //模拟盘点任务单号
        String simulateCheckCode = simulateCheckCode();

        check.setCreateBy(userId);
        check.setUpdateBy(userId);
        check.setStockCheckCode(simulateCheckCode);
        stockLevel2CheckMapper.insert(check);

        //去二级库存中查询该仓库的所有物资
        List<StockLevel2> stockLevel2List = stockLevel2Mapper.selectList(new QueryWrapper<StockLevel2>()
                .eq("warehouse_code", check.getWarehouseCode()));
        stockLevel2List.forEach(e->{
            //插入二级库盘点详情初始数据
            StockLevel2CheckDetail checkDetail = new StockLevel2CheckDetail();
            checkDetail.setStockCheckCode(check.getStockCheckCode());
            checkDetail.setWarehouseCode(check.getWarehouseCode());
            checkDetail.setMaterialCode(e.getMaterialCode());
            checkDetail.setCreateBy(userId);
            checkDetail.setUpdateBy(userId);
            stockLevel2CheckDetailMapper.insert(checkDetail);
        });


    }

    @Override
    public IPage<Stock2CheckVO> queryPageList(IPage<Stock2CheckVO> page,StockLevel2CheckDTO stockLevel2CheckDTO) {
        IPage<Stock2CheckVO> stock2CheckVOIPage = stockLevel2CheckMapper.queryPageList(
                page,
                stockLevel2CheckDTO.getStockCheckCode(),
                stockLevel2CheckDTO.getWarehouseCode(),
                stockLevel2CheckDTO.getStartTime(),
                stockLevel2CheckDTO.getEndTime());
//        stock2CheckVOIPage.getRecords().forEach(e->{
//            List<StockLevel2CheckDetail> checkDetailList = stockLevel2CheckDetailMapper.selectList(new QueryWrapper<StockLevel2CheckDetail>()
//                    .eq("stock_check_code", e.getStockCheckCode()));
//            if(CollUtil.isNotEmpty(checkDetailList)){
//                int sum = checkDetailList.stream().filter(j->j.getActualNum()!=null)
//                        .mapToInt(StockLevel2CheckDetail::getActualNum).sum();
//                e.setCheckAllNum(sum);
//            }
//        });
        List<Stock2CheckVO> records = stock2CheckVOIPage.getRecords();
        //根据盘点任务单号计算实盘数量
        for (Stock2CheckVO record : records) {
            Integer num = stockLevel2CheckDetailMapper.selectActualNum(record.getStockCheckCode());
            record.setNum(num);
        }

        return  stock2CheckVOIPage;
    }

    /**
     * 二级库盘点导出
     * @param stockLevel2CheckDTO
     * @return
     */
    @Override
    public List<StockLevel2CheckExcel> exportXls(StockLevel2CheckDTO stockLevel2CheckDTO) {
        List<StockLevel2CheckExcel> excels = stockLevel2CheckMapper.exportXls(stockLevel2CheckDTO);
        excels.forEach(e->{
                Integer num = stockLevel2CheckDetailMapper.selectActualNum(e.getStockCheckCode());
                e.setCheckNum(num);
        });
        return excels;
    }

    private String simulateCheckCode() {
        String applyCode="PD101.";
        String y = String.valueOf(LocalDateTime.now().getYear());
        String year = y.substring(y.length() - 2);
        int monthValue = LocalDateTime.now().getMonthValue();
        String month= monthValue +".";
        if(monthValue<10){
            month="0"+ monthValue+".";
        }
        Long integer = stockLevel2CheckMapper.selectCount(null);
        return applyCode+year+month+(integer+1);
    }
}
