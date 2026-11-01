# 📘 记账助手 · 项目文档

---

## 一、产品定位

**一句话定位**
> 一个不需要打开 APP、在聊天框里说一句话就能记账，并自动生成月度账单的工具。

**扩展定位（用于 README / 简历）**
> 轻量智能记账助手：以 QQ 为入口，用户一句话完成记账；基于信封预算法按月分配预算，实时计算剩余可用与日均可用节奏；通过定时推送提供每日小结与周报；AI 自动分类消费；支持多入口扩展与账单数据补全。

**项目代号建议**：`一句话账本` / `OneLineLedger`（比 `qqbot` 更像产品名）

---

## 二、用户洞察（问题定义）

**核心痛点**
> 记账软件失败的原因不是“功能不够”，而是 **记录成本 > 记录收益**。

**三个具体判断**

| 判断 | 含义 |
|---|---|
| **每增加一个“决定”，就增加一次流失** | 输金额、选分类、点确认，30 次记账就是 90 个决定 |
| **用户要的不是“记录”，是“现在还能花多少”** | 记录是手段，决策才是目的 |
| **不会主动打开 App，但一定会看到消息** | 因此推送 > 页面 |

**产品价值观**
> 先存后花 · 不制造压力 · 用数字说话

---

## 三、核心机制（产品内核）

### 1. 信封预算法

```
月可用 ¥3000
├── 餐饮 30%  ¥900
├── 交通 10%  ¥300
├── 娱乐 13%  ¥400
├── 购物 17%  ¥500
└── 储蓄 30%  ¥900   ← 先存后花
```

### 2. 实时反馈公式（全部“智能感”的来源）

```
剩余可用 = 本月预算 − 已支出
日均可用 = 剩余可用 ÷ 剩余天数

实际日均 > 日均可用 × 1.2  → ⚠️ 超速
实际日均 ≈ 日均可用        → ✅ 正常
实际日均 < 日均可用 × 0.8  → 🎉 有结余
```

### 3. 反馈分层原则

| ❌ 制造压力 | ✅ 提供信息 |
|---|---|
| “你已超支！” | “餐饮剩余 ¥312，还有 12 天” |
| “又买奶茶？” | “本周奶茶 4 杯，上周 2 杯” |

> 默认只陈述事实；超过 80% 才提醒，且只提醒一次。

---

## 四、功能需求

### 分层模型

```
【输入层】              【处理层】              【输出层】
QQ 聊天输入 ──┐                          ┌── 即时推送（QQ）
              │                          │
iOS 快捷指令 ──┼──→ 统一业务层 + MySQL ──┼── 网页可视化
              │     （预算/分类/统计）     │
网页表单   ──┘                          └── 定时推送（日结/周报）
```

### 需求优先级

| 优先级 | 功能 | 层 | 状态 |
|---|---|---|---|
| **P0** | 三张表（budget / bill / goal） | 处理 | 待做 |
| **P0** | 聊天记账 → 入库 | 输入 | 待做 |
| **P0** | 每笔回复「剩余 + 日均 + 节奏」 | 输出 | 待做 |
| **P0** | 每晚 21:30 当日小结 | 输出 | 待做 |
| **P1** | 周报（对比上周） | 输出 | |
| **P1** | 80% 预算预警 | 输出 | |
| **P1** | 网页可视化（ECharts） | 输出 | |
| **P1** | 部署上线 + README | — | |
| **P2** | iOS 快捷指令（简版输入） | 输入 | |
| **P2** | 截图 + AI 识别记账 | 输入 | |
| **P2** | 储蓄目标进度 | 处理 | |
| **P3** | 支付宝/微信 CSV 导入 | 输入 | |
| **P3** | 邮箱账单自动解析 | 输入 | |
| **P3** | Redis 多轮对话状态 | 处理 | |

### 明确不做

```
❌ 微信个人号 Hook          ❌ 自动登录抓取账单
❌ 手机 App（用 H5/PWA 代替） ❌ 多租户商业化运营
❌ 自动识别无障碍界面        ❌ 复杂账本分类体系（先 5 类）
```

---

## 五、非功能需求

| 维度 | 要求 |
|---|---|
| **多用户** | 所有表带 `user_id`，数据隔离 |
| **多入口** | 入口做成适配器，业务逻辑只有一份 |
| **隐私** | 截图**只存解析结果**，不保存原图 |
| **性能** | 单笔记账响应 < 2 秒 |
| **成本** | AI 调用按次计费，设置单日上限 |
| **可用性** | 预算未设置时，也必须能记账 |

---

## 六、技术方案

### 技术栈

| 层 | 选型 |
|---|---|
| 语言/框架 | Java 17 + Spring Boot 3 |
| 持久层 | MyBatis-Plus + MySQL 8 |
| 消息接入 | NapCat（OneBot 11 协议，HTTP 上报） |
| HTTP 客户端 | RestTemplate |
| 定时任务 | `@Scheduled` |
| 前端 | 单页 HTML + ECharts（CDN） |
| AI | 多模态大模型（识别截图）+ 文本模型（分类） |
| 部署 | 云服务器（后续） |
| 可选 | Redis（多轮对话状态，后期） |

### 系统架构

```
手机QQ ──> NapCat ──HTTP上报──> Controller 适配层
iOS快捷指令 ──HTTP──>                    │
网页 ──HTTP──>                           ▼
                                  Service 业务层
                        ├── BillService    记账/查询
                        ├── BudgetService  预算/剩余/节奏
                        ├── GoalService    储蓄目标
                        └── NotifyService  推送封装
                                  │
                                MySQL
                                  │
                            ┌─────┴─────┐
                        QQ 推送      Web 图表
```

### 数据模型

