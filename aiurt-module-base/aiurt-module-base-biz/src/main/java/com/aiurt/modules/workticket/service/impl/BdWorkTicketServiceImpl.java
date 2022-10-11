package com.aiurt.modules.workticket.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.workticket.dto.UploadPictureDTO;
import com.aiurt.modules.workticket.dto.WorkTicketReqDTO;
import com.aiurt.modules.workticket.dto.WorkTicketResDTO;
import com.aiurt.modules.workticket.entity.BdWorkTicket;
import com.aiurt.modules.workticket.mapper.BdWorkTicketMapper;
import com.aiurt.modules.workticket.service.IBdWorkTicketService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description: bd_work_ticket
 * @Author: aiurt
 * @Date:   2022-10-08
 * @Version: V1.0
 */
@Slf4j
@Service
public class BdWorkTicketServiceImpl extends ServiceImpl<BdWorkTicketMapper, BdWorkTicket> implements IBdWorkTicketService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public String addOrUpdate(BdWorkTicket bdWorkTicket) {
        if (StrUtil.isBlank(bdWorkTicket.getWorkTicketCode())) {
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
            String local = localDate.format(fmt);
            String key = local;
            key =  "bd_work_ticket:" + local;

            RedisAtomicLong inc = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());

            long increment = inc.getAndIncrement()+1;

            inc.expireAt(getEndTime());

            String value = increment>99?String.valueOf(increment):String.format("%02d", increment);

            String workTicketCode = String.format("%s%s",local,value);
            bdWorkTicket.setWorkTicketCode(workTicketCode);
        }
        boolean b = saveOrUpdate(bdWorkTicket);
        return bdWorkTicket.getId();
    }

    /**
     * 工作票查询
     * @param pageList
     * @param workTicketReqDTO
     * @return
     */
    @Override
    public Page<WorkTicketResDTO> historyGet(Page<WorkTicketResDTO> pageList, WorkTicketReqDTO workTicketReqDTO) {
        // 对前端传上来的时间做处理
        if(Objects.nonNull(workTicketReqDTO.getCreateTime())){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(workTicketReqDTO.getCreateTime());
            // 将时分秒,毫秒域清零
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            workTicketReqDTO.setCreateTime(calendar.getTime());
        }
        if(Objects.nonNull(workTicketReqDTO.getEndTime())){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(workTicketReqDTO.getEndTime());
            // 将时分秒,毫秒域清零
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            workTicketReqDTO.setEndTime(calendar.getTime());
        }

        // 类型
        if (StrUtil.equalsIgnoreCase("1", workTicketReqDTO.getProcessName())) {
            workTicketReqDTO.setProcessName("第一种工作票");
        } else if (StrUtil.equalsIgnoreCase("2", workTicketReqDTO.getProcessName())){
            workTicketReqDTO.setProcessName("第二种工作票");
        } else {
            workTicketReqDTO.setProcessName("");
        }

        List<WorkTicketResDTO> workTicketResDTOS = baseMapper.historyGet(pageList, workTicketReqDTO);
        return pageList.setRecords(workTicketResDTOS);
    }



    /**
     * 查询待办事项
     * @param pageList
     * @param username
     * @return
     */
    @Override
    public Page<BdWorkTicket> queryPageList(Page<BdWorkTicket> pageList, String username) {

        List<BdWorkTicket> bdWorkTicketList = baseMapper.queryPageList(pageList,username, "");

        return pageList.setRecords(bdWorkTicketList);
    }

    /**
     * 更新状态
     * @param businessKey 业务数据
     * @param states 状态值
     */
    @Override
    public void updateState(String businessKey, Integer states) {
        if (StrUtil.isBlank(businessKey)) {
            return;
        }
        BdWorkTicket workTicket = baseMapper.selectById(businessKey);
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.nonNull(workTicket)) {
            //更新状态
            workTicket.setState(states);


            // 已审核待签发
            if (states == 3) {
                // 设置审批人
                workTicket.setWorkLeaderSign(loginUser.getRealname());
                workTicket.setWorkLeaderSignTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }else if (states == 6) {

            }
            updateById(workTicket);
        }
    }

    /**
     * 权限认证
     *
     * @param id
     * @return
     */
    @Override
    public Boolean authUpload(String id) {
        if (StrUtil.isBlank(id)) {
            return Boolean.FALSE;
        }

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(sysUser)) {
            return false;
        }



        String orgId = sysUser.getOrgId();

        if (StrUtil.isBlank(orgId)) {
            return Boolean.FALSE;
        }

        BdWorkTicket bdWorkTicket = baseMapper.selectById(id);


        if (Objects.isNull(bdWorkTicket)) {
            return Boolean.FALSE;
        }

        String ticketFiller = bdWorkTicket.getTicketFiller();


        if (StrUtil.isBlank(ticketFiller)) {
            return Boolean.FALSE;
        }

        List<String> teamIdList = getBaseMapper().getTeamIdList(ticketFiller);

        teamIdList = Optional.ofNullable(teamIdList).orElse(Collections.emptyList());

        teamIdList.removeAll(Collections.singleton(null));


        if (teamIdList.contains(orgId)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 上传图片
     *
     * @param uploadPictureDTO
     */
    @Override
    public void uploadPicture(UploadPictureDTO uploadPictureDTO) {
        if (Objects.isNull(uploadPictureDTO) || StrUtil.isBlank(uploadPictureDTO.getId())
                || Objects.isNull(uploadPictureDTO.getPath())) {
            return;
        }
        log.info("请求参数:{}-{}", uploadPictureDTO.getId(), uploadPictureDTO.getPath());

        BdWorkTicket workTicket = baseMapper.selectById(uploadPictureDTO.getId());

        workTicket.setPicturePath(JSON.toJSONString(uploadPictureDTO.getPath()));

        updateById(workTicket);
    }

    /**
     * 获取当天的最后时间
     * @return
     */
    public Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(todayEnd.getTime());
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return todayEnd.getTime();
    }
}
