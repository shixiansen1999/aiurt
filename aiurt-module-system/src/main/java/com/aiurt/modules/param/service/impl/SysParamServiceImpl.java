package com.aiurt.modules.param.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.aiurt.modules.param.service.ISysParamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
	public void addSysParam(SysParam sysParam) {
	   //新增时设置hasChild为0
	    sysParam.setHasChild(ISysParamService.NOCHILD);
		if(oConvertUtils.isEmpty(sysParam.getPid())){
			sysParam.setPid(ISysParamService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			SysParam parent = baseMapper.selectById(sysParam.getPid());
            String configItem = "configItem";
            String module = "module";
            if (parent.getCategory().equals(configItem) && sysParam.getCategory().equals(module)) {
                throw new AiurtBootException("父级为配置项不能添加模块子级");
            }
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(sysParam);
	}

	@Override
	public void updateSysParam(SysParam sysParam) {
		SysParam entity = this.getById(sysParam.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = sysParam.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				sysParam.setPid(ISysParamService.ROOT_PID_VALUE);
			}
			if(!ISysParamService.ROOT_PID_VALUE.equals(sysParam.getPid())) {
				baseMapper.updateTreeNodeStatus(sysParam.getPid(), ISysParamService.HASCHILD);
			}
		}
		baseMapper.updateById(sysParam);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteSysParam(String id) throws AiurtBootException {

        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            //查询选中节点下所有子节点一并删除
           /* StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    SysParam sysParam = this.getById(idVal);
                    String pidVal = sysParam.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<SysParam> dataList = baseMapper.selectList(new QueryWrapper<SysParam>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
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
            }*/
            throw new AiurtBootException("当前节点存在子节点，不能删除");
        }else{
            SysParam sysParam = this.getById(id);
            if(sysParam==null) {
                throw new AiurtBootException("未找到对应实体");
            }
            updateOldParentNode(sysParam.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<SysParam> queryTreeListNoPage(QueryWrapper<SysParam> queryWrapper) {
        List<SysParam> dataList = baseMapper.selectList(queryWrapper);
        List<SysParam> mapList = new ArrayList<>();
        for(SysParam data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !ISysParamService.NOCHILD.equals(pidVal)){
                SysParam rootVal = this.getTreeRoot(pidVal);
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
            LambdaQueryWrapper<SysParam> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysParam::getPid, parentCode);
            List<SysParam> list = baseMapper.selectList(queryWrapper);
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
    public void getCategoryName(SysParam sysParam) {
        String category = sysParam.getCategory();
        String module = "module";
        if (module.equals(category)) {
            sysParam.setCategoryName("模块");
        } else {
            sysParam.setCategoryName("配置项");
        }
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

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private SysParam getTreeRoot(String pidVal){
        SysParam data =  baseMapper.selectById(pidVal);
        if(data != null && !ISysParamService.ROOT_PID_VALUE.equals(data.getPid())){
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
        List<SysParam> dataList = baseMapper.selectList(new QueryWrapper<SysParam>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(SysParam tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
