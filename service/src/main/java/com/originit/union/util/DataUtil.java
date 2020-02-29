package com.originit.union.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtil {

    public static class MapBuilder<K,V> {
        private List<K> keys;
        private List<V> values;

        public MapBuilder() {
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        public MapBuilder<K,V> append(K key, V value) {
            keys.add(key);
            values.add(value);
            return this;
        }

        public Map<K,V> build () {
            Map<K,V> map = new HashMap<>(keys.size());
            for (int i = 0; i < keys.size(); i++) {
                map.put(keys.get(i),values.get(i));
            }
            return map;
        }
    }

    public static <K,V> MapBuilder<K,V> mapBuilder() {
        return new MapBuilder<>();
    }
}
