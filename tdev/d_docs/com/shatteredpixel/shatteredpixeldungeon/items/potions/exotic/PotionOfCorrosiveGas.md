# PotionOfCorrosiveGas 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/potions/exotic/PotionOfCorrosiveGas.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticPotion |
| **代码行数** | 61 行 |
| **所属模块** | core |
| **官方中文名** | 腐蚀酸雾合剂 |

## 2. 文件职责说明

### 核心职责
腐蚀酸雾合剂是一种投掷型秘卷/合剂，碎裂后释放出致命的腐蚀性酸雾，对范围内所有单位造成持续伤害。

### 系统定位
作为毒气药剂的升级版本，对应普通药剂为毒气药剂（PotionOfToxicGas）。属于必须投掷使用的药剂类型。

### 不负责什么
- 不提供饮用效果（必须投掷使用）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `shatter()`: 碎裂时释放腐蚀酸雾

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticPotion 继承：
- 鉴定状态共享机制
- 价值计算（基于毒气药剂 +20金币）
- 颜色和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `shatter(int)` | 实现碎裂效果：释放腐蚀酸雾 |

### 依赖的关键类
- `CorrosiveGas`: 腐蚀气体Blob
- `Blob`: 气体基类
- `GameScene`: 游戏场景
- `PathFinder`: 路径查找工具

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.POTION_CORROGAS | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.POTION_CORROGAS;
}
```

## 7. 方法详解

### shatter(int)

**可见性**：public

**是否覆写**：是，覆写自 ExoticPotion

**方法职责**：实现碎裂效果，在目标位置释放腐蚀酸雾。

**参数**：
- `cell` (int)：目标格子位置

**返回值**：void

**前置条件**：无

**副作用**：
- 溅射效果
- 播放音效
- 鉴定药剂
- 生成腐蚀气体

**核心实现逻辑**：
```java
@Override
public void shatter( int cell ) {
    splash( cell );
    if (Dungeon.level.heroFOV[cell]) {
        identify();
        Sample.INSTANCE.play( Assets.Sounds.SHATTER );
        Sample.INSTANCE.play( Assets.Sounds.GAS );
    }

    int centerVolume = 25;
    for (int i : PathFinder.NEIGHBOURS8){
        if (!Dungeon.level.solid[cell+i]){
            GameScene.add( Blob.seed( cell+i, 25, CorrosiveGas.class ).setStrength( 2 + Dungeon.scalingDepth()/5));
        } else {
            centerVolume += 25;
        }
    }

    GameScene.add( Blob.seed( cell, centerVolume, CorrosiveGas.class ).setStrength( 2 + Dungeon.scalingDepth()/5));
}
```

**边界情况**：
- 如果周围有实心格子，气体体积会累加到中心位置
- 气体强度随地下城深度增加

## 8. 对外暴露能力

### 显式 API
- `shatter(int)`: 碎裂效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（毒气药剂 + 4能量）
- 通过 Generator 随机生成

### 调用者
- 投掷时调用 `shatter()`

### 系统流程位置
```
投掷 → onThrow() → shatter() → splash() + 生成CorrosiveGas
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.potions.exotic.potionofcorrosivegas.name | 腐蚀酸雾合剂 | 物品名称 |
| items.potions.exotic.potionofcorrosivegas.desc | 打开或摔碎这个密封的药瓶将导致内容物爆发成一团强腐蚀性的锈色酸雾。这种酸雾的扩散速度与致命性都远超毒气。不过稳定性较差，不能在空气中存留太久。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.POTION_CORROGAS: 物品图标
- Assets.Sounds.SHATTER: 碎裂音效
- Assets.Sounds.GAS: 气体音效

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 投掷腐蚀酸雾合剂
PotionOfCorrosiveGas potion = new PotionOfCorrosiveGas();
potion.shatter(targetCell); // 在目标位置释放腐蚀酸雾
```

### 气体强度计算

```java
// 气体强度 = 2 + Dungeon.scalingDepth()/5
// 例如：第10层时强度为 2 + 10/5 = 4
int strength = 2 + Dungeon.scalingDepth()/5;
```

## 12. 开发注意事项

### 状态依赖
- 气体强度随地下城深度增加
- 实心格子会阻挡气体扩散，但体积会累加到中心

### 生命周期耦合
- 气体体积：中心25 + 周边8格各25（若非实心）
- 气体强度随深度增加

### 常见陷阱
1. **必须投掷使用**：属于 mustThrowPots 集合中的药剂
2. **实心格子处理**：周围实心格子会使中心气体体积增加

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改气体体积和强度计算公式

### 不建议修改的位置
- 气体扩散的基础逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是