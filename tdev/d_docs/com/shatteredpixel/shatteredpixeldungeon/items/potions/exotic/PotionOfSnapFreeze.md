# PotionOfSnapFreeze 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/exotic/PotionOfSnapFreeze.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticPotion |
| **代码行数** | 64 行 |
| **所属模块** | core |
| **官方中文名** | 极速冰冻合剂 |

## 2. 文件职责说明

### 核心职责
极速冰冻合剂是一种投掷型秘卷/合剂，碎裂后立即冻结范围内所有目标并施加定身效果。

### 系统定位
作为冰霜药剂的升级版本，对应普通药剂为冰霜药剂（PotionOfFrost）。属于必须投掷使用的药剂类型。

### 不负责什么
- 不提供饮用效果
- 不造成直接伤害（冻结效果可能造成间接伤害）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `shatter()`: 碎裂时冻结周围目标

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticPotion 继承：
- 鉴定状态共享机制
- 价值计算（基于冰霜药剂 +20金币）
- 颜色和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `shatter(int)` | 实现碎裂效果：冻结和定身 |

### 依赖的关键类
- `Freezing`: 冻结效果类
- `Roots`: 定身Buff类
- `Actor`: 角色管理类
- `PathFinder`: 路径查找工具

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.POTION_SNAPFREEZ | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.POTION_SNAPFREEZ;
}
```

## 7. 方法详解

### shatter(int)

**可见性**：public

**是否覆写**：是，覆写自 ExoticPotion

**方法职责**：实现碎裂效果，冻结目标位置周围9格的所有目标并施加定身效果。

**参数**：
- `cell` (int)：目标格子位置

**返回值**：void

**前置条件**：无

**副作用**：
- 溅射效果
- 播放音效
- 鉴定药剂
- 冻结地形
- 定身范围内角色

**核心实现逻辑**：
```java
@Override
public void shatter(int cell) {
    splash( cell );
    if (Dungeon.level.heroFOV[cell]) {
        identify();
        Sample.INSTANCE.play( Assets.Sounds.SHATTER );
    }
    
    for (int offset : PathFinder.NEIGHBOURS9){
        if (!Dungeon.level.solid[cell+offset]) {
            Freezing.affect( cell + offset );
            
            Char ch = Actor.findChar( cell + offset);
            if (ch != null){
                Buff.prolong(ch, Roots.class, Roots.DURATION*2f);
            }
        }
    }
}
```

**边界情况**：
- 范围为9格（中心 + 周围8格）
- 定身持续时间为 Roots.DURATION * 2

## 8. 对外暴露能力

### 显式 API
- `shatter(int)`: 碎裂效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（冰霜药剂 + 4能量）
- 通过 Generator 随机生成

### 调用者
- 投掷时调用 `shatter()`

### 系统流程位置
```
投掷 → onThrow() → shatter() → splash() + Freezing.affect() + Buff.prolong(Roots)
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.exotic.potionofsnapfreeze.name | 极速冰冻合剂 | 物品名称 |
| items.potions.exotic.potionofsnapfreeze.desc | 一旦暴露在空气里，这种化学混合物会瞬间冻结并缠绕范围内一切对象。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.POTION_SNAPFREEZ: 物品图标
- Assets.Sounds.SHATTER: 碎裂音效

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 投掷极速冰冻合剂
PotionOfSnapFreeze potion = new PotionOfSnapFreeze();
potion.shatter(targetCell); // 冻结并定身目标周围9格

// 效果范围：NEIGHBOURS9（中心+周围8格）
// 冻结效果：Freezing.affect()
// 定身效果：Roots.DURATION * 2f
```

### 检查定身状态

```java
// 检查角色是否被定身
Roots roots = ch.buff(Roots.class);
if (roots != null) {
    // 角色被定身
}
```

## 12. 开发注意事项

### 状态依赖
- 冻结效果通过 Freezing.affect() 实现
- 定身效果持续时间为 Roots.DURATION 的两倍

### 生命周期耦合
- 定身效果随时间消散

### 常见陷阱
1. **必须投掷使用**：属于 mustThrowPots 集合中的药剂
2. **范围限定**：只影响目标周围9格（3x3区域）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改定身持续时间
- 可修改影响范围

### 不建议修改的位置
- 冻结效果的核心逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是