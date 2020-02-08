package com.originit.union.business.bean;

import lombok.Data;

import java.util.List;

/**
 * @author super 素材列表
 * @date 2020/2/4 17:21
 * @description 执念
 */
@Data
public class MaterialListBean {
    private  int  total_count;
    private  int  item_count;
    private  List<MaterialItemBean> materialItemBeans;
}
