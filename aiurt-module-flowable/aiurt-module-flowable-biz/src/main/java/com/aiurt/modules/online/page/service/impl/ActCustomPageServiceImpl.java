package com.aiurt.modules.online.page.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtNoDataException;
import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.aiurt.modules.online.page.mapper.ActCustomPageMapper;
import com.aiurt.modules.online.page.service.IActCustomPageService;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description: 设计表单
 * @Author: aiurt
 * @Date:   2022-10-26
 * @Version: V1.0
 */
@Service
public class ActCustomPageServiceImpl extends ServiceImpl<ActCustomPageMapper, ActCustomPage> implements IActCustomPageService {

    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private ActCustomPageModuleServiceImpl actCustomPageModuleService;

    /**
     * 编辑菜单
     *
     * @param actCustomPage
     */
    @Override
    public void edit(ActCustomPage actCustomPage) {

        ActCustomPage page = getById(actCustomPage.getId());

        if (Objects.isNull(page)) {
            throw new AiurtBootException("不存在该记录，请刷新重试");
        }

        Integer pageVersion = Optional.ofNullable(page.getPageVersion()).orElse(1);
        // 修改版本号
        actCustomPage.setPageVersion(pageVersion +1);

        updateById(actCustomPage);
    }

    @Override
    public IPage<ActCustomPage> queryPageList(Page<ActCustomPage> page, ActCustomPage actCustomPage) {
        LambdaQueryWrapper<ActCustomPage> queryWrapper = new LambdaQueryWrapper<>();
        String sysOrgCode = actCustomPage.getSysOrgCode();
        if (StrUtil.isNotBlank(sysOrgCode)) {
            SysDepartModel sysDepartModel = sysBaseAPI.selectAllById(sysOrgCode);
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
        return Result.OK("添加成功");
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
