# ChaliceOfBlood 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/ChaliceOfBlood.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 236行 |

## 2. 类职责说明
血之圣杯是一个成长型神器，玩家通过牺牲自身生命值来升级圣杯。装备后圣杯会提供被动的生命恢复加成，等级越高恢复效果越强。每次"刺击"（牺牲生命）都会对玩家造成伤害，伤害随等级增长呈指数级增加。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Artifact {
        <<abstract>>
        +int levelCap
        +ArtifactBuff passiveBuff()
        +Item upgrade()
    }
    
    class ChaliceOfBlood {
        +String AC_PRICK
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +Item upgrade()
        +void charge(Hero, float)
        +String desc()
        -int minPrickDmg()
        -int maxPrickDmg()
        -void prick(Hero)
    }
    
    class chaliceRegen {
        +继承自ArtifactBuff
        +无额外方法
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    Artifact <|-- ChaliceOfBlood
    ArtifactBuff <|-- chaliceRegen
    ChaliceOfBlood +-- chaliceRegen
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_PRICK | String | "PRICK" | 刺击动作标识，用于牺牲生命升级圣杯 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，根据等级变化（CHALICE1/2/3） |
| levelCap | int | - | 等级上限为10级 |

## 7. 方法详解

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法获取基础动作列表（第63行）
2. 检查条件（第64-68行）：
   - 已装备
   - 等级未达上限
   - 未诅咒
   - 英雄不是无敌状态
   - 英雄无魔法免疫
3. 满足条件时添加AC_PRICK动作（第69行）
4. 返回动作列表（第70行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第75行）
2. 如果动作是AC_PRICK（第77-112行）：
   - 计算最小和最大伤害（第79-80行）
   - 计算总生命值（HP+护盾）（第82行）
   - 计算死亡概率（第84-97行）
   - 显示确认窗口，包含伤害范围和死亡概率（第99-112行）

### minPrickDmg()
**签名**: `private int minPrickDmg()`
**功能**: 计算刺击的最小伤害
**参数**: 无
**返回值**: int - 最小伤害值
**实现逻辑**:
- 计算公式：ceil(3 + 2.5 * 等级²)（第118行）

### maxPrickDmg()
**签名**: `private int maxPrickDmg()`
**功能**: 计算刺击的最大伤害
**参数**: 无
**返回值**: int - 最大伤害值
**实施逻辑**:
- 计算公式：floor(7 + 3.5 * 等级²)（第122行）

### prick(Hero hero)
**签名**: `private void prick(Hero hero)`
**功能**: 执行刺击操作，造成伤害并升级圣杯
**参数**:
- hero: Hero - 执行刺击的英雄
**返回值**: void
**实现逻辑**:
1. 随机计算伤害值（第126行）
2. 处理各种护甲减伤效果（第129-143行）：
   - 地根草护甲减伤（第129-132行）
   - 圣域护甲减伤（第134-136行）
   - 岩石护甲减伤（第138-141行）
   - 英雄防御减免（第143行）
3. 播放动画和音效（第145-154行）
4. 对英雄造成伤害（第156行）
5. 如果英雄死亡，记录失败信息（第158-161行）
6. 如果存活，升级圣杯（第163-164行）

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级圣杯并更新图标
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 根据等级更新图标（第170-173行）：
   - 等级>=6: 使用CHALICE3图标
   - 等级>=2: 使用CHALICE2图标
2. 调用父类upgrade方法（第174行）

### restoreFromBundle(Bundle bundle)
**签名**: `void restoreFromBundle(Bundle bundle)`
**功能**: 从Bundle恢复物品状态并更新图标
**参数**:
- bundle: Bundle - 存储容器
**返回值**: void
**实现逻辑**:
1. 调用父类restoreFromBundle方法（第179行）
2. 根据等级更新图标（第180-181行）：
   - 等级>=7: 使用CHALICE3图标
   - 等级>=3: 使用CHALICE2图标

### passiveBuff()
**签名**: `protected ArtifactBuff passiveBuff()`
**功能**: 获取被动Buff实例
**参数**: 无
**返回值**: ArtifactBuff - chaliceRegen实例
**实现逻辑**:
- 创建并返回新的chaliceRegen内部类实例（第186行）

### charge(Hero target, float amount)
**签名**: `void charge(Hero target, float amount)`
**功能**: 提供即时治疗效果
**参数**:
- target: Hero - 接受治疗的目标英雄
- amount: float - 充能数量
**返回值**: void
**实现逻辑**:
1. 检查诅咒和魔法免疫状态（第191行）
2. 检查英雄是否饥饿（第194行）
3. 计算治疗量（第196-202行）：
   - 治疗延迟 = 10 - (1.33 + 等级*0.667)
   - 治疗量 = 5 / 治疗延迟
4. 如果治疗量>=1且英雄未满血，进行治疗（第203-210行）
5. 满血时停止休息（第207-209行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述文本（第215行）
2. 如果已装备（第217-227行）：
   - 诅咒状态：显示诅咒描述
   - 等级0：显示初始描述
   - 未满级：显示升级提示
   - 满级：显示满级描述

## 内部类 chaliceRegen

此类继承自ArtifactBuff，没有额外的字段或方法。它的作用是通过Regeneration类来实现被动生命恢复效果。圣杯等级越高，恢复速度越快。

## 11. 使用示例
```java
// 创建血之圣杯
ChaliceOfBlood chalice = new ChaliceOfBlood();

// 装备圣杯
chalice.doEquip(hero);

// 使用刺击升级（牺牲生命）
chalice.execute(hero, ChaliceOfBlood.AC_PRICK);

// 等级越高，伤害越大：
// +0: 3-7伤害
// +5: 65-95伤害
// +9: 205-291伤害
```

## 注意事项
1. 刺击伤害随等级呈指数增长，高等级时可能致命
2. 升级前确保有足够的生命值
3. 圣杯提供的治疗只在非饥饿状态下生效
4. 诅咒状态下圣杯会持续抽取生命
5. 死亡概率会被显示在确认窗口中

## 最佳实践
1. 在安全区域使用刺击升级
2. 准备好治疗药水或食物
3. 低等级时频繁升级更安全
4. 配合高生命恢复装备可以更快恢复
5. 圣杯+10时提供最强的被动恢复效果