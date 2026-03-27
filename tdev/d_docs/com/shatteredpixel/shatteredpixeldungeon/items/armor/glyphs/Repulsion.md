# Repulsion 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Repulsion.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 66行 |
| **所属模块** | core |
| **稀有度** | uncommon（稀有） |

## 2. 文件职责说明

### 核心职责
实现"反斥"刻印效果，当穿戴者受到近战攻击时有概率将攻击者击退。这是一种防御反击型的控制刻印。

### 系统定位
作为稀有级别的防御型刻印，通过击退攻击者来拉开距离，创造战术优势。适合需要保持距离的战斗风格。

### 不负责什么
- 不负责减少伤害
- 不负责击退效果的具体实现（由 WandOfBlastWave.throwChar() 负责）

## 3. 结构总览

### 主要成员概览
- **WHITE**：静态常量，白色发光效果
- **proc()**：核心方法，处理刻印触发逻辑
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 触发概率计算
- 击退轨迹计算
- 击退效果执行

### 生命周期/调用时机
在 Armor.proc() 方法中被调用，当护甲穿戴者受到近战攻击时触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法，需实现
- `glowing()`：抽象方法，需实现
- `procChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 实现击退效果的触发逻辑 |
| glowing() | 返回白色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Dungeon | 地牢实例 |
| WandOfBlastWave | 冲击波法杖（提供击退功能） |
| Ballistica | 弹道计算类 |
| ItemSprite.Glowing | 发光效果 |
| Random | 随机数生成 |

### 使用者
- `Armor`：通过 Armor.proc() 调用
- 随机生成系统：通过 Glyph.randomUncommon() 生成

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| WHITE | ItemSprite.Glowing | new ItemSprite.Glowing(0xFFFFFF) | 白色发光效果 |

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

**方法职责**：处理刻印触发效果，将近距离的攻击者击退。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，返回原始伤害值（此刻印不修改伤害）

**前置条件**：
- 护甲已装备且存在刻印
- 攻击者与防御者相邻（近战攻击）

**副作用**：
- 将攻击者击退
- 可能造成攻击者撞墙伤害

**核心实现逻辑**：
```java
@Override
public int proc( Armor armor, Char attacker, Char defender, int damage) {

    int level = Math.max( 0, armor.buffedLvl() );

    // lvl 0 - 20%
    // lvl 1 - 33%
    // lvl 2 - 43%
    float procChance = (level+1f)/(level+5f) * procChanceMultiplier(defender);
    if (Dungeon.level.adjacent(attacker.pos, defender.pos) && Random.Float() < procChance){

        float powerMulti = Math.max(1f, procChance);

        int oppositeHero = attacker.pos + (attacker.pos - defender.pos);
        Ballistica trajectory = new Ballistica(attacker.pos, oppositeHero, Ballistica.MAGIC_BOLT);
        WandOfBlastWave.throwChar(attacker,
                trajectory,
                Math.round(2 * powerMulti),
                true,
                true,
                this);
    }
    
    return damage;
}
```

**边界情况**：
- 只有近战攻击（相邻）才会触发
- 击退距离 = round(2 * powerMulti)
- 如果攻击者背后是墙壁，会造成撞击伤害

**触发概率计算**：
- 等级0：(0+1)/(0+5) = 20%
- 等级1：(1+1)/(1+5) ≈ 33%
- 等级2：(2+1)/(2+5) ≈ 43%

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，白色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return WHITE;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发
- `glowing()`：获取视觉效果

### 内部辅助方法
无。

### 扩展入口
可覆写 proc() 方法修改触发概率或击退距离。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约6.67%概率获得此刻印（uncommon 类别）
- 通过 Glyph.randomUncommon() 方法生成

### 调用者
- `Armor.proc()`：在战斗中调用刻印的 proc 方法

### 被调用者
- `WandOfBlastWave.throwChar()`：执行击退
- `Ballistica`：计算击退轨迹
- `Dungeon.level.adjacent()`：检查是否相邻

### 系统流程位置
```
近战攻击 → Armor.proc() → Repulsion.proc() → 相邻检查 + 概率判定
                                                    ↓
                                          计算击退轨迹 → 执行击退
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.repulsion.name | 反斥%s | 刻印名称 |
| items.armor.glyphs.repulsion.desc | 这个刻印会将敌人攻击的冲击力反弹回去，使攻击者飞至远处。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（白色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有反斥刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Repulsion.class, hero)) {
    // 护甲刻有反斥刻印，近战攻击者会被击退
}

// 直接创建反斥刻印并附着
armor.inscribe(new Repulsion());
```

### 击退方向计算
```java
// oppositeHero = attacker.pos + (attacker.pos - defender.pos)
// 即：攻击者位置 + (攻击者位置 - 防御者位置)
// 结果：攻击者背后的位置，用于计算击退轨迹
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，击退效果由 WandOfBlastWave.throwChar() 管理。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **仅近战有效**：只有相邻的攻击才会触发，远程攻击不会
2. **击退距离**：基础击退距离为 2 格，可随 powerMulti 增加
3. **撞击伤害**：被击退的角色撞墙会受到伤害
4. **this 参数**：throwChar 的最后一个参数是刻印本身，用于死亡判定

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改触发概率公式
- 修改击退距离
- 添加额外的击退效果

### 不建议修改的位置
- 相邻检查是必要的限制

### 重构建议
无，当前实现简洁清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 WHITE 常量
- [x] 是否已覆盖全部方法：已覆盖 proc() 和 glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"反斥"
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明