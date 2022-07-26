package com.aiurt.modules.train.task.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.train.task.dto.BdTrainSignDTO;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskSignMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainTaskSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 培训签到记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdTrainTaskSignServiceImpl extends ServiceImpl<BdTrainTaskSignMapper, BdTrainTaskSign> implements IBdTrainTaskSignService {

	@Autowired
	private BdTrainTaskSignMapper bdTrainTaskSignMapper;

	@Autowired
	private BdTrainTaskMapper bdTrainTaskMapper;

	@Override
	public List<BdTrainTaskSign> selectByMainId(String mainId) {
		return bdTrainTaskSignMapper.selectByMainId(mainId);
	}

	@Override
	public List<BdTrainSignDTO> getById(String id) {
		List<BdTrainTaskSign> bdTrainTaskSigns = bdTrainTaskSignMapper.selectByMainId(id);
		List<BdTrainSignDTO> list = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(bdTrainTaskSigns)) {
			String trainTaskId = bdTrainTaskSigns.get(0).getTrainTaskId();
			//获取任务信息
			BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(trainTaskId);
			String taskName = bdTrainTask.getTaskName();
			String teamName = bdTrainTaskMapper.getTeamName(bdTrainTask.getTaskTeamId());
			Date startTime = bdTrainTask.getStartTime();
			Date endTime = bdTrainTask.getEndTime();
			//根据轮数分组
			List<Integer> collect = bdTrainTaskSigns.stream().map(BdTrainTaskSign::getNumber).distinct().collect(Collectors.toList());

			collect.forEach(c->{
				BdTrainSignDTO bdTrainSignDTO = new BdTrainSignDTO();
				bdTrainSignDTO.setNum(c);
				bdTrainSignDTO.setNumbers(bdTrainTaskSigns.size());
				bdTrainSignDTO.setTaskName(taskName);
				bdTrainSignDTO.setTaskTeamName(teamName);
				bdTrainSignDTO.setStartTime(startTime);
				bdTrainSignDTO.setEndTime(endTime);
				List<BdTrainTaskSign> collect1 = bdTrainTaskSigns.stream().filter(s -> s.getNumber().equals(c)).collect(Collectors.toList());
				bdTrainSignDTO.setBdTrainTaskSignList(collect1);
				bdTrainSignDTO.setUserNum(collect1.size());
				list.add(bdTrainSignDTO);
			});
			return list;
		}
		return list;
	}

}
