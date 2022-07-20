package com.aiurt.modules.sysFile.service.impl;

import com.aiurt.modules.sysFile.entity.SysFileRole;
import com.aiurt.modules.sysFile.entity.SysFileType;
import com.aiurt.modules.sysFile.mapper.SysFileTypeMapper;
import com.aiurt.modules.sysFile.param.SysFileRoleParam;
import com.aiurt.modules.sysFile.param.SysFileTypeParam;
import com.aiurt.modules.sysFile.service.ISysFileRoleService;
import com.aiurt.modules.sysFile.service.ISysFileTypeService;
import com.aiurt.modules.sysFile.vo.SimpUserVO;
import com.aiurt.modules.sysFile.vo.SysFileTypeDetailVO;
import com.aiurt.modules.sysFile.vo.SysFileTypeTreeVO;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @Description: 文档类型表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysFileTypeServiceImpl extends ServiceImpl<SysFileTypeMapper, SysFileType> implements ISysFileTypeService {

	private final ISysFileRoleService roleService;

//	private final ISysUserService userService;

	@Override
	public Result<List<SysFileTypeTreeVO>> tree(String userId) {
		log.info("userId:{}", userId);

		//权限
		List<Long> role = roleService.queryRoleByUserId(userId);
		if (role == null || role.size() == 0) {
			return Result.ok(new ArrayList<>());
		}
		return Result.ok(getTree(role, 0L));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> add(HttpServletRequest req, SysFileTypeParam param) {
		SysFileType type = new SysFileType();

		type.setGrade(param.getGrade()).setName(param.getName()).setDelFlag(0).setParentId(param.getParentId());
		if (!this.save(type)) {
			throw new AiurtBootException("添加分类未成功,请稍后重试");
		}

		List<String> editIds = param.getEditIds();
		List<String> lookIds = param.getLookIds();
		editIds.forEach(lookIds::remove);
		//允许上传权限,自动享有查看权限
		for (String editId : editIds) {
			roleService.addRole(new SysFileRoleParam().setLookStatus(1).setEditStatus(1).setTypeId(type.getId()).setUserId(editId));
		}
		for (String lookId : lookIds) {
			roleService.addRole(new SysFileRoleParam().setLookStatus(1).setEditStatus(0).setTypeId(type.getId()).setUserId(lookId));
		}


		return Result.ok();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> edit(HttpServletRequest req, SysFileTypeParam param) {
		SysFileType sysFileType = this.getById(param.getId());
		if (sysFileType == null) {
			return Result.error("未查询到此项数据");
		}
		//修改本身分类
		sysFileType.setParentId(param.getParentId()).setGrade(param.getGrade()).setName(param.getName().trim());
		this.updateById(sysFileType);

		List<String> editIds = param.getEditIds();
		List<String> lookIds = param.getLookIds();
		editIds.forEach(lookIds::remove);
		lookIds.addAll(editIds);
		roleService.delRole(lookIds, param.getId());
		SysFileRoleParam roleParam = new SysFileRoleParam();
		//给予编辑权限
		for (String editId : editIds) {
			roleService.updateRole(roleParam.setEditStatus(1).setLookStatus(1).setUserId(editId).setTypeId(sysFileType.getId()));
		}
		//给予查看权限
		for (String lookId : lookIds) {
			if (!editIds.contains(lookId)) {
				roleService.updateRole(roleParam.setEditStatus(0).setLookStatus(1).setUserId(lookId).setTypeId(sysFileType.getId()));
			}
		}

		return Result.ok();
	}

	@Override
	public Result<SysFileTypeDetailVO> detail(HttpServletRequest req, Long id) {
		SysFileType sysFileType = this.getById(id);
		if (sysFileType==null){
			return Result.error("未查询到此条记录");
		}

		//返回对象
		SysFileTypeDetailVO vo = new SysFileTypeDetailVO();
		Optional.ofNullable(sysFileType).ifPresent(fileType -> {
			BeanUtils.copyProperties(fileType, vo);
			List<SysFileRole> list = roleService.lambdaQuery()
					.eq(SysFileRole::getDelFlag, 0)
					.eq(SysFileRole::getTypeId, fileType.getId()).list();
			if (list != null && list.size() > 0) {
				//区分查看与编辑
				Map<Integer, List<SysFileRole>> listMap = list.stream().collect(Collectors.groupingBy(SysFileRole::getEditStatus));
				if (listMap != null && listMap.size() > 0) {
					//获取编辑列表中数据
					if (CollectionUtils.isNotEmpty(listMap.get(1))) {
						Optional.ofNullable(listMap.get(1)).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
                            // todo 后期修改
							Collection<LoginUser> sysUsers = new ArrayList<>();
//							Collection<LoginUser> sysUsers = userService.listByIds(ids);
							if (sysUsers != null && sysUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : sysUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setEditUsers(userList);
							}
						});
					}
					//获取查看列表中数据
					Optional.ofNullable(listMap.get(0)).ifPresent(roles -> {
						List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
						// todo 后期修改
						Collection<LoginUser> sysUsers = new ArrayList<>();
//						Collection<LoginUser> sysUsers = userService.listByIds(ids);
						if (sysUsers != null && sysUsers.size() > 0) {
							Set<SimpUserVO> userList = new HashSet<>();
							for (LoginUser sysUser : sysUsers) {
								userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
							}
							Optional.ofNullable(vo.getEditUsers()).ifPresent(userList::addAll);
							vo.setLookUsers(userList);
						}
					});
					if (vo.getLookUsers()==null && vo.getEditUsers()!=null){
						vo.setLookUsers(vo.getEditUsers());
					}
				}
			}
		});
		return Result.ok(vo);
	}

	/**
	 * 获取树型结构
	 *
	 * @param role     角色
	 * @param parentId 父id
	 * @return {@link List}<{@link SysFileTypeTreeVO}>
	 */
	private List<SysFileTypeTreeVO> getTree(List<Long> role, Long parentId) {
		List<SysFileTypeTreeVO> list = new ArrayList<>();
		List<SysFileType> types = this.lambdaQuery()
				.eq(SysFileType::getParentId, parentId)
				.in(SysFileType::getId, role).list();
		if (types != null && types.size() > 0) {
			types.forEach(type -> {
				Optional.ofNullable(type).ifPresent(t -> {
					SysFileTypeTreeVO vo = new SysFileTypeTreeVO();
					BeanUtils.copyProperties(t, vo);
					vo.setChildren(getTree(role, vo.getId()));
					list.add(vo);
				});
			});
		}

		return list;
	}
}
