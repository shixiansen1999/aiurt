package com.aiurt.modules.recycle.service;

import com.aiurt.modules.recycle.entity.SysRecycle;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.sql.SQLException;

public interface ISysRecycleService extends IService<SysRecycle> {

    Result<String> restoreById(String id) throws SQLException;
}
