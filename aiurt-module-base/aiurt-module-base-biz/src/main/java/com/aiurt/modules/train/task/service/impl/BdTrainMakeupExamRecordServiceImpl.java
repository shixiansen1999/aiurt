package com.aiurt.modules.train.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.modules.train.eaxm.entity.BdExamRecord;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.quzrtz.MakeUpExamServiceImpl;
import com.aiurt.modules.train.quzrtz.job.CronUtlit;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.mapper.BdTrainMakeupExamRecordMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainMakeupExamRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @Description: 培训补考记录
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
@Service
public class BdTrainMakeupExamRecordServiceImpl extends ServiceImpl<BdTrainMakeupExamRecordMapper, BdTrainMakeupExamRecord> implements IBdTrainMakeupExamRecordService {

    @Autowired
    private BdExamRecordMapper bdExamRecordMapper;
    @Autowired
    private BdTrainTaskMapper bdTrainTaskMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private BdTrainTaskUserMapper bdTrainTaskUserMapper;
    @Autowired
    private MakeUpExamServiceImpl makeUpExamService;
    @Autowired
    private BdTrainMakeupExamRecordMapper bdTrainMakeupExamRecordMapper;
    /**
     * 分页列表查询
     *
     * @param page
     * @param bdTrainMakeupExamRecord
     * @return
     */
    @Override
    public IPage<BdTrainMakeupExamRecord> getList(Page<BdTrainMakeupExamRecord> page, BdTrainMakeupExamRecord bdTrainMakeupExamRecord) {
        List<BdTrainMakeupExamRecord> list = baseMapper.getList(page, bdTrainMakeupExamRecord);
        return page.setRecords(list);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void makeUp(String makeUpTime, String id) {
        BdTrainMakeupExamRecord bdTrainMakeupExamRecord = baseMapper.selectById(id);
        if (ObjectUtil.isNull(bdTrainMakeupExamRecord)) {
            throw new JeecgBootException("该记录不存在，请重新查询补考记录。");
        }
        //更新补考时间为
        if (StrUtil.isNotBlank(makeUpTime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parse = format.parse(makeUpTime);
                bdTrainMakeupExamRecord.setMakeupTime(parse);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        //更新为已经批准补考
        bdTrainMakeupExamRecord.setIsMakeup(1);

        BdExamRecord bdExamRecord =new BdExamRecord();
        if (bdTrainMakeupExamRecord.getExamClassify() == 1){
            // 作为第一次补考新增一条补考记录
            bdExamRecord.setUserId(bdTrainMakeupExamRecord.getUserId());
            bdExamRecord.setIsRelease("0");
            bdExamRecord.setExamTime(bdTrainMakeupExamRecord.getMakeupTime());
            bdExamRecord.setExamClassify(0);
            bdExamRecord.setExamState("0");
            bdExamRecord.setExamPaperId(bdTrainMakeupExamRecord.getExamPaperId());
            bdExamRecord.setTrainTaskId(bdTrainMakeupExamRecord.getTrainTaskId());
            bdExamRecordMapper.insert(bdExamRecord);
        }else if (bdTrainMakeupExamRecord.getExamClassify() == 0){
            // 多次补考 再原先的补考纪录上更新
            bdExamRecord.setId(bdTrainMakeupExamRecord.getExamId());
            bdExamRecord.setIsRelease("0");
            bdExamRecord.setExamClassify(0);
            bdExamRecord.setExamState("0");
            bdExamRecord.setExamTime(bdTrainMakeupExamRecord.getMakeupTime());
            bdExamRecord.setExamPaperId(bdTrainMakeupExamRecord.getExamPaperId());
            bdExamRecord.setTrainTaskId(bdTrainMakeupExamRecord.getTrainTaskId());
            bdExamRecordMapper.UpdateById(bdExamRecord);
        }
        //定时开始考试
        QuartzJobDTO quartzJobStart = new QuartzJobDTO();
        // 计算触发时间
        quartzJobStart.setCronExpression(CronUtlit.getCron(bdTrainMakeupExamRecord.getMakeupTime()));
        //参数
        quartzJobStart.setParameter(bdExamRecord.getId());
        //定时任务结束考试
        QuartzJobDTO quartzJobEnd = new QuartzJobDTO();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(bdTrainMakeupExamRecord.getMakeupTime());
        calendar.add(Calendar.DATE, 1);
        Date time1 = calendar.getTime();
        // 计算触发时间
        quartzJobEnd.setCronExpression(CronUtlit.getCron(time1));
        //参数
        quartzJobEnd.setParameter(bdExamRecord.getId());
        makeUpExamService.test(quartzJobStart,quartzJobEnd);
        //保存补考关闭考试的定时任务id
        bdTrainMakeupExamRecord.setQuartzJobId(quartzJobEnd.getId());

        bdTrainMakeupExamRecordMapper.updateById(bdTrainMakeupExamRecord);
    }


    @Override
    public void batchMakeUp(String makeUpTime, List<String> idList) {
        if (CollUtil.isNotEmpty(idList)) {
            for (String s : idList) {
                this.makeUp(makeUpTime, s);
            }
        }
    }

}
