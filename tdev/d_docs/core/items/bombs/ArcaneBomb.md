# ArcaneBomb 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/ArcaneBomb.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| 类类型 | public class |
| 继承关系 | extends Bomb |
| 代码行数 | 148行 |

## 2. 类职责说明
奥术炸弹是一种高级特殊炸弹，爆炸不会破坏地形但会无视护甲造成伤害。爆炸范围为2格，炸弹点燃后会显示Goo粒子效果，表示即将爆炸。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Bomb {
        <<abstract>>
        +int explosionRange()
        +boolean explodesDestructively()
        +void explode(int)
        +Fuse createFuse()
    }
    
    class ArcaneBomb {
        +int explosionRange()
        +boolean explodesDestructively()
        +void explode(int)
        +Fuse createFuse()
        +int value()
    }
    
    class ArcaneBombFuse {
        -ArrayList~Emitter~ gooWarnEmitters
        +Fuse ignite(Bomb)
        +void snuff()
    }
    
    class Fuse {
        <<abstract>>
    }
    
    Bomb <|-- ArcaneBomb
    Fuse <|-- ArcaneBombFuse
    ArcaneBomb +-- ArcaneBombFuse
    
    note for ArcaneBomb "爆炸范围: 2格\n不破坏地形\n无视护甲"
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（ARCANE_BOMB） |

## 7. 方法详解

### explodesDestructively()
**签名**: `boolean explodesDestructively()`
**功能**: 爆炸是否具有破坏性
**参数**: 无
**返回值**: boolean - false（不具有破坏性）
**实现逻辑**:
- 返回false（第54行）

### explosionRange()
**签名**: `int explosionRange()`
**功能**: 获取爆炸范围
**参数**: 无
**返回值**: int - 2格
**实现逻辑**:
- 返回2（第59行）

### createFuse()
**签名**: `Fuse createFuse()`
**功能**: 创建奥术炸弹专用的引信
**参数**: 无
**返回值**: Fuse - ArcaneBombFuse实例
**实现逻辑**:
- 返回新的ArcaneBombFuse实例（第64行）

### explode(int cell)
**签名**: `void explode(int cell)`
**功能**: 在指定位置爆炸并无视护甲造成伤害
**参数**:
- cell: int - 爆炸位置
**返回值**: void
**实现逻辑**:
1. 调用父类explode方法（第69行）
2. 收集受影响的角色（第71-82行）
3. 对每个受影响的角色（第84-92行）：
   - 造成基础伤害，无视护甲（第86-87行）
   - 如果击杀英雄，记录死亡原因

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（50 * 数量）

## 内部类 ArcaneBombFuse

继承自Fuse，表示奥术炸弹的特殊引信，带有Goo粒子效果。

### ignite(Bomb bomb)
**功能**: 点燃炸弹并添加粒子效果
**参数**:
- bomb: Bomb - 要点燃的炸弹
**返回值**: Fuse - 引信实例
**实现逻辑**:
1. 调用父类ignite方法（第109行）
2. 添加延迟Actor来设置粒子效果（第110-135行）

### snuff()
**功能**: 熄灭引信并清除粒子效果
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 调用父类snuff方法（第142行）
2. 停止所有粒子发射器（第143-145行）

## 奥术炸弹效果

| 特性 | 效果 |
|------|------|
| 破坏地形 | 否 |
| 无视护甲 | 是 |
| 爆炸范围 | 2格半径 |
| 粒子效果 | Goo粒子 |

## 11. 使用示例
```java
// 创建奥术炸弹
ArcaneBomb arcaneBomb = new ArcaneBomb();

// 点燃并投掷
arcaneBomb.execute(hero, Bomb.AC_LIGHTTHROW);
// 2回合后爆炸
// 爆炸范围2格
// 不破坏地形，无视护甲

// 合成配方
// 炸弹 + Goo粘液 = 奥术炸弹
// 成本: 6点炼金能量
```

## 注意事项
1. 不会破坏地形
2. 伤害无视护甲
3. 点燃后显示Goo粒子效果
4. 合成成本最高（6点能量）
5. 需要Goo粘液作为原料

## 最佳实践
1. 对付高护甲敌人
2. 保护地形不被破坏
3. 在需要精确伤害时使用
4. 配合其他控制效果
5. 合成成本高，谨慎使用