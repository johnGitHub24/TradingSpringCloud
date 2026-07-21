package com.trading.cloud.common.dto;

import lombok.Data;

/**
 * 【職責】承載 Gateway 儀表板聚合後的信任分數、訂單數量與最新訂單狀態。
 * 【技巧】以 Lombok {@code @Data} 產生序列化所需的存取器，讓 Spring MVC 可直接轉為 JSON。
 * 【概念】DTO 是服務邊界的資料契約；它只描述回應資料，不承載跨服務呼叫或業務判斷。
 * 【邊界】不保證最新狀態的排序來源，也不保存下游服務的完整訂單明細。
 */
@Data
public class DashboardResponse {
    private int systemTrust;
    private long orderCount;
    private String latestOrderStatus;
    private String message;
}
