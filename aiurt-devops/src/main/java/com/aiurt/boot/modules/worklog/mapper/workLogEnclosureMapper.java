package com.aiurt.boot.modules.worklog.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.worklog.entity.workLogEnclosure;

import java.util.List;

/**
 * @Description: 日志附件
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface workLogEnclosureMapper extends BaseMapper<workLogEnclosure> {

    /**
     * 查询附件列表
     * @param id
     * @return
     */
    List<String> query(Integer id);

}
