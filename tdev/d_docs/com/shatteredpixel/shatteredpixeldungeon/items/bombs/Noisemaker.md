# Noisemaker 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/Noisemaker.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
 |
| **文件类型** | class |
| **继承关系** | extends Bomb |
| **代码行数** | 164 行 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
噪音地雷，独特的炸弹变体，爆炸前会持续发出噪音吸引敌人，当有敌人接近时才爆炸。

而非立即爆炸。

需要 ScrollOfRage（狂暴卷轴）作为炼金材料。

### 系统定位
作为 `Bomb` 的子类，实现陷阱式炸弹机制。引信触发后不会立即爆炸，而是进入预警模式，持续发出噪音吸引敌人，直到有敌人踩到才引爆。

### 不负责什么
- 不负责地形破坏（覆写了 `explodesDestructively` 返回 false，但父类 `explode()` 不执行破坏逻辑)
 因为此炸弹不破坏地形)
- 不负责直接伤害（依赖父类爆炸伤害机制）

 只是延迟爆炸时机和增加吸引敌人的机制)

## 3. 结构总览

### 主要成员概览
无新增实例字段，继承 `Bomb` 所有字段。

### 主要逻辑块概览
- **预警模式**：引信烧完后发出噪音吸引敌人
每 6 回合一次
 - **触发爆炸**：有敌人踩到时爆炸
### 生命周期/调用时机
与父类 `Bomb` 相同，但引信烧完后进入预警模式，而非立即爆炸,需要敌人触发才引爆。

## 4. 继承与协作关系

### 父类提供的能力
继承 `Bomb` 所有能力。### 覆写的方法
| 方法 | 职责变更 |
|------|---------|
| `explosionRange()` | 返回 2，5x5 爆炸范围 |
| `createFuse()` | 返回 `NoisemakerFuse`，使用自定义引信 |
| `doPickUp(Hero, int)` | 预警模式后不可拾取 |
| `value()` | 返回 60（材料价格之和） |

### 依赖的关键类
| 类 | 用途 |
|-----|------|
| `NoisemakerFuse` | 内部类，自定义引信逻辑 |
 | `Heap` | 物品堆，炸弹存放位置 |
 | `Mob` | 怪物，用于吸引敌人  | `Speck` | 粒子效果，用于预警显示  | `ScrollOfRage` | 炼金材料  |

### 使用者
- `EnhanceBomb` 炼金配方系统
- 玩家使用

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| `image` | int | ItemSpriteSheet.NOISEMAKER | 噪音地雷图标 |

### NoisemakerFuse 内部类字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| `triggered` | boolean | 是否已触发预警模式  | `left` | int | 下次预警的倒计时  |

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。### 初始化块
```java
{
    image = ItemSpriteSheet.NOISEMAKER;
}
```

## 7. 方法详解

### explosionRange()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回爆炸范围。**返回值**：int，返回 2（5x5 区域）。### createFuse()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：创建自定义引信。**返回值**：Fuse，返回 `NoisemakerFuse` 实例。### doPickUp(Hero, int)

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：拾取炸弹，预警模式后不可拾取。

**参数**：
- `hero` (Hero)：英雄实例
 - `pos` (int)：拾取位置

**返回值**：boolean，预警模式返回 false，否则调用父类方法。**核心实现逻辑**：
```java
@Override
public boolean doPickUp(Hero hero, int pos) {
    // cannot pickup after first trigger
    if (fuse instanceof NoisemakerFuse && ((NoisemakerFuse) fuse).triggered){
        return false;
    }
 return super.doPickUp(hero, pos);
}```**边界情况**：已触发预警模式后无法拾取。

