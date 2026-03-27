# DimensionalSundial 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/DimensionalSundial.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 99 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
位面日晷是一种手持式日晷饰物，能在地牢深处显影。晷影的方位与世界的太阳无关，当日晷不再显影时（夜间），会招致危险。

### 系统定位
作为时间关联型饰物，位面日晷根据现实世界的昼夜时间调整敌人生成速率：昼间减少敌人，夜间增加敌人。

### 不负责什么
- 不直接影响现有敌人的行为
- 不改变Boss战或特殊房间的敌人数量

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.SUNDIAL
- **静态字段**：sundialWarned
- **静态方法**：spawnMultiplierAtCurrentTime()、enemySpawnMultiplierDaytime()、enemySpawnMultiplierNighttime()

### 主要逻辑块概览
- 基于现实时间的昼夜判定
- 敌人生成倍率计算
- 夜间警告机制

### 生命周期/调用时机
- 关卡生成时：查询敌人生成倍率
- 时间变化时：显示夜间警告

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回昼夜敌人生成倍率描述 |

### 依赖的关键类
- `Messages`：本地化文本
- `GLog`：日志输出
- `Calendar`、`GregorianCalendar`：时间判断
- `ItemSpriteSheet`：精灵图索引

### 使用者
- 关卡生成系统（查询spawnMultiplierAtCurrentTime）

## 5. 字段/常量详解

### 静态字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| sundialWarned | boolean | false | 是否已显示夜间警告 |

### 实例字段
无显式实例字段。

## 6. 构造与初始化机制

### 构造器
无显式构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.SUNDIAL;
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
            (int)(100*(1f - enemySpawnMultiplierDaytime(buffedLvl()))),
            (int)(100*(enemySpawnMultiplierNighttime(buffedLvl())-1f)));
    } else {
        return Messages.get(this, "typical_stats_desc",
            (int)(100*(1f - enemySpawnMultiplierDaytime(0))),
            (int)(100*(enemySpawnMultiplierNighttime(0)-1f)));
    }
}
```

---

### spawnMultiplierAtCurrentTime()

**可见性**：public static

**是否覆写**：否

**方法职责**：根据当前时间返回敌人生成倍率

**返回值**：float，昼间返回减少倍率，夜间返回增加倍率，未装备返回1

**核心实现逻辑**：
```java
public static float spawnMultiplierAtCurrentTime(){
    if (trinketLevel(DimensionalSundial.class) != -1) {
        Calendar cal = GregorianCalendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) >= 20 || cal.get(Calendar.HOUR_OF_DAY) <= 7) {
            if (!sundialWarned){
                GLog.w(Messages.get(DimensionalSundial.class, "warning"));
                sundialWarned = true;
            }
            return enemySpawnMultiplierNighttime();
        } else {
            return enemySpawnMultiplierDaytime();
        }
    } else {
        return 1f;
    }
}
```

**时间判定**：
- 夜间：20:00~8:00（次日）
- 昼间：8:00~20:00

---

### enemySpawnMultiplierDaytime()

**可见性**：public static

**方法职责**：获取昼间敌人生成倍率

---

### enemySpawnMultiplierDaytime(int level)

**可见性**：public static

**方法职责**：计算指定等级的昼间敌人生成倍率

**核心实现逻辑**：
```java
public static float enemySpawnMultiplierDaytime( int level ){
    if (level == -1){
        return 1f;
    } else {
        return 0.95f - 0.05f*level;
    }
}
```

**各级别昼间倍率**：
| 等级 | 倍率 | 敌人减少 |
|------|------|---------|
| 0 | 0.95 | 5% |
| 1 | 0.90 | 10% |
| 2 | 0.85 | 15% |
| 3 | 0.80 | 20% |

---

### enemySpawnMultiplierNighttime()

**可见性**：public static

**方法职责**：获取夜间敌人生成倍率

---

### enemySpawnMultiplierNighttime(int level)

**可见性**：public static

**方法职责**：计算指定等级的夜间敌人生成倍率

**核心实现逻辑**：
```java
public static float enemySpawnMultiplierNighttime( int level ){
    if (level == -1){
        return 1f;
    } else {
        return 1.25f + 0.25f*level;
    }
}
```

**各级别夜间倍率**：
| 等级 | 倍率 | 敌人增加 |
|------|------|---------|
| 0 | 1.25 | 25% |
| 1 | 1.50 | 50% |
| 2 | 1.75 | 75% |
| 3 | 2.00 | 100% |

## 8. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| spawnMultiplierAtCurrentTime() | 获取当前时间的敌人生成倍率 |
| enemySpawnMultiplierDaytime() | 获取昼间倍率 |
| enemySpawnMultiplierNighttime() | 获取夜间倍率 |

## 9. 运行机制与调用链

### 调用时机
关卡生成系统在计算敌人数量时调用spawnMultiplierAtCurrentTime()。

### 系统流程位置
```
关卡生成
    ↓
计算敌人数量
    ↓
查询DimensionalSundial.spawnMultiplierAtCurrentTime()
    ↓
获取当前时间（现实世界）
    ↓
返回对应的生成倍率
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.dimensionalsundial.name | 位面日晷 | 名称 |
| items.trinkets.dimensionalsundial.warning | 你的日晷不再显影，这使你倍感不安。 | 夜间警告 |
| items.trinkets.dimensionalsundial.desc | 不知为何，这块小型手持式日晷... | 描述 |
| items.trinkets.dimensionalsundial.typical_stats_desc | 这件饰物通常会在昼间(8:00~20:00)降低... | 典型属性描述 |
| items.trinkets.dimensionalsundial.stats_desc | 在当前等级下，这件饰物会在昼间... | 属性描述 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 获取当前敌人生成倍率
float multiplier = DimensionalSundial.spawnMultiplierAtCurrentTime();
int baseEnemies = 10;
int actualEnemies = Math.round(baseEnemies * multiplier);
```

### 时间判断示例

```java
// 判断当前是昼间还是夜间
Calendar cal = GregorianCalendar.getInstance();
boolean isNighttime = cal.get(Calendar.HOUR_OF_DAY) >= 20 || cal.get(Calendar.HOUR_OF_DAY) <= 7;
```

## 12. 开发注意事项

### 状态依赖
- 效果基于现实世界时间，与游戏内时间无关
- 夜间警告每局游戏只显示一次

### 常见陷阱
1. **时区差异**：使用系统本地时间，不同时区玩家体验不同
2. **昼夜不平衡**：夜间增加的敌人远多于昼间减少的敌人

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改时间判定逻辑（如使用游戏内时间）
- 调整昼夜倍率公式以改善平衡

### 不建议修改的位置
- sundialWarned的重置逻辑（应保持每局警告一次）

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是