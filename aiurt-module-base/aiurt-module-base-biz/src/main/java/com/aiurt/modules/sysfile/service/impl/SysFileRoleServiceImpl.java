package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.sysfile.entity.SysFileRole;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.mapper.SysFileRoleMapper;
import com.aiurt.modules.sysfile.mapper.SysFileTypeMapper;
import com.aiurt.modules.sysfile.param.SysFileRoleParam;
import com.aiurt.modules.sysfile.service.ISysFileRoleService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 文档权限表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysFileRoleServiceImpl extends ServiceImpl<SysFileRoleMapper, SysFileRole> implements ISysFileRoleService {

	private final SysFileTypeMapper sysFileTypeMapper;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean addRole(SysFileRoleParam param) {
		if (param == null || param.getTypeId() == null || StringUtils.isBlank(param.getUserId())) {
			return false;
		}
		if (param.getLookStatus() == null && param.getEditStatus() == null) {
			return false;
		}
		//此目录权限设置
		SysFileRole fileRole = new SysFileRole();
		fileRole.setDelFlag(0).setTypeId(param.getTypeId()).setUserId(param.getUserId());
		fileRole.setLookStatus(1);
		fileRole.setEditStatus(Optional.ofNullable(param.getEditStatus()).orElse(0));
		fileRole.setUploadStatus(Optional.ofNullable(param.getUploadStatus()).orElse(0));
		fileRole.setDownloadStatus(Optional.ofNullable(param.getDownloadStatus()).orElse(0));
		fileRole.setDeleteStatus(Optional.ofNullable(param.getDeleteStatus()).orElse(0));
		fileRole.setOnlineEditing(Optional.ofNullable(param.getOnlineEditing()).orElse(0));
		fileRole.setRenameStatus(Optional.ofNullable(param.getRenameStatus()).orElse(0));
		fileRole.setPrimaryLookStatus(Optional.ofNullable(param.getPrimaryLookStatus()).orElse(0));
		fileRole.setPrimaryEditStatus(Optional.ofNullable(param.getPrimaryEditStatus()).orElse(0));
		fileRole.setPrimaryDeleteStatus(Optional.ofNullable(param.getPrimaryDeleteStatus()).orElse(0));
		fileRole.setPrimaryDownloadStatus(Optional.ofNullable(param.getPrimaryDownloadStatus()).orElse(0));
		fileRole.setPrimaryRenameStatus(Optional.ofNullable(param.getPrimaryRenameStatus()).orElse(0));
		fileRole.setPrimaryOnlineEditing(Optional.ofNullable(param.getPrimaryOnlineEditing()).orElse(0));
		fileRole.setPrimaryUploadStatus(Optional.ofNullable(param.getPrimaryUploadStatus()).orElse(0));
		if (!this.save(fileRole)) {
			throw new AiurtBootException("权限设置未成功,请稍后重试");
		}
//		//添加权限
//		this.addRole(fileRole.getTypeId(), param.getUserId());

		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean addRole1(SysFileRoleParam param) {
		if (param == null || param.getFileId() == null || StringUtils.isBlank(param.getUserId())) {
			return false;
		}
		if (param.getLookStatus() == null && param.getEditStatus() == null) {
			return false;
		}
		//此目录权限设置
		SysFileRole fileRole = new SysFileRole();
		fileRole.setDelFlag(0).setFileId(param.getFileId()).setUserId(param.getUserId());
		fileRole.setLookStatus(1);
		fileRole.setEditStatus(Optional.ofNullable(param.getEditStatus()).orElse(0));
		fileRole.setUploadStatus(Optional.ofNullable(param.getUploadStatus()).orElse(0));
		fileRole.setDownloadStatus(Optional.ofNullable(param.getDownloadStatus()).orElse(0));
		fileRole.setDeleteStatus(Optional.ofNullable(param.getDeleteStatus()).orElse(0));
		fileRole.setOnlineEditing(Optional.ofNullable(param.getOnlineEditing()).orElse(0));
		fileRole.setRenameStatus(Optional.ofNullable(param.getRenameStatus()).orElse(0));
		fileRole.setPrimaryLookStatus(Optional.ofNullable(param.getPrimaryLookStatus()).orElse(0));
		fileRole.setPrimaryEditStatus(Optional.ofNullable(param.getPrimaryEditStatus()).orElse(0));
		fileRole.setPrimaryDeleteStatus(Optional.ofNullable(param.getPrimaryDeleteStatus()).orElse(0));
		fileRole.setPrimaryDownloadStatus(Optional.ofNullable(param.getPrimaryDownloadStatus()).orElse(0));
		fileRole.setPrimaryRenameStatus(Optional.ofNullable(param.getPrimaryRenameStatus()).orElse(0));
		fileRole.setPrimaryOnlineEditing(Optional.ofNullable(param.getPrimaryOnlineEditing()).orElse(0));
		fileRole.setPrimaryUploadStatus(Optional.ofNullable(param.getPrimaryUploadStatus()).orElse(0));
		if (!this.save(fileRole)) {
			throw new AiurtBootException("权限设置未成功,请稍后重试");
		}
//		//添加权限
//		this.addRole(fileRole.getTypeId(), param.getUserId());

		return true;
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateRole(SysFileRoleParam param) {
		if (param == null || param.getTypeId() == null || StringUtils.isBlank(param.getUserId())) {
			return false;
		}
		if (param.getLookStatus() == null && param.getEditStatus() == null) {
			return false;
		}

		return this.addRole(param);
	}


	/**
	 * 添加权限
	 *
	 * @param type   类型
	 * @param userId 用户id
	 * @return boolean
	 */
	private boolean addRole(Long type, String userId) {
		//上级文件目录
		List<Long> list = this.superList(type, userId);
		if (list.size() > 0) {
			this.lambdaUpdate().eq(SysFileRole::getDelFlag, 0)
					.in(SysFileRole::getId, list)
					.update(new SysFileRole().setLookStatus(1));
			List<SysFileRole> roleList = this.lambdaQuery().in(SysFileRole::getId, list).eq(SysFileRole::getDelFlag, 0).list();
			if (roleList != null && roleList.size() > 0) {
				//验证上级查看权限是否给予
				if (roleList.size() != list.size()) {
					roleList.forEach(r -> {
						list.remove(r.getId());
					});
					SysFileRole role = new SysFileRole();
					role.setDelFlag(0).setEditStatus(0).setLookStatus(1).setDownloadStatus(0).setDeleteStatus(0).setUploadStatus(0).setOnlineEditing(0).setUserId(userId);
					for (Long typeId : list) {
						role.setId(null).setTypeId(typeId);
						if (!this.save(role)) {
							throw new AiurtBootException("权限设置未成功,请稍后重试");
						}
					}
				}
			} else {
				SysFileRole role = new SysFileRole();
				role.setDelFlag(0).setEditStatus(0).setLookStatus(1).setDownloadStatus(0).setDeleteStatus(0).setUploadStatus(0).setOnlineEditing(0).setUserId(userId);
				for (Long typeId : list) {
					role.setId(null).setTypeId(typeId);
					if (!this.save(role)) {
						throw new AiurtBootException("权限设置未成功,请稍后重试");
					}
				}
			}

		}
		return true;
	}

	@Override
	public boolean delRole(List<String> ids, Long typeId) {
		this.remove(new LambdaQueryWrapper<SysFileRole>().eq(SysFileRole::getTypeId,typeId));
		return true;
	}

	@Override
	public List<Long> queryRoleByUserId(String userId) {

		List<SysFileRole> roleList = this.lambdaQuery()
				.eq(SysFileRole::getDelFlag, 0).eq(SysFileRole::getUserId, userId).list();

		return (roleList != null && roleList.size() > 0) ? roleList.stream().map(SysFileRole::getTypeId).collect(Collectors.toList()) : null;
	}

	@Override
	public List<Long> queryRoleByUserId(String userId, Long typeId) {

		List<Long> list = this.lowerList(typeId, userId);
		list.add(typeId);

		return list;
	}


	/**
	 * 上级id列表
	 *
	 * @param typeId id类型
	 * @param userId 用户id
	 * @return {@link List}<{@link Long}>
	 */
	private List<Long> superList(Long typeId, String userId) {

		List<Long> list = new ArrayList<>();
		if (typeId == 0) {
			return list;
		}
		Long id = typeId;
		while (true) {
			//查询父级id
			id = sysFileTypeMapper.selectParentId(id);
			SysFileRole one = this.lambdaQuery()
					.eq(SysFileRole::getDelFlag, 0)
					.eq(SysFileRole::getTypeId, id)
					.eq(SysFileRole::getUserId, userId).last("limit 1").one();
			if (one != null) {
				list.add(one.getId());
			} else {
				one = new SysFileRole();
				one.setUserId(userId).setLookStatus(1).setEditStatus(0).setDelFlag(0).setTypeId(id);
				if (!this.save(one)) {
					throw new AiurtBootException("权限设置未成功,请稍后重试");
				}
				list.add(one.getId());
			}
			if (id == 0) {
				break;
			}
		}
		return list;
	}


	/**
	 * 下级id列表
	 *
	 * @param typeId id类型
	 * @param userId 用户id
	 * @return {@link List}<{@link Long}>
	 */
	private List<Long> lowerList(Long typeId, String userId) {
		if (Objects.isNull(typeId)) {
			typeId = 0L;
		}
		List<Long> typeIdList = sysFileTypeMapper.selectChildId(typeId);
		typeIdList.add(typeId);


		List<SysFileRole> fileRoles = this.lambdaQuery()
				.eq(SysFileRole::getDelFlag, 0)
				.like(SysFileRole::getUserId, userId)
				.in(SysFileRole::getTypeId, typeIdList).list();
		if (CollectionUtil.isNotEmpty(fileRoles)) {
			List<Long> longs = fileRoles.stream().map(SysFileRole::getTypeId).collect(Collectors.toList());
			return longs;
		}
		return Collections.emptyList();
	}



}
