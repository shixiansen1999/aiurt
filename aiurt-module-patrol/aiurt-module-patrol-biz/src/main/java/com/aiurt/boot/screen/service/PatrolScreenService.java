package com.aiurt.boot.screen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.PatrolDictCode;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.screen.constant.ScreenConstant;
import com.aiurt.boot.screen.model.*;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.statistics.model.PatrolSituation;
import com.aiurt.boot.task.dto.TemperatureHumidityDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.TemperatureHumidityMapper;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.DateUtils;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author JB
 * @Description: 大屏巡视模块业务层
 */
@Service
public class PatrolScreenService {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private TemperatureHumidityMapper temperatureHumidityMapper;
    @Autowired
    private ISysParamAPI sysParamApi;

    /**
     * 大屏巡视模块-重要数据展示
     *
     * @param timeType
     * @param lineCode
     * @return
     */
    public ScreenImportantData getImportantData(Integer timeType, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split(ScreenConstant.TIME_SEPARATOR);
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);

        List<String> orgCodes = sysBaseApi.getTeamBylineAndMajor(lineCode);
        if (CollectionUtil.isEmpty(orgCodes)) {
            return new ScreenImportantData(0L, 0L, 0L);
        }
        ScreenModule module = new ScreenModule();
        module.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
        module.setOrgCodes(orgCodes);
        module.setStartTime(startTime);
        module.setEndTime(endTime);
        module.setLineCode(lineCode);
        ScreenImportantData data = new ScreenImportantData();
        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            module.setOmit(PatrolConstant.OMIT_STATUS);
            PatrolSituation taskDeviceCount = patrolTaskMapper.getTaskDeviceCount(module);
            data.setPatrolNumber(taskDeviceCount.getSum());
            data.setFinishNumber(taskDeviceCount.getFinish());
            data.setOmitNumber(taskDeviceCount.getOmit());
        } else {
            List<PatrolTask> list = patrolTaskMapper.getScreenDataCount(module);
            String omitStartTime = this.getOmitDateScope(startTime).split(ScreenConstant.TIME_SEPARATOR)[0];
            String omitEndTime = this.getOmitDateScope(endTime).split(ScreenConstant.TIME_SEPARATOR)[1];
            module.setStartTime(DateUtil.parse(omitStartTime));
            module.setEndTime(DateUtil.parse(omitEndTime));
            module.setOmit(PatrolConstant.OMIT_STATUS);
            long planNum = list.stream().count();
            long finishNum = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
            long omitNum = patrolTaskMapper.getScreenDataCount(module).stream().count();
            data.setPatrolNumber(planNum);
            data.setFinishNumber(finishNum);
            data.setOmitNumber(omitNum);
        }
        return data;
    }

    /**
     * 大屏巡视模块-巡视数据统计
     *
     * @param timeType
     * @param lineCode
     * @return
     */
    public ScreenStatistics getStatisticsData(Integer timeType, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split(ScreenConstant.TIME_SEPARATOR);
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);

        List<String> orgCodes = sysBaseApi.getTeamBylineAndMajor(lineCode);
        if (CollectionUtil.isEmpty(orgCodes)) {
            return new ScreenStatistics(0L, 0L, 0L, 0L, 0L, 0L);
        }
        ScreenStatistics data = new ScreenStatistics();
        ScreenModule module = new ScreenModule();
        module.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
        module.setOrgCodes(orgCodes);
        module.setStartTime(startTime);
        module.setEndTime(endTime);
        module.setLineCode(lineCode);
        //今日时间
        Date today = new Date();

        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            //指定时间范围数量
            module.setOmit(PatrolConstant.OMIT_STATUS);
            PatrolSituation taskDeviceCount = patrolTaskMapper.getTaskDeviceCount(module);
            data.setPlanNum(taskDeviceCount.getSum());
            data.setFinishNum(taskDeviceCount.getFinish());
            data.setOmitNum(taskDeviceCount.getOmit());
            data.setAbnormalNum(taskDeviceCount.getAbnormal());
            //今日数量构造条件对象
            module.setStartTime(DateUtil.parse(DateUtil.format(today, "yyyy-MM-dd 00:00:00")));
            module.setEndTime(DateUtil.parse(DateUtil.format(today, "yyyy-MM-dd 23:59:59")));
            PatrolSituation taskDeviceCountToday = patrolTaskMapper.getTaskDeviceCount(module);
            data.setTodayNum(taskDeviceCountToday.getSum());
            data.setTodayFinishNum(taskDeviceCountToday.getFinish());

        }else {
        List<PatrolTask> list = patrolTaskMapper.getScreenDataCount(module);
        List<PatrolTask> todayList = list.stream()
                .filter(l -> DateUtil.format(today, "yyyy-MM-dd").equals(DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd")))
                .collect(Collectors.toList());
        if (!ScreenConstant.THIS_WEEK.equals(timeType) && !ScreenConstant.THIS_MONTH.equals(timeType)) {
            //今日数量构造条件对象
            module.setStartTime(DateUtil.parse(DateUtil.format(today, "yyyy-MM-dd 00:00:00")));
            module.setEndTime(DateUtil.parse(DateUtil.format(today, "yyyy-MM-dd 23:59:59")));
            todayList = patrolTaskMapper.getScreenDataCount(module);
        }
        long planNum = list.stream().count();
        long finishNum = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
        //漏巡条件构建
        String omitStartTime = this.getOmitDateScope(startTime).split(ScreenConstant.TIME_SEPARATOR)[0];
        String omitEndTime = this.getOmitDateScope(endTime).split(ScreenConstant.TIME_SEPARATOR)[1];
        module.setStartTime(DateUtil.parse(omitStartTime));
        module.setEndTime(DateUtil.parse(omitEndTime));
        module.setOmit(PatrolConstant.OMIT_STATUS);
        long omitNum = patrolTaskMapper.getScreenDataCount(module).stream().count();

        long abnormalNum = list.stream().filter(l -> PatrolConstant.TASK_ABNORMAL.equals(l.getAbnormalState())).count();
        long todayNum = todayList.stream().count();
        long todayFinishNum = todayList.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();

        data.setPlanNum(planNum);
        data.setFinishNum(finishNum);
        data.setOmitNum(omitNum);
        data.setAbnormalNum(abnormalNum);
        data.setTodayNum(todayNum);
        data.setTodayFinishNum(todayFinishNum);
        }
        return data;
    }

    /**
     * 大屏巡视模块-巡视数据统计任务列表
     *
     * @param page
     * @param timeType
     * @param lineCode
     * @return
     */
    public IPage<ScreenStatisticsTask> getStatisticsTaskInfo(Page<ScreenStatisticsTask> page, Integer timeType, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split(ScreenConstant.TIME_SEPARATOR);
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);

        List<String> orgCodes = sysBaseApi.getTeamBylineAndMajor(lineCode);
        if (CollectionUtil.isEmpty(orgCodes)) {
            return page;
        }
        ScreenTran tran = new ScreenTran();
        tran.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
        tran.setStartTime(startTime);
        tran.setEndTime(endTime);
        tran.setOrgCodes(orgCodes);
        tran.setLineCode(lineCode);

        IPage<ScreenStatisticsTask> list = patrolTaskMapper.getScreenTask(page,tran);

        // 字典翻译
        Map<String, String> statusItems = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        Map<String, String> omitItems = sysBaseApi.getDictItems(PatrolDictCode.OMIT_STATUS)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        Map<String, String> abnormalItems = sysBaseApi.getDictItems(PatrolDictCode.ABNORMAL_STATE)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        for (ScreenStatisticsTask task : list.getRecords()) {
            String statusName = statusItems.get(String.valueOf(task.getStatus()));
            String omitStatusName = omitItems.get(String.valueOf(task.getOmitStatus()));
            String abnormalName = abnormalItems.get(String.valueOf(task.getAbnormalState()));
            task.setStatusName(statusName);
            task.setOmitStatusName(omitStatusName);
            task.setAbnormalStateName(abnormalName);
        }
        return list;
    }



    public List<TemperatureHumidityDTO> getTemAndHum(String date, String lineCode, String stationCode){
        if (date!=null && StrUtil.isNotEmpty(lineCode) && StrUtil.isNotEmpty(stationCode)) {
            List<TemperatureHumidityDTO> temAndHum = patrolTaskMapper.getTemAndHum(date, lineCode, stationCode);
            return temAndHum;
        }else {
        }
        return null;
    }

    public List<TemperatureHumidity> getTemAndHumTest(String date){
        String ip="192.168.1.187";
        try{
            //设定CommunityTarget
            CommunityTarget myTarget = new CommunityTarget();
            //定义远程主机的地址
            Address deviceAdd = GenericAddress.parse("udp:192.168.1.187/161");
            //定义本机的地址
//            Address localAdd = GenericAddress.parse("udp:192.168.1.187/161");
            //设定远程主机的地址
            myTarget.setAddress(deviceAdd);
            //设定本地主机的地址
//            myTarget.setAddress(localAdd);
            //设置snmp共同体
            myTarget.setCommunity(new OctetString("public"));
            //设置超时重试次数
            myTarget.setRetries(2);
            //设置超时的时间
            myTarget.setTimeout(5*60);
            //设置使用的snmp版本
            myTarget.setVersion(SnmpConstants.version1);

            //设定采取的协议
            TransportMapping transport = new DefaultUdpTransportMapping();//设定传输协议为UDP
            //调用TransportMapping中的listen()方法，启动监听进程，接收消息，由于该监听进程是守护进程，最后应调用close()方法来释放该进程
            transport.listen();
            //创建SNMP对象，用于发送请求PDU
            Snmp protocol = new Snmp(transport);
            //创建请求pdu,获取mib
            PDU request = new PDU();
            //调用的add方法绑定要查询的OID
            //温度
            request.add(new VariableBinding(new OID("1.3.6.1.4.1.58162.0.0")));
            //湿度
            request.add(new VariableBinding(new OID("1.3.6.1.4.1.58162.1.0")));
//            request.add(new VariableBinding(new OID(new int[] {1,3,6,1,4,1,58162,2,0})));
            //调用setType()方法来确定该pdu的类型
            request.setType(PDU.GETNEXT);
            //调用 send(PDU pdu,Target target)发送pdu，返回一个ResponseEvent对象
            ResponseEvent responseEvent = protocol.send(request, myTarget);
            //通过ResponseEvent对象来获得SNMP请求的应答pdu，方法：public PDU getResponse()
            PDU response=responseEvent.getResponse();
            //输出
            if(response != null){
                System.out.println("request.size()="+request.size());
//                System.out.println("response.size()="+response.size());
                //通过应答pdu获得mib信息（之前绑定的OID的值），方法：VaribleBinding get(int index)
                VariableBinding vb1 = response.get(0);
//                String variableBinding = (String)response.get(0);
                VariableBinding vb2 = response.get(1);
                System.out.println(vb1);
                System.out.println(vb1.toString().substring(24,27));
                System.out.println(vb2);
                System.out.println(vb2.toString().substring(24,27));
                DecimalFormat dF = new DecimalFormat("0.0");
                Integer tem=Integer.parseInt(vb1.toString().substring(24,27));
                Integer hum=Integer.parseInt(vb2.toString().substring(24,27));
                float temperature = (float)tem / 10;
                float humidity=(float)hum / 10;
//                String temperature=dF.format((float)tem/10);
//                String humidity=dF.format((float)hum/10);
                TemperatureHumidity th = new TemperatureHumidity();
//                th.setId("114514");
                th.setIp(ip);
                th.setTemperature(temperature);
                th.setHumidity(humidity);
                Date time = new Date(System.currentTimeMillis());
                th.setCreateTime(time);
                temperatureHumidityMapper.insert(th);
                List<TemperatureHumidity> list = new ArrayList<>();
                list.add(th);
                //调用close()方法释放该进程
                transport.close();
                return list;
                /**
                 * 输出结果：
                 * request.size()=2
                 * 1.3.6.1.4.1.58162.1.0 = 243
                 * 1.3.6.1.4.1.58162.2.0 = 337
                 */
                // TODO: 2023/3/14 把这个改成一个轮询的方法
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 大屏巡视模块-巡视任务完成情况
     *
     * @param lineCode
     * @return
     */
    public List<ScreenStatisticsGraph> getStatisticsGraph(String lineCode) {
        String dateTime = ScreenDateUtil.getDateTime(ScreenConstant.THIS_WEEK);
        String[] split = dateTime.split(ScreenConstant.TIME_SEPARATOR);
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);

        List<String> orgCodes = sysBaseApi.getTeamBylineAndMajor(lineCode);
        if (CollectionUtil.isEmpty(orgCodes)) {
            return new ArrayList<>();
        }

        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            List<ScreenStatisticsGraph> list = new ArrayList<>();

            ScreenModule module = new ScreenModule();
            module.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
            module.setStartTime(startTime);
            module.setEndTime(endTime);
            module.setLineCode(lineCode);
            module.setOmit(PatrolConstant.OMIT_STATUS);

            for (String orgCode : orgCodes) {
                module.setOrgCodes(CollUtil.newArrayList(orgCode));
                String departName = sysBaseApi.getDepartNameByOrgCode(orgCode);
                PatrolSituation taskDeviceCount = patrolTaskMapper.getTaskDeviceCount(module);
                if (ObjectUtil.isNotEmpty(taskDeviceCount)) {
                    ScreenStatisticsGraph graph = new ScreenStatisticsGraph();
                    graph.setOrgName(departName);
                    Long sum = taskDeviceCount.getSum();
                    graph.setTotal(sum);
                    graph.setFinish(taskDeviceCount.getFinish());
                    graph.setUnfinish(taskDeviceCount.getUnfinish());
                    String finishRate = String.format("%.1f", (1.0 * taskDeviceCount.getFinish() / sum) * 100);
                    String unfinishRate = String.format("%.1f", (1.0 * taskDeviceCount.getUnfinish() / sum) * 100);
                    graph.setFinishRate(finishRate + "%");
                    graph.setUnfinishRate(unfinishRate + "%");
                    list.add(graph);
                }
            }
            return list;
        } else {
            ScreenTran tran = new ScreenTran();
            tran.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
            tran.setStartTime(startTime);
            tran.setEndTime(endTime);
            tran.setOrgCodes(orgCodes);
            tran.setLineCode(lineCode);

            List<ScreenStatisticsGraph> list = patrolTaskMapper.getScreenGraph(tran);
            for (ScreenStatisticsGraph graph : list) {
                Long total = graph.getTotal();
                String finishRate = String.format("%.1f", (1.0 * graph.getFinish() / total) * 100);
                String unfinishRate = String.format("%.1f", (1.0 * graph.getUnfinish() / total) * 100);
                graph.setFinishRate(finishRate + "%");
                graph.setUnfinishRate(unfinishRate + "%");
            }
            return list;
        }
    }

    /**
     * 大屏巡视模块-巡视数据统计详情列表
     *
     * @param page
     * @param screenModule
     * @param lineCode
     * @return
     */
    public IPage<ScreenStatisticsTask> getStatisticsDataList(Page<ScreenStatisticsTask> page, Integer timeType,
                                                             Integer screenModule, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        // 模块参数未传直接返回空
        if (ObjectUtil.isEmpty(screenModule)) {
            return page;
        }

        List<String> orgCodes = sysBaseApi.getTeamBylineAndMajor(lineCode);
        if (CollectionUtil.isEmpty(orgCodes)) {
            return page;
        }
        ScreenModule moduleType = new ScreenModule();
        moduleType.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
        moduleType.setOrgCodes(orgCodes);
        moduleType.setLineCode(lineCode);
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split(ScreenConstant.TIME_SEPARATOR);
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        switch (screenModule) {
            // 计划数
            case 1:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                break;
            // 完成数
            case 2:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                moduleType.setStatus(PatrolConstant.TASK_COMPLETE);
                break;
            // 漏巡数
            case 3:
                String omitStartTime = this.getOmitDateScope(startTime).split(ScreenConstant.TIME_SEPARATOR)[0];
                String omitEndTime = this.getOmitDateScope(endTime).split(ScreenConstant.TIME_SEPARATOR)[1];
                moduleType.setStartTime(DateUtil.parse(omitStartTime));
                moduleType.setEndTime(DateUtil.parse(omitEndTime));
                moduleType.setOmit(PatrolConstant.OMIT_STATUS);
                break;
            // 巡视异常数
            case 4:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                moduleType.setAbnormal(PatrolConstant.TASK_ABNORMAL);
                break;
            // 今日巡视数
            case 5:
                moduleType.setToday(new Date());
                break;
            // 今日巡视完成数
            case 6:
                moduleType.setToday(new Date());
                moduleType.setStatus(PatrolConstant.TASK_COMPLETE);
                break;
            // 默认计划数
            default:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                break;
        }
        IPage<ScreenStatisticsTask> pageList = patrolTaskMapper.getStatisticsDataList(page, moduleType);
        // 字典翻译
        Map<String, String> statusItems = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        Map<String, String> omitItems = sysBaseApi.getDictItems(PatrolDictCode.OMIT_STATUS)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        Map<String, String> abnormalItems = sysBaseApi.getDictItems(PatrolDictCode.ABNORMAL_STATE)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        for (ScreenStatisticsTask task : pageList.getRecords()) {
            String statusName = statusItems.get(String.valueOf(task.getStatus()));
            String omitStatusName = omitItems.get(String.valueOf(task.getOmitStatus()));
            String abnormalName = abnormalItems.get(String.valueOf(task.getAbnormalState()));
            task.setStatusName(statusName);
            task.setOmitStatusName(omitStatusName);
            task.setAbnormalStateName(abnormalName);
        }
        return pageList;
    }

    /**
     * 如果参数日期是周一至周四，则返回上周五00时00分00秒和周日23时59分59秒，否则返回周一00时00分00秒和周四23时59分59秒
     *
     * @param date
     * @return
     */
    public String getOmitDateScope(Date date) {
        // 参数日期所在周的周一
        Date monday = DateUtils.getWeekStartTime(date);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = monday.toInstant().atZone(zoneId).toLocalDate();
        if (Calendar.FRIDAY == DateUtil.dayOfWeek(date) || Calendar.SATURDAY == DateUtil.dayOfWeek(date)
                || Calendar.SUNDAY == DateUtil.dayOfWeek(date)) {
            // 周一往后3天，星期四
            Date thursday = Date.from(localDate.plusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(monday, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(thursday, "yyyy-MM-dd 23:59:59"));
        } else {
            // 周一往前3天，星期五
            Date friday = Date.from(localDate.minusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前1天，星期天
            Date sunday = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(friday, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(sunday, "yyyy-MM-dd 23:59:59"));
        }
    }
    /**
     * 获取当前登录用户的专业编号
     *
     * @return
     */
    public List<String> getCurrentLoginUserMajors() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录系统，请登录后操作！");
        }
        List<CsUserMajorModel> majorList = sysBaseApi.getMajorByUserId(loginUser.getId());
        List<String> majors = majorList.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        return majors;
    }
}
