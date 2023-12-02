package com.example.demo.todos.mockmvc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jakarta.annotation.PostConstruct;

// https://spring.io/guides/gs/testing-web/

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class TodoWebTest {

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

        var actual = mockMvc
                .perform(get("/todos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        var expected = """
                [
                    {"id":1,"title":"Foo","completed":false,
                    "createdAt":"2023-08-31T15:00:00Z","updatedAt":"2023-09-01T15:00:00Z"},
                    {"id":2,"title":"Bar","completed":false,
                    "createdAt":"2023-09-02T15:00:00Z","updatedAt":"2023-09-03T15:00:00Z"},
                    {"id":3,"title":"Baz","completed":true,
                    "createdAt":"2023-09-04T15:00:00Z","updatedAt":"2023-09-05T15:00:00Z"}
                ]
                """;
        JSONAssert.assertEquals(expected, actual, STRICT);
    }

    @Test
    void findById_ok() throws Exception {

        var actual = mockMvc.perform(get("/todos/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        var expected = """
                {"id":3,"title":"Baz","completed":true,
                "createdAt":"2023-09-04T15:00:00Z","updatedAt":"2023-09-05T15:00:00Z"}
                """;
        ;

        JSONAssert.assertEquals(expected, actual, STRICT);
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
        var expectedTitle = "Hoge";
        var expectedCompleted = false;

        mockMvc
                .perform(post("/todos")
                        .contentType(APPLICATION_JSON)
                        .content(postData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.title").value(expectedTitle))
                .andExpect(jsonPath("$.completed").exists())
                .andExpect(jsonPath("$.completed").value(expectedCompleted))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
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
                "detail":"Validation error: [title: 'size must be between 2 and 30']",\
                "instance":"/todos",\
                "fieldErrors":[{\
                "field":"title",\
                "code":"Size",\
                "message":"size must be between 2 and 30"\
                }]\
                }\
                """;

        testPostBadRequest(postData, expected);
    }

    @Test
    void post_validation_ja_error() throws Exception {

        var postData = """
                {"id":null,"title":"a","completed":false}\
                """;
        var expected = """
                {\
                "type":"about:blank",\
                "title":"Bad Request",\
                "status":400,\
                "detail":"Validation error: [title: '2 から 30 の間のサイズにしてください']",\
                "instance":"/todos",\
                "fieldErrors":[{\
                "field":"title",\
                "code":"Size",\
                "message":"2 から 30 の間のサイズにしてください"\
                }]\
                }\
                """;

        var locale = new Locale("ja", "JP");
        testPostBadRequestLocale(postData, expected, locale);
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
                "detail":"Failed to read request: JSON parse error: \
                Unexpected character ('c' (code 99)): was expecting comma to separate Object entries",\
                "instance":"/todos"}\
                """;

        testPostBadRequest(postData, expected);
    }

    private void testPostBadRequest(String postData, String expected) throws Exception {
        var defaultLocale = new Locale("en", "US");
        testPostBadRequestLocale(postData, expected, defaultLocale);
    }

    private void testPostBadRequestLocale(String postData, String expected, Locale locale) throws Exception {
        mockMvc
                .perform(post("/todos")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag())
                        .content(postData))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expected));
    }

    @Test
    @Transactional
    void put_ok() throws Exception {

        var putData = """
                {"id":1,"title":"Hoge","completed":true}\
                """;
        var expectedId = 1L;
        var expectedTitle = "Hoge";
        var expectedCompleted = true;
        var expectedCreatedAt = "2023-08-31T15:00:00Z";

        mockMvc
                .perform(put("/todos/1")
                        .contentType(APPLICATION_JSON)
                        .content(putData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.title").value(expectedTitle))
                .andExpect(jsonPath("$.completed").exists())
                .andExpect(jsonPath("$.completed").value(expectedCompleted))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
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

        var patchData = """
                {"title":"Hoge"}\
                """;
        var expectedId = 3L;
        var expectedTitle = "Hoge";
        var expectedCompleted = true;
        var expectedCreatedAt = "2023-09-04T15:00:00Z";

        mockMvc
                .perform(patch("/todos/3")
                        .contentType(APPLICATION_JSON)
                        .content(patchData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.title").value(expectedTitle))
                .andExpect(jsonPath("$.completed").exists())
                .andExpect(jsonPath("$.completed").value(expectedCompleted))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
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
                "detail":"title: size must be between 2 and 30",\
                "instance":"/todos/2",\
                "fieldErrors":[{\
                "field":"title",\
                "code":"{jakarta.validation.constraints.Size.message}",\
                "message":"size must be between 2 and 30"\
                }]\
                }\
                """;

        mockMvc
                .perform(patch("/todos/2")
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
