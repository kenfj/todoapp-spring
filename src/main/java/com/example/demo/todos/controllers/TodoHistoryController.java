package com.example.demo.todos.controllers;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.todos.models.FilterParams;
import com.example.demo.todos.models.MapParams;
import com.example.demo.todos.models.PageSortParams;
import com.example.demo.todos.models.TodoHistory;
import com.example.demo.todos.services.TodoHistoryService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Nullable;

@RestController
@RequestMapping("/todo-histories")
public class TodoHistoryController {

    private TodoHistoryService service;

    public TodoHistoryController(TodoHistoryService service) {
        this.service = service;
    }

    private static final String PAGE_SORT_EXAMPLE = """
            {"_page":1,"_limit":3,"_sort":["queryType","completed"],"_order":["desc"]}
            """;
    private static final String FILTER_EXAMPLE = """
            {"title":"foo"}
            """;

    // @Nullable is to make the param optional (not required) in swagger ui
    // https://stackoverflow.com/questions/67297184
    @GetMapping
    public Iterable<TodoHistory> findAll(
            @Nullable @Parameter(example = PAGE_SORT_EXAMPLE) PageSortParams pageSortParams,
            @Nullable @Parameter(example = FILTER_EXAMPLE) FilterParams filterParams,
            @Nullable @Parameter(example = "{}") @RequestParam MultiValueMap<String, String> multiValueMap) {

        if (multiValueMap != null && multiValueMap.isEmpty())
            return service.findAll();

        var mapParams = new MapParams(multiValueMap);
        return service.findAll(pageSortParams, filterParams, mapParams);
    }

    @GetMapping("/{id}")
    public TodoHistory findById(@PathVariable Long id) {
        return service.findById(id).orElseThrow();
    }
}
