package com.aiurt.boot.standard.service.impl;

import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.standard.service.IPatrolStandardItemsService;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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


    @Override
    public List<PatrolStandardItems> queryPageList() {
        //1.查询表中未删除的所有的数据
        List<PatrolStandardItems> allList = baseMapper.selectList();
        //2.找到所有根节点 ParentId=0
        List<PatrolStandardItems> rooList = allList.stream().filter(r -> r.getParentId().equals("0")).collect(Collectors.toList());
        //3.找到所有非根节点
        List<PatrolStandardItems> subLists = allList.stream().filter(r -> !r.getParentId().equals("0")).collect(Collectors.toList());
        List<PatrolStandardItems> subList = allList.stream().filter(r -> !r.getParentId().equals("0")).collect(Collectors.toList());
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
    public Boolean check(Integer order, String parentId) {
        List<PatrolStandardItems> allList = baseMapper.selectList();
        List<PatrolStandardItems> rooList = allList.stream().filter(r -> r.getParentId().equals(parentId)).collect(Collectors.toList());
        for (PatrolStandardItems r:rooList) {
            if (r.getOrder().equals(order)){
                return false;
            }
        }
        return true;
    }

}
