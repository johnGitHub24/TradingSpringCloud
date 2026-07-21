package com.trading.cloud.order.domain;

/**
 * 【職責】表達以訂單識別碼查詢時找不到目標資料的領域失敗。
 * 【技巧】繼承未檢查 {@link RuntimeException}，讓 Service 可直接中止正常查詢流程並交由 Advice 統一轉譯。
 * 【概念】具名領域例外比回傳 {@code null} 更明確，能讓 HTTP 層穩定對應為 404 而非混淆不存在與程式錯誤。
 * 【邊界】不自行產生 HTTP 回應；由 {@link com.trading.cloud.order.config.GlobalExceptionHandler} 轉為 API 契約。
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * 建立包含可讀取查詢失敗原因的例外。
     * @param message 找不到訂單時提供給問題詳情的說明
     */
    public OrderNotFoundException(String message) {
        super(message);
    }
}
