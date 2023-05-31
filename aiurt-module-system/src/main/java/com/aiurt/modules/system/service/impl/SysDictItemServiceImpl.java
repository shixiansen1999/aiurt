package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.system.entity.SysDictItem;
import com.aiurt.modules.system.mapper.SysDictItemMapper;
import com.aiurt.modules.system.mapper.SysDictMapper;
import com.aiurt.modules.system.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper sysDictItemMapper;
    @Autowired
    private SysDictMapper sysDictMapper;

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public List<DictModel> dictByCode(String code) {
        return sysDictMapper.queryEnableDictItemsByCode(code);
    }
}
