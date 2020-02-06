package com.originit.union.WXbussiness.bean;

/**
 * @author super
 * @date 2020/2/2 16:26
 * @description 执念
 */
public class TagListBean {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagListBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "TagListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