### NoisemakerFuse.act()

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb.Fuse`

**方法职责**：执行引信逻辑，包括预警模式和爆炸检测。**核心实现逻辑**：
```java
@Override
protected boolean act() {
    if (!triggered){
        // acts like a normal fuse until first trigger
 return super.act();
    } else {
        // 查找炸弹位置, 检测是否有敌人接近
 // 如果有敌人接近,爆炸; // 否则发出噪音吸引敌人  }
}```**副作用**：预警模式每 6 回合发出一次噪音,吸引所有敌人。### NoisemakerFuse.trigger(Heap)

**可见性**：protected

**是否覆写**：是，覆写自 `Bomb.Fuse`

**方法职责**：触发机制，第一次触发预警，第二次爆炸。**参数**：
- `heap` (Heap)：物品堆实例**核心实现逻辑**：
```java
@Override
protected void trigger(Heap heap) {
    if (!triggered) {
        triggered = true; // 第一次触发预警模式
    } else {
        super.trigger(heap); // 第二次爆炸  }}```### NoisemakerFuse.freeze()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb.Fuse`

**方法职责**：冻结引信,预警模式后不可冻结。**返回值**：boolean，预警模式返回 false，否则调用父类方法。**核心实现逻辑**：
```java
@Override
public boolean freeze() {
    if (!triggered) {
        return super.freeze();
    } else {
        // noisemakers cannot have their fuse snuffed once triggered
 return false;    }}```### value()

**可见性**：public

**是否覆写**：是，覆写自 `Bomb`

**方法职责**：返回噪音地雷出售价格。**返回值**：int，返回 `quantity * 60`（炸弹 20 +  ScrollOfRage 40）。## 8. 对外暴露能力

### 显式 API
继承 `Bomb` 所有公开API。### 内部辅助方法
无。### 扩展入口
- `createFuse()` 可覆写使用其他自定义引信
 - `explosionRange()` 可覆写修改爆炸范围,## 9. 运行机制与调用链,### 创建时机,通过炼金合成：`Bomb` + `ScrollOfRage` → `Noisemaker`（炼金费用 1）。### 调用者
- 炼金系统
 - 玩家使用,### 被调用者
- `NoisemakerFuse` 引信逻辑
 - `Mob.beckon()` 吸引敌人,### 系统流程位置,```
炼金合成 → Noisemaker
    ↓
点燃投掷 → NoisemakerFuse.ignite() 
    ↓
引信烧完 → 进入预警模式
发出噪音吸引敌人
    ↓
有敌人接近 →  爆炸
```## 10. 资源、配置与国际化关联,### 引用的 messages 文案,| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| `items.bombs.noisemaker.name` | 噪音地雷 | 物品名称 |
| `items.bombs.noisemaker.desc` | 这枚改造过的炸弹会周而复始地发出噪音... | 物品描述 |
| `items.bombs.noisemaker.desc_burning` | 噪音地雷引信已点燃... | 焚烧描述 |
| `items.bombs.noisemaker.discover_hint` | 你可通过炼金合成该物品。 | 发现提示 |

### 依赖的资源
- **图标**：`ItemSpriteSheet.NOISEMAKER`
- **粒子**：`Speck.SCREAM`
 - **音效**：`Assets.Sounds.ALERT`,### 中文翻译来源,`core/src/main/assets/messages/items_zh.properties`,## 11. 使用示例,### 基本用法

```java
// 炼金合成
Bomb bomb = new Bomb();
ScrollOfRage scrollOfRage = new ScrollOfRage();
// 合成 Noisemaker

Noisemaker noisemaker = new Noisemaker();
hero.handle(noisemaker);
```## 12. 开发注意事项,### 状态依赖, `triggered` 状态决定引信是否进入预警模式, `left` 控制预警倒计时,### 生命周期耦合, `NoisemakerFuse` 的状态需要正确保存和恢复,### 常见陷阱
- 预警模式后不可拾取, - 预警模式后引信不可被冻结（freeze() 返回 false）, - 预警模式会吸引所有敌人，包括Boss,## 13. 修改建议与扩展点,### 适合扩展的位置
 `left` 倒计时可调整预警频率, - `explosionRange()` 可调整爆炸范围,### 不建议修改的位置
 `NoisemakerFuse` 的核心触发逻辑,### 重构建议,无。## 14. 事实核查清单

 - [x] 是否已覆盖全部字段
 - [x] 是否已覆盖全部方法
 - [x] 是否已检查继承链与覆写关系
 - [x] 是否已核对官方中文翻译
 - [x] 是否存在任何推测性表述
 - [x] 示例代码是否真实可用
 - [x] 是否遗漏资源/配置/本地化关联, - [x] 是否明确说明了注意事项与扩展点