# 文档执行计划

## 一、项目概览

### 1.1 当前状态

| 指标 | 数值 |
|------|------|
| 总源文件数 | 1,319 个 Java 文件 |
| 已有文档 | 686 个文件 (54.1%) |
| 缺失文档 | 581 个文件 (45.9%) |
| 孤版文档 | 43 个（路径不匹配） |

### 1.2 模块分布

| 模块 | 源文件数 | 文档覆盖率 |
|------|---------|-----------|
| core/ | 1,187 | 54.1% |
| SPD-classes/ | 80 | 0% (需新建) |
| desktop/ | 4 | 0% |
| android/ | 4 | 0% |
| ios/ | 2 | 0% |

### 1.3 质量评估结果

基于18个样本文件的评估：
- **A级文档**（高质量）：Bundle, Artifact, Weapon, Armor, Ring, Potion
- **B级文档**（良好）：Scroll, Generator, Item, Plant, Level, Random
- **C级文档**（需改进）：Bomb, Food, SupplyRation, Firebomb

**主要问题**：
- 消息键章节缺失（89%文件）
- 格式不一致
- 具体物品文档过于简略

---

## 二、优先级分类

### 2.1 最高优先级（P0）- 架构核心

这些类定义了游戏的核心架构，必须优先完成：

| 包路径 | 文件数 | 说明 |
|--------|--------|------|
| actors/Actor.java | 1 | 所有定时对象的基类 |
| actors/Char.java | 1 | 所有角色的基类 |
| actors/mobs/Mob.java | 1 | 所有怪物的基类 |
| actors/hero/Hero.java | 1 | 玩家角色 |
| items/Item.java | 1 | 所有物品的基类 |
| levels/Level.java | 1 | 关卡基类 |
| Dungeon.java | 1 | 全局状态管理 |
| SPD-classes/utils/Bundle.java | 1 | 序列化系统 |

### 2.2 高优先级（P1）- 完全缺失的包

| 包路径 | 文件数 | 说明 |
|--------|--------|------|
| levels/rooms/standard/ | 82 | 标准房间（完全缺失） |
| levels/rooms/quest/ | 29 | 任务房间（完全缺失） |
| levels/rooms/special/ | 24 | 特殊房间（完全缺失） |
| levels/rooms/connection/ | 8 | 连接房间（完全缺失） |
| actors/hero/spells/ | 30 | 牧师法术（完全缺失） |
| items/trinkets/ | 19 | 饰物系统（完全缺失） |
| effects/particles/ | 24 | 粒子效果（完全缺失） |

### 2.3 中优先级（P2）- 部分缺失的包

| 包路径 | 缺失数 | 总数 | 说明 |
|--------|--------|------|------|
| levels/traps/ | 33 | 34 | 陷阱系统 |
| items/artifacts/ | 15 | 16 | 神器系统 |
| items/wands/ | 15 | 16 | 法杖系统 |
| items/armor/glyphs/ | 13 | 14 | 护甲刻印 |
| items/potions/exotic/ | 13 | 14 | 合剂药水 |
| items/scrolls/exotic/ | 13 | 14 | 秘卷卷轴 |
| items/spells/ | 13 | 14 | 法术物品 |
| items/stones/ | 13 | 14 | 符石系统 |
| items/food/ | 11 | 12 | 食物系统 |
| items/bombs/ | 10 | 11 | 炸弹系统 |

### 2.4 低优先级（P3）- 引擎工具类

| 包路径 | 文件数 | 说明 |
|--------|--------|------|
| SPD-classes/noosa/ | 23 | 游戏引擎核心 |
| SPD-classes/utils/ | 22 | 工具类 |
| SPD-classes/glwrap/ | 11 | OpenGL 封装 |
| SPD-classes/input/ | 7 | 输入处理 |

---

## 三、执行策略

### 3.1 分阶段执行

#### 阶段1：基础设施（已完成）
- [x] 创建文档规范（DOC_STANDARDS.md）
- [x] 构建术语映射表
- [x] 评估现有文档质量
- [x] 生成覆盖度报告

#### 阶段2：修复孤版文档
- 修复43个路径不匹配的文档
- 统一文档目录结构

