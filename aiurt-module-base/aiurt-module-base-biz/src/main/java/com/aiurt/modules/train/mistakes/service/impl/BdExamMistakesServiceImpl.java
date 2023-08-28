package com.aiurt.modules.train.mistakes.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperRelMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordDetailMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.exam.entity.BdExamRecordDetail;
import com.aiurt.modules.train.mistakes.constant.BdExamMistakesConstant;
import com.aiurt.modules.train.mistakes.dto.other.QuestionDetailDTO;
import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesReqDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesAppDetailRespDTO;
import com.aiurt.modules.train.mistakes.dto.resp.BdExamMistakesRespDTO;
import com.aiurt.modules.train.mistakes.entity.BdExamMistakes;
import com.aiurt.modules.train.mistakes.entity.BdExamMistakesAnswer;
import com.aiurt.modules.train.mistakes.entity.BdExamMistakesQuestion;
import com.aiurt.modules.train.mistakes.mapper.BdExamMistakesMapper;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesAnswerService;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesQuestionService;
import com.aiurt.modules.train.mistakes.service.IBdExamMistakesService;
import com.aiurt.modules.train.question.dto.BdQuestionOptionsDTO;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.question.mapper.BdQuestionMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsAttMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsMapper;
import com.aiurt.modules.train.task.mapper.BdTrainPlanMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 错题集的service的实现类
 *
 * @author 华宜威
 * @date 2023-08-25 08:54:03
 */
@Service
public class BdExamMistakesServiceImpl extends ServiceImpl<BdExamMistakesMapper, BdExamMistakes> implements IBdExamMistakesService {

    @Autowired
    private BdExamRecordMapper examRecordMapper;
    @Autowired
    private BdExamRecordDetailMapper examRecordDetailMapper;
    @Autowired
    private BdExamPaperMapper examPaperMapper;
    @Autowired
    private BdQuestionMapper bdQuestionMapper;
    @Autowired
    private BdQuestionMapper questionMapper;
    @Autowired
    private BdExamPaperMapper bdExamPaperMapper;

    @Autowired
    private BdQuestionOptionsAttMapper bdQuestionOptionsAttMapper;

    @Autowired
    private BdQuestionOptionsMapper questionOptionsMapper;
    @Autowired
    private BdTrainPlanMapper trainPlanMapper;
    @Autowired
    private IBdExamMistakesQuestionService examMistakesQuestionService;
    @Autowired
    private IBdExamMistakesAnswerService examMistakesAnswerService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateMistakesByExamRecodeId(String examRecordId) {
        BdExamRecord bdExamRecord = examRecordMapper.selectById(examRecordId);
        // 差不到考试记录，直接返回
        if (bdExamRecord == null || bdExamRecord.getScore() == null) {
            return;
        }
        // 判断是否符合生成错题集的条件
        // 不是已经结束的考试，直接返回
        if (!ExamConstans.RECORD_OVER.equals(bdExamRecord.getIsRelease())) {
            return;
        }
        // 获取考卷信息
        BdExamPaper bdExamPaper = examPaperMapper.selectById(bdExamRecord.getExamPaperId());

        // 考卷总分
        Integer totalScore = bdExamPaper.getScore();
        // 考卷及格分
        Integer passScore = bdExamPaper.getPassscore();
        // 考生分数
        Integer userScore = bdExamRecord.getScore();
        // 只有及格并且不是满分的，才生成错题集数据，否则直接返回
        if (!(userScore >= passScore && userScore < totalScore)){
            return;
        }

        // 生成错题集
        BdExamMistakes mistakes = new BdExamMistakes();
        mistakes.setUserId(bdExamRecord.getUserId());
        mistakes.setTrainTaskId(bdExamRecord.getTrainTaskId());
        mistakes.setExamPaperId(bdExamPaper.getId());
        mistakes.setName(bdExamPaper.getName());
        // 错题集的初始状态：1未开答
        mistakes.setState(BdExamMistakesConstant.EXAM_MISTAKES_STATE_NOT_ANSWER);
        this.save(mistakes);
        // 生成错题集-考题关联表的数据
        // 1、查询考生的答题情况，只查答错的题
        LambdaQueryWrapper<BdExamRecordDetail> examRecordDetailQueryWrapper = new LambdaQueryWrapper<>();
        examRecordDetailQueryWrapper.eq(BdExamRecordDetail::getExamRecordId, bdExamRecord.getId());
        examRecordDetailQueryWrapper.ne(BdExamRecordDetail::getIsTrue, 1);
        List<BdExamRecordDetail> bdExamRecordDetailList = examRecordDetailMapper.selectList(examRecordDetailQueryWrapper);
        if (CollUtil.isEmpty(bdExamRecordDetailList)) {
            return;
        }
        // 2、根据考生的答题情况，生成错题集-考题关联表的数据
        List<BdExamMistakesQuestion> examMistakesQuestionList = bdExamRecordDetailList.stream().map(recordDetail -> {
            BdExamMistakesQuestion examMistakesQuestion = new BdExamMistakesQuestion();
            examMistakesQuestion.setMistakesId(mistakes.getId());
            examMistakesQuestion.setQuestionId(recordDetail.getQueId());
            return examMistakesQuestion;
        }).collect(Collectors.toList());
        // 3、保存错题集-考题关联表的数据
        examMistakesQuestionService.saveBatch(examMistakesQuestionList);

    }


