package com.aiurt.boot.weeklyplan.mapper;

import com.aiurt.boot.weeklyplan.entity.BdSite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @Description: 工区表，存储工区包含工作场所及对应工班信息
 * @Author: wgp
 * @Date:   2021-03-31
 * @Version: V1.0
 */
@Component
public interface BdSiteMapper extends BaseMapper<BdSite> {
}
