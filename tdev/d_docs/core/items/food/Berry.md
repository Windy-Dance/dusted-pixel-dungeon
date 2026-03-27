# Berry 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/food/Berry.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.food |
| 类类型 | public class |
| 继承关系 | extends Food |
| 代码行数 | 72行 |

## 2. 类职责说明
浆果是一种小型食物，能量值较低（约100点），但进食时间很短（1回合，有天赋则0回合）。食用两个浆果后会掉落一个随机种子，鼓励玩家在需要种子时食用。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Food {
        <<abstract>>
        +float energy
        +void satisfy(Hero)
        +float eatingTime()
    }
    
    class Berry {
        +void satisfy(Hero)
        +float eatingTime()
        +int value()
    }
    
    class SeedCounter {
        +count(): int
    }
    
    Food <|-- Berry
    CounterBuff <|-- SeedCounter
    Berry +-- SeedCounter
    
    note for Berry "能量值: HUNGRY/3 (约100点)\n进食时间: 1回合 (有天赋则0回合)\n吃2个掉落随机种子"
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（BERRY） |
| energy | float | - | 能量值（HUNGRY/3，约100点） |
| bones | boolean | - | 是否可出现在遗骨中（false） |

## 7. 方法详解

### eatingTime()
**签名**: `float eatingTime()`
**功能**: 获取进食时间
**参数**: 无
**返回值**: float - 进食时间
**实现逻辑**:
1. 如果有进食相关天赋，返回0（第50-51行）
2. 否则返回1回合（第53行）

### satisfy(Hero hero)
**签名**: `void satisfy(Hero hero)`
**功能**: 满足饥饿需求并追踪食用次数
**参数**:
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 调用父类satisfy方法（第58行）
2. 增加食用计数（第59行）
3. 如果计数>=2，掉落随机种子并重置计数（第60-63行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（5 * 数量）

## 内部类 SeedCounter

继承自CounterBuff，用于追踪浆果食用次数。

- revivePersists: true - 存档后保持计数

## 11. 使用示例
```java
// 创建浆果
Berry berry = new Berry();

// 食用浆果
berry.execute(hero, Food.AC_EAT);
// 恢复饥饿值（约100点）
// 进食时间短（1回合）

// 吃两个浆果
berry.execute(hero, Food.AC_EAT); // 第一个，计数=1
berry.execute(hero, Food.AC_EAT); // 第二个，计数=2
// 掉落一个随机种子到脚下
// 计数重置为0

// 配合天赋瞬间进食
if (hero.hasTalent(Talent.IRON_STOMACH)) {
    // 进食时间=0回合
}
```

## 注意事项
1. 能量值较低，约为普通口粮的一半
2. 进食时间很短，适合紧急使用
3. 吃两个掉落随机种子
4. 不会出现在遗骨中
5. 价值较低（5金币）

## 最佳实践
1. 需要种子时连续吃两个浆果
2. 在战斗中快速补充饥饿
3. 配合天赋实现瞬间进食
4. 不适合作为主要食物来源
5. 紧急情况下比大份食物更灵活