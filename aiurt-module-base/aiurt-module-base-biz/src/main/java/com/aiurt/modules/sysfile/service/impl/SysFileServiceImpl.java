package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileRole;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.mapper.SysFileMapper;
import com.aiurt.modules.sysfile.mapper.SysFileRoleMapper;
import com.aiurt.modules.sysfile.mapper.SysFileTypeMapper;
import com.aiurt.modules.sysfile.param.FileAppParam;
import com.aiurt.modules.sysfile.param.SysFileRoleParam;
import com.aiurt.modules.sysfile.param.SysFileTypeParam;
import com.aiurt.modules.sysfile.service.ISysFileRoleService;
import com.aiurt.modules.sysfile.service.ISysFileService;
import com.aiurt.modules.sysfile.vo.FIlePlanVO;
import com.aiurt.modules.sysfile.vo.FileAppVO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sysfile.vo.SimpUserVO;
import com.aiurt.modules.sysfile.vo.SysFileTypeDetailVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 文档表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

	private final SysFileTypeMapper sysFileTypeMapper;
	@Autowired
	private SysFileRoleMapper sysFileRoleMapper;

	@Autowired
	private ISysBaseAPI iSysBaseAPI;

	private final ISysFileRoleService roleService;

	@Override
	public IPage<FileAppVO> selectAppList(FileAppParam param) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<SysFileRole> roleList = sysFileRoleMapper.selectList(new LambdaQueryWrapper<SysFileRole>()
				.eq(SysFileRole::getDelFlag, 0).eq(SysFileRole::getUserId, loginUser.getId()));
        List<Long> role =roleList.stream().map(s-> s.getTypeId()).collect(Collectors.toList());

		if (param.getPageNo() == null) {
			param.setPageNo(1);
		}
		if (param.getPageSize() == null) {
			param.setPageSize(10);
		}
		IPage<FileAppVO> page = new Page<>();
		if (role == null || role.size() == 0) {
			return page.setRecords(new ArrayList<>());
		}
		List<FileAppVO> list = new ArrayList<>();
		int len = param.getPageNo() * param.getPageSize();
		LambdaQueryWrapper<SysFileType> typeQueryWrapper = new LambdaQueryWrapper<SysFileType>()
				.eq(SysFileType::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(SysFileType::getId, SysFileType::getName)
				.in(SysFileType::getId, role);
		if (param.getTypeId() == null) {
			typeQueryWrapper.eq(SysFileType::getParentId, 0);
		} else {
			typeQueryWrapper.eq(SysFileType::getParentId, param.getTypeId());
		}
		IPage<SysFileType> typePage = sysFileTypeMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()), typeQueryWrapper);
		long total = typePage.getTotal();
		BeanUtils.copyProperties(typePage, page);

		Optional.ofNullable(typePage.getRecords()).ifPresent(sysFileTypes -> {
			sysFileTypes.forEach(t -> {
				FileAppVO appVO = new FileAppVO();
				appVO.setTypeName(t.getName()).setTypeId(t.getId()).setStatus(0).setParentId(t.getParentId());
				list.add(appVO);
			});
		});
		if (total > len) {
			page.setRecords(list).setTotal(param.getTypeId() == null ? total :
					total + this.lambdaQuery().eq(SysFile::getDelFlag, 0)
							.eq(SysFile::getTypeId, param.getTypeId()).count());
			return page;
		}

		if (ObjectUtil.isNotNull(param.getTypeId())){
			long l = len - total;
			long l1 = l % param.getPageSize() > 0 ? (l / param.getPageSize()) + 1 : l / param.getPageSize();
			long size = l > 10 ? 10 : l;
			long no = l1 - 1;
			IPage<SysFile> filePage = this.baseMapper.selectFilePage(new Page(no, size), param.getTypeId());
			Optional.ofNullable(filePage.getRecords()).ifPresent(sysFiles -> {
				sysFiles.forEach(f -> {
					FileAppVO appVO = new FileAppVO();
					appVO.setFileName(f.getName()).setId(f.getId()).setUrl(f.getUrl()).setStatus(1).setTypeId(f.getTypeId()).setDownStatus(f.getDownStatus());
					list.add(appVO);
				});
			});
			page.setRecords(list).setTotal(total + filePage.getTotal());
			return page;
		}else {
			//
			page.setRecords(list);
		}
		return page;
	}

	private final ISysFileRoleService sysFileRoleService;

	@Override
	public List<FIlePlanVO> selectList() {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();

		//String userId = "7d04b0436b574ca3b97c8f9f105bc9f0";
		List<FIlePlanVO> list = new ArrayList<>();
		//为前端准备负数id
		Long unTypeId = -1L;

		List<SysFileRole> fileRoles = sysFileRoleService.lambdaQuery()
				.eq(SysFileRole::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(SysFileRole::getUserId, userId)
				.select(SysFileRole::getTypeId).list();
		if (CollectionUtils.isEmpty(fileRoles)) {
			return list;
		}
		List<Long> roleTypeIds = fileRoles.stream().map(SysFileRole::getTypeId).collect(Collectors.toList());

		LambdaQueryWrapper<SysFileType> typeQueryWrapper = new LambdaQueryWrapper<SysFileType>()
				.eq(SysFileType::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(SysFileType::getParentId, 0)
				.in(SysFileType::getId, roleTypeIds)
				.select(SysFileType::getId, SysFileType::getName);

		List<SysFileType> typeList = this.sysFileTypeMapper.selectList(typeQueryWrapper);

		for (SysFileType type : typeList) {
			FIlePlanVO vo = new FIlePlanVO();
			vo.setTypeName(type.getName())
					.setTypeId(type.getId())
					.setParentId(type.getParentId())
					.setStatus(0)
					.setId(unTypeId--)
					.setChildren(selectParent(type.getId(), userId, roleTypeIds, unTypeId--))
			;
			list.add(vo);
		}

		//文件
		List<SysFile> sysFiles = this.lambdaQuery()
				.eq(SysFile::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(SysFile::getTypeId, 0)
				.and(query -> {
					query.eq(SysFile::getDownStatus, CommonConstant.STATUS_ENABLE).or().eq(SysFile::getCreateBy, userId);
				}).list();
		for (SysFile sysFile : sysFiles) {
			FIlePlanVO vo = new FIlePlanVO();
			vo.setTypeName(sysFile.getName())
					.setUrl(sysFile.getUrl())
					.setStatus(1)
					.setId(sysFile.getId())
					.setTypeId(sysFile.getTypeId())
					.setType(sysFile.getType());
			list.add(vo);

		}

		return list;
	}

	@Override
	public Result<?> add(HttpServletRequest req, SysFile param) {
		this.getList(param);
		return Result.ok();
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

	@Override
	public Result<SysFileTypeDetailVO> detail(HttpServletRequest req, Long id) {
		SysFile sysFile = this.getById(id);
		if (sysFile==null){
			return Result.error("未查询到此条记录");
		}

		//返回对象
		SysFileTypeDetailVO vo = new SysFileTypeDetailVO();
		Optional.ofNullable(sysFile).ifPresent(fileType -> {
			BeanUtils.copyProperties(fileType, vo);
			List<SysFileRole> list = roleService.lambdaQuery()
					.eq(SysFileRole::getDelFlag, 0)
					.eq(SysFileRole::getFileId, fileType.getId()).list();

			//查询当前文件的类型
			List<SysFileRole> sysFileRoleList = roleService.lambdaQuery()
					.eq(SysFileRole::getDelFlag,0)
					.eq(SysFileRole::getTypeId,fileType.getTypeId()).list();

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

					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap6.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap1.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap2.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap3.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap4.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap5.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap7.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap8.get(1))) {
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
					//获取原可下载列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryUploadStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryUploadStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap9.get(1))) {
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
					//获取原可上传列表中数据
					List<SysFileRole> sysFileRoleList1 = new ArrayList<>();
					if (sysFileRoleList != null && sysFileRoleList.size() > 0){
						Map<Integer, List<SysFileRole>> integerListMap = sysFileRoleList.stream()
								.filter(item-> ObjectUtil.isNotEmpty(item.getPrimaryDownloadStatus())).collect(Collectors.groupingBy(SysFileRole::getPrimaryDownloadStatus));
						if (integerListMap != null && integerListMap.size() > 0){
							sysFileRoleList1 = integerListMap.get(1);
						}
					}
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap10.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap11.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap12.get(1))) {
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
					if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listMap13.get(1))) {
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


	private Result<?> getList(SysFile param){
		//编辑
		List<String> editIds = param.getEditIds();
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


		//允许上传的权限，自动享有查看权限
		if(CollUtil.isNotEmpty(uploads)){
		for (String uploadId : uploads) {
			roleService.addRole1(new SysFileRoleParam().setLookStatus(1).setEditStatus(0).setDeleteStatus(1)
					.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(1).setDownloadStatus(1).setRenameStatus(1).setOnlineEditing(1).setUploadStatus(1).setFileId(param.getId()).setUserId(uploadId));
			stringSet.add(uploadId);
		  }
		}
		//允许编辑权限
		if(CollUtil.isNotEmpty(editIds)){
		for (String editId : editIds) {
			if (!stringSet.contains(editId)){
				roleService.addRole1(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setEditStatus(1).setDeleteStatus(1).setDownloadStatus(1).setOnlineEditing(1).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(1).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setFileId(param.getId()).setUserId(editId));
				stringSet.add(editId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,editId).eq(SysFileRole::getFileId,param.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setEditStatus(1);
				sysFileRole.setPrimaryEditStatus(1);
				roleService.updateById(sysFileRole);
			}
		  }
		}
		//允许删除的权限，自动享有查看权限
		if(CollUtil.isNotEmpty(deletes)){
		for (String deleteId : deletes) {
			if (!stringSet.contains(deleteId)) {
				roleService.addRole1(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setEditStatus(0).setDeleteStatus(1).setDownloadStatus(1).setOnlineEditing(1).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(1).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setFileId(param.getId()).setUserId(deleteId));
				stringSet.add(deleteId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,deleteId).eq(SysFileRole::getFileId,param.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setPrimaryDeleteStatus(1);
				roleService.updateById(sysFileRole);
			}
		 }
		}
		//允许在线编辑的权限，自动享有查看权限
		if(CollUtil.isNotEmpty(onlineEditing)){
		for (String onlineEditingId : onlineEditing) {
			if (!stringSet.contains(onlineEditingId)){
				roleService.addRole1(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setDownloadStatus(1).setEditStatus(0).setDeleteStatus(0).setOnlineEditing(1).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(1).setPrimaryUploadStatus(0).setFileId(param.getId()).setUserId(onlineEditingId));
				stringSet.add(onlineEditingId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,onlineEditingId).eq(SysFileRole::getFileId,param.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setPrimaryOnlineEditing(1);
				roleService.updateById(sysFileRole);
			}
		  }
		}
		//允许下载的权限，自动享有查看权限
		if(CollUtil.isNotEmpty(downloads)){
		for (String downloadId : downloads) {
			if (!stringSet.contains(downloadId)) {
				roleService.addRole1(new SysFileRoleParam().setLookStatus(1).setDownloadStatus(1).setEditStatus(0).setDeleteStatus(0).setOnlineEditing(0).setUploadStatus(0)
						.setPrimaryLookStatus(0).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(1).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setFileId(param.getId()).setUserId(downloadId));
				stringSet.add(downloadId);
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,downloadId).eq(SysFileRole::getFileId,param.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setPrimaryDownloadStatus(1);
				roleService.updateById(sysFileRole);
			}
		 }
		}
		//仅仅允许查看的权限
		if(CollUtil.isNotEmpty(lookIds)) {
			for (String lookId : lookIds) {
				if (!stringSet.contains(lookId)) {
					roleService.addRole1(new SysFileRoleParam().setLookStatus(1).setEditStatus(0).setDownloadStatus(0).setDeleteStatus(0).setUploadStatus(0).setOnlineEditing(0)
							.setPrimaryLookStatus(1).setPrimaryEditStatus(0).setPrimaryDeleteStatus(0).setPrimaryDownloadStatus(0).setPrimaryRenameStatus(0).setPrimaryOnlineEditing(0).setPrimaryUploadStatus(0).setFileId(param.getId()).setUserId(lookId));
					stringSet.add(lookId);
				}else {
					LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
					queryWrapper.eq(SysFileRole::getUserId,lookId).eq(SysFileRole::getFileId,param.getId()).eq(SysFileRole::getDelFlag,0);
					SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
					sysFileRole.setPrimaryLookStatus(1);
					roleService.updateById(sysFileRole);
				}
			}
		}
		return Result.ok();
	}
	private List<FIlePlanVO> selectParent(Long typeId, String userId, List<Long> roleTypeIds, Long unTypeId) {

		List<FIlePlanVO> list = new ArrayList<>();

		LambdaQueryWrapper<SysFileType> typeQueryWrapper = new LambdaQueryWrapper<SysFileType>()
				.eq(SysFileType::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(SysFileType::getParentId, typeId)
				.in(SysFileType::getId, roleTypeIds)
				.select(SysFileType::getId, SysFileType::getName);

		List<SysFileType> typeList = this.sysFileTypeMapper.selectList(typeQueryWrapper);

		for (SysFileType type : typeList) {
			FIlePlanVO vo = new FIlePlanVO();
			vo.setTypeName(type.getName())
					.setTypeId(type.getId())
					.setParentId(type.getParentId())
					.setStatus(0)
					.setId(unTypeId--)
					.setTempId(unTypeId--)
			;
			vo.setChildren(selectParent(type.getId(), userId, roleTypeIds, unTypeId));
			if (!CollectionUtils.isEmpty(vo.getChildren())){
				List<FIlePlanVO> children = vo.getChildren();
				unTypeId = children.get(children.size()-1).getTempId()-1;
			}
			list.add(vo);
		}

		//文件
		List<SysFile> sysFiles = this.lambdaQuery().eq(SysFile::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(SysFile::getTypeId, typeId)
				.and(query -> {
					query.eq(SysFile::getDownStatus, CommonConstant.STATUS_ENABLE)
							.or()
							.eq(SysFile::getCreateBy, userId);
				}).list();
		for (SysFile sysFile : sysFiles) {
			FIlePlanVO vo = new FIlePlanVO();
			vo.setTypeName(sysFile.getName())
					.setUrl(sysFile.getUrl())
					.setStatus(1)
					.setId(sysFile.getId())
					.setTypeId(sysFile.getTypeId())
					.setType(sysFile.getType())
					.setTempId(unTypeId--)
			;
			list.add(vo);
		}

		return list;
	}


}
