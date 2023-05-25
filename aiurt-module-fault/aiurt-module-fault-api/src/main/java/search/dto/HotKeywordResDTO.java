package search.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @author
 * @description 热门关键词
 */
@ApiModel(value = "热门关键词", description = "热门关键词")
@Data
public class HotKeywordResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * 记录内容
     */
    @Excel(name = "记录内容", width = 15)
    @ApiModelProperty(value = "记录内容")
    private String keyword;

    /**
     * 搜索次数
     */
    @Excel(name = "搜索次数", width = 15)
    @ApiModelProperty(value = "搜索次数")
    private Integer resultCount;
}
