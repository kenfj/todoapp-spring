package com.example.demo.todos.controllers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.containsString;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// https://spring.io/guides/gs/testing-web/

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class TodoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll_ok() throws Exception {

        var actual = mockMvc
                .perform(get("/todos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        var expected = """
                [
                    {"id":1,"title":"Foo","completed":false},
                    {"id":2,"title":"Bar","completed":false},
                    {"id":3,"title":"Baz","completed":true}
                ]
                """;
        JSONAssert.assertEquals(expected, actual, STRICT);
    }

    @Test
    void findById_ok() throws Exception {

        mockMvc.perform(get("/todos/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Baz")));
    }

    @Test
    void findById_error() throws Exception {

        mockMvc.perform(get("/todos/123"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_error2() throws Exception {

        mockMvc.perform(get("/todos/asdf"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_ok() throws Exception {

        var postData = """
                {"id":null,"title":"Hoge","completed":false}\
                """;
        var expected = """
                {"id":4,"title":"Hoge","completed":false}\
                """;

        mockMvc
                .perform(post("/todos")
                        .contentType(APPLICATION_JSON)
                        .content(postData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    @SuppressWarnings("java:S5976")
    void post_error() throws Exception {

        var postData = """
                {"id":123,"title":"Hoge","completed":false}\
                """;
        var expected = """
                {"type":"about:blank",\
                "title":"Bad Request",\
                "status":400,\
                "detail":"Invalid create id: 123",\
                "instance":"/todos"}\
                """;

        testPostBadRequest(postData, expected);
    }

    @Test
    void post_validation_error() throws Exception {

        var postData = """
                {"id":null,"title":"a","completed":false}\
                """;
        var expected = """
                {\
                "type":"about:blank",\
                "title":"Bad Request",\
                "status":400,\
                "detail":"Validation error: [title: 'size must be between 2 and 10']",\
                "instance":"/todos",\
                "fieldErrors":[{\
                "field":"title",\
                "code":"Size",\
                "message":"size must be between 2 and 10"\
                }]\
                }\
                """;

        testPostBadRequest(postData, expected);
    }

    @Test
    void post_read_error() throws Exception {

        var postData = """
                {"id":null,"title":"aaa,"completed":false}\
                """;
        var expected = """
                {"type":"about:blank",\
                "title":"Bad Request",\
                "status":400,\
                "detail":"Failed to read request",\
                "instance":"/todos"}\
                """;

        testPostBadRequest(postData, expected);
    }

    private void testPostBadRequest(String postData, String expected) throws Exception {
        mockMvc
                .perform(post("/todos")
                        .contentType(APPLICATION_JSON)
                        .content(postData))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expected));
    }

    @Test
    @Transactional
    void put_ok() throws Exception {

        var input = """
                {"id":1,"title":"Hoge","completed":true}\
                """;
        var expected = """
                {"id":1,"title":"Hoge","completed":true}\
                """;

        mockMvc
                .perform(put("/todos/1")
                        .contentType(APPLICATION_JSON)
                        .content(input))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    void put_error() throws Exception {

        var input = """
                {"id":10,"title":"Hoge","completed":true}\
                """;

        mockMvc
                .perform(put("/todos/1")
                        .contentType(APPLICATION_JSON)
                        .content(input))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void patch_ok() throws Exception {

        var input = """
                {"title":"Hoge"}\
                """;
        var expected = """
                {"id":3,"title":"Hoge","completed":true}\
                """;

        mockMvc
                .perform(patch("/todos/3")
                        .contentType(APPLICATION_JSON)
                        .content(input))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    void patch_error() throws Exception {

        var input = """
                {"title":123}\
                """;
        var expected = """
                {"type":"about:blank","title":"Bad Request","status":400,\
                "detail":"argument type mismatch","instance":"/todos/3"}\
                """;

        mockMvc
                .perform(patch("/todos/3")
                        .contentType(APPLICATION_JSON)
                        .content(input))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expected));
    }

    @Test
    void patch_validation_error() throws Exception {

        var input = """
                {"title":"a"}\
                """;
        var expected = """
                {\
                "type":"about:blank",\
                "title":"Bad Request",\
                "status":400,\
                "detail":"title: size must be between 2 and 10",\
                "instance":"/todos/3",\
                "fieldErrors":[{\
                "field":"title",\
                "code":"{jakarta.validation.constraints.Size.message}",\
                "message":"size must be between 2 and 10"\
                }]\
                }\
                """;

        mockMvc
                .perform(patch("/todos/3")
                        .contentType(APPLICATION_JSON)
                        .content(input))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expected));
    }

    @Test
    @Transactional
    void delete_ok() throws Exception {

        mockMvc.perform(delete("/todos/3"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void delete_error() throws Exception {

        var expected = """
                {"type":"about:blank","title":"Not Found","status":404,\
                "detail":"Invalid delete id: 123","instance":"/todos/123"}\
                """;

        mockMvc.perform(delete("/todos/123"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expected));
    }
}
