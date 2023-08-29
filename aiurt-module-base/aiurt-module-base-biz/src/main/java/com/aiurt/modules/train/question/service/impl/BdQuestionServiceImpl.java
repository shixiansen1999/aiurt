package com.aiurt.modules.train.question.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.train.question.dto.BdQuestionDTO;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.question.mapper.BdQuestionMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsAttMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsMapper;
import com.aiurt.modules.train.question.service.IBdQuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.nashorn.internal.objects.Global;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.common.Glob;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdQuestionServiceImpl extends ServiceImpl<BdQuestionMapper, BdQuestion> implements IBdQuestionService {

    @Autowired
    private BdQuestionMapper bdQuestionMapper;

    @Autowired
    private BdQuestionOptionsAttMapper bdQuestionOptionsAttMapper;

    @Autowired
    private BdQuestionOptionsMapper bdQuestionOptionsMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public Page<BdQuestion> queryPageList(Page<BdQuestion> pageList,BdQuestion condition) {
        List<BdQuestion> questionList = bdQuestionMapper.list(pageList,condition);
        boolean b = GlobalThreadLocal.setDataFilter(false);
        questionList.forEach(e -> {
            List<BdQuestionOptionsAtt> bdQuestionOptionsActs = bdQuestionMapper.listss(e.getId());
                e.setPic("无");
                e.setVideo("无");
                e.setOther("无");
            if (CollectionUtil.isNotEmpty(bdQuestionOptionsActs)){
            List<String> collect = bdQuestionOptionsActs.stream().map(BdQuestionOptionsAtt::getType).collect(Collectors.toList());
            for (String s:collect) {
                if ("pic".equals(s)) {
                    e.setPic("有");
                }
                if ("video".equals(s)) {
                    e.setVideo("有");
                }
                if ("other".equals(s)) {
                    e.setOther("有");
                }
            }}

            List<BdQuestionOptions> lists = bdQuestionMapper.lists(e.getId());
            e.setExamAllQuestionOptionList(lists);
        });
        GlobalThreadLocal.setDataFilter(b);
        return pageList.setRecords(questionList);
    }

    @Override
    public void addBdQuestion(BdQuestion bdQuestion) {
        // 将习题的所属班组设置为当前登录人所在的班组
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        bdQuestion.setOrgCode(loginUser.getOrgCode());

        bdQuestionMapper.insert(bdQuestion);
        modifyDelete(bdQuestion);
    }

    @Override
    public void updateBdQuestion(BdQuestion bdQuestion) {
        String ids = bdQuestion.getId();
        //删除原来的试题图片
        bdQuestionMapper.deletequestionoptionsatt(ids);

        //删除原来的答案
        bdQuestionMapper.deletquestionoptions(ids);
        bdQuestionMapper.updateById(bdQuestion);
        modifyDelete(bdQuestion);
    }

    @Override
    public BdQuestion bdQuestion(String id) {
        BdQuestion bdQuestion = bdQuestionMapper.bdQuestion(id);
        String queTypeName = iSysBaseAPI.translateDict("que_type", String.valueOf(bdQuestion.getQueType()));
        bdQuestion.setQueTypeName(queTypeName);
        List<BdQuestionOptions> lists = bdQuestionMapper.lists(id);
        List<BdQuestionOptionsAtt> enclosures = bdQuestionMapper.listss(id);
        if (ObjectUtil.isNotNull(bdQuestion)){
        bdQuestion.setEnclosureList(enclosures);
        bdQuestion.setExamAllQuestionOptionList(lists);
        }
        return bdQuestion;
    }

    @Override
    public List<BdQuestion> getLearningMaterials(String id) {
        //根据试卷id获取习题id
        List<String> quesId = bdQuestionMapper.getQuesId(id);
        //根据习题id获取正确的选项内容
        List<BdQuestion> options = bdQuestionMapper.getOptions(quesId);

        options.forEach(bdQuestion -> {
            List<BdQuestionOptionsAtt> att = bdQuestionOptionsAttMapper.getAtt(bdQuestion.getId());
            //根据文件类型分类
            List<BdQuestionOptionsAtt> imageList = att.stream().filter(a -> ("pic").equals(a.getType())).collect(Collectors.toList());
            List<BdQuestionOptionsAtt> videoList = att.stream().filter(a -> ("video").equals(a.getType())).collect(Collectors.toList());
            List<BdQuestionOptionsAtt> materialsList = att.stream().filter(a -> ("other").equals(a.getType())).collect(Collectors.toList());
            bdQuestion.setImageList(imageList);
            bdQuestion.setVideoList(videoList);
            bdQuestion.setMaterialsList(materialsList);
        });
        return options;
    }

    @Override
    public List<BdQuestion> randomSelectionQuestion(String categoryIds, Integer choiceQuestionNum, Integer shortAnswerQuestionNum) {
        List<BdQuestion> questionList = bdQuestionMapper.randomSelectionQuestion(StrUtil.isNotBlank(categoryIds) ? StrUtil.splitTrim(categoryIds, ",") : null, choiceQuestionNum, shortAnswerQuestionNum);
        List<DictModel> queType = iSysBaseAPI.queryDictItemsByCode("que_type");
        Map<String, String> queTypeMap = queType.stream().collect(Collectors.toMap(DictModel::getValue,DictModel::getText));
        //查找试题
        List<String> questionIds = questionList.stream().map(BdQuestion::getId).collect(Collectors.toList());
        LambdaQueryWrapper<BdQuestionOptionsAtt> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BdQuestionOptionsAtt::getQuestionId, questionIds);
        List<BdQuestionOptionsAtt> bdQuestionOptionsActs = bdQuestionOptionsAttMapper.selectList(wrapper);
        questionList.forEach(e -> {
            e.setQueTypeName(queTypeMap.get(e.getQueType().toString()));
            e.setPic("无");
            e.setVideo("无");
            e.setOther("无");
            List<BdQuestionOptionsAtt> optionsAtts = Optional.ofNullable(bdQuestionOptionsActs).orElse(CollUtil.newArrayList()).stream().filter(b -> b.getQuestionId().equals(e.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(optionsAtts)){
                List<String> collect = optionsAtts.stream().map(BdQuestionOptionsAtt::getType).collect(Collectors.toList());
                for (String s:collect) {
                    if ("pic".equals(s)) {
                        e.setPic("有");
                    }
                    if ("video".equals(s)) {
                        e.setVideo("有");
                    }
                    if ("other".equals(s)) {
                        e.setOther("有");
                    }
                }}
        });
        return questionList;
    }

    @Override
    public BdQuestionDTO getQuestionNum(String categoryIds) {

        Integer choiceQuestionNum = bdQuestionMapper.getQuestionNum(StrUtil.isNotBlank(categoryIds) ? StrUtil.splitTrim(categoryIds, ",") : null, 1);
        Integer shortAnswerQuestionNum = bdQuestionMapper.getQuestionNum(StrUtil.isNotBlank(categoryIds) ? StrUtil.splitTrim(categoryIds, ",") : null, 2);
        BdQuestionDTO dto = new BdQuestionDTO();
        dto.setChoiceQuestionNum(choiceQuestionNum);
        dto.setShortAnswerQuestionNum(shortAnswerQuestionNum);

        return dto;
    }

    private void modifyDelete(BdQuestion bdQuestion) {
        if( bdQuestion.getImageList() != null){
            for (BdQuestionOptionsAtt bdQuestionOptionsAtt : bdQuestion.getImageList()){
                BdQuestionOptionsAtt bdQuestionOptionsAtt1 = new BdQuestionOptionsAtt();
                bdQuestionOptionsAtt1.setQuestionId(bdQuestion.getId());
                bdQuestionOptionsAtt1.setPath(bdQuestionOptionsAtt.getPath());
                bdQuestionOptionsAtt1.setName(bdQuestionOptionsAtt.getName());
                bdQuestionOptionsAtt1.setType("pic");
                bdQuestionOptionsAttMapper.insert(bdQuestionOptionsAtt1);
            }
        }
        if( bdQuestion.getVideoList() != null){
            for (BdQuestionOptionsAtt bdQuestionOptionsAtt : bdQuestion.getVideoList()){
                BdQuestionOptionsAtt bdQuestionOptionsAtt1 = new BdQuestionOptionsAtt();
                bdQuestionOptionsAtt1.setQuestionId(bdQuestion.getId());
                bdQuestionOptionsAtt1.setPath(bdQuestionOptionsAtt.getPath());
                bdQuestionOptionsAtt1.setName(bdQuestionOptionsAtt.getName());
                bdQuestionOptionsAtt1.setType("video");
                bdQuestionOptionsAttMapper.insert(bdQuestionOptionsAtt1);
            }
        }
        if( bdQuestion.getMaterialsList() != null){
            for (BdQuestionOptionsAtt bdQuestionOptionsAtt : bdQuestion.getMaterialsList()){
                BdQuestionOptionsAtt bdQuestionOptionsAtt1 = new BdQuestionOptionsAtt();
                bdQuestionOptionsAtt1.setQuestionId(bdQuestion.getId());
                bdQuestionOptionsAtt1.setPath(bdQuestionOptionsAtt.getPath());
                bdQuestionOptionsAtt1.setName(bdQuestionOptionsAtt.getName());
                bdQuestionOptionsAtt1.setType("other");
                bdQuestionOptionsAttMapper.insert(bdQuestionOptionsAtt1);
            }
        }
        //选择题
        if (bdQuestion.getSelectList() != null){
            for (String bdQuestionOptions : bdQuestion.getSelectList()){
                BdQuestionOptions bdQuestionOptions1 = new BdQuestionOptions();
                bdQuestionOptions1.setQuestionId(bdQuestion.getId());
                bdQuestionOptions1.setContent(bdQuestionOptions);
                if (bdQuestion.getQueType().equals(1) && null!=bdQuestion.getAnswer() && bdQuestion.getAnswer().contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(1);
                }
                if (bdQuestion.getQueType().equals(1) && null!=bdQuestion.getAnswer() && !bdQuestion.getAnswer().contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(0);
                }
                if (bdQuestion.getQueType().equals(2) && null!=bdQuestion.getAnswer() && Arrays.asList( bdQuestion.getAnswer().split(",")).contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(1);
                }
                if (bdQuestion.getQueType().equals(2) && null!=bdQuestion.getAnswer() && !Arrays.asList( bdQuestion.getAnswer().split(",")).contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(0);
                }
                bdQuestionOptionsMapper.insert(bdQuestionOptions1);
            }
        }
        //简答题
        if (bdQuestion.getSelectList().length ==0){
            BdQuestionOptions bdQuestionOptions1 = new BdQuestionOptions();
            bdQuestionOptions1.setQuestionId(bdQuestion.getId());
            bdQuestionOptions1.setContent(bdQuestion.getAnswer());
            bdQuestionOptions1.setIsRight(1);
            bdQuestionOptionsMapper.insert(bdQuestionOptions1);
        }
    }
}
