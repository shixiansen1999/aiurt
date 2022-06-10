package com.aiurt.boot.modules.patrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.PatrolContent;
import com.swsc.copsms.modules.patrol.mapper.PatrolContentMapper;
import com.swsc.copsms.modules.patrol.service.IPatrolContentService;
import com.swsc.copsms.modules.patrol.vo.PatrolContentTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 巡检项内容
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolContentServiceImpl extends ServiceImpl<PatrolContentMapper, PatrolContent> implements IPatrolContentService {

	@Override
	public Result<?> queryTree(HttpServletRequest req, Long id) {

		List<PatrolContent> list = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.RECORD_ID, id)
				.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolContent.PARENT_ID, 0)
		);
		List<PatrolContentTreeVO> voList = new ArrayList<>();
		for (PatrolContent content : list) {
			PatrolContentTreeVO vo = new PatrolContentTreeVO();
			BeanUtils.copyProperties(content, vo);
			vo.setChildren(getTree(id, vo.getId()));
			voList.add(vo);
		}

		return Result.ok(voList);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> add(PatrolContent patrolContent) {
		PatrolContent one = this.baseMapper.selectOne(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolContent.RECORD_ID, patrolContent.getRecordId())
				.eq(PatrolContent.SEQUENCE, patrolContent.getSequence()));
		//查重,若有相同,则顺序加1
		if (one != null) {
			one.setSequence(one.getSequence() + 1);
			int insert = this.baseMapper.updateById(one);
			if (insert < 1) {
				throw new SwscException("保存排序失败,请稍后再试");
			}
			sequence(one, patrolContent.getRecordId(), patrolContent.getSequence());
		}
		int insert = this.baseMapper.insert(patrolContent);
		if (insert < 1) {
			throw new SwscException("新增失败");
		}
		return Result.ok();
	}

	@Override
	public Result<?> edit(PatrolContent patrolContent) {
		PatrolContent byId = this.baseMapper.selectById(patrolContent.getId());
		int n = byId.getSequence() - patrolContent.getSequence();
		if (n != 0) {
			if (n > 0) {
				sequence(patrolContent, patrolContent.getRecordId(), patrolContent.getSequence());
			} else {
				//在后面
				PatrolContent one = this.baseMapper.selectOne(new QueryWrapper<PatrolContent>()
						.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
						.eq(PatrolContent.RECORD_ID, patrolContent.getRecordId())
						.eq(PatrolContent.SEQUENCE, patrolContent.getSequence()));
				if (one != null) {
					//查询所有在此之间的数据
					List<PatrolContent> contentList = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
							.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
							.eq(PatrolContent.RECORD_ID, patrolContent.getRecordId())
							.between(PatrolContent.SEQUENCE, byId.getSequence() + 1, patrolContent.getSequence()));
					for (PatrolContent content : contentList) {
						content.setSequence(content.getSequence() - 1);
						int i = this.baseMapper.updateById(content);
						if (i < 1) {
							throw new SwscException("保存排序失败,请稍后再试");
						}
					}
				}
			}
		}

		int insert = this.baseMapper.updateById(patrolContent);
		if (insert < 1) {
			throw new SwscException("修改失败");
		}

		return Result.ok();
	}



	@Override
	public Result<?> queryList(HttpServletRequest req, Long id) {



		List<PatrolContent> list = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.RECORD_ID, id)
				.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolContent.TYPE, 0));


		return Result.ok(list);
	}

	/**
	 * 排序
	 *
	 * @param patrolContent 巡逻的内容
	 * @param recordId      记录id
	 * @param sequence      序列
	 */
	private void sequence(PatrolContent patrolContent, Long recordId, Integer sequence) {
		int len = patrolContent.getSequence();
		List<PatrolContent> contentList = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolContent.RECORD_ID, recordId)
				.gt(PatrolContent.SEQUENCE, sequence)
				.orderByAsc(PatrolContent.SEQUENCE));
		for (PatrolContent content : contentList) {
			//若有相同,顺序加1
			if (len++ == content.getSequence()) {
				content.setSequence(content.getSequence() + 1);
				int i = this.baseMapper.updateById(content);
				if (i < 1) {
					throw new SwscException("保存排序失败,请稍后再试");
				}
			} else {
				break;
			}
		}
	}


	/**
	 * 树形获取
	 *
	 * @param parentId
	 * @return
	 */
	public List<PatrolContentTreeVO> getTree(Long id, Long parentId) {
		List<PatrolContentTreeVO> list = new ArrayList<>();
		List<PatrolContent> contents = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.RECORD_ID, id)
				.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG).eq(PatrolContent.PARENT_ID, parentId));
		for (PatrolContent content : contents) {
			PatrolContentTreeVO vo = new PatrolContentTreeVO();
			BeanUtils.copyProperties(content, vo);
			vo.setChildren(getTree(id, vo.getId()));
			list.add(vo);
		}
		return list;
	}
}
