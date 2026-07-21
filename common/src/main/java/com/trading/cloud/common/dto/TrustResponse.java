package com.trading.cloud.common.dto;

import lombok.Data;

/**
 * 【職責】定義 loop-service 對外提供的系統信任分數回應。
 * 【技巧】透過 Lombok {@code @Data} 產生存取器，讓控制器與 OpenFeign 可依 JavaBean 慣例交換 JSON。
 * 【概念】DTO 將傳輸資料與服務內部狀態分離，呼叫端只依賴穩定的欄位契約。
 * 【邊界】不定義信任分數的計算公式、持久化方式或變更權限。
 */
@Data
public class TrustResponse {
    private int systemTrust;
    private String service;
}
