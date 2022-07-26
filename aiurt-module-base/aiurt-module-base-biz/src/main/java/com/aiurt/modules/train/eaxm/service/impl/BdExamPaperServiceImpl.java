package com.aiurt.modules.train.eaxm.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamPaperRel;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.exam.entity.BdExamRecordDetail;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperRelMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordDetailMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.eaxm.service.IBdExamPaperService;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.question.mapper.BdQuestionMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsAttMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 试卷库表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdExamPaperServiceImpl extends ServiceImpl<BdExamPaperMapper, BdExamPaper> implements IBdExamPaperService {

    @Autowired
    private BdQuestionMapper bdQuestionMapper;

    @Autowired
    private BdExamPaperMapper bdExamPaperMapper;
    @Autowired
    private BdExamRecordMapper bdExamRecordMapper;
    @Autowired
    private BdExamRecordDetailMapper examRecordDetailMapper;

    @Autowired
    private BdQuestionOptionsMapper questionOptionsMapper;
    @Autowired
    private BdExamPaperRelMapper bdExamPaperRelMapper;
    @Autowired
    private BdQuestionOptionsAttMapper bdQuestionOptionsAttMapper;

    @Override
    public BdExamPaper questionDetail(String id) {
        BdExamPaper bdExamPaper = bdExamPaperMapper.selectById(id);
        bdExamPaper.setNumber(bdExamPaper.getDanumber()+bdExamPaper.getScqnumber());
        bdExamPaper.setContentList(bdExamPaperMapper.trainTask(id));
        return bdExamPaper;
    }
    @Override
    public Page<BdExamPaper> queryPageList(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper, String userId, String examClassify) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<BdExamPaper> bdExamPapers = bdExamPaperMapper.queryList(pageList,bdExamPaper);
        bdExamPapers.stream().forEach(e->{
            //根据试卷id获取试卷的问题
            List<BdQuestion> bdQuestions = bdQuestionMapper.contentList(e.getId());
            //根据问题id获取附件内容、选项内容及答案
            bdQuestions.stream().forEach(q->{
                //根据问题id获取附件内容
                LambdaQueryWrapper<BdQuestionOptionsAtt> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(BdQuestionOptionsAtt::getQuestionId, q.getId()).eq(BdQuestionOptionsAtt::getType,"pic");
                List<BdQuestionOptionsAtt> list = bdQuestionOptionsAttMapper.selectList(queryWrapper);
                q.setImageList(list);
                //根据问题id获取选项内容及标准答案
                List<BdQuestionOptions> examAllQuestionOptionList = questionOptionsMapper.optionList(q.getId());
                List<String> answerList = new ArrayList<>();
                q.setExamAllQuestionOptionList(examAllQuestionOptionList);
                examAllQuestionOptionList.stream().filter(es->es.getIsRight().equals(1)).forEach(es->answerList.add(es.getContent()));
                if(ObjectUtil.isNotEmpty(bdExamPaper.getExamRecordId()))
                {
                    String examineAnswer = bdExamPaperMapper.getExamineAnswer(e,bdExamPaper.getExamRecordId(),q.getId(),df.format(date));
                    q.setAppAnswer(examineAnswer);
                }
                q.setAnswerList(answerList);
            });
            if(ObjectUtil.isNull(bdExamPaper.getExamRecordId())||ObjectUtil.isNotEmpty(bdExamPaper.getTaskId()))
            { //根据试卷id获取考生考试记录
                List<BdExamRecord> formalExamRecord  = new ArrayList<>();
                List<BdExamRecord> makeUpRecord = new ArrayList<>();
                List<BdExamRecord> examineRecordList = bdExamRecordMapper.getRecordList(e.getId(), userId,examClassify,bdExamPaper.getTaskId());
                List<BdExamRecordDetail> makeUpAnswer = new ArrayList<>();
                List<BdExamRecordDetail> formalExamAnswer = new ArrayList<>();
                examineRecordList.stream().forEach(er->{
                    bdQuestions.stream().forEach(q->{
                        if(ObjectUtil.isNotEmpty(er.getExamClassify()))
                        {
                            if (er.getExamClassify() == 0) {
                                List<BdExamRecordDetail> makeUpAnswerList = examRecordDetailMapper.getAnswerList(er.getId(), q.getId(),df.format(date));
                                makeUpAnswer.addAll(makeUpAnswerList);
                            }
                            if (er.getExamClassify() == 1) {
                                List<BdExamRecordDetail> formalAnswerList = examRecordDetailMapper.getAnswerList(er.getId(), q.getId(),df.format(date));
                                formalExamAnswer.addAll(formalAnswerList);
                            }
                        }

                    });
                    if (makeUpAnswer.size() > 0) {
                        er.setMakeUpAnswerList(makeUpAnswer);
                    }
                    if (formalExamAnswer.size() > 0) {
                        er.setFormalExamAnswerList(formalExamAnswer);
                    }
                });
                List<BdExamRecord>  makeUpRecordList = examineRecordList.stream().filter(ex -> ex.getExamClassify() != null && ex.getExamClassify().equals(0)).collect(Collectors.toList());
                List<BdExamRecord> formalExamRecordList  = examineRecordList.stream().filter(ex -> ex.getExamClassify() != null && ex.getExamClassify().equals(1)).collect(Collectors.toList());
                if (makeUpRecordList.size() > 0) {
                    makeUpRecord.addAll(makeUpRecordList);
                }
                if (formalExamRecordList.size() > 0) {
                    formalExamRecord.addAll(formalExamRecordList);
                }
                e.setFormalExamRecordList(formalExamRecord);
                e.setMakeUpRecordList(makeUpRecord);
            }
            //根据题目类型id,放入到该对应的集合里
            List<BdQuestion> singleChoiceList = bdQuestions.stream().filter(sc -> sc.getQueType().equals(1)).collect(Collectors.toList());
            List<BdQuestion>  multipleChoiceList =bdQuestions.stream().filter(sc->sc.getQueType().equals(2)).collect(Collectors.toList());
            List<BdQuestion> answerQuestionList =bdQuestions.stream().filter(sc->sc.getQueType().equals(3)).collect(Collectors.toList());
            e.setSingleChoiceList(singleChoiceList);
            e.setMultipleChoiceList( multipleChoiceList);
            e.setAnswerQuestionList(answerQuestionList);
            //计算题目类型及总分数
            e.setSingleChoiceAmount(singleChoiceList.size());
            e.setSingleChoiceCScore(singleChoiceList.size()*e.getScqscore());
            e.setMultipleChoiceAmount(multipleChoiceList.size());
            e.setMultipleChoiceCScore(multipleChoiceList.size()*e.getScqscore());
            e.setAnswerQuestionScore(e.getDanumber()*e.getDascore());
        });
        return pageList.setRecords(bdExamPapers);
    }

    @Override
    public BdExamPaper addDetail(BdExamPaper bdExamPaper) {
        List<String> questionIds = bdExamPaper.getQuestionId();
        Integer scqNumber = 0;
        Integer daNumber = 0;
        for (String questionId : questionIds) {
            BdQuestion bdQuestion = bdQuestionMapper.selectById(questionId);
            Integer queType = bdQuestion.getQueType();
            if (queType.equals(3)) {
                daNumber++;
            } else {
                scqNumber++;
            }
        }
        bdExamPaper.setScqnumber(scqNumber);
        bdExamPaper.setDanumber(daNumber);
        bdExamPaper.setNumber(scqNumber + daNumber);
        bdExamPaper.setState(1);
        this.save(bdExamPaper);
        String examPaperId = bdExamPaper.getId();
        for (String questionId : questionIds) {
            BdExamPaperRel bdExamPaperRel = new BdExamPaperRel();
            bdExamPaperRel.setPaperId(examPaperId);
            bdExamPaperRel.setQueId(questionId);
            bdExamPaperRelMapper.insert(bdExamPaperRel);
        }
        return bdExamPaper;
    }

    @Override
    public void updateState(String id) {
        LambdaUpdateWrapper<BdExamPaper> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(BdExamPaper::getState,0).eq(BdExamPaper::getId,id);
        update(updateWrapper);
    }

    @Override
    public Page<BdExamPaper> examPaperList(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper) {
        List<BdExamPaper> bdExamPapers = bdExamPaperMapper.queryList(pageList,bdExamPaper);
        return pageList.setRecords(bdExamPapers);
    }
    @Override
    public Page<BdExamPaper> examPaperPrintList(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper) {
        BdExamPaper e = bdExamPaperMapper.selectById(bdExamPaper);
            //根据试卷id获取试卷的问题
            List<BdQuestion> bdQuestions = bdQuestionMapper.contentList(e.getId());
            bdQuestions.stream().forEach(q->{
                //根据问题id获取选项内容及答案
                List<BdQuestionOptions> examAllQuestionOptionList = questionOptionsMapper.optionList(q.getId());
                q.setExamAllQuestionOptionList(examAllQuestionOptionList);
            });
            //根据题目类型id,放入到该对应的集合里
            List<BdQuestion> singleChoiceList = bdQuestions.stream().filter(sc -> sc.getQueType().equals(1)).collect(Collectors.toList());
            List<BdQuestion>  multipleChoiceList =bdQuestions.stream().filter(sc->sc.getQueType().equals(2)).collect(Collectors.toList());
            List<BdQuestion> answerQuestionList =bdQuestions.stream().filter(sc->sc.getQueType().equals(3)).collect(Collectors.toList());
            e.setSingleChoiceList(singleChoiceList);
            e.setMultipleChoiceList( multipleChoiceList);
            e.setAnswerQuestionList(answerQuestionList);
            //计算题目类型及总分数
            e.setSingleChoiceAmount(singleChoiceList.size());
            e.setSingleChoiceCScore(singleChoiceList.size()*e.getScqscore());
            e.setMultipleChoiceAmount(multipleChoiceList.size());
            e.setMultipleChoiceCScore(multipleChoiceList.size()*e.getScqscore());
            e.setAnswerQuestionScore(e.getDanumber()*e.getDascore());
            List<BdExamPaper> bdExamPapers = new ArrayList<>();
            bdExamPapers.add(e);
        return pageList.setRecords(bdExamPapers);
    }

    @Override
    public Page<BdExamPaper> examPaperDetail(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper) {
        BdExamPaper e = bdExamPaperMapper.selectById(bdExamPaper);
            //根据试卷id获取试卷的问题
            List<BdQuestion> bdQuestions = bdQuestionMapper.contentList(e.getId());
            bdQuestions.stream().forEach(q->{
                //根据问题id获取附件内容
                //根据问题id获取附件内容
                LambdaQueryWrapper<BdQuestionOptionsAtt> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(BdQuestionOptionsAtt::getQuestionId, q.getId()).eq(BdQuestionOptionsAtt::getType,"pic");
                List<BdQuestionOptionsAtt> list = bdQuestionOptionsAttMapper.selectList(queryWrapper);
                q.setImageList(list);
                //根据问题id获取选项内容及答案
                List<BdQuestionOptions> examAllQuestionOptionList = questionOptionsMapper.optionList(q.getId());
                List<String> answerList = new ArrayList<>();
                q.setExamAllQuestionOptionList(examAllQuestionOptionList);
                examAllQuestionOptionList.stream().filter(es->es.getIsRight()==1).forEach(es->answerList.add(es.getContent()));
                q.setAnswerList(answerList);
            });
            //根据题目类型id,放入到该对应的集合里
            List<BdQuestion> singleChoiceList = bdQuestions.stream().filter(sc -> sc.getQueType().equals(1)).collect(Collectors.toList());
            List<BdQuestion>  multipleChoiceList =bdQuestions.stream().filter(sc->sc.getQueType().equals(2)).collect(Collectors.toList());
            List<BdQuestion> answerQuestionList =bdQuestions.stream().filter(sc->sc.getQueType().equals(3)).collect(Collectors.toList());
            e.setSingleChoiceList(singleChoiceList);
            e.setMultipleChoiceList( multipleChoiceList);
            e.setAnswerQuestionList(answerQuestionList);
            //计算题目类型及总分数
            e.setSingleChoiceAmount(singleChoiceList.size());
            e.setSingleChoiceCScore(singleChoiceList.size()*e.getScqscore());
            e.setMultipleChoiceAmount(multipleChoiceList.size());
            e.setMultipleChoiceCScore(multipleChoiceList.size()*e.getScqscore());
            e.setAnswerQuestionScore(e.getDanumber()*e.getDascore());
            List<BdExamPaper> bdExamPapers = new ArrayList<>();
            bdExamPapers.add(e);
        return pageList.setRecords(bdExamPapers);
    }

    @Override
    public Page<BdExamPaper> getStudentAppExamRecord(Page<BdExamPaper> pageList, BdExamPaper bdExamPaper) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BdExamPaper e = bdExamPaperMapper.selectById(bdExamPaper);
            //根据试卷id获取试卷的问题
            List<BdQuestion> bdQuestions = bdQuestionMapper.contentList(e.getId());
            //根据问题id获取附件内容、选项内容及答案
            bdQuestions.stream().forEach(q->{
                //根据问题id获取附件内容
                LambdaQueryWrapper<BdQuestionOptionsAtt> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(BdQuestionOptionsAtt::getQuestionId, q.getId()).eq(BdQuestionOptionsAtt::getType,"pic");
                List<BdQuestionOptionsAtt> list = bdQuestionOptionsAttMapper.selectList(queryWrapper);
                q.setImageList(list);
                //根据问题id获取选项内容及标准答案
                List<BdQuestionOptions> examAllQuestionOptionList = questionOptionsMapper.optionList(q.getId());
                List<String> answerList = new ArrayList<>();
                q.setExamAllQuestionOptionList(examAllQuestionOptionList);
                //获取标准答案
                examAllQuestionOptionList.stream().filter(es->es.getIsRight().equals(1)).forEach(es->answerList.add(es.getContent()));
                //获取考生答案
                String examineAnswer = bdExamPaperMapper.getExamineAnswer(e,bdExamPaper.getExamRecordId(),q.getId(),df.format(date));
                q.setAppAnswer(examineAnswer);
                q.setAnswerList(answerList);
            });
            //根据题目类型id,放入到该对应的集合里
            List<BdQuestion> singleChoiceList = bdQuestions.stream().filter(sc -> sc.getQueType().equals(1)).collect(Collectors.toList());
            List<BdQuestion>  multipleChoiceList =bdQuestions.stream().filter(sc->sc.getQueType().equals(2)).collect(Collectors.toList());
            List<BdQuestion> answerQuestionList =bdQuestions.stream().filter(sc->sc.getQueType().equals(3)).collect(Collectors.toList());
            e.setSingleChoiceList(singleChoiceList);
            e.setMultipleChoiceList( multipleChoiceList);
            e.setAnswerQuestionList(answerQuestionList);
            List<BdExamPaper> bdExamPapers = new ArrayList<>();
            bdExamPapers.add(e);
        return pageList.setRecords(bdExamPapers);
    }

}
