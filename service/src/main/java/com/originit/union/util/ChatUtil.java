package com.originit.union.util;

import java.util.Collections;
import java.util.List;

public class ChatUtil {

    /**
     * 获取截取的列表的开始元素位置
     * @param count 截取多少个
     * @param size 列表的总大小
     * @return 列表的开始位置
     */
    public static long getFromSize (int count,long size) {
        if (count == -1 || count > size) {
            return 0;
        }
        return size - count;
    }

    public static int getFromSize (int count,int size) {
        if (count == -1 || count > size) {
            return 0;
        }
        return size - count;
    }

    public static <T> List<T> subList(List<T> list, int curPage, int userCount) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        } else {
            if (userCount == -1) {
                return list;
            }
            int start = (curPage - 1)*userCount;
            int end = start + userCount;
            if (end > list.size()) {
                end = list.size();
            }
            return list.subList(start,end);
        }
    }
}
