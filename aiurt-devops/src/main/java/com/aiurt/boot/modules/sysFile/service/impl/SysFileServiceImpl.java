package com.aiurt.boot.modules.sysFile.service.impl;

import com.aiurt.boot.modules.sysFile.entity.SysFile;
import com.aiurt.boot.modules.sysFile.entity.SysFileRole;
import com.aiurt.boot.modules.sysFile.entity.SysFileType;
import com.aiurt.boot.modules.sysFile.mapper.SysFileMapper;
import com.aiurt.boot.modules.sysFile.mapper.SysFileTypeMapper;
import com.aiurt.boot.modules.sysFile.param.FileAppParam;
import com.aiurt.boot.modules.sysFile.service.ISysFileRoleService;
import com.aiurt.boot.modules.sysFile.service.ISysFileService;
import com.aiurt.boot.modules.sysFile.vo.FIlePlanVO;
import com.aiurt.boot.modules.sysFile.vo.FileAppVO;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

	@Override
	public IPage<FileAppVO> selectAppList(FileAppParam param) {
		if (param.getPageNo() == null) {
			param.setPageNo(1);
		}
		if (param.getPageSize() == null) {
			param.setPageSize(10);
		}
		IPage<FileAppVO> page = new Page<>();
		List<FileAppVO> list = new ArrayList<>();
		int len = param.getPageNo() * param.getPageSize();
		LambdaQueryWrapper<SysFileType> typeQueryWrapper = new LambdaQueryWrapper<SysFileType>()
				.eq(SysFileType::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(SysFileType::getId, SysFileType::getName);
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
		} else {
			long l = len - total;
			long l1 = l % param.getPageSize() > 0 ? (l / param.getPageSize()) + 1 : l / param.getPageSize();
			long size = l > 10 ? 10 : l;
			long no = l1 - 1;
			IPage<SysFile> filePage = this.baseMapper.selectFilePage(new Page(no, size), param.getTypeId());
			Optional.ofNullable(filePage.getRecords()).ifPresent(sysFiles -> {
				sysFiles.forEach(f -> {
					FileAppVO appVO = new FileAppVO();
					appVO.setFileName(f.getName()).setId(f.getId()).setUrl(f.getUrl()).setStatus(1).setTypeId(f.getTypeId());
					list.add(appVO);
				});
			});
			page.setRecords(list).setTotal(total + filePage.getTotal());
			return page;
		}

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
