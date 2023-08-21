package com.aiurt.modules.train.trainarchive.mapper;

import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.train.trainarchive.dto.TrainArchiveDTO;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: train_archive
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@EnableDataPerm
public interface TrainArchiveMapper extends BaseMapper<TrainArchive> {
    /**
     *  培训档案-分页列表查询
     * @param page 分页参数
     * @param trainArchiveDTO 查询参数
     * @return 分页列表
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "tr.org_code"),
            @DataColumn(key = "userName",value = "tr.user_id"),
    })
    List<TrainArchiveDTO> pageList(@Param("page") Page<TrainArchiveDTO> page, @Param("condition")TrainArchiveDTO trainArchiveDTO);

    /**
     * 培训档案-通过id查询
     * @param id 参数
     * @return 培训档案信息
     */
    TrainArchiveDTO queryById(@Param("id") String id);
}
