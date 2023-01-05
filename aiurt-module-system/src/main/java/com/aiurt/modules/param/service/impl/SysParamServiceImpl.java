package com.aiurt.modules.param.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.aiurt.modules.param.service.ISysParamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: sys_param
 * @Author: aiurt
 * @Date:   2022-12-30
 * @Version: V1.0
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements ISysParamService {

	@Override
	public Result<String> addSysParam(SysParam sysParam) {
	    //校验编码是否唯一
        LambdaQueryWrapper<SysParam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysParam::getDelFlag, 0);
        queryWrapper.eq(SysParam::getCode, sysParam.getCode());
        boolean exists = this.getBaseMapper().exists(queryWrapper);
        if (exists) {
            return Result.error("编码已经存在！");
        }

        //新增时设置hasChild为0
        sysParam.setDelFlag(0);
		baseMapper.insert(sysParam);
        return Result.OK("添加成功！");
    }

	@Override
	public Result<String> updateSysParam(SysParam sysParam) {
		SysParam entity = this.getById(sysParam.getId());
        //校验编码是否唯一
        LambdaQueryWrapper<SysParam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysParam::getDelFlag, 0);
        queryWrapper.eq(SysParam::getCode, sysParam.getCode());
        queryWrapper.ne(SysParam::getId, sysParam.getId());
        boolean exists = this.getBaseMapper().exists(queryWrapper);
        if (exists) {
            return Result.error("编码已经存在,请修改！");
        }

        if(entity==null) {
            return Result.error("未找到对应实体!");
		}
		baseMapper.updateById(sysParam);
        return Result.OK("编辑成功!");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<String>  deleteSysParam(String id) throws AiurtBootException {

        SysParam sysParam = this.getById(id);
        if(sysParam==null) {
            return Result.error("未找到对应实体");
        }
        baseMapper.deleteById(id);
        return Result.OK("删除成功!");
	}




    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public void getCategoryName(SysParam sysParam) {
        String category = sysParam.getCategory();
        String module = "module";
        if (module.equals(category)) {
            sysParam.setCategoryName("模块");
        } else {
            sysParam.setCategoryName("配置项");
        }
    }

    @Override
    public Result<IPage<SysParam>> queryPageList(SysParam sysParam, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<SysParam> queryWrapper = new LambdaQueryWrapper<>();

        String paramTypeId = sysParam.getParamTypeId();
        if (StrUtil.isNotBlank(paramTypeId)) {
            // 查询分类的下级
            List<String> list = baseMapper.selectChild(paramTypeId);
            if (CollUtil.isEmpty(list)) {
                list = Collections.singletonList(paramTypeId);
            }
            queryWrapper.in(SysParam::getParamTypeId, list);
        }
        Page<SysParam> page = new Page<>(pageNo, pageSize);
        IPage<SysParam> pageList = this.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!ISysParamService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<SysParam>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, ISysParamService.NOCHILD);
			}
		}
	}




}
