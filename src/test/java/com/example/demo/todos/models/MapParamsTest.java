package com.example.demo.todos.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

class MapParamsTest {

    @Test
    void testContainsInequality() {

        var map = Map.of(
                "_page", List.of("1"),
                "id_gte", List.of("3"),
                "title", List.of("foo"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(map);
        var mapParams = new MapParams(multiValueMap);

        var expected = true;
        var actual = mapParams.requireSqlQuery();

        assertEquals(expected, actual);
    }

    @Test
    void testToWhere() {

        var map = Map.of(
                "_page", List.of("1"),
                "id_gte", List.of("3"),
                "title", List.of("foo"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(map);
        var mapParams = new MapParams(multiValueMap);

        var expected = "(id >= :id_gte0) AND (title = :title0)";
        var actual = mapParams.toWhere();

        assertEquals(expected, actual);
    }

    @Test
    void testToWhere2() {

        var map = Map.of(
                "_page", List.of("1"),
                "id_gte", List.of("3"),
                "id_lte", List.of("5"),
                "title", List.of("foo"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(map);
        var mapParams = new MapParams(multiValueMap);

        var expected = "(id >= :id_gte0) AND (id <= :id_lte0) AND (title = :title0)";
        var actual = mapParams.toWhere();

        assertEquals(expected, actual);
    }

    @Test
    void testToWhere3() {

        var map = Map.of(
                "_page", List.of("1"),
                "id", List.of("3", "5"),
                "title", List.of("foo"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(map);
        var mapParams = new MapParams(multiValueMap);

        var expected = "(id = :id0 OR id = :id1) AND (title = :title0)";
        var actual = mapParams.toWhere();

        assertEquals(expected, actual);
    }

    @Test
    void testToSqlParams() {

        var map = Map.of(
                "_page", List.of("1"),
                "id_gte", List.of("3"),
                "title", List.of("foo"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(map);
        var mapParams = new MapParams(multiValueMap);

        var actual = mapParams.toSqlParams();
        assertEquals("3", actual.getValue("id_gte0"));
        assertEquals("foo", actual.getValue("title0"));

        var actualParamNames = Set.of(actual.getParameterNames());
        var expectedParamNames = Set.of("id_gte0", "title0");
        assertEquals(expectedParamNames, actualParamNames);
    }

    @Test
    void testToSqlParams3() {

        var map = Map.of(
                "_page", List.of("1"),
                "id", List.of("3", "5"),
                "title", List.of("foo"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(map);
        var mapParams = new MapParams(multiValueMap);

        var actual = mapParams.toSqlParams();
        assertEquals("3", actual.getValue("id0"));
        assertEquals("5", actual.getValue("id1"));
        assertEquals("foo", actual.getValue("title0"));

        var actualParamNames = Set.of(actual.getParameterNames());
        var expectedParamNames = Set.of("id0", "id1", "title0");
        assertEquals(expectedParamNames, actualParamNames);
    }
}
