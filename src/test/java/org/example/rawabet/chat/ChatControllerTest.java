package org.example.rawabet.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rawabet.chat.controllers.ChatController;
import org.example.rawabet.chat.dto.ChatSessionResponseDTO;
import org.example.rawabet.chat.services.interfaces.IChatSessionService;
import org.example.rawabet.security.JwtFilter;
import org.example.rawabet.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ChatController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IChatSessionService chatSessionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private ChatSessionResponseDTO sessionDTO;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2)).doFilter(
                    inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        sessionDTO = ChatSessionResponseDTO.builder()
                .id(1L)
                .seanceId(10L)
                .name("Inception — 20:30")
                .code("ABC123")
                .active(true)
                .startTime(LocalDateTime.of(2024, 6, 1, 20, 30))
                .endTime(LocalDateTime.of(2024, 6, 1, 22, 30))
                .createdAt(LocalDateTime.of(2024, 6, 1, 20, 0))
                .build();
    }

    @Test
    void joinChat_returnsSession() throws Exception {
        when(chatSessionService.getByCode("ABC123")).thenReturn(sessionDTO);

        mockMvc.perform(get("/chat/join/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.name").value("Inception — 20:30"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void isActive_returnsTrue() throws Exception {
        when(chatSessionService.isChatActive("ABC123")).thenReturn(true);

        mockMvc.perform(get("/chat/active/ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void isActive_returnsFalse() throws Exception {
        when(chatSessionService.isChatActive("XYZ999")).thenReturn(false);

        mockMvc.perform(get("/chat/active/XYZ999"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getActiveSessionBySeance_returnsSession() throws Exception {
        when(chatSessionService.getActiveSessionBySeanceId(10L)).thenReturn(Optional.of(sessionDTO));

        mockMvc.perform(get("/chat/session/seance/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seanceId").value(10))
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    void getActiveSessionBySeance_returnsNoContent_whenAbsent() throws Exception {
        when(chatSessionService.getActiveSessionBySeanceId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/chat/session/seance/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void restartChat_returnsCreatedSession() throws Exception {
        when(chatSessionService.createChatSession(eq(10L), eq("Inception — 20:30"), eq(90)))
                .thenReturn(sessionDTO);

        mockMvc.perform(post("/chat/session/10/restart")
                        .param("name", "Inception — 20:30")
                        .param("durationMinutes", "90"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Inception — 20:30"));
    }

    @Test
    void restartChat_usesDefaultDuration_whenNotProvided() throws Exception {
        when(chatSessionService.createChatSession(eq(10L), eq("Test"), eq(120)))
                .thenReturn(sessionDTO);

        mockMvc.perform(post("/chat/session/10/restart")
                        .param("name", "Test"))
                .andExpect(status().isOk());
    }

    @Test
    void closeSession_returnsClosedSession() throws Exception {
        ChatSessionResponseDTO closed = ChatSessionResponseDTO.builder()
                .id(1L)
                .seanceId(10L)
                .name("Inception — 20:30")
                .code("ABC123")
                .active(false)
                .build();

        when(chatSessionService.closeSession(1L)).thenReturn(closed);

        mockMvc.perform(put("/chat/session/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void getAllSessions_returnsList() throws Exception {
        ChatSessionResponseDTO second = ChatSessionResponseDTO.builder()
                .id(2L)
                .seanceId(20L)
                .name("Dune — 19:00")
                .code("DEF456")
                .active(false)
                .build();

        when(chatSessionService.getAllSessions()).thenReturn(List.of(sessionDTO, second));

        mockMvc.perform(get("/chat/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("ABC123"))
                .andExpect(jsonPath("$[1].code").value("DEF456"));
    }
}
