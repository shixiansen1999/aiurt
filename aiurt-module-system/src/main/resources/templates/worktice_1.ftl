<!DOCTYPE html>

<html lang="en">
<head>
</head>
<body>
<style media="print">
    /*@page {*/
    /*    size: auto;  !* auto is the initial value *!*/
    /*    margin: 0mm; !* this affects the margin in the printer settings *!*/
    /*}*/
    @page {

        size: auto;  /* auto is the initial value */

        /*margin: 0mm; !* this affects the margin in the printer settings *!*/

        margin-top: 0.5cm;

        margin-left: 0;

        margin-right: 0;

        margin-bottom: 0.9cm;

        /*padding-bottom: 0.5cm;*/

    }
    /*table{ page-break-inside: avoid;}*/
    tr{page-break-inside: avoid;  }

    td{page-break-inside: avoid; }

    thead {display: table-header-group;}


</style>
<div><span style="text-align:right;"><p id="bhts" style="white-space:pre-wrap;"></p></span></div>

<div id="table-form"  style="width:90%;  margin: 0 auto; ">

<#--    style="height: 100vh; "-->
    <div id="div-print1"  >

<#--        <div style="text-align:center;">-->
<#--            <span style="font-size:20px;">变电所第一种工作票(第一页)</span>-->
<#--            <span id="dyp" style="float:right;font-size:18px;border:1px solid #000"><#if data.ticketType??>${data.ticketType}<#else></#if></span>-->
<#--        </div>-->
<#--        <div style="clear:both"></div>-->

<#--        <span style="float:left"><span>${data.station!}</span>&emsp;站（所）</span>-->
<#--        <span style="float:right">第<span t_id="gzph">${data.workTicketCode!}</span>号</span>-->



        <table border="1" style=" width:100%;  border-collapse:collapse;table-layout: fixed; text-align:left; ">
            <thead >
                <tr style="border-top: none;border-left: none;border-right: none; text-align: center">

                    <td colspan="10" >

                        <div style="text-align:center;">

                            <span style="font-size:20px;">变电所第一种工作票</span>

                            <span id="dyp" style="float:right;font-size:18px;border:1px solid #000"><#if data.ticketType??>${data.ticketType}<#else></#if></span>

                        </div>

                        <div style="clear:both"></div>

                        <span style="float:left"><span>${data.station!}</span>&emsp;站（所）</span>

                        <span style="float:right">第<span t_id="gzph">${data.workTicketCode!}</span>号</span>

                    </td>

                </tr>

            </thead>
            <tbody style="vertical-align: top;">
            <tr>
                <td colspan="2">作业地点及内容</td>
                <td colspan="6">${data.workAddressContent!}</td>
                <td>填票人</td>
                <td>${data.ticketFiller!}</td>
            </tr>
            <tr>
                <td colspan="2">工作票有效期</td>
                <td colspan="8">自 <span>${data.workStartTime!}</span> 至 <span>${data.workEndTime!}</span> 止</td>
            </tr>
            <tr>
                <td colspan="2">工作负责人</td>
                <td colspan="8" t_id="gzfzr">${data.workLeader!}</td>
            </tr>


            <#list data.resNames as nameList>

                <#if  (nameList_index) % 4 = 0>
                    <tr>
                </#if>

                <#if nameList_index = 0>
                    <tr>
                    <td colspan="2" rowspan="4">工作组成员</td>
                    <td colspan="2" t_id="bzcy4">${nameList!""}</td>
                </#if>

                <#if (nameList_index > 0)>
                    <td colspan="2" t_id="bzcy4">${nameList!""}</td>
                </#if>

                <#if  ((nameList_index+1) % 4 = 0 && nameList_index > 0)>
                    </tr>
                </#if>
            </#list>


            <tr>
                <td colspan="10">
                    <span style="float:right">共计：<span t_id="rs"> ${data.count!0} </span> 人</span>
                </td>
            </tr>



