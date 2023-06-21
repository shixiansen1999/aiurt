package search.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @author
 * @description 搜索记录
 */
@ApiModel(value = "搜索记录", description = "搜索记录")
@Data
public class SearchRecordResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 记录内容
     */
    @Excel(name = "记录内容", width = 15)
    @ApiModelProperty(value = "记录内容")
    private String keyword;
}
