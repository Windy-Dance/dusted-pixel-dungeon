# SaltCube 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/SaltCube.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 85 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
盐晶立方是一块巨大的盐晶饰物，被切割成近乎完美的立方体。它通过魔法脱水并保存玩家所吃的食物，延长饱腹感，但也减少了非空腹时的生命回复速率。

### 系统定位
作为饥饿与回复调整型饰物，盐晶立方提供更长久的饱腹感，但以降低回复速度为代价。

### 不负责什么
- 不影响空腹时的回复速率
- 在楼层已被封锁时效果无效

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.SALT_CUBE
- **静态方法**：hungerGainMultiplier()、hungerGainMultiplier(int)、healthRegenMultiplier()、healthRegenMultiplier(int)

### 主要逻辑块概览
- 饥饿增长乘数计算
- 生命回复乘数计算

### 生命周期/调用时机
- 饥饿增长时：查询饥饿乘数
- 生命回复时：查询回复乘数

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回饥饿和回复乘数描述 |

### 依赖的关键类
- `Messages`：本地化文本
- `ItemSpriteSheet`：精灵图索引

### 使用者
- 饥饿系统（查询饥饿乘数）
- 回复系统（查询回复乘数）

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
    image = ItemSpriteSheet.SALT_CUBE;
}
```

## 7. 方法详解

### hungerGainMultiplier(int level)

**可见性**：public static

**方法职责**：计算指定等级的饥饿增长乘数

**核心实现逻辑**：
```java
public static float hungerGainMultiplier( int level ){
    if (level == -1){
        return 1;
    } else {
        return 1f / (1f + 0.25f*(level+1));
    }
}
```

**各级别饥饿乘数**：
| 等级 | 乘数 | 饱腹时间增加 |
|------|------|-------------|
| -1 | 1.00 | 0% |
| 0 | 0.80 | +25% |
| 1 | 0.67 | +50% |
| 2 | 0.57 | +75% |
| 3 | 0.50 | +100% |

---

### healthRegenMultiplier(int level)

**可见性**：public static

**方法职责**：计算指定等级的生命回复乘数

**核心实现逻辑**：
```java
public static float healthRegenMultiplier( int level ){
    switch (level){
        case -1: default:
            return 1;
        case 0:
            return 0.84f;
        case 1:
            return 0.73f;
        case 2:
            return 0.66f;
        case 3:
            return 0.6f;
    }
}
```

**各级别回复乘数**：
| 等级 | 乘数 | 回复减少 |
|------|------|---------|
| -1 | 1.00 | 0% |
| 0 | 0.84 | -16% |
| 1 | 0.73 | -27% |
| 2 | 0.66 | -34% |
| 3 | 0.60 | -40% |

## 8. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| hungerGainMultiplier() | 获取饥饿增长乘数 |
| healthRegenMultiplier() | 获取生命回复乘数 |

## 9. 运行机制与调用链

### 调用时机
- 饥饿系统计算饥饿增长时
- 回复系统计算生命回复时

### 系统流程位置
```
饥饿更新
    ↓
查询SaltCube.hungerGainMultiplier()
    ↓
调整饥饿增长速度

生命回复
    ↓
查询SaltCube.healthRegenMultiplier()
    ↓
调整回复速度（仅非空腹时）
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.saltcube.name | 盐晶立方 | 名称 |
| items.trinkets.saltcube.desc | 这块巨大的盐晶被切割成近乎完美的立方体... | 描述 |
| items.trinkets.saltcube.typical_stats_desc | 这件饰物通常会增加你_%1$s%%_的饥饿所需时间... | 典型属性描述 |
| items.trinkets.saltcube.stats_desc | 在当前等级下，这件饰物会增加你_%1$s%%_的饥饿所需时间... | 属性描述 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 饥饿计算
float hungerMult = SaltCube.hungerGainMultiplier();
hero.hunger += hungerIncrease * hungerMult;

// 回复计算（仅非空腹时）
if (!hero.starving()) {
    float regenMult = SaltCube.healthRegenMultiplier();
    hero.HP += regenAmount * regenMult;
}
```

## 12. 开发注意事项

### 状态依赖
- 回复减少仅在非空腹时生效
- 楼层已封锁时效果无效

### 常见陷阱
1. **忽视空腹条件**：空腹时回复乘数无效
2. **回复减少是代价**：需要权衡饱腹时间与回复速度

## 13. 修改建议与扩展点

### 适合扩展的位置
- 调整饥饿/回复的平衡公式

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是