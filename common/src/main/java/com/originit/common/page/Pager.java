package com.originit.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装分页的信息
 * @author xxc、
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pager<T> {

    /**
     * 数据
     */
    private List<T> data;

    /**
     * 数据总记录数
     */
    private Long total;
}
