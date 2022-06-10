package com.aiurt.boot.modules.patrol.service.impl;

import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.entity.PatrolPoolContent;
import com.aiurt.boot.modules.patrol.mapper.PatrolPoolContentMapper;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolContentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 巡检人员任务项
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Service
public class PatrolPoolContentServiceImpl extends ServiceImpl<PatrolPoolContentMapper, PatrolPoolContent> implements IPatrolPoolContentService {

	@Override
	public Result<?> queryList(Long id, HttpServletRequest req) {
		List<PatrolPoolContent> list = this.baseMapper.selectList(new QueryWrapper<PatrolPoolContent>()
				.eq(PatrolPoolContent.PATROL_POOL_ID, id)
				.eq(PatrolPoolContent.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.orderByAsc(PatrolPoolContent.SEQUENCE).eq(PatrolPoolContent.TYPE, PatrolConstant.DISABLE));
		return Result.ok(list);
	}

	@Override
	public boolean copyContent(List<PatrolContent> list, Long id) {

		List<PatrolPoolContent> addList = new ArrayList<>();

		for (PatrolContent vo : list) {
			vo.setId(null);
			PatrolPoolContent content = new PatrolPoolContent();
			BeanUtils.copyProperties(vo, content);
			content.setPatrolPoolId(id);
			addList.add(content);
		}
		return this.saveBatch(addList);
	}


}
