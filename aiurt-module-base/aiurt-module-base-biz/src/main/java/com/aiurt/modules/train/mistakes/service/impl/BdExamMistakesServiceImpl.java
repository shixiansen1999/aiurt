package com.aiurt.modules.train.mistakes.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordDetailMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.exam.entity.BdExamRecordDetail;
import com.aiurt.modules.train.mistakes.constant.BdExamMistakesConstant;
import com.aiurt.modules.train.mistakes.dto.other.QuestionDetailDTO;
import com.aiurt.modules.train.mistakes.dto.req.BdExamMistakesReqDTO;
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
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsMapper;
import com.aiurt.modules.train.task.mapper.BdTrainPlanMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private BdQuestionMapper questionMapper;
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
    public List<QuestionDetailDTO> getReviewById(String id) {
        List<QuestionDetailDTO> list = new ArrayList<>();
        // 错题集id为空直接返回
        if (StrUtil.isEmpty(id)) {
            return list;
        }

        // 根据错题集id，获取考生的答题情况
        LambdaQueryWrapper<BdExamMistakesAnswer> examMistakesAnswerQueryWrapper = new LambdaQueryWrapper<>();
        examMistakesAnswerQueryWrapper.eq(BdExamMistakesAnswer::getDelFlag, CommonConstant.DEL_FLAG_0);
        examMistakesAnswerQueryWrapper.eq(BdExamMistakesAnswer::getMistakesId, id);
        List<BdExamMistakesAnswer> examMistakesAnswerList = examMistakesAnswerService.list(examMistakesAnswerQueryWrapper);
        // 考生的答题情况为空的话，直接返回
        if (CollUtil.isEmpty(examMistakesAnswerList)){
            return list;
        }
        // 对考生的答题情况进行循环，组装成QuestionDetailDTO对象
        examMistakesAnswerList.forEach(mistakesAnswer->{
            String questionId = mistakesAnswer.getQuestionId();
            // 获取习题及其选项
            BdQuestion bdQuestion = questionMapper.selectById(questionId);
            List<BdQuestionOptions> bdQuestionOptions = questionOptionsMapper.optionList(questionId);
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
            questionDetailDTO.setStuAnswer(mistakesAnswer.getStuAnswer());
            questionDetailDTO.setMideas(mideas);
            list.add(questionDetailDTO);
        });
        return list;
    }
}
