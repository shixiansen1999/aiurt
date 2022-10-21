package com.aiurt.modules.train.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;
import com.aiurt.modules.train.feedback.mapper.BdTrainQuestionFeedbackMapper;
import com.aiurt.modules.train.feedback.mapper.BdTrainQuestionFeedbackOptionsMapper;
import com.aiurt.modules.train.feedback.mapper.BdTrainQuestionFeedbackQuesMapper;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackService;
import com.aiurt.modules.train.feedback.vo.BdTrainQuestionFeedbackPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 问题反馈主表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdTrainQuestionFeedbackServiceImpl extends ServiceImpl<BdTrainQuestionFeedbackMapper, BdTrainQuestionFeedback> implements IBdTrainQuestionFeedbackService {

	@Autowired
	private BdTrainQuestionFeedbackMapper bdTrainQuestionFeedbackMapper;
	@Autowired
	private BdTrainQuestionFeedbackQuesMapper bdTrainQuestionFeedbackQuesMapper;
	@Autowired
	private BdTrainQuestionFeedbackOptionsMapper bdTrainQuestionFeedbackOptionsMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(BdTrainQuestionFeedback bdTrainQuestionFeedback, List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList,List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList) {
		bdTrainQuestionFeedbackMapper.insert(bdTrainQuestionFeedback);
		if(bdTrainQuestionFeedbackQuesList!=null && bdTrainQuestionFeedbackQuesList.size()>0) {
			for(BdTrainQuestionFeedbackQues entity:bdTrainQuestionFeedbackQuesList) {
				//外键设置
				entity.setTrainQuestionFeedbackId(bdTrainQuestionFeedback.getId());
				bdTrainQuestionFeedbackQuesMapper.insert(entity);
			}
		}
		if(bdTrainQuestionFeedbackOptionsList!=null && bdTrainQuestionFeedbackOptionsList.size()>0) {
			for(BdTrainQuestionFeedbackOptions entity:bdTrainQuestionFeedbackOptionsList) {
				//外键设置
				entity.setTrainQuestionFeedbackId(bdTrainQuestionFeedback.getId());
				bdTrainQuestionFeedbackOptionsMapper.insert(entity);
			}
		}
	}
		@Override
	public void add(BdTrainQuestionFeedback bdTrainQuestionFeedback, BdTrainQuestionFeedbackPage bdTrainQuestionFeedbackPage){
		bdTrainQuestionFeedback.setState(0);
		bdTrainQuestionFeedback.setIdel(0);
		bdTrainQuestionFeedbackMapper.insert(bdTrainQuestionFeedback);

		bdTrainQuestionFeedbackPage.getBdTrainQuestionFeedbackOptionsList().forEach(o ->{
			o.setTrainQuestionFeedbackId(bdTrainQuestionFeedback.getId());
			bdTrainQuestionFeedbackOptionsMapper.insert(o);
		});
		bdTrainQuestionFeedbackPage.getBdTrainQuestionFeedbackQuesList().forEach(q ->{
			q.setTrainQuestionFeedbackId(bdTrainQuestionFeedback.getId());
		bdTrainQuestionFeedbackQuesMapper.insert(q);
		});
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(BdTrainQuestionFeedback bdTrainQuestionFeedback,List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList,List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList) {
		bdTrainQuestionFeedbackMapper.updateById(bdTrainQuestionFeedback);

		//1.先删除子表数据
		BdTrainQuestionFeedbackQues bdTrainQuestionFeedbackQues = new BdTrainQuestionFeedbackQues();
		bdTrainQuestionFeedbackQues.setIdel(1);
		bdTrainQuestionFeedbackQuesMapper.update(bdTrainQuestionFeedbackQues,new UpdateWrapper<BdTrainQuestionFeedbackQues>().eq("train_question_feedback_id",bdTrainQuestionFeedback.getId()));
		BdTrainQuestionFeedbackOptions bdTrainQuestionFeedbackOptions = new BdTrainQuestionFeedbackOptions();
		bdTrainQuestionFeedbackOptions.setIdel(1);
		bdTrainQuestionFeedbackOptionsMapper.update(bdTrainQuestionFeedbackOptions,new UpdateWrapper<BdTrainQuestionFeedbackOptions>().eq("train_question_feedback_id",bdTrainQuestionFeedback.getId()));


		//2.子表数据重新插入
		if(bdTrainQuestionFeedbackQuesList!=null && bdTrainQuestionFeedbackQuesList.size()>0) {
			for(BdTrainQuestionFeedbackQues entity:bdTrainQuestionFeedbackQuesList) {
				//外键设置
				entity.setId(null);
				entity.setTrainQuestionFeedbackId(bdTrainQuestionFeedback.getId());
				bdTrainQuestionFeedbackQuesMapper.insert(entity);
			}
		}
		if(bdTrainQuestionFeedbackOptionsList!=null && bdTrainQuestionFeedbackOptionsList.size()>0) {
			for(BdTrainQuestionFeedbackOptions entity:bdTrainQuestionFeedbackOptionsList) {
				//外键设置
				entity.setId(null);
				entity.setTrainQuestionFeedbackId(bdTrainQuestionFeedback.getId());
				bdTrainQuestionFeedbackOptionsMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
		bdTrainQuestionFeedback.setIdel(1);
		bdTrainQuestionFeedbackMapper.update(bdTrainQuestionFeedback,new UpdateWrapper<BdTrainQuestionFeedback>().eq("id",id));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
		bdTrainQuestionFeedback.setIdel(1);
		for(Serializable id:idList) {
			bdTrainQuestionFeedbackMapper.update(bdTrainQuestionFeedback,new UpdateWrapper<BdTrainQuestionFeedback>().eq("id",id.toString()));
		}
	}
	@Override
	public Page<BdTrainQuestionFeedback> queryPageList(Page<BdTrainQuestionFeedback> page, BdTrainQuestionFeedback bdTrainQuestionFeedback){
		List<BdTrainQuestionFeedback> pageList = bdTrainQuestionFeedbackMapper.selectPageList(page,bdTrainQuestionFeedback);
		return page.setRecords(pageList);
	}

	@Override
	public void enable(BdTrainQuestionFeedback bdTrainQuestionFeedback){
		LambdaUpdateWrapper<BdTrainQuestionFeedback> questionFeedbackLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
		questionFeedbackLambdaUpdateWrapper.set(BdTrainQuestionFeedback::getState,0).eq(BdTrainQuestionFeedback::getClassify,bdTrainQuestionFeedback.getClassify());
		update(questionFeedbackLambdaUpdateWrapper);
		bdTrainQuestionFeedback.setState(1);
		bdTrainQuestionFeedbackMapper.updateById(bdTrainQuestionFeedback);
	}
	@Override
	public BdTrainQuestionFeedbackPage queryByFeedbackPage(String id){
		BdTrainQuestionFeedbackPage bdTrainQuestionFeedbackPage =new BdTrainQuestionFeedbackPage();
		BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
		bdTrainQuestionFeedback =bdTrainQuestionFeedbackMapper.selectById(id);
		List<BdTrainQuestionFeedbackOptions> list = bdTrainQuestionFeedbackOptionsMapper.selectList(new LambdaQueryWrapper<BdTrainQuestionFeedbackOptions>().eq(BdTrainQuestionFeedbackOptions::getTrainQuestionFeedbackId,id).eq(BdTrainQuestionFeedbackOptions::getIdel,0));
		List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList = bdTrainQuestionFeedbackQuesMapper.selectList(new LambdaQueryWrapper<BdTrainQuestionFeedbackQues>().eq(BdTrainQuestionFeedbackQues::getTrainQuestionFeedbackId,id).eq(BdTrainQuestionFeedbackQues::getIdel,0));
        bdTrainQuestionFeedbackPage.setBdTrainQuestionFeedbackQuesList(bdTrainQuestionFeedbackQuesList);
		bdTrainQuestionFeedbackPage.setBdTrainQuestionFeedbackOptionsList(list);
		if (ObjectUtils.isNotEmpty(bdTrainQuestionFeedback)){
			BeanUtils.copyProperties(bdTrainQuestionFeedback,bdTrainQuestionFeedbackPage);
		}
		return bdTrainQuestionFeedbackPage;

	}
}
