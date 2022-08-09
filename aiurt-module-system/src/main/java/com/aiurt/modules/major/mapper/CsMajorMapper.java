package com.aiurt.modules.major.mapper;


import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.major.entity.CsMajor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface CsMajorMapper extends BaseMapper<CsMajor> {

}
