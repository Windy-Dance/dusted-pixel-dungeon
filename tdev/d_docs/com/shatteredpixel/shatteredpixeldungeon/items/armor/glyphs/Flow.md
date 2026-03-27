# Flow 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Flow.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 58行 |
| **所属模块** | core |
| **稀有度** | uncommon（稀有） |

## 2. 文件职责说明

### 核心职责
实现"涌流"刻印效果，当穿戴者在水中移动时大幅提升移动速度。这是一种环境交互型的移动增强刻印。

### 系统定位
作为稀有级别的机动型刻印，通过与水域地形的交互提供移动速度加成，适合需要快速穿越水域的场景。

### 不负责什么
- 不负责水地形的生成和管理
- 不负责速度计算的具体逻辑（由 Char.speed() 负责）

## 3. 结构总览

### 主要成员概览
- **BLUE**：静态常量，蓝色发光效果
- **proc()**：核心方法（空实现，效果在别处触发）
- **speedBoost()**：静态方法，计算速度加成
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 速度加成计算
- 粒子特效播放

### 生命周期/调用时机
刻印在护甲生成时创建，效果在 Char.speed() 方法中通过检查刻印触发。

## 4. 继承与协作关系

### 父类提供的能力
- `proc(Armor, Char, Char, int)`：抽象方法
- `glowing()`：抽象方法
- `genericProcChanceMultiplier(Char)`：触发概率乘数计算

### 覆写的方法
| 方法 | 说明 |
|------|------|
| proc() | 空实现，效果在 Char.speed() 中触发 |
| glowing() | 返回蓝色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Dungeon | 地牢实例 |
| Speck | 粒子效果 |
| ItemSprite.Glowing | 发光效果 |
| Random | 随机数生成 |

### 使用者
- `Char.speed()`：计算移动速度时调用 speedBoost() 方法
- `Armor`：管理刻印附着

## 5. 字段/常量详解

### 静态常量

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| BLUE | ItemSprite.Glowing | new ItemSprite.Glowing(0x0000FF) | 蓝色发光效果，象征水流 |

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

**方法职责**：空实现。涌流刻印的效果不在 proc() 中触发，而是在 Char.speed() 方法中通过调用 speedBoost() 实现速度加成。

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
    //no proc effect, triggers in Char.speed()
    return damage;
}
```

**边界情况**：此刻印不通过 proc() 方法生效。

---

### speedBoost()

**可见性**：public static

**是否覆写**：否，静态方法

**方法职责**：计算涌流刻印提供的速度加成。只有当角色站在水中时才生效。

**参数**：
- `owner` (Char)：护甲穿戴者
- `level` (int)：护甲等级

**返回值**：float，速度乘数（1表示无加成）

**前置条件**：护甲已装备且有涌流刻印。

**副作用**：如果角色站在水中且有精灵，会播放蓝色光粒子效果。

**核心实现逻辑**：
```java
public static float speedBoost( Char owner, int level ){
    if (level == -1 || !Dungeon.level.water[owner.pos]){
        return 1;
    } else {
        if (owner.sprite != null){
            int particles = 2 + (int) Random.Float(1+level/2f);
            owner.sprite.emitter().startDelayed(Speck.factory(Speck.BLUE_LIGHT), 0.02f, particles, 0.05f);
        }
        return (2f + 0.5f*level) * genericProcChanceMultiplier(owner);
    }
}
```

**边界情况**：
- 当 level == -1 时返回 1（无加成）
- 当角色不在水中时返回 1（无加成）
- 速度乘数随等级线性增长

**速度加成计算**：
- 等级0：2.0 倍速度
- 等级1：2.5 倍速度
- 等级5：4.5 倍速度

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，蓝色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return BLUE;
}
```

## 8. 对外暴露能力

### 显式 API
- `proc(Armor, Char, Char, int)`：刻印效果触发（空实现）
- `glowing()`：获取视觉效果
- `speedBoost(Char, int)`：静态方法，计算速度加成

### 内部辅助方法
无。

### 扩展入口
可覆写 speedBoost() 方法修改速度加成公式。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约6.67%概率获得此刻印（uncommon 类别）
- 通过 Glyph.randomUncommon() 方法生成

### 调用者
- `Char.speed()`：计算移动速度时调用 speedBoost() 方法
- `Armor`：管理刻印附着

### 被调用者
- `Dungeon.level.water[]`：检查当前位置是否为水域
- `Speck`：粒子视觉效果

### 系统流程位置
```
角色移动 → Char.speed() → 检查涌流刻印 → Flow.speedBoost()
                                          ↓
                                    检查是否在水中
                                          ↓
                                   是：返回速度乘数 + 播放粒子
                                   否：返回 1
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.flow.name | 涌流%s | 刻印名称 |
| items.armor.glyphs.flow.desc | 这个刻印能操控使用者周身的水流，让使用者在水中移动时速度大大加快。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（蓝色）
- 粒子效果：Speck.BLUE_LIGHT（蓝色光粒子）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有涌流刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Flow.class, hero)) {
    // 护甲刻有涌流刻印，在水中移动更快
}

// 计算速度加成
int level = armor.buffedLvl();
float speedMulti = Flow.speedBoost(hero, level);
// 如果在水中，speedMulti > 1；否则为 1
```

### 与移动系统集成（推测）
```java
// 在 Char.speed() 中的典型调用模式
public float speed() {
    float speed = baseSpeed;
    Armor armor = belongings.armor();
    if (armor != null && armor.hasGlyph(Flow.class, this)) {
        speed *= Flow.speedBoost(this, armor.buffedLvl());
    }
    return speed;
}
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，速度加成依赖于角色当前位置的地形类型。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **proc() 不生效**：涌流刻印的 proc() 是空实现
2. **水域检查**：必须站在水中才有效果，浅水或其他液体可能不生效
3. **粒子效果延迟**：粒子效果使用 startDelayed() 方法，有轻微延迟
4. **等级 -1**：当护甲无效时，返回速度乘数 1

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 speedBoost() 中的速度加成公式
- 添加额外的触发条件（如：在特定地形上）

### 不建议修改的位置
- proc() 方法的空实现是刻意的

### 重构建议
无，当前实现简洁清晰。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 BLUE 常量
- [x] 是否已覆盖全部方法：已覆盖 proc()、speedBoost()、glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"涌流"
- [x] 是否存在任何推测性表述：Char.speed() 调用方式基于源码注释推测
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明