**budget（预算）**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 |
| month | CHAR(7) | `2026-11` |
| category | VARCHAR(20) | 分类 |
| amount | DECIMAL(10,2) | 预算额 |

**bill（账单）**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 |
| amount | DECIMAL(10,2) | 金额 |
| category | VARCHAR(20) | 分类 |
| note | VARCHAR(100) | 备注/商户 |
| source | VARCHAR(20) | `chat` / `shortcut` / `csv` / `manual` |
| occurred_at | DATETIME | 消费时间 |

**goal（储蓄目标）**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 |
| name | VARCHAR(50) | 目标名 |
| target_amount | DECIMAL(10,2) | 目标金额 |
| saved_amount | DECIMAL(10,2) | 已存 |
| target_date | DATE | 截止日期 |

```sql
CREATE TABLE budget (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  month CHAR(7) NOT NULL COMMENT '2026-11',
  category VARCHAR(20) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_month_cat (user_id, month, category)
);

CREATE TABLE bill (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  category VARCHAR(20) NOT NULL,
  note VARCHAR(100),
  source VARCHAR(20) NOT NULL DEFAULT 'chat',
  occurred_at DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_user_time (user_id, occurred_at)
);

CREATE TABLE goal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  target_amount DECIMAL(10,2) NOT NULL,
  saved_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
  target_date DATE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 关键设计原则

1. **Service 是唯一业务出口** —— QQ / 网页 / 快捷指令都调同一个方法，绝不各写一份
2. **能算的不存** —— 剩余、日均都是实时计算，避免数据不一致
3. **source 字段统一多数据源** —— 入口可插拔，模型不变
4. **通道抽象为接口** —— `MessageChannel`，未来接微信/企微只加实现类
5. **先记后算** —— 预算未配置也必须能记账

### 接口设计（预留）

```
POST /api/bill           新增账单（网页/快捷指令）
GET  /api/summary        月度汇总（剩余、日均、分类）
GET  /api/bills          账单列表
POST /api/budget         设置预算
```

```json
GET /api/summary?month=2026-11
{
  "totalBudget": 3000,
  "spent": 1860,
  "remaining": 1140,
  "daysLeft": 11,
  "dailyAllowance": 103.6,
  "categories": [
    { "name": "餐饮", "budget": 900, "spent": 620, "remaining": 280 },
    { "name": "娱乐", "budget": 400, "spent": 380, "remaining": 20 }
  ]
}
```

---

## 七、计划

### 里程碑

| 周次 | 日期 | 目标 |
|---|---|---|
| **W1** | 11/2–11/8 | 建表 + 聊天记账入库 + 每笔反馈 |
| **W2** | 11/9–11/15 | 预算设置 + 定时推送 + 周报 |
| **W3** | 11/16–11/22 | 网页可视化（ECharts） |
| **W4** | 11/23–11/29 | 部署上线 + README + 简历初版 |
| **W5** | 11/30–12/6 | iOS 快捷指令（简版输入） |
| **W6+** | 12 月起 | 学业优先，项目只做收尾与打磨 |

### 本周每日拆解

| 天 | 任务 |
|---|---|
| 周一 | 建三张表 + 配置 MyBatis-Plus |
| 周二 | `BillService.insert()` 记账入库 |
| 周三 | `BudgetService.getSummary()` 计算剩余/日均 |
| 周四 | 记账后把「剩余 + 日均 + 节奏」回给用户 |
| 周五 | 定时推送当日小结（`@Scheduled`） |
| 周六 | 整理代码 + 更新 README |
| 周日 | 复盘 + 补进度 |

### 完成判据（Definition of Done）

> **W1 结束时，手机发「午饭 25」，机器人回「餐饮剩余 ¥312，日均 ¥26，节奏正常」。**
> 这一句话出现，产品就立住了。

---

## 八、推广性 = 架构可扩展

| 层级 | 做法 |
|---|---|
| **现在** | 用 QQ 做开发与演示（开发快、成本低） |
| **架构上** | 入口抽象为适配器，业务层与通道解耦 |
| **未来** | 企业微信 / 公众号（官方接口，可商用） |
| **兜底** | Web/H5，无需安装，人人可用 |

> 技术上，**一个机器人即可服务 N 个用户**（`user_id` 隔离）。
> 现实瓶颈是 QQ 号风控与合规，**故不做商业化，只做架构预留。**

---

## 九、简历包装

> **「一句话账本」| Java · Spring Boot · MySQL · NapCat**
>
> - 以 QQ 为入口实现自然语言记账，用户发送「午饭 25」即可完成记录，**单笔输入 ≤ 8 字符、响应 < 2 秒**
> - 基于**信封预算法**按月分配预算，实时计算剩余可用与日均可用节奏，并在每笔记录后返回反馈
> - 通过定时任务提供**每日小结与周报**，形成“记录—反馈—调整”的正向循环
> - 采用**多入口适配 + 统一业务层**架构，支持 QQ、网页表单、iOS 快捷指令接入，业务逻辑单一出口
> - 使用 `source` 字段统一多数据源，预留 CSV 导入与通知监听扩展

**面试可讲的项目难点**

1. 为什么先做“手动”，而不是直接做全自动（技术权衡）
2. 如何在不增加用户操作的前提下降低记账门槛（产品思考）
3. 多入口架构下如何保证业务逻辑只有一份（工程能力）

---

## 十、风险与边界

| 风险 | 应对 |
|---|---|
| **范围蔓延** | 严格执行 P0 清单，做完再扩 |
| **QQ 号风控** | 只用小号，不做群发/营销 |
| **学业冲突** | 12 月起项目降级为维护 |
| **AI 识别错误** | 提供“改 32 餐饮”修正入口 |
| **隐私** | 截图不落盘，仅存解析结果 |

---

