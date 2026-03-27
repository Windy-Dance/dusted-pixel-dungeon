# ScrollOfForesight 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfForesight.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 49 行 |
| **所属模块** | core |
| **官方中文名** | 先见秘卷 |

## 2. 文件职责说明

### 核心职责
先见秘卷是一种阅读型秘卷，阅读后为使用者提供先见效果，持续揭示周围的隐藏门和陷阱。

### 系统定位
作为探地卷轴的升级版本，对应普通卷轴为探地卷轴（ScrollOfMagicMapping）。

### 不负责什么
- 不立即揭示整个地图
- 不提供敌人位置感知

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `doRead()`: 阅读效果，添加先见Buff

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算（基于探地卷轴 +30金币）
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读效果：添加先见Buff |

### 依赖的关键类
- `Foresight`: 先见Buff类

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_FORESIGHT | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_FORESIGHT;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读效果，为使用者添加先见Buff。

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    Sample.INSTANCE.play( Assets.Sounds.READ );
    
    Buff.affect(curUser, Foresight.class, Foresight.DURATION);

    identify();
    
    readAnimation();
}
```

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（探地卷轴 + 6能量）
- 通过 Generator 随机生成

### 系统流程位置
```
阅读 → doRead() → Buff.affect() → 先见效果（持续揭示隐藏门和陷阱）
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofforesight.name | 先见秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofforesight.desc | 在阅读了这幅卷轴后，附近地形的细节将自发不断地映入阅读者的脑海。这种效果会持续相当长的一段时间，并且会揭示所有的隐藏门与陷阱，不再需要主动搜索。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_FORESIGHT: 物品图标
- Assets.Sounds.READ: 阅读音效

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 阅读先见秘卷
ScrollOfForesight scroll = new ScrollOfForesight();
scroll.doRead(); // 为使用者添加先见效果

// 先见效果
// 持续时间：Foresight.DURATION
// 效果：持续揭示周围的隐藏门和陷阱
```

### 检查先见状态

```java
Foresight foresight = hero.buff(Foresight.class);
if (foresight != null) {
    // 英雄处于先见状态
}
```

## 12. 开发注意事项

### 状态依赖
- 持续时间由 Foresight.DURATION 定义

### 生命周期耦合
- 先见效果随时间消散

### 常见陷阱
1. **持续性**：与探地卷轴不同，先见效果是持续的而非即时

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改持续时间

### 不建议修改的位置
- 先见效果的核心逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是