# UnstableSpellbook 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/UnstableSpellbook.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.artifacts |
| 类类型 | public class |
| 继承关系 | extends Artifact |
| 代码行数 | 416行 |

## 2. 类职责说明
不稳定法术书是一个随机型神器，使用时会随机施放一个普通卷轴的效果。通过喂食已鉴定的卷轴来升级，升级后可以使用对应卷轴的异域版本。法术书有索引列表，喂食列表顶部的卷轴可以升级它。

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
    
    class UnstableSpellbook {
        -ArrayList~Class~ scrolls
        +String AC_READ
        +String AC_ADD
        +ArrayList~String~ actions(Hero)
        +void execute(Hero, String)
        +void doReadEffect(Hero)
        +Item upgrade()
        +String desc()
        -void setupScrolls()
        -void checkForArtifactProc(Hero, Scroll)
    }
    
    class bookRecharge {
        +boolean act()
    }
    
    class ExploitHandler {
        +Scroll scroll
        +boolean act()
    }
    
    class ArtifactBuff {
        <<abstract>>
    }
    
    class Buff {
        <<abstract>>
    }
    
    Artifact <|-- UnstableSpellbook
    ArtifactBuff <|-- bookRecharge
    Buff <|-- ExploitHandler
    UnstableSpellbook +-- bookRecharge
    UnstableSpellbook +-- ExploitHandler
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_READ | String | "READ" | 阅读动作标识 |
| AC_ADD | String | "ADD" | 添加卷轴动作标识 |
| SCROLLS | String | "scrolls" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标，使用ItemSpriteSheet.ARTIFACT_SPELLBOOK |
| levelCap | int | - | 等级上限为10级 |
| charge | int | - | 当前充能，初始为等级*0.6+2 |
| partialCharge | float | - | 部分充能值 |
| chargeCap | int | - | 充能上限，等级*0.6+2 |
| defaultAction | String | - | 默认动作为AC_READ |
| scrolls | ArrayList<Class> | private final | 卷轴索引列表 |

## 7. 方法详解

### UnstableSpellbook()
**签名**: `public UnstableSpellbook()`
**功能**: 构造函数，初始化卷轴列表
**实现逻辑**:
1. 调用父类构造函数（第85行）
2. 调用setupScrolls初始化卷轴列表（第87行）

### setupScrolls()
**签名**: `private void setupScrolls()`
**功能**: 初始化卷轴索引列表
**参数**: 无
**返回值**: void
**实现逻辑**:
1. 清空列表（第91行）
2. 获取所有卷轴类和概率（第93-94行）
3. 随机添加卷轴到列表（第95-102行）
4. 移除变形卷轴（第103行）

### actions(Hero hero)
**签名**: `ArrayList<String> actions(Hero hero)`
**功能**: 获取该物品可用的动作列表
**参数**:
- hero: Hero - 英雄对象
**返回值**: ArrayList<String> - 动作名称列表
**实现逻辑**:
1. 调用父类actions方法（第108行）
2. 如果已装备、有充能、未诅咒、无魔法免疫，添加阅读动作（第109-111行）
3. 如果已装备、未满级、未诅咒、无魔法免疫，添加添加动作（第112-114行）

### execute(Hero hero, String action)
**签名**: `void execute(Hero hero, String action)`
**功能**: 执行指定的物品动作
**参数**:
- hero: Hero - 执行动作的英雄
- action: String - 要执行的动作名称
**返回值**: void
**实现逻辑**:
1. 调用父类execute方法（第121行）
2. 检查魔法免疫（第123行）
3. 如果动作是AC_READ（第125-133行）：
   - 检查盲目、装备、充能、诅咒状态
   - 执行阅读效果
4. 如果动作是AC_ADD（第135-137行）：
   - 打开物品选择器

### doReadEffect(Hero hero)
**签名**: `void doReadEffect(Hero hero)`
**功能**: 执行阅读效果
**参数**:
- hero: Hero - 英雄
**返回值**: void
**实现逻辑**:
1. 消耗1点充能（第141行）
2. 随机选择一个卷轴（第143-152行）：
   - 减少鉴定、解咒、地图卷轴的出现概率
   - 排除变形卷轴
3. 匿名化卷轴，禁用天赋触发（第154-155行）
4. 如果还有充能且卷轴不在索引中（第160-195行）：
   - 显示选项窗口
   - 选择普通版本或异域版本
   - 异域版本额外消耗1点充能
5. 否则直接使用普通卷轴（第196-200行）

