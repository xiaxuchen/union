package com.originit.union.WXbussiness.bean;

import lombok.Data;

import java.util.List;

/**
 * @author super
 * @date 2020/2/4 17:21
 * @description 执念
 */
@Data
public class MaterialListBean {
    private  int  total_count;
    private  int  item_count;
    private  List<MaterialItemBean> materialItemBeans;
}
