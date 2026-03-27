# SkeletonKey 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/SkeletonKey.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 679行 |

## 2. 类职责说明
骷髅钥匙是一个多功能神器，可以替代各种钥匙的功能。它可以打开锁门、水晶门、锁箱和水晶箱，还可以将普通门锁上，甚至创建临时墙壁来阻挡敌人。每次使用都会消耗充能并获得经验。

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
    
    class SkeletonKey {
        +String AC_INSERT
        +CellSelector.Listener targeter
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +void gainExp(int)
        +Item upgrade()
        +String desc()
        -void placeWall(int, int)
    }
    
    class keyRecharge {
        +boolean act()
    }
    
    class KeyWall {
        +void evolve()
        +void onBuildFlagMaps(Level)
        +void onUpdateCellFlags(Level, int)
    }
    
    class KeyReplacementTracker {
        +int[] ironKeysNeeded
        +int[] goldenKeysNeeded
        +int[] crystalKeysNeeded
        +void setupKeysForDepth()
        +void processIronLockOpened()
        +void processGoldLockOpened()
        +void processCrystalLockOpened()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    class Blob {
        <<abstract>>
    }
    
    class Buff {
        <<abstract>>
    }
    
    Artifact <|-- SkeletonKey
    ArtifactBuff <|-- keyRecharge
    Blob <|-- KeyWall
    Buff <|-- KeyReplacementTracker
    SkeletonKey +-- keyRecharge
    SkeletonKey +-- KeyWall
    SkeletonKey +-- KeyReplacementTracker
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_INSERT | String | "INSERT" | 插入钥匙动作标识 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_KEY |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为3+等级/2 |
| chargeCap | int | - | 充能上限，3+等级/2 |
| defaultAction | String | - | 默认动作为AC_INSERT |
| targeter | CellSelector.Listener | public | 目标选择监听器 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第82行）
2. 检查条件（第83-85行）：已装备、无魔法免疫、未诅咒
3. 满足条件时添加AC_INSERT动作（第86行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第93行）
2. 检查魔法免疫（第95行）
3. 如果动作是AC_INSERT（第97-111行）：
   - 检查装备和诅咒状态
   - 打开目标选择器

### gainExp(int xpGain)
**签名**: `void gainExp(int xpGain)`
**功能**: 获得经验并可能升级
**参数**:
- xpGain: int - 经验值
**返回值**: void
**实现逻辑**:
1. 检查是否已满级（第116-118行）
2. 增加经验（第120行）
3. 经验足够时升级（第121-126行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级钥匙，增加充能上限
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 更新充能上限：3 + (等级+1)/2（第455行）
2. 调用父类upgrade方法（第456行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第410行）
2. 添加装备状态描述（第412-418行）

### placeWall(int pos, int knockbackDIR)
**签名**: `private void placeWall(int pos, int knockbackDIR)`
**功能**: 在指定位置创建临时墙壁
**参数**:
- pos: int - 位置
- knockbackDIR: int - 击退方向
**返回值**: void
**实现逻辑**:
1. 获取现有的墙壁Blob（第460行）
2. 如果位置可行走或已有墙壁，添加新墙壁（第461-462行）
3. 如果有敌人，将其击退（第464-468行）

## 内部类 keyRecharge

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的充能逻辑
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 检查充能未满、未诅咒、无魔法免疫、可恢复（第426-429行）
2. 计算充能速度：1 / (120 - (上限-当前)*7.5)（第431行）
3. 应用能量戒指加成（第432行）
4. 转换部分充能为整数充能（第435-442行）

## 内部类 KeyWall

继承自Blob，表示钥匙创建的临时墙壁。

### evolve()
**功能**: 每帧更新墙壁状态
**实现逻辑**:
1. 遍历所有单元格（第485-502行）
2. 减少墙壁持续时间（第488行）
3. 更新体积和标记（第497-501行）

### onUpdateCellFlags(Level l, int cell)
**功能**: 更新单元格标志
**实现逻辑**:
- 如果有墙壁，设置losBlocking、solid、passable、avoid标志（第540-545行）

## 内部类 KeyReplacementTracker

继承自Buff，追踪钥匙使用情况以优化钥匙丢弃。

### setupKeysForDepth()
**功能**: 设置当前层的钥匙需求
**实现逻辑**:
1. 初始化所有钥匙需求为0（第577-579行）
2. 统计锁箱数量（第581-587行）
3. 统计锁门和水晶门数量（第589-595行）

### processIronLockOpened() / processGoldLockOpened() / processCrystalLockOpened()
**功能**: 处理打开锁后的钥匙需求更新
**实现逻辑**:
1. 检查是否需要初始化（第606-608行等）
2. 减少对应钥匙需求（第609行等）
3. 处理多余的钥匙（第610行等）

### processExcessKeys()
**功能**: 移除多余的钥匙
**实现逻辑**:
1. 检查每种钥匙的需求（第630-651行）
2. 移除超过需求的钥匙（第633-636行等）
3. 显示丢弃提示（第652-655行）

## 目标选择器功能

### 锁门 (LOCKED_DOOR)
- 消耗1充能
- 获得经验+3
- 不能打开锁定的出口门

### 水晶门 (CRYSTAL_DOOR)
- 消耗5充能
- 获得经验+7
- 打开后显示发现效果

### 普通门 (DOOR/OPEN_DOOR)
- 消耗2充能
- 获得经验+2
- 将门锁上（变为HERO_LKD_DR）
- 如果门上有角色，会将其推开

### 锁箱 (LOCKED_CHEST)
- 消耗2充能
- 获得经验+4

### 水晶箱 (CRYSTAL_CHEST)
- 消耗5充能
- 获得经验+7

### 创建墙壁
- 消耗2充能
- 获得经验+2
- 在目标方向创建临时墙壁
- 墙壁持续10回合

## 11. 使用示例
```java
// 创建骷髅钥匙
SkeletonKey key = new SkeletonKey();

// 装备钥匙
key.doEquip(hero);

// 使用钥匙
key.execute(hero, SkeletonKey.AC_INSERT);
// 选择目标：
// - 锁门：打开
// - 水晶门：打开
// - 普通门：锁上
// - 锁箱/水晶箱：打开
// - 其他位置：创建临时墙壁
```

## 注意事项
1. 不能打开锁定的出口门
2. 锁门时如果有角色，会将其推开
3. 创建的墙壁持续10回合
4. 使用会获得经验，经验足够会升级
5. 自动追踪钥匙使用，优化钥匙携带

## 最佳实践
1. 用于替代各种钥匙，节省背包空间
2. 创建墙壁阻挡敌人追击
3. 升级增加充能上限
4. 配合能量戒指提高充能效率
5. 注意钥匙使用后的钥匙丢弃优化