package org.sopt.collaboration.global.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "cors.allowed-origins=http://localhost:3000,https://*.vercel.app,")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsConfigTest {

    private static final String PATH = "/v3/api-docs";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("허용된 출처의 preflight 요청에는 CORS 허용 헤더를 응답한다")
    void 허용된_출처_preflight_허용() throws Exception {
        // given
        String origin = "http://localhost:3000";

        // when & then
        mockMvc.perform(options(PATH)
                        .header(HttpHeaders.ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
    }

    @Test
    @DisplayName("와일드카드 패턴에 맞는 Vercel 출처를 허용한다")
    void 패턴_출처_허용() throws Exception {
        // given
        String origin = "https://collaboration-git-feat-abc.vercel.app";

        // when & then
        mockMvc.perform(options(PATH)
                        .header(HttpHeaders.ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
    }

    @Test
    @DisplayName("허용되지 않은 출처의 preflight 요청은 403으로 거부한다")
    void 허용되지_않은_출처_거부() throws Exception {
        // given
        String origin = "https://evil.example.com";

        // when & then
        mockMvc.perform(options(PATH)
                        .header(HttpHeaders.ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
