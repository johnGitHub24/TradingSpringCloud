package com.trading.cloud.loop.api;

import com.trading.cloud.common.dto.TrustResponse;
import com.trading.cloud.loop.application.TrustService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【職責】提供查詢與遞增系統信任分數的 loop-service REST API。
 * 【技巧】使用 Spring MVC 映射不同 HTTP 方法，並將回應 DTO 自動序列化為 JSON。
 * 【概念】Controller 保持薄層可將 HTTP 路由與分數狀態管理分離，使服務規則可不依賴 Web 框架測試。
 * 【邊界】不保存資料、不計算分數演算法；本示範的記憶體狀態由 {@link TrustService} 管理。
 */
@RestController
@RequestMapping("/api/v1")
public class TrustController {

    private final TrustService trustService;

    /** 注入管理信任分數狀態的應用服務。 */
    public TrustController(TrustService trustService) {
        this.trustService = trustService;
    }

    /**
     * 【職責】讀取並回傳目前系統信任分數。
     * 【技巧】以 {@code @GetMapping} 宣告不改變服務狀態的唯讀端點。
     * 【概念】查詢與命令分開映射，可讓 API 使用者由 HTTP 方法辨識是否預期造成狀態變更。
     * @return 目前信任分數與服務名稱
     */
    @GetMapping("/trust")
    public TrustResponse getTrust() {
        return trustService.current();
    }

    /**
     * 【職責】將系統信任分數遞增一點並回傳更新結果。
     * 【技巧】以 {@code @PostMapping} 表示此請求具有狀態變更副作用，實作委派給 Service。
     * 【概念】命令端點不自行操作欄位，能避免 API 層成為業務狀態的第二個管理位置。
     * 【邊界】不支援指定增量、持久化或並行存取保證。
     * @return 遞增後的信任分數與服務名稱
     */
    @PostMapping("/trust/increment")
    public TrustResponse increment() {
        return trustService.increment();
    }

    /**
     * 【職責】提供不依賴信任分數狀態的簡易存活探針。
     * 【技巧】以固定字串回應，方便 Gateway 或人工驗證服務端點是否可達。
     * 【概念】存活檢查應與業務資料解耦，避免資料異常時無法判斷程序本身是否仍在運作。
     * @return 固定的 loop-service 存活標記
     */
    @GetMapping("/health/ping")
    public String ping() {
        return "loop-service-ok";
    }
}
