package com.aiurt.modules.modeler.service.impl;

import com.aiurt.modules.modeler.entity.ActCustomClassify;
import com.aiurt.modules.modeler.mapper.ActCustomClassifyMapper;
import com.aiurt.modules.modeler.service.IActCustomClassifyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程分类
 * @Author: aiurt
 * @Date:   2022-07-21
 * @Version: V1.0
 */
@Service
public class ActCustomClassifyServiceImpl extends ServiceImpl<ActCustomClassifyMapper, ActCustomClassify> implements IActCustomClassifyService {

	@Override
	public void addActCustomClassify(ActCustomClassify actCustomClassify) {
	   //新增时设置hasChild为0
	    actCustomClassify.setHasChild(IActCustomClassifyService.NOCHILD);
		if(oConvertUtils.isEmpty(actCustomClassify.getPid())){
			actCustomClassify.setPid(IActCustomClassifyService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			ActCustomClassify parent = baseMapper.selectById(actCustomClassify.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(actCustomClassify);
	}

	@Override
	public void updateActCustomClassify(ActCustomClassify actCustomClassify) {
		ActCustomClassify entity = this.getById(actCustomClassify.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		String oldPid = entity.getPid();
		String newPid = actCustomClassify.getPid();
		if(!oldPid.equals(newPid)) {
			updateOldParentNode(oldPid);
			if(oConvertUtils.isEmpty(newPid)){
				actCustomClassify.setPid(IActCustomClassifyService.ROOT_PID_VALUE);
			}
			if(!IActCustomClassifyService.ROOT_PID_VALUE.equals(actCustomClassify.getPid())) {
				baseMapper.updateTreeNodeStatus(actCustomClassify.getPid(), IActCustomClassifyService.HASCHILD);
			}
		}
		baseMapper.updateById(actCustomClassify);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteActCustomClassify(String id) throws AiurtBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    ActCustomClassify actCustomClassify = this.getById(idVal);
                    String pidVal = actCustomClassify.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<ActCustomClassify> dataList = baseMapper.selectList(new QueryWrapper<ActCustomClassify>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
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
            ActCustomClassify actCustomClassify = this.getById(id);
            if(actCustomClassify==null) {
                throw new AiurtBootException("未找到对应实体");
            }
            updateOldParentNode(actCustomClassify.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<ActCustomClassify> queryTreeListNoPage(QueryWrapper<ActCustomClassify> queryWrapper) {
        List<ActCustomClassify> dataList = baseMapper.selectList(queryWrapper);
        List<ActCustomClassify> mapList = new ArrayList<>();
        for(ActCustomClassify data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !IActCustomClassifyService.NOCHILD.equals(pidVal)){
                ActCustomClassify rootVal = this.getTreeRoot(pidVal);
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
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<ActCustomClassify> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ActCustomClassify::getPid, parentCode);
            List<ActCustomClassify> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new AiurtBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new AiurtBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IActCustomClassifyService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<ActCustomClassify>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IActCustomClassifyService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private ActCustomClassify getTreeRoot(String pidVal){
        ActCustomClassify data =  baseMapper.selectById(pidVal);
        if(data != null && !IActCustomClassifyService.ROOT_PID_VALUE.equals(data.getPid())){
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
        List<ActCustomClassify> dataList = baseMapper.selectList(new QueryWrapper<ActCustomClassify>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(ActCustomClassify tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
