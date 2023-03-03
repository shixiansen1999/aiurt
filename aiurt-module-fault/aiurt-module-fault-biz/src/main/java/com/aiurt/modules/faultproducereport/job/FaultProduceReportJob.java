package com.aiurt.modules.faultproducereport.job;

import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
import com.aiurt.modules.faultproducereportline.entity.FaultProduceReportLine;
import com.aiurt.modules.faultproducereportline.service.IFaultProduceReportLineService;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;

/**
 * 每日定时任务：定时生成生产日报数据
 * 每日系统自动生成生产日报数据，添加入三个表：fault_produce_report、fault_produce_report_line、fault_produce_report_line_detail
 */
@Slf4j
@Component
//@RequiredArgsConstructor
public class FaultProduceReportJob implements Job {
    @Autowired
    private IFaultService iFaultService;
    @Autowired
    private IFaultProduceReportService faultProduceReportService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private IFaultProduceReportLineService iFaultProduceReportLineService;
    @Autowired
    private IFaultRepairRecordService iFaultRepairRecordService;
    @Autowired
    private IFaultProduceReportLineDetailService iFaultProduceReportLineDetailService;

    /**
     * 参数的格式： 统计截止时间/统计多少个小时内的
     * 举例1：
     *      09:00:00/24   (表示统计昨天09:00:00到今天09:00:00的数据（统计24小时内的数据）)
     * 举例2：
     *      09:00:00/9    (表示统计今天00:00:00到今天09:00:00的数据（统计9小时内的数据）)
     * 举例3：
     *      00:00:00/24  (表示统计昨天00:00:00到今天00:00:00的数据（即统计昨天一天的数据）)
     * 注：例子里的 “今天” 是系统运行时候的时间
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext){
        log.info("自动生成生产日报数据任务执行啦...");
//        String parameter = "00:00:00/24";
        boolean matches = Pattern.matches("\\d{2}:\\d{2}:\\d{2}/\\d+", parameter);
        if (!matches) {
            // 参数格式错误
            log.info("生产日报-数据生成-参数格式错误");
            return;
        }
        String[] parameterSplit = parameter.split("/");
        // 参数的截止时间
        String paramEndTime = parameterSplit[0];
        // 参数的取多少个小时内的数据
        String paramInterval = parameterSplit[1];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 系统运行的日期，即今天日期
        String todayString = dateFormat.format(new Date());
        // 统计截止时间：当前日期+截止时间
        String endTimeString = todayString + " " + paramEndTime;
        Date endDate = DateUtils.parseDate(endTimeString, pattern);
        // 统计开始时间：统计截止时间-统计的时间间隔
        Date beginDate = DateUtils.addHours(endDate, -Integer.parseInt(paramInterval));
        // 统计日期，就是统计开始时间的日期
        Date statisticsDate = beginDate;

        // 查询故障保修单:
        // 查询条件：审核通过(status=12)&approval_pass_time(审核通过时间)在统计时间范围内(昨天的00:00:00到昨天的23:59:59)
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Fault::getStatus, 12);
        queryWrapper.ge(Fault::getApprovalPassTime, beginDate);
        queryWrapper.le(Fault::getApprovalPassTime, endDate);
        List<Fault> faultList = iFaultService.list(queryWrapper);

        // 把faultList按照专业区分开, 使用一个map装起来, majorCode作为key
        Map<String, List<Fault>> map = new HashMap<>();
        for (Fault fault : faultList) {
            String majorCode = fault.getMajorCode();
            if (map.get(majorCode) == null) {
                map.put(majorCode, new ArrayList<Fault>() {{
                    add(fault);
                }});
            } else {
                map.get(majorCode).add(fault);
            }
        }
        // 遍历map,一个key就是一条生产日报的数据
        for (String majorCodeKey : map.keySet()) {
            FaultProduceReport report = new FaultProduceReport();
            report.setMajorCode(majorCodeKey);  // 专业编码
            report.setStatisticsDate(statisticsDate);  // 统计日期
            report.setStartTime(beginDate);  // 统计开始时间
            report.setEndTime(endDate); // 统计截止时间
            report.setState(0);  // 状态、不知道那个数字表示开始，先置为0

            List<Fault> faults = map.get(majorCodeKey);
            report.setTotalNum(faults.size());  // 生产日报故障总数
            Integer delayNum = 0;  // 生产日报延误次数
            // 需要使用一个map来存储线路故障，使用lineCode作为key
            Map<String, List<Fault>> reportLineMap = new HashMap<>();
            for (Fault f : faults) {
                if (f.getAffectDrive() == 1 || f.getAffectPassengerService() == 1 || f.getIsStopService() == 1) {
                    delayNum++;
                }
                String lineCode = f.getLineCode();
                if (reportLineMap.get(lineCode) == null) {
                    reportLineMap.put(lineCode, new ArrayList<Fault>() {{
                        add(f);
                    }});
                } else {
                    reportLineMap.get(lineCode).add(f);
                }
            }
            report.setDelayNum(delayNum);  // 生产日报延误次数
            // 存入一条生产日报数据，并获取存入的生产日报id，线路故障数据要用----------->雪花算法可以存储后可以直接获取id
            faultProduceReportService.save(report);

            // 一条生产日报数据，可能有几条线路故障数据, 看reportLineMap有几个key
            for (String reportLineKey : reportLineMap.keySet()) {
                FaultProduceReportLine reportLine = new FaultProduceReportLine();
                reportLine.setLineCode(reportLineKey);  // 线路编码
                // 根据线路编码，查询线路名称
                Map<String, String> lineNameCodeMap = iSysBaseAPI.getLineNameByCode(new ArrayList<String>() {{
                    add(reportLineKey);
                }});
                reportLine.setLineName(lineNameCodeMap.get(reportLineKey));
                List<Fault> reportLineFaults = reportLineMap.get(reportLineKey);
                reportLine.setTotalNum(reportLineFaults.size());  // 故障总数
                Integer lineDelayNum = 0;
                List<FaultProduceReportLineDetail> reportLineDetailList = new ArrayList<>();
                for (Fault LineFault : reportLineFaults) {
                    if (LineFault.getAffectDrive() == 1 || LineFault.getAffectPassengerService() == 1 || LineFault.getIsStopService() == 1) {
                        lineDelayNum++;
                    }
                    // 一条故障对应一条故障清单
                    FaultProduceReportLineDetail reportLineDetail = new FaultProduceReportLineDetail();
                    reportLineDetail.setLineCode(reportLineKey);  // 线路编码
                    reportLineDetail.setLineName(lineNameCodeMap.get(reportLineKey));  // 线路名称
                    reportLineDetail.setFaultCode(LineFault.getCode());  // 故障编码
                    reportLineDetail.setFaultPhenomenon(LineFault.getSymptoms());  // 故障现象
                    // 处理情况 -- 根据故障编码查询故障维修单（时间排序）
                    LambdaQueryWrapper<FaultRepairRecord> qw = new LambdaQueryWrapper<>();
                    qw.eq(FaultRepairRecord::getFaultCode, LineFault.getCode());
                    qw.orderByDesc(FaultRepairRecord::getUpdateTime);
                    List<FaultRepairRecord> list = iFaultRepairRecordService.list(qw);
                    if (list.size() > 0){
                        reportLineDetail.setMaintenanceMeasures(list.get(0).getMaintenanceMeasures());
                    }
                    reportLineDetail.setAffectDrive(LineFault.getAffectDrive());  // 是否影响行车
                    reportLineDetail.setAffectPassengerService(LineFault.getAffectPassengerService()); // 是否影响客运服务
                    reportLineDetail.setIsStopService(LineFault.getIsStopService()); // 是否停止服务
                    reportLineDetail.setStationCode(LineFault.getStationCode());  // 站点编码
                    // 根据站点编码查询站点名称
                    String stationName = iSysBaseAPI.getStationNameByCode(new ArrayList<String>() {{
                        add(LineFault.getStationCode());
                    }}).get(LineFault.getStationCode());
                    reportLineDetail.setStationName(stationName);
                    reportLineDetail.setFaultProduceReportId(report.getId());
                    reportLineDetailList.add(reportLineDetail);
                }
                reportLine.setDelayNum(lineDelayNum); // 延误次数
                reportLine.setFaultProduceReportId(report.getId());  // 生产日报id
                // 存入线路故障数据
                iFaultProduceReportLineService.save(reportLine);
                // 存入故障清单数据
                iFaultProduceReportLineDetailService.saveBatch(reportLineDetailList);
            }
        }
    }
}
