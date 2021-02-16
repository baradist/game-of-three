package cf.baradist.gameofthree;

import cf.baradist.gameofthree.dto.CreatGameResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    public static final String CONTENT_TYPE = "application/json";
    public static final String GAME_API = "/api/game";
    public static final String JOHN = "john";
    public static final String MARY = "mary";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test() throws Exception {
        String gameStartedContent = mockMvc.perform(post(GAME_API).with(user(JOHN))
                .contentType(CONTENT_TYPE)
                .content("{ \"sum\": 11 }"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", notNullValue()))
                .andReturn().getResponse().getContentAsString();
        CreatGameResultDto joinedGameEvent = objectMapper.readValue(gameStartedContent, CreatGameResultDto.class);
        String gameId = joinedGameEvent.getGameId();

        mockMvc.perform(get(GAME_API)
                .with(user(MARY)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", is(gameId)))
                .andExpect(jsonPath("[0].player1", is(JOHN)))
                .andExpect(jsonPath("[0].player2", nullValue()))
                .andExpect(jsonPath("[0].nextTurn", nullValue()))
                .andExpect(jsonPath("[0].sum", is(11)))
                .andExpect(jsonPath("[0].winner", nullValue()));

        mockMvc.perform(put(GAME_API).with(user(MARY))
                .contentType(CONTENT_TYPE)
                .content("{ \"gameId\": \"" + gameId + "\" }"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(gameId)))
                .andExpect(jsonPath("$.playerId", is(MARY)));

        mockMvc.perform(get(GAME_API + "/" + gameId).with(user(MARY)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.player1", is(JOHN)))
                .andExpect(jsonPath("$.player2", is(MARY)))
                .andExpect(jsonPath("$.nextTurn", is(MARY)));

        mockMvc.perform(get(GAME_API + "/current")
                .with(user(JOHN)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId)));

        mockMvc.perform(get(GAME_API + "/current")
                .with(user(JOHN)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId)));

        String moveUrl = GAME_API + "/" + gameId + "/move";
        mockMvc.perform(post(moveUrl).with(user(MARY))
                .contentType(CONTENT_TYPE)
                .content("{\"gameId\": \"" + gameId + "\", \"turnNumber\": 0, \"action\": 1}"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.nextSum", is(4)));

        mockMvc.perform(post(moveUrl).with(user(JOHN))
                .contentType(CONTENT_TYPE)
                .content("{\"gameId\": \"" + gameId + "\", \"turnNumber\": 1, \"action\": -1}"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.nextSum", is(1)));

        mockMvc.perform(get(GAME_API + "/" + gameId).with(user(JOHN)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(1)))
                .andExpect(jsonPath("$.winner", is(JOHN)));
    }
}
