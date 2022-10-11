<!DOCTYPE html>



<html lang="en">

<head>

    <style media="print">

        @page {

            size: auto;  /* auto is the initial value */

            /*margin: 0mm; !* this affects the margin in the printer settings *!*/

            margin-top: 0.5cm;

            margin-left: 0;

            margin-right: 0;

            margin-bottom: 0.9cm;

            /*padding-bottom: 0.5cm;*/

        }





        table{

            /*page-break-after: auto;*/



        }

        tr{page-break-inside: avoid;  }

        td{page-break-inside: avoid; }

        thead {display: table-header-group;}

        /*tfoot{display: table-footer-group;page-break-inside: avoid; }*/



    </style>

</head>

<body>

<#--<div><span style="text-align:right;"><p id="bhts"></p></span></div>-->



<div id="table-form" >

    <#--    margin:0 auto;-->

    <div id="div-print1" style="width:90%; margin-left: auto;margin-right: auto;margin-top: 0;">



        <#--            <div style="text-align:center;">-->

        <#--                <span style="font-size:20px;">变电所第二种工作票</span>-->

        <#--                <span id="dyp" style="float:right;font-size:18px;border:1px solid #000">${data.ticketType!}</span>-->

        <#--            </div>-->

        <#--            <div style="clear:both"></div>-->

        <#--            <span style="float:left"><span t_id="zd">${data.station!}</span>&emsp;站（所）</span> -->

        <#--            <span style="float:right">第<span t_id="gzph">${data.workTicketCode!}</span>号</span>-->



        <table border="1" style="width:100%;border-collapse:collapse;table-layout: fixed; text-align:left; ">

            <thead >

            <tr style="border-top: none;border-left: none;border-right: none; text-align: center">

                <td colspan="10" >

                    <div style="text-align:center;">

                        <span style="font-size:20px;">变电所第二种工作票</span>

                        <span id="dyp" style="float:right;font-size:18px;border:1px solid #000">${data.ticketType!}</span>

                    </div>

                    <div style="clear:both"></div>

                    <span style="float:left"><span t_id="zd">${data.station!}</span>&emsp;站（所）</span>

                    <span style="float:right">第<span t_id="gzph">${data.workTicketCode!}</span>号</span>

                </td>

            </tr>

            <tr>

                <td colspan="2">作业地点及内容</td>

                <td colspan="6" t_id="zydd">${data.workAddressContent!}</td>

                <td>填票人</td>

                <td t_id="tpr">${data.ticketFiller!}</td>

            </tr>

            <tr>

                <td colspan="2">工作票有效期</td>

                <td colspan="8">自 <span t_id="zyrq1">${data.workStartTime!}</span> 至 <span t_id="zyrq2">${data.workEndTime!}</span> 止</td>

            </tr>

            <tr>

                <td colspan="2">工作负责人</td>

                <td colspan="8" t_id="gzfzr">${data.workLeader!}</td>

            </tr>

            </thead>


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

                <td colspan="10"><span style="float:right">共计：<span t_id="rs">${data.count!0}</span> 人</span></td>

            </tr>

            <tr>

                <td colspan="2">工作条件(停电不停电)</td>

                <td colspan="8" t_id="gztj"><#if (data.workCondition)?? && data.workCondition == "0">停电<#else >不停电</#if></td>

            </tr>



            <tr id="traq" style="height: 200px;" valign="top">

                <td colspan="5"  > <b>必须采取的安全措施</b><br>

                    <#--                    <div id="safeContent" t_id="aqcs1" style="white-space:pre-wrap;width:98%;min-height: 180px;display: block;">-->

                    <#--                        <br>-->

                    <div id="safeContent"  style="white-space:pre-line;  word-break: break-all; word-wrap: break-word;" >
                        ${data.safetyMeasures!}
                    </div>

                    <#--                    </div>-->

                </td>

                <td colspan="5"><b>已经完成的安全措施</b><span><img t_id="gdtp"></span><br>

                    <#--                    <div id="safeContent1" t_id="aqcs2" style="white-space:pre-wrap;width:98%;min-height: 180px;display: block;">-->

                    <#--                    <br>-->

                    <div id="safeContent1"  style="white-space:pre-line;  word-break: break-all; word-wrap: break-word;" >

                        ${data.completedSafetyMeasures!}

                    </div>

                    <#--                    </div>-->

                </td>

            </tr>



            <tr>

                <td colspan="10" style="padding-left: 20px" >

                    工作负责人:<u t_id="gzfzr">${data.workLeaderSign!"&emsp;&emsp;&emsp;&emsp;"}</u><span style="float:right">签字时间:<u t_id="gzfzrtime">${data.workLeaderSignTime!"&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br>

                    签发人:<u t_id="qfr">${data.signeUser!"&emsp;&emsp;&emsp;&emsp;"}</u><span style="float:right">签字时间:<u t_id="qfrtime">${data.signeUserTime!"&emsp;&emsp;&emsp;&emsp;"}</u></span><br>

                </td>

            </tr>

            <#--                    <div style="page-break-inside: avoid">-->

            <tr style="border-top: none;">

                <td colspan="10" style="padding-top: 10px;" >

                    根据电力调度员<u t_id="t1">${data.filePowerDispatcher!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>发布第<u t_id="t2">${data.fileWorkTicketCode!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>号命令准予

                    <br><br>

                    <#--                </td>-->

                    <#--            </tr>-->

                    <#--            <tr style="border-top: none;border-bottom: none;">-->

                    <#--                <td colspan="10"  >-->

                    在<u t_id="t3">${data.fileStartTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>开始工作。<br><br>

                    <#--                </td>-->

                    <#--            </tr>-->

                    <#--                    </div>-->

                    <#--                    <div style="page-break-inside: avoid">-->

                    <#--            <tr style="border-top: none;border-bottom: none;">-->

                    <#--                <td colspan="10"  >-->

                    实际于<u t_id="t4">${data.fileActualTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>安全措施已经做好。<br>

                    <span style="float:right">工作许可人:<u t_id="t5">${data.workPermitHolder1!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span>

                </td>

            </tr>

            <#--                    </div>-->

            <#--                </td>-->

            <#--            </tr>-->

            <tr  style="border-top: none;">

                <td colspan="10" style="padding-top: 10px;">

                    <#--                    <div style="display: flex;align-items: center">-->

                    经检查安全措施已经做好。实际于<u t_id="t6">${data.fileConfirmActualTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>开始工作。<br>

                    <span style="float:right">工作负责人:<u t_id="t7">${data.fileWorkLeader!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br>

                    <#--                    </div>-->

                    <#--                </td>-->

                    <#--            </tr>-->

                    <#--            <tr style="border-top: none;border-bottom: none">-->

                    <#--                <td colspan="10"  >-->

                    <#--                    <div style="page-break-inside: avoid">-->

                    变更作业组成员记录：<u t_id="t8">${data.fileRecord!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u><br>

                    <span style="float:right">批准人（工作负责人或签发人）：<u t_id="t9">${data.approvedUser!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span>

                    <#--                    </div>-->

                    <#--                    </td>-->

                    <#--            </tr>-->

                    <#--            <tr style="border-top: none;border-bottom: none">-->

                    <#--                <td colspan="10"  >-->

                    <#--                    <div style="page-break-inside: avoid">-->

                    <#--                    </div>-->

                </td>

            </tr>

            <tr style="border-top: none;">

                <td colspan="10"  >

                    <br>因工作间断，开工前重新检查安全措施，可以于<u t_id="t10">${data.interruptedTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>开工作业。<br>

                    <span style="float:right">工作负责人：<u t_id="t11">${data.fileWorkLeader2!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br>



                    <#--                    <div style="page-break-inside: avoid">-->

                    因转移工地，工作负责人应交待现场安全措施。

                    <span style="float:right">工作负责人：<u t_id="t21">${data.fileWorkLeader3!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br>

                    <#--                    </div>-->

                    <#--                    </td>-->

                    <#--                </tr>-->

                    <#--            <tr style="border-top: none;border-bottom: none">-->

                    <#--                <td colspan="10"  >-->

                    <#--                    <div style="page-break-inside: avoid">-->

                </td>

            </tr>

            <tr style="border-top: none;">

                <td colspan="10"  >

                    <br>工作已于<u t_id="t13">${data.endTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>全部结束。

                    <span style="float:right">工作负责人：<u t_id="t14">${data.fileWorkLeader4!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br><br>



                    经电力调度员<u t_id="t15">${data.filePowerDispatcher2!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>批准工作票于<u t_id="t16">${data.finalEndTime!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u>结束。<br>

                    <span style="float:right">工作许可人：<u t_id="t17">${data.workPermitHolder3!"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"}</u></span><br>

                    <#--                    </div>-->

                </td>

            </tr>



        </table>







    </div>



</div>

<script>

</script>

</body>

</html>
