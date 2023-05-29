package com.aiurt.boot.screen.service;

import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.mapper.TemperatureHumidityMapper;
import com.aiurt.modules.sensorinformation.entity.SensorInformation;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : sbx
 * @Classname : TemperatureHumidityService
 * @Description : TODO
 * @Date : 2023/5/17 9:00
 */
@Slf4j
public class TemperatureHumidityService implements Callable<TemperatureHumidity> {

    private SensorInformation sensor;

    private TemperatureHumidityMapper temperatureHumidityMapper;

    private String port;

    public TemperatureHumidityService(SensorInformation sensor, TemperatureHumidityMapper temperatureHumidityMapper, String port) {
        this.sensor = sensor;
        this.temperatureHumidityMapper = temperatureHumidityMapper;
        this.port = port;
    }
    @Override
    public TemperatureHumidity call() throws Exception {
        Lock lock = new ReentrantLock();
        TemperatureHumidity th = new TemperatureHumidity();
        lock.lock();
        // 记录传感器温湿度
        try {
            //设定CommunityTarget
            CommunityTarget myTarget = new CommunityTarget();
            //定义远程主机的地址
            String deviceIp = "udp:" + sensor.getStationIp() + "/" + port;
            Address deviceAdd = GenericAddress.parse(deviceIp);
            //设定远程主机的地址
            myTarget.setAddress(deviceAdd);
            //设置snmp共同体
            myTarget.setCommunity(new OctetString("public"));
            //设置超时重试次数
            myTarget.setRetries(2);
            //设置超时的时间
            myTarget.setTimeout(5 * 60);
            //设置使用的snmp版本
            myTarget.setVersion(SnmpConstants.version1);

            //设定采取的协议
            //设定传输协议为UDP
            TransportMapping transport = new DefaultUdpTransportMapping();
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
            //调用setType()方法来确定该pdu的类型
            request.setType(PDU.GETNEXT);
            //调用 send(PDU pdu,Target target)发送pdu，返回一个ResponseEvent对象
            ResponseEvent responseEvent = protocol.send(request, myTarget);
            //通过ResponseEvent对象来获得SNMP请求的应答pdu，方法：public PDU getResponse()
            PDU response = responseEvent.getResponse();
            //输出
            if (response != null) {
                //通过应答pdu获得mib信息（之前绑定的OID的值），方法：VaribleBinding get(int index)
                VariableBinding vb1 = response.get(0);
                VariableBinding vb2 = response.get(1);
                DecimalFormat dF = new DecimalFormat("0.0");
                String vb1str = vb1.toString();
                String vb2str = vb2.toString();
                Integer tem = Integer.parseInt(vb1str.substring(24, vb1str.length()));
                Integer hum = Integer.parseInt(vb2str.substring(24, vb2str.length()));
                float temperature = (float) tem / 10;
                float humidity = (float) hum / 10;
                th.setIp(sensor.getStationIp());
                th.setTemperature(temperature);
                th.setHumidity(humidity);
                Date time = new Date(System.currentTimeMillis());
                th.setCreateTime(time);
                System.out.println(time + "   定时任务--temperatureHumidity表插入一条数据...");
                temperatureHumidityMapper.insert(th);
                //调用close()方法释放该进程
                transport.close();
            }
        } catch (IOException e) {
            log.error("传感器温湿度记录失败--线路code：{}，站点code：{}，ip：{}", sensor.getLineCode(), sensor.getStationCode(), sensor.getStationIp());
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return th;
    }
}
