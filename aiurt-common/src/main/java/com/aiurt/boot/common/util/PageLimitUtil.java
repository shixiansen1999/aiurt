package com.aiurt.boot.common.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 手动分页工具类
 * @Author km
 * @Date 2021/9/2 15:36
 * @Version 1.0
 */
@Data
public class PageLimitUtil<T> {

    @ApiModelProperty("当前页")
    private int current;
    @ApiModelProperty("每页数量")
    private int size;
    @ApiModelProperty("是否分页")
    private boolean limitByPage;
    @ApiModelProperty("总条数")
    private int total;
    @ApiModelProperty("总页数")
    private int pages;
    /**
     * 分页数据，放入全部数据，返回分页数据（如果不分页则返回全部）
     */
    @ApiModelProperty("分页后的数据")
    private List<T> records;

    public PageLimitUtil(Integer pageNum, Integer pageSize, Boolean limitByPage, List<T> records) {
        if(pageNum == null || pageSize == null || limitByPage == null || records == null){
            throw new RuntimeException("【手动分页工具】检查参数是否正确");
        }
        if(pageNum < 1 || pageSize <1){
            throw new RuntimeException("【手动分页工具】pageNum or pageSize 不能小于1");
        }
        this.current = pageNum;
        this.size = pageSize;
        this.limitByPage = limitByPage;
        this.total = records.size();
        this.records = records;
        limitByPage();
    }

    private void limitByPage(){
        if(!limitByPage){
            return;
        }
        List<T> response = new ArrayList<>();
        //看看数据够不够
        if((current-1) * size <= total){
            int count = 1;
            for( int i = 0 ; i<records.size();i++){
                if(((current-1) * size -1 + count) < total &&  count <= size){
                    response.add(records.get(((current-1) * size -1 + count)));
                    count++;
                }else{
                    break;
                }
            }
        }
        if(total%size>0){
            this.pages = total/size+1;
        }else{
            this.pages = total/size;
        }
        this.records = response;
    }
}
