package com.aiurt.modules.online.page.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.aiurt.modules.online.page.entity.ActCustomPageModule;
import com.aiurt.modules.online.page.mapper.ActCustomPageMapper;
import com.aiurt.modules.online.page.mapper.ActCustomPageModuleMapper;
import com.aiurt.modules.online.page.service.IActCustomPageModuleService;
import com.aiurt.modules.online.page.service.IActCustomPageService;
import com.aiurt.modules.tree.TreeBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.system.vo.SysPermissionModel;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: act_custom_page_module
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
@Service
public class ActCustomPageModuleServiceImpl extends ServiceImpl<ActCustomPageModuleMapper, ActCustomPageModule> implements IActCustomPageModuleService {

    @Autowired
    private ActCustomPageMapper actCustomPageMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
	@Override
    @Transactional(rollbackFor = Exception.class)
    public void addActCustomPageModule(ActCustomPageModule actCustomPageModule) {
        LambdaQueryWrapper<ActCustomPageModule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomPageModule::getId,actCustomPageModule.getId());
        List<ActCustomPageModule> actCustomPageModules = baseMapper.selectList(queryWrapper);
        if(CollUtil.isNotEmpty(actCustomPageModules)){
            throw new AiurtBootException("该菜单已经存在，请不要重复添加");
        }
        //新增时设置hasChild为0
	    actCustomPageModule.setHasChild(IActCustomPageModuleService.NOCHILD);
        //新增时获取菜单名称
        SysPermissionModel sysPermissionModel = sysBaseApi.getPermissionById(actCustomPageModule.getId());
        if (ObjectUtil.isNotEmpty(sysPermissionModel)) {
            String permissionName = sysPermissionModel.getName();
            actCustomPageModule.setModuleName(permissionName);
        }
        if(StrUtil.isEmpty(actCustomPageModule.getPid())){
			actCustomPageModule.setPid(IActCustomPageModuleService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			ActCustomPageModule parent = baseMapper.selectById(actCustomPageModule.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
        actCustomPageModule.setModuleCode(String.format("%s%s","m",System.currentTimeMillis()));
        baseMapper.insert(actCustomPageModule);
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActCustomPageModule(ActCustomPageModule actCustomPageModule) {
		ActCustomPageModule entity = this.getById(actCustomPageModule.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = actCustomPageModule.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(StrUtil.isEmpty(new_pid)){
				actCustomPageModule.setPid(IActCustomPageModuleService.ROOT_PID_VALUE);
			}
			if(!IActCustomPageModuleService.ROOT_PID_VALUE.equals(actCustomPageModule.getPid())) {
				baseMapper.updateTreeNodeStatus(actCustomPageModule.getPid(), IActCustomPageModuleService.HASCHILD);
			}
		}
		baseMapper.updateById(actCustomPageModule);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteActCustomPageModule(String id) throws AiurtBootException {
        //如果模块被引用，则不可以删除
        List<ActCustomPage> pageCustomModule = actCustomPageMapper.selectList(new QueryWrapper<ActCustomPage>().eq("page_module", id));
        if(CollUtil.isNotEmpty(pageCustomModule)){
            throw new AiurtBootException("该模块已被引用，无法删除");
        }
        //查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    ActCustomPageModule actCustomPageModule = this.getById(idVal);
                    String pidVal = actCustomPageModule.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<ActCustomPageModule> dataList = baseMapper.selectList(new QueryWrapper<ActCustomPageModule>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if(flag){
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for(String pid : pidArr){
                this.updateOldParentNode(pid);
            }
        }else{
            ActCustomPageModule actCustomPageModule = this.getById(id);
            if(actCustomPageModule==null) {
                throw new AiurtBootException("未找到对应实体");
            }
            updateOldParentNode(actCustomPageModule.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<ActCustomPageModule> queryTreeListNoPage(QueryWrapper<ActCustomPageModule> queryWrapper) {
        List<ActCustomPageModule> dataList = baseMapper.selectList(queryWrapper);
        List<ActCustomPageModule> mapList = new ArrayList<>();
        for(ActCustomPageModule data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !IActCustomPageModuleService.NOCHILD.equals(pidVal)){
                ActCustomPageModule rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentId) {
        String pid = ROOT_PID_VALUE;
        if (StrUtil.isNotEmpty(parentId)) {
            LambdaQueryWrapper<ActCustomPageModule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ActCustomPageModule::getPid, parentId);
            List<ActCustomPageModule> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new AiurtBootException("该编码【" + parentId + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new AiurtBootException("该编码【" + parentId + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (StrUtil.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> getModuleTree(String name) {
        List<SelectTreeModel> moduleTree = baseMapper.getModuleTree(name);
        return TreeBuilder.buildTree(moduleTree);
    }

    /**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IActCustomPageModuleService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<ActCustomPageModule>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IActCustomPageModuleService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private ActCustomPageModule getTreeRoot(String pidVal){
        ActCustomPageModule data =  baseMapper.selectById(pidVal);
        if(data != null && !IActCustomPageModuleService.ROOT_PID_VALUE.equals(data.getPid())){
            return this.getTreeRoot(data.getPid());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<ActCustomPageModule> dataList = baseMapper.selectList(new QueryWrapper<ActCustomPageModule>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(ActCustomPageModule tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
