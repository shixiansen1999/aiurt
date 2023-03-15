package com.aiurt.boot.screen.job;

import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.mapper.TemperatureHumidityMapper;
import freemarker.core.HTMLOutputFormat;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每日2点系统记录传感器的温湿度
 */
@Slf4j
@Component
public class TemperatureHumidityJob implements Job {

    @Autowired
    TemperatureHumidityMapper temperatureHumidityMapper;
    // TODO: 2023/3/15 ip 传感器暂定，若有多个ip再考虑组装
    private String ip="192.168.1.187";
    //端口统一设置为161
    private String port="161";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("开始采集传感器的温湿度...");
        try{
            //设定CommunityTarget
            CommunityTarget myTarget = new CommunityTarget();
            //定义远程主机的地址
            String deviceIp="udp:"+ip+"/"+port;
            Address deviceAdd = GenericAddress.parse(deviceIp);
//            Address deviceAdd = GenericAddress.parse("udp:192.168.1.187/161");
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
                System.out.println(time+"   定时任务--temperatureHumidity表插入一条数据...");
                temperatureHumidityMapper.insert(th);
//                List<TemperatureHumidity> list = new ArrayList<>();
//                list.add(th);
                //调用close()方法释放该进程
                transport.close();
                /**
                 * 输出结果：
                 * request.size()=2
                 * 1.3.6.1.4.1.58162.1.0 = 243
                 * 1.3.6.1.4.1.58162.2.0 = 337
                 */
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
