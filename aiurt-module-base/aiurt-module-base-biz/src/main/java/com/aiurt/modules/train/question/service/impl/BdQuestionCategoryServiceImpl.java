package com.aiurt.modules.train.question.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.util.TreeUtils;
import com.aiurt.modules.train.question.entity.BdQuestionCategory;
import com.aiurt.modules.train.question.mapper.BdQuestionCategoryMapper;
import com.aiurt.modules.train.question.service.IBdQuestionCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: bd_question_category
 * @Author: jeecg-boot
 * @Date:   2022-04-15
 * @Version: V1.0
 */
@Service
public class BdQuestionCategoryServiceImpl extends ServiceImpl<BdQuestionCategoryMapper, BdQuestionCategory> implements IBdQuestionCategoryService {

	@Override
	public void addBdQuestionCategory(BdQuestionCategory bdQuestionCategory) {
        // 校验类别名称是否重复
        if (StrUtil.isNotEmpty(bdQuestionCategory.getName())) {
            LambdaQueryWrapper<BdQuestionCategory> la = new LambdaQueryWrapper<>();
            la.eq(BdQuestionCategory::getName, bdQuestionCategory.getName());
            List<BdQuestionCategory> bdExCategories = baseMapper.selectList(la);
            if (CollUtil.isNotEmpty(bdExCategories)) {
                throw new JeecgBootException("习题类别名称已存在，请重新输入");
            }
        }
		if(oConvertUtils.isEmpty(bdQuestionCategory.getPid())){
			bdQuestionCategory.setPid(IBdQuestionCategoryService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			BdQuestionCategory parent = baseMapper.selectById(bdQuestionCategory.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(bdQuestionCategory);
	}

	@Override
	public void updateBdQuestionCategory(BdQuestionCategory bdQuestionCategory) {
		BdQuestionCategory entity = this.getById(bdQuestionCategory.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = bdQuestionCategory.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				bdQuestionCategory.setPid(IBdQuestionCategoryService.ROOT_PID_VALUE);
			}
			if(!IBdQuestionCategoryService.ROOT_PID_VALUE.equals(bdQuestionCategory.getPid())) {
				baseMapper.updateTreeNodeStatus(bdQuestionCategory.getPid(), IBdQuestionCategoryService.HASCHILD);
			}
		}
		baseMapper.updateById(bdQuestionCategory);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBdQuestionCategory(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    BdQuestionCategory bdQuestionCategory = this.getById(idVal);
                    String pidVal = bdQuestionCategory.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<BdQuestionCategory> dataList = baseMapper.selectList(new QueryWrapper<BdQuestionCategory>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
                    if((dataList == null || dataList.size()==0) && !Arrays.asList(idArr).contains(pidVal)
                            && !sb.toString().contains(pidVal)){
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
            BdQuestionCategory bdQuestionCategory = this.getById(id);
            if(bdQuestionCategory==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(bdQuestionCategory.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<BdQuestionCategory> queryTreeListNoPage(QueryWrapper<BdQuestionCategory> queryWrapper) {
        List<BdQuestionCategory> dataList = baseMapper.selectList(queryWrapper);
        List<BdQuestionCategory> mapList = new ArrayList<>();
        for(BdQuestionCategory data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !"0".equals(pidVal)){
                BdQuestionCategory rootVal = this.getTreeRoot(pidVal);
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

    /**
     * 查询习题类别树
     *
     * @return
     */
    @Override
    public List<TreeNode> queryPageList() {
        List<TreeNode> treeNodes = baseMapper.queryPageList();
        return TreeUtils.treeFirst(treeNodes);
    }

    /**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IBdQuestionCategoryService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = Math.toIntExact(baseMapper.selectCount(new QueryWrapper<BdQuestionCategory>().eq("pid", pid)));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IBdQuestionCategoryService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private BdQuestionCategory getTreeRoot(String pidVal){
        BdQuestionCategory data =  baseMapper.selectById(pidVal);
        if(data != null && !"0".equals(data.getPid())){
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
        List<BdQuestionCategory> dataList = baseMapper.selectList(new QueryWrapper<BdQuestionCategory>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(BdQuestionCategory tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
