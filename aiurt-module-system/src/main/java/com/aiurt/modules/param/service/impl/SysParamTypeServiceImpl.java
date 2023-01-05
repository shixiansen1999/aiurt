package com.aiurt.modules.param.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.entity.SysParamType;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.aiurt.modules.param.mapper.SysParamTypeMapper;
import com.aiurt.modules.param.service.ISysParamTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 系统参数分类树
 * @Author: aiurt
 * @Date:   2023-01-04
 * @Version: V1.0
 */
@Service
public class SysParamTypeServiceImpl extends ServiceImpl<SysParamTypeMapper, SysParamType> implements ISysParamTypeService {

	@Autowired
	private SysParamMapper sysParamMapper;

	@Override
	public void addSysParamType(SysParamType sysParamType) {
	   //新增时设置hasChild为0
	    sysParamType.setHasChild(ISysParamTypeService.NOCHILD);
		if(oConvertUtils.isEmpty(sysParamType.getPid())){
			sysParamType.setPid(Integer.valueOf(ISysParamTypeService.ROOT_PID_VALUE));
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			SysParamType parent = baseMapper.selectById(sysParamType.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(sysParamType);
	}

	@Override
	public void updateSysParamType(SysParamType sysParamType) {
		SysParamType entity = this.getById(sysParamType.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		Integer old_pid = entity.getPid();
        Integer new_pid = sysParamType.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				sysParamType.setPid(Integer.valueOf(ISysParamTypeService.ROOT_PID_VALUE));
			}
			if(!ISysParamTypeService.ROOT_PID_VALUE.equals(sysParamType.getPid())) {
				baseMapper.updateTreeNodeStatus(sysParamType.getPid(), ISysParamTypeService.HASCHILD);
			}
		}
		baseMapper.updateById(sysParamType);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteSysParamType(String id) throws AiurtBootException {
		// 判断有配置项不能删除
		// 下级有配置先也不能删除
		if (StrUtil.isBlank(id)) {
			return;
		}

		List<String> childList = sysParamMapper.selectChild(id);
		if (CollUtil.isEmpty(childList)) {
			childList = Collections.singletonList(id);
		}

		LambdaQueryWrapper<SysParam> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(SysParam::getParamTypeId, childList);

		boolean exists = sysParamMapper.exists(queryWrapper);

		if (exists) {
			throw new AiurtBootException("不能删除该分类， 该类型下已存在配置项");
		}

		baseMapper.deleteBatchIds(childList);
	}




	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(Integer pid) {
		if(!ISysParamTypeService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<SysParamType>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, ISysParamTypeService.NOCHILD);
			}
		}
	}







}
