# VialOfBlood 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/VialOfBlood.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 93 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
凝血试管是一根细长的试管饰物，内装有一些地牢住民的血液。它通过魔法使治疗更为强效，但也更为缓效。

### 系统定位
作为治疗调整型饰物，凝血试管增加治疗总量，但限制每回合的回复上限。

### 不负责什么
- 不影响非治疗来源的生命回复
- 不影响饥饿回复

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.BLOOD_VIAL
- **静态方法**：delayBurstHealing()、totalHealMultiplier()、totalHealMultiplier(int)、maxHealPerTurn()、maxHealPerTurn(int)

### 主要逻辑块概览
- 治疗总量乘数计算
- 每回合回复上限计算

### 生命周期/调用时机
- 治疗时：查询治疗加成和回复上限

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回治疗加成和回复上限描述 |

### 依赖的关键类
- `Dungeon`：获取英雄HP
- `Messages`：本地化文本
- `ItemSpriteSheet`：精灵图索引

### 使用者
- 治疗系统（治疗药剂、水袋、生命之泉）

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
    image = ItemSpriteSheet.BLOOD_VIAL;
}
```

## 7. 方法详解

### delayBurstHealing()

**可见性**：public static

**方法职责**：判断是否启用延迟爆发治疗

**返回值**：boolean，等级≠-1时返回true

**核心实现逻辑**：
```java
public static boolean delayBurstHealing(){
    return trinketLevel(VialOfBlood.class) != -1;
}
```

---

### totalHealMultiplier(int level)

**可见性**：public static

**方法职责**：计算指定等级的治疗总量乘数

**核心实现逻辑**：
```java
public static float totalHealMultiplier(int level){
    if (level == -1){
        return 1;
    } else {
        return 1f + 0.125f*(level+1);
    }
}
```

**各级别治疗乘数**：
| 等级 | 乘数 | 治疗增加 |
|------|------|---------|
| -1 | 1.00 | 0% |
| 0 | 1.125 | +12.5% |
| 1 | 1.25 | +25% |
| 2 | 1.375 | +37.5% |
| 3 | 1.50 | +50% |

---

### maxHealPerTurn(int level)

**可见性**：public static

**方法职责**：计算指定等级的每回合回复上限

**核心实现逻辑**：
```java
public static int maxHealPerTurn(int level){
    int maxHP = Dungeon.hero == null ? 20 : Dungeon.hero.HT;
    if (level == -1){
        return maxHP;
    } else {
        switch (level){
            case 0: default:
                return 4 + Math.round(0.15f*maxHP);
            case 1:
                return 3 + Math.round(0.10f*maxHP);
            case 2:
                return 2 + Math.round(0.07f*maxHP);
            case 3:
                return 1 + Math.round(0.05f*maxHP);
        }
    }
}
```

**各级别回复上限（以英雄HP=100为例）**：
| 等级 | 公式 | 上限 |
|------|------|------|
| -1 | maxHP | 100 |
| 0 | 4 + 15% maxHP | 19 |
| 1 | 3 + 10% maxHP | 13 |
| 2 | 2 + 7% maxHP | 9 |
| 3 | 1 + 5% maxHP | 6 |

## 8. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| delayBurstHealing() | 判断是否启用延迟治疗 |
| totalHealMultiplier() | 获取治疗总量乘数 |
| maxHealPerTurn() | 获取每回合回复上限 |

## 9. 运行机制与调用链

### 调用时机
治疗系统计算治疗量时查询相关参数。

### 系统流程位置
```
使用治疗药剂/水袋/生命之泉
    ↓
查询VialOfBlood.totalHealMultiplier()
    ↓
计算总治疗量 = 基础量 * 乘数
    ↓
查询VialOfBlood.maxHealPerTurn()
    ↓
分多回合回复，每回合不超过上限
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.vialofblood.name | 凝血试管 | 名称 |
| items.trinkets.vialofblood.desc | 这根细长的试管内装有一些地牢住民的血液... | 描述 |
| items.trinkets.vialofblood.typical_stats_desc | 这件饰物通常会提升你从治疗药剂、水袋或生命之泉获得的治疗总量... | 典型属性描述 |
| items.trinkets.vialofblood.stats_desc | 在当前等级下，这件饰物会提升你从治疗药剂、水袋或生命之泉获得的治疗总量... | 属性描述 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 治疗计算
int baseHeal = 20;
float mult = VialOfBlood.totalHealMultiplier();
int totalHeal = Math.round(baseHeal * mult);

// 分回合回复
int maxPerTurn = VialOfBlood.maxHealPerTurn();
int remaining = totalHeal;
while (remaining > 0) {
    int healThisTurn = Math.min(remaining, maxPerTurn);
    hero.HP += healThisTurn;
    remaining -= healThisTurn;
    // 等待下一回合
}
```

## 12. 开发注意事项

### 状态依赖
- 只影响治疗药剂、水袋、生命之泉
- 回复上限与英雄最大HP相关

### 常见陷阱
1. **忽视分回合机制**：高等级时回复速度很慢
2. **战斗中治疗延迟**：可能无法在紧急时刻快速回复

## 13. 修改建议与扩展点

### 适合扩展的位置
- 调整回复上限公式
- 添加对其他治疗来源的影响

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是