package com.aiurt.modules.train.eaxm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.modules.train.exam.dto.BdAchievementDTO;
import com.aiurt.modules.train.exam.dto.BdExamRecordDTO;
import com.aiurt.modules.train.exam.dto.ExamDetailsDTO;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.exam.entity.BdExamRecordDetail;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordDetailMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.eaxm.service.IBdExamRecordService;
import com.aiurt.modules.train.task.dto.TranscriptDTO;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.mapper.BdTrainMakeupExamRecordMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainTaskService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 考试记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdExamRecordServiceImpl extends ServiceImpl<BdExamRecordMapper, BdExamRecord> implements IBdExamRecordService {

    @Autowired
    private  BdExamRecordMapper bdExamRecordMapper;

    @Autowired
    private BdExamRecordDetailMapper bdExamRecordDetailMapper;

    @Autowired
    private BdExamPaperMapper bdExamPaperMapper;

    @Autowired
    private BdTrainMakeupExamRecordMapper bdTrainMakeupExamRecordMapper;
    @Autowired
    private BdTrainTaskMapper bdTrainTaskMapper;

    @Autowired
    private BdTrainTaskUserMapper bdTrainTaskUserMapper;

    @Autowired
    private IBdTrainTaskService bdTrainTaskService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public Page<BdExamRecord> queryPageList(Page<BdExamRecord> pageList,BdExamRecord condition) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String id = sysUser.getId();
        condition.setUserId(id);
        List<BdExamRecord> bdExamRecordList = bdExamRecordMapper.list(pageList,condition);
        if (CollectionUtil.isNotEmpty(bdExamRecordList)){
            bdExamRecordList.forEach(e -> {
                String string = bdExamRecordMapper.selectOnes(e.getUserId(), e.getTrainTaskId(),e.getExamPaperId());
                if (ObjectUtil.isNotNull(string) && "0".equals(string)){
                    e.setIsRelease("0");
                }
                if (ObjectUtil.isNotNull(string) && "1".equals(string)){
                    e.setIsRelease("1");
                }
                if (ObjectUtil.isNotNull(string) && "2".equals(string)){
                    e.setIsRelease("2");
                }
                if (ObjectUtil.isNotNull(string) && "3".equals(string)){
                    e.setIsRelease("3");
                }
                if (ObjectUtil.isNotNull(string) && "4".equals(string)){
                    e.setIsRelease("2");
                }
                if (ObjectUtil.isNull(string)){
                    this.name(e);
                }
                //查询是否批准补考
                List<BdTrainMakeupExamRecord> excretory = bdExamRecordMapper.excretory(e.getUserId(), e.getTrainTaskId(),e.getExamPaperId());
                String string1 = bdExamRecordMapper.selectOness(e.getUserId(), e.getTrainTaskId(),e.getExamPaperId());
                if (excretory.size()>1){
                    e.setIdentification(2);
                }
                if(ObjectUtil.isNotNull(string1)){
                    e.setExamClassify(0);
                    e.setIsRelease(string1);
                    if("4".equals(string1)){
                        e.setIsRelease("2");
                    }
                }
                if(ObjectUtil.isNull(string1)){
                    e.setExamClassify(1);
                }
             });
        }
        List<BdExamRecord> bdExamRecords = getBdExamRecodes(bdExamRecordList);
        List<BdExamRecord> recordList = new ArrayList();
        bdExamRecords.forEach(e -> {
            if ("3".equals(e.getIsRelease())){
                recordList.add(e);
            }
        });
        bdExamRecords.removeAll(recordList);
        return pageList.setRecords(bdExamRecordList);
    }

    @Override
    public Page<BdExamRecord> queryPageListPc(Page<BdExamRecord> pageList, BdExamRecord condition) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        condition.setUserId(sysUser.getId());
        List<BdExamRecord> lists = bdExamRecordMapper.listss(pageList,condition);
        timeList(lists);
        if (CollectionUtil.isNotEmpty(lists)){
            lists.forEach(e -> {
                String bdExamRecord = bdExamRecordMapper.selectOnes(e.getUserId(), e.getTrainTaskId(),e.getExamPaperId());
                if (ObjectUtil.isNotNull(bdExamRecord) && "2".equals(bdExamRecord)){
                    e.setTaskState(5);
                }
                //如果是带复核不显示是否及格
                if(e.getIsRelease()!=null){
                    if ("2".equals(e.getIsRelease())){
                      e.setIsPass(null);
                    }
                }
                // PC显示待发布对应到待复核
                if ("4".equals(e.getIsRelease())){
                    e.setIsRelease("2");
                }
                // 补考获取批准补考时间
                if (e.getExamClassify()==0){
                   BdExamRecord bdExamRecord1 = bdExamRecordMapper.selectById(e.getId());
                    e.setExamPlanTime(bdExamRecord1.getExamTime());
                }
                // 实际开始时间不为空 加有效期设为实际关闭时间
                BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(e.getTrainTaskId());
                int num = bdTrainTask.getExamValidityPeriod();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    if (e.getExamPlanTime()!=null){
                        Date time1 = new Date();
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(e.getExamPlanTime());
                        calendar.add(Calendar.DATE,num);
                        time1=calendar.getTime();
                        String string1 = simpleDateFormat.format(time1);
                        e.setExaminationDeadline(string1);
                  }
            });
        }
        return pageList.setRecords(lists);
    }

    @Override
    public void addBdQuestionCategory(BdExamRecord bdExamRecord) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        bdExamRecord.setUserId(sysUser.getId());
        List<BdExamRecordDTO> bdQuestionList = bdExamRecord.getBdExamRecordDTOList();
        int count = 0;
        //查询试卷的选择题分值
        BdExamPaper bdExamPaper =   bdExamPaperMapper.selectById(bdExamRecord.getExamPaperId());
        Integer integer = bdExamPaper.getScqscore();
        //获得记录id
        BdTrainTaskUser bdTrainTaskUser = bdExamRecordMapper.resultLists(sysUser.getId(),bdExamRecord.getTrainTaskId(),bdExamRecord.getExamClassify(),bdExamRecord.getExamPaperId());
        bdExamRecord.setId(bdTrainTaskUser.getRecordId());
        List<String> detailById = bdExamRecordDetailMapper.getDetailById(bdExamRecord.getId());
        //假如已经存在答题详情，先删除在插入
        if ( CollectionUtil.isNotEmpty(detailById)) {
            bdExamRecordDetailMapper.deleteBatchIds(detailById);
        }
        // 该考试是补考
        if (bdExamRecord.getExamClassify() == 0 ) {
            //关闭结束考试定时任务
            String quartzJobIdById = bdTrainMakeupExamRecordMapper.getQuartzJobIdById(sysUser.getId(), bdExamRecord.getTrainTaskId());
            QuartzJobDTO quartzJobDTO = bdTrainMakeupExamRecordMapper.getQuartzJobDTO(quartzJobIdById);
            sysBaseAPI.deleteAndStopJob(quartzJobDTO);
        }
        if(CollectionUtil.isNotEmpty(bdQuestionList)){
            for (BdExamRecordDTO bdExamRecordDTO : bdQuestionList){
                List<String>  stringList =  bdExamRecordMapper.listId(bdExamRecordDTO.getExercisesId());
                String[] optionId = bdExamRecordDTO.getOptionId();
                List<String> list = Arrays.asList(optionId);
                if (stringList.size() == list.size()) {
                    stringList.removeAll(list);
                    if (CollectionUtil.isEmpty(stringList)) {
                        Integer isTrue = 1;
                        count++;
                        doDetail(bdExamRecordDTO,bdExamRecord,isTrue,integer);
                    } else {
                        Integer isTrue = 2;
                        Integer score = 0;
                        doDetail(bdExamRecordDTO,bdExamRecord,isTrue,score);
                    }
                } else {
                    Integer isTrue = 2;
                    Integer score = 0;
                    doDetail(bdExamRecordDTO,bdExamRecord,isTrue,score);
                }
            }
        }
            // 将状态修改成以考试
            bdExamRecord.setExamState("2");
            //判断是否有简单题从而产生补考记录
            List<Integer> collect = bdQuestionList.stream().map(BdExamRecordDTO::getTopicCategory).distinct().collect(Collectors.toList());
            int i = count*integer;
            bdExamRecord.setScore(i);
            bdExamRecord.setIsSubmit(1);
            Integer integer1 = bdExamPaper.getPassscore();
            //是否及格
            if (i>=integer1){
                bdExamRecord.setIsPass(1);
            }
            if (i<integer1){
                bdExamRecord.setIsPass(0);
            }
            boolean flag = false;
            for (int j = 0; j < collect.size(); j++) {
                Integer topicCategory = collect.get(j);
                if (topicCategory == 3) {
                    flag = true;
                    break;
                }
            }
            if (!flag){
                bdExamRecord.setIsRelease("3");
                bdExamRecord.setCorrect(1);
            }else {
                bdExamRecord.setIsRelease("2");
                bdExamRecord.setCorrect(0);
            }
            if (i<integer1 && !flag ){
                bdExamRecord.setIsPass(0);
                BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(bdExamRecord.getTrainTaskId());
                if (bdTrainTask.getMakeUpState() == 1) {
                    BdTrainMakeupExamRecord bdTrainMakeupExamRecord = new BdTrainMakeupExamRecord();
                    if (ObjectUtil.isNotNull(bdTrainTask)){
                        bdTrainMakeupExamRecord.setSysOrgCode(bdTrainTask.getTaskTeamName());
                    }
                    bdTrainMakeupExamRecord.setCreateTime(new Date());
                    bdTrainMakeupExamRecord.setTrainTaskId(bdExamRecord.getTrainTaskId());
                    bdTrainMakeupExamRecord.setExamPaperId(bdExamRecord.getExamPaperId());
                    bdTrainMakeupExamRecord.setUserId(sysUser.getId());
                    bdTrainMakeupExamRecord.setExamId(bdExamRecord.getId());
                    bdTrainMakeupExamRecord.setExamClassify(bdExamRecord.getExamClassify());
                    bdTrainMakeupExamRecordMapper.insert(bdTrainMakeupExamRecord);
                }
            }
            //如果是考试中改为已考试
            if ("1".equals(bdExamRecord.getExamState())) {
                bdExamRecord.setExamState("2");
            }
            if (ObjectUtil.isNotNull(bdTrainTaskUser)){
                bdExamRecordMapper.updateById(bdExamRecord);
            }
            //正式考试，考试未结束，如果所有人考完，提前结束考试，且关闭结束考试定时任务
            if (bdExamRecord.getExamClassify() == 1) {
                earlyClosure(flag,bdExamRecord);
            }
    }

    public void doDetail(BdExamRecordDTO bdExamRecordDTO,BdExamRecord bdExamRecord,Integer isTrue ,Integer score) {
        BdExamRecordDetail bdExamRecordDetail = new BdExamRecordDetail();
        bdExamRecordDetail.setAnswer(bdExamRecordDTO.getContent());
        bdExamRecordDetail.setIsTrue(isTrue);
        bdExamRecordDetail.setScore(score);
        bdExamRecordDetail.setQueId(bdExamRecordDTO.getExercisesId());
        bdExamRecordDetail.setExamRecordId(bdExamRecord.getId());
        bdExamRecordDetailMapper.insert(bdExamRecordDetail);
    }

    public void earlyClosure(boolean flag,BdExamRecord bdExamRecord) {
        String trainTaskId = bdExamRecord.getTrainTaskId();
        List<BdExamRecord> bdExamRecords = bdExamRecordMapper.getNum(trainTaskId);
        List<BdTrainTaskUser> userList = bdTrainTaskUserMapper.getUserListById(trainTaskId);
        BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(trainTaskId);
        if (!flag) {
            //没有简答题
            List<BdExamRecord> collect = bdExamRecords.stream().filter(c -> "3".equals(c.getIsRelease())).collect(Collectors.toList());
            if (collect.size() == userList.size()) {
                bdTrainTask.setId(trainTaskId);
                bdTrainTask.setTaskState(6);
                bdTrainTaskService.updateById(bdTrainTask);
                //提前结束考试删除定时任务
                QuartzJobDTO quartzJobDTO = bdTrainMakeupExamRecordMapper.getQuartzJobDTO(bdTrainTask.getQuartzJobId());
                sysBaseAPI.deleteAndStopJob(quartzJobDTO);
            }
        } else {
            //有简答题
            List<BdExamRecord> collect = bdExamRecords.stream().filter(c -> "2".equals(c.getIsRelease())).collect(Collectors.toList());
            if (collect.size() == userList.size()) {
                bdTrainTask.setId(trainTaskId);
                bdTrainTask.setTaskState(5);
                bdTrainTaskService.updateById(bdTrainTask);
                //提前结束考试删除定时任务
                QuartzJobDTO quartzJobDTO = bdTrainMakeupExamRecordMapper.getQuartzJobDTO(bdTrainTask.getQuartzJobId());
                sysBaseAPI.deleteAndStopJob(quartzJobDTO);
            }
        }
    }



    @Override
    public Page<BdExamRecord> lists(Page<BdExamRecord> pageList,BdExamRecord condition) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        condition.setUserId(sysUser.getId());
        List<BdExamRecord> lists = bdExamRecordMapper.lists(pageList,condition);
        timeList(lists);
        lists.forEach(e -> {
            this.name(e);
            if (e.getAnswerTime()!=null && e.getSubmitTime()!=null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                String string1 = simpleDateFormat.format(e.getAnswerTime());
                String string2 = simpleDateFormat.format(e.getSubmitTime());
                e.setTime(string1+ "-" +string2);
            }
        });
        return pageList.setRecords(lists);
    }

    @Override
    public Page<BdExamRecord> lecturerList(Page<BdExamRecord> pageList,BdExamRecord condition) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        condition.setUserId(sysUser.getId());
        List<BdExamRecord> bdExamRecordList = bdExamRecordMapper.lecturerList(pageList,condition);
        timeList(bdExamRecordList);
        List<BdExamRecord> bdExamRecords = getBdExamRecords(bdExamRecordList);
        return pageList.setRecords(bdExamRecords);

    }

    @Override
    public BdExamPaper lecturerReadOne(String id, Integer exemplify, Integer state,String taskId) {
        BdExamPaper bdExamPaper = bdExamRecordMapper.lecturerReadOne(id,exemplify,state,taskId);
        if (ObjectUtil.isNotNull(bdExamPaper)){
            List<BdTrainTaskUser> list = bdExamRecordMapper.userList(bdExamPaper.getTaskId());
            bdExamPaper.setTakeNumber(list.size());
            list.forEach(e -> {
                SysDepartModel sysDepartModel = sysBaseAPI.selectAllById(e.getTeamId().toString());
                e.setSysOrgCode(sysDepartModel.getDepartName());
            });
            bdExamPaper.setUserList(list);
            if (bdExamPaper.getState()==0 || bdExamPaper.getState()==1 || bdExamPaper.getState()==2 || bdExamPaper.getState()==3){
                bdExamPaper.setTaskState(0);
            }
            if (bdExamPaper.getState() == 4){
                bdExamPaper.setTaskState(1);
            }
            if (bdExamPaper.getState() == 5){
                bdExamPaper.setTaskState(2);
            }
            if (bdExamPaper.getState() == 7){
                bdExamPaper.setTaskState(3);
            }
            if (bdExamPaper.getState() == 6){
                bdExamPaper.setTaskState(3);
            }
            //根据试卷id查询参考人数
            List<String> stringList = bdExamRecordMapper.bdExamPaper(bdExamPaper.getId(),bdExamPaper.getTaskId());
            BdExamPaper bdExamPaper1 = bdExamPaper.setPateNumber(stringList.size());

            //缺考人数
            if(bdExamPaper.getTakeNumber()!=null && bdExamPaper.getPateNumber()!=null){
                Integer integer1 = bdExamPaper.getTakeNumber()-bdExamPaper1.getPateNumber();
                bdExamPaper.setLackNumber(integer1);
            }
        }
        return bdExamPaper;
    }

    @Override
    public Page<BdExamRecord> recordList(Page<BdExamRecord> pageList,BdExamRecord condition) {
        List<BdExamRecord> bdExamRecordList = bdExamRecordMapper.recordList(pageList,condition);
        timeList(bdExamRecordList);
        return pageList.setRecords(bdExamRecordList);
    }

    @Override
    public List<BdTrainTaskUser> resultList(String id,String examPaperId,Integer examClassify) {
        List<BdTrainTaskUser> list = bdExamRecordMapper.userList(id);
        list.forEach(e -> {
            BdTrainTaskUser bdTrainTaskUser = bdExamRecordMapper.resultListss(e.getUserId(),e.getTrainTaskId(),1,examPaperId);
            String teamName = bdTrainTaskMapper.getTeamName(e.getTeamId());
            e.setSysOrgCode(teamName);
            if (ObjectUtil.isNotNull(bdTrainTaskUser)){
                if (bdTrainTaskUser.getScore()!=null){
                    e.setScore(bdTrainTaskUser.getScore());
                }
                //查询补考状态
                BdTrainTaskUser bdTrainTaskUsers = bdExamRecordMapper.resultListss(e.getUserId(),e.getTrainTaskId(),0,examPaperId);
                if (ObjectUtil.isNotNull(bdTrainTaskUsers)){
                    e.setMakeup(bdTrainTaskUsers.getIsRelease());
                }
                e.setRecordId(bdTrainTaskUser.getRecordId());
                e.setExamPaperId(bdTrainTaskUser.getExamPaperId());
                e.setExamTime(bdTrainTaskUser.getExamTime());
                e.setSubmitTime(bdTrainTaskUser.getSubmitTime());
                e.setUseTime(bdTrainTaskUser.getUseTime());
                e.setMakeUpState(bdTrainTaskUser.getMakeUpState());
                e.setIsRelease(bdTrainTaskUser.getIsRelease());
                if ("0".equals(bdTrainTaskUser.getExamState())){
                    e.setExaminationStatus("未考试");
                }if ("1".equals(bdTrainTaskUser.getExamState())){
                    e.setExaminationStatus("考试中");
                }if ("2".equals(bdTrainTaskUser.getExamState())){
                    e.setExaminationStatus("已考试");
                }if (bdTrainTaskUser.getIsPass()!=null && bdTrainTaskUser.getIsPass().equals(1)){
                    e.setPassName("及格");
                }if (bdTrainTaskUser.getIsPass()!=null && bdTrainTaskUser.getIsPass().equals(0)){
                    e.setPassName("不及格");
                }
                if (bdTrainTaskUser.getExamPaperId()!=null){
                    List<BdTrainMakeupExamRecord> excretory = bdExamRecordMapper.excretory(e.getUserId(), e.getTrainTaskId(),bdTrainTaskUser.getExamPaperId());
                    e.setExamFrequency(excretory.size());
                    if (excretory.size()>0){
                        e.setMakeUpStateName("是");
                    }else {
                        e.setMakeUpStateName("否");
                    }
                }
            }
            if (ObjectUtil.isNull(bdTrainTaskUser)){
                e.setExaminationStatus("未考试");
                e.setMakeUpStateName("否");
                e.setExamFrequency(0);
            }
        });
        return list;
    }

    @Override
    public void bdAchievementDTOList(BdAchievementDTO bdAchievementDTO) {
        BdExamRecord bdExamRecord = new BdExamRecord();
        if(ObjectUtil.isNotEmpty(bdAchievementDTO)) {
            bdExamRecord.setUserId(bdAchievementDTO.getUserId());
            bdExamRecord.setTrainTaskId(bdAchievementDTO.getTrainTaskId());
            bdExamRecord.setExamPaperId(bdAchievementDTO.getExamPaperId());
            bdExamRecord.setExamClassify(1);
            bdExamRecord.setExamTime(new Date());
            bdExamRecord.setIsSubmit(1);
            if (ObjectUtil.isNull(bdAchievementDTO.getScore())) {
                bdExamRecord.setExamState("0");
            } else {
                bdExamRecord.setExamState("2");
            }
            bdExamRecord.setCorrect(1);
            bdExamRecord.setIsRelease("3");
            BdExamPaper bdExamPaper =   bdExamPaperMapper.selectById(bdAchievementDTO.getExamPaperId());
            //总分
            Integer score = bdAchievementDTO.getScore();
            if (score != null) {
                bdExamRecord.setScore(score);
                //及格分
                Integer passcode = bdExamPaper.getPassscore();
                //是否及格
                if (score>=passcode){
                    bdExamRecord.setIsPass(1);
                }
                if (score<passcode){
                    bdExamRecord.setIsPass(0);
                }
                if (bdAchievementDTO.getScore() == null){
                    bdExamRecord.setIsPass(0);
                }
            }
        }
        bdExamRecordMapper.insert(bdExamRecord);
    }

    @Override
    public void updatedAchievementDOList(BdAchievementDTO bdAchievementDTO) {
        BdExamPaper bdExamPaper =   bdExamPaperMapper.selectById(bdAchievementDTO.getExamPaperId());
        //总分
        Integer score = bdAchievementDTO.getScore();
        if (score != null) {
            bdAchievementDTO.setScore(score);
            bdAchievementDTO.setExamState("2");
            //及格分
            Integer passcode = bdExamPaper.getPassscore();
            //是否及格
            if (score >= passcode) {
                bdAchievementDTO.setIsPass(1);
            }
            if (score < passcode) {
                bdAchievementDTO.setIsPass(0);
            }
            if (bdAchievementDTO.getScore() == null) {
                bdAchievementDTO.setIsPass(0);
            }
        } else {
            bdAchievementDTO.setExamState("0");
        }
        bdExamRecordMapper.updatedAchievementDOList(bdAchievementDTO);
    }

    @Override
    public void  addList(List<BdAchievementDTO> bdAchievementDTOList){
        bdAchievementDTOList.forEach(e -> {
            BdTrainTaskUser bdTrainTaskUser = bdExamRecordMapper.resultListss(e.getUserId(),e.getTrainTaskId(),e.getExamClassify(),e.getExamPaperId());
            if (ObjectUtil.isNotNull(bdTrainTaskUser)){
                    updatedAchievementDOList(e);
            }else {
                    bdAchievementDTOList(e);
            }
        });
    }
    private void timeList(List<BdExamRecord> bdExamRecordList) {
        bdExamRecordList.forEach(e -> {
            if (e.getExamPlanTime()!=null && e.getExamValidityPeriod() !=null){
                Date time1 = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(e.getExamPlanTime());
                calendar.add(Calendar.DATE,e.getExamValidityPeriod());
                time1=calendar.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
                String string1 = simpleDateFormat.format(time1);
                e.setExaminationDeadline(string1);
            }
        });
    }

    @NotNull
    private List<BdExamRecord> getBdExamRecodes(List<BdExamRecord> bdExamRecordList) {
        return bdExamRecordList;
    }
    @NotNull
    private List<BdExamRecord> getBdExamRecords(List<BdExamRecord> bdExamRecordList) {
        bdExamRecordList.forEach(this::name);
        return bdExamRecordList;
    }

    private void name(BdExamRecord e) {
        if (e.getTaskState()==0 || e.getTaskState()==1 || e.getTaskState()==2 || e.getTaskState()==3){
            e.setIsRelease("0");
        }
        if (e.getTaskState() == 4){
            e.setIsRelease("1");
        }
        if (e.getTaskState() == 5){
            e.setIsRelease("2");
        }
        if (e.getTaskState() == 6){
            e.setIsRelease("3");
        }
        if (e.getTaskState() == 7){
            e.setIsRelease("3");
        }
    }
    private void names(BdExamRecord e) {
        if (e.getTaskState()==0 || e.getTaskState()==1 || e.getTaskState()==2 || e.getTaskState()==3){
            e.setIsRelease("0");
        }
        if (e.getTaskState() == 4){
            e.setIsRelease("1");
        }
        if (e.getTaskState() == 5){
            e.setIsRelease("2");
        }
    }

    @Override
    public ExamDetailsDTO examDetails(String examPaperId,String trainTaskId){
        ExamDetailsDTO examDetailsDTO = bdExamRecordMapper.examDetails(examPaperId,trainTaskId);
        examDetailsDTO.setAbsenteesNumber(examDetailsDTO.getReferenceNumber()-examDetailsDTO.getActualReferenceNumber());
        List<TranscriptDTO> transcriptDTOList =bdTrainTaskMapper.selectTranscriptList(examPaperId,trainTaskId);
        if (ObjectUtil.isNull(examDetailsDTO)){
            return null;
        }
        if (examDetailsDTO.getIsRelease()==0){
            examDetailsDTO.setIsRelease(4);
        }else if (examDetailsDTO.getIsRelease()<4 && examDetailsDTO.getIsRelease()>0){
            examDetailsDTO.setIsRelease(0);
        }else if (examDetailsDTO.getIsRelease()==4){
            examDetailsDTO.setIsRelease(1);
        }else if (examDetailsDTO.getIsRelease()==5){
            examDetailsDTO.setIsRelease(2);
        }else if (examDetailsDTO.getIsRelease()==6 || examDetailsDTO.getIsRelease()==7){
            examDetailsDTO.setIsRelease(3);
        }
        if(transcriptDTOList.size()!=0){

            examDetailsDTO.setTranscriptDTOS(transcriptDTOList);
        }
        if (transcriptDTOList.size()==0){
            examDetailsDTO.setTranscriptDTOS(new ArrayList<>());
        }
        List<String> actualReferenceNames = bdExamRecordMapper.bdExamPaperName(trainTaskId);
        List<String> referenceNames=bdExamRecordMapper.actualReferenceNames(trainTaskId);
        if (actualReferenceNames.size()!=0 && referenceNames.size()!=0){
            referenceNames.removeAll(actualReferenceNames);
            examDetailsDTO.setAbsentPersonNames(referenceNames);
        }else {
            examDetailsDTO.setAbsentPersonNames(new ArrayList<>());
        }

        if (examDetailsDTO.getExamTime()!=null){
            Date time1 = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(examDetailsDTO.getExamTime());
            calendar.add(Calendar.DATE,1);
            time1=calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            String string1 = simpleDateFormat.format(time1);
            examDetailsDTO.setExaminationDeadline(string1);
        }
        return examDetailsDTO;
    }

    @Override
    public BdExamRecord readOnes(String examPaperId, String trainTaskId) {
        return bdExamRecordMapper.readOnes(examPaperId,trainTaskId);
    }

    @Override
    public BdExamRecord readOne(String examPaperId, String trainTaskId, Integer examClassify , String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String string  = sysUser.getId();
        String string1 = null;
        BdExamRecord bdExamRecord = new BdExamRecord();
        if (id==null){
             bdExamRecord = bdExamRecordMapper.readOness(examPaperId, trainTaskId,examClassify, string, id);
        }
        if (id!=null){
            bdExamRecord = bdExamRecordMapper.readOne(examPaperId, trainTaskId,examClassify, string1, id);
        }
        return bdExamRecord;
    }
}
