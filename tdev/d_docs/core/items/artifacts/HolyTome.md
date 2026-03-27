# HolyTome 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/HolyTome.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 374行 |

## 2. 类职责说明
神圣典籍是牧师职业专属的神器物品，允许玩家施放各种牧师法术。每种法术消耗不同数量的充能，使用法术会获得经验并升级典籍。牧师可以通过轻阅读天赋在不装备典籍的情况下使用它。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int chargeCap
        +int levelCap
        +int exp
        +ArtifactBuff passiveBuff()
    }
    
    class HolyTome {
        -ClericSpell quickSpell
        +ClericSpell targetingSpell
        +String AC_CAST
        +ArrayList~String~ actions(Hero)
        +int targetingPos(Hero, int)
        +void execute(Hero, String)
        +boolean doUnequip(Hero, boolean, boolean)
        +boolean collect(Bag)
        +boolean canCast(Hero, ClericSpell)
        +void spendCharge(float)
        +void directCharge(float)
        +Item upgrade()
        +void charge(Hero, float)
        +void setQuickSpell(ClericSpell)
    }
    
    class TomeRecharge {
        +boolean attachTo(Char)
        +void detach()
        +boolean act()
        +String actionName()
        +int actionIcon()
        +int indicatorColor()
        +void doAction()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    class ActionIndicator.Action {
        <<interface>>
    }
    
    Artifact <|-- HolyTome
    ArtifactBuff <|-- TomeRecharge
    ActionIndicator.Action <|.. TomeRecharge
    HolyTome +-- TomeRecharge
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_CAST | String | "CAST" | 施法动作标识 |
| QUICK_CLS | String | "quick_cls" | Bundle存储键，用于保存快捷法术 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_TOME |
| exp | int | - | 经验值，初始为0 |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为等级+3（最大10） |
| partialCharge | float | - | 部分充能值 |
| chargeCap | int | - | 充能上限，等级+3（最大10） |
| defaultAction | String | - | 默认动作为AC_CAST |
| unique | boolean | - | 是否为唯一物品（true） |
| bones | boolean | - | 是否可出现在遗骨中（false） |
| targetingSpell | ClericSpell | public | 当前目标的法术，用于目标选择 |
| quickSpell | ClericSpell | private | 快捷法术 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第71行）
2. 检查条件（第72-74行）：已装备或有轻阅读天赋、未诅咒、无魔法免疫
3. 满足条件时添加AC_CAST动作（第75行）

### targetingPos(Hero user, int dst)
**签名**: `int targetingPos(Hero user, int dst)`
**功能**: 获取目标位置（根据法术类型）
**参数**:
- user: Hero - 使用者
- dst: int - 目标坐标
**返回值**: int - 实际目标位置
**实现逻辑**:
1. 如果没有目标法术或法术不需要目标选择，返回原始目标（第105-106行）
2. 否则使用弹道计算返回碰撞位置（第108行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第83行）
2. 检查魔法免疫状态（第85行）
3. 如果动作是AC_CAST（第87-96行）：
   - 检查装备和诅咒状态（第89-90行）
   - 打开法术选择窗口（第93行）

### doUnequip(Hero hero, boolean collect, boolean single)
**签名**: `boolean doUnequip(Hero hero, boolean collect, boolean single)`
**功能**: 卸下典籍
**参数**:
- hero: Hero - 卸下装备的英雄
- collect: boolean - 是否放入背包
- single: boolean - 是否为单独操作
**返回值**: boolean - 是否卸下成功
**实现逻辑**:
1. 调用父类doUnequip方法（第114行）
2. 如果成功且有轻阅读天赋，重新激活（第115-117行）

### collect(Bag container)
**签名**: `boolean collect(Bag container)`
**功能**: 将典籍放入背包
**参数**:
- container: Bag - 容器
**返回值**: boolean - 是否成功
**实现逻辑**:
1. 调用父类collect方法（第126行）
2. 如果有轻阅读天赋，激活被动效果（第127-131行）

### onDetach()
**签名**: `protected void onDetach()`
**功能**: 从背包移除时的处理
**参数**: 无
**返回值**: void
**实现逻辑**:
- 移除被动Buff（第140-143行）

### canCast(Hero hero, ClericSpell spell)
**签名**: `boolean canCast(Hero hero, ClericSpell spell)`
**功能**: 检查是否可以施放指定法术
**参数**:
- hero: Hero - 英雄
- spell: ClericSpell - 法术
**返回值**: boolean - 是否可以施放
**实现逻辑**:
1. 检查装备或轻阅读天赋（第147行）
2. 检查魔法免疫（第148行）
3. 检查充能是否足够（第149行）
4. 检查法术是否可用（第150行）

### spendCharge(float chargesSpent)
**签名**: `void spendCharge(float chargesSpent)`
**功能**: 消耗充能并获得经验
**参数**:
- chargesSpent: float - 消耗的充能数
**返回值**: void
**实现逻辑**:
1. 扣除充能（第154-158行）
2. 计算经验值（第161-171行）
3. 经验足够时升级（第173-179行）

### directCharge(float amount)
**签名**: `void directCharge(float amount)`
**功能**: 直接增加充能
**参数**:
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
- 增加充能，不超过上限（第185-197行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级典籍，增加充能上限
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 增加充能上限，最大为10（第202行）
2. 调用父类upgrade方法（第203行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - TomeRecharge实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为典籍充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第213行）
2. 如果未装备，充能效率降低（第216行）
3. 增加充能（第217-227行）

### setQuickSpell(ClericSpell spell)
**签名**: `void setQuickSpell(ClericSpell spell)`
**功能**: 设置快捷法术
**参数**:
- spell: ClericSpell - 法术实例
**返回值**: void
**实现逻辑**:
1. 如果是同一个法术，清除快捷法术（第233-237行）
2. 否则设置新的快捷法术（第238-243行）

### storeInBundle(Bundle bundle) / restoreFromBundle(Bundle bundle)
**功能**: 序列化和反序列化快捷法术

## 内部类 TomeRecharge

实现了ActionIndicator.Action接口，允许设置快捷法术并通过动作指示器使用。

### attachTo(Char target) / detach()
**功能**: 附加/移除时设置/清除动作指示器

### act()
**功能**: 每帧执行的充能逻辑
**实现逻辑**:
1. 计算充能速度（第291-298行）
2. 转换部分充能为整数充能（第302-309行）

### actionName() / actionIcon() / indicatorColor()
**功能**: 返回动作指示器的显示信息

### doAction()
**功能**: 执行快捷法术
**实现逻辑**:
1. 检查诅咒和法术可用性（第342-350行）
2. 处理目标选择或直接施法（第352-370行）

## 11. 使用示例
```java
// 创建神圣典籍（牧师专属）
HolyTome tome = new HolyTome();

// 装备典籍
tome.doEquip(hero);

// 打开法术选择窗口
tome.execute(hero, HolyTome.AC_CAST);

// 检查是否可以施放法术
if (tome.canCast(hero, spell)) {
    spell.onCast(tome, hero);
}

// 设置快捷法术
tome.setQuickSpell(GuidingLight.INSTANCE);
```

## 注意事项
1. 牧师专属物品，其他职业无法有效使用
2. 轻阅读天赋允许未装备时使用（效率降低）
3. 使用法术消耗充能并获得经验
4. 典籍不能出售或出现在遗骨中
5. 快捷法术可通过动作指示器快速使用

## 最佳实践
1. 优先升级典籍增加充能上限
2. 设置常用法术为快捷法术
3. 合理使用法术避免充能不足
4. 配合能量戒指提高充能效率
5. 根据战斗情况选择合适的法术