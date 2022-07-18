package com.aiurt.config.mybatis.filter;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/7/1417:25
 */
@Builder
@Data
public class DataScopeParam extends PermissionFilter {

    private String sql;

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 需要追加的sql
     *
     * @return
     */
    @Override
    public String getSql() {
        return StrUtil.isNotEmpty(sql) ? sql : "";
    }

}
