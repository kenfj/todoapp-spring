package com.example.demo.utils;

import java.util.HashMap;
import java.util.Map;

// MapからRecordへの変換
// https://zenn.dev/minor/articles/e210825aa08d36

// c.f. https://stackoverflow.com/questions/29988841/spring-rest-and-patch-method

public class RecordUtil {

    private RecordUtil() {
        super();
    }

    public static <R extends Record> R patchRecord(Class<R> cls, R rec, Map<String, Object> fields)
            throws ReflectiveOperationException {

        var map = record2Map(cls, rec);

        fields.forEach(map::put);

        return map2Record(cls, map);
    }

    public static <R extends Record> R map2Record(Class<R> cls, Map<String, Object> map)
            throws ReflectiveOperationException {

        var rc = cls.getRecordComponents();
        var types = new Class<?>[rc.length];
        var args = new Object[rc.length];

        for (int i = 0; i < rc.length; i++) {
            types[i] = rc[i].getType();
            args[i] = map.get(rc[i].getName());
        }

        return cls.getDeclaredConstructor(types).newInstance(args);
    }

    public static <R extends Record> Map<String, Object> record2Map(Class<R> cls, R rec)
            throws ReflectiveOperationException {

        var rcs = cls.getRecordComponents();
        var map = new HashMap<String, Object>();

        for (var rc : rcs) {
            var type = rc.getType();
            var name = rc.getName();
            var val = rc.getAccessor().invoke(rec);

            map.put(name, type.cast(val));
        }

        return map;
    }
}
