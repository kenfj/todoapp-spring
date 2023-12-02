package com.example.demo.todos.models;

import static com.example.demo.utils.StringUtils.camelToSnake;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.MultiValueMap;

public class MapParams {

    private final MultiValueMap<String, String> params;
    private final Map<String, String> operators = Map.of(
            "_lte", "<=",
            "_gte", ">=",
            "_like", "LIKE",
            "_in", "IN");

    public MapParams(MultiValueMap<String, String> params) {
        this.params = params;
    }

    private Stream<String> paramKeys() {
        return params.keySet().stream().sorted()
                // skip PageSortParams
                .filter(key -> !key.startsWith("_"));
    }

    public boolean requireSqlQuery() {
        return paramKeys()
                // check if _lte _gte exist or key has multiple values
                .anyMatch(key -> key.contains("_")
                        || params.get(key).size() > 1);
    }

    public String toWhere() {
        return paramKeys()
                .map(this::columnSql)
                .collect(joining(" AND "));
    }

    private String columnSql(String key) {
        var keySize = params.get(key).size();
        var range = IntStream.range(0, keySize).boxed();
        // id_gte=1&id_lte=10 should use AND
        // but id=1&id=10 should use OR
        var delimiter = key.contains("_") ? " AND " : " OR ";

        return range.map(i -> {
            var col = key.contains("_") ? key.split("_")[0] : key;
            var suffix = key.replace(col, "");

            var column = camelToSnake(col);
            var operator = operators.getOrDefault(suffix, "=");
            var placeholder = operator.equals("IN")
                    ? "(:" + key + i + ")"
                    : ":" + key + i;

            return column + " " + operator + " " + placeholder;
        })
                .collect(joining(delimiter, "(", ")"));
    }

    public SqlParameterSource toSqlParams() {
        var map = paramKeys()
                .flatMap(this::columnParams)
                .collect(toMap(Entry::getKey, Entry::getValue));

        return new MapSqlParameterSource(map);
    }

    private Stream<Entry<String, Object>> columnParams(String key) {
        var keySize = params.get(key).size();
        var range = IntStream.range(0, keySize).boxed();

        return range.map(i -> {
            var col = key.contains("_") ? key.split("_")[0] : key;
            var suffix = key.replace(col, "");
            var operator = operators.getOrDefault(suffix, "=");

            var val = params.get(key).get(i);

            // _in value is parsed as csv
            var value = operator.equals("IN")
                    ? Arrays.asList(val.split(",", -1))
                    : val;
            return Map.entry(key + i, value);
        });
    }
}
