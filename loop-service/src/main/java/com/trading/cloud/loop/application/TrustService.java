package com.trading.cloud.loop.application;

import com.trading.cloud.common.dto.TrustResponse;
import org.springframework.stereotype.Service;

/**
 * 【職責】管理教學用途的記憶體系統信任分數，並轉換為對外回應 DTO。
 * 【技巧】以 Spring {@code @Service} 管理單例元件，讓 Controller 透過依賴注入共享同一個計數狀態。
 * 【概念】將狀態操作封裝於 Service，可讓 Controller 不直接持有可變資料，日後也能替換為持久化實作。
 * 【邊界】不提供持久化、分散式一致性、併發同步或複雜信任計算。
 */
@Service
public class TrustService {

    private int systemTrust = 0;

    /**
     * 【職責】建立描述目前記憶體信任分數的回應。
     * 【技巧】每次建立新的 {@link TrustResponse}，避免將內部可變狀態直接暴露給呼叫端。
     * 【概念】傳回 DTO 的快照可分離服務內部資料與 API 輸出，呼叫端修改 DTO 不會改變計數器。
     * @return 目前信任分數與固定服務名稱
     */
    public TrustResponse current() {
        TrustResponse response = new TrustResponse();
        response.setSystemTrust(systemTrust);
        response.setService("loop-service");
        return response;
    }

    /**
     * 【職責】將記憶體信任分數加一後回傳最新快照。
     * 【技巧】先更新欄位，再重用 {@link #current()} 集中 DTO 組裝邏輯。
     * 【概念】將「變更狀態」與「建立回應」分離，可避免兩個端點逐漸產生不同的回應格式。
     * 【邊界】整數溢位及多執行緒遞增競爭不在此教學示範的處理範圍。
     * @return 遞增後的信任分數與固定服務名稱
     */
    public TrustResponse increment() {
        systemTrust++;
        return current();
    }
}
