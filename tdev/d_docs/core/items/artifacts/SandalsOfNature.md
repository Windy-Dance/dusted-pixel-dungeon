# SandalsOfNature 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/SandalsOfNature.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 374行 |

## 2. 类职责说明
自然之鞋是一个成长型神器，通过喂食种子来升级。每次升级鞋子形态会变化（凉鞋→鞋子→靴子→护腿），装备时踩踏草地可以获得充能。喂食特定种子后可以解锁该种子的能力，消耗充能在目标位置触发植物效果。

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
    
    class SandalsOfNature {
        +ArrayList~Class~ seeds
        +Class curSeedEffect
        +String AC_FEED
        +String AC_ROOT
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +ItemSprite.Glowing glowing()
        +String name()
        +String desc()
        +Item upgrade()
        +boolean canUseSeed(Item)
    }
    
    class Naturalism {
        +void charge()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- SandalsOfNature
    ArtifactBuff <|-- Naturalism
    SandalsOfNature +-- Naturalism
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_FEED | String | "FEED" | 喂食动作标识 |
| AC_ROOT | String | "ROOT" | 触发种子效果动作标识 |
| SEEDS | String | "seeds" | Bundle存储键 |
| CUR_SEED_EFFECT | String | "cur_seed_effect" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，根据等级变化 |
| levelCap | int | - | 等级上限为3级 |
| charge | int | - | 当前充能，初始为0 |
| chargeCap | int | - | 充能上限为100 |
| defaultAction | String | - | 默认动作为AC_ROOT |
| seeds | ArrayList<Class> | public | 已喂食的种子列表 |
| curSeedEffect | Class | public | 当前激活的种子效果 |
| seedColors | HashMap | private static | 种子颜色映射 |
| seedChargeReqs | HashMap | private static | 种子充能需求映射 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第123行）
2. 检查魔法免疫（第124-126行）
3. 如果已装备且未诅咒，添加喂食动作（第127-129行）
4. 如果有种子效果且充能足够，添加触发动作（第130-135行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第141行）
2. 检查魔法免疫（第143行）
3. 如果动作是AC_FEED，打开物品选择器（第145-147行）
4. 如果动作是AC_ROOT，打开目标选择器（第149-157行）

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - Naturalism实例

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 为鞋子充能
**参数**:
- target: Hero - 目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
- 增加充能，不超过上限（第168-179行）

### glowing()
**签名**: `ItemSprite.Glowing glowing()`
**功能**: 获取物品发光效果
**参数**: 无
**返回值**: ItemSprite.Glowing - 发光效果（基于当前种子颜色）
**实现逻辑**:
- 如果有种子效果，返回对应颜色的发光效果（第184-185行）

### name()
**签名**: `String name()`
**功能**: 获取物品名称
**参数**: 无
**返回值**: String - 名称（根据等级变化）
**实现逻辑**:
- 等级0返回默认名称（第192行）
- 否则返回对应等级的名称（第193行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第198行）
2. 添加装备状态描述（第200-208行）
3. 添加当前种子效果信息（第211-215行）
4. 添加已喂食种子数量（第217-219行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级鞋子，更新图标
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
- 根据等级更新图标（第226-229行）

### canUseSeed(Item item)
**签名**: `boolean canUseSeed(Item item)`
**功能**: 检查种子是否可以使用
**参数**:
- item: Item - 物品
**返回值**: boolean - 是否可用
**实现逻辑**:
1. 检查是否是种子（第234行）
2. 检查是否已在列表中（第235行）
3. 检查是否已满级且效果相同（第236行）

## 内部类 Naturalism

### charge()
**签名**: `void charge()`
**功能**: 通过踩踏草地充能
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫（第273行）
2. 计算充能增益：(3 + 等级) / 6（第276行）
3. 应用能量戒指加成（第277行）
4. 增加充能（第278-285行）

## 静态字段 itemSelector

物品选择器，用于选择要喂食的种子。

### onSelect(Item item)
**功能**: 选择种子后的处理
**参数**:
- item: Item - 选中的物品
**实现逻辑**:
1. 检查是否是有效种子（第308行）
2. 如果未满级，添加到种子列表（第309行）
3. 设置当前种子效果（第310行）
4. 播放动画和音效（第313-316行）
5. 如果种子足够，升级鞋子（第317-324行）
6. 移除种子物品（第328行）

## 静态字段 cellSelector

目标选择器，用于选择触发种子效果的位置。

### onSelect(Integer cell)
**功能**: 选择目标位置后的处理
**参数**:
- cell: Integer - 目标位置
**实现逻辑**:
1. 检查距离和视野（第339-340行）
2. 播放粒子效果（第344-349行）
3. 触发植物效果（第351-352行）
4. 消耗充能（第360行）
5. 触发神器使用天赋（第361行）

## 种子充能需求表

| 种子 | 充能需求 |
|------|---------|
| 腐莓种子 | 8 |
| 火缚根种子 | 20 |
| 迅速藤种子 | 20 |
| 太阳草种子 | 80 |
| 冰帽种子 | 20 |
| 风暴藤种子 | 20 |
| 悲伤苔种子 | 20 |
| 法皇种子 | 12 |
| 地根种子 | 40 |
| 星花种子 | 40 |
| 消失叶种子 | 12 |
| 盲草种子 | 12 |

## 11. 使用示例
```java
// 创建自然之鞋
SandalsOfNature sandals = new SandalsOfNature();

// 装备鞋子
sandals.doEquip(hero);

// 喂食种子升级
sandals.execute(hero, SandalsOfNature.AC_FEED);
// 选择种子喂食
// 喂食3/6/9个种子升级到1/2/3级

// 踩踏草地充能
Naturalism buff = hero.buff(Naturalism.class);
if (buff != null) buff.charge();

// 触发种子效果
sandals.execute(hero, SandalsOfNature.AC_ROOT);
// 选择目标位置触发效果
```

## 注意事项
1. 需要喂食种子来升级和激活效果
2. 升级需要喂食特定数量的种子（3/6/9）
3. 踩踏草地可以自然充能
4. 不同种子效果有不同的充能需求
5. 满级后可以更换种子效果

## 最佳实践
1. 优先喂食高价值的种子（太阳草、星花等）
2. 在草地多的区域多走动充能
3. 根据战斗需要选择合适的种子效果
4. 太阳草种子提供强大的治疗效果
5. 星花种子提供增益效果