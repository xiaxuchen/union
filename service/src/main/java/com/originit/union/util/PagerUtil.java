package com.originit.union.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.common.page.Pager;

import java.util.stream.Collectors;

public class PagerUtil {

    /**
     * 简化转换操作
     */
    public static <T> Pager<T> fromIPage(IPage<T> iPage) {
        return new Pager<>(iPage.getRecords(), iPage.getTotal());
    }

    /**
     * 创建Pager的时候同时进行转换
     */
    public static <T,R> Pager<T> fromIPage(IPage<R> iPage,Convert<T,R> convert) {
        return new Pager<>(iPage.getRecords().stream().map(convert::convert).collect(Collectors.toList()), iPage.getTotal());
    }

    /**
     * 创建page
     * @param curPage 当前页
     * @param pageSize 每页的size
     * @return page对象
     */
    public static Page createPage(Integer curPage, Integer pageSize) {
        if (curPage == null) {
            curPage = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        return new Page<>(curPage, pageSize);
    }

    public interface Convert<T,R> {
        T convert(R from);
    }
}
