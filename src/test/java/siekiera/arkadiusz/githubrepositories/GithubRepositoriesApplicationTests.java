package siekiera.arkadiusz.githubrepositories;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GithubRepositoriesApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetRepositories_forUserAllegro_isNotEmptyList() throws Exception {
        mockMvc.perform(get("/repos/allegro").accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$", Matchers.not(Matchers.empty()))
            );
    }

    @Test
    void testGetRepositories_forNonExistingUser_returns404() throws Exception {
        mockMvc.perform(get("/repos/_"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserStars_forUserAllegro_isGraterThanZero() throws Exception {
        String username = "allegro";
        mockMvc.perform(get(String.format("/repos/%s/stars", username)).accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.username").value(username),
                jsonPath("$.stars", Matchers.greaterThan(0))
            );
    }

    @Test
    void testGetUserStars_forNonExistingUser_returns404() throws Exception {
        mockMvc.perform(get("/repos/_/stars"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetMostPopularLanguages_forNonExistingUser_returns404() throws Exception {
        mockMvc.perform(get("/repos/_/favourite-languages"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetMostPopularLanguages_forUser_isNotEmptyListContainingLanguageAndBytesGraterThan0() throws Exception {
        mockMvc.perform(get("/repos/siekiera-a/favourite-languages").accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$", Matchers.not(Matchers.empty())),
                jsonPath("$[0].bytes", Matchers.greaterThan(0)),
                jsonPath("$[0].language", Matchers.not(Matchers.blankOrNullString()))
            );
    }

}
