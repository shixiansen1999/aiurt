package com.aiurt.modules.online.workflowapi.service.impl;

import com.aiurt.common.constant.CacheConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterfaceModule;
import com.aiurt.modules.online.workflowapi.mapper.ActCustomInterfaceModuleMapper;
import com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceModuleService;
import com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceService;
import com.aiurt.modules.tree.TreeBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @Description: act_custom_interface_module
 * @Author: jeecg-boot
 * @Date: 2023-08-14
 * @Version: V1.0
 */
@Service
public class ActCustomInterfaceModuleServiceImpl extends ServiceImpl<ActCustomInterfaceModuleMapper, ActCustomInterfaceModule> implements IActCustomInterfaceModuleService {

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IActCustomInterfaceService actCustomInterfaceService;

    @Override
    public void addActCustomInterfaceModule(ActCustomInterfaceModule actCustomInterfaceModule) {
        //新增时设置hasChild为0
        actCustomInterfaceModule.setHasChild(IActCustomInterfaceModuleService.NOCHILD);
        if (oConvertUtils.isEmpty(actCustomInterfaceModule.getPid())) {
            actCustomInterfaceModule.setPid(IActCustomInterfaceModuleService.ROOT_PID_VALUE);
        } else {
            //如果当前节点父ID不为空 则设置父节点的hasChildren 为1
            ActCustomInterfaceModule parent = baseMapper.selectById(actCustomInterfaceModule.getPid());
            String num = "1";
            if (parent != null && !num.equals(parent.getHasChild())) {
                parent.setHasChild("1");
                baseMapper.updateById(parent);
            }
        }

        actCustomInterfaceModule.setModuleCode(String.format("%s%s", "m", System.currentTimeMillis()));
        baseMapper.insert(actCustomInterfaceModule);
    }

    @Override
    public void updateActCustomInterfaceModule(ActCustomInterfaceModule actCustomInterfaceModule) {
        ActCustomInterfaceModule entity = this.getById(actCustomInterfaceModule.getId());
        if (entity == null) {
            throw new AiurtBootException("未找到对应实体");
        }
        String oldPid = entity.getPid();
        String newPid = actCustomInterfaceModule.getPid();
        if (!oldPid.equals(newPid)) {
            updateOldParentNode(oldPid);
            if (oConvertUtils.isEmpty(newPid)) {
                actCustomInterfaceModule.setPid(IActCustomInterfaceModuleService.ROOT_PID_VALUE);
            }
            if (!IActCustomInterfaceModuleService.ROOT_PID_VALUE.equals(actCustomInterfaceModule.getPid())) {
                baseMapper.updateTreeNodeStatus(actCustomInterfaceModule.getPid(), IActCustomInterfaceModuleService.HASCHILD);
            }
        }
        baseMapper.updateById(actCustomInterfaceModule);
        // 刷新指定字典表的缓存
        Set keys = redisTemplate.keys("sys:cache:dictTable::SimpleKey \\[act_custom_interface_module*");
        redisTemplate.delete(keys);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActCustomInterfaceModule(String id) throws AiurtBootException {
        //查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        String symbol = ",";
        if (id.indexOf(symbol) > 0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if (idVal != null) {
                    ActCustomInterfaceModule actCustomInterfaceModule = this.getById(idVal);
                    String pidVal = actCustomInterfaceModule.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<ActCustomInterfaceModule> dataList = baseMapper.selectList(new QueryWrapper<ActCustomInterfaceModule>().eq("pid", pidVal).notIn("id", Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if (flag) {
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            // 模块下有接口就不能删除
            actCustomInterfaceService.checkAndThrowIfModuleHasAssociatedInterfaces(Arrays.asList(idArr));
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for (String pid : pidArr) {
                this.updateOldParentNode(pid);
            }
        } else {
            ActCustomInterfaceModule actCustomInterfaceModule = this.getById(id);
            if (actCustomInterfaceModule == null) {
                throw new AiurtBootException("未找到对应实体");
            }
            // 模块下有接口就不能删除
            actCustomInterfaceService.checkAndThrowIfModuleHasAssociatedInterfaces(Arrays.asList(id));

            updateOldParentNode(actCustomInterfaceModule.getPid());
            baseMapper.deleteById(id);
        }
    }

    @Override
    public List<ActCustomInterfaceModule> queryTreeListNoPage(QueryWrapper<ActCustomInterfaceModule> queryWrapper) {
        List<ActCustomInterfaceModule> dataList = baseMapper.selectList(queryWrapper);
        List<ActCustomInterfaceModule> mapList = new ArrayList<>();
        for (ActCustomInterfaceModule data : dataList) {
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if (pidVal != null && !IActCustomInterfaceModuleService.NOCHILD.equals(pidVal)) {
                ActCustomInterfaceModule rootVal = this.getTreeRoot(pidVal);
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
    public List<SelectTreeModel> getModuleTree(String name) {
        List<SelectTreeModel> moduleTree = baseMapper.getModuleTree(name);
        return TreeBuilder.buildTree(moduleTree);
    }


    /**
     * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
     *
     * @param pid
     */
    private void updateOldParentNode(String pid) {
        if (!IActCustomInterfaceModuleService.ROOT_PID_VALUE.equals(pid)) {
            Long count = baseMapper.selectCount(new QueryWrapper<ActCustomInterfaceModule>().eq("pid", pid));
            if (count == null || count <= 1) {
                baseMapper.updateTreeNodeStatus(pid, IActCustomInterfaceModuleService.NOCHILD);
            }
        }
    }

    /**
     * 递归查询节点的根节点
     *
     * @param pidVal
     * @return
     */
    private ActCustomInterfaceModule getTreeRoot(String pidVal) {
        ActCustomInterfaceModule data = baseMapper.selectById(pidVal);
        if (data != null && !IActCustomInterfaceModuleService.ROOT_PID_VALUE.equals(data.getPid())) {
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
        List<ActCustomInterfaceModule> dataList = baseMapper.selectList(new QueryWrapper<ActCustomInterfaceModule>().eq("pid", pidVal));
        if (dataList != null && dataList.size() > 0) {
            for (ActCustomInterfaceModule tree : dataList) {
                if (!sb.toString().contains(tree.getId())) {
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(), sb);
            }
        }
        return sb;
    }

}
