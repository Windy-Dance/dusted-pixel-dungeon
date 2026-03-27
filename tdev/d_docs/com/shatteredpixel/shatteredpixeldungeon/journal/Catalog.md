# Catalog.java - 物品目录系统

## 概述
`Catalog` 枚举是游戏的物品知识追踪系统，负责记录玩家已识别的物品和使用统计。它将所有游戏物品分为装备和消耗品两大类，共22个子分类。

## 分类体系

### 装备分类 (equipmentCatalogs)
- **MELEE_WEAPONS** - 近战武器（T1-T5等级）
- **ARMOR** - 护甲  
- **ENCHANTMENTS** - 武器附魔（普通、稀有、诅咒）
- **GLYPHS** - 护甲符文（普通、稀有、诅咒）
- **THROWN_WEAPONS** - 投掷武器（T1-T5等级）
- **WANDS** - 法杖
- **RINGS** - 戒指
- **ARTIFACTS** - 神器
- **TRINKETS** - 饰品
- **MISC_EQUIPMENT** - 其他装备（破碎封印、灵魂弓等）

### 消耗品分类 (consumableCatalogs)  
- **POTIONS** - 药水
- **SCROLLS** - 卷轴
- **SEEDS** - 种子
- **STONES** - 符石
- **FOOD** - 食物
- **EXOTIC_POTIONS** - 异域药水
- **EXOTIC_SCROLLS** - 异域卷轴  
- **BOMBS** - 炸弹
- **TIPPED_DARTS** - 毒飞镖
- **BREWS_ELIXIRS** - 酿造物/灵药
- **SPELLS** - 法术
- **MISC_CONSUMABLES** - 其他消耗品（金币、钥匙、催化剂等）

## 数据结构

### 状态追踪
每个分类维护两个映射表：
- `seen: LinkedHashMap<Class<?>, Boolean>` - 追踪物品是否被识别
- `useCount: LinkedHashMap<Class<?>, Integer>` - 追踪物品使用次数

### 初始化配置
静态初始化块预填充所有物品类：
```java
static {
    MELEE_WEAPONS.addItems(Generator.Category.WEP_T1.classes);
    MELEE_WEAPONS.addItems(Generator.Category.WEP_T2.classes);
    // ...
    
    FOOD.addItems(Food.class, Pasty.class, MysteryMeat.class, /* ... */);
    // ...
}
```

## 核心API

### 识别状态管理
```java
// 检查物品是否已被识别
boolean isSeen(Class<?> cls)

// 标记物品为已识别  
void setSeen(Class<?> cls)
```

### 使用统计
```java
// 获取物品使用次数
int useCount(Class<?> cls)

// 增加使用次数（单次）
void countUse(Class<?> cls)

// 增加使用次数（多次）
void countUses(Class<?> cls, int uses)
```

### 批量查询
```java
// 获取分类信息
Collection<Class<?>> items()        // 分类中的所有物品
String title()                      // 分类标题（本地化）
int totalItems()                    // 物品总数
int totalSeen()                     // 已识别物品数
```

## 游戏集成

### 成就系统
- 集成旧版徽章系统（v2.5之前）：
  ```java
  catalogBadges.put(MELEE_WEAPONS, Badges.Badge.ALL_WEAPONS_IDENTIFIED);
  ```
- 新版通过 `Badges.validateCatalogBadges()` 验证

### 自动保存
- 状态变更时设置保存标志：
  ```java
  Journal.saveNeeded = true;
  ```

### 使用限制
- 地牢深度>15且分支>0时不计使用次数（避免测试模式影响统计）

## 持久化

### 序列化格式
- 只保存有状态变化的物品（已识别或有使用次数）
- 使用三个并行数组存储：
  - `catalog_classes` - 物品类数组
  - `catalog_seen` - 识别状态数组  
  - `catalog_uses` - 使用次数数组

### 序列化方法
```java
public static void store(Bundle bundle)
public static void restore(Bundle bundle)
```

### 向后兼容
- 支持v2.5之前的旧格式（`catalog_items`）
- 自动迁移旧数据到新格式

## 使用示例

### 物品识别处理
```java
// 当玩家识别一个药水时
if (potion instanceof PotionOfHealing) {
    Catalog.setSeen(PotionOfHealing.class);
}
```

### 使用统计更新
```java
// 使用药水后
Catalog.countUse(PotionOfHealing.class);

// 多次使用（如批量炼金）
Catalog.countUses(PotionOfHealing.class, 3);
```

### UI显示逻辑
```java
// 显示物品目录
for (Catalog cat : Catalog.equipmentCatalogs) {
    int seen = cat.totalSeen();
    int total = cat.totalItems();
    String title = cat.title() + " (" + seen + "/" + total + ")";
    // 显示进度条或列表
}
```

## 设计特点

### 枚举驱动架构
- 利用Java枚举的天然优势
- 静态初始化确保数据一致性
- 类型安全的分类引用

### 内存效率
- 使用 LinkedHashMap 保持插入顺序
- 按需存储（只保存有变化的数据）
- 防止整数溢出（使用 Integer.MAX_VALUE 作为上限）

### 扩展性
- 易于添加新的物品分类
- 支持动态物品注册（通过 Generator.Category）
- 兼容异域物品系统

## 性能考虑

### 查询优化
- O(1) 时间复杂度的状态查询
- 缓存物品到分类的映射关系
- 避免重复的反射操作

### 内存管理
- 控制保存数据量（只保存必要信息）
- 使用原始类型数组减少对象开销
- 合理的默认值设置（避免null检查）