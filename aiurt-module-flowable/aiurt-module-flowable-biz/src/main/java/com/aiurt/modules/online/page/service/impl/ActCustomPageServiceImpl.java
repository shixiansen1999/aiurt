package com.aiurt.modules.online.page.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.mapper.ActCustomModelInfoMapper;
import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.aiurt.modules.online.page.entity.ActCustomPageField;
import com.aiurt.modules.online.page.mapper.ActCustomPageMapper;
import com.aiurt.modules.online.page.service.IActCustomPageFieldService;
import com.aiurt.modules.online.page.service.IActCustomPageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description: 设计表单
 * @Author: aiurt
 * @Date:   2022-10-26
 * @Version: V1.0
 */
@Service
public class ActCustomPageServiceImpl extends ServiceImpl<ActCustomPageMapper, ActCustomPage> implements IActCustomPageService {

    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ActCustomPageModuleServiceImpl actCustomPageModuleService;
    @Autowired
    private IActCustomPageFieldService actCustomPageFieldService;
    @Autowired
    private ActCustomModelInfoMapper actCustomModelInfoMapper;
    /**
     * 编辑菜单
     *
     * @param actCustomPage
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(ActCustomPage actCustomPage) {
        ActCustomPage page = getById(actCustomPage.getId());
        if (Objects.isNull(page)) {
            throw new AiurtBootException("不存在该记录，请刷新重试");
        }
      // 检查数据库中是否已存在具有相同name的记录
        if (isNameExists(actCustomPage.getPageName(), actCustomPage.getId())) {
            return Result.error("名称已存在，请使用其他名称！");
        }
        Integer pageVersion = Optional.ofNullable(page.getPageVersion()).orElse(1);
        // 修改版本号
        actCustomPage.setPageVersion(pageVersion +1);
        updateById(actCustomPage);
        //表单字段编辑
        String id = actCustomPage.getId();
        QueryWrapper<ActCustomPageField> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ActCustomPageField::getPageId, id);
        actCustomPageFieldService.remove(wrapper);
        List<ActCustomPageField> fieldList = actCustomPage.getFieldList();
        if (CollectionUtil.isNotEmpty(fieldList)) {
            fieldList.forEach(l -> {
                l.setPageId(id);
            });
            actCustomPageFieldService.saveBatch(fieldList);
        }
        return Result.OK("编辑成功");
    }

    @Override
    public IPage<ActCustomPage> queryPageList(Page<ActCustomPage> page, ActCustomPage actCustomPage) {
        LambdaQueryWrapper<ActCustomPage> queryWrapper = new LambdaQueryWrapper<>();
        String sysOrgCode = actCustomPage.getSysOrgCode();
        if (StrUtil.isNotBlank(sysOrgCode)) {
            SysDepartModel sysDepartModel = sysBaseApi.selectAllById(sysOrgCode);
            if (Objects.nonNull(sysDepartModel)) {
                actCustomPage.setSysOrgCode(sysDepartModel.getOrgCode());
            }

            queryWrapper.eq(ActCustomPage::getSysOrgCode, actCustomPage.getSysOrgCode());
        }
        queryWrapper.like(StrUtil.isNotBlank(actCustomPage.getPageName()),ActCustomPage::getPageName, actCustomPage.getPageName());
        //设置树形结构查询
        String pageModuleId = actCustomPage.getPageModule();
        List<SelectTreeModel> moduleTree = actCustomPageModuleService.getModuleTree(null);
        if (StrUtil.isNotEmpty(pageModuleId)) {
            List<String> pageModuleIds = getNodeAndDescendantsIds(moduleTree, pageModuleId);
            if(CollUtil.isNotEmpty(pageModuleIds)){
                queryWrapper.in(ActCustomPage::getPageModule,pageModuleIds);
            }
        }
        Page<ActCustomPage> actCustomPagePage = baseMapper.selectPage(page, queryWrapper);
        //关联表单字段查询
        List<ActCustomPage> records = actCustomPagePage.getRecords();
        // 收集所有表单的ID
        List<String> pageIds = new ArrayList<>();
        for (ActCustomPage record : records) {
            pageIds.add(record.getId());
        }
        // 批量查询所有记录的字段
        LambdaQueryWrapper<ActCustomPageField> query = new LambdaQueryWrapper<>();
        if(CollUtil.isNotEmpty(pageIds)){
            query.in(ActCustomPageField::getPageId, pageIds)
                    .eq(ActCustomPageField::getDelFlag, CommonConstant.DEL_FLAG_0);
        }
        List<ActCustomPageField> actCustomPageFields = actCustomPageFieldService.getBaseMapper().selectList(query);
       // 使用 Map 来组织字段列表，以便后续关联到相应的记录
        Map<String, List<ActCustomPageField>> pageIdToFieldsMap = new HashMap<>(32);
        for (ActCustomPageField field : actCustomPageFields) {
            String pageId = field.getPageId();
            pageIdToFieldsMap.computeIfAbsent(pageId, k -> new ArrayList<>()).add(field);
        }
        // 关联字段列表到相应的记录
        for (ActCustomPage record : records) {
            List<ActCustomPageField> fields = pageIdToFieldsMap.get(record.getId());
            if (CollUtil.isEmpty(fields)) {
                fields = Collections.emptyList();
            }
            record.setFieldList(fields);
        }
        actCustomPagePage.setRecords(records);
        return actCustomPagePage;
    }

    @Override
    public boolean isNameExists(String name, String id) {
        LambdaQueryWrapper<ActCustomPage> lam = new LambdaQueryWrapper<>();
        lam.eq(ActCustomPage::getPageName, name);
        if (StrUtil.isNotEmpty(id)) {
            lam.ne(ActCustomPage::getId, id);
        }
        Long count = baseMapper.selectCount(lam);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(ActCustomPage actCustomPage) {
        // 检查数据库中是否已存在具有相同名称的记录
        if (isNameExists(actCustomPage.getPageName(), null)) {
            return Result.error("名称已存在，请使用其他名称！");
        }
        actCustomPage.setPageVersion(1);
        // 设置表单标识为pageTag+时间戳后6位
        long timestamp = System.currentTimeMillis();
        String timestampString = String.valueOf(timestamp);
        String lastSixDigits = timestampString.substring(timestampString.length() - 6);
        actCustomPage.setPageTag(String.format("%s%s", "pageTag", lastSixDigits));
        baseMapper.insert(actCustomPage);
        //保存表单字段
        String id = actCustomPage.getId();
        List<ActCustomPageField> fieldList = actCustomPage.getFieldList();
        for (ActCustomPageField customPageField : fieldList) {
            customPageField.setPageId(id);
        }
        actCustomPageFieldService.saveBatch(fieldList);
    return Result.OK("添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteById(String id) {
        //如果模块被引用，则不可以删除
        LambdaQueryWrapper<ActCustomModelInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StrUtil.isNotEmpty(id)){
            lambdaQueryWrapper.eq(ActCustomModelInfo::getPageId,id).eq(ActCustomModelInfo::getDelFlag,CommonConstant.DEL_FLAG_0);
        }
        List<ActCustomModelInfo> pageCustomModule = actCustomModelInfoMapper.selectList(lambdaQueryWrapper);
        if(CollUtil.isNotEmpty(pageCustomModule)){
            throw new AiurtBootException("该表单已被引用，无法删除");
        }
        baseMapper.deleteById(id);
        return Result.OK("删除成功");
    }

    @Override
    public Result<String> deleteByIds(List<String> ids) {
        if(CollUtil.isNotEmpty(ids)){
            // 查找所有被引用的表单
            List<String> referencedIds = new ArrayList<>();
            for (String id : ids) {
                List<ActCustomModelInfo> pageCustomModule = actCustomModelInfoMapper.selectList(new QueryWrapper<ActCustomModelInfo>().eq("page_id", id).eq("del_flag",CommonConstant.DEL_FLAG_0));
                if (CollUtil.isNotEmpty(pageCustomModule)) {
                    referencedIds.add(id);
                }
            }
            // 如果有被引用的表单，抛出异常
            if (CollUtil.isNotEmpty(referencedIds)) {
                throw new AiurtBootException("有表单已被引用，无法删除!");
            }
            // 执行批量删除
            baseMapper.deleteBatchIds(ids);
        }
        return Result.OK("删除成功");
    }


    /**
     * 根据左侧树查询节点及子节点
     * @param nodes
     * @param pageModuleId
     * @return
     */
    public List<String> getNodeAndDescendantsIds(List<SelectTreeModel> nodes, String pageModuleId) {
        List<String> result = new ArrayList<>();
        SelectTreeModel selectTreeModel = findNodeById(nodes, pageModuleId);
        if (selectTreeModel != null) {
            result.add(selectTreeModel.getKey());
            addDescendantIds(result, selectTreeModel);
        }
        return result;
    }

    private SelectTreeModel findNodeById(List<SelectTreeModel> nodes, String pageModuleId) {
        for (SelectTreeModel node : nodes) {
            if (pageModuleId.equals(node.getKey())) {
                return node;
            } else if (node.getChildren() != null) {
                SelectTreeModel result = findNodeById(node.getChildren(), pageModuleId);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void addDescendantIds(List<String> result, SelectTreeModel node) {
        if (node.getChildren() != null) {
            for (SelectTreeModel child : node.getChildren()) {
                result.add(child.getKey());
                addDescendantIds(result, child);
            }
        }
    }
}
