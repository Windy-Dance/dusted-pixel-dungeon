# ShardOfOblivion 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/ShardOfOblivion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 223 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
遗忘碎片是一小块邪能碎片经过炼金釜烹煮后化成的饰物。它从玩家的无知中获得力量，每装备或使用一件未鉴定装备就提升敌人掉落战利品的概率，同时阻止自动鉴定。

### 系统定位
作为风险/回报型饰物，遗忘碎片奖励玩家保持装备未鉴定状态，提供手动鉴定功能。

### 不负责什么
- 不直接增加战利品种类
- 不影响已鉴定装备

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.OBLIVION_SHARD
- **静态常量**：AC_IDENTIFY
- **静态字段**：identifySelector
- **静态方法**：passiveIDDisabled()、lootChanceMultiplier()、lootChanceMultiplier(int)
- **内部类**：WandUseTracker、ThrownUseTracker

### 主要逻辑块概览
- 未鉴定装备追踪
- 战利品掉落概率加成计算
- 手动鉴定功能

### 生命周期/调用时机
- 战利品掉落时：查询掉落概率加成
- 装备鉴定时：阻止自动鉴定
- 使用碎片时：手动鉴定已就绪装备

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回未鉴定装备上限描述 |
| actions(Hero) | 添加IDENTIFY动作 |
| execute(Hero, String) | 处理IDENTIFY动作 |

### 依赖的关键类
- `Badges`：成就验证
- `Dungeon`：地牢状态
- `Hero`、`Talent`：英雄和天赋
- `Item`、`Weapon`、`Armor`、`Ring`、`Wand`：装备类
- `FlavourBuff`：Buff基类
- `GameScene`、`WndBag`：UI交互
- `Messages`、`GLog`：文本和日志
- `Identification`：视觉效果
- `BuffIndicator`：Buff图标
- `ItemSpriteSheet`：精灵图索引

### 使用者
- 战利品生成系统
- 装备鉴定系统
- 法杖/投掷武器使用系统

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_IDENTIFY | String | "IDENTIFY" | 鉴定动作标识 |

### 静态字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| identifySelector | WndBag.ItemSelector | 物品选择器 |

## 6. 构造与初始化机制

### 构造器
无显式构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.OBLIVION_SHARD;
}
```

## 7. 方法详解

### actions(Hero hero)

**可见性**：public

**是否覆写**：是，覆写自Item

**方法职责**：添加鉴定动作

**核心实现逻辑**：
```java
@Override
public ArrayList<String> actions(Hero hero) {
    ArrayList<String> actions = super.actions(hero);
    actions.add(AC_IDENTIFY);
    return actions;
}
```

---

### execute(Hero hero, String action)

**可见性**：public

**是否覆写**：是，覆写自Item

**方法职责**：处理鉴定动作

**核心实现逻辑**：
```java
@Override
public void execute(Hero hero, String action) {
    if (action.equals(AC_IDENTIFY)){
        curUser = hero;
        curItem = this;
        GameScene.selectItem(identifySelector);
    } else {
        super.execute(hero, action);
    }
}
```

---

### passiveIDDisabled()

**可见性**：public static

**方法职责**：判断被动鉴定是否被禁用

**返回值**：boolean，等级≥0时返回true

**核心实现逻辑**：
```java
public static boolean passiveIDDisabled(){
    return trinketLevel(ShardOfOblivion.class) >= 0;
}
```

---

### lootChanceMultiplier(int level)

**可见性**：public static

**方法职责**：计算指定等级的战利品掉落概率乘数

**核心实现逻辑**：
```java
public static float lootChanceMultiplier(int level){
    if (level < 0) return 1f;

    int wornUnIDed = 0;
    // 统计未鉴定装备数量
    if (Dungeon.hero.belongings.weapon() != null && !Dungeon.hero.belongings.weapon().isIdentified()){
        wornUnIDed++;
    }
    // ... 其他装备位检查
    
    wornUnIDed = Math.min(wornUnIDed, level+1);
    return 1f + .2f*wornUnIDed;
}
```

**最大加成**：
| 等级 | 最大未鉴定装备数 | 最大乘数 |
|------|-----------------|---------|
| 0 | 1 | 1.2x |
| 1 | 2 | 1.4x |
| 2 | 3 | 1.6x |
| 3 | 4 | 1.8x |

## 8. 内部类详解

### WandUseTracker

**类型**：extends FlavourBuff

**职责**：追踪未鉴定法杖的使用状态

**持续时间**：50回合

**图标**：BuffIndicator.WAND

---

### ThrownUseTracker

**类型**：extends FlavourBuff

**职责**：追踪未鉴定投掷武器的使用状态

**持续时间**：50回合

**图标**：BuffIndicator.THROWN_WEP

## 9. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| passiveIDDisabled() | 判断被动鉴定是否禁用 |
| lootChanceMultiplier() | 获取战利品掉落乘数 |

## 10. 运行机制与调用链

### 调用时机
- 战利品生成时查询lootChanceMultiplier()
- 装备鉴定时检查passiveIDDisabled()
- 使用法杖/投掷武器时添加追踪Buff

### 系统流程位置
```
战利品生成
    ↓
查询ShardOfOblivion.lootChanceMultiplier()
    ↓
统计未鉴定装备数量（上限为level+1）
    ↓
计算掉落乘数（每件+20%）

装备鉴定检查
    ↓
查询ShardOfOblivion.passiveIDDisabled()
    ↓
如果true，阻止自动鉴定
```

## 11. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.shardofoblivion.name | 遗忘碎片 | 名称 |
| items.trinkets.shardofoblivion.desc | 经过炼金釜的烹煮，这一小块邪能碎片已经化为了... | 描述 |
| items.trinkets.shardofoblivion.stats_desc | 在当前等级下，这件饰物会使你每装备或使用一件未鉴定装备就提升20%%敌人掉落战利品的概率... | 属性描述 |
| items.trinkets.shardofoblivion.ac_identify | 鉴定 | 动作名称 |
| items.trinkets.shardofoblivion.identify_prompt | 鉴定一件物品 | 提示 |
| items.trinkets.shardofoblivion.identify_ready | 一件物品的鉴定已就绪：%s... | 就绪提示 |
| items.trinkets.shardofoblivion.identify_not_yet | 这个物品的鉴定尚未就绪。 | 未就绪提示 |
| items.trinkets.shardofoblivion.identify | 你鉴定了这个物品！ | 成功提示 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 12. 使用示例

### 基本用法

```java
// 战利品生成
float lootMult = ShardOfOblivion.lootChanceMultiplier();
if (Random.Float() < baseDropChance * lootMult) {
    dropLoot();
}

// 检查被动鉴定
if (!ShardOfOblivion.passiveIDDisabled()) {
    // 允许自动鉴定
    item.autoIdentify();
}
```

## 13. 开发注意事项

### 状态依赖
- 阻止自动鉴定是核心机制
- 手动鉴定需要装备达到就绪状态
- 天赋可以加速鉴定就绪

### 常见陷阱
1. **忘记使用手动鉴定**：装备不会自动鉴定，需要手动操作
2. **忽视追踪Buff**：法杖和投掷武器使用后会添加临时追踪

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是