<#--            <tr id="traq" style=" height:55vh;">-->
<#--                <td colspan="5">-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>必须采取的安全措施</b><br>-->
<#--                        <b>1.断开的断路器和断开的隔离开关：</b>-->
<#--                        <div t_id="aqcs1" style="white-space:pre-wrap;width:98%;display: block;"><#if data.circuitBreakerSwitch??>${data.circuitBreakerSwitch}<#else>""</#if></div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>2.安装接地线（或接地刀闸）的位置：</b>-->
<#--                        <div t_id="aqcs2" style="white-space:pre-wrap;width:98%;display: block;"><#if data.groundWireStation??>${data.groundWireStation}<#else>""</#if></div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>3.装设防护栅、悬挂标示牌的位置：</b>-->
<#--                        <div t_id="aqcs3" style="white-space:pre-wrap;width:98%;display: block;"><#if data.signboardStation??>${data.signboardStation}<#else>""</#if></div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>4.注意作业地点附近有电的设备：</b>-->
<#--                        <div t_id="aqcs4" style="white-space:pre-wrap;width:98%;display: block;"><#if data.electricEquipmentOl??>${data.electricEquipmentOl}<#else>""</#if></div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>5.其他安全措施：</b>-->
<#--                        <div t_id="aqcs5" style="white-space:pre-wrap;width:98%;display: block;"><#if data.safetyMeasures??>${data.safetyMeasures}<#else>""</#if></div>-->
<#--                    </div>-->
<#--                </td>-->

<#--                <td colspan="5" >-->
<#--                    <div style=" height: 11vh;">-->
<#--                        <b>已经完成的安全措施</b>-->
<#--                        <span id="spanimg"><img t_id="gdtp"></span>-->
<#--                        <br>-->
<#--                        <b>1.断开的断路器和断开的隔离开关：</b>-->

<#--                        <div t_id="aqcs6" style="white-space:pre-wrap;width:98%;display: block;color:black;">-->
<#--                            <#if data.completedCircuitBreakerSwitch??>${data.completedCircuitBreakerSwitch}<#else></#if>-->
<#--                        </div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>2.安装接地线（或接地刀闸）的位置：</b>-->
<#--                        <div t_id="aqcs7" style="white-space:pre-wrap;width:98%;display: block;color:black;">-->
<#--                            <#if data.completedGroundWireStation??>${data.completedGroundWireStation}<#else></#if>-->
<#--                        </div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>3.装设防护栅、悬挂标示牌的位置：</b>-->
<#--                        <div t_id="aqcs8" style="white-space:pre-wrap;width:98%;display: block;color:black;">-->
<#--                            <#if data.completedSignboardStation??>${data.completedSignboardStation}<#else></#if>-->
<#--                        </div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>4.注意作业地点附近有电的设备：</b>-->
<#--                        <div t_id="aqcs9" style="white-space:pre-wrap;width:98%;display: block;color:black;">-->
<#--                            <#if data.completedElectricEquipmentOl??>${data.completedElectricEquipmentOl}<#else></#if>-->
<#--                        </div>-->
<#--                    </div>-->

<#--                    <div style=" height: 11vh;">-->
<#--                        <b>5.其他安全措施：</b>-->
<#--                        <div t_id="aqcs10" style="white-space:pre-wrap;width:98%;display: block;color:black;">-->
<#--                            <#if data.completedSafetyMeasures??>${data.completedSafetyMeasures}<#else></#if>-->
<#--                        </div>-->
<#--                    </div>-->

