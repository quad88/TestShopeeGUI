# Agile Development Guide
### Core Theory, Practices & Applied Examples

> **Audience:** Developer Lead / Senior Developer  
> **Context:** Applied to SmartPick — Shopee/Lazada Integration Feature  
> **Date:** May 9, 2026

---

## Table of Contents

1. [Agile Manifesto & 12 Principles](#1-agile-manifesto--12-principles)
2. [Scrum Framework Overview](#2-scrum-framework-overview)
3. [Roles & Responsibilities](#3-roles--responsibilities)
4. [Artifacts](#4-artifacts)
5. [Epic vs PBI vs Story vs Task](#5-epic-vs-pbi-vs-story-vs-task)
6. [Writing Good User Stories (INVEST)](#6-writing-good-user-stories-invest)
7. [Acceptance Criteria](#7-acceptance-criteria)
8. [Story Points & Estimation](#8-story-points--estimation)
9. [Planning Poker](#9-planning-poker)
10. [Velocity & Forecasting](#10-velocity--forecasting)
11. [Sprint Events](#11-sprint-events)
12. [Definition of Ready & Definition of Done](#12-definition-of-ready--definition-of-done)
13. [Time Estimates: 1 Developer vs Team](#13-time-estimates-1-developer-vs-team)
14. [Applied Example: Shopee Integration](#14-applied-example-shopee-integration)
15. [Common Mistakes & Anti-Patterns](#15-common-mistakes--anti-patterns)
16. [Quick Reference Card](#16-quick-reference-card)

---

## 1. Agile Manifesto & 12 Principles

### The 4 Values

```
Individuals and interactions    OVER    processes and tools
Working software                OVER    comprehensive documentation
Customer collaboration          OVER    contract negotiation
Responding to change            OVER    following a plan
```

> The right side still has value — but we **prioritize** the left side.

### The 12 Principles (Simplified)

| # | Principle | What it means in practice |
|---|---|---|
| 1 | Satisfy the customer through early, continuous delivery | Ship working features every sprint — not at end of project |
| 2 | Welcome changing requirements, even late | Don't freeze scope; adapt the backlog |
| 3 | Deliver working software frequently (weeks, not months) | 2-week sprints with shippable increment |
| 4 | Business and developers collaborate daily | PO available to answer questions, not just at planning |
| 5 | Build projects around motivated individuals | Trust devs; remove blockers, don't micromanage |
| 6 | Face-to-face conversation is most efficient | Prefer a 10-min call over a 30-message thread |
| 7 | Working software is the primary measure of progress | "90% done" means nothing — working code does |
| 8 | Sustainable pace — indefinitely | No crunch culture; consistent velocity is better than bursts |
| 9 | Continuous attention to technical excellence | Refactoring, clean code, tests = speed long-term |
| 10 | Simplicity — maximize work NOT done | Don't build what hasn't been asked for yet (YAGNI) |
| 11 | Self-organizing teams | Team decides HOW to do the work; PO decides WHAT |
| 12 | Regularly reflect and adjust | Retrospectives apply to process itself |

---

## 2. Scrum Framework Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    PRODUCT BACKLOG                          │
│   (Epics → Stories → prioritized by PO)                    │
└──────────────────────┬──────────────────────────────────────┘
                       │  Sprint Planning
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    SPRINT BACKLOG                           │
│   (Stories selected for this sprint + broken into Tasks)   │
└──────────────────────┬──────────────────────────────────────┘
                       │  2-Week Sprint
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  DAILY SCRUM (15 min)                       │
│   What did I do? What will I do? Any blockers?             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 SPRINT INCREMENT                            │
│   Working, tested, deployable software                     │
└──────────┬───────────────────────────┬──────────────────────┘
           │                           │
           ▼                           ▼
    Sprint Review                Sprint Retrospective
  (demo to PO/stakeholders)    (team reflects on process)
```

---

## 3. Roles & Responsibilities

### Product Owner (PO)
- Owns the **Product Backlog** — decides priority
- Defines **WHAT** to build and **WHY** (business value)
- Writes or approves Acceptance Criteria
- Available to answer questions during the sprint
- **NOT** the one who decides HOW or HOW LONG

### Scrum Master (SM)
- Facilitates Scrum events (planning, retro, standup)
- Removes blockers and protects the team from interruptions
- Coaches the team on Agile practices
- **NOT** a project manager or team lead

### Development Team (Dev Lead + Devs)
- Decides **HOW** to implement stories
- Owns the technical architecture
- Estimates story points
- Self-organizes — no one assigns tasks from outside
- Accountable for Definition of Done

### Developer Lead (Your Role)
- Bridges PO ↔ Dev team on technical feasibility
- Breaks Epics into dev-ready stories
- Identifies technical risks before sprint starts
- Reviews + approves PR/architecture decisions
- Communicates realistic estimates and blockers to PO

---

## 4. Artifacts

### Product Backlog
- Ordered list of everything that might be done
- Never "complete" — grows and evolves
- PO owns it; dev team refinements it together

### Sprint Backlog
- Subset of Product Backlog selected for the current sprint
- Owned by the dev team — PO cannot change it mid-sprint
- Includes the sprint goal

### Increment
- Sum of ALL completed backlog items at end of sprint
- Must meet Definition of Done
- Must be potentially shippable (even if not released)

---

## 5. Epic vs PBI vs Story vs Task

```
PRODUCT BACKLOG
│
├── EPIC  (business capability — multiple sprints)
│   ├── STORY  (user outcome — fits in 1 sprint)
│   │   ├── TASK  (technical step — hours, dev-only)
│   │   ├── TASK
│   │   └── TASK
│   ├── STORY
│   └── STORY
│
├── BUG  (defect — also a PBI)
├── SPIKE  (research/investigation — timeboxed)
└── TECH TASK  (infrastructure, refactor — no direct user value)
```

> **PBI (Product Backlog Item)** = umbrella term for ANYTHING on the backlog.  
> Epic, Story, Bug, Spike, Tech Task are all types of PBI.

### Comparison Table

| | Epic | Story | Task |
|---|---|---|---|
| **Audience** | PO, stakeholders | PO + developers | Developers only |
| **Size** | Multiple sprints | ≤ 1 sprint | Hours (1–8h) |
| **Written as** | Business capability | User story format | Technical action |
| **Estimated in** | T-shirt (S/M/L/XL) or rough SP | Story points | Hours (optional) |
| **Done when** | All child stories done | AC met + DoD passed | Subtask complete |

### When to Split Epic → Stories

Split when you can answer **YES** to all:
1. Can this be built independently without blocking another story in the same sprint?
2. Does it deliver something the user/PO can see, test, or validate?
3. Can QA write test cases for it right now?
4. Can 1 developer finish it within the sprint?

**Real Example (Shopee):**

```
Epic: SHOP-001 — Seller can connect Shopee shop
  ✗ Too big for one sprint → split into:

  Story 1: Generate Shopee auth URL                  ← independent, testable, small ✓
  Story 2: Handle callback + exchange code            ← independent, testable, small ✓
  Story 3: Save tokens to database                   ← depends on Story 2 (break into same sprint)
  Story 4: Auto-refresh access token                 ← independent scheduler logic ✓
```

---

## 6. Writing Good User Stories (INVEST)

Every story must pass the INVEST criteria:

| Letter | Criteria | What to check |
|---|---|---|
| **I** | Independent | Can you build it without requiring another story first? |
| **N** | Negotiable | Is the scope flexible? (not a fixed contract) |
| **V** | Valuable | Does the user/business get something useful? |
| **E** | Estimable | Does the team understand it enough to size it? |
| **S** | Small | Can 1 dev finish it in a sprint? (aim ≤ 8 points) |
| **T** | Testable | Can you write pass/fail test cases for it? |

### User Story Format

```
As a [type of user],
I want [to do something],
So that [I get some value / outcome].
```

### Bad vs Good Stories

| ❌ Bad | ✅ Good |
|---|---|
| "Build Shopee integration" | "As a seller, I want to receive a Shopee auth link so I can connect my shop" |
| "Fix the token thing" | "As a system, I want to auto-refresh the access token before it expires so API calls never fail due to expiry" |
| "Do the database" | "As a system, I want to save Shopee tokens to a database so they persist across server restarts" |
| "Improve performance" | "As a seller, I want order list to load within 3 seconds so I can work without waiting" |

### Story-Writing Rules for Dev Lead
- Write from the **user's perspective**, not the developer's
- One story = one user goal (not two features bundled together)
- Avoid technical implementation detail in the story title
- Technical detail belongs in **Tasks** or **Technical Notes**, not the story itself

---

## 7. Acceptance Criteria

Definition: **conditions that must ALL be true for the story to be accepted by PO**

### Format: Given / When / Then (Gherkin style)

```
Given [initial context / precondition]
When  [action is taken]
Then  [expected outcome]
```

### Example — Token Exchange Story

```
Story: Handle OAuth callback and exchange code for tokens

Given a seller has clicked the Shopee auth link and authorized SmartPick
When Shopee redirects to /shopee/callback?code=ABC123&shop_id=12345
Then the system exchanges the code for access_token and refresh_token
And both tokens are saved to the database with correct expiry timestamps
And the seller's record is created (or updated if already exists)
And the seller is redirected to the SmartPick dashboard with success message
And if the code is invalid or expired, the system returns a clear error (not a 500)
```

### Characteristics of Good AC
- ✅ Testable — can be verified pass/fail
- ✅ Specific — no ambiguous words like "fast", "nice", "proper"
- ✅ Agreed — PO + dev + QA all understand the same thing
- ❌ Not implementation — don't say "save using `TokenRepository.save()`"

---

## 8. Story Points & Estimation

### What Story Points Are

```
Story Points = Relative estimate of ( Effort + Complexity + Uncertainty )
```

| Factor | Low | High |
|---|---|---|
| **Effort** | A few lines of config | Many files, complex logic |
| **Complexity** | Straightforward CRUD | HMAC signing, concurrency, crypto |
| **Uncertainty** | Done this before | New API, unknown external behavior |

### What Story Points Are NOT
- ❌ Hours or days
- ❌ A commitment to a deadline
- ❌ A measure of developer skill or speed
- ❌ Something management should track per person

### Fibonacci Scale

```
1  pt  → Trivial. Config change, typo fix, copy text update.
2  pt  → Simple. Well-understood, no surprises. Similar to something done before.
3  pt  → Small-medium. Light logic, one component, minimal risk.
5  pt  → Medium. Some complexity, a few moving parts, minor unknowns.
8  pt  → Large. Significant complexity, external dependency, or multiple components.
13 pt  → Very large. High uncertainty. Should be split before putting in sprint.
21 pt  → Epic. Must split. Not ready for sprint.
```

### Why Fibonacci (not 1–10)?

The gaps force the team to have a real conversation:
- "Is this a 5 or an 8?" → meaningful discussion about risk
- "Is this a 6 or a 7?" → false precision, wastes time

### Real Examples (Shopee Project)

| Story | Points | Reasoning |
|---|---|---|
| Generate Shopee auth URL | 3 | Known formula, just needs config injection |
| Handle callback + exchange code | 5 | HTTP call + parse response + error cases |
| Save tokens to DB (JPA entity + repo) | 3 | Standard Spring Data JPA, entity is defined |
| Auto-refresh scheduler | 5 | Scheduler logic + concurrency concern (PESSIMISTIC_WRITE) |
| Re-auth expiry alert | 3 | DB query + notification call, AC is clear |
| Fetch order list | 5 | API call + DTO parsing + configurable params |
| Error handling + retry | 3 | `@Retryable` + circuit breaker config |
| Project setup + CI pipeline | 3 | Repeatable process, but env-specific config adds uncertainty |

---

## 9. Planning Poker

### Process

```
1. Dev Lead (or SM) reads story + AC aloud
2. Each team member selects their estimate card (secretly)
3. Everyone reveals simultaneously (prevents anchoring)
4. If consensus → done
5. If spread (e.g., 3 vs 8) → lowest + highest explain reasoning
6. Brief discussion → re-estimate
7. Repeat until consensus (usually 2 rounds max)
```

### Example Discussion

```
Story: Auto-refresh access token scheduler

Dev A estimates: 3  ("It's just a @Scheduled method with a repo query")
Dev B estimates: 8  ("There's a race condition risk — two instances refreshing at same time")

Discussion:
  Dev B: "What if two pods refresh the same token simultaneously?"
  Dev A: "Oh right — we need SELECT FOR UPDATE or a lock"

Re-estimate: Everyone agrees → 5
```

> The disagreement **IS the value** of planning poker — it surfaces hidden complexity.

### Remote Planning Poker Tools
- [PlanningPoker.com](https://planningpoker.com) — free, simple
- [Scrum Poker Online](https://scrumpoker.online) — no registration
- Jira built-in estimation (if using Jira)

---

## 10. Velocity & Forecasting

### Velocity Definition

```
Velocity = Total story points COMPLETED (DoD met) in a sprint
```

Only **completed** stories count. A story that is 90% done = 0 points.

### Calculating Velocity

```
Sprint 1: 12 completed points  (2 stories cut due to blocker)
Sprint 2: 16 completed points
Sprint 3: 15 completed points
──────────────────────────────
Average velocity = (12+16+15) / 3 = 14.3 ≈ 14 points/sprint
```

### Using Velocity to Forecast

```
Remaining backlog: 45 story points
Team average velocity: 14 points/sprint
Sprint duration: 2 weeks

Forecast: 45 / 14 = ~3.2 sprints ≈ 7 weeks remaining
```

> Tell the PO **a range**, not a single date:
> "Based on current velocity, we estimate 6–8 weeks, assuming no scope changes."

### Velocity Pitfalls to Avoid

| ❌ Mistake | ✅ Correct |
|---|---|
| Counting partially done stories | Only count stories meeting DoD |
| Inflating estimates to look faster | Estimate honestly; velocity self-corrects |
| Comparing velocity across teams | Velocity is team-internal; team A's 10 ≠ team B's 10 |
| Using velocity as performance metric | It's a planning tool, not a KPI |

---

## 11. Sprint Events

### Sprint Planning (Start of Sprint)
- **Who:** PO + Dev Team + SM
- **Duration:** Max 2h per 1-week sprint (4h for 2-week sprint)
- **Output:** Sprint Goal + Sprint Backlog (selected stories + tasks)
- **Key question:** "What can we deliver this sprint that meets the Sprint Goal?"

### Daily Scrum / Standup (Every Day)
- **Who:** Dev Team (PO optional, SM facilitates)
- **Duration:** Max 15 minutes
- **Format:**
  ```
  1. What did I complete since yesterday?
  2. What will I work on today?
  3. Do I have any blockers?
  ```
- **Not** a status report to management — it's team coordination

### Sprint Review (End of Sprint)
- **Who:** PO + Dev Team + Stakeholders
- **Duration:** Max 1h per 1-week sprint (2h for 2-week sprint)
- **Output:** PO accepts/rejects stories; feedback collected
- **Format:** Demo working software — not slides, not screenshots

### Sprint Retrospective (After Review)
- **Who:** Dev Team + SM (PO optional)
- **Duration:** Max 45min per 1-week sprint (1.5h for 2-week sprint)
- **Format:**
  ```
  ✅ What went well?     (keep doing this)
  ❌ What didn't work?   (stop doing this)
  💡 What to try next?   (experiment with this)
  ```
- **Output:** 1–3 concrete action items for next sprint (not a wishlist)

### Backlog Refinement (Mid-Sprint)
- **Who:** PO + Dev Team
- **Duration:** ~1h per sprint (not a formal Scrum event — but essential)
- **Purpose:** Groom upcoming stories to be sprint-ready
- **Output:** Stories are estimated, AC is clear, dependencies identified

---

## 12. Definition of Ready & Definition of Done

### Definition of Ready (DoR)
> A story is **ready** to be pulled into a sprint when:

- [ ] User story is written in standard format
- [ ] Acceptance Criteria are written and agreed by PO + team
- [ ] Story is estimated by the dev team
- [ ] Dependencies are identified (external APIs, other stories, infrastructure)
- [ ] UI mockup / API contract available (if applicable)
- [ ] Story is small enough to fit in the sprint

> ⚠️ If a story doesn't meet DoR, it **cannot enter Sprint Planning**.

### Definition of Done (DoD)
> A story is **done** when ALL of these are true:

- [ ] All Acceptance Criteria are met
- [ ] Code is reviewed (PR approved by at least 1 other dev)
- [ ] Unit tests written and passing
- [ ] Integration tests passing
- [ ] No new `System.out.println` — SLF4J logging used
- [ ] No hardcoded credentials or secrets in code
- [ ] Deployed to SIT environment
- [ ] PO has reviewed and accepted the story

> A story with code merged but NOT deployed to SIT = **NOT done**.

---

## 13. Time Estimates: 1 Developer vs Team

### Realistic Capacity for 1 Developer per Sprint (2 weeks)

A developer does NOT code for 10 days × 8 hours = 80 hours per sprint.

**Real usable capacity per sprint (1 developer):**

```
Total days:           10 working days
Minus meetings:       - 1.5 days  (standup, planning, review, retro, refinement)
Minus code review:    - 0.5 days
Minus context switch: - 0.5 days
Minus buffer (bugs):  - 0.5 days
─────────────────────────────────────
Actual coding time:   ~7.5 days ≈ 60 hours
```

**Story points per sprint for 1 developer:**
```
Junior dev:     4–6 points/sprint
Mid-level dev:  6–8 points/sprint
Senior dev:     8–10 points/sprint
```

> Start with **6 points/sprint** as baseline for 1 mid-level developer. Adjust after 2–3 sprints once you have real velocity data.

---

### Revised Sprint Plan for 1 Developer

The earlier plan was designed for a **small team (2–3 devs)**. Here is the realistic plan for **1 developer**:

#### Original Plan (Team of 2–3)
```
Sprint 1: 16 points  (2 weeks)
Sprint 2: 14 points  (2 weeks)
Sprint 3: 10 points  (2 weeks)
Total:    40 points  (~6 weeks)
```

#### Revised Plan (1 Developer, ~7 points/sprint)

| Sprint | Stories | Points | Duration |
|---|---|---|---|
| **Sprint 1** | Project setup + CI pipeline | 3 | Week 1–2 |
| | Generate Shopee auth URL | 3 | |
| | `Total` | **6 pt** | **2 weeks** |
| **Sprint 2** | Handle callback + exchange code | 5 | Week 3–4 |
| | Save tokens to DB | 3 | (partial) |
| | `Total` | **8 pt** (stretch) | **2 weeks** |
| **Sprint 3** | Auto-refresh access token scheduler | 5 | Week 5–6 |
| | Re-auth expiry alert | 3 | (partial) |
| | `Total` | **8 pt** (stretch) | **2 weeks** |
| **Sprint 4** | Fetch order list API | 5 | Week 7–8 |
| | Error handling + retry | 3 | |
| | `Total` | **8 pt** (stretch) | **2 weeks** |
| **Sprint 5** | Token encryption at rest | 3 | Week 9–10 |
| | API key guard on endpoints | 2 | |
| | Integration tests (WireMock) | 3 | |
| | `Total` | **8 pt** (stretch) | **2 weeks** |

**Total: ~40 points / 7 points per sprint average = ~6 sprints = 12 weeks (3 months) for 1 developer**

---

### Team Size Impact on Delivery

```
1 developer  (~7 pt/sprint)  →  40 pt / 7  = ~6 sprints = 12 weeks
2 developers (~13 pt/sprint) →  40 pt / 13 = ~3 sprints =  6 weeks
3 developers (~18 pt/sprint) →  40 pt / 18 = ~2 sprints =  4 weeks
```

> ⚠️ **Brook's Law:** Adding people to a late software project makes it later.  
> Communication overhead grows with team size. 3 devs ≠ 3× speed.

---

### How to Communicate Estimates to PO

Never say: *"It will take X weeks."*

Say: *"Based on our team size and the current backlog (40 story points), and assuming our velocity stabilizes around 7 points per sprint, we forecast delivery in **5–7 sprints (10–14 weeks)**. This assumes no major scope change and that sandbox API credentials are available by Sprint 1."*

**Always include:**
- Range (not single date)
- Assumptions
- Risks that could change the estimate

---

## 14. Applied Example: Shopee Integration

### Full Backlog with Estimates

```
Epic SHOP-001: Shopee Shop Connection
──────────────────────────────────────
  SHOP-001-1  Generate auth URL                         3 pt  Sprint 1
  SHOP-001-2  Handle callback + exchange code           5 pt  Sprint 2
  SHOP-001-3  Save tokens to DB (entity + repo)         3 pt  Sprint 2
  SHOP-001-4  Auto-refresh access token scheduler       5 pt  Sprint 3
  SHOP-001-5  Re-auth expiry alert (7-day + 1-day)      3 pt  Sprint 3

Epic SHOP-002: Shopee Order Sync
──────────────────────────────────────
  SHOP-002-1  Fetch order list API                      5 pt  Sprint 4
  SHOP-002-2  Error handling + retry + circuit breaker  3 pt  Sprint 4

Tech Tasks (no user value, but necessary)
──────────────────────────────────────
  TECH-001    Project setup + DB schema + CI pipeline   3 pt  Sprint 1
  TECH-002    Integration tests — WireMock Shopee API   3 pt  Sprint 5
  TECH-003    Token encryption at rest                  3 pt  Sprint 5
  TECH-004    API key guard on endpoints                2 pt  Sprint 5
──────────────────────────────────────────────────────────────
Total:                                                 38 pt  ~6 sprints (1 dev)
```

### Example Spike

```
Spike: SHOP-SPIKE-001 — Shopee Sandbox API Behavior

Goal: Confirm how Shopee handles expired authorization codes in sandbox
Timebox: 4 hours (0.5 day)
Output: Written notes on:
  - How long code is valid (expected: 10 min)
  - Does sandbox return same error codes as production?
  - Can we test token refresh with sandbox?

Output committed to: Confluence / Notion / project wiki
```

Spikes have **0 story points** (they produce knowledge, not working software). But they protect downstream estimates from guesswork.

---

## 15. Common Mistakes & Anti-Patterns

| Anti-Pattern | What it looks like | What to do instead |
|---|---|---|
| **Waterfall in disguise** | All stories estimated before any built; no changes allowed | Embrace rolling refinement; re-prioritize backlog each sprint |
| **Story = Task** | Story: "Create TokenRepository interface" | Story should be user-value: "System saves tokens to DB so they survive restarts" |
| **Infinite refinement** | Team debates every edge case before coding | Start with happy path. Handle edge cases as they appear. |
| **Velocity pressure** | Manager says "why is velocity dropping?" | Velocity fluctuates. Focus on delivery of value, not the number. |
| **Partial done = done** | "It's done except for tests and deployment" | No. Not done until DoD is met. Count 0 points. |
| **PO in every standup** | PO assigns tasks during standup | Standup is for the dev team. PO observes, doesn't direct. |
| **Gold plating** | Dev adds unrequested features "because it's better" | Build exactly what the AC says. YAGNI (You Aren't Gonna Need It). |
| **No retrospective action** | Team has retro, nothing changes | Each retro must produce ≥1 concrete, actionable change for next sprint |

---

## 16. Quick Reference Card

### Story Format
```
As a [user type],
I want [goal],
So that [value/outcome].
```

### INVEST Checklist
```
I — Independent    N — Negotiable    V — Valuable
E — Estimable      S — Small         T — Testable
```

### Fibonacci Points
```
1=trivial  2=simple  3=small  5=medium  8=large  13=split me!  21=epic
```

### Sprint Events
```
Planning     → select stories + sprint goal
Daily Scrum  → 15 min sync (what did/will/blockers)
Review       → demo to PO + stakeholders
Retro        → process self-improvement
Refinement   → groom upcoming stories (mid-sprint)
```

### DoR / DoD Summary
```
Ready:  story written + AC agreed + estimated + dependencies known
Done:   AC met + PR reviewed + tests pass + deployed to SIT + PO accepted
```

### 1 Developer Capacity
```
~7 story points per 2-week sprint (mid-level)
40 point backlog = ~6 sprints = 12 weeks
Always report as a range with assumptions
```

### Velocity Formula
```
Velocity = Sum of completed story points per sprint
Forecast  = Remaining backlog pts / average velocity = sprints remaining
```

---

*Last updated: May 9, 2026 — SmartPick Engineering Team*

