package com.aiurt.boot.modules.patrol.service.impl;


import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.mapper.PatrolContentMapper;
import com.aiurt.boot.modules.patrol.service.IPatrolContentService;
import com.aiurt.boot.modules.patrol.vo.PatrolContentTreeVO;
import com.aiurt.boot.modules.patrol.vo.importdir.PatrolContentImportVO;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 巡检项内容
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolContentServiceImpl extends ServiceImpl<PatrolContentMapper, PatrolContent> implements IPatrolContentService {

	@Override
	public Result<List<PatrolContentTreeVO>> queryTree(Long id) {
		List<PatrolContentTreeVO> voList = new ArrayList<>();

		List<PatrolContent> list = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.RECORD_ID, id)
				.eq(PatrolContent.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.orderByAsc(PatrolContent.SEQUENCE)
		);

		if (CollectionUtils.isNotEmpty(list)){

			Map<Long, List<PatrolContent>> listMap = list.stream().collect(Collectors.groupingBy(PatrolContent::getParentId));

			if (listMap.get(PatrolConstant.NUM_LONG_0)==null){
				return Result.ok(voList);
			}

			List<PatrolContent> contentList = listMap.get(PatrolConstant.NUM_LONG_0);

			for (PatrolContent content : contentList) {
				PatrolContentTreeVO treeVO = new PatrolContentTreeVO();
				BeanUtils.copyProperties(content,treeVO);

				treeVO.setChildren(setTree(listMap,content.getCode()));
				voList.add(treeVO);
			}
		}

		return Result.ok(voList);
	}

	private List<PatrolContentTreeVO> setTree(Map<Long, List<PatrolContent>> listMap, Long code) {
		List<PatrolContentTreeVO> voList = null;
		List<PatrolContent> contents = listMap.get(code);
		if (CollectionUtils.isNotEmpty(contents)){
			voList = new ArrayList<>();
			for (PatrolContent content : contents) {
				PatrolContentTreeVO treeVO = new PatrolContentTreeVO();
				BeanUtils.copyProperties(content,treeVO);
				treeVO.setChildren(setTree(listMap,content.getCode()));
				voList.add(treeVO);
			}
		}

		return voList;
	}

	@Override
	public Result<?> queryList( Long id) {
		List<PatrolContent> list = this.baseMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.RECORD_ID, id)
				.eq(PatrolContent.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.eq(PatrolContent.TYPE, 0));

		return Result.ok(list);
	}

	@Override
	public List<PatrolContentImportVO> selectExportList(Long id) {
		return this.baseMapper.selectExportList(id);
	}


}