#### 阶段3：核心架构文档
- 完成 P0 优先级的8个核心类文档
- 确保架构理解正确

#### 阶段4：批量补充缺失文档
- 按包分组并行处理
- 使用子代理加速进度

#### 阶段5：质量复核
- 文件级复核
- 模块级复核
- 全局5轮复核

### 3.2 并行处理策略

使用子代理并行处理独立的包：

```
核心代理 (Sisyphus)
├── 子代理1: levels/rooms 包 (165文件)
├── 子代理2: actors/hero/spells 包 (30文件)
├── 子代理3: items/trinkets 包 (19文件)
├── 子代理4: effects/particles 包 (24文件)
├── 子代理5: levels/traps 包 (33文件)
├── 子代理6: items/artifacts+wands (31文件)
├── 子代理7: items/exotic 变体 (40文件)
└── 子代理8: SPD-classes 引擎 (80文件)
```

### 3.3 文档生成流程

每个文件的文档生成流程：

1. **读取源码** - 使用 Read 工具读取 Java 源文件
2. **分析结构** - 提取字段、方法、继承关系
3. **核对翻译** - 从术语表获取官方中文翻译
4. **生成文档** - 按14章节模板生成
5. **写入文件** - 保存到对应目录
6. **验证检查** - 确保格式正确、章节完整

---

## 四、工作分解结构 (WBS)

### WBS-1: 修复孤版文档（43个）

| 任务ID | 描述 | 文件数 |
|--------|------|--------|
| WBS-1.1 | 修复 particles 路径 | 24 |
| WBS-1.2 | 修复 SPD-classes 路径 | 11 |
| WBS-1.3 | 处理其他孤版 | 8 |

### WBS-2: 核心架构文档（P0）

| 任务ID | 文件 | 优先级 |
|--------|------|--------|
| WBS-2.1 | Actor.java | P0 |
| WBS-2.2 | Char.java | P0 |
| WBS-2.3 | Mob.java | P0 |
| WBS-2.4 | Hero.java | P0 |
| WBS-2.5 | Item.java | P0 |
| WBS-2.6 | Level.java | P0 |
| WBS-2.7 | Dungeon.java | P0 |
| WBS-2.8 | Bundle.java | P0 |

### WBS-3: 房间系统文档（165文件）

| 任务ID | 包路径 | 文件数 |
|--------|--------|--------|
| WBS-3.1 | levels/rooms/standard/ | 82 |
| WBS-3.2 | levels/rooms/quest/ | 29 |
| WBS-3.3 | levels/rooms/special/ | 24 |
| WBS-3.4 | levels/rooms/connection/ | 8 |
| WBS-3.5 | levels/rooms/sewerboss/ | 7 |
| WBS-3.6 | levels/rooms/secret/ | 14 |
| WBS-3.7 | levels/rooms/ | 1 (Room.java) |

### WBS-4: 法术与技能文档（60文件）

| 任务ID | 包路径 | 文件数 |
|--------|--------|--------|
| WBS-4.1 | actors/hero/spells/ | 30 |
| WBS-4.2 | items/spells/ | 14 |
| WBS-4.3 | actors/hero/abilities/ | 20 |

### WBS-5: 物品系统文档（剩余部分）

| 任务ID | 包路径 | 缺失数 |
|--------|--------|--------|
| WBS-5.1 | items/artifacts/ | 15 |
| WBS-5.2 | items/wands/ | 15 |
| WBS-5.3 | items/trinkets/ | 19 |
| WBS-5.4 | items/armor/glyphs/ | 13 |
| WBS-5.5 | items/potions/exotic/ | 13 |
| WBS-5.6 | items/scrolls/exotic/ | 13 |
| WBS-5.7 | items/stones/ | 13 |
| WBS-5.8 | items/food/ | 11 |
| WBS-5.9 | items/bombs/ | 10 |
| WBS-5.10 | items/armor/curses/ | 8 |
| WBS-5.11 | items/potions/elixirs/ | 9 |
| WBS-5.12 | items/potions/brews/ | 7 |

### WBS-6: 陷阱与效果文档

