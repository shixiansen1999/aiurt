package com.aiurt.modules.faultalarm.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.modules.faultalarm.constant.FaultAlarmConstant;
import com.aiurt.modules.faultalarm.dto.req.AlmRecordReqDTO;
import com.aiurt.modules.faultalarm.dto.req.CancelAlarmReqDTO;
import com.aiurt.modules.faultalarm.dto.resp.AlmRecordRespDTO;
import com.aiurt.modules.faultalarm.entity.AlmRecord;
import com.aiurt.modules.faultalarm.entity.AlmRecordHistory;
import com.aiurt.modules.faultalarm.mapper.AlmRecordHistoryMapper;
import com.aiurt.modules.faultalarm.mapper.AlmRecordMapper;
import com.aiurt.modules.faultalarm.service.IFaultAlarmService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 集中告警实现类
 * @Author: wgp
 * @Date: 2023-06-05
 * @Version: V1.0
 */
@Slf4j
@Service
public class FaultAlarmServiceImpl extends ServiceImpl<AlmRecordMapper, AlmRecord> implements IFaultAlarmService {

    @Resource
    private AlmRecordMapper almRecordMapper;
    @Resource
    private AlmRecordHistoryMapper almRecordHistoryMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public IPage<AlmRecordRespDTO> queryAlarmRecordPageList(AlmRecordReqDTO almRecordReqDto, Integer pageNo, Integer pageSize) {
        Page<AlmRecordRespDTO> page = new Page<>(pageNo, pageSize);
        page = almRecordMapper.queryAlarmRecordPageList(page, almRecordReqDto);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelAlarm(CancelAlarmReqDTO cancelAlarmReqDTO) {
        AlmRecord almRecord = almRecordMapper.selectById(cancelAlarmReqDTO.getId());
        if (ObjectUtil.isEmpty(almRecord)) {
            throw new AiurtBootException("未查询到此项记录");
        }

        // 检查告警是否已经取消过
        String redisKey = FaultAlarmConstant.FAULT_ALARM_ID + almRecord.getId();
        if (ObjectUtil.isNotEmpty(redisUtil.get(FaultAlarmConstant.FAULT_ALARM_ID + almRecord.getId()))) {
            throw new AiurtBootException("取消告警失败，此告警信息上一次取消告警离此次取消告警小于30分钟");
        }

        // 添加历史记录
        AlmRecordHistory almRecordHistory = createAlmRecordHistory(almRecord, cancelAlarmReqDTO);
        almRecordHistoryMapper.insert(almRecordHistory);

        // 设置redis的缓存时间为30分钟
        redisUtil.set(redisKey, almRecord.getId(), 30 * 60);

        // 删除原来的记录
        almRecordMapper.deleteById(almRecord.getId());
    }

    @Override
    public AlmRecordRespDTO faultAlarmService(String id) {
        AlmRecordRespDTO result = almRecordHistoryMapper.queryById(id);
        if (ObjectUtil.isEmpty(result)) {
            throw new AiurtBootException("未查询到此项记录");
        }
        return result;
    }

    @Override
    @DS("multi-sqlserver")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<AlmRecord> querySqlServerOnAlm() {
        return almRecordMapper.querySqlServerOnAlm();
    }

    @Override
    public IPage<AlmRecordRespDTO> queryAlarmRecordHistoryPageList(AlmRecordReqDTO almRecordReqDto, Integer pageNo, Integer pageSize) {
        Page<AlmRecordRespDTO> page = new Page<>(pageNo, pageSize);
        page = almRecordMapper.queryAlarmRecordHistoryPageList(page, almRecordReqDto);
        return page;
    }

    /**
     * 获取当前登录人信息
     *
     * @return
     */
    public LoginUser getLoginUser() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }
        return loginUser;
    }

    /**
     * 根据提供的AlmRecord和CancelAlarmReqDTO创建一个AlmRecordHistory对象。
     *
     * @param almRecord         表示告警记录的AlmRecord对象。
     * @param cancelAlarmReqDTO 包含取消告警详情的CancelAlarmReqDTO对象。
     * @return 创建的AlmRecordHistory对象。
     */
    private AlmRecordHistory createAlmRecordHistory(AlmRecord almRecord, CancelAlarmReqDTO cancelAlarmReqDTO) {
        AlmRecordHistory almRecordHistory = new AlmRecordHistory();
        almRecordHistory.setCancelReason(cancelAlarmReqDTO.getCancelReason());
        almRecordHistory.setState(FaultAlarmConstant.ALM_DEAL_STATE_2);
        almRecordHistory.setDealDateTime(new Date());
        almRecordHistory.setDealUserId(getLoginUser().getId());
        almRecordHistory.setDealRemark(cancelAlarmReqDTO.getDealRemark());

        // 复制属性（排除不需要复制的属性）
        BeanUtils.copyProperties(almRecord, almRecordHistory, new String[]{"id", "createTime", "createBy", "updateBy", "updateTime"});

        return almRecordHistory;
    }

}
