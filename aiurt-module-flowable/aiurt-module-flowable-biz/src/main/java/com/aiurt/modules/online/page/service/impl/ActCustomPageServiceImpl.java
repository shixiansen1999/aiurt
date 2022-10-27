package com.aiurt.modules.online.page.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtNoDataException;
import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.aiurt.modules.online.page.mapper.ActCustomPageMapper;
import com.aiurt.modules.online.page.service.IActCustomPageService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
}
