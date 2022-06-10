package com.aiurt.boot.modules.system.service.impl;

import com.aiurt.boot.modules.system.entity.SysDictItem;
import com.aiurt.boot.modules.system.service.ISysDictItemService;
import com.aiurt.boot.modules.system.mapper.SysDictItemMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public List<SysDictItem> selectByDictCode(String code) {
        return sysDictItemMapper.selectByDictCode(code);
    }
}