| 任务ID | 包路径 | 文件数 |
|--------|--------|--------|
| WBS-6.1 | levels/traps/ | 34 |
| WBS-6.2 | effects/particles/ | 24 |
| WBS-6.3 | effects/ | 34 |

### WBS-7: 引擎核心文档（SPD-classes）

| 任务ID | 包路径 | 文件数 |
|--------|--------|--------|
| WBS-7.1 | noosa/ | 23 |
| WBS-7.2 | utils/ | 22 |
| WBS-7.3 | glwrap/ | 11 |
| WBS-7.4 | input/ | 7 |
| WBS-7.5 | gltextures/ | 4 |
| WBS-7.6 | noosa/tweeners/ | 6 |
| WBS-7.7 | noosa/particles/ | 3 |
| WBS-7.8 | noosa/audio/ | 2 |
| WBS-7.9 | noosa/ui/ | 3 |
| WBS-7.10 | glscripts/ | 1 |

### WBS-8: 质量复核

| 任务ID | 描述 |
|--------|------|
| WBS-8.1 | 文件级复核（全部文件） |
| WBS-8.2 | 模块级复核（按模块） |
| WBS-8.3 | 全局复核第1轮 |
| WBS-8.4 | 全局复核第2轮 |
| WBS-8.5 | 全局复核第3轮 |
| WBS-8.6 | 全局复核第4轮 |
| WBS-8.7 | 全局复核第5轮 |

---

## 五、资源分配

### 5.1 代理角色分配

| 代理类型 | 角色 | 适用任务 |
|----------|------|----------|
| explore | 源码分析 | 读取源码、提取结构 |
| deep | 复杂文档 | 架构核心类、复杂逻辑类 |
| quick | 简单文档 | 工具类、枚举、简单类 |
| visual-engineering | UI/视觉类 | sprites, effects, ui |
| oracle | 复核咨询 | 质量复核、架构审查 |

### 5.2 并行度建议

- 最大并行子代理数：8
- 每个子代理处理文件数：20-50
- 每个文件预计耗时：5-15分钟

---

## 六、验收标准

### 6.1 文件级验收

- [ ] 14个章节全部存在
- [ ] 所有字段已覆盖
- [ ] 所有方法已覆盖
- [ ] 中文翻译来自官方文件
- [ ] 没有推测性内容
- [ ] Markdown 格式正确

### 6.2 模块级验收

- [ ] 文档覆盖率 100%
- [ ] 术语使用一致
- [ ] 格式风格一致
- [ ] 无孤版文档

### 6.3 项目级验收

- [ ] 全部 1,319 个源文件有文档
- [ ] 5 轮全局复核完成
- [ ] 质量达到 A/B 级标准

---

## 七、风险管理

### 7.1 风险识别

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| 翻译不一致 | 高 | 中 | 严格使用术语表 |
| 臆测性内容 | 中 | 高 | 多轮复核、源码对照 |
| 格式不统一 | 高 | 低 | 模板强制、自动化检查 |
| 子代理质量偏差 | 中 | 高 | 抽样检查、重新委派 |

### 7.2 质量控制点

1. 每个子代理完成后抽样检查
2. 每个包完成后模块级复核
3. 发现问题立即修正再继续

---

## 八、里程碑

| 里程碑 | 目标 | 预计状态 |
|--------|------|----------|
| M1 | 规范与计划完成 | 已完成 |
| M2 | 孤版文档修复 | 待执行 |
| M3 | P0核心架构文档 | 待执行 |
| M4 | 50%文档覆盖 | 待执行 |
| M5 | 80%文档覆盖 | 待执行 |
| M6 | 100%文档覆盖 | 待执行 |
| M7 | 第1轮全局复核 | 待执行 |
| M8 | 第5轮全局复核 | 待执行 |
| M9 | 项目完成 | 待执行 |

---

## 九、开始执行

执行将按照以下顺序进行：

1. **立即执行**：修复孤版文档
2. **并行执行**：P0核心文档 + 高优先级包
3. **批量执行**：剩余缺失文档
4. **最终复核**：5轮全局复核

准备好开始执行了吗？