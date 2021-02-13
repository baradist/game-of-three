package cf.baradist.gameofthree;

import cf.baradist.gameofthree.event.StartedGameEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/game")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void test() throws Exception {
        String gameStartedContent = mockMvc.perform(post("/game")
                .contentType("application/json")
                .content("{ \"sum\": 11 }")
                .header("Player", "john")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", notNullValue()))
                .andReturn().getResponse().getContentAsString();
        StartedGameEvent joinedGameEvent = objectMapper.readValue(gameStartedContent, StartedGameEvent.class);
        String gameId = joinedGameEvent.getGameId();

        mockMvc.perform(get("/game")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(gameId)))
                .andExpect(jsonPath("[0].player1", is("john")))
                .andExpect(jsonPath("[0].player2", nullValue()))
                .andExpect(jsonPath("[0].nextTurn", nullValue()))
                .andExpect(jsonPath("[0].sum", is(11)))
                .andExpect(jsonPath("[0].finished", is(false)))
                .andExpect(jsonPath("[0].winner", nullValue()));

        mockMvc.perform(put("/game")
                .contentType("application/json")
                .content("{ \"gameId\": \"" + gameId + "\" }")
                .header("Player", "mary")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(gameId)))
                .andExpect(jsonPath("$.playerId", is("mary")));

        mockMvc.perform(get("/game")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("[0].player1", is("john")))
                .andExpect(jsonPath("[0].player2", is("mary")))
                .andExpect(jsonPath("[0].nextTurn", is("mary")));

        mockMvc.perform(post("/game/" + gameId + "/move")
                .contentType("application/json")
                .content("{\"gameId\": \"" + gameId + "\", \"number\": 0, \"action\": 1}")
                .header("Player", "mary")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(4)))
                .andExpect(jsonPath("$.finished", is(false)));

        mockMvc.perform(post("/game/" + gameId + "/move")
                .contentType("application/json")
                .content("{\"gameId\": \"" + gameId + "\", \"number\": 0, \"action\": -1}")
                .header("Player", "john")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(1)))
                .andExpect(jsonPath("$.finished", is(true)));

        mockMvc.perform(get("/game")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("[0].sum", is(1)))
                .andExpect(jsonPath("[0].finished", is(true)))
                .andExpect(jsonPath("[0].winner", is("john")));
    }
}
