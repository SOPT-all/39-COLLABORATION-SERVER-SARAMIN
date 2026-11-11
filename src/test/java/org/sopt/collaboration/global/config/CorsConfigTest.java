package org.sopt.collaboration.global.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

class CorsConfigTest {

    private static final String PATH = "/v3/api-docs";

    private static ResultActions preflight(MockMvc mockMvc, String origin) throws Exception {
        return mockMvc.perform(options(PATH)
                .header(HttpHeaders.ORIGIN, origin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"));
    }

    @Nested
    @SpringBootTest(properties = "CORS_ALLOWED_ORIGINS=")
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @DisplayName("추가 출처 환경변수가 비어 있을 때")
    class 추가_출처_없음 {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("기본 출처인 localhost는 허용한다")
        void localhost_허용() throws Exception {
            // given
            String origin = "http://localhost:3000";

            // when & then
            preflight(mockMvc, origin)
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
        }

        @Test
        @DisplayName("허용되지 않은 출처는 모든 출처 허용으로 열리지 않고 403으로 거부한다")
        void 허용되지_않은_출처_거부() throws Exception {
            // given
            String origin = "https://evil.example.com";

            // when & then
            preflight(mockMvc, origin)
                    .andExpect(status().isForbidden())
                    .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        }
    }

    @Nested
    @SpringBootTest(properties = "CORS_ALLOWED_ORIGINS=https://*.vercel.app")
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @DisplayName("추가 출처 환경변수에 패턴을 지정했을 때")
    class 추가_출처_패턴 {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("패턴에 맞는 Vercel 출처를 허용한다")
        void 패턴_출처_허용() throws Exception {
            // given
            String origin = "https://collaboration-git-feat-abc.vercel.app";

            // when & then
            preflight(mockMvc, origin)
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
        }

        @Test
        @DisplayName("추가 출처를 지정해도 localhost 기본 출처는 유지된다")
        void localhost_유지() throws Exception {
            // given
            String origin = "http://localhost:5173";

            // when & then
            preflight(mockMvc, origin)
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
        }
    }

    @Test
    @DisplayName("허용 출처가 하나도 없으면 기동 시 예외를 던진다")
    void 허용_출처_없으면_예외() {
        // given
        String[] allowedOrigins = {"", " "};

        // when & then
        assertThatThrownBy(() -> new CorsConfig(allowedOrigins))
                .isInstanceOf(IllegalStateException.class);
    }
}
