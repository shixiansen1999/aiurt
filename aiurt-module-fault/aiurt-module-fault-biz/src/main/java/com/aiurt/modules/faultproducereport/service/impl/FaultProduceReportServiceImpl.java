package com.aiurt.modules.faultproducereport.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.mapper.FaultProduceReportMapper;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date: 2023-02-23
 * @Version: V1.0
 */
@Service
@Slf4j
public class FaultProduceReportServiceImpl extends ServiceImpl<FaultProduceReportMapper, FaultProduceReport> implements IFaultProduceReportService, IFlowableBaseUpdateStatusService {

    @Autowired
    private FaultProduceReportMapper produceReportMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        FaultProduceReport faultProduceReport = this.getById(businessKey);
        if (ObjectUtil.isEmpty(faultProduceReport)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        } else {
            int states = updateStateEntity.getStates();
            switch (states) {
                case 2:
                    faultProduceReport.setState(1);
                case 3:
                    faultProduceReport.setState(0);


            }
        }
    }

    /**
     * 保存或者编辑年演练计划信息
     *
     * @param faultProduceReport
     * @return
     */
    public String startProcess(FaultProduceReport faultProduceReport) {
        String id = faultProduceReport.getId();

        return id;
    }

    @Override
    public Result<FaultProduceReport> getDetail() {
        FaultProduceReport produceReport = produceReportMapper.getDetail();
        return Result.OK(produceReport);
    }

    @Override
    public Result<IPage<FaultProduceReport>> queryPageList(Page<FaultProduceReport> pageList, FaultProduceReport faultProduceReport, String beginDay, String endDay) {
        // 获取到当前登录的用户的专业(majorCode、可能有多个，使用List存储)
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        List<String> majorCodeList = new ArrayList<>();
        for (CsUserMajorModel csUserMajorModel : CsUserMajorModelList) {
            majorCodeList.add(csUserMajorModel.getMajorCode());
        }
        // 如果查询参数有majorCode，这个majorCode在当前登录的用户的专业内，查询的专业只查询这个majorCode，不然查询的空
        if (faultProduceReport.getMajorCode() != null) {
            if (majorCodeList.contains(faultProduceReport.getMajorCode())) {
                majorCodeList.clear();
                majorCodeList.add(faultProduceReport.getMajorCode());
            } else {
                // 查询参数有majorCode，但是当前登录的用户的专业不包含majorCode，返回空数据
                return Result.ok(pageList);
            }
        }

        // 不传时间参数，默认查询所有
        // 传有时间参数的话，统计时间大于等于开始时间，小于等于结束时间
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 时间是否是指定格式(日期格式：yyyy-MM-dd)， 不是指定格式的话，舍弃
        if (beginDay != null) {
            try {
                DateUtils.parseDate(beginDay + " 00:00:00", pattern);
            } catch (Exception ignored) {
                beginDay = null;
            }
        }
        if (endDay != null) {
            try {
                DateUtils.parseDate(endDay + " 23:59:59", pattern);
            } catch (Exception ignored) {
                endDay = null;
            }
        }
        List<FaultProduceReport> reportList = produceReportMapper.queryPageList(pageList, majorCodeList, beginDay, endDay);
        pageList.setRecords(reportList);
        return Result.ok(pageList);
    }

}
