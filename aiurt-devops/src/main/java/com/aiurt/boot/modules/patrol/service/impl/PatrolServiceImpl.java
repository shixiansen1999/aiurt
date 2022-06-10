package com.aiurt.boot.modules.patrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.Patrol;
import com.swsc.copsms.modules.patrol.mapper.PatrolMapper;
import com.swsc.copsms.modules.patrol.param.PatrolPageParam;
import com.swsc.copsms.modules.patrol.service.IPatrolService;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 巡检标准
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolServiceImpl extends ServiceImpl<PatrolMapper, Patrol> implements IPatrolService {

	private final SysUserMapper sysUserMapper;

	@Override
	public Result<?> pageList(PatrolPageParam param, Integer pageNo, Integer pageSize, HttpServletRequest req) {

		QueryWrapper<Patrol> queryWrapper = new QueryWrapper<>();

		queryWrapper.eq(Patrol.DEL_FLAG, PatrolConstant.UN_DEL_FLAG);
		if (StringUtils.isNotBlank(param.getTitle())) {
			//查询名称
			queryWrapper.like(Patrol.TITLE,param.getTitle());
		}
		//查询创建人
		if (StringUtils.isNotBlank(param.getCreateByName())) {
			List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<SysUser>()
					.like(SysUser.REAL_NAME, param.getCreateByName().trim()).eq(SysUser.DEL_FLAG, PatrolConstant.UN_DEL_FLAG));
			if (sysUsers!=null && sysUsers.size()>0){
				queryWrapper.in(Patrol.CREATE_BY,sysUsers.stream().map(SysUser::getId).collect(Collectors.toList()));
			}
		}

		Page<Patrol> page = new Page<>(pageNo, pageSize);
		IPage<Patrol> pageList = this.baseMapper.selectPage(page, queryWrapper);
		for (Patrol record : pageList.getRecords()) {
			SysUser sysUser = sysUserMapper.selectById(record.getCreateBy());
			if (sysUser!=null) {
				record.setCreateBy(sysUser.getRealname());
			}
		}


		return Result.ok(pageList);
	}
}
