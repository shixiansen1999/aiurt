package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.sysfile.entity.SysFileRole;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.mapper.SysFileTypeMapper;
import com.aiurt.modules.sysfile.param.SysFileRoleParam;
import com.aiurt.modules.sysfile.param.SysFileTypeParam;
import com.aiurt.modules.sysfile.service.ISysFileRoleService;
import com.aiurt.modules.sysfile.service.ISysFileTypeService;
import com.aiurt.modules.sysfile.vo.SimpUserVO;
import com.aiurt.modules.sysfile.vo.SysFileTypeDetailVO;
import com.aiurt.modules.sysfile.vo.SysFileTypeTreeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private SysFileTypeMapper sysFileTypeMapper;

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
		// 判断是否重同一级名
		boolean exists = sysFileTypeMapper.exists(new LambdaQueryWrapper<SysFileType>().eq(SysFileType::getGrade, param.getGrade()).eq(SysFileType::getName, param.getName()).eq(SysFileType::getParentId, param.getParentId()));
		if(exists) {
			throw new AiurtBootException("添加分类未成功,同级已添加该分类名称");
		}
		type.setGrade(param.getGrade()).setName(param.getName()).setDelFlag(0).setParentId(param.getParentId());
		if (!this.save(type)) {
			throw new AiurtBootException("添加分类未成功,请稍后重试");
		}

		this.getList(param,type);
		return Result.ok();
	}

	private Result<?> getList(SysFileTypeParam param,SysFileType type){
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String id = loginUser.getId();
		//编辑
		List<String> editIds = param.getEditIds();
		if (!editIds.contains(id)) {

		}
		//查看
		List<String> lookIds = param.getLookIds();
		//上传
		List<String> uploads = param.getUploads();
		//下载
		List<String> downloads = param.getDownloads();
		//删除状态
		List<String> deletes = param.getDeletes();
		//在线编辑状态
		List<String> onlineEditing = param.getOnlineEditing();

		Set<String> stringSet = new HashSet<>();

		// 文档分类的创建者拥有所有的权限
		//允许上传的权限
		if (CollUtil.isNotEmpty(uploads)){
		for (String uploadId : uploads) {
			roleService.addRole(new SysFileRoleParam().setLookStatus(1).setPrimaryLookStatus(0).setEditStatus(1).setPrimaryEditStatus(0).setDeleteStatus(1).setPrimaryDeleteStatus(0)
					.setDownloadStatus(1).setPrimaryDownloadStatus(0).setRenameStatus(1).setPrimaryRenameStatus(0).setOnlineEditing(1).setPrimaryOnlineEditing(0).setUploadStatus(1).setPrimaryUploadStatus(1).setTypeId(type.getId()).setUserId(uploadId));
			stringSet.add(uploadId);
		  }
		}
		//允许编辑权限
		if (CollUtil.isNotEmpty(editIds)){
		for (String editId : editIds) {
			if (!stringSet.contains(editId)){
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setEditStatus(1).setDeleteStatus(1).setDownloadStatus(1).setOnlineEditing(1).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(1).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setTypeId(type.getId()).setUserId(editId));
				stringSet.add(editId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,editId).eq(SysFileRole::getTypeId,type.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setEditStatus(1);
				sysFileRole.setPrimaryEditStatus(1);
				roleService.updateById(sysFileRole);
			}
		  }
		}
		//允许删除的权限
		if (CollUtil.isNotEmpty(deletes)){
		for (String deleteId : deletes) {
			if (!stringSet.contains(deleteId)) {
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setEditStatus(0).setDeleteStatus(1).setDownloadStatus(1).setOnlineEditing(1).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(1).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setTypeId(type.getId()).setUserId(deleteId));
				stringSet.add(deleteId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,deleteId).eq(SysFileRole::getTypeId,type.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setPrimaryDeleteStatus(1);
				roleService.updateById(sysFileRole);
			}
		  }
		}
		//允许在线编辑的权限
		if (CollUtil.isNotEmpty(onlineEditing)){
		for (String onlineEditingId : onlineEditing) {
			if (!stringSet.contains(onlineEditingId)){
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setDownloadStatus(1).setEditStatus(0).setDeleteStatus(0).setOnlineEditing(1).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(1).setPrimaryUploadStatus(0).setTypeId(type.getId()).setUserId(onlineEditingId));
				stringSet.add(onlineEditingId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,onlineEditingId).eq(SysFileRole::getTypeId,type.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setPrimaryOnlineEditing(1);
				roleService.updateById(sysFileRole);
			}
		  }
		}
		//允许下载的权限
		if (CollUtil.isNotEmpty(downloads)) {
			for (String downloadId : downloads) {
				if (!stringSet.contains(downloadId)) {
					roleService.addRole(new SysFileRoleParam().setLookStatus(1).setDownloadStatus(1).setEditStatus(0).setDeleteStatus(0).setOnlineEditing(0).setUploadStatus(0)
							.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(1).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setTypeId(type.getId()).setUserId(downloadId));
					stringSet.add(downloadId);
				}else {
					LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
					queryWrapper.eq(SysFileRole::getUserId,downloadId).eq(SysFileRole::getTypeId,type.getId()).eq(SysFileRole::getDelFlag,0);
					SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
					sysFileRole.setPrimaryDownloadStatus(1);
					roleService.updateById(sysFileRole);
				}
			}
		}
		//仅仅允许查看的权限
		if (CollUtil.isNotEmpty(lookIds)) {
		for (String lookId : lookIds) {
			if (!stringSet.contains(lookId)) {
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setEditStatus(0).setDownloadStatus(0).setDeleteStatus(0).setUploadStatus(0).setOnlineEditing(0)
						.setPrimaryLookStatus(1).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setTypeId(type.getId()).setUserId(lookId));
				stringSet.add(lookId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,lookId).eq(SysFileRole::getTypeId,type.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setPrimaryLookStatus(1);
				roleService.updateById(sysFileRole);
			}
		 }
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

		LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysFileRole::getTypeId,param.getId());
		List<SysFileRole> sysFileRoles = roleService.getBaseMapper().selectList(queryWrapper);
		roleService.removeByIds(sysFileRoles);

		this.getList(param,sysFileType);
		return Result.ok();
	}

	public Result<SysFileTypeDetailVO> detail(Long id){
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


				Map<Integer, List<SysFileRole>> listMap8 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getEditStatus())).collect(Collectors.groupingBy(SysFileRole::getEditStatus));
				if (listMap8 != null && listMap8.size() > 0) {
					//获取原可编辑列表中数据
					if (CollectionUtils.isNotEmpty(listMap8.get(1))) {
						Optional.ofNullable(listMap8.get(1)).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setEditUsers(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap9 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getUploadStatus())).collect(Collectors.groupingBy(SysFileRole::getUploadStatus));
				if (listMap9 != null && listMap9.size() > 0) {
					//获取原可上传列表中数据
					if (CollectionUtils.isNotEmpty(listMap9.get(1))) {
						Optional.ofNullable(listMap9.get(1)).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setUploadStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap11 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getDeleteStatus())).collect(Collectors.groupingBy(SysFileRole::getDeleteStatus));
				if (listMap11 != null && listMap11.size() > 0) {
					//获取原可删除列表中数据
					if (CollectionUtils.isNotEmpty(listMap11.get(1))) {
						Optional.ofNullable(listMap11.get(1)).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setDeleteStatus(userList);
							}
						});
					}
				}

	         }
       });
		return Result.ok(vo);
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

			//查询当前类型是否存在父级
			List<SysFileRole> sysFileRoleList = roleService.lambdaQuery()
					.eq(SysFileRole::getDelFlag,0)
					.eq(SysFileRole::getTypeId,fileType.getParentId()).list();

			if (list != null && list.size() > 0) {
				Map<Integer, List<SysFileRole>> listMap = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getEditStatus())).collect(Collectors.groupingBy(SysFileRole::getEditStatus));
				if (listMap != null && listMap.size() > 0) {
					//获取编辑列表中数据


					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getEditStatus())).collect(Collectors.groupingBy(SysFileRole::getEditStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setEditUsers(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap6 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getLookStatus())).collect(Collectors.groupingBy(SysFileRole::getLookStatus));
				if (listMap6 != null && listMap6.size() > 0) {
					//获取查看列表中数据


					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getLookStatus())).collect(Collectors.groupingBy(SysFileRole::getLookStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap6.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap6.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
					List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
					String[] array = new String[ids.size()];
					for(int i = 0; i < ids.size();i++){
						array[i] = ids.get(i);
					}
					List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
					if (loginUsers != null && loginUsers.size() > 0) {
						Set<SimpUserVO> userList = new HashSet<>();
						for (LoginUser sysUser : loginUsers) {
							userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
						}
						Optional.ofNullable(vo.getEditUsers()).ifPresent(userList::addAll);
						vo.setLookUsers(userList);
					}
				});
					}
				}
				Map<Integer, List<SysFileRole>> listMap1 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getUploadStatus())).collect(Collectors.groupingBy(SysFileRole::getUploadStatus));
				if (listMap1 != null && listMap1.size() > 0) {
					//获取上传列表中数据

					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getUploadStatus())).collect(Collectors.groupingBy(SysFileRole::getUploadStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap1.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap1.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setUploadStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap2 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getDownloadStatus())).collect(Collectors.groupingBy(SysFileRole::getDownloadStatus));
				if (listMap2 != null && listMap2.size() > 0) {
					//获取下载列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getDownloadStatus())).collect(Collectors.groupingBy(SysFileRole::getDownloadStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap2.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap2.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setDownloadStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap3 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getDeleteStatus())).collect(Collectors.groupingBy(SysFileRole::getDeleteStatus));
				if (listMap3 != null && listMap3.size() > 0) {
					//获取删除列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getDeleteStatus())).collect(Collectors.groupingBy(SysFileRole::getDeleteStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap3.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap3.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setDeleteStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap4 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getOnlineEditing())).collect(Collectors.groupingBy(SysFileRole::getOnlineEditing));
				if (listMap4 != null && listMap4.size() > 0) {
					//获取在线编辑列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getOnlineEditing())).collect(Collectors.groupingBy(SysFileRole::getOnlineEditing));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap4.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap4.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setOnlineEditing(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap5 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getRenameStatus())).collect(Collectors.groupingBy(SysFileRole::getRenameStatus));
				if (listMap5 != null && listMap5.size() > 0) {
					//获取重命名列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getRenameStatus())).collect(Collectors.groupingBy(SysFileRole::getRenameStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap5.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap5.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setRenameStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap7 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryLookStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryLookStatus));
				if (listMap7 != null && listMap7.size() > 0) {
					//获取原可查看列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryLookStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryLookStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap7.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap7.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryLookStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap8 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryEditStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryEditStatus));
				if (listMap8 != null && listMap8.size() > 0) {
					//获取原可编辑列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryEditStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryEditStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap8.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap8.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryEditStatus(userList);
							}
						});
					}
				}
				Map<Integer, List<SysFileRole>> listMap9 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryUploadStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryUploadStatus));
				if (listMap9 != null && listMap9.size() > 0) {
					//获取原可上传列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryUploadStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryUploadStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap9.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap9.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryUploadStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap10 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryDownloadStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryDownloadStatus));
				if (listMap10 != null && listMap10.size() > 0) {
					//获取原可下载列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryDownloadStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryDownloadStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap10.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap10.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryDownloadStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap11 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryDeleteStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryDeleteStatus));
				if (listMap11 != null && listMap11.size() > 0) {
					//获取原可删除列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryDeleteStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryDeleteStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap11.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap11.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryDeleteStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap12 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryRenameStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryRenameStatus));
				if (listMap12 != null && listMap12.size() > 0) {
					//获取原可删除列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryRenameStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryRenameStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap12.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap12.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryRenameStatus(userList);
							}
						});
					}
				}

				Map<Integer, List<SysFileRole>> listMap13 = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryOnlineEditing())).collect(Collectors.groupingBy(SysFileRole::getPrimaryOnlineEditing));
				if (listMap13 != null && listMap13.size() > 0) {
					//获取原可删除列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryOnlineEditing())).collect(Collectors.groupingBy(SysFileRole::getPrimaryOnlineEditing));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (CollectionUtils.isNotEmpty(listMap13.get(1))) {
						List<SysFileRole> sysFileRole = getSysFileRole(sysFileRoleList1, listMap13.get(1));
						Optional.ofNullable(sysFileRole).ifPresent(roles -> {
							List<String> ids = roles.stream().map(SysFileRole::getUserId).collect(Collectors.toList());
							String[] array = new String[ids.size()];
							for(int i = 0; i < ids.size();i++){
								array[i] = ids.get(i);
							}
							List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(array);
							if (loginUsers != null && loginUsers.size() > 0) {
								Set<SimpUserVO> userList = new HashSet<>();
								for (LoginUser sysUser : loginUsers) {
									userList.add(new SimpUserVO().setUserId(sysUser.getId()).setUserName(sysUser.getRealname()));
								}
								vo.setPrimaryOnlineEditing(userList);
							}
						});
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
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<SysFileTypeTreeVO> list = new ArrayList<>();
		// 查询父级, 创建人拥有所有的权限
		List<SysFileType> types = this.lambdaQuery()
				.eq(SysFileType::getParentId, parentId)
				.in(SysFileType::getId, role).or(q->q.eq(SysFileType::getCreateBy, loginUser.getUsername())
						.eq(SysFileType::getParentId, parentId)).list();
		if (types != null && types.size() > 0) {
			types.forEach(type -> {
				Optional.ofNullable(type).ifPresent(t -> {
					SysFileTypeTreeVO vo = new SysFileTypeTreeVO();
					BeanUtils.copyProperties(t, vo);
					Result<SysFileTypeDetailVO> detail = this.detail(t.getId());
					vo.setEditUsers(ObjectUtil.isNotEmpty(detail.getResult().getEditUsers()) ? detail.getResult().getEditUsers() : null);
					vo.setUploadStatus(ObjectUtil.isNotEmpty(detail.getResult().getUploadStatus()) ? detail.getResult().getUploadStatus() : null);
					vo.setDeleteStatus(ObjectUtil.isNotEmpty(detail.getResult().getDeleteStatus()) ? detail.getResult().getDeleteStatus() : null);
					// 权限处理， 判断是否有权限编辑，删除，
					vo.setChildren(getTree(role, vo.getId()));
					list.add(vo);
				});
			});
		}

		return list;
	}

	private List<SysFileRole> getSysFileRole(List<SysFileRole> list1,List<SysFileRole> list2) {
		ArrayList<SysFileRole> sysFileRoles = new ArrayList<>();
		if (CollUtil.isNotEmpty(list1)) {
			Map<String, SysFileRole> roleMap = list1.stream().collect(Collectors.toMap(SysFileRole::getUserId, t -> t, (key1, key2) -> key1));
			for (SysFileRole sysFileRole : list2) {
				SysFileRole fileRole = roleMap.get(sysFileRole.getUserId());
				if (ObjectUtil.isNotEmpty(fileRole)) {
					sysFileRoles.add(fileRole);
				}
			}
		} else {
			sysFileRoles.addAll(list2);
		}

		return sysFileRoles;
	}
}
