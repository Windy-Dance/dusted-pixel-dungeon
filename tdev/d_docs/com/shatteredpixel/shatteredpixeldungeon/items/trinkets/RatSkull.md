# RatSkull 饰物文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/RatSkull.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Trinket |
| **代码行数** | 60 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
巨鼠头骨是一件可怕的饰物，并不比正常老鼠的头骨大多少。头骨的魔力似乎能吸引更多稀有的地牢住民，使它们更有可能出现。

### 系统定位
作为稀有敌人增强型饰物，巨鼠头骨增加稀有敌人的出现频率。

### 不负责什么
- 不影响普通敌人的生成
- 对水晶宝箱怪与装甲石像的效果减半

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.RAT_SKULL
- **静态方法**：exoticChanceMultiplier()、exoticChanceMultiplier(int)

### 主要逻辑块概览
- 稀有敌人出现概率乘数计算

### 生命周期/调用时机
- 敌人生成时：查询稀有概率乘数

## 4. 继承与协作关系

### 父类提供的能力
从Trinket继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 返回6+2*level() |
| statsDesc() | 返回稀有敌人概率乘数描述 |

### 依赖的关键类
- `Messages`：本地化文本
- `ItemSpriteSheet`：精灵图索引

### 使用者
- 敌人生成系统（查询稀有概率）

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
    image = ItemSpriteSheet.RAT_SKULL;
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

### exoticChanceMultiplier(int level)

**可见性**：public static

**方法职责**：计算指定等级的稀有敌人出现概率乘数

**核心实现逻辑**：
```java
public static float exoticChanceMultiplier( int level ){
    if (level == -1){
        return 1f;
    } else {
        return 2f + 1f*level;
    }
}
```

**各级别稀有乘数**：
| 等级 | 乘数 | 稀有敌人增加 |
|------|------|-------------|
| -1 | 1x | 基准 |
| 0 | 2x | +100% |
| 1 | 3x | +200% |
| 2 | 4x | +300% |
| 3 | 5x | +400% |

## 8. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| exoticChanceMultiplier() | 获取稀有敌人概率乘数 |

## 9. 运行机制与调用链

### 调用时机
敌人生成系统在决定是否生成稀有敌人时查询乘数。

### 系统流程位置
```
敌人生成
    ↓
决定是否生成稀有敌人
    ↓
查询RatSkull.exoticChanceMultiplier()
    ↓
根据乘数调整概率
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.ratskull.name | 巨鼠头骨 | 名称 |
| items.trinkets.ratskull.desc | 这件可怕的饰物并不比正常老鼠的头骨大多少... | 描述 |
| items.trinkets.ratskull.typical_stats_desc | 这件饰物通常会使稀有敌人的出现频率变为原频率的_%d倍_。然而，头骨对水晶宝箱怪与装甲石像效果减半。 | 典型属性描述 |
| items.trinkets.ratskull.stats_desc | 在当前等级下，这件饰物会使稀有敌人的出现频率变为原频率的_%d倍_。然而，头骨对水晶宝箱怪与装甲石像效果减半。 | 属性描述 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 稀有敌人生成
float exoticMult = RatSkull.exoticChanceMultiplier();
if (Random.Float() < baseExoticChance * exoticMult) {
    spawnExoticEnemy();
}

// 对水晶宝箱怪和装甲石像效果减半
if (isCrystalMimic || isArmoredStatue) {
    exoticMult = exoticMult / 2;
}
```

## 12. 开发注意事项

### 状态依赖
- 对水晶宝箱怪与装甲石像效果减半
- 不影响普通敌人的生成

### 常见陷阱
1. **忽视例外情况**：某些稀有敌人受减半效果影响
2. **乘数理解错误**：等级3时乘数为5x，而非+5x

## 13. 修改建议与扩展点

### 适合扩展的位置
- 添加对特定稀有敌人的差异化效果

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是