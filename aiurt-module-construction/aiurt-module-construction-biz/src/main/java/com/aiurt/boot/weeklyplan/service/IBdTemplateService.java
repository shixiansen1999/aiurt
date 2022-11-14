package com.aiurt.boot.weeklyplan.service;

import com.aiurt.boot.weeklyplan.entity.BdTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

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
