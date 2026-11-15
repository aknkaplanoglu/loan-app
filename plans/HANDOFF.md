# Handoff — loan-app
**Güncelleme:** 2026-11-16 · `main@e209ce2` · Claude (handoff kurulumu)

## Durum
Uykuda (son commit 2025-02-11). Banka için kredi yönetimi REST API'si (Spring Boot 3.4, Java 17, JPA, Security, H2, Docker): kredi oluşturma, taksit listeleme/ödeme (erken ödeme indirimi, gecikme cezası), ADMIN/CUSTOMER rolleri. Postman koleksiyonu `collection/`.
## Aktif işler
- Yok. Açık PR yok.
## Bekleyen dallar ve değişiklikler
- Yok: tek dal `main` = `origin/main`, çalışma ağacı temiz, stash yok.
## Senden beklenen kararlar
- Yok.
## Ortam ve tuzaklar
- `mvn clean package`, `mvn spring-boot:run`; Swagger `http://localhost:8080/swagger-ui.html`, H2 konsolu `/h2-console` ([README](../README.md)). Dockerfile var.
## Son doğrulama
- Bu snapshot'ta test koşulmadı.
## Sıradaki adım
- Yok.
