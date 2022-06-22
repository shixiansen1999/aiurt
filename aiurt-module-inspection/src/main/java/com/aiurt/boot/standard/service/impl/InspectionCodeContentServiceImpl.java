package com.aiurt.boot.standard.service.impl;

import com.aiurt.boot.entity.inspection.standard.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.service.IInspectionCodeContentService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class InspectionCodeContentServiceImpl extends ServiceImpl<InspectionCodeContentMapper, InspectionCodeContent> implements IInspectionCodeContentService {

	@Override
	public void addInspectionCodeContent(InspectionCodeContent inspectionCodeContent) {
	   //新增时设置hasChild为0
	    inspectionCodeContent.setHasChild(IInspectionCodeContentService.NOCHILD);
		if(oConvertUtils.isEmpty(inspectionCodeContent.getPid())){
			inspectionCodeContent.setPid(IInspectionCodeContentService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			InspectionCodeContent parent = baseMapper.selectById(inspectionCodeContent.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(inspectionCodeContent);
	}

	@Override
	public void updateInspectionCodeContent(InspectionCodeContent inspectionCodeContent) {
		InspectionCodeContent entity = this.getById(inspectionCodeContent.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = inspectionCodeContent.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				inspectionCodeContent.setPid(IInspectionCodeContentService.ROOT_PID_VALUE);
			}
			if(!IInspectionCodeContentService.ROOT_PID_VALUE.equals(inspectionCodeContent.getPid())) {
				baseMapper.updateTreeNodeStatus(inspectionCodeContent.getPid(), IInspectionCodeContentService.HASCHILD);
			}
		}
		baseMapper.updateById(inspectionCodeContent);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteInspectionCodeContent(String id) throws AiurtBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    InspectionCodeContent inspectionCodeContent = this.getById(idVal);
                    String pidVal = inspectionCodeContent.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<InspectionCodeContent> dataList = baseMapper.selectList(new QueryWrapper<InspectionCodeContent>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
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
            InspectionCodeContent inspectionCodeContent = this.getById(id);
            if(inspectionCodeContent==null) {
                throw new AiurtBootException("未找到对应实体");
            }
            updateOldParentNode(inspectionCodeContent.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<InspectionCodeContent> queryTreeListNoPage(QueryWrapper<InspectionCodeContent> queryWrapper) {
        List<InspectionCodeContent> dataList = baseMapper.selectList(queryWrapper);
        List<InspectionCodeContent> mapList = new ArrayList<>();
        for(InspectionCodeContent data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !IInspectionCodeContentService.NOCHILD.equals(pidVal)){
                InspectionCodeContent rootVal = this.getTreeRoot(pidVal);
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
            LambdaQueryWrapper<InspectionCodeContent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InspectionCodeContent::getPid, parentCode);
            List<InspectionCodeContent> list = baseMapper.selectList(queryWrapper);
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
		if(!IInspectionCodeContentService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<InspectionCodeContent>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IInspectionCodeContentService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private InspectionCodeContent getTreeRoot(String pidVal){
        InspectionCodeContent data =  baseMapper.selectById(pidVal);
        if(data != null && !IInspectionCodeContentService.ROOT_PID_VALUE.equals(data.getPid())){
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
        List<InspectionCodeContent> dataList = baseMapper.selectList(new QueryWrapper<InspectionCodeContent>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(InspectionCodeContent tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
