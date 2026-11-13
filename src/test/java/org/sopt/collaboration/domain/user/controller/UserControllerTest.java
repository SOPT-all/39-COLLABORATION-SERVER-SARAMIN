package org.sopt.collaboration.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    private static final String URL = "/api/v1/users/me/documents";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("보유 서류 체크 변경 시 필드가 하나라도 빠지면 400을 응답한다")
    void 서류_필드가_빠지면_400을_응답한다() throws Exception {
        // given
        String body = """
                {"hasResume": true, "hasCoverLetter": true}
                """;

        // when & then
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("보유 서류 체크 변경 시 요청 본문이 없으면 400을 응답한다")
    void 요청_본문이_없으면_400을_응답한다() throws Exception {
        // when & then
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
