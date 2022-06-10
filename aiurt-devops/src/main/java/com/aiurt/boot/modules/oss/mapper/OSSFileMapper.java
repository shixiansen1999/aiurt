package com.aiurt.boot.modules.oss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.oss.entity.OSSFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OSSFileMapper extends BaseMapper<OSSFile> {

}
