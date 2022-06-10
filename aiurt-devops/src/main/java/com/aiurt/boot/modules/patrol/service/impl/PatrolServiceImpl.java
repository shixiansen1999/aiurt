package com.aiurt.boot.modules.patrol.service.impl;

import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.mapper.PatrolMapper;
import com.aiurt.boot.modules.patrol.param.PatrolPageParam;
import com.aiurt.boot.modules.patrol.service.IPatrolService;
import com.aiurt.boot.modules.patrol.vo.PatrolDetailStrategy;
import com.aiurt.boot.modules.patrol.vo.PatrolPageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 巡检标准
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolServiceImpl extends ServiceImpl<PatrolMapper, Patrol> implements IPatrolService {

	private final ISubsystemService subsystemService;

	private final IStationService iStationService;

	private final RoleAdditionalUtils roleAdditionalUtils;

	@Override
	public Result<?> pageList(PatrolPageParam param, Integer pageNo, Integer pageSize) {

		IPage<PatrolPageVO> pages = new Page<>();

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		QueryWrapper<Patrol> queryWrapper = new QueryWrapper<>();

		queryWrapper.lambda().eq(Patrol::getDelFlag, CommonConstant.DEL_FLAG_0).orderByDesc(Patrol::getCreateTime);

		if (StringUtils.isNotBlank(param.getTitle())) {
			//查询名称
			queryWrapper.lambda().like(Patrol::getTitle, param.getTitle());
		}
		if (param.getStatus() != null) {
			queryWrapper.lambda().eq(Patrol::getStatus, param.getStatus());
		}

		//查询创建人
		if (StringUtils.isNotBlank(param.getCreateByName())) {
			queryWrapper.lambda().like(Patrol::getCreateByName, param.getCreateByName());
		}
		if (StringUtils.isNotBlank(param.getTypes())) {
			queryWrapper.lambda().eq(Patrol::getTypes, param.getTypes());
		}else {
			List<String> sysCodes = roleAdditionalUtils.getListSystemCodesByUserId(user.getId());
			if (CollectionUtils.isNotEmpty(sysCodes)) {
				queryWrapper.lambda().in(Patrol::getTypes, sysCodes);
			}
		}
		if (StringUtils.isNotBlank(param.getOrganizationId())) {
			//queryWrapper.lambda().like(Patrol::getOrganizationIds, param.getOrganizationId()).isNotNull(Patrol::getOrganizationIds);
			queryWrapper.lambda().apply("FIND_IN_SET({0},organization_ids)", param.getOrganizationId()).isNotNull(Patrol::getOrganizationIds);
		}

		IPage<Patrol> pageList = this.baseMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

		//存到vo
		BeanUtils.copyProperties(pageList, pages);
		List<PatrolPageVO> voList = new ArrayList<>();

		for (Patrol record : pageList.getRecords()) {

			PatrolPageVO vo = new PatrolPageVO();

			BeanUtils.copyProperties(record, vo);
			//线路id
			String ids = record.getOrganizationIds();
			if (StringUtils.isNotBlank(ids)) {
				List<String> list = Arrays.asList(ids.trim().split(PatrolConstant.SPL));

				Collection<Station> stations = iStationService.listByIds(list);

				if (stations != null && stations.size() > 0) {
					vo.setOrganizationName(StringUtils.join(stations.stream().map(Station::getStationName).collect(Collectors.toList()), PatrolConstant.SPL));
				}
			}

			//系统名称
			if (StringUtils.isNotBlank(record.getTypes())) {
				List<Subsystem> lineList = subsystemService.list(new LambdaQueryWrapper<Subsystem>()
						.eq(Subsystem::getDelFlag, 0).select(Subsystem::getSystemName)
						.in(Subsystem::getSystemCode, Arrays.asList(record.getTypes().split(PatrolConstant.SPL))));
				vo.setSystemTypeName(StringUtils.join(lineList.stream().map(Subsystem::getSystemName).collect(Collectors.toList()),
						PatrolConstant.SPL));
			}
			voList.add(vo);
		}

		pages.setRecords(voList);
		return Result.ok(pages);
	}

	@Override
	public Result<?> detailStrategy(Long id) {
		PatrolDetailStrategy strategy = new PatrolDetailStrategy();
		Patrol patrol = this.baseMapper.selectById(id);
		if (patrol != null) {
			BeanUtils.copyProperties(patrol, strategy);
			if (StringUtils.isNotBlank(patrol.getOrganizationIds())) {
				strategy.setOrganizationIds(Arrays.asList(patrol.getOrganizationIds().split(PatrolConstant.SPL)));
			}
			if (StringUtils.isNotBlank(patrol.getDayOfWeek())) {
				strategy.setDayOfWeek(Arrays.asList(patrol.getDayOfWeek().split(PatrolConstant.SPL)));
			}
		}

		return Result.ok(strategy);
	}
}
