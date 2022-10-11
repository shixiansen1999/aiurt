package com.aiurt.modules.workticket.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.workticket.entity.BdWorkTicket;
import com.aiurt.modules.workticket.service.IBdWorkTicketService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags="工作票打印")
@Controller
@RequestMapping("/workTicket/bdWorkTicket")
@Slf4j
public class BdWorkTicketPrintController {

    @Autowired
    private IBdWorkTicketService bdWorkTicketService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @GetMapping(value = "/printf")
    @ApiOperation(value="工作票打印数据查询", notes="工作票打印数据查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工作票id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "打印类型,0: 普通打印,1:正票,2:副票", required = false, paramType = "query"),
            @ApiImplicitParam(name = "workTicketType", value = "工作票类型,0: 第一种工作票,1:第二种工作票",required = false, paramType = "query")
    })
    public String onlinePreview(Model model, HttpServletRequest req, @RequestParam(value = "id") String id,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "workTicketType", required = false)String workTicketType) {

        if (StrUtil.isBlank(id)) {
            throw  new AiurtBootException("参数id不能为空");
        }

        BdWorkTicket workTicket = bdWorkTicketService.getById(id);



        if (Objects.isNull(workTicket)) {
            throw new AiurtBootException("数据不存在");
        }

        // 判断转态可能没有在归档,需要查询中间业务数据
        String workLeader = workTicket.getWorkLeader();
        List<String> userNameList= new ArrayList<>();
        if (StrUtil.startWith(workLeader, '[')) {
            userNameList = JSONObject.parseArray(workLeader, String.class);
        } else {
            userNameList.add(workLeader);
        }

        List<LoginUser> loginUserList = sysBaseAPI.getLoginUserList(userNameList);
        if (CollUtil.isNotEmpty(loginUserList)) {
            List<String> collect = loginUserList.stream().map(LoginUser::getRealname).collect(Collectors.toList());
            workTicket.setWorkLeader(StrUtil.join(",", collect));
        } else {
            workTicket.setWorkLeader(workLeader);
        }

        // 站点
        if (StrUtil.isNotBlank(workTicket.getStation())) {
            JSONObject bdStation = sysBaseAPI.getCsStationById(workTicket.getStation());

            if (Objects.nonNull(bdStation)) {
                workTicket.setStation(bdStation.getString("stationName"));
            }
        }

        String workStartTime = workTicket.getWorkStartTime();
        JSONArray objects = JSONObject.parseArray(workStartTime);
        //开始时间
        workTicket.setWorkStartTime((String) objects.get(0));
        //结束时间
        workTicket.setWorkEndTime((String) objects.get(1));

        String workPartner = workTicket.getWorkPartner();
        JSONArray jsonArray = JSONObject.parseArray(workPartner);
        List<String> resNames = new ArrayList();

        for (int i = 0; i < jsonArray.size(); i++) {
            resNames.add(jsonArray.getString(i));
        }

        workTicket.setResNames(resNames);//工作组成员名字
        workTicket.setCount(resNames.size());//共计人数

        if (resNames.size()<16){
            for (int j = 0; resNames.size()<16; j++) {
                resNames.add("&emsp;&emsp;");
            }
        }

        workTicket.setTicketType("");

        if (StrUtil.equalsIgnoreCase("1",type)) {
            workTicket.setTicketType("正票");
        } else if (StrUtil.equalsIgnoreCase("2",type)) {
            workTicket.setTicketType("副票");
        }

        model.addAttribute("data",workTicket);

        if (StrUtil.equalsIgnoreCase("1", workTicketType)) {
            return "worktice_2";
        }
        return "worktice_1";

    }
}
