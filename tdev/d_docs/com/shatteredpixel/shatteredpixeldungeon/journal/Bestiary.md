# Bestiary.java - 敌人图鉴系统

## 概述
`Bestiary` 枚举是游戏的生物知识追踪系统，记录玩家遭遇的所有敌人、NPC、盟友、陷阱和植物。它将游戏实体分为9个主要分类，追踪每个实体的发现状态和遭遇次数。

## 分类体系

### 敌人分类
- **REGIONAL** - 区域敌人：各层常规怪物（老鼠、蛇、狗头人、骷髅等）
- **BOSSES** - Boss：主要首领（史莱姆王、天狗、DM300、矮人王、Yog-Dzewa等）
- **UNIVERSAL** - 通用敌人：全地牢出现（幽魂、食人鱼、模仿者、雕像等）
- **RARE** - 稀有敌人：特殊变异或罕见敌人（白化鼠、隐士蟹、酸性史莱姆等）
- **QUEST** - 任务敌人：特定任务相关（腐臭鼠、狗头人欺诈者、腐烂之心等）

### 友方单位分类  
- **NEUTRAL** - 中立NPC：商店老板、铁匠、巫师、鼠王等
- **ALLY** - 盟友：镜像分身、鹰魂、玫瑰幽灵英雄等

### 环境分类
- **TRAP** - 陷阱：所有类型的机关陷阱（毒镖、冰冻、闪电、召唤等）
- **PLANT** - 植物：可互动植物（腐莓、日光草、冰冠花、火绒花等）

## 数据结构

### 状态追踪
每个分类维护两个映射表：
- `seen: LinkedHashMap<Class<?>, Boolean>` - 追踪实体是否被遭遇过
- `encounterCount: LinkedHashMap<Class<?>, Integer>` - 追踪遭遇/击杀次数

### 类型转换映射
处理特殊情况的内部类转换：
```java
static {
    classConversions.put(CorpseDust.DustWraith.class, Wraith.class);
    classConversions.put(TenguDartTrap.class, PoisonDartTrap.class);
    classConversions.put(DwarfKing.DKGhoul.class, Ghoul.class);
    // ...
}
```

## 核心API

### 发现状态管理
```java
// 检查实体是否已被发现
boolean isSeen(Class<?> cls)

// 标记实体为已发现
void setSeen(Class<?> cls)
```

### 遭遇统计
```java
// 获取遭遇次数
int encounterCount(Class<?> cls)

// 增加遭遇次数（单次）
void countEncounter(Class<?> cls)

// 增加遭遇次数（多次）  
void countEncounters(Class<?> cls, int encounters)

// 跳过统计（用于Boss清理小怪时）
boolean skipCountingEncounters = false;
```

### 批量查询
```java
// 获取分类信息
Collection<Class<?>> entities()     // 分类中的所有实体
String title()                      // 分类标题（本地化）
int totalEntities()                 // 实体总数  
int totalSeen()                     // 已发现实体数
```

## 游戏集成

### 成就系统
- 集成徽章验证：
  ```java
  Badges.validateCatalogBadges();
  ```

### 自动保存
- 状态变更时设置保存标志：
  ```java
  Journal.saveNeeded = true;
  ```

### 特殊处理
- 自动处理内部类到标准类的转换
- Boss战结束时跳过小怪统计（避免重复计数）

## 持久化

### 序列化格式
- 只保存有状态变化的实体（已发现或有遭遇次数）
- 使用三个并行数组存储：
  - `bestiary_classes` - 实体类数组
  - `bestiary_seen` - 发现状态数组
  - `bestiary_encounters` - 遭遇次数数组

### 序列化方法
```java
public static void store(Bundle bundle)
public static void restore(Bundle bundle)
```

## 使用示例

### 敌人遭遇处理
```java
// 当玩家首次看到某个敌人时
if (!Bestiary.isSeen(Rat.class)) {
    Bestiary.setSeen(Rat.class);
}

// 击败敌人后
Bestiary.countEncounter(Rat.class);
```

### 陷阱触发处理
```java
// 陷阱被触发时
Bestiary.setSeen(PoisonDartTrap.class);
Bestiary.countEncounter(PoisonDartTrap.class);
```

### 植物互动处理
```java
// 玩家踩到植物时
Bestiary.setSeen(Sungrass.class);
Bestiary.countEncounter(Sungrass.class);
```

### UI显示逻辑
```java
// 显示图鉴进度
for (Bestiary cat : Bestiary.values()) {
    int seen = cat.totalSeen();
    int total = cat.totalEntities();
    String title = cat.title() + " (" + seen + "/" + total + ")";
    // 显示进度条或列表
}
```

## 设计特点

### 枚举驱动架构
- 利用Java枚举提供类型安全的分类
- 静态初始化确保完整的游戏实体覆盖
- 支持灵活的分类扩展

### 智能类型处理
- 自动处理内部类和特殊变体
- 统一的接口简化使用逻辑
- 保持数据一致性

### 性能优化
- O(1) 时间复杂度的状态查询
- 高效的遭遇统计更新
- 内存友好的数据结构

## 扩展支持

### 新实体添加
- 只需在相应分类的静态初始化块中添加类引用
- 自动获得完整的追踪支持
- 无缝集成到现有UI系统

### 自定义分类
- 可以轻松添加新的分类枚举值
- 复用现有的状态追踪机制
- 支持独立的显示逻辑

## 与游戏系统的协同

### 图鉴UI
- 提供完整的数据显示接口
- 支持按分类筛选和排序
- 集成本地化文本系统

### 成就追踪
- 为"发现所有X"类成就提供数据基础
- 支持复杂的发现条件组合
- 实时更新成就状态

### 游戏平衡
- 通过遭遇统计分析玩家行为
- 为难度调整提供数据支持
- 帮助设计更合理的敌人分布