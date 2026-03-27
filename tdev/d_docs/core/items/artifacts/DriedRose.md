# DriedRose 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/DriedRose.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 1044行 |

## 2. 类职责说明
枯萎玫瑰是一个召唤型神器，需要完成幽灵任务后才能使用。装备后可以消耗充能召唤幽灵英雄作为盟友，幽灵可以装备武器和护甲。通过收集花瓣来升级玫瑰，提升幽灵的属性和生命值。幽灵死亡后需要重新充能才能再次召唤。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int charge
        +int chargeCap
        +int levelCap
        +ArtifactBuff passiveBuff()
    }
    
    class DriedRose {
        -boolean talkedTo
        -boolean firstSummon
        -GhostHero ghost
        -int ghostID
        -MeleeWeapon weapon
        -Armor armor
        -int droppedPetals
        +String AC_SUMMON
        +String AC_DIRECT
        +String AC_OUTFIT
        +ArrayList~String~ actions(Hero)
        +String defaultAction()
        +void execute(Hero, String)
        +int ghostStrength()
        +String status()
        +Weapon ghostWeapon()
        +Armor ghostArmor()
    }
    
    class Petal {
        +boolean doPickUp(Hero, int)
        +boolean isUpgradable()
        +boolean isIdentified()
    }
    
    class GhostHero {
        -DriedRose rose
        +void defendPos(int)
        +void followHero()
        +void targetChar(Char)
        +Weapon weapon()
        +Armor armor()
        +int attackSkill(Char)
        +int damageRoll()
        +void sayAppeared()
        +void sayBoss()
        +void sayDefeated()
    }
    
    class roseRecharge {
        +boolean act()
    }
    
    class WndGhostHero {
        -ItemButton btnWeapon
        -ItemButton btnArmor
    }
    
    Artifact <|-- DriedRose
    Item <|-- Petal
    DirectableAlly <|-- GhostHero
    ArtifactBuff <|-- roseRecharge
    Window <|-- WndGhostHero
    DriedRose +-- Petal
    DriedRose +-- GhostHero
    DriedRose +-- roseRecharge
    DriedRose +-- WndGhostHero
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_SUMMON | String | "SUMMON" | 召唤动作标识 |
| AC_DIRECT | String | "DIRECT" | 指挥动作标识 |
| AC_OUTFIT | String | "OUTFIT" | 装备动作标识 |
| TALKEDTO | String | "talkedto" | Bundle存储键 |
| FIRSTSUMMON | String | "firstsummon" | Bundle存储键 |
| GHOSTID | String | "ghostID" | Bundle存储键 |
| PETALS | String | "petals" | Bundle存储键 |
| WEAPON | String | "weapon" | Bundle存储键 |
| ARMOR | String | "armor" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，根据等级变化 |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为100 |
| chargeCap | int | - | 充能上限为100 |
| defaultAction | String | - | 默认动作（召唤或指挥） |
| talkedTo | boolean | private | 是否与幽灵交谈过 |
| firstSummon | boolean | private | 是否首次召唤 |
| ghost | GhostHero | private | 当前幽灵实例 |
| ghostID | int | private | 幽灵Actor ID |
| weapon | MeleeWeapon | private | 幽灵装备的武器 |
| armor | Armor | private | 幽灵装备的护甲 |
| droppedPetals | int | public | 已掉落的花瓣数量 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 获取基础动作列表（第115行）
2. 检查幽灵任务是否完成，未完成则返回空列表（第116-118行）
3. 检查召唤条件（第119-124行）：已装备、充能满、未诅咒、无魔法免疫、无活跃幽灵
4. 检查指挥条件：有活跃幽灵（第126-128行）
5. 检查装备条件：已鉴定且未诅咒（第129-131行）

### defaultAction()
**签名**: `String defaultAction()`
**功能**: 获取默认动作
**参数**: 无
**返回值**: String - 默认动作名称
**实现逻辑**:
- 如果有活跃幽灵，返回指挥动作（第138-139行）
- 否则返回召唤动作（第141行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. AC_SUMMON（第150-204行）：
   - 检查各种条件
   - 寻找召唤点
   - 创建幽灵并添加到场景
   - 播放特效和音效
   - 首次召唤显示特殊对话
2. AC_DIRECT（第206-212行）：
   - 找到幽灵实例
   - 打开单元格选择器指挥幽灵
3. AC_OUTFIT（第214-216行）：
   - 打开幽灵装备窗口

### findGhost()
**签名**: `private void findGhost()`
**功能**: 查找幽灵实例
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 通过Actor ID查找幽灵（第220-222行）
2. 如果未找到，检查是否在停滞状态中（第224-230行）

### ghostStrength()
**签名**: `int ghostStrength()`
**功能**: 获取幽灵的力量值
**参数**: 无
**返回值**: int - 力量值
**实现逻辑**:
- 计算公式：13 + 等级/2（第234行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 检查幽灵任务是否完成（第239-242行）
2. 获取基础描述（第244行）
3. 添加装备状态描述（第246-255行）
4. 添加装备信息（第257-270行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值
**实现逻辑**:
- 如果有装备的武器或护甲，返回-1（不可出售）（第277-282行）
- 否则返回父类价值（第283行）

