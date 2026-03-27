# HolyBomb 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/HolyBomb.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| 类类型 | public class |
| 继承关系 | extends Bomb |
| 代码行数 | 91行 |

## 2. 类职责说明
圣水炸弹是一种特殊炸弹，对亡灵和恶魔类敌人造成额外伤害。爆炸范围为2格，会产生圣光效果，对不死和恶魔属性的敌人造成额外50%伤害。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Bomb {
        <<abstract>>
        +int explosionRange()
        +void explode(int)
    }
    
    class HolyBomb {
        +int explosionRange()
        +void explode(int)
        +int value()
    }
    
    class HolyDamage {
        +伤害来源标记
    }
    
    Bomb <|-- HolyBomb
    HolyBomb +-- HolyDamage
    
    note for HolyBomb "爆炸范围: 2格\n对亡灵/恶魔+50%伤害\n产生圣光效果"
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（HOLY_BOMB） |

## 7. 方法详解

### explosionRange()
**签名**: `int explosionRange()`
**功能**: 获取爆炸范围
**参数**: 无
**返回值**: int - 2格
**实现逻辑**:
- 返回2（第47行）

### explode(int cell)
**签名**: `void explode(int cell)`
**功能**: 在指定位置爆炸并对亡灵/恶魔造成额外伤害
**参数**:
- cell: int - 爆炸位置
**返回值**: void
**实现逻辑**:
1. 调用父类explode方法（第52行）
2. 如果在视野内，显示圣光效果（第54-56行）
3. 收集受影响的角色（第58-69行）
4. 对每个亡灵或恶魔属性的角色（第71-78行）：
   - 显示暗影粒子效果（第73行）
   - 造成额外50%伤害（第76-77行）
5. 播放阅读音效（第81行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（50 * 数量）

## 圣水炸弹效果

| 目标类型 | 额外伤害 |
|---------|---------|
| 亡灵敌人 | +50% |
| 恶魔敌人 | +50% |
| 普通敌人 | 无 |
| 爆炸范围 | 2格半径 |

## 11. 使用示例
```java
// 创建圣水炸弹
HolyBomb holyBomb = new HolyBomb();

// 点燃并投掷
holyBomb.execute(hero, Bomb.AC_LIGHTTHROW);
// 2回合后爆炸
// 爆炸范围2格
// 对亡灵/恶魔造成额外伤害

// 合成配方
// 炸弹 + 解咒卷轴 = 圣水炸弹
// 成本: 3点炼金能量
```

## 注意事项
1. 爆炸范围比普通炸弹大（2格 vs 1格）
2. 只对亡灵和恶魔有额外效果
3. 额外伤害为基础伤害的50%
4. 有特殊的圣光视觉效果
5. 合成成本较高（3点能量）

## 最佳实践
1. 对付亡灵和恶魔敌人
2. 在亡灵多的区域使用
3. 配合其他炸弹使用
4. 在Boss战中对抗亡灵Boss
5. 注意合成成本较高