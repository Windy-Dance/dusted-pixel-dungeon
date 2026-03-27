# Potential 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Potential.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 63行 |
| **所属模块** | core |
| **稀有度** | common（普通） |

## 2. 文件职责说明

### 核心职责
实现"电势"刻印效果，当穿戴者受到攻击时有概率为装备的法杖充能。这是一种资源恢复型的辅助刻印。

### 系统定位
作为普通级别的辅助型刻印，通过受到攻击来为法杖充能，适合依赖法杖进行战斗的职业（如法师）。

### 不负责什么
- 不负责法杖充能的具体逻辑（由 Hero.belongings.charge() 负责）
- 不负责减少伤害

## 3. 结构总览

### 主要成员概览
- **WHITE**：静态常量，白色发光效果
- **proc()**：核心方法，处理刻印触发逻辑
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 触发概率计算
- 法杖充能
- 粒子特效播放

### 生命周期/调用时机
在 Armor.proc() 方法中被调用，当护甲穿戴者受到攻击时触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法，需实现
- `glowing()`：抽象方法，需实现
- `procChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 实现法杖充能的触发逻辑 |
| glowing() | 返回白色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Hero | 英雄类 |
| EnergyParticle | 能量粒子效果 |
| ItemSprite.Glowing | 发光效果 |
| Random | 随机数生成 |

### 使用者
- `Armor`：通过 Armor.proc() 调用
- 随机生成系统：通过 Glyph.randomCommon() 生成

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| WHITE | ItemSprite.Glowing | new ItemSprite.Glowing(0xFFFFFF, 0.6f) | 白色发光效果，第二个参数 0.6f 为透明度 |

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

**方法职责**：处理刻印触发效果，为英雄的法杖充能。

**参数**：
- `armor` (Armor)：触发刻印的护甲实例
- `attacker` (Char)：攻击者
- `defender` (Char)：防御者（护甲穿戴者）
- `damage` (int)：原始伤害值

**返回值**：int，返回原始伤害值（此刻印不修改伤害）

**前置条件**：护甲已装备且存在刻印，防御者必须是 Hero 类型。

**副作用**：
- 为英雄的法杖充能
- 播放能量粒子效果

**核心实现逻辑**：
```java
@Override
public int proc( Armor armor, Char attacker, Char defender, int damage) {

    int level = Math.max( 0, armor.buffedLvl() );
    
    // lvl 0 - 16.7%
    // lvl 1 - 28.6%
    // lvl 2 - 37.5%
    float procChance = (level+1f)/(level+6f) * procChanceMultiplier(defender);
    if (Random.Float() < procChance && defender instanceof Hero) {

        float powerMulti = Math.max(1f, procChance);

        int wands = ((Hero) defender).belongings.charge( powerMulti );
        if (wands > 0) {
            defender.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
        }
    }
    
    return damage;
}
```

**边界情况**：
- 只有 Hero 类型的防御者才能触发效果
- 只有当有法杖被充能时才播放粒子效果
- 触发概率随等级提升而增加

**触发概率计算**：
- 等级0：(0+1)/(0+6) ≈ 16.7%
- 等级1：(1+1)/(1+6) ≈ 28.6%
- 等级2：(2+1)/(2+6) = 37.5%

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，白色发光效果对象（透明度 0.6）

**核心实现逻辑**：
```java
@Override
public Glowing glowing() {
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
可覆写 proc() 方法修改触发概率或充能量。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约12.5%概率获得此刻印（common 类别）
- 通过 Glyph.randomCommon() 方法生成

### 调用者
- `Armor.proc()`：在战斗中调用刻印的 proc 方法

### 被调用者
- `Hero.belongings.charge()`：法杖充能方法
- `EnergyParticle`：能量粒子效果

### 系统流程位置
```
战斗攻击 → Armor.proc() → Potential.proc() → 概率判定 → 法杖充能
                                                   ↓
                                           播放能量粒子效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.potential.name | 电势%s | 刻印名称 |
| items.armor.glyphs.potential.desc | 这个刻印在被击中时会积蓄能量，在生效时为使用者的法杖充能。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（白色，透明度 0.6）
- 粒子效果：EnergyParticle（能量粒子）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有电势刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Potential.class, hero)) {
    // 护甲刻有电势刻印，受击时会为法杖充能
}

// 直接创建电势刻印并附着
armor.inscribe(new Potential());
```

### 充能量计算
```java
// powerMulti = max(1, procChance)
// 充能量由 Hero.belongings.charge(powerMulti) 决定
// 具体充能逻辑在 Hero.Belongings 类中实现
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，充能效果依赖于 Hero.belongings.charge() 方法。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **仅对 Hero 有效**：只有 Hero 类型的防御者才能触发效果
2. **需要装备法杖**：如果没有装备法杖，charge() 返回 0，不会播放粒子效果
3. **透明发光效果**：发光效果的透明度为 0.6，比其他刻印更淡

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改触发概率公式
- 修改充能量计算

### 不建议修改的位置
- 类型检查（defender instanceof Hero）是必要的限制

### 重构建议
无，当前实现简洁清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 WHITE 常量
- [x] 是否已覆盖全部方法：已覆盖 proc() 和 glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"电势"
- [x] 是否存在任何推测性表述：无，全部基于源码
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明