### status()
**签名**: `String status()`
**功能**: 获取状态显示
**参数**: 无
**返回值**: String - 状态字符串
**实现逻辑**:
1. 查找幽灵实例（第288-295行）
2. 如果有幽灵，返回其生命百分比（第297-299行）
3. 否则返回父类状态（第297行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - roseRecharge实例
**实现逻辑**:
- 创建并返回新的roseRecharge实例（第305行）

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为玫瑰充能或治疗幽灵
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第310行）
2. 如果没有活跃幽灵，为玫瑰充能（第312-325行）
3. 如果有活跃幽灵且未满血，治疗幽灵（第326-333行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级玫瑰并更新图标
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 根据等级更新图标（第338-341行）
2. 更新已掉落花瓣数（第344行）
3. 如果有活跃幽灵，更新其属性并恢复生命（第346-349行）

### ghostWeapon() / ghostArmor()
**签名**: `Weapon ghostWeapon() / Armor ghostArmor()`
**功能**: 获取幽灵的装备
**参数**: 无
**返回值**: Weapon/Armor - 装备实例
**实现逻辑**:
- 返回武器或护甲字段（第354-360行）

## 内部类 Petal

### doPickUp(Hero hero, int pos)
**签名**: `boolean doPickUp(Hero hero, int pos)`
**功能**: 拾取花瓣
**参数**:
- hero: Hero - 拾取的英雄
- pos: int - 拾取位置
**返回值**: boolean - 是否成功
**实现逻辑**:
1. 检查是否有枯萎玫瑰（第502-506行）
2. 如果玫瑰已满级，提示无空间（第507-510行）
3. 否则升级玫瑰并播放音效（第512-524行）

## 内部类 GhostHero

### updateRose()
**签名**: `private void updateRose()`
**功能**: 更新玫瑰引用和幽灵属性
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 如果没有玫瑰引用，从英雄背包获取（第585-591行）
2. 设置防御技能为英雄等级+4（第594行）
3. 计算最大生命：20 + 8*玫瑰等级（第596行）

### act()
**签名**: `protected boolean act()`
**功能**: 每帧执行的行为
**参数**: 无
**返回值**: boolean - 是否继续
**实现逻辑**:
1. 更新玫瑰引用（第611行）
2. 如果没有玫瑰或未装备或英雄有魔法免疫，受到伤害（第612-616行）
3. 执行父类act（第622行）

### attackSkill(Char target)
**签名**: `int attackSkill(Char target)`
**功能**: 获取攻击技能值
**参数**:
- target: Char - 目标
**返回值**: int - 攻击技能值
**实现逻辑**:
- 基础值：英雄等级 + 9（第630行）
- 如果有武器，乘以武器准确率因子（第632-634行）

### damageRoll()
**签名**: `int damageRoll()`
**功能**: 计算伤害
**参数**: 无
**返回值**: int - 伤害值
**实现逻辑**:
- 如果有武器，使用武器的伤害计算（第656-657行）
- 否则随机0-5伤害（第659行）

### sayAppeared()
**签名**: `void sayAppeared()`
**功能**: 幽灵出现时的对话
**参数**: 无
**返回值**: void
**实现逻辑**:
- 根据当前深度选择不同的对话内容（第779-807行）

### sayBoss()
**签名**: `void sayBoss()`
**功能**: 看到Boss时的对话
**参数**: 无
**返回值**: void
**实现逻辑**:
- 根据当前深度选择对应Boss的对话（第813-833行）

## 11. 使用示例
```java
// 创建枯萎玫瑰
DriedRose rose = new DriedRose();

// 需要完成幽灵任务才能使用
if (Ghost.Quest.completed()) {
    // 装备玫瑰
    rose.doEquip(hero);
    
    // 召唤幽灵
    rose.execute(hero, DriedRose.AC_SUMMON);
    
    // 指挥幽灵
    rose.execute(hero, DriedRose.AC_DIRECT);
    
    // 为幽灵装备物品
    rose.execute(hero, DriedRose.AC_OUTFIT);
}

// 收集花瓣升级
Petal petal = new Petal();
petal.doPickUp(hero, pos); // 自动升级玫瑰
```

## 注意事项
1. 必须完成幽灵任务才能使用
2. 召唤需要100%充能
3. 幽灵死亡后充能归零
4. 幽灵装备的物品不能是独特物品或诅咒物品
5. 装备需要满足幽灵的力量要求

## 最佳实践
1. 尽早收集花瓣升级玫瑰
2. 为幽灵装备合适的武器和护甲
3. 在Boss战前召唤幽灵
4. 使用指挥功能控制幽灵位置
5. 注意保护幽灵避免其死亡