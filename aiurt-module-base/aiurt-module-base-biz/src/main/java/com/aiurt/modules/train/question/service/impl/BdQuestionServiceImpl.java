package com.aiurt.modules.train.question.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.question.mapper.BdQuestionMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsAttMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsMapper;
import com.aiurt.modules.train.question.service.IBdQuestionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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

    @Override
    public Page<BdQuestion> queryPageList(Page<BdQuestion> pageList,BdQuestion condition) {
        List<BdQuestion> questionList = bdQuestionMapper.list(pageList,condition);
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
        });
        return pageList.setRecords(questionList);
    }

    @Override
    public void addBdQuestion(BdQuestion bdQuestion) {
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
