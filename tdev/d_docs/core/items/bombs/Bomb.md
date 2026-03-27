# Bomb 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/bombs/Bomb.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| 类类型 | public class |
| 继承关系 | extends Item |
| 代码行数 | 449行 |

## 2. 类职责说明
炸弹是一种投掷型武器，可以点燃后投掷或直接投掷。点燃的炸弹会在2回合后爆炸，造成范围伤害并破坏地形。炸弹可以通过与各种物品组合来创建特殊类型的炸弹。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        <<abstract>>
        +boolean stackable
        +String defaultAction
        +boolean usesTargeting
    }
    
    class Bomb {
        +Fuse fuse
        +String AC_LIGHTTHROW
        +boolean isSimilar(Item)
        +boolean explodesDestructively()
        +int explosionRange()
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +void explode(int)
        +Item random()
        +ItemSprite.Glowing glowing()
        +int value()
        +String desc()
        #Fuse createFuse()
        #void onThrow(int)
    }
    
    class Fuse {
        +Bomb bomb
        +Fuse ignite(Bomb)
        +boolean act()
        +boolean freeze()
        +void snuff()
        #void trigger(Heap)
    }
    
    class DoubleBomb {
        +boolean doPickUp(Hero, int)
    }
    
    class ConjuredBomb {
        +魔法召唤的炸弹
    }
    
    class EnhanceBomb {
        +LinkedHashMap validIngredients
        +HashMap bombCosts
        +boolean testIngredients(ArrayList)
        +int cost(ArrayList)
        +Item brew(ArrayList)
    }
    
    Item <|-- Bomb
    Actor <|-- Fuse
    Bomb <|-- DoubleBomb
    Bomb <|-- ConjuredBomb
    Recipe <|-- EnhanceBomb
    Bomb +-- Fuse
    Bomb +-- DoubleBomb
    Bomb +-- ConjuredBomb
    Bomb +-- EnhanceBomb
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_LIGHTTHROW | String | "LIGHTTHROW" | 点燃投掷动作标识 |
| FUSE | String | "fuse" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（BOMB） |
| defaultAction | String | - | 默认动作（AC_LIGHTTHROW） |
| usesTargeting | boolean | - | 是否使用目标选择（true） |
| stackable | boolean | - | 是否可堆叠（true） |
| fuse | Fuse | public | 炸弹的引信 |
| lightingFuse | boolean | private static | 是否正在点燃引信 |

## 7. 方法详解

### isSimilar(Item item)
**签名**: `boolean isSimilar(Item item)`
**功能**: 检查物品是否相似（用于堆叠）
**参数**:
- item: Item - 要比较的物品
**返回值**: boolean - 是否相似
**实现逻辑**:
- 调用父类方法并检查引信状态（第86行）

### explodesDestructively()
**签名**: `boolean explodesDestructively()`
**功能**: 爆炸是否具有破坏性
**参数**: 无
**返回值**: boolean - true（默认具有破坏性）
**实现逻辑**:
- 返回true（第90行）

### explosionRange()
**签名**: `int explosionRange()`
**功能**: 获取爆炸范围
**参数**: 无
**返回值**: int - 爆炸范围（默认为1）
**实现逻辑**:
- 返回1（第94行）

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第99行）
2. 添加点燃投掷动作（第100行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 如果动作是AC_LIGHTTHROW，设置lightingFuse标志（第107-111行）
2. 调用父类execute方法（第113行）

### onThrow(int cell)
**签名**: `void onThrow(int cell)`
**功能**: 投掷时的处理
**参数**:
- cell: int - 目标单元格
**返回值**: void
**实现逻辑**:
1. 如果不是坑且正在点燃，添加延迟引信（第122-123行）
2. 调用父类onThrow方法（第125行）

### explode(int cell)
**签名**: `void explode(int cell)`
**功能**: 在指定位置爆炸
**参数**:
- cell: int - 爆炸位置
**返回值**: void
**实现逻辑**:
1. 移除引信（第140-143行）
2. 播放爆炸音效（第145行）
3. 如果具有破坏性（第147-215行）：
   - 计算受影响的单元格和角色
   - 播放爆炸粒子效果
   - 破坏可燃地形
   - 触发其他炸弹
   - 对角色造成伤害

