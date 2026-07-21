package com.trading.cloud.loop.api;

import com.trading.cloud.loop.application.TrustService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.trading.cloud.common.dto.TrustResponse;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 【職責】切片測試 TrustController 的 HTTP 映射與 JSON 回應。
 * 【技巧】{@code @WebMvcTest} + {@code @MockBean} 隔離 Service。
 * 【概念】Web 切片測試不啟動完整上下文，專注驗證 Controller 契約。
 */
@WebMvcTest(TrustController.class)
class TrustControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrustService trustService;

    /**
     * CASE CLOUD-LOOP-001：查詢信任分數。
     * Given: Service stub systemTrust=3；When: GET /api/v1/trust；Then: 200 且 JSON 正確。
     */
    @Test
    void CLOUD_LOOP_001_returnsTrust() throws Exception {
        TrustResponse trust = new TrustResponse();
        trust.setSystemTrust(3);
        trust.setService("loop-service");
        when(trustService.current()).thenReturn(trust);

        mockMvc.perform(get("/api/v1/trust"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemTrust").value(3));
    }
}
