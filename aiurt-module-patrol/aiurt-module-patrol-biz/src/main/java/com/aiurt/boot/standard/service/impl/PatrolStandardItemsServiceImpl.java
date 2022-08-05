package com.aiurt.boot.standard.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.aiurt.boot.standard.dto.PatrolStandardItemsDTO;
import com.aiurt.boot.standard.dto.SysDictDTO;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.standard.service.IPatrolStandardItemsService;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStandardMapper;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolStandardItemsServiceImpl extends ServiceImpl<PatrolStandardItemsMapper, PatrolStandardItems> implements IPatrolStandardItemsService {

@Autowired
private  PatrolStandardItemsMapper patrolStandardItemsMapper;
@Autowired
private  PatrolTaskDeviceMapper patrolTaskDeviceMapper;
@Autowired
private PatrolTaskStandardMapper patrolTaskStandardMapper;
    @Override
    public List<PatrolStandardItems> queryPageList(String id) {
        //1.查询表中未删除的所有的数据
        List<PatrolStandardItems> allList = baseMapper.selectItemList(id);
        //2.找到所有根节点 ParentId=0
        List<PatrolStandardItems> rooList = allList.stream().filter(r -> "0".equals(r.getParentId())).collect(Collectors.toList());
        //3.找到所有非根节点
        List<PatrolStandardItems> subLists = allList.stream().filter(r -> !"0".equals(r.getParentId())).collect(Collectors.toList());
        List<PatrolStandardItems> subList = allList.stream().filter(r -> !"0".equals(r.getParentId())).collect(Collectors.toList());
        //4.循环阶段去subList找对应的字节点
        rooList = rooList.stream().map(root -> {
            //通过根节点的id和子节点的pid判断是否相等，如果相等的话，代表是根节点的子集
            List<PatrolStandardItems> list = subLists.stream().filter(r -> r.getParentId().equals(root.getId())).collect(Collectors.toList());
            //如果当前没一个子级，初始化一个数组
            if (CollectionUtils.isEmpty(list)){
                list =new ArrayList<>();
            }
            root.setChildren(list);
            return root;
        }).collect(Collectors.toList());
        subList =subList.stream().map(s->{
            List<PatrolStandardItems> list = subLists.stream().filter(l-> l.getParentId().equals(s.getId())).collect(Collectors.toList());
            s.setChildren(list);
            return s;
        }).collect(Collectors.toList());
        return rooList;
    }

    @Override
    public Boolean check(Integer order, String parentId, String id) {
        List<PatrolStandardItems> allList = baseMapper.selectItemList(id);
        List<PatrolStandardItems> rooList = allList.stream().filter(r -> r.getParentId().equals(parentId)).collect(Collectors.toList());
        for (PatrolStandardItems r:rooList) {
            if (r.getOrder().equals(order)){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Tree<String>> getTaskPoolList(String id) {
        //查询这个标准表的未删除的检查项,传巡检任务标准关联表id
        LambdaQueryWrapper<PatrolTaskDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolTaskDevice::getTaskId,id);
        //查询标准表id
        PatrolTaskDevice patrolTaskDevice = patrolTaskDeviceMapper.selectOne(queryWrapper);
        //获取标准表的检查项
        List<PatrolStandardItems> patrolStandardItemsList = patrolStandardItemsMapper.getList(patrolTaskDevice.getTaskStandardId());
        List<PatrolStandardItemsDTO> list = CollUtil.newArrayList();
        //形成树形结构
        patrolStandardItemsList.stream().forEach(e->list.add(new PatrolStandardItemsDTO(e.getId(),e.getContent(),e.getParentId())));
        TreeNodeConfig config = new TreeNodeConfig();
        config.setIdKey("id");
        config.setDeep(2);
        List<Tree<String>> treeList = TreeUtil.build(list,"0",config,(node,tree) ->{
           tree.setId(node.getId()).toString();
           tree.setName(node.getName());
           tree.setParentId(node.getPid()).toString();
        });
         return treeList;
    }

    @Override
    public List<SysDictDTO> querySysDict(Integer modules) {
        List<SysDictDTO> list =baseMapper.querySysDict(modules);
        return list;
    }

    @Override
    public void checkCode(String code, String standardId) {
        List<PatrolStandardItems> patrolStandardItems = baseMapper.selectList(
                 new LambdaQueryWrapper<PatrolStandardItems>()
                              .eq(PatrolStandardItems::getStandardId,standardId)
                              .eq(PatrolStandardItems::getCode,code));
        if (CollUtil.isNotEmpty(patrolStandardItems)){
         throw new AiurtBootException("输入的code当前列表重复,请重新输入");
        }
    }
}
