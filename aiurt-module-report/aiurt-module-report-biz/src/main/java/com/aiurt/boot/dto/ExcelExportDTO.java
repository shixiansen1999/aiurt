package com.aiurt.boot.dto;

import com.aiurt.boot.entity.HeadInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Excel导出DTO对象
 */
@Data
public class ExcelExportDTO implements Serializable {

    private static final long serialVersionUID = 8958746611747526363L;
    /**
     * 请求数据的地址(全路径)
     */
    @NotNull(message = "请求数据的地址不能为空！")
    @ApiModelProperty(value = "请求数据的地址(全路径)")
    private String dataUrl;
    /**
     * 请求方法类型(GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE)
     */
    @NotNull(message = "请求方法类型不能为空！")
    @ApiModelProperty(value = "请求方法类型(GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE)")
    private String method;
    /**
     * token
     */
    @NotNull(message = "token不能为空！")
    @ApiModelProperty(value = "token")
    private String token;
    /**
     * POST请求参数体(Json格式化的字符串)
     */
    @ApiModelProperty(value = "POST请求参数体(Json格式化的字符串)")
    private String reqBody;
    /**
     * 服务的网关前缀地址
     */
    @ApiModelProperty(value = "服务的网关前缀地址")
    private String gateWayUrl;
    /**
     * Excel名称
     */
    @ApiModelProperty(value = "Excel名称")
    private String excelName;
    /**
     * sheet名称
     */
    @ApiModelProperty(value = "sheet名称")
    private String sheetName;

    /**
     * Excel表头信息
     */
    @NotEmpty(message = "Excel表头信息不能为空！")
    @ApiModelProperty(value = "Excel表头信息")
    private List<HeadInfo> headInfos;
}
