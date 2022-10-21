package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.modules.fault.dto.FaultFrequencyDTO;
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
import java.util.stream.Stream;

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
    public Page<SparePartStatistics> selectSparePartStatistics(Page<SparePartStatistics> pageList, SparePartStatistics sparePartStatistics) {

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
                                                       sparePartStockMapper.getSubsystemByUserId(pageList, user.getId(),list):
                                                       sparePartStockMapper.getSubsystemByUserId(pageList, user.getId(),null);
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
                        Long l1 = 0L;
                        Long l2 = 0L;
                        Long l3 = 0L;
                        Long l4 = 0L;
                        Long l5= 0L;
                        Long l6 = 0L;
                        Long l7 = 0L;
                        List<String> list3 = new ArrayList<>();
                        list3.add(q.getBaseTypeCode());
                        List<MaterialBaseType> list4 = new ArrayList<>();
                        if (CollectionUtil.isNotEmpty(q.getMaterialBaseTypeList())){
                            this.getAllSubset(list4,q.getMaterialBaseTypeList());
                        }
                        List<String> collect1 = list4.stream().map(MaterialBaseType::getBaseTypeCode).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(collect1)){
                            //物资类型的子级的二级库库存
                             l1 = sparePartStockMapper.stockCount(null, CollectionUtil.isNotEmpty(list1) ? list1:collect1);
                            //物资类型的子级的三级库库存
                             l2 = sparePartStockMapper.sparePartCount(null, CollectionUtil.isNotEmpty(list1) ? list1:collect1);
                             //物资类型的子级的上两年度的总消耗量
                             l3 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:collect1, DateUtil.date().year() - 2,null);
                            //物资类型的子级的上年度的总消耗量
                             l4 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:collect1, DateUtil.date().year() - 1,null);
                            //物资类型的子级的本年度的总消耗量
                            l5 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:collect1, DateUtil.date().year(),null);
                            //物资类型的子级的上个月的消耗量
                            l6= sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:collect1, DateUtil.date().year(), DateUtil.date().month() - 1);
                            //物资类型的子级的本月的消耗量
                            l7 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(), DateUtil.date().month());
                        }
                        //物资类型的二级库库存
                        Long aLong2 = sparePartStockMapper.stockCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3);
                        long l = aLong2!=null ? aLong2 + l1 :0L;
                        //物资类型的三级库库存
                        Long aLong3 = sparePartStockMapper.sparePartCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3);
                        long l8 = aLong3!=null ? aLong3 + l2 : 0L;
                        //上两年度的总消耗量
                        Long aLong9 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year() - 2,null);
                        long l9 = aLong9!=null ? aLong9 + l3 : 0L;
                        //上年度的总消耗量
                        Long aLong10 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year() - 1,null);
                        long l10 = aLong10!=null ? aLong10 + l4 : 0L;
                        //本年度的总消耗量
                        Long aLong11 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(),null);
                        long l11 = aLong11!=null ? aLong11 + l5 : 0L;
                        //上个月的消耗量
                        Long aLong12 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(), DateUtil.date().month() - 1);
                        long l12 = aLong12!=null ? aLong12+l6 : 0L;
                        //本月的消耗量
                        Long aLong13 = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1:list3, DateUtil.date().year(), DateUtil.date().month());
                        long l13 = aLong13!=null ? aLong13+l7 : 0L;
                        SparePartStatistics sparePartStatistics1 = new SparePartStatistics();
                        this.getJudge(sparePartStatistics1,q,l,l8,l9,l10,l11,l12,l13);
                    }
                    e.setMaterialBaseTypeList(collect);
                }
            }
        }
        return pageList.setRecords(subsystemByUserId);
    }

    private void getAllSubset(List<MaterialBaseType> list1, List<MaterialBaseType> list2){
        list1.addAll(list2);
        for (MaterialBaseType materialBaseType : list2) {
            List<MaterialBaseType> materialBaseTypeList = materialBaseType.getMaterialBaseTypeList();
            if (CollectionUtil.isNotEmpty(materialBaseTypeList)){
                this.getAllSubset(list1,materialBaseTypeList);
            }
        }
    }

    @Override
    public List<MaterialBaseType> selectConsume(SparePartConsume sparePartConsume) {
        //获取登录的用户信息
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //子系统编码集合
        List<String> list = new ArrayList<>();
        if (StrUtil.isNotBlank(sparePartConsume.getSystemCode())){
            String[] split = sparePartConsume.getSystemCode().split(",");
            list = Arrays.asList(split);
        }
        //备件类型编码集合
        List<String> list1 = new ArrayList<>();
        if (StrUtil.isNotBlank(sparePartConsume.getBaseTypeCode())){
            String[] split = sparePartConsume.getBaseTypeCode().split(",");
            list1 = Arrays.asList(split);
        }
        //根据用户id查询对应的子系统
        List<SparePartStatistics> subsystemByUserId =  CollectionUtil.isNotEmpty(list) ?
                sparePartStockMapper.getSubsystemByUserId(null, user.getId(),list):
                sparePartStockMapper.getSubsystemByUserId(null, user.getId(),null);
        //查询子系统和所对应的物资类型
        LambdaQueryWrapper<MaterialBaseType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialBaseType::getDelFlag,0);
        if (CollectionUtil.isNotEmpty(list1)){
            queryWrapper.in(MaterialBaseType::getBaseTypeCode,list1);
        }
        List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(queryWrapper);
        List<MaterialBaseType> materialBaseTypeLitres = iMaterialBaseTypeService.treeList(materialBaseTypeList,"0");

        switch (sparePartConsume.getType()) {
            // TODO: 1表示类型为近半年 横坐标为月份（半年有6个月份）
            case "1":
                List<MaterialBaseType> timeCount = getTimeCount(materialBaseTypeLitres, list1, 6, subsystemByUserId);
                timeCount.forEach(e->{
                    List<SparePartConsume> sparePartConsumeList = e.getSparePartConsumeList();
                    long sum = sparePartConsumeList.stream().mapToLong(SparePartConsume::getCount).sum();
                    e.setCount(sum);
                });
                List<MaterialBaseType> count = ListUtil.sortByProperty(timeCount, "count");
                if (StrUtil.isNotBlank(sparePartConsume.getSystemCode())&&StrUtil.isNotBlank(sparePartConsume.getBaseTypeCode())){
                    return timeCount;
                }else {
                    return ListUtil.sub(count, count.size() <= 5 ? 0 : count.size() - 5, count.size());
                }
                // TODO: 2表示类型为近一年 横坐标为月份（一年有12个月份）
            case "2":
                List<MaterialBaseType> timeCount1 = getTimeCount(materialBaseTypeLitres, list1, 12, subsystemByUserId);
                timeCount1.forEach(e->{
                    List<SparePartConsume> sparePartConsumeList = e.getSparePartConsumeList();
                    long sum = sparePartConsumeList.stream().mapToLong(SparePartConsume::getCount).sum();
                    e.setCount(sum);
                });
                List<MaterialBaseType> count1 = ListUtil.sortByProperty(timeCount1, "count");
                if (StrUtil.isNotBlank(sparePartConsume.getSystemCode())&&StrUtil.isNotBlank(sparePartConsume.getBaseTypeCode())){
                    return timeCount1;
                }else {
                    return ListUtil.sub(count1, count1.size() <= 5 ? 0 : count1.size() - 5, count1.size());
                }
                // TODO: 3表示类型为近两年 横坐标为季度（两年有8个季度）
            case "3":
                List<MaterialBaseType> time = getTime(8, list1, subsystemByUserId, materialBaseTypeLitres);
                time.forEach(e->{
                    List<SparePartConsume> sparePartConsumeList = e.getSparePartConsumeList();
                    long sum = sparePartConsumeList.stream().mapToLong(SparePartConsume::getCount).sum();
                    e.setCount(sum);
                });
                List<MaterialBaseType> count2 = ListUtil.sortByProperty(time, "count");
                if (StrUtil.isNotBlank(sparePartConsume.getSystemCode())&&StrUtil.isNotBlank(sparePartConsume.getBaseTypeCode())){
                    return time;
                }else {
                    return ListUtil.sub(count2, count2.size() <= 5 ? 0 : count2.size() - 5, count2.size());
                }

                // TODO: 4表示类型为近三年 横坐标为季度（三年有12个季度）
            case "4":
                List<MaterialBaseType> time1 = getTime(12, list1, subsystemByUserId, materialBaseTypeLitres);
                time1.forEach(e->{
                    List<SparePartConsume> sparePartConsumeList = e.getSparePartConsumeList();
                    long sum = sparePartConsumeList.stream().mapToLong(SparePartConsume::getCount).sum();
                    e.setCount(sum);
                });
                List<MaterialBaseType> count3 = ListUtil.sortByProperty(time1, "count");
                if (StrUtil.isNotBlank(sparePartConsume.getSystemCode())&&StrUtil.isNotBlank(sparePartConsume.getBaseTypeCode())){
                    return time1;
                }else {
                    return ListUtil.sub(count3, count3.size() <= 5 ? 0 : count3.size() - 5, count3.size());
                }

            default:
                return materialBaseTypeLitres;
        }
    }

    private List<MaterialBaseType> getTime (Integer integer,
                          List<String> list1,
                          List<SparePartStatistics> subsystemByUserId,
                          List<MaterialBaseType> materialBaseTypeLitres){
        DateTime date = new DateTime();
        List<MaterialBaseType> materialBaseTypes = new ArrayList<>();
        for (SparePartStatistics e : subsystemByUserId) {
            List<MaterialBaseType> collect = materialBaseTypeLitres.stream().filter(materialBaseType -> e.getSystemCode().equals(materialBaseType.getSystemCode())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                for (int i = 0; i<integer; i++) {
                    if (i==0){
                        String last12Months = getLast12Months(1);
                        String substrings = last12Months.substring(0,4);
                        DateTime parse = DateUtil.parse(last12Months,"yyyy-MM");
                        int quarter = parse.quarter();
                        String string = Convert.numberToChinese(Convert.toDouble(quarter),false);
                        //开始
                        DateTime dateTime = DateUtil.beginOfQuarter(parse);
                        //结束
                        DateTime dateTime1 = DateUtil.endOfQuarter(parse);

                        if (parse.equals(dateTime1) || parse.before(dateTime1)){
                            addDetail(collect,substrings,string,list1,dateTime,parse);
                        }else {
                            addDetail(collect,substrings,string,list1,dateTime,dateTime1);
                        }
                        date = DateUtil.offsetMonth(parse, -3);
                    }else if (i==(integer-1)){
                        String last12Months = getLast12Months(3*(i+1));
                        String substrings = last12Months.substring(0,4);
                        DateTime parse = DateUtil.parse(last12Months,"yyyy-MM");
                        int quarter = parse.quarter();
                        String string = Convert.numberToChinese(Convert.toDouble(quarter),false);
                        //开始
                        DateTime dateTime = DateUtil.beginOfQuarter(parse);
                        //结束
                        DateTime dateTime1 = DateUtil.endOfQuarter(parse);


                        if (parse.equals(dateTime) || parse.after(dateTime)){
                            addDetail(collect,substrings,string,list1,parse,dateTime1);
                        }else {
                            addDetail(collect,substrings,string,list1,dateTime,dateTime1);
                        }
                    } else {
                        String last12Months = DateUtil.format(date, "yyyy-MM");
                        String substrings = last12Months.substring(0,4);
                        DateTime parse = DateUtil.parse(last12Months,"yyyy-MM");
                        int quarter = parse.quarter();
                        String string = Convert.numberToChinese(Convert.toDouble(quarter),false);
                        //开始
                        DateTime dateTime = DateUtil.beginOfQuarter(parse);
                        //结束
                        DateTime dateTime1 = DateUtil.endOfQuarter(parse);

                        addDetail(collect,substrings,string,list1,dateTime,dateTime1);
                        date = DateUtil.offsetMonth(parse, -3);
                    }
                }
                materialBaseTypes.addAll(collect);
            }
        }
        return materialBaseTypes;
    }


    private void addDetail(List<MaterialBaseType> collect,String substrings,String string,List<String> list1,DateTime begin,DateTime end) {
        for(MaterialBaseType q : collect){
            //子集全部集合
            List<MaterialBaseType> materialBaseTypeList = new ArrayList<>();
            if (CollUtil.isNotEmpty(q.getMaterialBaseTypeList())) {
                getAllSubset(materialBaseTypeList, q.getMaterialBaseTypeList());
            }
            if (CollUtil.isEmpty(q.getSparePartConsumeList())) {
                List<SparePartConsume> list = new ArrayList<>();
                SparePartConsume sparePartConsume1 = new SparePartConsume();
                sparePartConsume1.setQuarter(substrings+"年"+"第"+string+"季度");
                q.setMaterialBaseTypeList(null);
                List<String> list3 = new ArrayList<>();
                list3.add(q.getBaseTypeCode());
                Long timeCount = sparePartStockMapper.getTimeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : list3, begin, end);
                //子集的数据
                Long subjectCount = 0L;
                if (CollUtil.isNotEmpty(materialBaseTypeList)) {
                    List<String> collect1 = materialBaseTypeList.stream().map(MaterialBaseType::getBaseTypeCode).collect(Collectors.toList());
                    subjectCount = subjectCount + sparePartStockMapper.getTimeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : collect1, begin, end);
                }

                if (timeCount!=null){
                    sparePartConsume1.setCount(timeCount+subjectCount);
                }else {
                    sparePartConsume1.setCount(subjectCount);
                }
                list.add(sparePartConsume1);
                q.setSparePartConsumeList(list);
            } else {
                List<SparePartConsume> sparePartConsumeList = q.getSparePartConsumeList();
                SparePartConsume sparePartConsume1 = new SparePartConsume();
                sparePartConsume1.setQuarter(substrings+"年"+"第"+string+"季度");
                q.setMaterialBaseTypeList(null);
                List<String> list3 = new ArrayList<>();
                list3.add(q.getBaseTypeCode());
                Long timeCount = sparePartStockMapper.getTimeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : list3, begin, end);
                //子集的数据
                Long subjectCount = 0L;
                if (CollUtil.isNotEmpty(materialBaseTypeList)) {
                    List<String> collect1 = materialBaseTypeList.stream().map(MaterialBaseType::getBaseTypeCode).collect(Collectors.toList());
                    subjectCount = subjectCount + sparePartStockMapper.getTimeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : collect1, begin, end);
                }
                if (timeCount!=null){
                    sparePartConsume1.setCount(timeCount+subjectCount);
                }else {
                    sparePartConsume1.setCount(subjectCount);
                }
                sparePartConsumeList.add(sparePartConsume1);
            }
        }
    }

    private List<MaterialBaseType> getTimeCount(List<MaterialBaseType> materialBaseTypeLitres,List<String> list1,Integer integer,List<SparePartStatistics> subsystemByUserId){
        List<MaterialBaseType> materialBaseTypes = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(subsystemByUserId)){
            for (SparePartStatistics e : subsystemByUserId) {
                List<MaterialBaseType> collect = materialBaseTypeLitres.stream().filter(materialBaseType -> e.getSystemCode().equals(materialBaseType.getSystemCode())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    for(MaterialBaseType q : collect){
                        for (int i = 1; i<=integer; i++) {
                            String last12Months = getLast12Months(i);
                            String substrings = last12Months.substring(0,4);
                            String substring = last12Months.substring(5,7);

                            //子集全部集合
                            List<MaterialBaseType> materialBaseTypeList = new ArrayList<>();
                            if (CollUtil.isNotEmpty(q.getMaterialBaseTypeList())) {
                                getAllSubset(materialBaseTypeList, q.getMaterialBaseTypeList());
                            }
                            if (CollUtil.isEmpty(q.getSparePartConsumeList())) {
                                List<SparePartConsume> list = new ArrayList<>();
                                SparePartConsume sparePartConsume = new SparePartConsume();
                                sparePartConsume.setMonth(substring);
                                list.add(sparePartConsume);
                                q.setMaterialBaseTypeList(null);
                                List<String> list3 = new ArrayList<>();
                                list3.add(q.getBaseTypeCode());
                                Long aLong = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : list3, Integer.valueOf(substrings), Integer.valueOf(substring));
                                //子集的数据
                                Long subjectCount = 0L;
                                if (CollUtil.isNotEmpty(materialBaseTypeList)) {
                                    List<String> collect1 = materialBaseTypeList.stream().map(MaterialBaseType::getBaseTypeCode).collect(Collectors.toList());
                                    subjectCount = subjectCount + sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : collect1, Integer.valueOf(substrings), Integer.valueOf(substring));
                                }
                                if (aLong!=null){
                                    sparePartConsume.setCount(aLong+subjectCount);
                                }else {
                                    sparePartConsume.setCount(subjectCount);
                                }
                                q.setSparePartConsumeList(list);
                            } else {
                                List<SparePartConsume> sparePartConsumeList = q.getSparePartConsumeList();
                                SparePartConsume sparePartConsume = new SparePartConsume();
                                sparePartConsume.setMonth(substring);
                                q.setMaterialBaseTypeList(null);
                                List<String> list3 = new ArrayList<>();
                                list3.add(q.getBaseTypeCode());
                                Long aLong = sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : list3, Integer.valueOf(substrings), Integer.valueOf(substring));
                                //子集的数据
                                Long subjectCount = 0L;
                                if (CollUtil.isNotEmpty(materialBaseTypeList)) {
                                    List<String> collect1 = materialBaseTypeList.stream().map(MaterialBaseType::getBaseTypeCode).collect(Collectors.toList());
                                    subjectCount = subjectCount + sparePartStockMapper.timeCount(null, CollectionUtil.isNotEmpty(list1) ? list1 : collect1, Integer.valueOf(substrings), Integer.valueOf(substring));
                                }
                                if (aLong!=null){
                                    sparePartConsume.setCount(aLong+subjectCount);
                                }else {
                                    sparePartConsume.setCount(subjectCount);
                                }
                                sparePartConsumeList.add(sparePartConsume);
                            }
                        }
                    }
                    materialBaseTypes.addAll(collect);
                }
            }
        }
        return materialBaseTypes;
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
        Page<SparePartStatistics> partStatisticsPage = this.selectSparePartStatistics(page, sparePartStatistics);
        List<SparePartStatistics> records = partStatisticsPage.getRecords();
        List<SparePartStatistics> dtos = new ArrayList<>();

        for (SparePartStatistics statisticsDTO : records) {
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
        if (CollectionUtil.isNotEmpty(records)) {
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