<#--                </td>-->
<#--            </tr>-->


            <tr style="height: 13vh">
                <td colspan="5">

                        <b>必须采取的安全措施</b><br>
                        <b>1.断开的断路器和断开的隔离开关：</b>

                    <div t_id="aqcs1" style="white-space:pre-wrap;width:98%;display: block; min-height: 8vh"><#if data.circuitBreakerSwitch??>${data.circuitBreakerSwitch}<#else>""</#if>
                    </div>
                </td>
                <td colspan="5" style="padding-top: 0">


                            <b>已经完成的安全措施</b>
                            <span id="spanimg"><img t_id="gdtp"></span>
                            <br>
                            <b>1.断开的断路器和断开的隔离开关：</b>


                        <div t_id="aqcs6" style="white-space:pre-wrap;width:98%;display: block;color:black;min-height: 8vh"><#if data.completedCircuitBreakerSwitch??>${data.completedCircuitBreakerSwitch}<#else></#if>
                        </div>

                </td>
            </tr>

            <tr style="height: 13vh">
                <td colspan="5">
                    <b>2.安装接地线（或接地刀闸）的位置：</b>
                    <div t_id="aqcs2" style="white-space:pre-wrap;width:98%;display: block; min-height: 10vh"><#if data.groundWireStation??>${data.groundWireStation}<#else>""</#if>
                    </div>
                </td>

                <td colspan="5">
                    <b>2.安装接地线（或接地刀闸）的位置：</b>
                      <div t_id="aqcs7" style="white-space:pre-wrap;width:98%;display: block;color:black; min-height: 10vh">
                          <#if data.completedGroundWireStation??>${data.completedGroundWireStation}<#else></#if>
                      </div>
                </td>
            </tr>

            <tr style="height: 13vh">
                <td colspan="5">
                    <b>3.装设防护栅、悬挂标示牌的位置：</b>
                    <div t_id="aqcs8" style="white-space:pre-wrap;width:98%;display: block;color:black; min-height: 10vh">
                        <#if data.completedSignboardStation??>${data.completedSignboardStation}<#else></#if>
                    </div>
                </td>
                <td colspan="5">
                    <b>3.装设防护栅、悬挂标示牌的位置：</b>
                                           <div t_id="aqcs8" style="white-space:pre-wrap;width:98%;display: block;color:black; min-height: 10vh">
                                              <#if data.completedSignboardStation??>${data.completedSignboardStation}<#else></#if>
                                           </div>
                </td>
            </tr>

            <tr style="height: 13vh">
                <td colspan="5">
                    <b>4.注意作业地点附近有电的设备：</b>
                    <div t_id="aqcs4" style="white-space:pre-wrap;width:98%;display: block; min-height: 10vh"><#if data.electricEquipmentOl??>${data.electricEquipmentOl}<#else>""</#if></div>
                </td>
                <td colspan="5">
                    <b>4.注意作业地点附近有电的设备：</b>
                    <div t_id="aqcs9" style="white-space:pre-wrap;width:98%;display: block;color:black; height: 10vh">
                                               <#if data.completedElectricEquipmentOl??>${data.completedElectricEquipmentOl}<#else></#if>
                    </div>
                </td>
            </tr>

            <tr style="height: 13vh">
                <td colspan="5">
                    <b>5.其他安全措施：</b>
                    <div t_id="aqcs5" style="white-space:pre-wrap;width:98%;display: block; min-height: 10vh"><#if data.safetyMeasures??>${data.safetyMeasures}<#else>""</#if></div>
                </td>
                <td colspan="5">
                    <b>5.其他安全措施：</b>
                    <div t_id="aqcs10" style="white-space:pre-wrap;width:98%;display: block;color:black; min-height: 10vh">
                                             <#if data.completedSafetyMeasures??>${data.completedSafetyMeasures}<#else></#if>
                    </div>
                </td>
            </tr>



            <tr>
                <td colspan="10" style="padding: 2vh"> 工作负责人:<u
                            t_id="gzfzr"><#if data.workLeaderSign??>${data.workLeaderSign}<#else>&emsp;&emsp;&emsp;&emsp;</#if></u><span
                            style="float:right">签字时间:<u
                                t_id="gzfzrtime"><#if data.workLeaderSignTime??>${data.workLeaderSignTime}<#else>&emsp;&emsp;&emsp;&emsp;</#if></u></span><br>
                    &emsp;&emsp;签发人:<u
                            t_id="qfr"><#if data.signeUser??>${data.signeUser}<#else>&emsp;&emsp;&emsp;&emsp;</#if></u><span
                            style="float:right">签字时间:<u
                                t_id="qfrtime"><#if data.signeUserTime??>${data.signeUserTime}<#else>&emsp;&emsp;&emsp;&emsp;</#if></u></span><br>
                    &emsp;&emsp;&emsp;电调:<u
                            t_id="qrr"><#if data.powerDispatcherName??>${data.powerDispatcherName}<#else>&emsp;&emsp;&emsp;&emsp;</#if></u><span
                            style="float:right">签字时间:<u
                                t_id="qrrtime"><#if data.powerDispatcherTime??>${data.powerDispatcherTime}<#else>&emsp;&emsp;&emsp;&emsp;</#if></u></span><br>
                </td>
            </tr>