    @Override
    public IPage<BdExamMistakesRespDTO> pageList(BdExamMistakesReqDTO bdExamMistakesReqDTO) {
        Integer pageNo = Optional.ofNullable(bdExamMistakesReqDTO.getPageNo()).orElseGet(() -> 1);
        Integer pageSize = Optional.ofNullable(bdExamMistakesReqDTO.getPageSize()).orElseGet(() -> 10);
        Page<BdExamMistakesRespDTO> page = new Page<>(pageNo, pageSize);
        return this.baseMapper.pageList(page, bdExamMistakesReqDTO);
    }

    @Override
    public BdExamMistakesAppDetailRespDTO getAppMistakesDetail(String id, String examRecordId) {
        // 根据id获取错题集
        BdExamMistakes examMistakes = this.getById(id);
        // 获取考卷，需要总题目数和总分数
        BdExamPaper examPaper = examPaperMapper.selectById(examMistakes.getExamPaperId());

        // 问题集合
        List<BdQuestion> bdQuestionList;
        // 考生答案的Map,问题id做key，考生答案做value
        Map<String, String> stuAnswerMap;
        // 如果错题集状态是未作答的，获取的详情就是整张考卷的题目
        if (BdExamMistakesConstant.EXAM_MISTAKES_STATE_NOT_ANSWER.equals(examMistakes.getState())) {
            //根据试卷id获取试卷的问题
            bdQuestionList = bdQuestionMapper.contentList(examMistakes.getExamPaperId());
            List<String> questionIdList = bdQuestionList.stream().map(BdQuestion::getId).collect(Collectors.toList());
            // 获取考生答案，做成一个map，使用问题id做可key，考生答案做value
            LambdaQueryWrapper<BdExamRecordDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BdExamRecordDetail::getExamRecordId, examRecordId);
            queryWrapper.in(BdExamRecordDetail::getQueId, questionIdList);
            queryWrapper.orderByDesc(BdExamRecordDetail::getCreateTime);
            List<BdExamRecordDetail> bdExamRecordDetailList = examRecordDetailMapper.selectList(queryWrapper);
            stuAnswerMap = bdExamRecordDetailList.stream().collect(Collectors.toMap(BdExamRecordDetail::getQueId, BdExamRecordDetail::getAnswer, (a, b) -> a));
        }else{
            bdQuestionList = examMistakesQuestionService.getQuestionByMistakesId(examMistakes.getId());
            List<String> questionIdList = bdQuestionList.stream().map(BdQuestion::getId).collect(Collectors.toList());
            LambdaQueryWrapper<BdExamMistakesAnswer> answerQueryWrapper = new LambdaQueryWrapper<>();
            answerQueryWrapper.eq(BdExamMistakesAnswer::getDelFlag, CommonConstant.DEL_FLAG_0);
            answerQueryWrapper.eq(BdExamMistakesAnswer::getMistakesId, examMistakes.getId());
            answerQueryWrapper.in(BdExamMistakesAnswer::getQuestionId, questionIdList);
            answerQueryWrapper.orderByDesc(BdExamMistakesAnswer::getCreateTime);
            List<BdExamMistakesAnswer> answerList = examMistakesAnswerService.list(answerQueryWrapper);
            stuAnswerMap = answerList.stream().collect(Collectors.toMap(BdExamMistakesAnswer::getQuestionId, BdExamMistakesAnswer::getStuAnswer, (a, b) -> a));
        }

