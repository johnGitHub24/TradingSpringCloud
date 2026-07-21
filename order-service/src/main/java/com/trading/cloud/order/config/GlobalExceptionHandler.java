package com.trading.cloud.order.config;

import com.trading.cloud.order.domain.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * 【職責】將 order-service 的領域例外統一轉換為符合 RFC 7807 的 HTTP 問題回應。
 * 【技巧】以 {@code @RestControllerAdvice} 攔截 Controller 例外，並使用 Spring {@link ProblemDetail}
 * 描述狀態、細節、實例 URI 與自訂錯誤碼。
 * 【概念】例外轉譯集中後，Controller 只處理成功流程，API 使用者也能取得一致而可機讀的失敗契約。
 * 【邊界】僅處理已宣告的領域例外，不取代未預期系統錯誤的監控或通用錯誤政策。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 【職責】將找不到訂單的領域例外轉為 HTTP 404 問題詳情。
     * 【技巧】由 {@code @ExceptionHandler} 依例外型別選擇處理器，並將原始請求 URI 設為問題實例。
     * 【概念】以穩定的 {@code errorCode} 補足 HTTP 狀態，可讓前端依業務原因處理而非解析錯誤文字。
     * @param ex 服務查詢時拋出的找不到訂單例外
     * @param request 觸發例外的 HTTP 請求，用於保留實例 URI
     * @return 包含 {@code ORDER_NOT_FOUND} 的 404 RFC 7807 回應
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Order Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", "ORDER_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