<#--        </table>-->
<#--    </div>-->



<#--    <div id="div-print2" style="height: 100vh;">-->
<#--        <div style="text-align:center;">-->
<#--            <span style="font-size:20px;">变电所第一种工作票(第二页)</span>-->
<#--            <span id="dyp" style="float:right;font-size:18px;border:1px solid #000">${data.ticketType!""}</span>-->
<#--        </div>-->
<#--        <div style="clear:both"></div>-->
<#--        <span style="float:left"><span t_id="zd">${data.station!""}</span>&emsp;站（所）</span> <span-->
<#--                style="float:right">第<span-->
<#--                    t_id="gzph">${data.workTicketCode!""}</span>号</span>-->
<#--        <table border="1" style="width:100%;border-collapse:collapse;text-align:left;font-size: 12px;table-layout: fixed;">-->
            <tr >
<#--                style="padding:30px"-->
                <td colspan="10" >

                    <br> 根据电力调度员<u
                            t_id="t1">${data.filePowerDispatcher!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>发布第<u
                            t_id="t2">${data.fileWorkTicketCode!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>号命令<br><br>准予在<u
                            t_id="t3">${data.fileStartTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>开始工作。<br><br>实际于<u
                            t_id="t4">${data.fileActualTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>安全措施已经做好。<br><br>
                    <span style="float:right">工作许可人:<u
                                t_id="t5">${data.workPermitHolder1!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br><br>
                    经检查安全措施已经做好。实际于<u
                            t_id="t6">${data.fileConfirmActualTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>开始工作。<br><br>
                    <span style="float:right">工作负责人:<u
                                t_id="t7">${data.fileWorkLeader!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br>
                    变更作业组成员记录：<u t_id="t8">${data.fileRecord!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u><br><br>
                    <span style="float:right">批准人（工作负责人或签发人）：<u t_id="t9">${data.approvedUser!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br><br>
                    经电力调度员<u t_id="t10">${data.filePowerDispatcher1!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>同意时间延长到<u
                            t_id="t11">${data.delayTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u><br><br>
                    <span style="float:right">工作许可人:<u
                                t_id="t12">${data.workPermitHolder2!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><span
                            style="float:right">工作负责人:<u t_id="t13">${data.fileWorkLeader1!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br><br>
                    因工作间断，开工前重新检查安全措施，可以于<u t_id="t14">${data.interruptedTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>开工作业。<br><br>
                    <span style="float:right">工作负责人：<u
                                t_id="t15">${data.fileWorkLeader2!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br><br>
                    因转移工地，工作负责人应交待现场安全措施。 <span style="float:right">工作负责人：<u t_id="t16">${data.fileWorkLeader3!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br><br>
                    工作已于<u t_id="t17">${data.endTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>全部结束。<br><br>
                    <span style="float:right">工作负责人：<u
                                t_id="t18">${data.fileWorkLeader4!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br><br>
                    临时接地线共<u t_id="t19">${data.fileTeam!"&emsp;&emsp;&emsp;&emsp;"}</u>组和临时防护栅、标示牌已拆除，并恢复了常设防护栅和标示牌。<br><br><br> 经电力调度员<u
                            t_id="t23">${data.filePowerDispatcher2!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>批准工作票于<u t_id="t20">${data.finalEndTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>结束。<br><br>
                    <span style="float:right">工作许可人：<u
                                t_id="t21">${data.workPermitHolder3!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br>
                    备注：<u
                            t_id="t22">${data.remark!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u><br>
                </td>

            </tr>
            </tbody>
        </table>
<#--        <br>-->
    </div>

</div>

<script>

</script>
</body>


</html>
