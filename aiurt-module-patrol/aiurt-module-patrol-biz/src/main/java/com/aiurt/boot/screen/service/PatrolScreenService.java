package com.aiurt.boot.screen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
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
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.aiurt.boot.task.mapper.TemperatureHumidityMapper;
import com.aiurt.boot.task.param.TemHumParam;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
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
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
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
            //漏巡
            String omitStartTime = this.getOmitDateScope(startTime).split(ScreenConstant.TIME_SEPARATOR)[0];
            String omitEndTime = this.getOmitDateScope(endTime).split(ScreenConstant.TIME_SEPARATOR)[1];
            module.setStartTime(DateUtil.parse(omitStartTime));
            module.setEndTime(DateUtil.parse(omitEndTime));
            PatrolSituation taskDeviceCount2 = patrolTaskMapper.getTaskDeviceCount(module);
            data.setOmitNumber(taskDeviceCount2.getOmit());
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

            PatrolSituation taskDeviceCount = patrolTaskMapper.getTaskDeviceCount(module);
            data.setPlanNum(taskDeviceCount.getSum());
            data.setFinishNum(taskDeviceCount.getFinish());
            data.setAbnormalNum(taskDeviceCount.getAbnormal());
            //漏巡条件构建
            String omitStartTime = this.getOmitDateScope(startTime).split(ScreenConstant.TIME_SEPARATOR)[0];
            String omitEndTime = this.getOmitDateScope(endTime).split(ScreenConstant.TIME_SEPARATOR)[1];
            module.setStartTime(DateUtil.parse(omitStartTime));
            module.setEndTime(DateUtil.parse(omitEndTime));
            module.setOmit(PatrolConstant.OMIT_STATUS);
            PatrolSituation taskDeviceOmitCount = patrolTaskMapper.getTaskDeviceCount(module);
            data.setOmitNum(taskDeviceOmitCount.getOmit());
            //今日数量构造条件对象
            module.setStartTime(DateUtil.parse(DateUtil.format(today, "yyyy-MM-dd 00:00:00")));
            module.setEndTime(DateUtil.parse(DateUtil.format(today, "yyyy-MM-dd 23:59:59")));
            PatrolSituation taskDeviceCountToday = patrolTaskMapper.getTaskDeviceCount(module);
            data.setTodayNum(taskDeviceCountToday.getSum());
            data.setTodayFinishNum(taskDeviceCountToday.getFinish());

        }else {
        List<PatrolTask> list = patrolTaskMapper.getScreenDataCount(module);
        List<PatrolTask> todayList = list.stream()
                //.filter(l -> DateUtil.format(today, "yyyy-MM-dd").equals(DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd")))
                .filter(l -> {
                    if (l.getSource() == 3) {
                        // 使用 end_date 进行筛选
                        return DateUtil.format(today, "yyyy-MM-dd").equals(DateUtil.format(l.getEndDate(), "yyyy-MM-dd"));
                    } else {
                        // 使用 patrol_date 进行筛选
                        return DateUtil.format(today, "yyyy-MM-dd").equals(DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd"));
                    }
                })
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

        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            ScreenModule moduleType = new ScreenModule();
            moduleType.setOrgCodes(orgCodes);
            moduleType.setLineCode(lineCode);
            moduleType.setStartTime(startTime);
            moduleType.setEndTime(endTime);

            List<DictModel> dictItems1 = sysBaseApi.getDictItems(PatrolDictCode.PATROL_BILL_STATUS);
            Map<String, String> omitItems = sysBaseApi.getDictItems(PatrolDictCode.OMIT_STATUS)
                    .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));

            IPage<ScreenStatisticsTask> pageList = patrolTaskMapper.getStatisticsDataDeviceList(page, moduleType);
            pageList.getRecords().forEach(l -> {

                String taskCode = l.getCode();
                // 巡视用户信息
                QueryWrapper<PatrolTaskUser> userWrapper = new QueryWrapper<>();
                userWrapper.lambda().eq(PatrolTaskUser::getTaskCode, taskCode).eq(PatrolTaskUser::getDelFlag, 0);
                List<PatrolTaskUser> list = patrolTaskUserMapper.selectList(userWrapper);
                // 巡视用户Map
                Map<String, List<PatrolTaskUser>> userMap = list.stream().collect(Collectors.groupingBy(PatrolTaskUser::getTaskCode));

                List<PatrolTaskUser> userList = Optional.ofNullable(userMap.get(taskCode)).orElseGet(ArrayList::new);
                List<String> indexUsers = new ArrayList<>();
                userList.forEach(u -> {
                    if (StrUtil.isEmpty(u.getUserName())) {
                        String username = patrolTaskUserMapper.getUsername(u.getUserId());
                        indexUsers.add(username);
                        return;
                    }
                    indexUsers.add( u.getUserName());
                });
                // 字典翻译
                String statusDictName = dictItems1.stream()
                        .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                        .map(DictModel::getText).collect(Collectors.joining());
                String omitStatusName = omitItems.get(String.valueOf(l.getOmitStatus()));
                l.setStatusName(statusDictName);
                l.setOmitStatusName(omitStatusName);
                l.setUserInfo(CollUtil.join(indexUsers, ","));
            });
            return pageList;

        }
        else {
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
    }



    public ScreenTemHum getTemAndHum(TemHumParam temHumParam){
        boolean b = ObjectUtil.isEmpty(temHumParam.getMode()) || StrUtil.isEmpty(temHumParam.getLineCode()) || StrUtil.isEmpty(temHumParam.getStationCode());
        if (b) {
            throw new AiurtBootException("获取模式、线路和站点code不能为空");
        }
        Date date = DateUtil.date();
        ScreenTemHum screenTemHum = new ScreenTemHum();

        // 获取当前整点时刻的温湿度
        TemHumParam current = new TemHumParam();
        current.setDate(date).setLineCode(temHumParam.getLineCode()).setStationCode(temHumParam.getStationCode()).setMode(ScreenConstant.MODE_0);
        List<TemperatureHumidityDTO> currentTemHumList = patrolTaskMapper.getTemAndHum(current);
        if (CollUtil.isNotEmpty(currentTemHumList)) {
            TemperatureHumidityDTO currentTemHun = currentTemHumList.get(0);
            screenTemHum.setCurrentTemHum(currentTemHun);
        }

        // 获取今日/近一周/近30天温湿度
        temHumParam.setDate(date).setLineCode(temHumParam.getLineCode()).setStationCode(temHumParam.getStationCode());
        switch (temHumParam.getMode()) {
            case ScreenConstant.MODE_1:
                break;
            case ScreenConstant.MODE_2:
                temHumParam.setInterval(ScreenConstant.INTERVAL_7).setHour(ScreenConstant.HOUR_14);
                break;
            case ScreenConstant.MODE_3:
                temHumParam.setInterval(ScreenConstant.INTERVAL_30).setHour(ScreenConstant.HOUR_14);
                break;
            default:
                return screenTemHum;
        }
        List<TemperatureHumidityDTO> temAndHumList = patrolTaskMapper.getTemAndHum(temHumParam);
        screenTemHum.setTemHumList(temAndHumList);
        return screenTemHum;
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
                    if (sum != 0) {
                        String finishRate = String.format("%.1f", (1.0 * taskDeviceCount.getFinish() / sum) * 100);
                        String unfinishRate = String.format("%.1f", (1.0 * taskDeviceCount.getUnfinish() / sum) * 100);
                        graph.setFinishRate(finishRate + "%");
                        graph.setUnfinishRate(unfinishRate + "%");
                    } else {
                        graph.setFinishRate("0%");
                        graph.setUnfinishRate("0%");
                    }

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
                                                             Integer screenModule, String lineCode,String stationCode,String username) {
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
        moduleType.setStationCode(stationCode);
        moduleType.setUsername(username);
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


        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            Integer[] i = {2};
            Integer[] j = {0,1};
            switch (screenModule) {
                // 完成数
                case 2:
                    moduleType.setTaskDeviceStatus(i);
                    break;
                // 漏巡数
                case 3:
                    moduleType.setTaskDeviceStatus(j);
                    break;
                // 巡视异常数
                case 4:
                    moduleType.setState(0);
                    break;
                // 今日巡视完成数
                case 6:
                    moduleType.setTaskDeviceStatus(i);
                    break;
                default:
                    break;
            }
            List<DictModel> dictItems1 = sysBaseApi.getDictItems(PatrolDictCode.PATROL_BILL_STATUS);
            Map<String, String> omitItems = sysBaseApi.getDictItems(PatrolDictCode.OMIT_STATUS)
                    .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));

            IPage<ScreenStatisticsTask> pageList = patrolTaskMapper.getStatisticsDataDeviceList(page, moduleType);
            pageList.getRecords().forEach(l -> {

                String taskCode = l.getCode();
                // 巡视用户信息
                QueryWrapper<PatrolTaskUser> userWrapper = new QueryWrapper<>();
                userWrapper.lambda().eq(PatrolTaskUser::getTaskCode, taskCode).eq(PatrolTaskUser::getDelFlag, 0);
                List<PatrolTaskUser> list = patrolTaskUserMapper.selectList(userWrapper);
                // 巡视用户Map
                Map<String, List<PatrolTaskUser>> userMap = list.stream().collect(Collectors.groupingBy(PatrolTaskUser::getTaskCode));

                List<PatrolTaskUser> userList = Optional.ofNullable(userMap.get(taskCode)).orElseGet(ArrayList::new);
                List<String> indexUsers = new ArrayList<>();
                userList.forEach(u -> {
                    if (StrUtil.isEmpty(u.getUserName())) {
                        String name = patrolTaskUserMapper.getUsername(u.getUserId());
                        indexUsers.add(name);
                        return;
                    }
                    indexUsers.add( u.getUserName());
                });
                // 字典翻译
                String statusDictName = dictItems1.stream()
                        .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                        .map(DictModel::getText).collect(Collectors.joining());
                String omitStatusName = omitItems.get(String.valueOf(l.getOmitStatus()));
                l.setStatusName(statusDictName);
                l.setOmitStatusName(omitStatusName);
                l.setUserInfo(CollUtil.join(indexUsers, ","));
            });
            return pageList;

        } else {
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

    }

    /**
     * 如果参数日期是周一至周四，则返回上周四00时00分00秒和周日23时59分59秒，否则返回周一00时00分00秒和周三23时59分59秒
     *
     * @param date
     * @return
     */
    public String getOmitDateScope(Date date) {
        // 参数日期所在周的周一
        /*Date monday = DateUtils.getWeekStartTime(date);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = monday.toInstant().atZone(zoneId).toLocalDate();
        if (Calendar.THURSDAY == DateUtil.dayOfWeek(date) ||Calendar.FRIDAY == DateUtil.dayOfWeek(date) || Calendar.SATURDAY == DateUtil.dayOfWeek(date)
                || Calendar.SUNDAY == DateUtil.dayOfWeek(date)) {
            // 周一往后2天，星期三
            Date wednesday = Date.from(localDate.plusDays(2).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(monday, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(wednesday, "yyyy-MM-dd 23:59:59"));
        } else {
            // 周一往前3天，星期五（旧）
            //Date friday = Date.from(localDate.minusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前4天，星期四（新）
            Date friday = Date.from(localDate.minusDays(4).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前1天，星期天
            Date sunday = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(friday, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(sunday, "yyyy-MM-dd 23:59:59"));
        }*/
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_WEEKDAYS);
        String value = sysParamModel.getValue();
        String[] split = StrUtil.split(value, ",");
        List<Date> patrolList = new ArrayList();
        List<Integer> weekList = new ArrayList();
        //传入时间一周数据,当前日期所在周
        Date format = DateUtils.getWeekStartTime(date);
        DateTime monday = DateUtil.parse(DateUtil.format(format, "yyyy-MM-dd 00:00:00"));
        DateTime tuesDay = DateUtil.offsetDay(monday, 1);
        DateTime wedDay = DateUtil.offsetDay(monday, 2);
        DateTime thDay = DateUtil.offsetDay(monday, 3);
        DateTime friDay = DateUtil.offsetDay(monday, 4);
        DateTime saDay = DateUtil.offsetDay(monday, 5);
        DateTime sunDay = DateUtil.offsetDay(monday, 6);
        //参数日期所在周漏检日期
        for (String s : split) {
            if (("1").equals(s)) {
                patrolList.add(monday);
            }
            if (("2").equals(s)) {
                patrolList.add(tuesDay);
            }
            if (("3").equals(s)) {
                patrolList.add(wedDay);
            }
            if (("4").equals(s)) {
                patrolList.add(thDay);
            }
            if (("5").equals(s)) {
                patrolList.add(friDay);
            }
            if (("6").equals(s)) {
                patrolList.add(saDay);
            }
            if (("7").equals(s)) {
                patrolList.add(sunDay);
            }
        }
        //漏检开始和结束时间
        Date firstDate = patrolList.stream().min(Comparator.comparingLong(Date::getTime)).get();
        Date secondDate = patrolList.stream().max(Comparator.comparingLong(Date::getTime)).get();
        long betweenDay = DateUtil.between(firstDate, secondDate, DateUnit.DAY);

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = firstDate.toInstant().atZone(zoneId).toLocalDate();
        if (date.after(firstDate) && date.before(secondDate) || date.equals(firstDate)) {
            // 第一次漏检往前推两次漏检间隔天数
            Date start = Date.from(localDate.minusDays(7 - betweenDay).atStartOfDay().atZone(zoneId).toInstant());
            // 第一次漏检往前推1天
            Date end = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(start, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(end, "yyyy-MM-dd 23:59:59"));

        } else {
            if (date.before(firstDate)) {
                Date start = Date.from(localDate.minusDays(7).atStartOfDay().atZone(zoneId).toInstant());
                // 第一次漏检往前1天
                Date end = Date.from(localDate.minusDays(7 - betweenDay).atStartOfDay().atZone(zoneId).toInstant());
                return DateUtil.format(start, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(end, "yyyy-MM-dd 00:00:00"));

            } else {
                // 第一次漏检往后推两次检修间隔天数
                return DateUtil.format(firstDate, "yyyy-MM-dd 00:00:00").concat(ScreenConstant.TIME_SEPARATOR).concat(DateUtil.format(secondDate, "yyyy-MM-dd 00:00:00"));

            }
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
