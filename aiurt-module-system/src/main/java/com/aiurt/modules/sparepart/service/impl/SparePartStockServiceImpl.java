package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTOS;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartConsume;
import com.aiurt.modules.sparepart.entity.dto.SparePartStatistics;
import com.aiurt.modules.sparepart.mapper.SparePartLendStockMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
@Service
public class SparePartStockServiceImpl extends ServiceImpl<SparePartStockMapper, SparePartStock> implements ISparePartStockService {
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartLendStockMapper sparePartLendStockMapper;
    @Autowired
    private IMaterialBaseTypeService iMaterialBaseTypeService;
    @Autowired
    private CommonAPI commonAPI;
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    @Override
    public List<SparePartStock> selectList(Page page, SparePartStock sparePartStock){
        return sparePartStockMapper.readAll(page,sparePartStock);
    }
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    @Override
    public List<SparePartStock> selectLendList(Page page, SparePartStock sparePartStock){
        return sparePartLendStockMapper.readAll(page,sparePartStock);
    }

    @Override
    public List<SparePartStatistics> selectSparePartStatistics(Page page, SparePartStatistics sparePartStatistics) {

        //获取登录的用户信息
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //子系统编码集合
        List<String> list = new ArrayList<>();
        if (StrUtil.isNotBlank(sparePartStatistics.getSystemCode())){
            String[] split = sparePartStatistics.getSystemCode().split(",");
            list = Arrays.asList(split);
        }

        //备件类型编码集合
        List<String> list1 = new ArrayList<>();
        if (StrUtil.isNotBlank(sparePartStatistics.getBaseTypeCode())){
            String[] split = sparePartStatistics.getBaseTypeCode().split(",");
            list1 = Arrays.asList(split);
        }

        //根据用户id查询对应的子系统
        List<SparePartStatistics> subsystemByUserId =  CollectionUtil.isNotEmpty(list) ?
                                                       sparePartStockMapper.getSubsystemByUserId(page, null,list):
                                                       sparePartStockMapper.getSubsystemByUserId(page, user.getId(),null);
        //查询子系统和所对应的物资类型
        LambdaQueryWrapper<MaterialBaseType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialBaseType::getDelFlag,0);
        if (CollectionUtil.isNotEmpty(list1)){
            queryWrapper.in(MaterialBaseType::getBaseTypeCode,list1);
        }
        List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(queryWrapper);

