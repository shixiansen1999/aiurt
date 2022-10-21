package com.aiurt.modules.weeklyplan.service;

import com.aiurt.modules.weeklyplan.entity.BdTemplate;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 周计划模板表
 * @Author: Lai W.
 * @Version: 1.0
 */
public interface IBdTemplateService extends IService<BdTemplate> {

    /**
     * 查询
     * @return
     */
    List<BdTemplate> queryAll();
}