        //根据问题id获取附件内容、选项内容及答案
        bdQuestionList.forEach(question->{
            //根据问题id获取附件内容
            LambdaQueryWrapper<BdQuestionOptionsAtt> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BdQuestionOptionsAtt::getQuestionId, question.getId()).eq(BdQuestionOptionsAtt::getType,"pic");
            List<BdQuestionOptionsAtt> list = bdQuestionOptionsAttMapper.selectList(queryWrapper);
            question.setImageList(list);
            //设置所有选项
            List<BdQuestionOptions> examAllQuestionOptionList = questionOptionsMapper.optionList(question.getId());
            question.setExamAllQuestionOptionList(examAllQuestionOptionList);
            // 设置标准答案
            List<String> answerList = new ArrayList<>();
            examAllQuestionOptionList.stream().filter(es->es.getIsRight().equals(1)).forEach(es->answerList.add(es.getContent()));
            question.setAnswerList(answerList);
            // 获取考生答案
            question.setAppAnswer(stuAnswerMap.get(question.getId()));
        });


        // 组装返回对象
        BdExamMistakesAppDetailRespDTO respDTO = new BdExamMistakesAppDetailRespDTO();

        //根据题目类型id,放入到该对应的集合里
        List<BdQuestion> singleChoiceList = bdQuestionList.stream().filter(sc -> sc.getQueType().equals(1)).collect(Collectors.toList());
        List<BdQuestion>  multipleChoiceList =bdQuestionList.stream().filter(sc->sc.getQueType().equals(2)).collect(Collectors.toList());
        List<BdQuestion> answerQuestionList =bdQuestionList.stream().filter(sc->sc.getQueType().equals(3)).collect(Collectors.toList());
        respDTO.setSingleChoiceList(singleChoiceList);
        respDTO.setMultipleChoiceList( multipleChoiceList);
        respDTO.setAnswerQuestionList(answerQuestionList);
        //赋值原考卷总题目数量，总分数
        respDTO.setName(examPaper.getName());
        respDTO.setNumber(examPaper.getNumber());
        respDTO.setScore(examPaper.getScore());
        // 错题集状态
        respDTO.setState(examMistakes.getState());
        return respDTO;
    }

    @Override
    public List<QuestionDetailDTO> getReviewById(String id) {
        List<QuestionDetailDTO> list = new ArrayList<>();
        // 错题集id为空直接返回
        if (StrUtil.isEmpty(id)) {
            return list;
        }

        // 根据错题集id，获取考生的错题，这里要获取考生的错题而不是直接用答题情况是因为当考生没有答错题集时，错题也是存在的
        LambdaQueryWrapper<BdExamMistakesQuestion> examMistakesQuestionQueryWrapper = new LambdaQueryWrapper<>();
        examMistakesQuestionQueryWrapper.eq(BdExamMistakesQuestion::getDelFlag, CommonConstant.DEL_FLAG_0);
        examMistakesQuestionQueryWrapper.eq(BdExamMistakesQuestion::getMistakesId, id);
        List<BdExamMistakesQuestion> examMistakesQuestionList = examMistakesQuestionService.list(examMistakesQuestionQueryWrapper);
        // 考生的错题情况为空的话，直接返回
        if (CollUtil.isEmpty(examMistakesQuestionList)){
            return list;
        }

        // 根据错题集id，获取考生的答题情况，并整合成一个Map，key是习题id，value是考生答案
        LambdaQueryWrapper<BdExamMistakesAnswer> examMistakesAnswerQueryWrapper = new LambdaQueryWrapper<>();
        examMistakesAnswerQueryWrapper.eq(BdExamMistakesAnswer::getDelFlag, CommonConstant.DEL_FLAG_0);
        examMistakesAnswerQueryWrapper.eq(BdExamMistakesAnswer::getMistakesId, id);
        List<BdExamMistakesAnswer> examMistakesAnswerList = examMistakesAnswerService.list(examMistakesAnswerQueryWrapper);
        Map<String, String> answerMap = examMistakesAnswerList.stream().collect(Collectors.toMap(BdExamMistakesAnswer::getQuestionId, BdExamMistakesAnswer::getStuAnswer));

        // 将习题根据id查询合成一个map
        List<String> questionIdList = examMistakesQuestionList.stream().map(BdExamMistakesQuestion::getQuestionId).collect(Collectors.toList());
        List<BdQuestion> bdQuestionList = questionMapper.selectBatchIds(questionIdList);
        Map<String, BdQuestion> questionMap = bdQuestionList.stream().collect(Collectors.toMap(BdQuestion::getId, question -> question));

        // 将选项根据习题id合成一个map
        LambdaQueryWrapper<BdQuestionOptions> optionsQueryWrapper = new LambdaQueryWrapper<>();
        optionsQueryWrapper.in(BdQuestionOptions::getQuestionId, questionIdList);
        List<BdQuestionOptions> bdQuestionOptionsList = questionOptionsMapper.selectList(optionsQueryWrapper);
        Map<String, List<BdQuestionOptions>> optionsMap = bdQuestionOptionsList.stream().collect(Collectors.groupingBy(BdQuestionOptions::getQuestionId));

        // 对考生的答题情况进行循环，组装成QuestionDetailDTO对象
        examMistakesQuestionList.forEach(mistakesQuestion->{
            String questionId = mistakesQuestion.getQuestionId();
            // 获取习题及其选项
            BdQuestion bdQuestion = questionMap.get(questionId);
            List<BdQuestionOptions> bdQuestionOptions = optionsMap.get(questionId);
            // 获取多媒体
            List<BdQuestionOptionsAtt> mideas = trainPlanMapper.getMidea(questionId);

            // 将实体类的选项对象转化成要返回的选项DTO对象
            List<BdQuestionOptionsDTO> bdQuestionOptionsDTOList = bdQuestionOptions.stream().map(options -> {
                BdQuestionOptionsDTO bdQuestionOptionsDTO = new BdQuestionOptionsDTO();
                BeanUtils.copyProperties(options, bdQuestionOptionsDTO);
                return bdQuestionOptionsDTO;
            }).collect(Collectors.toList());

            // 获取标准答案, 多个使用中文逗号隔开
            String answer = bdQuestionOptionsDTOList.stream()
                    .filter(optionsDTO -> Integer.valueOf(1).equals(optionsDTO.getIsRight()))
                    .map(BdQuestionOptionsDTO::getContent)
                    .collect(Collectors.joining("，"));

            QuestionDetailDTO questionDetailDTO = new QuestionDetailDTO();
            questionDetailDTO.setContent(bdQuestion.getContent());
            questionDetailDTO.setQueType(bdQuestion.getQueType());
            questionDetailDTO.setBdQuestionOptionsDTOList(bdQuestionOptionsDTOList);
            questionDetailDTO.setAnswer(answer);
            questionDetailDTO.setStuAnswer(answerMap.get(questionId));
            questionDetailDTO.setMideas(mideas);
            list.add(questionDetailDTO);
        });
        return list;
    }

    @Override
    public void auditById(String id, Integer isPass) {
        if (StrUtil.isEmpty(id)){
            throw new AiurtBootException("错题集id参数不能为空");
        }
        BdExamMistakes examMistakes = this.getById(id);
        if (examMistakes == null) {
            throw new AiurtBootException("错题集查询为空");
        }
        if (!BdExamMistakesConstant.EXAM_MISTAKES_STATE_PENDING_REVIEW.equals(examMistakes.getState())){
            throw new AiurtBootException("只有待审核状态才能审核");
        }
        Integer newState = Integer.valueOf(1).equals(isPass) ? BdExamMistakesConstant.EXAM_MISTAKES_STATE_PASSED : BdExamMistakesConstant.EXAM_MISTAKES_STATE_REJECTED;
        LambdaUpdateWrapper<BdExamMistakes> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BdExamMistakes::getId, id);
        updateWrapper.set(BdExamMistakes::getState, newState);
        this.update(updateWrapper);
    }
}
