package com.aiurt.modules.faultstatistics.service;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.FaultFrequencyDTO;
import com.aiurt.modules.fault.dto.FaultStatisticsDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.modules.faultanalysisreport.dto.SpareConsumeDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zwl
 * @Title:
 * @Description: 首页故障单统计业务层
 * @date 2022/9/611:14
 */
@Service
public class FaultStatisticsService {

    @Resource
    private FaultMapper faultMapper;

    @Autowired
    private IDeviceChangeSparePartService deviceChangeSparePartService;



    public FaultStatisticsDTO getFaultList(Date startDate, Date endDate) {
        FaultStatisticsDTO faultStatisticsDTO = new FaultStatisticsDTO();
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return faultStatisticsDTO;
        }

        // 将符合条件的故障单查出
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Fault::getHappenTime, DateUtil.beginOfDay(startDate));
        queryWrapper.le(Fault::getHappenTime, DateUtil.endOfDay(endDate));
        List<Fault> faultList = faultMapper.selectList(queryWrapper);

        //故障单总数
        long l1 = CollUtil.isNotEmpty(faultList) ? faultList.size() : 0L;

        //已完成数量
        long l2 = CollUtil.isNotEmpty(faultList) ? faultList.stream().filter(re -> FaultConstant.FAULT_STATUS.equals(re.getStatus())).count() : 0L;
        faultStatisticsDTO.setAlreadyComplete(l2);

        //未完成数量
        long l3 = CollUtil.isNotEmpty(faultList) ? faultList.stream().filter(re -> !FaultConstant.FAULT_STATUS.equals(re.getStatus())).count() : 0L;
        faultStatisticsDTO.setNotComplete(l3);

        //报修数量
        long l4 = CollUtil.isNotEmpty(faultList) ? faultList.stream().filter(re -> FaultConstant.FAULT_MODE_CODE_1.equals(re.getFaultModeCode())).count() : 0L;
        faultStatisticsDTO.setRepairFault(l4);

        //自检自修数量
        long l5 = CollUtil.isNotEmpty(faultList) ? faultList.stream().filter(re -> FaultConstant.FAULT_MODE_CODE_0.equals(re.getFaultModeCode())).count() : 0L;
        faultStatisticsDTO.setOwnFault(l5);

        //故障发生次数列表
        List<FaultFrequencyDTO> frequencyDTOList = faultMapper.selectBySubSystemCode(startDate, endDate);

        List<FaultFrequencyDTO> dtoList = new ArrayList<>();
        frequencyDTOList.forEach(e->{
            if (StrUtil.isNotBlank(e.getSubSystemCode())){
                dtoList.add(e);
            }
        });
        if (CollectionUtil.isNotEmpty(dtoList)){
            //根据次数排序
            List<FaultFrequencyDTO> number = ListUtil.sortByProperty(dtoList, "number");

            if (number.size()>=5){
                //截取后五个值
                List<FaultFrequencyDTO> sub = ListUtil.sub(number, number.size()-5, number.size());
                //子系统
                subList(faultStatisticsDTO, sub);
            }else {
                //截取总数的值
                List<FaultFrequencyDTO> sub = ListUtil.sub(number, 0,Integer.parseInt(String.valueOf(number.size())));
                //子系统
                subList(faultStatisticsDTO, sub);
            }
        }
        return faultStatisticsDTO;
    }

    private void subList(FaultStatisticsDTO faultStatisticsDTO, List<FaultFrequencyDTO> sub) {
        List<String> collect = sub.stream().map(FaultFrequencyDTO::getSubSystemCode).collect(Collectors.toList());
        String string5 = this.translateMajor(collect, FaultConstant.SUBSYSTEM);
        String[] split4 = string5.split("；");
        List<String> list4 = Arrays.asList(split4);
        for (int i = 0; i < sub.size(); i++) {
            String string = list4.get(i);
            FaultFrequencyDTO faultFrequencyDTO = sub.get(i);
            faultFrequencyDTO.setSubSystemName(string);
        }
        faultStatisticsDTO.setFaultFrequencyDTOList(sub);
    }

    /**
     * 翻译专业、专业子系统信息
     *
     * @param codeList code值
     * @param type     类型：major代表专业、subsystem代表子系统
     * @return
     */
    public String translateMajor(List<String> codeList, String type) {
        if (CollUtil.isEmpty(codeList) || StrUtil.isEmpty(type)) {
            return "";
        }
        List<String> nameList = new ArrayList<>();
        if (FaultConstant.MAJOR.equals(type)) {
            nameList = faultMapper.translateMajors(codeList);
        }
        if (FaultConstant.SUBSYSTEM.equals(type)) {
            nameList = faultMapper.translateSubsystems(codeList);
        }
        return CollUtil.isNotEmpty(nameList) ? StrUtil.join("；", nameList) : "";
    }

    /**
     * 获取备件消耗top
     * @param type 1-4:表示1-4季度，5-6：半年，年度
     * @return
     */
    public List<SpareConsumeDTO> getSpareConsume(String type) {
        //Date
        Date startDate = null;
        Date endDate = null;
        Calendar calendar = Calendar.getInstance();
        switch (type) {
            case "1":
                startDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
                endDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-03-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
                break;
            case "2":
                startDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-04-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
                endDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-06-30 23:59:59", "yyyy-MM-dd HH:mm:ss");
                break;
            case "3":
                startDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-07-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
                endDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-09-30 23:59:59", "yyyy-MM-dd HH:mm:ss");
                break;
            case "4":
                startDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-10-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
                endDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
                break;
            case "5":
                startDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
                endDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-06-30 23:59:59", "yyyy-MM-dd HH:mm:ss");
                break;
            default:
                startDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
                endDate = DateUtil.parse(calendar.get(Calendar.YEAR)+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
        }
        List<SpareConsumeDTO> spareConsumeDTOS = deviceChangeSparePartService.querySpareConsume(startDate, endDate);
        return spareConsumeDTOS;
    }
}
