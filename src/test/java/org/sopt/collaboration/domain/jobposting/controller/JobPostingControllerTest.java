package org.sopt.collaboration.domain.jobposting.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobPostingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("마감 임박 공고 조회 시 size가 1 미만이면 400을 응답한다")
    void 마감_임박_공고_size가_1_미만이면_400을_응답한다() throws Exception {
        // given
        String size = "0";

        // when & then
        mockMvc.perform(get("/api/v1/job-postings/deadline-soon").param("size", size))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400));
    }
}
