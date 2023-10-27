package com.aiurt.modules.online.page.service;


import com.aiurt.modules.online.page.entity.ActCustomPageField;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: act_custom_page_field
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
public interface IActCustomPageFieldService extends IService<ActCustomPageField> {

    List<String> listPageFieldCode(String pageId);
}
