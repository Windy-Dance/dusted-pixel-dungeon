# Camouflage 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Camouflage.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 57行 |
| **所属模块** | core |
| **稀有度** | uncommon（稀有） |

## 2. 文件职责说明

### 核心职责
实现"迷彩"刻印效果，当穿戴者踩踏高草时会获得短暂的隐形效果。这是一种环境交互型的防御刻印。

### 系统定位
作为稀有级别的防御型刻印，通过与地形（高草）的交互提供隐身能力，适合需要规避战斗或进行偷袭的场景。

### 不负责什么
- 不负责隐形状态的具体效果实现（由 Invisibility Buff 负责）
- 不负责高草的生成和管理

## 3. 结构总览

### 主要成员概览
- **GREEN**：静态常量，绿色发光效果
- **proc()**：核心方法（空实现，效果在别处触发）
- **activate()**：静态方法，激活隐形效果
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 隐形效果激活
- 音效播放

### 生命周期/调用时机
刻印在护甲生成时创建，效果在踩踏高草时通过 HighGrass.trample() 触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法
- `glowing()`：抽象方法
- `genericProcChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 空实现，效果在 HighGrass.trample() 中触发 |
| glowing() | 返回绿色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Buff | 状态效果基类 |
| Invisibility | 隐形状态效果 |
| Dungeon | 地牢实例 |
| Assets.Sounds | 音效资源 |
| Sample | 音效播放器 |
| ItemSprite.Glowing | 发光效果 |

### 使用者
- `HighGrass.trample()`：踩踏高草时调用 activate() 方法
- `Armor`：管理刻印附着

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| GREEN | ItemSprite.Glowing | new ItemSprite.Glowing(0x448822) | 绿色发光效果，象征植物/伪装 |

### 实例字段
无实例字段。

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无显式构造器定义。

### 初始化块
无初始化块。

### 初始化注意事项
刻印实例通过 Reflection.newInstance() 动态创建。

## 7. 方法详解

### proc()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：空实现。迷彩刻印的效果不在 proc() 中触发，而是在踩踏高草时通过 HighGrass.trample() 调用 activate() 方法实现。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者
- `damage` (int)：原始伤害值

**返回值**：int，返回原始伤害值

**核心实现逻辑**：
```java
@Override
public int proc(Armor armor, Char attacker, Char defender, int damage) {
    //no proc effect, triggers in HighGrass.trample
    return damage;
}
```

**边界情况**：此刻印不通过 proc() 方法生效。

---

### activate()

**可见性**：public static

**是否覆写**：否，静态方法

**方法职责**：激活迷彩刻印的隐形效果。当角色踩踏高草时调用此方法，使角色获得隐形状态。

**参数**：
- `ch` (Char)：获得隐形效果的角色
- `level` (int)：护甲等级

**返回值**：void

**前置条件**：角色正在踩踏高草，且护甲有迷彩刻印。

**副作用**：
- 对角色施加 Invisibility 状态
- 如果角色在英雄视野内，播放"融合"音效

**核心实现逻辑**：
```java
public static void activate(Char ch, int level){
    if (level == -1) return;
    Buff.prolong(ch, Invisibility.class, Math.round((3 + level/2f)* genericProcChanceMultiplier(ch)));
    if ( Dungeon.level.heroFOV[ch.pos] ) {
        Sample.INSTANCE.play( Assets.Sounds.MELD );
    }
}
```

**边界情况**：
- 当 level == -1 时直接返回，不施加隐形（表示无效刻印）
- 隐形持续时间 = 3 + level/2（向上取整），再乘以概率乘数

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，绿色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return GREEN;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发（空实现）
- `glowing()`：获取视觉效果
- `activate(Char, int)`：静态方法，激活隐形效果

### 内部辅助方法
无。

### 扩展入口
可覆写 activate() 或创建子类扩展隐形逻辑。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约6.67%概率获得此刻印（uncommon 类别）
- 通过 Glyph.randomUncommon() 方法生成

### 调用者
- `HighGrass.trample()`：踩踏高草时调用 activate() 方法
- `Armor`：管理刻印附着

### 被调用者
- `Invisibility`：隐形状态效果
- `Buff.prolong()`：状态施加方法
- `Sample.INSTANCE.play()`：音效播放

### 系统流程位置
```
角色移动到高草地格 → HighGrass.trample() → Camouflage.activate() → 施加隐形状态
                                                                      ↓
                                                              播放融合音效
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.camouflage.name | 迷彩%s | 刻印名称 |
| items.armor.glyphs.camouflage.desc | 这个刻印能让使用者与高草融为一体，得到短暂的隐形效果。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（绿色）

音效：
- Assets.Sounds.MELD：融合音效

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有迷彩刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Camouflage.class, hero)) {
    // 护甲刻有迷彩刻印，踩踏高草时会隐形
}

// 手动激活迷彩效果（通常由 HighGrass.trample() 调用）
int level = armor.buffedLvl();
Camouflage.activate(hero, level);
```

### 隐形持续时间计算
```java
// 等级0时：round(3 + 0/2) = 3 回合
// 等级1时：round(3 + 0.5) = 3 回合
// 等级2时：round(3 + 1) = 4 回合
// 等级5时：round(3 + 2.5) = 6 回合
// 实际持续时间还需乘以 genericProcChanceMultiplier(ch)
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，隐形状态由 Invisibility Buff 管理。

### 生命周期耦合
刻印的生命周期与护甲绑定。激活效果的生命周期由 Buff 管理。

### 常见陷阱
1. **proc() 不生效**：迷彩刻印的 proc() 是空实现
2. **触发条件**：必须踩踏高草才会触发，不是受伤时触发
3. **等级 -1**：当护甲无效时，activate() 直接返回不施加效果
4. **音效位置**：音效只在角色在英雄视野内时播放

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 activate() 中的隐形持续时间公式
- 添加额外的触发条件

### 不建议修改的位置
- proc() 方法的空实现是刻意的

### 重构建议
可考虑将隐形持续时间计算提取为单独的方法，便于测试和修改。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 GREEN 常量
- [x] 是否已覆盖全部方法：已覆盖 proc()、activate()、glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"迷彩"
- [x] 是否存在任何推测性表述：HighGrass.trample() 调用方式基于源码注释
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键和音效
- [x] 是否明确说明了注意事项与扩展点：已详细说明