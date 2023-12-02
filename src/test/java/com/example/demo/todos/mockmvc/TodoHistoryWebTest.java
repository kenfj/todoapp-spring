package com.example.demo.todos.mockmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.annotation.PostConstruct;

@SpringBootTest
@AutoConfigureMockMvc
class TodoHistoryWebTest {

    @Autowired
    private MockMvc mockMvc;

    // fix UTF-8 issue https://stackoverflow.com/questions/58525387
    @Autowired
    private WebApplicationContext wac;

    @PostConstruct
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }

    @Test
    void findAll_ok() throws Exception {

        // jsonPath with list of object
        // https://stackoverflow.com/questions/46885972
        mockMvc
                .perform(get("/todo-histories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].queryType").value("CREATE"))
                .andExpect(jsonPath("$[0].todoId").value(1))
                .andExpect(jsonPath("$[0].title").value("foo"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].queryType").value("CREATE"))
                .andExpect(jsonPath("$[2].todoId").value(3))
                .andExpect(jsonPath("$[2].title").value("baz"))
                .andExpect(jsonPath("$[2].completed").value(false))
                .andExpect(jsonPath("$[5].id").value(6))
                .andExpect(jsonPath("$[5].queryType").value("DELETE"))
                .andExpect(jsonPath("$[5].todoId").value(4))
                .andExpect(jsonPath("$[5].title").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[5].completed").value(IsNull.nullValue()))
                .andReturn();
    }

    @Test
    void findAll_filter() throws Exception {

        var url = "/todo-histories?_page=1&_limit=3&_sort=title&_order=desc&_sort=completed";

        mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].queryType").value("CREATE"))
                .andExpect(jsonPath("$.content[0].todoId").value(1))
                .andExpect(jsonPath("$.content[0].title").value("foo"))
                .andExpect(jsonPath("$.content[0].completed").value(false))
                .andExpect(jsonPath("$.content[2].queryType").value("UPDATE"))
                .andExpect(jsonPath("$.content[2].todoId").value(3))
                .andExpect(jsonPath("$.content[2].title").value("baz"))
                .andExpect(jsonPath("$.content[2].completed").value(true))
                .andReturn();
    }
}
