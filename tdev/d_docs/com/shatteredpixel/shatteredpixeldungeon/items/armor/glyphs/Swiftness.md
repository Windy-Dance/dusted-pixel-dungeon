# Swiftness 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Swiftness.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs |
| **文件类型** | class |
| **继承关系** | extends Armor.Glyph |
| **代码行数** | 73行 |
| **所属模块** | core |
| **稀有度** | common（普通） |

## 2. 文件职责说明

### 核心职责
实现"迅捷"刻印效果，当穿戴者周围没有敌人时提高移动速度。这是一种条件触发型的机动增强刻印。

### 系统定位
作为普通级别的机动型刻印，通过检测周围是否有敌人来提供移动速度加成，适合探索和快速穿越地牢。

### 不负责什么
- 不负责敌人检测的具体逻辑（由 Actor.chars() 和 PathFinder 负责）
- 不负责速度计算的具体逻辑（由 Char.speed() 负责）

## 3. 结构总览

### 主要成员概览
- **YELLOW**：静态常量，黄色发光效果
- **proc()**：核心方法（空实现，效果在别处触发）
- **speedBoost()**：静态方法，计算速度加成
- **glowing()**：返回视觉效果

### 主要逻辑块概览
- 敌人距离检测
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
| glowing() | 返回黄色发光效果 |

### 实现的接口契约
继承自 Armor.Glyph 的抽象接口。

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| Armor | 护甲实例 |
| Char | 角色基类 |
| Dungeon | 地牢实例 |
| Actor | 角色管理器 |
| PathFinder | 路径查找工具 |
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
| YELLOW | ItemSprite.Glowing | new ItemSprite.Glowing(0xFFFF00) | 黄色发光效果，象征速度 |

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

**方法职责**：空实现。迅捷刻印的效果不在 proc() 中触发，而是在 Char.speed() 方法中通过调用 speedBoost() 实现速度加成。

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

**方法职责**：计算迅捷刻印提供的速度加成。只有当角色周围 3 格范围内没有敌人时才生效。

**参数**：
- `owner` (Char)：护甲穿戴者
- `level` (int)：护甲等级

**返回值**：float，速度乘数（1表示无加成）

**前置条件**：护甲已装备且有迅捷刻印。

**副作用**：如果触发速度加成且有精灵，会播放黄色光粒子效果。

**核心实现逻辑**：
```java
public static float speedBoost( Char owner, int level ){
    if (level == -1){
        return 1;
    }

    boolean enemyNear = false;
    // 敌人在3格可通行路径内算作"附近"
    PathFinder.buildDistanceMap(owner.pos, Dungeon.level.passable, 3);
    for (Char ch : Actor.chars()){
        if (ch.alignment == Char.Alignment.ENEMY && PathFinder.distance[ch.pos] != Integer.MAX_VALUE){
            enemyNear = true;
        }
    }
    if (enemyNear){
        return 1;
    } else {
        if (owner.sprite != null){
            int particles = 1 + (int)Random.Float(1+level/5f);
            owner.sprite.emitter().startDelayed(Speck.factory(Speck.YELLOW_LIGHT), 0.02f, particles, 0.05f);
        }
        return (1.2f + 0.04f * level) * genericProcChanceMultiplier(owner);
    }
}
```

**边界情况**：
- 当 level == -1 时返回 1（无加成）
- 当周围有敌人时返回 1（无加成）
- 敌人检测基于 3 格可通行路径，而非直线距离

**速度加成计算**：
- 等级0：1.2 倍速度
- 等级5：1.4 倍速度
- 等级10：1.6 倍速度

---

### glowing()

**可见性**：public

**是否覆写**：是，覆写自 Armor.Glyph

**方法职责**：返回刻印的视觉发光效果。

**参数**：无

**返回值**：ItemSprite.Glowing，黄色发光效果对象

**核心实现逻辑**：
```java
@Override
public ItemSprite.Glowing glowing() {
    return YELLOW;
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
可覆写 speedBoost() 方法修改速度加成公式或敌人检测范围。

## 9. 运行机制与调用链

### 创建时机
- 护甲随机生成时，有约12.5%概率获得此刻印（common 类别）
- 通过 Glyph.randomCommon() 方法生成

### 调用者
- `Char.speed()`：计算移动速度时调用 speedBoost() 方法
- `Armor`：管理刻印附着

### 被调用者
- `PathFinder.buildDistanceMap()`：构建距离图
- `Actor.chars()`：获取所有角色
- `Speck`：粒子视觉效果

### 系统流程位置
```
角色移动 → Char.speed() → 检查迅捷刻印 → Swiftness.speedBoost()
                                          ↓
                                    构建距离图
                                          ↓
                                    检查敌人是否在3格内
                                          ↓
                                   无敌人：返回速度乘数 + 粒子
                                   有敌人：返回 1
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.armor.glyphs.swiftness.name | 迅捷%s | 刻印名称 |
| items.armor.glyphs.swiftness.desc | 这个刻印会在近范围内没有敌人时提高使用者的移动速度。 | 刻印描述 |

### 依赖的资源
视觉效果：
- 发光效果：ItemSprite.Glowing（黄色）
- 粒子效果：Speck.YELLOW_LIGHT（黄色光粒子）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法
```java
// 检查护甲是否有迅捷刻印
Armor armor = hero.belongings.armor();
if (armor != null && armor.hasGlyph(Swiftness.class, hero)) {
    // 护甲刻有迅捷刻印，周围无敌人时移动更快
}

// 计算速度加成
int level = armor.buffedLvl();
float speedMulti = Swiftness.speedBoost(hero, level);
// 如果周围无敌人，speedMulti > 1；否则为 1
```

### 敌人检测范围说明
```java
// 使用 PathFinder.buildDistanceMap 构建距离图
// 范围为 3 格"可通行"路径
// 注意：陷阱、深渊等不可通行地格会阻断检测
// 即：敌人可能在直线距离 2 格内，但如果中间有深渊则不被检测到
```

## 12. 开发注意事项

### 状态依赖
刻印本身无状态，速度加成依赖于敌人检测结果。

### 生命周期耦合
刻印的生命周期与护甲绑定。

### 常见陷阱
1. **proc() 不生效**：迅捷刻印的 proc() 是空实现
2. **敌人检测范围**：基于可通行路径而非直线距离
3. **性能考虑**：每次调用 speedBoost() 都会构建距离图，可能影响性能
4. **等级 -1**：当护甲无效时，返回速度乘数 1

## 13. 修改建议与扩展点

### 适合扩展的位置
- 修改 speedBoost() 中的速度加成公式
- 修改敌人检测范围（当前为 3 格）

### 不建议修改的位置
- proc() 方法的空实现是刻意的

### 重构建议
可考虑缓存敌人检测结果，避免频繁构建距离图。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：已覆盖 YELLOW 常量
- [x] 是否已覆盖全部方法：已覆盖 proc()、speedBoost()、glowing()
- [x] 是否已检查继承链与覆写关系：已说明继承 Armor.Glyph
- [x] 是否已核对官方中文翻译：已使用 items_zh.properties 中的"迅捷"
- [x] 是否存在任何推测性表述：Char.speed() 调用方式基于源码注释推测
- [x] 示例代码是否真实可用：示例代码基于实际 API
- [x] 是否遗漏资源/配置/本地化关联：已列出相关消息键
- [x] 是否明确说明了注意事项与扩展点：已详细说明