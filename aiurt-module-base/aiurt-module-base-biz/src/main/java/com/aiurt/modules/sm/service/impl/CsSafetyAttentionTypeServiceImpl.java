package com.aiurt.modules.sm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.TreeUtils;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionTypeMapper;
import com.aiurt.modules.sm.service.ICsSafetyAttentionService;
import com.aiurt.modules.sm.service.ICsSafetyAttentionTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 安全事项类型表
 * @Author: aiurt
 * @Date: 2022-11-17
 * @Version: V1.0
 */
@Service
public class CsSafetyAttentionTypeServiceImpl extends ServiceImpl<CsSafetyAttentionTypeMapper, CsSafetyAttentionType> implements ICsSafetyAttentionTypeService {

    @Autowired
    private ICsSafetyAttentionService iCsSafetyAttentionServicel;

    @Override
    public void addCsSafetyAttentionType(CsSafetyAttentionType csSafetyAttentionType) {
        // 校验安全事项类型编码是否重复
        checkSmTypeUnique(csSafetyAttentionType);

        //新增时设置hasChild为0
        csSafetyAttentionType.setHasChild(ICsSafetyAttentionTypeService.NOCHILD);
        if (oConvertUtils.isEmpty(csSafetyAttentionType.getPid())) {
            // 维护编码层级关系
            csSafetyAttentionType.setCodeScc("/" + csSafetyAttentionType.getCode() + "/");
            csSafetyAttentionType.setPid(ICsSafetyAttentionTypeService.ROOT_PID_VALUE);
        } else {
            //如果当前节点父ID不为空 则设置父节点的hasChildren 为1
            CsSafetyAttentionType parent = baseMapper.selectById(csSafetyAttentionType.getPid());
            if (parent != null && !"1".equals(parent.getHasChild())) {
                parent.setHasChild("1");
                baseMapper.updateById(parent);
            }
            // 维护编码层级关系
            // 查询上级的编码
            CsSafetyAttentionType safetyAttentionType = baseMapper.selectById(csSafetyAttentionType.getPid());
            csSafetyAttentionType.setCodeScc((Objects.nonNull(safetyAttentionType) && StrUtil.isNotBlank(safetyAttentionType.getCodeScc()) ? safetyAttentionType.getCodeScc() : "/") + csSafetyAttentionType.getCode() + "/");
        }

        // 部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.nonNull(loginUser)) {
            csSafetyAttentionType.setSysOrgCode(loginUser.getOrgCode());
        }
        baseMapper.insert(csSafetyAttentionType);
    }

    @Override
    public void updateCsSafetyAttentionType(CsSafetyAttentionType csSafetyAttentionType) {
        CsSafetyAttentionType entity = this.getById(csSafetyAttentionType.getId());
        if (entity == null) {
            throw new AiurtBootException("未找到对应实体");
        }
        String old_pid = entity.getPid();
        String new_pid = csSafetyAttentionType.getPid();
        if (!old_pid.equals(new_pid)) {
            updateOldParentNode(old_pid);
            if (oConvertUtils.isEmpty(new_pid)) {
                csSafetyAttentionType.setPid(ICsSafetyAttentionTypeService.ROOT_PID_VALUE);
            }
            if (!ICsSafetyAttentionTypeService.ROOT_PID_VALUE.equals(csSafetyAttentionType.getPid())) {
                baseMapper.updateTreeNodeStatus(csSafetyAttentionType.getPid(), ICsSafetyAttentionTypeService.HASCHILD);
            }
        }
        // 编码发生关系,维护层级关系
        if (StrUtil.isNotEmpty(csSafetyAttentionType.getCode())
                && StrUtil.isNotEmpty(entity.getCode()) &&
                !csSafetyAttentionType.getCode().equals(entity.getCode())) {
            String oldCode = entity.getCode();
            String newCode = csSafetyAttentionType.getCode();
            // 如果存在子级，需要全部更新层级关系
            List<CsSafetyAttentionType> treeChildIds = this.getTreeChildIds(csSafetyAttentionType.getId(), new ArrayList<>());
            if (CollUtil.isNotEmpty(treeChildIds)) {
                for (CsSafetyAttentionType treeChildId : treeChildIds) {
                    updateCodeScc(treeChildId, oldCode, newCode);
                }
                this.updateBatchById(treeChildIds);
            }
            // 更新自身层级
            this.updateCodeScc(csSafetyAttentionType, oldCode, newCode);
            this.updateById(csSafetyAttentionType);

        }
        baseMapper.updateById(csSafetyAttentionType);
    }

    /**
     * 修改层级关系
     *
     * @param treeChildId
     * @param oldCode     旧编码
     * @param newCode     新编码
     */
    private void updateCodeScc(CsSafetyAttentionType treeChildId, String oldCode, String newCode) {
        if (ObjectUtil.isEmpty(treeChildId) || StrUtil.isEmpty(newCode) || StrUtil.isEmpty(oldCode)) {
            return;
        }
        treeChildId.setCodeScc(treeChildId.getCodeScc().replace("/" + oldCode + "/", "/" + newCode + "/"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCsSafetyAttentionType(String id) throws AiurtBootException {
        //查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);

        // 校验给分类下面是否有安全事项数据
        if (id.indexOf(",") > 0) {
            List<String> idList = StrUtil.split(id, ',');
            long count = iCsSafetyAttentionServicel.count(new LambdaQueryWrapper<CsSafetyAttention>().in(CsSafetyAttention::getAttentionType, idList).eq(CsSafetyAttention::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (count > 0) {
                throw new AiurtBootException("该节点或其子节点下面存在安全事项数据，无法进行删除");
            }
        }

        if (id.indexOf(",") > 0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if (idVal != null) {
                    CsSafetyAttentionType csSafetyAttentionType = this.getById(idVal);
                    String pidVal = csSafetyAttentionType.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<CsSafetyAttentionType> dataList = baseMapper.selectList(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pidVal).notIn("id", Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if (flag) {
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for (String pid : pidArr) {
                this.updateOldParentNode(pid);
            }
        } else {
            CsSafetyAttentionType csSafetyAttentionType = this.getById(id);
            if (csSafetyAttentionType == null) {
                throw new AiurtBootException("未找到对应实体");
            }
            long count = iCsSafetyAttentionServicel.count(new LambdaQueryWrapper<CsSafetyAttention>().eq(CsSafetyAttention::getAttentionType, csSafetyAttentionType.getId()).eq(CsSafetyAttention::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (count > 0) {
                throw new AiurtBootException("该节点或其子节点下面存在安全事项数据，无法进行删除");
            }
            updateOldParentNode(csSafetyAttentionType.getPid());
            baseMapper.deleteById(id);
        }
    }

    @Override
    public List<CsSafetyAttentionType> queryTreeListNoPage(QueryWrapper<CsSafetyAttentionType> queryWrapper) {
        List<CsSafetyAttentionType> dataList = baseMapper.selectList(queryWrapper);
        List<CsSafetyAttentionType> mapList = new ArrayList<>();
        for (CsSafetyAttentionType data : dataList) {
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if (pidVal != null && !ICsSafetyAttentionTypeService.NOCHILD.equals(pidVal)) {
                CsSafetyAttentionType rootVal = this.getTreeRoot(pidVal);
                if (rootVal != null && !mapList.contains(rootVal)) {
                    mapList.add(rootVal);
                }
            } else {
                if (!mapList.contains(data)) {
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
    public List<TreeNode> queryTreeList() {
        List<TreeNode> treeNodes = baseMapper.queryTreeList();
        List<TreeNode> nodes = TreeUtils.treeFirst(treeNodes);
        // 拼接专业
        List<TreeNode> result = baseMapper.queryAllMajor(null);
        if (CollUtil.isNotEmpty(result) && CollUtil.isNotEmpty(nodes)) {
            for (TreeNode treeNode : result) {
                if (ObjectUtil.isEmpty(treeNode)) {
                    continue;
                }

                // 将安全事项类型挂在专业的孩子节点
                List<TreeNode> childList = new ArrayList<>();
                for (TreeNode node : nodes) {
                    if (treeNode.getId().equals(node.getExtendedField())) {
                        childList.add(node);
                    }
                }
                treeNode.setChildList(childList);
            }
        }
        return result;
    }

    @Override
    public TreeNode queryTreeByMajorCode(String majorCode) {
        if (StrUtil.isEmpty(majorCode)) {
            return new TreeNode();
        }
        List<TreeNode> treeNodes = baseMapper.queryTreeByMajorCode(majorCode);
        List<TreeNode> childs = TreeUtils.treeFirst(treeNodes);

        // 拼接专业
        List<TreeNode> result = baseMapper.queryAllMajor(majorCode);
        if (CollUtil.isNotEmpty(result)) {
            TreeNode treeNode = result.get(0);
            treeNode.setChildList(childs);
            return treeNode;
        }
        return new TreeNode();
    }

    /**
     * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
     *
     * @param pid
     */
    private void updateOldParentNode(String pid) {
        if (!ICsSafetyAttentionTypeService.ROOT_PID_VALUE.equals(pid)) {
            Long count = baseMapper.selectCount(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pid));
            if (count == null || count <= 1) {
                baseMapper.updateTreeNodeStatus(pid, ICsSafetyAttentionTypeService.NOCHILD);
            }
        }
    }

    /**
     * 递归查询节点的根节点
     *
     * @param pidVal
     * @return
     */
    private CsSafetyAttentionType getTreeRoot(String pidVal) {
        CsSafetyAttentionType data = baseMapper.selectById(pidVal);
        if (data != null && !ICsSafetyAttentionTypeService.ROOT_PID_VALUE.equals(data.getPid())) {
            return this.getTreeRoot(data.getPid());
        } else {
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     *
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if (pidVal != null) {
                if (!sb.toString().contains(pidVal)) {
                    if (sb.toString().length() > 0) {
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal, sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     *
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal, StringBuffer sb) {
        List<CsSafetyAttentionType> dataList = baseMapper.selectList(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pidVal));
        if (dataList != null && dataList.size() > 0) {
            for (CsSafetyAttentionType tree : dataList) {
                if (!sb.toString().contains(tree.getId())) {
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(), sb);
            }
        }
        return sb;
    }

    /**
     * 判断安全事项编码是否重复
     *
     * @param csSafetyAttentionType
     */
    private void checkSmTypeUnique(CsSafetyAttentionType csSafetyAttentionType) {
        if (ObjectUtil.isEmpty(csSafetyAttentionType)) {
            throw new AiurtBootException("未接收到参数");
        }

        LambdaQueryWrapper<CsSafetyAttentionType> lam = new LambdaQueryWrapper<>();
        lam.eq(CsSafetyAttentionType::getCode, csSafetyAttentionType.getCode());
        lam.eq(CsSafetyAttentionType::getDelFlag, CommonConstant.DEL_FLAG_0);
        // 兼容修改时的验证
        if (StrUtil.isNotEmpty(csSafetyAttentionType.getId())) {
            lam.ne(CsSafetyAttentionType::getId, csSafetyAttentionType.getId());
        }
        if (baseMapper.selectCount(lam) > 0) {
            throw new AiurtBootException("安全事项分类编码重复");
        }

        // 同一专业下分类名称不允许重复
        LambdaQueryWrapper<CsSafetyAttentionType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CsSafetyAttentionType::getName, csSafetyAttentionType.getName());
        lambdaQueryWrapper.eq(CsSafetyAttentionType::getDelFlag, CommonConstant.DEL_FLAG_0);
        lambdaQueryWrapper.eq(CsSafetyAttentionType::getMajorCode, csSafetyAttentionType.getMajorCode());
        // 兼容修改时的验证
        if (StrUtil.isNotEmpty(csSafetyAttentionType.getId())) {
            lambdaQueryWrapper.ne(CsSafetyAttentionType::getId, csSafetyAttentionType.getId());
        }
        if (baseMapper.selectCount(lambdaQueryWrapper) > 0) {
            throw new AiurtBootException("安全事项名称重复");
        }
    }

    /**
     * 递归查询所有子节点
     *
     * @param pidVal
     * @param sb
     * @return
     */
    private List<CsSafetyAttentionType> getTreeChildIds(String pidVal, List<CsSafetyAttentionType> sb) {
        List<CsSafetyAttentionType> dataList = baseMapper.selectList(new QueryWrapper<CsSafetyAttentionType>().eq("pid", pidVal).eq("del_flag", CommonConstant.DEL_FLAG_0));
        if (dataList != null && dataList.size() > 0) {
            for (CsSafetyAttentionType tree : dataList) {
                if (!sb.contains(tree.getId())) {
                    sb.add(tree);
                }
                this.getTreeChildIds(tree.getId(), sb);
            }
        }
        return sb;
    }
}