        List<MaterialBaseType> materialBaseTypeLitres = iMaterialBaseTypeService.treeList(materialBaseTypeList,"0");
        if (CollUtil.isNotEmpty(subsystemByUserId)){
            for (SparePartStatistics e : subsystemByUserId) {
                List<String> list2 = new ArrayList<>();
                list2.add(e.getSystemCode());
                //所属系统的二级库库存
                Long aLong = sparePartStockMapper.stockCount(CollectionUtil.isNotEmpty(list) ? list:list2,null);
                //所属系统的三级库库存
                Long aLong1 = sparePartStockMapper.sparePartCount(CollectionUtil.isNotEmpty(list) ? list:list2, null);
                //上两年度的总消耗量
                Long aLong4 = sparePartStockMapper.timeCount(CollectionUtil.isNotEmpty(list) ? list:list2, null, DateUtil.date().year() - 2,null);
                //上年度的总消耗量
                Long aLong5 = sparePartStockMapper.timeCount(CollectionUtil.isNotEmpty(list) ? list:list2, null, DateUtil.date().year() - 1,null);
                //本年度的总消耗量
                Long aLong6 = sparePartStockMapper.timeCount(CollectionUtil.isNotEmpty(list) ? list:list2, null, DateUtil.date().year(),null);
                //上个月的消耗量
                Long aLong7 = sparePartStockMapper.timeCount(CollectionUtil.isNotEmpty(list) ? list:list2, null, DateUtil.date().year(), DateUtil.date().month() - 1);
                //本月的消耗量
                Long aLong8 = sparePartStockMapper.timeCount(CollectionUtil.isNotEmpty(list) ? list:list2, null, DateUtil.date().year(), DateUtil.date().month());
                MaterialBaseType materialBaseType1 = new MaterialBaseType();
                this.getJudge(e,materialBaseType1,aLong,aLong1,aLong4,aLong5,aLong6,aLong7,aLong8);
                List<MaterialBaseType> collect = materialBaseTypeLitres.stream().filter(materialBaseType -> e.getSystemCode().equals(materialBaseType.getSystemCode())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    for (MaterialBaseType q : collect) {
                        q.setMaterialBaseTypeList(null);
                        List<String> list3 = new ArrayList<>();
                        list3.add(q.getBaseTypeCode());
                        //物资类型的二级库库存
                        Long aLong2 = sparePartStockMapper.stockCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3);
                        //物资类型的三级库库存
                        Long aLong3 = sparePartStockMapper.sparePartCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3);
                        //上两年度的总消耗量
                        Long aLong9 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year() - 2,null);
                        //上年度的总消耗量
                        Long aLong10 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year() - 1,null);
                        //本年度的总消耗量
                        Long aLong11 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(),null);
                        //上个月的消耗量
                        Long aLong12 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(), DateUtil.date().month() - 1);
                        //本月的消耗量
                        Long aLong13 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(), DateUtil.date().month());
                        SparePartStatistics sparePartStatistics1 = new SparePartStatistics();
                        this.getJudge(sparePartStatistics1,q,aLong2,aLong3,aLong9,aLong10,aLong11,aLong12,aLong13);
                    }
                    e.setMaterialBaseTypeList(collect);
                }
            }
        }
        return subsystemByUserId;
    }

    @Override
    public List<SparePartConsume> selectConsume(SparePartConsume sparePartConsume) {
        switch (sparePartConsume.getType()) {
            case "1":
                for (int i = 1; i<=6; i++) {
                    String last12Months = getLast12Months(i);
                    String substring = last12Months.substring(5,7);
                    String changeover = substring+"月";
                    System.out.println(changeover);
                }
                break;
            case "2":
                for (int i = 1; i<=12; i++) {
                    String last12Months = getLast12Months(i);
                    String substring = last12Months.substring(5,7);
                    String changeover = substring+"月";
                    System.out.println(changeover);
                }
                break;
            case "3":
                for (int i = 1; i<=24; i++) {
                    String last12Months = getLast12Months(i);
                    String substring = last12Months.substring(5,7);
                    String changeover = substring+"月";
                    System.out.println(changeover);
                }
                break;
            default:
                for (int i = 1; i<=36; i++) {
                    String last12Months = getLast12Months(i);
                    String substring = last12Months.substring(5,7);
                    String changeover = substring+"月";
                    System.out.println(changeover);
                }
        }

        return null;
    }

    private void getJudge( SparePartStatistics e ,MaterialBaseType q, Long aLong, Long aLong1, Long aLong4,Long aLong5,Long aLong6,Long aLong7,Long aLong8){
        if (aLong4 != null) {
            e.setTwoTotalConsumption(aLong4);
            q.setTwoTotalConsumption(aLong4);
        } else {
            e.setTwoTotalConsumption(0L);
            q.setTwoTotalConsumption(0L);
        }
        if (aLong5 != null) {
            e.setLastYearConsumption(aLong5);
            q.setLastYearConsumption(aLong5);
        } else {
            e.setLastYearConsumption(0L);
            q.setLastYearConsumption(0L);
        }
        if (aLong6 != null) {
            e.setThisYearConsumption(aLong6);
            q.setThisYearConsumption(aLong6);
        } else {
            e.setThisYearConsumption(0L);
            q.setThisYearConsumption(0L);
        }if (aLong7 != null){
            e.setLastMonthConsumption(aLong7);
            q.setLastMonthConsumption(aLong7);
        }else {
            e.setLastMonthConsumption(0L);
            q.setLastMonthConsumption(0L);
        }if (aLong8 != null){
            e.setThisMonthConsumption(aLong8);
            q.setThisMonthConsumption(aLong8);
        }else {
            e.setThisMonthConsumption(0L);
            q.setThisMonthConsumption(0L);
        }if (aLong!=null){
            e.setTwoCount(aLong);
            q.setTwoCount(aLong);
        }else {
            e.setTwoCount(0L);
            q.setTwoCount(0L);
        }if(aLong1!=null){
            e.setThreeCount(aLong1);
            q.setThreeCount(aLong1);
        }else {
            e.setThreeCount(0L);
            q.setThreeCount(0L);
        }
        //上两年度月均消耗量
        BigDecimal div1 = e.getTwoTotalConsumption()!=null ? NumberUtil.div(e.getTwoTotalConsumption().toString(), "12",2, RoundingMode.HALF_UP) : new BigDecimal(0L);
        BigDecimal div11 = q.getTwoTotalConsumption()!=null ? NumberUtil.div(q.getTwoTotalConsumption().toString(), "12",2, RoundingMode.HALF_UP) : new BigDecimal(0L);
        e.setTwoMonthConsumption(e.getTwoTotalConsumption()==0 ? "0" : div1.toString());
        q.setTwoMonthConsumption(q.getTwoTotalConsumption()==0 ? "0" : div11.toString());
        //上年度月均消耗量
        BigDecimal div2 = e.getLastYearConsumption()!=null ? NumberUtil.div(e.getLastYearConsumption().toString(), "12",2, RoundingMode.HALF_UP) : new BigDecimal(0L);
        BigDecimal div22 = q.getLastYearConsumption()!=null ? NumberUtil.div(q.getLastYearConsumption().toString(), "12",2, RoundingMode.HALF_UP) : new BigDecimal(0L);
        e.setLastYearMonthConsumption(e.getLastYearConsumption()==0 ? "0" : div2.toString());
        q.setLastYearMonthConsumption(q.getLastYearConsumption()==0 ? "0" : div22.toString());
        //本年度月均消耗量
        BigDecimal div3 = e.getThisYearConsumption()!=null ? NumberUtil.div(e.getThisYearConsumption().toString(), "12",2, RoundingMode.HALF_UP) : new BigDecimal(0L);
        BigDecimal div33 = q.getThisYearConsumption()!=null ? NumberUtil.div(q.getThisYearConsumption().toString(), "12",2, RoundingMode.HALF_UP) : new BigDecimal(0L);
        e.setThisYearMonthConsumption(e.getThisYearConsumption()==0 ? "0" : div3.toString());
        q.setThisYearMonthConsumption(q.getThisYearConsumption()==0 ? "0" : div33.toString());

    }

    public static String getLast12Months(int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -i);
        Date m = c.getTime();
        return sdf.format(m);
    }

    @Override
    public ModelAndView reportExport(HttpServletRequest request, SparePartStatistics sparePartStatistics) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Page<SparePartStatistics> page = new Page<>(sparePartStatistics.getPageNo(), sparePartStatistics.getPageSize());
        List<SparePartStatistics> sparePartStatisticsList = this.selectSparePartStatistics(page, sparePartStatistics);
        List<SparePartStatistics> dtos = new ArrayList<>();

        for (SparePartStatistics statisticsDTO : sparePartStatisticsList) {
            dtos.add(statisticsDTO);
            List<MaterialBaseType> materialBaseTypeList = statisticsDTO.getMaterialBaseTypeList();
            List<SparePartStatistics> dtoNameList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(materialBaseTypeList)){
                for (MaterialBaseType dto : materialBaseTypeList) {
                    SparePartStatistics sparePartStatistics1 = new SparePartStatistics();
                    BeanUtil.copyProperties(dto,sparePartStatistics1);
                    dtoNameList.add(sparePartStatistics1);

                }
            }
            if (CollUtil.isNotEmpty(dtoNameList)) {
                dtos.addAll(dtoNameList);
            }
        }
        if (CollectionUtil.isNotEmpty(sparePartStatisticsList)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "备件报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, SparePartStatistics.class);
            //自定义导出列表
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS,sparePartStatistics.getExportColumns());
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-备件报表", "备件报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, dtos);
        }
        return mv;
    }

}
