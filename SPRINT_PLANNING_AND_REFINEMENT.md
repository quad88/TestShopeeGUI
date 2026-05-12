# Sprint Planning & Backlog Refinement
### โปรเจกต์ SmartPick — ฟีเจอร์เชื่อมต่อ Shopee Interface
#### อิงตาม Epic จริง + อธิบายกระบวนการคิดทั้งฝั่ง PO และ Dev

> **วัตถุประสงค์ของเอกสารนี้:**  
> ใช้เป็น Learning Guide + Working Template สำหรับ Sprint Planning และ Backlog Refinement  
> ทุก Section มีคำอธิบาย "💭 วิธีคิด" เพื่อให้เข้าใจว่าทำไมถึงตัดสินใจแบบนั้น  
>
> **ทีม:** 1 Developer (Mid-level) | **Sprint Length:** 2 สัปดาห์ | **Velocity เริ่มต้น:** ~7 pts/sprint

---

## สารบัญ

1. [ภาพรวม Product Backlog](#1-ภาพรวม-product-backlog)
2. [Backlog Refinement — กระบวนการและวิธีคิด](#2-backlog-refinement--กระบวนการและวิธีคิด)
3. [Sprint 1 — วางรากฐาน + เปิด Auth URL](#3-sprint-1--วางรากฐาน--เปิด-auth-url)
4. [Sprint 2 — เชื่อมต่อครบ Flow + เก็บ Token](#4-sprint-2--เชื่อมต่อครบ-flow--เก็บ-token)
5. [Sprint 3 — Token เสถียร + แจ้งเตือนหมดอายุ](#5-sprint-3--token-เสถียร--แจ้งเตือนหมดอายุ)
6. [Sprint 4 — ดึงออเดอร์ + Resilience](#6-sprint-4--ดึงออเดอร์--resilience)
7. [Sprint 5 — Security + Quality พร้อม Production](#7-sprint-5--security--quality-พร้อม-production)
8. [บัตรอ้างอิงด่วน](#8-บัตรอ้างอิงด่วน)

---

## 1. ภาพรวม Product Backlog

### Product Backlog ณ วันเริ่มโปรเจกต์

```
╔══════════════════════════════════════════════════════════════════════╗
║  EPIC SHOP-001: เชื่อมต่อร้าน Shopee (Authentication & Token)       ║
╠══════════════════════════════════════════════════════════════════════╣
║  ID           Story / Item                              Pts  Sprint  ║
║  ─────────────────────────────────────────────────────────────────  ║
║  TECH-001     ตั้งโปรเจกต์ + DB Schema + CI Pipeline    3    S1    ║
║  SHOP-001-1   สร้าง Shopee Auth URL                      3    S1    ║
║  SHOP-001-2   Handle Callback + Exchange Code            5    S2    ║
║  SHOP-001-3   บันทึก Token ลง DB (Entity + Repo)         3    S2    ║
║  SHOP-001-4   Auto-refresh Access Token Scheduler        5    S3    ║
║  SHOP-001-5   แจ้งเตือนหมดอายุ Re-auth (7d + 1d)        3    S3    ║
╠══════════════════════════════════════════════════════════════════════╣
║  EPIC SHOP-002: ซิงค์ออเดอร์ Shopee                                 ║
╠══════════════════════════════════════════════════════════════════════╣
║  SHOP-002-1   ดึงรายการออเดอร์                           5    S4    ║
║  SHOP-002-2   Error Handling + Retry + Circuit Breaker   3    S4    ║
╠══════════════════════════════════════════════════════════════════════╣
║  TECH TASKS: Infrastructure & Quality                               ║
╠══════════════════════════════════════════════════════════════════════╣
║  TECH-002     Integration Tests — WireMock Shopee API    3    S5    ║
║  TECH-003     Token Encryption at Rest                   3    S5    ║
║  TECH-004     API Key Guard บน Endpoints                 2    S5    ║
╚══════════════════════════════════════════════════════════════════════╝
รวม: 38 Story Points | ~6 Sprints | ~12 สัปดาห์ (1 Dev, Mid-level)
```

### 💭 วิธีคิด: ทำไมต้องเรียงแบบนี้?

```
คำถามที่ PO ถาม:        "อะไรสำคัญที่สุดถ้าผมจะเห็นคุณค่าจาก Sprint นี้?"
คำถามที่ Dev ถาม:       "อะไรต้องทำก่อน เพื่อให้งานถัดไปไม่ติดขัด?"
คำถามที่ทีม align กัน:  "Story ไหนบล็อก Story อื่น? → เอาไว้ Sprint เดียวกัน"

ตัวอย่าง:
  SHOP-001-3 (บันทึก Token) ขึ้นอยู่กับ SHOP-001-2 (Exchange Code)
  → จึงใส่ใน Sprint 2 ด้วยกัน แม้ว่า Sprint 2 จะหนักขึ้น (8 pts stretch)

  TECH-001 (Setup) ต้องอยู่ Sprint 1 เพราะทุก Story ถัดไปต้องการ infrastructure นี้
```

---

## 2. Backlog Refinement — กระบวนการและวิธีคิด

### Refinement คืออะไร และทำตอนไหน?

```
Refinement = Session กลาง Sprint เพื่อเตรียม Stories ที่จะเข้า Sprint ถัดไป
ทำเมื่อ:    กลาง Sprint ปัจจุบัน (ประมาณ Day 5–7 ของ Sprint 2 สัปดาห์)
ใครเข้าร่วม: PO + Dev Team
ระยะเวลา:   ~1 ชั่วโมง ต่อ Sprint

Output ที่ต้องการ:
  ✅ Story เขียนในรูปแบบ User Story แล้ว
  ✅ Acceptance Criteria ชัดเจน ทั้ง PO และ Dev เห็นด้วย
  ✅ Story ได้รับการประมาณการ (Story Points)
  ✅ Dependencies ระบุแล้ว
  ✅ Story ผ่าน Definition of Ready (DoR)
```

---

### Refinement Session Template

#### ก่อน Session (PO เตรียม)

```
[ ] เลือก Story 3–5 อันที่จะเข้า Sprint ถัดไป
[ ] เขียน User Story draft แล้ว
[ ] เขียน Acceptance Criteria draft แล้ว (อย่างน้อย happy path)
[ ] ระบุว่า Story นี้ทำไมสำคัญ (Business Value)
[ ] รู้ว่า Story นี้มี dependency กับอะไรไหม
```

#### ก่อน Session (Dev เตรียม)

```
[ ] อ่าน Story draft และ AC ล่วงหน้า
[ ] คิด technical approach คร่าวๆ
[ ] ตั้งคำถามเรื่องที่ยังไม่ชัด
[ ] ประเมินความเสี่ยงทางเทคนิค
```

#### ระหว่าง Session (ทั้งคู่ทำร่วมกัน)

```
1.  PO อ่าน Story + อธิบาย Business Value (5 นาที)
2.  Dev ถามจนเข้าใจ หรือโต้แย้ง scope ที่ใหญ่เกิน (10 นาที)
3.  ปรับ AC ให้ชัดเจน เพิ่ม edge cases ที่สำคัญ (10 นาที)
4.  Dev ประมาณการ Story Points (Planning Poker ถ้ามีหลายคน) (5 นาที)
5.  ระบุ Tasks คร่าวๆ (ไม่ต้องละเอียด เพียงพอให้รู้ว่างานใหญ่แค่ไหน) (5 นาที)
6.  ตรวจ DoR Checklist — ถ้าผ่านครบ Story "พร้อม" (5 นาที)
```

---

### 💭 วิธีคิด: สัญญาณว่า Story "ยังไม่พร้อม"

| สัญญาณ | ความหมาย | ต้องทำอะไร |
|---|---|---|
| Dev ถามคำถามเดิมซ้ำหลายรอบ | AC ยังไม่ชัด | ปรับ AC ให้ตอบคำถามนั้น |
| "แล้วแต่กรณี", "TBD", "ค่อยคุย" อยู่ใน AC | ยังไม่ตัดสินใจ | PO ต้องตัดสินใจก่อน |
| Dev ประมาณการได้ > 8 pts | Story ใหญ่เกินไป | แตก Story ออกเป็น 2 |
| ไม่รู้ว่า External API ทำงานยังไง | ความไม่แน่นอนสูง | สร้าง Spike ก่อน |
| "ทำได้ แต่ต้องรอ Story X ก่อน" | Dependency ที่ยังไม่จัดการ | จัด Sprint ให้ถูก |

---

### ตัวอย่าง Refinement Session จริง: SHOP-001-2

```
📋 Story Draft (PO นำเข้า):
   "ในฐานะ Seller ฉันต้องการให้ระบบรับ callback จาก Shopee เพื่อเชื่อมต่อสำเร็จ"

🗣️ PO อธิบาย:
   "ขั้นตอนนี้คือหลังจาก Seller กด approve บน Shopee แล้ว Shopee จะ redirect
   กลับมาที่เรา เราต้องแลก code นั้นเป็น access_token และ refresh_token"

❓ Dev ถาม:
   - "ถ้า code หมดอายุหรือ invalid ต้องทำอะไร?"
   - "บันทึก token ที่ไหน? DB หรือ in-memory?"
   - "ถ้า Shopee เรียก callback 2 ครั้ง (retry) ต้องจัดการยังไง?"

✏️ ปรับ AC ให้ครอบคลุม:
   - เพิ่ม edge case: code invalid → error response ชัดเจน
   - กำหนด: token บันทึกลง DB (TECH-001 ทำ schema แล้ว)
   - กำหนด: idempotent — ถ้า callback ซ้ำ ให้ update ไม่ใช่ error

🃏 ประมาณการ: 5 pts
   เหตุผล: HTTP call + Parse response + Error handling + ความไม่แน่นอนของ Shopee API behavior

✅ DoR Check: ผ่านทุกข้อ → พร้อมเข้า Sprint 2
```

---

## 3. Sprint 1 — วางรากฐาน + เปิด Auth URL

```
Sprint:     1 / 5
ระยะเวลา:  สัปดาห์ที่ 1–2 (11–24 พฤษภาคม 2569)
ความจุ:    6 Story Points (Commit) 
Stories:   TECH-001 (3 pts) + SHOP-001-1 (3 pts)
```

---

### 🎯 Sprint Goal

> **"วางรากฐานระบบให้มั่นคง และเปิดให้ Seller เริ่มกระบวนการเชื่อมต่อร้าน Shopee ได้เป็นครั้งแรก"**

#### 💭 วิธีคิด Sprint Goal นี้

```
PO ถาม:  "Sprint นี้ถ้าเสร็จแล้ว ฉันจะได้เห็นอะไร?"
Dev ตอบ: "Seller กดปุ่ม 'เชื่อมต่อ Shopee' แล้วได้ Auth URL ไปเปิดใน Browser ได้"

→ Sprint Goal = ผลลัพธ์ที่มองเห็นได้ ไม่ใช่รายการงาน

❌ Sprint Goal ที่ไม่ดี: "สร้าง Project, ทำ Database, สร้าง Auth URL"
✅ Sprint Goal ที่ดี:    "วางรากฐานและเปิดให้ Seller เริ่มกระบวนการเชื่อมต่อ Shopee ได้"

กฎ: Sprint Goal ควรตอบคำถาม "ทำไม Sprint นี้ถึงสำคัญ?" ได้ใน 1 ประโยค
```

---

### 👔 PO View — Sprint 1

#### Business Value

```
ทำไม Sprint นี้สำคัญ:
  - ถ้า Sprint 1 ไม่เสร็จ → Sprint 2-5 ทำไม่ได้เลย (hard dependency ทั้งหมด)
  - Seller ยังใช้งานจริงไม่ได้ แต่จะ Demo การเริ่ม Auth flow ให้ Stakeholder ดูได้
  - Unblocks team ให้ทำงานบน foundation ที่มั่นคงตั้งแต่ Sprint 2

Value ที่ส่งมอบ Sprint นี้:
  - ✅ ระบบพร้อมพัฒนาต่อ (CI/CD, DB, environment config)
  - ✅ Seller สามารถ click เพื่อเริ่ม OAuth flow กับ Shopee ได้ (แม้ยังไม่ครบ flow)

สิ่งที่ PO ควรเตรียม Sprint นี้:
  - Shopee Sandbox credentials (Partner ID, Secret Key)
  - Redirect URL ที่จะใช้ใน Sandbox
  - ยืนยัน environment: Dev / SIT / Prod ที่จะใช้
```

#### คำถามที่ PO ต้องตอบได้ก่อน Sprint 1 เริ่ม

```
Q: Redirect URL หลัง OAuth คือ URL อะไร?
Q: Database ที่ใช้คือ MySQL หรือ PostgreSQL?
Q: มี CI/CD อยู่แล้วไหม? หรือต้องสร้างใหม่?
Q: Environment config (secrets) จัดการใน Vault / env file / Kubernetes secrets?
```

---

### 👨‍💻 Dev View — Sprint 1

#### Technical Approach

```
TECH-001: ตั้งโปรเจกต์ + DB Schema + CI Pipeline
─────────────────────────────────────────────────
Goal:   วางโครงสร้างที่ทุก Story ถัดไปสามารถ build on top ได้

Tasks:
  [ ] 1h  - สร้าง Spring Boot project (ถ้ายังไม่มี) หรือ integrate module ใหม่
  [ ] 2h  - ออกแบบ DB Schema สำหรับ ShopeeToken entity
                CREATE TABLE shopee_tokens (
                  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
                  shop_id     VARCHAR(50) NOT NULL UNIQUE,
                  access_token  TEXT NOT NULL,
                  refresh_token TEXT NOT NULL,
                  access_token_expires_at  TIMESTAMP NOT NULL,
                  refresh_token_expires_at TIMESTAMP NOT NULL,
                  created_at  TIMESTAMP DEFAULT NOW(),
                  updated_at  TIMESTAMP DEFAULT NOW()
                );
  [ ] 2h  - สร้าง Flyway / Liquibase migration script
  [ ] 2h  - ตั้ง CI pipeline (GitHub Actions / GitLab CI)
                build → test → (deploy to SIT on merge)
  [ ] 1h  - ตั้ง application.properties สำหรับ dev/sit/prod profiles
  [ ] 1h  - ตั้ง logging config (SLF4J + Logback)

💭 วิธีคิด DB Schema:
  - ทำไมต้อง UNIQUE บน shop_id? → 1 ร้านมีแค่ 1 token ชุด ถ้า reconnect ให้ UPDATE ไม่ใช่ INSERT ซ้ำ
  - ทำไมต้องเก็บ expires_at? → Scheduler ใน Sprint 3 จะ query token ที่ใกล้หมดอายุ
  - ทำไมต้อง TEXT ไม่ใช่ VARCHAR? → Token ยาวไม่แน่นอน ป้องกัน truncation

─────────────────────────────────────────────────
SHOP-001-1: สร้าง Shopee Auth URL
─────────────────────────────────────────────────
Goal:   Return URL ที่ Seller ใช้ไปอนุมัติ app บน Shopee

Tasks:
  [ ] 1h  - สร้าง ShopeeConfig.java (partner_id, secret_key, redirect_url, base_url)
  [ ] 2h  - สร้าง ShopeeAuth.java :: generateAuthUrl(shopId)
                URL Format:
                https://partner.shopeemobile.com/api/v2/shop/auth_partner
                  ?partner_id={partner_id}
                  &timestamp={unix_timestamp}
                  &sign={HMAC_SHA256}
                  &redirect={redirect_url}
  [ ] 2h  - สร้าง ShopeeSignature.java :: sign(path, timestamp) → HMAC-SHA256
  [ ] 1h  - สร้าง REST endpoint: GET /shopee/auth?shop_id={id}
  [ ] 1h  - เขียน Unit Test สำหรับ signature generation
  [ ] 1h  - Manual test กับ Shopee Sandbox

💭 วิธีคิด HMAC Signature:
  - Shopee ต้องการ signature เพื่อยืนยันว่า request มาจาก partner จริง
  - Input:  partner_id + path + timestamp (concatenated)
  - Secret: partner_secret key
  - Output: hex string ของ HMAC-SHA256
  - ทำไม timestamp? → ป้องกัน replay attack (signature หมดอายุใน ~5 นาที)
```

#### ความเสี่ยงทางเทคนิค Sprint 1

```
⚠️ ความเสี่ยง: Shopee Sandbox อาจมี rate limit หรือ behavior ต่างจาก docs
   Mitigation: ทำ Spike 2 ชั่วโมงทดสอบ signature generation กับ sandbox ก่อน

⚠️ ความเสี่ยง: Team ยังไม่รู้ว่า redirectUrl จะเป็นอะไรใน sandbox
   Mitigation: PO ต้องให้ข้อมูลนี้ก่อน Sprint เริ่ม (ดู PO checklist ด้านบน)
```

---

### 📝 User Stories — Sprint 1

#### TECH-001: ตั้งโปรเจกต์ + DB Schema + CI Pipeline

```
Story Type: Tech Task (ไม่มี User Story format — เพราะเป็น infrastructure)
Points:     3
Priority:   Must Have (ทุก Sprint ถัดไปขึ้นอยู่กับ Story นี้)

Acceptance Criteria:
  [ ] โปรเจกต์ build ผ่านใน CI pipeline โดยไม่มี error
  [ ] DB migration รันอัตโนมัติเมื่อ application start
  [ ] Table shopee_tokens ถูกสร้างใน database ของ SIT environment
  [ ] SIT environment รันได้และ health endpoint ตอบ HTTP 200
  [ ] ไม่มี credentials ฝังใน source code (ใช้ env variable หรือ config server)
  [ ] CI pipeline รัน unit test และ fail เมื่อ test fail

💭 ทำไม AC เหล่านี้:
  - "build ผ่านใน CI" → ยืนยันว่า pipeline ทำงานได้จริง ไม่ใช่แค่ local
  - "DB migration อัตโนมัติ" → ลด manual step เวลา deploy ใหม่
  - "ไม่มี credentials ใน code" → Security hygiene ตั้งแต่ต้น แก้ทีหลังยาก
```

#### SHOP-001-1: สร้าง Shopee Auth URL

```
User Story:
  ในฐานะ Seller
  ฉันต้องการได้รับ URL สำหรับอนุมัติการเชื่อมต่อกับ Shopee
  เพื่อที่ฉันจะสามารถเริ่มกระบวนการเชื่อมต่อร้านค้าของฉันกับ SmartPick ได้

Points:  3
Priority: Must Have

Acceptance Criteria:
  ─── Happy Path ───────────────────────────────────────────────────────────
  Given  Seller ต้องการเชื่อมต่อร้าน Shopee ของตน (shop_id = 12345)
  When   ระบบสร้าง Auth URL สำหรับ shop_id นั้น
  Then   URL ที่ได้ต้องมี parameter ครบ: partner_id, timestamp, sign, redirect
  And    signature (sign) ต้องคำนวณด้วย HMAC-SHA256 ถูกต้องตาม Shopee spec
  And    timestamp ใน URL ต้องเป็น Unix timestamp ปัจจุบัน (±30 วินาที)

  ─── Edge Cases ───────────────────────────────────────────────────────────
  Given  API ถูกเรียกโดยไม่มี shop_id
  When   GET /shopee/auth (ไม่มี query param)
  Then   ระบบคืน HTTP 400 Bad Request พร้อม error message ชัดเจน

  Given  เรียก URL เดิม 2 ครั้งในเวลาต่างกัน
  When   สร้าง Auth URL 2 ครั้ง
  Then   timestamp และ signature ต้องต่างกัน (ไม่ cache URL)

  ─── Security ─────────────────────────────────────────────────────────────
  And    secret key ต้องไม่ปรากฏใน URL, logs หรือ response body

💭 ทำไม AC เรื่อง "ไม่ cache URL":
  Shopee signature มี timestamp → URL ที่ generate ใหม่แต่ละครั้งจะ valid
  ถ้า cache URL เก่า → timestamp เก่า → Shopee reject เพราะ expired signature
```

---

### ✅ Definition of Ready Checklist — Sprint 1

```
TECH-001:
  [✓] Story เขียนแล้ว (Tech Task format)
  [✓] AC ชัดเจนและทีมเห็นด้วย
  [✓] ประมาณการแล้ว (3 pts)
  [✓] Dependencies: ต้องมี Shopee Sandbox credentials จาก PO
  [✓] เล็กพอสำหรับ 1 Sprint

SHOP-001-1:
  [✓] User Story เขียนแล้ว
  [✓] AC ชัดเจน (happy path + edge cases)
  [✓] ประมาณการแล้ว (3 pts)
  [✓] Dependencies: TECH-001 ต้องทำ project setup ก่อน (อยู่ Sprint เดียวกัน)
  [✓] Shopee API spec สำหรับ signature อ่านแล้ว
  [✓] เล็กพอสำหรับ 1 Sprint
```

### ✅ Definition of Done Checklist — Sprint 1 (ตรวจก่อน Sprint Review)

```
TECH-001:
  [ ] Table shopee_tokens อยู่ใน SIT DB แล้ว
  [ ] CI pipeline green (build + test ผ่าน)
  [ ] ไม่มี credentials ใน code (peer review ยืนยัน)
  [ ] PR ได้รับการ review และ approve
  [ ] PO ตรวจ SIT environment ว่า health check ผ่าน

SHOP-001-1:
  [ ] AC ทั้งหมดผ่าน (รวม edge cases)
  [ ] Unit test สำหรับ signature generation ผ่าน
  [ ] ทดสอบกับ Shopee Sandbox แล้ว (URL valid)
  [ ] ไม่มี secret key ปรากฏใน logs
  [ ] Deploy ไป SIT แล้ว
  [ ] PR approved
  [ ] PO ทดสอบเรียก GET /shopee/auth แล้วได้ URL ที่ถูกต้อง
```

---

## 4. Sprint 2 — เชื่อมต่อครบ Flow + เก็บ Token

```
Sprint:     2 / 5
ระยะเวลา:  สัปดาห์ที่ 3–4 (25 พฤษภาคม – 7 มิถุนายน 2569)
ความจุ:    8 Story Points (Stretch — ถ้า callback complex อาจเลื่อน SHOP-001-3 ไป S3)
Stories:   SHOP-001-2 (5 pts) + SHOP-001-3 (3 pts)
```

---

### 🎯 Sprint Goal

> **"ทำให้การเชื่อมต่อร้าน Shopee สำเร็จครบ flow: Seller อนุมัติแล้ว token ถูกเก็บในระบบถาวร"**

#### 💭 วิธีคิด Sprint Goal นี้

```
Sprint 1:  สร้าง "ประตูเข้า" (Auth URL)
Sprint 2:  จัดการ "กลับบ้าน" (Callback) และ "เก็บกุญแจ" (Token to DB)

เหตุผลที่ SHOP-001-2 และ SHOP-001-3 อยู่ Sprint เดียวกัน:
  - SHOP-001-3 (บันทึก Token) ต้องการ token ที่ได้จาก SHOP-001-2 (Exchange Code)
  - ถ้าแยก Sprint → Sprint 2 เสร็จ exchange code แต่ไม่มีที่เก็บ = ไม่มีคุณค่า
  - Sprint Goal ต้องสื่อถึง "complete outcome" ไม่ใช่งานกึ่งสำเร็จ

Stretch Goal: ถ้า SHOP-001-2 ซับซ้อนกว่าคาด → ย้าย SHOP-001-3 ไป Sprint 3
  (Sprint Goal ยังบรรลุได้บางส่วน — exchange code สำเร็จแล้ว แม้ยังไม่ persist)
```

---

### 👔 PO View — Sprint 2

#### Business Value

```
ทำไม Sprint นี้สำคัญ:
  - Sprint นี้คือ "moment of truth" ของ OAuth flow
  - หลัง Sprint 2: ทีมสามารถ Demo การ connect ร้านได้ end-to-end
  - Shopee token ถูกเก็บ → ระบบ "จำ" ว่าร้านไหน connect แล้ว

ผลลัพธ์ที่ PO เห็นใน Sprint Review:
  Demo flow:
    1. Seller กด "เชื่อมต่อ Shopee" → ได้ Auth URL
    2. เปิด URL → อนุมัติบน Shopee
    3. Shopee redirect กลับมา → ระบบ exchange code → เก็บ token
    4. ดู DB → มี record ของ Seller นั้นแล้ว

สิ่งที่ PO ต้องเตรียม Sprint นี้:
  - ยืนยัน redirect URL ที่ register ไว้กับ Shopee Partner Portal
  - ยืนยัน format ของ Dashboard URL หลัง connect สำเร็จ
  - เตรียม test Shopee Seller account สำหรับ demo
```

---

### 👨‍💻 Dev View — Sprint 2

#### Technical Approach

```
SHOP-001-2: Handle Callback + Exchange Code
─────────────────────────────────────────────────
Goal:   รับ GET /shopee/callback?code=X&shop_id=Y และแลก code เป็น token

Flow:
  Browser → GET /shopee/callback?code=ABC&shop_id=12345
    → ShopeeCallbackHandler.handleCallback(code, shopId)
    → ShopeeAuth.exchangeCodeForToken(code, shopId)
        POST https://partner.shopeemobile.com/api/v2/auth/token/get
        Body: { code, shop_id, partner_id, timestamp, sign }
    → Shopee returns: { access_token, refresh_token, expire_in }
    → Return token DTO

Tasks:
  [ ] 1h  - สร้าง ShopeeCallbackHandler (Controller)
  [ ] 2h  - สร้าง ShopeeAuth.exchangeCodeForToken()
                - สร้าง request body + sign
                - HTTP POST ไป Shopee
                - Parse response → TokenDTO
  [ ] 1h  - สร้าง TokenDTO (access_token, refresh_token, expires_at, shop_id)
  [ ] 2h  - Error handling:
                - code invalid/expired → HTTP 400 + meaningful error
                - Shopee API timeout → HTTP 502 + retry hint
                - Unexpected Shopee error → log full response, return HTTP 500
  [ ] 1h  - Unit test กับ Mock Shopee response (WireMock / Mockito)
  [ ] 1h  - Integration test กับ Sandbox จริง

💭 วิธีคิด error handling:
  อย่า return HTTP 500 สำหรับ "user made a mistake" → ใช้ 4xx
  อย่า return HTTP 400 สำหรับ "system failed" → ใช้ 5xx
  เสมอ log full error context (shopId, timestamp) เพื่อ debug ได้

─────────────────────────────────────────────────
SHOP-001-3: บันทึก Token ลง DB
─────────────────────────────────────────────────
Goal:   รับ TokenDTO จาก SHOP-001-2 และเก็บ/อัปเดตใน shopee_tokens table

Tasks:
  [ ] 1h  - สร้าง ShopeeToken JPA Entity (map กับ table ที่สร้างใน TECH-001)
  [ ] 1h  - สร้าง ShopeeTokenRepository (extends JpaRepository)
  [ ] 1h  - สร้าง ShopeeTokenService.saveOrUpdateToken(TokenDTO)
                - ถ้ามี record ของ shop_id → UPDATE
                - ถ้าไม่มี → INSERT
  [ ] 1h  - เชื่อม ShopeeCallbackHandler → ShopeeTokenService
  [ ] 1h  - เขียน Unit Test สำหรับ save และ update scenarios

💭 วิธีคิด saveOrUpdate (Upsert):
  - Seller อาจ reconnect ร้านหลายครั้ง
  - ถ้า INSERT โดยไม่ check → duplicate data
  - ใช้ JPA findByShopId() ก่อน ถ้ามีให้ update, ถ้าไม่มีให้ save
  - หรือใช้ @Query("ON DUPLICATE KEY UPDATE") สำหรับ MySQL
```

#### ความเสี่ยงทางเทคนิค Sprint 2

```
⚠️ ความเสี่ยง: Shopee authorization code หมดอายุเร็วมาก (~10 นาที)
   Mitigation: ทดสอบใน Sandbox ก่อนว่า expiry จริงเป็นเท่าไร (Spike ถ้าจำเป็น)

⚠️ ความเสี่ยง: Shopee อาจ call callback URL ซ้ำ (retry)
   Mitigation: implement idempotent handler — ถ้า code เคย exchange แล้ว → return success ไม่ใช่ error

⚠️ ความเสี่ยง: 8 pts stretch อาจมากเกินไป
   Mitigation: ถ้า SHOP-001-2 เสร็จแต่ SHOP-001-3 ไม่เสร็จ → ย้ายไป Sprint 3 โดยไม่ผ่าน DoD
               อย่า "ทำเสร็จบางส่วน" และนับ points
```

---

### 📝 User Stories — Sprint 2

#### SHOP-001-2: Handle Callback + Exchange Code

```
User Story:
  ในฐานะ Seller ที่อนุมัติ SmartPick บน Shopee แล้ว
  ฉันต้องการให้ระบบรับ callback และแลก authorization code เป็น access token
  เพื่อที่ระบบจะสามารถเรียก Shopee API ในนามของร้านฉันได้

Points:  5
Priority: Must Have

Acceptance Criteria:
  ─── Happy Path ───────────────────────────────────────────────────────────
  Given  Seller อนุมัติ SmartPick บน Shopee และ Shopee redirect กลับมา
  When   GET /shopee/callback?code=ABC123&shop_id=12345
  Then   ระบบเรียก Shopee token endpoint เพื่อแลก code เป็น token
  And    ได้รับ access_token, refresh_token และเวลาหมดอายุที่ถูกต้อง
  And    Seller ถูก redirect ไปยัง /dashboard?shop_id=12345&status=connected
  And    Response time ≤ 5 วินาที (รวม Shopee API call)

  ─── Edge Cases ───────────────────────────────────────────────────────────
  Given  code ที่ส่งมาไม่ถูกต้องหรือหมดอายุ
  When   GET /shopee/callback?code=INVALID&shop_id=12345
  Then   ระบบคืน HTTP 400 พร้อม error: "Invalid or expired authorization code"
  And    Seller ถูก redirect ไปหน้า error พร้อมคำแนะนำให้ลองใหม่

  Given  Shopee API timeout หรือไม่ตอบสนอง
  When   รอ Shopee response เกิน 10 วินาที
  Then   ระบบคืน HTTP 502 พร้อม error message
  And    Log full error details (shop_id, timestamp, error response)

  Given  callback ถูกเรียกซ้ำด้วย code เดิม (Shopee retry)
  When   GET /shopee/callback?code=ABC123&shop_id=12345 (ครั้งที่ 2)
  Then   ระบบจัดการได้โดยไม่ error (idempotent behavior)
```

#### SHOP-001-3: บันทึก Token ลง DB

```
User Story:
  ในฐานะระบบ
  ฉันต้องการบันทึก Shopee access_token และ refresh_token ลงฐานข้อมูล
  เพื่อที่ข้อมูลการเชื่อมต่อจะยังคงอยู่แม้ server จะรีสตาร์ท

Points:  3
Priority: Must Have

Acceptance Criteria:
  ─── Happy Path ───────────────────────────────────────────────────────────
  Given  ระบบได้รับ access_token, refresh_token สำหรับ shop_id=12345
  When   บันทึก token ลง DB
  Then   มี record ใน shopee_tokens table สำหรับ shop_id=12345
  And    access_token_expires_at คำนวณจาก current_time + expire_in seconds ถูกต้อง
  And    refresh_token_expires_at ถูกบันทึกตาม Shopee response

  ─── Reconnect Scenario ───────────────────────────────────────────────────
  Given  shop_id=12345 มี record ใน DB อยู่แล้ว
  When   Seller connect ร้านใหม่ → ได้ token ชุดใหม่
  Then   Record ของ shop_id=12345 ถูก UPDATE (ไม่ใช่ INSERT ใหม่)
  And    ไม่มี duplicate record สำหรับ shop_id เดียวกัน

  ─── Security ─────────────────────────────────────────────────────────────
  And    token ใน DB ยังไม่ encrypted (Sprint 5 จะเพิ่ม — noted as known limitation)

💭 หมายเหตุ "known limitation":
  การ document ไว้ว่า "ยังไม่ encrypt แต่จะทำ Sprint 5" คือการจัดการ tech debt
  อย่างโปร่งใส ดีกว่าทำครึ่งๆ กลางๆ หรือทำให้ซับซ้อนเกินความจำเป็นตอนนี้
```

---

### ✅ DoR + DoD — Sprint 2

```
Definition of Ready (ตรวจก่อน Sprint Planning):
  SHOP-001-2:
    [✓] User Story + AC เขียนแล้ว (รวม edge cases)
    [✓] Shopee token exchange endpoint URL และ parameter รู้แล้ว
    [✓] ประมาณการ 5 pts
    [✓] Dependencies: TECH-001 (Sprint 1) เสร็จแล้ว
    [✓] Redirect URL หลัง auth success กำหนดแล้ว

  SHOP-001-3:
    [✓] User Story + AC เขียนแล้ว
    [✓] DB Schema กำหนดแล้ว (TECH-001)
    [✓] ประมาณการ 3 pts
    [✓] Dependencies: SHOP-001-2 (Sprint เดียวกัน)

Definition of Done (ตรวจใน Sprint Review):
  SHOP-001-2:
    [ ] AC ทุกข้อผ่าน (รวม error cases)
    [ ] Unit tests สำหรับ exchange flow ผ่าน
    [ ] Integration test กับ Shopee Sandbox ผ่าน
    [ ] Error scenarios return HTTP code ที่ถูกต้อง
    [ ] ไม่ log access_token หรือ refresh_token ใน plain text
    [ ] Deploy ไป SIT
    [ ] PR approved
    [ ] PO demo flow ผ่าน (connect ร้านได้จริงใน sandbox)

  SHOP-001-3:
    [ ] Token บันทึกใน DB หลัง callback สำเร็จ
    [ ] Reconnect scenario test ผ่าน (UPDATE ไม่ใช่ duplicate INSERT)
    [ ] expires_at คำนวณถูกต้อง (verifiable จาก DB)
    [ ] Unit tests ผ่าน
    [ ] Deploy ไป SIT
    [ ] PR approved
    [ ] PO ดู DB record ยืนยันว่าข้อมูลครบถ้วน
```

---

## 5. Sprint 3 — Token เสถียร + แจ้งเตือนหมดอายุ

```
Sprint:     3 / 5
ระยะเวลา:  สัปดาห์ที่ 5–6 (8–21 มิถุนายน 2569)
ความจุ:    8 Story Points (Stretch)
Stories:   SHOP-001-4 (5 pts) + SHOP-001-5 (3 pts)
```

---

### 🎯 Sprint Goal

> **"ทำให้การเชื่อมต่อ Shopee มีความเสถียรในระยะยาว: token ต่ออายุอัตโนมัติและแจ้งเตือนก่อนหมดอายุ"**

#### 💭 วิธีคิด Sprint Goal นี้

```
Sprint 2: Seller "เชื่อมต่อ" ได้แล้ว
Sprint 3: แต่ถ้า token หมดอายุ → ระบบพัง, ต้อง reconnect ใหม่ → UX แย่มาก

จุดประสงค์หลัก:
  - ลด "invisible failure" — token หมดอายุโดยผู้ใช้ไม่รู้
  - Automate maintenance ที่ต้องทำซ้ำๆ
  - เตือน Seller ก่อนระบบพัง (proactive > reactive)

ทำไม Sprint 3 สำคัญกว่า Sprint 4 (ดึงออเดอร์):
  ถ้าดึงออเดอร์ได้แต่ token หมดอายุแล้ว → ดึงออเดอร์ล้มเหลวทุกครั้ง
  → ลงทุน Sprint 4 เปล่า ต้อง fix อยู่ดี
  → จึง "stabilize the connection" ก่อน "use the connection"
```

---

### 👔 PO View — Sprint 3

```
Business Value:
  - Token Shopee มีอายุ ~4 ชั่วโมง (access_token) และ ~30 วัน (refresh_token)
  - ถ้าไม่ refresh อัตโนมัติ → Seller ต้อง reconnect ทุก 4 ชั่วโมง → user ไม่พอใจ
  - แจ้งเตือนก่อน refresh_token หมดอายุ → ป้องกัน "reconnect ทีหลัง" ลืมแล้วระบบพัง

ผลลัพธ์ที่ PO เห็นใน Sprint Review:
  Demo:
    1. Token ที่ใกล้หมดอายุ (simulate ใน test) → scheduler refresh ให้อัตโนมัติ
    2. Token ที่ refresh_token ใกล้หมด (7 วัน) → ระบบส่ง notification

สิ่งที่ PO ต้องเตรียม:
  - กำหนด channel การแจ้งเตือน: email / in-app / LINE Notify?
  - กำหนด threshold: แจ้งเตือนล่วงหน้า 7 วัน และ 1 วัน ตกลงกันแล้ว?
  - Notification service มีอยู่แล้วไหม? หรือต้องสร้างใหม่?
```

---

### 👨‍💻 Dev View — Sprint 3

#### Technical Approach

```
SHOP-001-4: Auto-refresh Access Token Scheduler
─────────────────────────────────────────────────
Goal:  รัน scheduler ทุก 30 นาที ตรวจหา token ที่จะหมดอายุใน 30 นาที → refresh

Flow:
  @Scheduled(fixedRate = 30min)
  → Query: SELECT * FROM shopee_tokens WHERE access_token_expires_at < NOW() + 30min
  → ทุก record ที่ได้: เรียก ShopeeAuth.refreshToken(shopId, refreshToken)
  → อัปเดต access_token และ access_token_expires_at ใน DB

Tasks:
  [ ] 1h  - สร้าง ShopeeTokenRefreshScheduler.java (@Scheduled)
  [ ] 2h  - เขียน query ดึง token ใกล้หมดอายุ
  [ ] 2h  - สร้าง ShopeeAuth.refreshToken(shopId, refreshToken)
                POST /api/v2/auth/access_token/get
                Body: { shop_id, refresh_token, partner_id, timestamp, sign }
  [ ] 1h  - อัปเดต token ใน DB หลัง refresh สำเร็จ
  [ ] 1h  - Handle concurrent refresh: ใช้ @Transactional + PESSIMISTIC_WRITE lock
                เพื่อป้องกัน 2 scheduler instance refresh token เดียวกันพร้อมกัน
  [ ] 1h  - เขียน Unit + Integration tests

💭 วิธีคิดเรื่อง Concurrent Refresh (สำคัญมาก!):
  ปัญหา: ถ้า deploy แบบ multiple instances (2 pods) → ทั้งคู่ schedule พร้อมกัน
          → ทั้งคู่ query เจอ token เดียวกัน → ทั้งคู่ refresh → เหลือ token ชุดหนึ่งที่ valid
          → pod แรก save token A, pod สอง save token B → token A ถูก override → A invalid

  วิธีแก้: SELECT FOR UPDATE (PESSIMISTIC_WRITE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM ShopeeToken t WHERE t.expiresAt < :threshold")
    List<ShopeeToken> findTokensToRefresh(@Param("threshold") LocalDateTime threshold);

    → เมื่อ pod 1 lock row → pod 2 รอ → pod 1 refresh + update + release lock
    → pod 2 ได้ lock → query ใหม่ → row นี้ updated แล้ว หมดอายุอีก 4h → ไม่ต้อง refresh

─────────────────────────────────────────────────
SHOP-001-5: แจ้งเตือนหมดอายุ Re-auth
─────────────────────────────────────────────────
Goal:  แจ้งเตือน Seller เมื่อ refresh_token ใกล้หมดอายุ (ต้อง reconnect ร้านใหม่)

Tasks:
  [ ] 1h  - สร้าง ExpiryNotificationScheduler (@Scheduled ทุกวัน 08:00)
  [ ] 1h  - Query: token ที่ refresh_token_expires_at อยู่ใน 7 วัน หรือ 1 วัน
  [ ] 2h  - เรียก Notification Service (email/in-app)
                ถ้า notification service ยังไม่มี → log warning + TODO comment
  [ ] 1h  - เขียน Unit Test

💭 วิธีคิด: "ทำไมต้องแยก scheduler สำหรับ access_token กับ refresh_token"
  access_token: หมดบ่อย (~4h) → ระบบ refresh อัตโนมัติได้ → ผู้ใช้ไม่ต้องรู้
  refresh_token: หมดนาน (~30d) → ระบบทำอะไรไม่ได้ (ต้อง ผู้ใช้ re-auth) → ต้องเตือน
  → logic ต่างกันมาก → แยก scheduler ดีกว่า
```

#### ความเสี่ยงทางเทคนิค Sprint 3

```
⚠️ ความเสี่ยง: Notification service ยังไม่มี
   Mitigation: Sprint 3 implement notification logic ครบ แต่ใช้ log/email stub
               Sprint ถัดไปค่อย wire กับ real notification

⚠️ ความเสี่ยง: @Scheduled ไม่ fire ถ้า application down ช่วง schedule time
   Mitigation: ยอมรับ limitation นี้ใน Sprint 3 (ใช้ scheduled job แบบ stateless)
               Production-grade solution (distributed scheduler) เป็น separate backlog item
```

---

### 📝 User Stories — Sprint 3

#### SHOP-001-4: Auto-refresh Access Token Scheduler

```
User Story:
  ในฐานะระบบ
  ฉันต้องการ refresh access_token ของ Shopee อัตโนมัติก่อนหมดอายุ
  เพื่อที่การเรียก Shopee API จะไม่ล้มเหลวจาก token หมดอายุโดยที่ผู้ใช้ไม่รู้

Points:  5
Priority: Must Have

Acceptance Criteria:
  ─── Happy Path ───────────────────────────────────────────────────────────
  Given  มี token ที่ access_token_expires_at ≤ 30 นาทีข้างหน้า
  When   Scheduler รันทุก 30 นาที
  Then   ระบบเรียก Shopee refresh token API
  And    access_token ใหม่และ expires_at ใหม่ถูก update ใน DB
  And    updated_at ของ record นั้นถูกอัปเดตด้วย

  ─── Concurrent Safety ────────────────────────────────────────────────────
  Given  มี 2 instances ของ application รันพร้อมกัน
  When   ทั้งสอง instance schedule refresh พร้อมกัน
  Then   token ถูก refresh เพียงครั้งเดียว (ไม่ double-refresh)
  And    DB ไม่มี race condition หรือ inconsistency

  ─── Failure Handling ─────────────────────────────────────────────────────
  Given  Shopee refresh API ไม่ตอบสนอง (timeout)
  When   Scheduler พยายาม refresh
  Then   Log error พร้อม shop_id และ timestamp
  And    ลอง retry อีก 1 ครั้งใน 5 นาที
  And    ถ้ายังไม่สำเร็จ → mark token ว่า "refresh_failed" สำหรับ monitoring
```

#### SHOP-001-5: แจ้งเตือนหมดอายุ Re-auth

```
User Story:
  ในฐานะ Seller ที่เชื่อมต่อร้านกับ SmartPick
  ฉันต้องการรับการแจ้งเตือนเมื่อ refresh_token ใกล้หมดอายุ
  เพื่อที่ฉันจะ reconnect ร้านได้ทันเวลาก่อนระบบหยุดทำงาน

Points:  3
Priority: Should Have

Acceptance Criteria:
  ─── Notification Trigger ─────────────────────────────────────────────────
  Given  มี token ที่ refresh_token_expires_at = วันนี้ + 7 วัน
  When   Notification scheduler รันทุกวันเวลา 08:00
  Then   Seller ได้รับการแจ้งเตือน: "refresh token จะหมดอายุใน 7 วัน"
  And    แจ้งเตือนซ้ำอีกครั้งเมื่อเหลือ 1 วัน

  ─── No Duplicate Notification ────────────────────────────────────────────
  Given  แจ้งเตือน 7 วันส่งแล้ว
  When   Scheduler รันวันถัดไป
  Then   ไม่ส่งแจ้งเตือน 7 วันซ้ำอีก (เว้นแต่เหลือ ≤ 1 วัน)

  ─── Content ──────────────────────────────────────────────────────────────
  And    แจ้งเตือนมี: ชื่อร้าน, วันหมดอายุ, ลิงก์สำหรับ reconnect

💭 หมายเหตุสำหรับ Dev:
  ถ้า notification service ยังไม่มี → implement logic ครบ + log ข้อความ
  stub notification call ไว้ก่อน → งาน wire จริงเป็น separate story ใน backlog
  อย่า block story นี้เพราะรอ notification service
```

---

### ✅ DoR + DoD — Sprint 3

```
Definition of Ready:
  SHOP-001-4:
    [✓] AC ชัดเจน รวม concurrent safety requirement
    [✓] Shopee refresh token API spec อ่านแล้ว
    [✓] ประมาณการ 5 pts (รวม PESSIMISTIC_WRITE complexity)
    [✓] Sprint 2 เสร็จแล้ว (token อยู่ใน DB)

  SHOP-001-5:
    [✓] Notification channel กำหนดแล้ว (หรือตกลงใช้ stub)
    [✓] Threshold 7 วัน + 1 วัน ยืนยันกับ PO
    [✓] ประมาณการ 3 pts

Definition of Done:
  SHOP-001-4:
    [ ] Unit test สำหรับ token selection query ผ่าน
    [ ] Concurrent safety test ผ่าน (simulate 2 concurrent calls)
    [ ] Retry logic ทำงานถูกต้อง
    [ ] Log error มี shop_id และ timestamp
    [ ] Deploy ไป SIT
    [ ] PO ตรวจ: simulate token expiry → scheduler refresh → DB updated

  SHOP-001-5:
    [ ] Notification trigger ถูกต้องทั้ง 7 วัน และ 1 วัน
    [ ] ไม่มี duplicate notification
    [ ] Unit test ผ่าน
    [ ] Deploy ไป SIT
    [ ] PO ยืนยัน notification content ถูกต้อง
```

---

## 6. Sprint 4 — ดึงออเดอร์ + Resilience

```
Sprint:     4 / 5
ระยะเวลา:  สัปดาห์ที่ 7–8 (22 มิถุนายน – 5 กรกฎาคม 2569)
ความจุ:    8 Story Points (Stretch)
Stories:   SHOP-002-1 (5 pts) + SHOP-002-2 (3 pts)
```

---

### 🎯 Sprint Goal

> **"เริ่มส่งมอบคุณค่าทางธุรกิจ: ดึงและแสดงรายการออเดอร์ Shopee ได้อย่างเชื่อถือได้แม้ API มีปัญหาชั่วคราว"**

#### 💭 วิธีคิด Sprint Goal นี้

```
Sprint 1-3: เชื่อมต่อ + token management (infrastructure)
Sprint 4:   ใช้การเชื่อมต่อนั้นเพื่อ "ทำงานจริง" กับ Shopee

Goal รวม 2 stories เพราะ:
  - SHOP-002-1 ดึงออเดอร์ได้ → คุณค่าทันที
  - SHOP-002-2 error handling → ทำให้ SHOP-002-1 เชื่อถือได้ในระยะยาว
  - ดึงออเดอร์ได้แต่ไม่มี error handling → production ใช้ไม่ได้จริง
  → Sprint Goal ต้องสื่อทั้งคุณค่าและความน่าเชื่อถือ
```

---

### 👔 PO View — Sprint 4

```
Business Value:
  Sprint นี้คือ "first real business feature" — ระบบเริ่มดึงข้อมูลจาก Shopee ได้จริง
  - Seller เห็นออเดอร์ใน SmartPick (ไม่ต้องเปิด Shopee app แยก)
  - ระบบทนต่อ Shopee API ที่อาจช้าหรือ down ชั่วคราว (circuit breaker)

Demo ใน Sprint Review:
  1. เรียก GET /shopee/orders?shop_id=12345
  2. เห็น order list พร้อม order_id, status, amount
  3. Simulate Shopee API error → ระบบ retry และ return meaningful error (ไม่ crash)

สิ่งที่ PO ต้องเตรียม:
  - กำหนด Order fields ที่ต้องการ (order_id, status, total_amount, created_at, ?)
  - กำหนด filter parameters: date range, status filter, pagination?
  - Order data ใน Shopee Sandbox พร้อมไหม? (มี test order อยู่ใน test seller account)
```

---

### 👨‍💻 Dev View — Sprint 4

#### Technical Approach

```
SHOP-002-1: ดึงรายการออเดอร์ Shopee
─────────────────────────────────────────────────
Goal:   GET /shopee/orders?shop_id=X&from=Y&to=Z → return order list

Flow:
  GET /shopee/orders?shop_id=12345&from=1716000000&to=1716086400
    → ShopeeOrderAPI.getOrders(shopId, from, to, pageSize)
    → GET https://partner.shopeemobile.com/api/v2/order/get_order_list
          ?partner_id=...&shopid=12345&...&sign=...
    → Parse response → List<OrderDTO>
    → Return JSON

Tasks:
  [ ] 1h  - สร้าง ShopeeOrderAPI.java
  [ ] 2h  - Implement getOrders() ด้วย HTTP GET + sign + parse response
  [ ] 1h  - สร้าง OrderDTO (order_id, status, total_amount, created_at)
  [ ] 1h  - สร้าง REST endpoint: GET /shopee/orders
  [ ] 1h  - Handle pagination (Shopee ส่งมาแบบ page token)
  [ ] 1h  - Unit test กับ mock response

─────────────────────────────────────────────────
SHOP-002-2: Error Handling + Retry + Circuit Breaker
─────────────────────────────────────────────────
Goal:   ทำให้ทุก Shopee API call มี retry และ circuit breaker

Tasks:
  [ ] 1h  - เพิ่ม @Retryable บน method ที่ call Shopee API
                @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
  [ ] 2h  - ตั้ง Resilience4j CircuitBreaker
                - threshold: 50% failure rate ใน 10 calls → open circuit
                - wait duration: 30 วินาที → half-open → probe
  [ ] 1h  - Return graceful error response เมื่อ circuit open
  [ ] 1h  - เขียน test สำหรับ retry และ circuit breaker behavior

💭 วิธีคิด Circuit Breaker:
  Shopee API อาจมี downtime หรือ rate limit
  ถ้าไม่มี circuit breaker: ทุก request รอ timeout (10s) → ระบบช้ามาก
  Circuit Breaker เปิดหลังเจอ error เยอะ → fail fast (1ms) แทน timeout
  → User experience ดีขึ้นมาก แม้ Shopee จะ down
```

---

### 📝 User Stories — Sprint 4

#### SHOP-002-1: ดึงรายการออเดอร์ Shopee

```
User Story:
  ในฐานะ Seller ที่เชื่อมต่อร้านกับ SmartPick แล้ว
  ฉันต้องการดูรายการออเดอร์ Shopee ใน SmartPick
  เพื่อที่ฉันจะจัดการออเดอร์ได้โดยไม่ต้องเปิดหลายหน้าต่าง

Points:  5
Priority: Must Have

Acceptance Criteria:
  ─── Happy Path ───────────────────────────────────────────────────────────
  Given  shop_id=12345 มี valid access_token ใน DB
  When   GET /shopee/orders?shop_id=12345&from_date=2026-05-01&to_date=2026-05-11
  Then   ได้รับรายการออเดอร์ที่มี order_id, status, total_amount, created_at
  And    ออเดอร์เรียงตาม created_at descending (ใหม่สุดก่อน)
  And    Response time ≤ 10 วินาที

  ─── Pagination ───────────────────────────────────────────────────────────
  Given  ออเดอร์มีมากกว่า 50 รายการในช่วงเวลานั้น
  When   GET /shopee/orders?shop_id=12345&page=2&page_size=20
  Then   ได้รับออเดอร์ page ที่ 2 ถูกต้อง
  And    Response มี total_count และ has_more_pages

  ─── Error Cases ──────────────────────────────────────────────────────────
  Given  shop_id ไม่มีใน DB (ยังไม่ได้ connect)
  When   GET /shopee/orders?shop_id=99999
  Then   HTTP 404: "Shop not connected. Please connect your Shopee store first."
```

#### SHOP-002-2: Error Handling + Retry + Circuit Breaker

```
User Story:
  ในฐานะระบบ
  ฉันต้องการให้การเรียก Shopee API มี retry อัตโนมัติและ circuit breaker
  เพื่อที่ระบบจะทนต่อ Shopee API ที่มีปัญหาชั่วคราวโดยไม่ crash หรือค้าง

Points:  3
Priority: Should Have

Acceptance Criteria:
  ─── Retry ────────────────────────────────────────────────────────────────
  Given  Shopee API ล้มเหลวครั้งแรก แต่สำเร็จครั้งที่ 2
  When   ระบบเรียก Shopee API
  Then   ระบบ retry อัตโนมัติสูงสุด 3 ครั้ง
  And    Delay ระหว่าง retry: 1s, 2s, 4s (exponential backoff)
  And    User เห็น success response (ไม่รู้ว่ามี retry เกิดขึ้น)

  ─── Circuit Breaker ──────────────────────────────────────────────────────
  Given  Shopee API ล้มเหลว 5 ครั้งติดต่อกัน (ใน 10 calls)
  When   ระบบพยายามเรียก Shopee API
  Then   Circuit Breaker เปิด → fail fast HTTP 503
  And    Log: "Shopee API circuit breaker OPEN — failing fast"
  And    หลัง 30 วินาที ระบบลอง probe อีกครั้ง (half-open state)
```

---

### ✅ DoR + DoD — Sprint 4

```
Definition of Ready:
  SHOP-002-1:
    [✓] Shopee getOrderList API spec อ่านแล้ว
    [✓] Order fields ที่ต้องการกำหนดแล้ว (PO ยืนยัน)
    [✓] Pagination behavior ตกลงแล้ว
    [✓] ประมาณการ 5 pts
    [✓] Test orders พร้อมใน Shopee Sandbox

  SHOP-002-2:
    [✓] Retry policy กำหนดแล้ว (3 attempts, exponential backoff)
    [✓] Circuit breaker threshold กำหนดแล้ว
    [✓] Resilience4j dependency เพิ่มใน pom.xml แล้ว
    [✓] ประมาณการ 3 pts

Definition of Done:
  SHOP-002-1:
    [ ] ดึงออเดอร์ได้จริงจาก Shopee Sandbox
    [ ] Pagination ทำงานถูกต้อง
    [ ] Error cases return HTTP code ถูกต้อง
    [ ] Unit test + Integration test ผ่าน
    [ ] Deploy ไป SIT
    [ ] PO ตรวจ order list ใน SIT ตรงกับ Shopee Sandbox

  SHOP-002-2:
    [ ] Retry behavior test ผ่าน (mock Shopee ให้ fail ก่อน success)
    [ ] Circuit breaker test ผ่าน (mock continuous failures)
    [ ] Log message ชัดเจน มี context ครบ
    [ ] Deploy ไป SIT
    [ ] PR approved
```

---

## 7. Sprint 5 — Security + Quality พร้อม Production

```
Sprint:     5 / 5
ระยะเวลา:  สัปดาห์ที่ 9–10 (6–19 กรกฎาคม 2569)
ความจุ:    8 Story Points (Stretch)
Stories:   TECH-003 (3 pts) + TECH-004 (2 pts) + TECH-002 (3 pts)
```

---

### 🎯 Sprint Goal

> **"ยกระดับระบบให้พร้อมใช้งานจริงด้วยความปลอดภัยของ token และการทดสอบ integration ที่ครอบคลุม"**

#### 💭 วิธีคิด Sprint Goal นี้

```
Sprint 1-4: ระบบ "ทำงานได้" แต่ยังไม่ "production-ready"

Tech debt ที่สะสมไว้:
  - Token ใน DB ยัง plain text (TECH-003 จะแก้)
  - API endpoint ไม่มี authentication (TECH-004 จะแก้)
  - Integration tests ใช้ Shopee Sandbox จริง → ช้า, flaky (TECH-002 จะแก้ด้วย WireMock)

ทำไม Tech Sprints มีคุณค่า:
  - Security issues → data breach → legal liability → มากกว่าเสียเวลา fix
  - ไม่มี integration tests → ทุก change กลัว break → development ช้าลงใน long run
  - API ไม่มี auth → ใครก็ดึงออเดอร์ได้ → unacceptable ก่อน launch

💭 วิธีอธิบายให้ PO เข้าใจว่าทำไม Sprint นี้สำคัญ:
  "Sprint นี้ไม่ได้เพิ่มฟีเจอร์ใหม่ แต่ทำให้ฟีเจอร์เดิมปลอดภัยและเชื่อถือได้
   ถ้าไม่ทำ: เราเสี่ยง data breach และ CI pipeline ที่ slow/unreliable ใน Sprint ถัดไป"
```

---

### 👔 PO View — Sprint 5

```
Business Value:
  - TECH-003 (Encryption): ปกป้อง token จาก DB breach → compliance requirement
  - TECH-004 (API Key Guard): ป้องกัน unauthorized access → security hygiene
  - TECH-002 (Integration Tests): CI รันเร็วขึ้น, test reliable → dev speed เพิ่ม

ผลลัพธ์หลัง Sprint 5:
  ✅ ระบบพร้อมสำหรับ production deployment
  ✅ Security review ผ่าน (token encrypted, endpoint protected)
  ✅ CI pipeline reliable และรันภายใน 5 นาที

สิ่งที่ PO ต้องตัดสินใจ:
  - API Key สำหรับ Guard จะจัดการยังไง? (env var, key management service?)
  - Encryption algorithm: AES-256 ตกลงไหม?
  - Integration test coverage ต้องการ critical paths อะไรบ้าง?
```

---

### 👨‍💻 Dev View — Sprint 5

#### Technical Approach

```
TECH-003: Token Encryption at Rest
─────────────────────────────────────────────────
Goal:  เก็บ token ใน DB แบบ encrypted (AES-256)

Tasks:
  [ ] 1h  - สร้าง TokenEncryptionService (AES-256-GCM)
  [ ] 1h  - Encrypt token ก่อน save ใน ShopeeTokenService
  [ ] 1h  - Decrypt token หลัง load จาก DB
  [ ] 1h  - Migration: encrypt token ที่มีอยู่แล้วใน DB
  [ ] 1h  - Unit test สำหรับ encrypt/decrypt roundtrip

💭 วิธีคิด: ทำไม AES-256-GCM?
  - AES-256: key ยาวพอ ทนต่อ brute force
  - GCM mode: มี authentication tag → ตรวจจับ tampering ได้ด้วย
  - ทางเลือก: JPA AttributeConverter → transparent ไม่ต้องแก้ business logic

─────────────────────────────────────────────────
TECH-004: API Key Guard บน Endpoints
─────────────────────────────────────────────────
Goal:  ทุก endpoint ต้องมี X-API-Key header ถูกต้อง

Tasks:
  [ ] 1h  - สร้าง ApiKeyInterceptor หรือ Spring Security config
  [ ] 1h  - Validate X-API-Key header กับ key ที่ config ไว้
  [ ] 1h  - Return HTTP 401 ถ้า key ไม่ถูกต้องหรือไม่มี header

─────────────────────────────────────────────────
TECH-002: Integration Tests — WireMock Shopee API
─────────────────────────────────────────────────
Goal:  แทน Shopee Sandbox tests ด้วย WireMock → เร็ว, reliable, ไม่ต้องอินเตอร์เน็ต

Tasks:
  [ ] 1h  - เพิ่ม WireMock dependency
  [ ] 2h  - สร้าง WireMock stubs สำหรับ:
                - Auth token exchange (success + expired code + Shopee error)
                - Refresh token (success + failure)
                - Get orders (success + pagination + empty + error)
  [ ] 2h  - แปลง integration tests เดิมให้ใช้ WireMock แทน Sandbox
  [ ] 1h  - ตรวจว่า CI pipeline รันเร็วขึ้น

💭 วิธีคิด WireMock:
  ปัญหาของ test กับ Shopee Sandbox จริง:
  - ช้า (network latency)
  - Flaky (Shopee sandbox อาจ down)
  - ต้องมี test account + sandbox credentials
  - ไม่สามารถ simulate error scenarios ได้ทุกกรณี

  WireMock แก้ได้ทุกข้อ: เร็ว, reliable, ควบคุม response ได้ 100%
```

---

### 📝 Acceptance Criteria — Sprint 5

#### TECH-003: Token Encryption at Rest

```
  Given  ระบบบันทึก token ใหม่ลง DB
  When   ดูข้อมูลใน DB โดยตรง (SQL query)
  Then   access_token และ refresh_token ใน DB ต้องไม่อ่านออกเป็น plain text
  And    ระบบยังดึง token ได้และใช้งานได้ปกติ (decrypt อัตโนมัติ)
  And    migration script เข้ารหัส token เดิมที่มีอยู่แล้วทั้งหมด
```

#### TECH-004: API Key Guard

```
  Given  เรียก GET /shopee/orders โดยไม่มี X-API-Key header
  When   Request ถึง server
  Then   HTTP 401 Unauthorized

  Given  เรียกพร้อม X-API-Key: wrong-key
  When   Request ถึง server
  Then   HTTP 401 Unauthorized

  Given  เรียกพร้อม X-API-Key ที่ถูกต้อง
  When   Request ถึง server
  Then   Request ผ่านไปยัง handler ปกติ
```

#### TECH-002: Integration Tests with WireMock

```
  Given  WireMock stubbed Shopee API responses
  When   รัน integration test suite ทั้งหมด
  Then   Tests ผ่านทั้งหมด โดยไม่ต้องเชื่อมต่อ Shopee Sandbox จริง
  And    รันใน CI ภายใน 5 นาที (รวม unit tests)
  And    ครอบคลุม happy path + error scenarios ทุก endpoint
```

---

### ✅ DoR + DoD — Sprint 5

```
Definition of Ready:
  TECH-003:
    [✓] Encryption key จัดเก็บใน environment config (ไม่ใช่ hardcode)
    [✓] ประมาณการ 3 pts
    [✓] Migration strategy กำหนดแล้ว
  TECH-004:
    [✓] API Key management strategy กำหนดแล้ว (env var + rotation plan)
    [✓] ประมาณการ 2 pts
  TECH-002:
    [✓] WireMock version compatible กับ Spring Boot version ที่ใช้
    [✓] Shopee API response samples เก็บไว้แล้ว (จากการ test กับ Sandbox ใน Sprint ก่อน)
    [✓] ประมาณการ 3 pts

Definition of Done:
  TECH-003:
    [ ] Token ใน DB อ่านเป็น ciphertext (SQL ดูได้)
    [ ] ระบบ decrypt + ใช้งาน token ได้ปกติ
    [ ] Existing token migration ผ่าน (ไม่มี plain text เหลือ)
    [ ] Encryption key ไม่อยู่ใน source code
    [ ] Unit test roundtrip ผ่าน
  TECH-004:
    [ ] Unauthorized request → 401
    [ ] Authorized request → pass through
    [ ] ไม่มี endpoint ที่ข้ามการ check (regression test)
  TECH-002:
    [ ] ไม่มี test ที่ call Shopee Sandbox จริง (ยกเว้น manual/exploratory)
    [ ] Coverage ≥ 80% ของ Shopee integration paths
    [ ] CI ผ่านใน < 5 นาที
    [ ] PR approved
    [ ] PO: รับรองว่า Sprint Review demo ผ่านทุก acceptance criteria
```

---

## 8. บัตรอ้างอิงด่วน

### Sprint Goal ทั้ง 5 Sprint

| Sprint | Goal (1 ประโยค) | Business Value หลัก |
|---|---|---|
| **S1** | วางรากฐานและเปิดให้ Seller เริ่มกระบวนการเชื่อมต่อ Shopee ได้ | Unblocks ทุก Sprint ถัดไป |
| **S2** | เชื่อมต่อครบ flow: token ถูกเก็บในระบบถาวร | Seller connect ร้านได้ end-to-end |
| **S3** | Token เสถียร: refresh อัตโนมัติ + แจ้งเตือนหมดอายุ | ลด invisible failures |
| **S4** | ดึงออเดอร์ได้อย่างเชื่อถือได้แม้ Shopee API มีปัญหา | First real business feature |
| **S5** | พร้อม production: ปลอดภัย + integration tests ครอบคลุม | Production readiness |

---

### โครงสร้างความคิดสำหรับทุก Sprint

```
ก่อน Sprint Planning (PO ทำ):
  1. เลือก Stories จาก Product Backlog ที่ priority สูงสุด
  2. ตรวจ DoR — ถ้าไม่ผ่าน → ทำ Refinement ก่อน
  3. เขียน Sprint Goal draft
  4. เตรียมข้อมูล/ตัดสินใจที่ Dev จะถาม

ระหว่าง Sprint Planning (ทีม):
  1. PO อธิบาย Sprint Goal + Business Value (10 นาที)
  2. ทีมอ่าน Stories + ถาม AC (20 นาที)
  3. Dev แตก Tasks จาก Stories (20 นาที)
  4. ประมาณ Capacity ว่ารับได้แค่ไหน (10 นาที)
  5. Commit Sprint Backlog ร่วมกัน (10 นาที)

สัญญาณว่า Sprint Planning ล้มเหลว:
  ❌ Dev ไม่รู้ว่าจะ implement ยังไง → AC ไม่ชัด → Refine ก่อน
  ❌ Stories มากเกิน Capacity → ลด scope หรือเลื่อน Story ออก
  ❌ PO ไม่อยู่ใน planning → รอก่อน อย่าเดา requirement
  ❌ Sprint Goal เขียนเป็น task list → เขียนใหม่ให้เป็น outcome
```

---

### โครงสร้างความคิดสำหรับทุก Refinement

```
PO ถามตัวเอง:               Dev ถามตัวเอง:
  "ทำไม Story นี้สำคัญ?"      "ทำยังไงถึงจะทำได้?"
  "ผู้ใช้ได้อะไร?"             "มีความเสี่ยงอะไร?"
  "Done หน้าตาเป็นยังไง?"      "ใช้เวลานานเท่าไร?"
  "มีกรณีพิเศษอะไรบ้าง?"       "ขึ้นอยู่กับอะไร?"

เมื่อ PO และ Dev ตอบคำถามของกันและกันได้ → Story พร้อมแล้ว
```

---

### Formula: Story Points → Timeline (โปรเจกต์นี้)

```
Backlog รวม:    38 Story Points
Velocity:       ~7 pts/sprint (1 Dev, Mid-level)
Sprint length:  2 สัปดาห์

Timeline:  38 / 7 = ~5.4 Sprints = ~11 สัปดาห์

รายงานกับ PO:
  "คาดว่าจะใช้เวลา 10–12 สัปดาห์ (5–6 Sprints)
   โดยสมมติว่า: Shopee Sandbox credentials พร้อมตั้งแต่ Sprint 1,
   Scope ไม่เปลี่ยน, และไม่มี Sprint ที่ถูก interrupt"
```

---

*เอกสารนี้สร้างโดย: SmartPick Engineering Team*  
*อ้างอิงจาก: AGILE_GUIDE_TH.md*  
*วันที่: 11 พฤษภาคม 2569*  
*สำหรับ: Learning + Working Template (Sprint Planning & Refinement)*

