package com.example.demo.utils;

import static com.example.demo.utils.RecordUtil.map2Record;
import static com.example.demo.utils.RecordUtil.patchRecord;
import static com.example.demo.utils.RecordUtil.record2Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

// MapからRecordへの変換
// https://zenn.dev/minor/articles/e210825aa08d36

record Account(String username, String domain, Integer count) {
}

class RecordUtilTest {

    @Test
    void test_patchRecord() throws ReflectiveOperationException {

        var account = new Account("foo", "example.com", 1);
        Map<String, Object> fields = Map.of("username", "bar");

        var actual = patchRecord(Account.class, account, fields);

        var expected = new Account("bar", "example.com", 1);
        assertEquals(expected, actual);
    }

    @Test
    void test_patchRecord2() throws ReflectiveOperationException {

        var account = new Account("foo", "example.com", 1);
        Map<String, Object> fields = Map.of("username", "bar", "count", 2);

        var actual = patchRecord(Account.class, account, fields);

        var expected = new Account("bar", "example.com", 2);
        assertEquals(expected, actual);
    }

    @Test
    void test_Map2Record() throws ReflectiveOperationException {

        Map<String, Object> map = Map.of(
                "username", "foo",
                "domain", "example.com",
                "count", 1);

        var account = map2Record(Account.class, map);

        var expected = new Account("foo", "example.com", 1);
        assertEquals(expected, account);
    }

    @Test
    void test_Record2Map() throws ReflectiveOperationException {

        var rec = new Account("foo", "example.com", 1);

        var map = record2Map(Account.class, rec);

        Map<String, Object> expected = Map.of(
                "username", "foo",
                "domain", "example.com",
                "count", 1);
        assertEquals(expected, map);
    }
}
