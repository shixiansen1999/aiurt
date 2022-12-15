package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
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
		List<SysFileType> sysFileTypeList = sysFileTypeMapper.selectList(new LambdaQueryWrapper<SysFileType>().eq(SysFileType::getGrade, param.getGrade()).eq(SysFileType::getName, param.getName()));
		if(CollUtil.isNotEmpty(sysFileTypeList))
		{
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
		for (String uploadId : uploads) {
			roleService.addRole(new SysFileRoleParam().setLookStatus(1).setEditStatus(0).setDeleteStatus(1)
					.setDownloadStatus(1).setRenameStatus(1).setOnlineEditing(1).setUploadStatus(1).setTypeId(type.getId()).setUserId(uploadId));
			stringSet.add(uploadId);
		}
		//允许编辑权限,自动享有查看权限，删除权限，下载权限，在线编辑权限
		for (String editId : editIds) {
			if (!stringSet.contains(editId)){
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setEditStatus(1).setDeleteStatus(1).setDownloadStatus(1).setOnlineEditing(1).setUploadStatus(0).setTypeId(type.getId()).setUserId(editId));
			}else {
				LambdaQueryWrapper<SysFileRole> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysFileRole::getUserId,editId).eq(SysFileRole::getTypeId,type.getId()).eq(SysFileRole::getDelFlag,0);
				SysFileRole sysFileRole = roleService.getBaseMapper().selectOne(queryWrapper);
				sysFileRole.setEditStatus(1);
				roleService.updateById(sysFileRole);
			}
		}
		//允许删除的权限
		for (String deleteId : deletes) {
			if (!stringSet.contains(deleteId)) {
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setEditStatus(0).setDeleteStatus(1).setDownloadStatus(1).setOnlineEditing(1).setUploadStatus(0).setTypeId(type.getId()).setUserId(deleteId));
			}
		}
		//允许在线编辑的权限
		for (String onlineEditingId : onlineEditing) {
			if (!stringSet.contains(onlineEditingId)){
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setRenameStatus(1).setDownloadStatus(1).setEditStatus(0).setDeleteStatus(0).setOnlineEditing(1).setUploadStatus(0).setTypeId(type.getId()).setUserId(onlineEditingId));
			}
		}
		//允许下载的权限
		for (String downloadId : downloads) {
			if (!stringSet.contains(downloadId)) {
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setDownloadStatus(1).setEditStatus(0).setDeleteStatus(0).setOnlineEditing(0).setUploadStatus(0).setTypeId(type.getId()).setUserId(downloadId));
			}
		}
		//仅仅允许查看的权限
		for (String lookId : lookIds) {
			if (!stringSet.contains(lookId)) {
				roleService.addRole(new SysFileRoleParam().setLookStatus(1).setEditStatus(0).setDownloadStatus(0).setDeleteStatus(0).setUploadStatus(0).setOnlineEditing(0).setTypeId(type.getId()).setUserId(lookId));
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
				Map<Integer, List<SysFileRole>> listMap = list.stream()
						.filter(item-> ObjectUtil.isNotEmpty(item.getEditStatus())).collect(Collectors.groupingBy(SysFileRole::getEditStatus));
				if (listMap != null && listMap.size() > 0) {
					//获取编辑列表中数据
					if (CollectionUtils.isNotEmpty(listMap.get(1))) {
						Optional.ofNullable(listMap.get(1)).ifPresent(roles -> {
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
					if (CollectionUtils.isNotEmpty(listMap6.get(1))) {
				     Optional.ofNullable(listMap6.get(1)).ifPresent(roles -> {
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
					if (CollectionUtils.isNotEmpty(listMap1.get(1))) {
						Optional.ofNullable(listMap1.get(1)).ifPresent(roles -> {
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
					if (CollectionUtils.isNotEmpty(listMap2.get(1))) {
						Optional.ofNullable(listMap2.get(1)).ifPresent(roles -> {
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
					if (CollectionUtils.isNotEmpty(listMap3.get(1))) {
						Optional.ofNullable(listMap3.get(1)).ifPresent(roles -> {
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
					if (CollectionUtils.isNotEmpty(listMap4.get(1))) {
						Optional.ofNullable(listMap4.get(1)).ifPresent(roles -> {
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
					if (CollectionUtils.isNotEmpty(listMap5.get(1))) {
						Optional.ofNullable(listMap5.get(1)).ifPresent(roles -> {
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