### checkForArtifactProc(Hero user, Scroll scroll)
**签名**: `private void checkForArtifactProc(Hero user, Scroll scroll)`
**功能**: 检查是否触发神器效果
**参数**:
- user: Hero - 使用者
- scroll: Scroll - 卷轴
**返回值**: void
**实现逻辑**:
1. 如果是AOE效果卷轴（催眠、解咒、恐惧）（第207-213行）：
   - 对视野内所有敌人触发神器效果
2. 如果是愤怒卷轴（第215-218行）：
   - 对所有敌人触发神器效果

### upgrade()
**签名**: `Item upgrade()`
**功能**: 升级法术书，增加充能上限并移除已喂食的卷轴
**参数**: 无
**返回值**: Item - 升级后的物品
**实现逻辑**:
1. 更新充能上限：（等级+1）*0.6+2（第280行）
2. 移除索引顶部的卷轴（第283-285行）
3. 调用父类upgrade方法（第287行）

### resetForTrinity(int visibleLevel)
**签名**: `void resetForTrinity(int visibleLevel)`
**功能**: 为三位一体模式重置物品状态
**参数**:
- visibleLevel: int - 可见等级
**返回值**: void
**实现逻辑**:
1. 调用父类方法（第292行）
2. 重新初始化卷轴列表（第293行）
3. 移除超出等级的卷轴（第294-296行）

### desc()
**签名**: `String desc()`
**功能**: 获取物品描述文本
**参数**: 无
**返回值**: String - 描述文本
**实现逻辑**:
1. 获取基础描述（第301行）
2. 如果已装备且诅咒，添加诅咒描述（第304-306行）
3. 如果未满级且有索引，显示前两个卷轴名称（第308-313行）
4. 如果已升级，显示异域版本提示（第316-318行）

### storeInBundle(Bundle bundle) / restoreFromBundle(Bundle bundle)
**功能**: 序列化和反序列化卷轴索引列表

## 内部类 bookRecharge

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的充能逻辑
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 检查充能未满、未诅咒、无魔法免疫、可恢复（第345-348行）
2. 计算充能速度：1 / (120 - (上限-当前)*5)（第350行）
3. 应用能量戒指加成（第351行）
4. 转换部分充能为整数充能（第354-361行）

## 内部类 ExploitHandler

继承自Buff，用于防止玩家通过退出游戏来作弊选择想要的卷轴。

### act()
**签名**: `boolean act()`
**功能**: 强制使用普通卷轴
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 设置当前用户和物品（第230-231行）
2. 在渲染线程执行卷轴阅读（第234-240行）
3. 移除Buff（第241行）

## 静态字段 itemSelector

物品选择器，用于选择要喂食的卷轴。

### itemSelectable(Item item)
**功能**: 检查物品是否可选择
**参数**:
- item: Item - 物品
**返回值**: boolean - 是否可选择
**实现逻辑**:
- 必须是已鉴定的卷轴且在索引列表中（第386行）

### onSelect(Item item)
**功能**: 选择卷轴后的处理
**参数**:
- item: Item - 选中的物品
**实现逻辑**:
1. 检查是否是有效的已鉴定卷轴（第391行）
2. 遍历索引列表前两个位置（第393-409行）：
   - 如果匹配，播放动画，花费时间
   - 移除卷轴，升级法术书
   - 显示成功提示
3. 如果不在索引中，显示无法使用提示（第410行）
4. 如果未鉴定，显示未知卷轴提示（第411-413行）

## 11. 使用示例
```java
// 创建不稳定法术书
UnstableSpellbook book = new UnstableSpellbook();

// 装备法术书
book.doEquip(hero);

// 阅读法术书（随机卷轴效果）
book.execute(hero, UnstableSpellbook.AC_READ);
// 消耗1点充能
// 如果升级过且有充能，可以选择异域版本

// 喂食卷轴升级
book.execute(hero, UnstableSpellbook.AC_ADD);
// 选择索引列表中的卷轴喂食
// 必须是已鉴定的卷轴
```

## 注意事项
1. 变形卷轴不会随机出现
2. 鉴定、解咒、地图卷轴出现概率减半
3. 使用异域版本额外消耗1点充能
4. 必须喂食索引列表中的卷轴才能升级
5. 退出重进会强制使用普通卷轴（防止作弊）

## 最佳实践
1. 优先喂食常用卷轴以获得异域版本
2. 鉴定所有卷轴后再考虑喂食
3. 升级法术书增加充能上限
4. 配合能量戒指提高充能效率
5. 索引列表显示在描述中，注意查看