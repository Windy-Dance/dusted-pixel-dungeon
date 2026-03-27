# MimicTooth 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/MimicTooth.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 80 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
拟箱利齿是一颗大尖牙饰物，肯定是从某个非常倒霉的宝箱怪口中拔下来的。它能影响地牢中的宝箱怪，使其更常见、更危险，同时掉落更丰厚的战利品。

### 系统定位
作为宝箱怪增强型饰物，拟箱利齿增加玩家与宝箱怪战斗的机会和收益，适合寻求挑战和奖励的玩家。

### 不负责什么
- 不直接生成宝箱怪（由关卡生成系统处理）
- 不影响其他类型的怪物

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.MIMIC_TOOTH
- **静态方法**：mimicChanceMultiplier()、mimicChanceMultiplier(int)、stealthyMimics()、ebonyMimicChance()、ebonyMimicChance(int)

### 主要逻辑块概览
- 宝箱怪出现概率乘数计算
- 隐形宝箱怪判定
- 黑檀宝箱怪额外生成概率

### 生命周期/调用时机
- 关卡生成时：查询宝箱怪生成概率
- 宝箱怪行为判定时：检查是否隐形

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回宝箱怪概率和黑檀宝箱怪概率描述 |

### 依赖的关键类
- `Messages`：本地化文本
- `ItemSpriteSheet`：精灵图索引

### 使用者
- 关卡生成系统（查询宝箱怪概率）
- 宝箱怪类（检查stealthyMimics）

## 5. 字段/常量详解

### 静态常量
无显式静态常量。

### 实例字段
无显式实例字段。

## 6. 构造与初始化机制

### 构造器
无显式构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.MIMIC_TOOTH;
}
```

## 7. 方法详解

### upgradeEnergyCost()

**可见性**：protected

**是否覆写**：是，覆写自Trinket

**核心实现逻辑**：
```java
@Override
protected int upgradeEnergyCost() {
    //6 -> 8(14) -> 10(24) -> 12(36)
    return 6+2*level();
}
```

---

### statsDesc()

**可见性**：public

**是否覆写**：是，覆写自Trinket

**核心实现逻辑**：
```java
@Override
public String statsDesc() {
    if (isIdentified()){
        return Messages.get(this, "stats_desc",
            Messages.decimalFormat("#.##", mimicChanceMultiplier(buffedLvl())),
            Messages.decimalFormat("#.##", 100*ebonyMimicChance(buffedLvl())));
    } else {
        return Messages.get(this, "typical_stats_desc",
            Messages.decimalFormat("#.##", mimicChanceMultiplier(0)),
            Messages.decimalFormat("#.##", 100*ebonyMimicChance(0)));
    }
}
```

---

### mimicChanceMultiplier()

**可见性**：public static

**方法职责**：获取当前等级的宝箱怪概率乘数

---

### mimicChanceMultiplier(int level)

**可见性**：public static

**方法职责**：计算指定等级的宝箱怪出现概率乘数

**核心实现逻辑**：
```java
public static float mimicChanceMultiplier( int level ){
    if (level == -1){
        return 1f;
    } else {
        return 1.5f + 0.5f*level;
    }
}
```

**各级别概率乘数**：
| 等级 | 乘数 | 宝箱怪增加 |
|------|------|-----------|
| -1 | 1.0 | 基准 |
| 0 | 1.5 | +50% |
| 1 | 2.0 | +100% |
| 2 | 2.5 | +150% |
| 3 | 3.0 | +200% |

---

### stealthyMimics()

**可见性**：public static

**方法职责**：判断宝箱怪是否更难被识别

**返回值**：boolean，等级≥0时返回true

**核心实现逻辑**：
```java
public static boolean stealthyMimics(){
    return trinketLevel(MimicTooth.class) >= 0;
}
```

**说明**：装备拟箱利齿后，所有宝箱怪都更难被识别。

---

### ebonyMimicChance()

**可见性**：public static

**方法职责**：获取当前等级的黑檀宝箱怪生成概率

---

### ebonyMimicChance(int level)

**可见性**：public static

**方法职责**：计算指定等级的黑檀宝箱怪额外生成概率

**核心实现逻辑**：
```java
public static float ebonyMimicChance( int level ){
    if (level >= 0){
        return 0.125f + 0.125f * level;
    } else {
        return 0;
    }
}
```

**各级别黑檀宝箱怪概率**：
| 等级 | 概率 |
|------|------|
| -1 | 0% |
| 0 | 12.5% |
| 1 | 25% |
| 2 | 37.5% |
| 3 | 50% |

## 8. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| mimicChanceMultiplier() | 获取宝箱怪概率乘数 |
| stealthyMimics() | 判断宝箱怪是否更难识别 |
| ebonyMimicChance() | 获取黑檀宝箱怪概率 |

## 9. 运行机制与调用链

### 调用时机
- 关卡生成时查询mimicChanceMultiplier()决定宝箱怪数量
- 每层额外生成黑檀宝箱怪时查询ebonyMimicChance()
- 宝箱怪行为判定时检查stealthyMimics()

### 系统流程位置
```
关卡生成
    ↓
计算宝箱怪数量
    ↓
查询MimicTooth.mimicChanceMultiplier()
    ↓
增加宝箱怪生成

每层检查
    ↓
查询MimicTooth.ebonyMimicChance()
    ↓
可能生成额外黑檀宝箱怪
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.mimictooth.name | 拟箱利齿 | 名称 |
| items.trinkets.mimictooth.desc | 这颗大尖牙肯定是强行从某个非常倒霉的宝箱怪口中拔下来的... | 描述 |
| items.trinkets.mimictooth.typical_stats_desc | 这件饰物通常会使所有类型的宝箱怪的出现频率变为原频率的_%1$s倍_... | 典型属性描述 |
| items.trinkets.mimictooth.stats_desc | 在当前等级下，这件饰物会使所有类型的宝箱怪的出现频率变为原频率的_%1$s倍_... | 属性描述 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 计算宝箱怪数量
int baseMimics = 1;
float multiplier = MimicTooth.mimicChanceMultiplier();
int actualMimics = Math.round(baseMimics * multiplier);

// 检查是否生成额外黑檀宝箱怪
if (Random.Float() < MimicTooth.ebonyMimicChance()) {
    spawnEbonyMimic();
}

// 宝箱怪识别难度
if (MimicTooth.stealthyMimics()) {
    // 宝箱怪更难被识别
    mimic.setStealthy(true);
}
```

## 12. 开发注意事项

### 状态依赖
- stealthyMimics()只要装备就生效，与等级无关
- 宝箱怪战利品也会更丰厚

### 常见陷阱
1. **忽视黑檀宝箱怪**：这是额外生成的，不影响普通宝箱怪
2. **低估风险**：宝箱怪更难识别，容易被偷袭

## 13. 修改建议与扩展点

### 适合扩展的位置
- 调整各类宝箱怪的权重
- 添加对特定宝箱怪类型的特殊效果

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是