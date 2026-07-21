package com.trading.cloud.common.dto;

import lombok.Data;

/**
 * 【職責】定義 order-service 與 Gateway 共用的訂單摘要回應契約。
 * 【技巧】以 Lombok {@code @Data} 統一產生 JavaBean 存取器，供 JSON 序列化與 Feign 解碼使用。
 * 【概念】跨模組 DTO 將傳輸格式集中管理，使呼叫端不必依賴 order-service 的內部領域模型。
 * 【邊界】僅表示查詢摘要，不包含撮合明細、持久化規則或訂單狀態轉換。
 */
@Data
public class OrderSummaryResponse {
    private Long orderId;
    private String symbol;
    private String status;
    private String service;
}
