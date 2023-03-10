package com.aiurt.boot.mapper;

import com.aiurt.modules.search.dto.FileDataDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileAnalysisMapper {

    @Select("select id, type_id, name, url as address, type as format from sys_file where del_flag = 0")
    List<FileDataDTO> syncCanonicalKnowledgeBase();
}
