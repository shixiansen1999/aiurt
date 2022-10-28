package com.aiurt.boot.standard.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.service.IInspectionCodeContentService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class InspectionCodeContentServiceImpl extends ServiceImpl<InspectionCodeContentMapper, InspectionCodeContent> implements IInspectionCodeContentService {
    @Resource
    private ISysBaseAPI sysBaseApi;
	@Override
	public void addInspectionCodeContent(InspectionCodeContent inspectionCodeContent) {
	   //新增时设置hasChild为0
	    inspectionCodeContent.setHasChild(IInspectionCodeContentService.NOCHILD);
		if(oConvertUtils.isEmpty(inspectionCodeContent.getPid())){
			inspectionCodeContent.setPid(IInspectionCodeContentService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			InspectionCodeContent parent = baseMapper.selectById(inspectionCodeContent.getPid());
			if(parent!=null && !InspectionConstant.HAS_CHILD_1.equals(parent.getHasChild())){
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
		String oldPid = entity.getPid();
		String newPid = inspectionCodeContent.getPid();
		if(!oldPid.equals(newPid)) {
			updateOldParentNode(oldPid);
			if(oConvertUtils.isEmpty(newPid)){
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

    @Override
    public IPage<InspectionCodeContent> pageList(Page<InspectionCodeContent> page, InspectionCodeContent inspectionCodeContent) {
        //1.查询表中未删除的所有的数据
        List<InspectionCodeContent> allList = baseMapper.selectLists(inspectionCodeContent);
        if(inspectionCodeContent.getCode()!=null ||inspectionCodeContent.getName()!=null ||inspectionCodeContent.getStatusItem()!=null){
            return page.setRecords(allList);
        }
        //2.找到所有根节点 ParentId=0
        List<InspectionCodeContent> rooList = allList.stream().filter(r -> "0".equals(r.getPid())).collect(Collectors.toList());
        //3.找到所有非根节点
        List<InspectionCodeContent> subLists = allList.stream().filter(r -> !"0".equals(r.getPid())).collect(Collectors.toList());
        if (rooList.size()<1){
            return page.setRecords(subLists);
        }
        List<InspectionCodeContent> subList = allList.stream().filter(r -> !"0".equals(r.getPid())).collect(Collectors.toList());
        //4.循环阶段去subList找对应的字节点
        rooList = rooList.stream().map(root -> {
            //通过根节点的id和子节点的pid判断是否相等，如果相等的话，代表是根节点的子集
            List<InspectionCodeContent> list = subLists.stream().filter(r -> r.getPid().equals(root.getId())).collect(Collectors.toList());
            //如果当前没一个子级，初始化一个数组
            if (CollectionUtils.isEmpty(list)){
                list =new ArrayList<>();
            }
            root.setChildren(list);
            return root;
        }).collect(Collectors.toList());
        subList =subList.stream().map(s->{
            List<InspectionCodeContent> list = subLists.stream().filter(l-> l.getPid().equals(s.getId())).collect(Collectors.toList());
            s.setChildren(list);
            return s;
        }).collect(Collectors.toList());
        return page.setRecords(rooList);
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


    /**
     * 通过检修标准id查看检修项
     *
     * @param id  检修标准id
     * @return
     */
    @Override
    public List<InspectionCodeContent> selectCodeContentList(String id) {
        if (StrUtil.isEmpty(id)) {
            return new ArrayList<>();
        }
        List<InspectionCodeContent> result = baseMapper.selectList(
                new LambdaQueryWrapper<InspectionCodeContent>()
                        .eq(InspectionCodeContent::getInspectionCodeId, id)
                        .orderByAsc(InspectionCodeContent::getSortNo));

        if (CollUtil.isNotEmpty(result)) {
            result.forEach(r -> {
                r.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));
                r.setStatusItemName(sysBaseApi.translateDict(DictConstant.INSPECTION_STATUS_ITEM, String.valueOf(r.getStatusItem())));
                r.setInspectionTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_VALUE,String.valueOf(r.getInspectionType())));
            });
        }

        // 构造树形结构
        return treeFirst(result);
    }

    @Override
    public void checkCode(String code, String inspectionCodeId, String id) {
        QueryWrapper<InspectionCodeContent> queryWrapper = new QueryWrapper<InspectionCodeContent>();
               queryWrapper.lambda().eq(InspectionCodeContent::getInspectionCodeId,inspectionCodeId)
                .eq(InspectionCodeContent::getCode,code);
        if (id!="" && id!=null){
            queryWrapper.ne("id",id);
        }
        List<InspectionCodeContent> inspectionCodeContents = baseMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(inspectionCodeContents)){
            throw new AiurtBootException("输入的code当前列表重复,请重新输入");
        }
    }
    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<InspectionCodeContent> treeFirst(List<InspectionCodeContent> list) {
        Map<String, InspectionCodeContent> map = new HashMap<>(50);
        for (InspectionCodeContent treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }

    /**
     * @param list
     * @param map
     * @return
     */
    private static List<InspectionCodeContent> addChildren(List<InspectionCodeContent> list, Map<String, InspectionCodeContent> map) {
        List<InspectionCodeContent> rootNodes = new ArrayList<>();
        for (InspectionCodeContent treeNode : list) {
            InspectionCodeContent parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<InspectionCodeContent>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }

}
