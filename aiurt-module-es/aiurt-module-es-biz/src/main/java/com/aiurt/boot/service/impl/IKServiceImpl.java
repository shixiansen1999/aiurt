package com.aiurt.boot.service.impl;

import com.aiurt.boot.constant.EsConstant;
import com.aiurt.boot.service.IKService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/1716:22
 */
@Service
@Slf4j
public class IKServiceImpl implements IKService {
    @Autowired
    private ISysBaseAPI sysBaseApi;

    /**
     * 验证当前是否是最新的head
     *
     * @return
     */
    @Override
    public String getCurrentNewModified() {
        String currentNewModified = sysBaseApi.getCurrentNewModified(EsConstant.IK_DICT_CODE);
        return currentNewModified;
    }

    /**
     * 获取分词
     *
     * @return
     */
    @Override
    public String getDict() {
        List<DictModel> dictItems = sysBaseApi.getDictItems(EsConstant.IK_DICT_CODE);
        return String.join("\n", dictItems.stream().map(DictModel::getTitle).collect(Collectors.toSet()));
    }
}
