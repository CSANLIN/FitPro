package com.fitness.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private List<T> list;
    private long total;
    private long pageNum;
    private long pageSize;

    private PageResult(List<T> list, long total, long pageNum, long pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(
            page.getRecords(),
            page.getTotal(),
            page.getCurrent(),
            page.getSize()
        );
    }

    public static <T> PageResult<T> of(List<T> list, IPage<?> page) {
        return new PageResult<>(
            list,
            page.getTotal(),
            page.getCurrent(),
            page.getSize()
        );
    }
}
