package com.aiurt.boot.modules.patrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.PatrolPool;
import com.swsc.copsms.modules.patrol.entity.PatrolTask;
import com.swsc.copsms.modules.patrol.mapper.PatrolPoolMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolTaskMapper;
import com.swsc.copsms.modules.patrol.param.PoolAppointParam;
import com.swsc.copsms.modules.patrol.param.PoolPageParam;
import com.swsc.copsms.modules.patrol.service.IPatrolPoolService;
import com.swsc.copsms.modules.patrol.utils.NumberGenerateUtils;
import com.swsc.copsms.modules.patrol.vo.PatrolPoolVO;
import com.swsc.copsms.modules.system.entity.SysDepart;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysDepartMapper;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 巡检计划池
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolPoolServiceImpl extends ServiceImpl<PatrolPoolMapper, PatrolPool> implements IPatrolPoolService {

	private final NumberGenerateUtils numberGenerateUtils;

	private final PatrolTaskMapper patrolTaskMapper;

	private final SysUserMapper sysUserMapper;

	private final SysDepartMapper sysDepartMapper;

	@Override
	public Result<?> selectPage(PoolPageParam param, HttpServletRequest req) {

		IPage<PatrolPoolVO> page = this.baseMapper.selectPageList(new Page<PatrolPoolVO>(param.getPageNo(), param.getPageSize()), param);

		//设置人员名称及部门名称
		for (PatrolPoolVO record : page.getRecords()) {
			if (StringUtils.isNotBlank(record.getStaffIds())) {
				String[] userIds = record.getStaffIds().trim().split(PatrolConstant.SPL);
				List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<SysUser>()
						.eq(SysUser.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
						.in(SysUser.ID, userIds));
				if (sysUsers != null && sysUsers.size() > 0) {
					record.setStaffName(StringUtils.join(
							sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()),
							PatrolConstant.SPL));
				}
			}
			SysDepart sysDepart = sysDepartMapper.selectById(record.getOrganizationId());
			if (sysDepart != null ){
				record.setOrganizationName(sysDepart.getDepartName());
			}
		}

		return Result.ok(page);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> appoint(HttpServletRequest req, PoolAppointParam param) {

		// TODO: 2021/9/17 拼接线路等字段
		String before = "X";

		Long id = param.getId();

		//任务
		PatrolTask task = new PatrolTask();

		// TODO: 2021/9/17 获取线路等
		String codeNo = numberGenerateUtils.getCodeNo(before);

		task.setPatrolPoolId(id)
				.setDelFlag(0)
				.setStatus(0)
				.setCode(codeNo)
				.setCounts(0)
				.setStaffIds(StringUtils.join(param.getUserIds(), PatrolConstant.SPL))
				.setIgnoreStatus(0);



		//任务池
		PatrolPool pool = this.baseMapper.selectById(id);
		pool.setStatus(1);

		int update = this.baseMapper.updateById(pool);
		if (update < 1) {
			throw new SwscException("更新任务池错误");
		}

		int insert = patrolTaskMapper.insert(task);
		if (insert < 1) {
			throw new SwscException("分配任务失败");
		}
		return Result.ok();
	}

	@Override
	public Result<?> receive(HttpServletRequest req, Long id) {



		return null;
	}
}
