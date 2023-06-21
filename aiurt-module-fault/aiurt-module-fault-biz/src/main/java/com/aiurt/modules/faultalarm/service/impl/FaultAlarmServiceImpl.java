package com.aiurt.modules.faultalarm.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultalarm.constant.FaultAlarmConstant;
import com.aiurt.modules.faultalarm.dto.req.AlmRecordReqDTO;
import com.aiurt.modules.faultalarm.dto.req.CancelAlarmReqDTO;
import com.aiurt.modules.faultalarm.dto.req.OnFailureReportedReqDTO;
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
    @Resource
    private IFaultService faultService;

    @Override
    public IPage<AlmRecordRespDTO> queryAlarmRecordPageList(AlmRecordReqDTO almRecordReqDto, Integer pageNo, Integer pageSize) {
        Page<AlmRecordRespDTO> page = new Page<>(pageNo, pageSize);
        page = almRecordMapper.queryAlarmRecordPageList(page, almRecordReqDto);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelAlarm(CancelAlarmReqDTO cancelAlarmReqDTO) {
        AlmRecord almRecord = getAlmRecordById(cancelAlarmReqDTO.getId());

        // 检查告警是否已经取消过，离上一次取消间隔不到30分
        String redisKey = FaultAlarmConstant.FAULT_ALARM_ID + almRecord.getId();
        if (ObjectUtil.isNotEmpty(redisUtil.get(FaultAlarmConstant.FAULT_ALARM_ID + almRecord.getId()))) {
            throw new AiurtBootException("取消告警失败，此告警信息上一次取消告警离此次取消告警小于30分钟");
        }

        // 添加历史记录
        AlmRecordHistory almRecordHistory = createAlmRecordHistory(almRecord, cancelAlarmReqDTO.getCancelReason(), cancelAlarmReqDTO.getDealRemark(), FaultAlarmConstant.ALM_DEAL_STATE_2, null);
        almRecordHistoryMapper.insert(almRecordHistory);

        // 设置redis的缓存时间为30分钟
        redisUtil.set(redisKey, almRecord.getId(), 30 * 60);

        // 删除原来的记录
        almRecordMapper.deleteById(almRecord.getId());
    }

    @Override
    public AlmRecordRespDTO alarmDetails(String id) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onFailureReported(OnFailureReportedReqDTO onFailureReportedReqDTO) {
        AlmRecord almRecord = getAlmRecordById(onFailureReportedReqDTO.getId());

        // 查询工单编号是否存在
        faultService.isExist(onFailureReportedReqDTO.getFaultCode());

        // 添加历史记录
        AlmRecordHistory almRecordHistory = createAlmRecordHistory(almRecord, null, "已上报故障", FaultAlarmConstant.ALM_DEAL_STATE_3, onFailureReportedReqDTO.getFaultCode());
        almRecordHistoryMapper.insert(almRecordHistory);

        // 设置redis的缓存时间为30分钟
        String redisKey = FaultAlarmConstant.FAULT_ALARM_ID + almRecord.getId();
        redisUtil.set(redisKey, almRecord.getId(), 30 * 60);

        // 删除原来的记录
        almRecordMapper.deleteById(almRecord.getId());
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
     * 创建告警记录历史对象
     *
     * @param almRecord    故障记录对象
     * @param cancelReason 取消原因
     * @param dealRemark   处理备注
     * @param state        状态
     * @param faultCode    工单编号
     * @return AlmRecordHistory 告警记录历史对象
     */
    private AlmRecordHistory createAlmRecordHistory(AlmRecord almRecord, String cancelReason, String dealRemark, Integer state, String faultCode) {
        AlmRecordHistory almRecordHistory = new AlmRecordHistory();

        // 复制属性（排除不需要复制的属性）
        BeanUtils.copyProperties(almRecord, almRecordHistory, new String[]{"id", "createTime", "createBy", "updateBy", "updateTime"});
        almRecordHistory.setCancelReason(cancelReason);
        almRecordHistory.setState(state);
        almRecordHistory.setDealDateTime(new Date());
        almRecordHistory.setDealUserId(getLoginUser().getId());
        almRecordHistory.setDealRemark(dealRemark);
        almRecordHistory.setFaultCode(faultCode);

        return almRecordHistory;
    }

    /**
     * 根据ID查询AlmRecord对象
     *
     * @param id AlmRecord的ID
     * @return AlmRecord 查询到的AlmRecord对象
     * @throws AiurtBootException 如果未查询到对应记录，将抛出此异常
     */
    public AlmRecord getAlmRecordById(String id) throws AiurtBootException {
        AlmRecord almRecord = almRecordMapper.selectById(id);
        if (ObjectUtil.isEmpty(almRecord)) {
            throw new AiurtBootException("未查询到此项记录");
        }
        return almRecord;
    }
}
