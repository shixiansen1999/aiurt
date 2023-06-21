package search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: search_records
 * @Author: aiurt
 * @Date: 2023-5-25
 * @Version: V1.0
 */
@Data
@TableName("search_records")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "search_records", description = "search_records")
public class SearchRecords implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 执行搜索的用户ID
     */
    @Excel(name = "执行搜索的用户ID", width = 15)
    @ApiModelProperty(value = "执行搜索的用户ID")
    private String userId;
    /**
     * 搜索关键词
     */
    @Excel(name = "搜索关键词", width = 15)
    @ApiModelProperty(value = "搜索关键词")
    private String keyword;
    /**
     * 搜索操作的时间
     */
    @Excel(name = "搜索操作的时间", width = 15)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "搜索操作的时间")
    private Date searchTime;
    /**
     * 搜索结果数量
     */
    @Excel(name = "搜索结果数量", width = 15)
    @ApiModelProperty(value = "搜索结果数量")
    private Integer resultCount;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 删除标准
     */
    @Excel(name = "删除标准", width = 15)
    @ApiModelProperty(value = "删除标准")
    private Integer delFlag;
}
