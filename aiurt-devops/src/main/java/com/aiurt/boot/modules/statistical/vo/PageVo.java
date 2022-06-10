package com.aiurt.boot.modules.statistical.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: niuzeyu
 * @date: 2022年01月20日 13:05
 */
@Data
@Accessors(chain = true)
public class PageVo<T> implements Serializable {
    private Long current;
    private Long size;
    private Long pages;
    private Long total;
    private T records;

    public void setPage(IPage iPage) {
        this.setCurrent(iPage.getCurrent());
        this.setTotal(iPage.getTotal());
        this.setPages(iPage.getPages());
        this.setSize(iPage.getSize());
    }
}
