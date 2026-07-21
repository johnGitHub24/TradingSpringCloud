# API 規格書 — Trading Spring Cloud

**Gateway Base URL：** `http://localhost:8080`

---

## Gateway — 聚合與代理

### GET `/api/v1/dashboard`（OpenFeign）

```json
{
  "systemTrust": 5,
  "orderCount": 2,
  "latestOrderStatus": "NEW",
  "message": "Feign aggregated loop-service + order-service"
}
```

### GET `/proxy/loop/trust` → loop-service

等同 `GET http://localhost:8081/api/v1/trust`

### GET `/proxy/orders/{orderId}` → order-service

等同 `GET http://localhost:8082/api/v1/orders/{orderId}`

---

## loop-service :8081

| Method | 路徑 | 說明 |
|--------|------|------|
| GET | `/api/v1/trust` | 系統可信度 |
| POST | `/api/v1/trust/increment` | 信任分 +1 |

---

## order-service :8082

| Method | 路徑 | 說明 |
|--------|------|------|
| GET | `/api/v1/orders` | 訂單列表 |
| GET | `/api/v1/orders/{id}` | 單筆訂單 |

---

*最後更新：2026-07-07*
