package com.originit.common.datastruct;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SortedMap<K,V> extends LinkedHashMap<K,V> {

    private boolean modified;

    @Override
    public V put(K key, V value) {
        modified = true;
        return super.put(key, value);
    }


}