### random()
**签名**: `Item random()`
**功能**: 生成随机数量的炸弹
**参数**: 无
**返回值**: Item - 炸弹或双倍炸弹

### glowing()
**签名**: `ItemSprite.Glowing glowing()`
**功能**: 获取发光效果
**参数**: 无
**返回值**: ItemSprite.Glowing - 红色发光（点燃时）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述
**参数**: 无
**返回值**: String - 包含伤害范围的描述

## 内部类 Fuse

继承自Actor，表示炸弹的引信。

### ignite(Bomb bomb)
**功能**: 点燃炸弹
**参数**:
- bomb: Bomb - 要点燃的炸弹
**返回值**: Fuse - 引信实例

### act()
**功能**: 每帧执行的行为
**实现逻辑**:
1. 检查炸弹引信是否有效（第294-297行）
2. 查找炸弹所在的堆（第300-306行）
3. 触发爆炸（第303-304行）

### trigger(Heap heap)
**功能**: 触发爆炸
**参数**:
- heap: Heap - 炸弹所在的堆
**实现逻辑**:
1. 从堆中移除炸弹（第315行）
2. 记录使用统计（第316行）
3. 执行爆炸（第317行）
4. 移除引信（第318行）

### freeze() / snuff()
**功能**: 冻结/熄灭引信

## 内部类 DoubleBomb

双倍炸弹，拾取时变成2个普通炸弹。

### doPickUp(Hero hero, int pos)
**功能**: 拾取时转换为2个普通炸弹
**实现逻辑**:
- 创建2个普通炸弹并拾取（第342-344行）
- 在英文环境下显示"1+1 free!"（第346-347行）

## 内部类 EnhanceBomb

炸弹强化配方类，用于制作各种特殊炸弹。

### validIngredients 映射表

| 原料 | 炸弹类型 |
|------|---------|
| 冰霜药水 | FrostBomb |
| 镜像卷轴 | WoollyBomb |
| 液体火焰药水 | Firebomb |
| 愤怒卷轴 | Noisemaker |
| 隐身药水 | SmokeBomb |
| 充能卷轴 | FlashBangBomb |
| 治疗药水 | RegrowthBomb |
| 解咒卷轴 | HolyBomb |
| Goo粘液 | ArcaneBomb |
| 金属碎片 | ShrapnelBomb |

### bombCosts 成本表

| 炸弹类型 | 炼金能量 |
|---------|---------|
| FrostBomb, WoollyBomb | 0 |
| Firebomb, Noisemaker | 1 |
| SmokeBomb, FlashBangBomb | 2 |
| RegrowthBomb, HolyBomb | 3 |
| ArcaneBomb, ShrapnelBomb | 6 |

## 11. 使用示例
```java
// 创建炸弹
Bomb bomb = new Bomb();

// 点燃并投掷
bomb.execute(hero, Bomb.AC_LIGHTTHROW);
// 2回合后爆炸

// 直接投掷（不会爆炸）
bomb.execute(hero, Item.AC_THROW);

// 拾取点燃的炸弹会熄灭
bomb.fuse = new Fuse().ignite(bomb);
bomb.doPickUp(hero, pos); // 熄灭引信

// 制作特殊炸弹
// 炸弹 + 冰霜药水 = 冰霜炸弹
// 成本: 0点炼金能量
```

## 注意事项
1. 点燃后2回合爆炸
2. 爆炸会破坏可燃地形
3. 伤害随深度增加
4. 拾取点燃的炸弹会熄灭
5. 可以通过炼金制作特殊炸弹

## 最佳实践
1. 先点燃再投掷造成最大伤害
2. 避免在可燃地形使用普通炸弹
3. 制作特殊炸弹获得额外效果
4. 双倍炸弹性价比高
5. 注意不要炸到自己