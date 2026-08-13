package com.trading.cloud.loop.application;

import com.trading.cloud.common.dto.TrustResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 【職責】驗證 TrustService 記憶體信任分數的讀取與遞增契約。
 * 【技巧】直接 new Service，不啟動 Spring。
 * 【概念】狀態變更測「先改再組 DTO」；HTTP 映射留給 Controller 測試。
 */
@Tag("unit")
class TrustServiceTest {

    private TrustService trustService;

    @BeforeEach
    void setUp() {
        trustService = new TrustService();
    }

    /**
     * CASE CLOUD-LOOP-001：查詢目前信任分數快照。
     * Given: 新實例預設 0；When: current；Then: systemTrust=0、service=loop-service。
     */
    @Test
    void CLOUD_LOOP_001_currentReturnsInitialTrust() {
        TrustResponse response = trustService.current();

        assertThat(response.getSystemTrust()).isZero();
        assertThat(response.getService()).isEqualTo("loop-service");
    }

    /**
     * CASE CLOUD-LOOP-002：遞增後回傳最新快照且不共用可變內部狀態。
     * Given: 初始 0；When: increment 兩次；Then: 分數為 2，current 與先前 DTO 互不影響。
     */
    @Test
    void CLOUD_LOOP_002_incrementAdvancesTrustSnapshot() {
        TrustResponse first = trustService.increment();
        TrustResponse second = trustService.increment();

        assertThat(first.getSystemTrust()).isEqualTo(1);
        assertThat(second.getSystemTrust()).isEqualTo(2);
        assertThat(trustService.current().getSystemTrust()).isEqualTo(2);
        first.setSystemTrust(99);
        assertThat(trustService.current().getSystemTrust()).isEqualTo(2);
    }
}
