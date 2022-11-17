package com.aiurt.modules.sm.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sm.dto.SafetyAttentionTypeTreeDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionTypeMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionTypeService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 安全事项类型表
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Service
public class CsSafetyAttentionTypeServiceImpl extends ServiceImpl<CsSafetyAttentionTypeMapper, CsSafetyAttentionType> implements ICsSafetyAttentionTypeService {

	@Override
	public void addCsSafetyAttentionType(CsSafetyAttentionType csSafetyAttentionType) {
	   //新增时设置hasChild为0
	    csSafetyAttentionType.setHasChild(ICsSafetyAttentionTypeService.NOCHILD);
		if(oConvertUtils.isEmpty(csSafetyAttentionType.getPid())){
			csSafetyAttentionType.setPid(ICsSafetyAttentionTypeService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			CsSafetyAttentionType parent = baseMapper.selectById(csSafetyAttentionType.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(csSafetyAttentionType);
	}

	@Override
	public void updateCsSafetyAttentionType(CsSafetyAttentionType csSafetyAttentionType) {
		CsSafetyAttentionType entity = this.getById(csSafetyAttentionType.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = csSafetyAttentionType.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				csSafetyAttentionType.setPid(ICsSafetyAttentionTypeService.ROOT_PID_VALUE);
			}
			if(!ICsSafetyAttentionTypeService.ROOT_PID_VALUE.equals(csSafetyAttentionType.getPid())) {
				baseMapper.updateTreeNodeStatus(csSafetyAttentionType.getPid(), ICsSafetyAttentionTypeService.HASCHILD);
			}
		}
		baseMapper.updateById(csSafetyAttentionType);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCsSafetyAttentionType(String id) throws AiurtBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    CsSafetyAttentionType csSafetyAttentionType = this.getById(idVal);
                    String pidVal = csSafetyAttentionType.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<CsSafetyAttentionType> dataList = baseMapper.selectList(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
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
            CsSafetyAttentionType csSafetyAttentionType = this.getById(id);
            if(csSafetyAttentionType==null) {
                throw new AiurtBootException("未找到对应实体");
            }
            updateOldParentNode(csSafetyAttentionType.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<CsSafetyAttentionType> queryTreeListNoPage(QueryWrapper<CsSafetyAttentionType> queryWrapper) {
        List<CsSafetyAttentionType> dataList = baseMapper.selectList(queryWrapper);
        List<CsSafetyAttentionType> mapList = new ArrayList<>();
        for(CsSafetyAttentionType data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !ICsSafetyAttentionTypeService.NOCHILD.equals(pidVal)){
                CsSafetyAttentionType rootVal = this.getTreeRoot(pidVal);
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
            LambdaQueryWrapper<CsSafetyAttentionType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CsSafetyAttentionType::getPid, parentCode);
            List<CsSafetyAttentionType> list = baseMapper.selectList(queryWrapper);
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

    @Override
    public List<SelectTreeModel> queryTreeList() {
        LambdaQueryWrapper<CsSafetyAttentionType> query = new LambdaQueryWrapper<>();
        query.eq(CsSafetyAttentionType::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(CsSafetyAttentionType::getSort);
        query.orderByDesc(CsSafetyAttentionType::getCreateTime);
        List<CsSafetyAttentionType> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
//        List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
        return null ;
    }

    /**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!ICsSafetyAttentionTypeService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, ICsSafetyAttentionTypeService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private CsSafetyAttentionType getTreeRoot(String pidVal){
        CsSafetyAttentionType data =  baseMapper.selectById(pidVal);
        if(data != null && !ICsSafetyAttentionTypeService.ROOT_PID_VALUE.equals(data.getPid())){
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
        List<CsSafetyAttentionType> dataList = baseMapper.selectList(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(CsSafetyAttentionType tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
