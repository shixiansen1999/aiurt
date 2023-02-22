package com.aiurt.boot;

import com.aiurt.modules.search.dto.FileDataDTO;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;

public interface EsFileAPI {

    /**
     * 更新ES规程规范记录数据
     *
     * @param fileDataDTO
     * @return
     */
    UpdateResponse updateFileData(FileDataDTO fileDataDTO);

    /**
     * 添加ES规程规范记录数据
     *
     * @param fileDataDTO
     */
    IndexResponse saveFileData(FileDataDTO fileDataDTO);
}
