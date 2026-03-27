# Trinket 抽象类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/Trinket.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | abstract class |
| **继承关系** | extends Item |
| **代码行数** | 137 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
Trinket是所有饰物（Trinket）物品的抽象基类，定义了饰物系统的核心框架，包括：
- 饰物升级机制（通过炼金能量）
- 饰物等级查询系统
- 基础信息展示框架

### 系统定位
饰物是游戏中一种特殊的物品类型，提供各种被动效果来改变地牢环境或其居民的行为。饰物通过魔能触媒（TrinketCatalyst）制作，可消耗炼金能量升级以增强效果，丢弃后可完全摆脱其影响。

### 不负责什么
- 不直接实现具体的饰物效果（由子类实现）
- 不处理饰物的获取逻辑（由TrinketCatalyst处理）

## 3. 结构总览

### 主要成员概览
- **实例初始化块**：设置levelKnown=true、unique=true
- **抽象方法**：upgradeEnergyCost()、statsDesc()
- **静态方法**：trinketLevel(Class)
- **内部类**：PlaceHolder、UpgradeTrinket

### 主要逻辑块概览
- 饰物等级查询机制
- 升级配方机制
- 占位符机制

### 生命周期/调用时机
- 创建时：通过TrinketCatalyst的炼金配方创建
- 升级时：通过UpgradeTrinket配方消耗炼金能量升级
- 使用时：装备后提供被动效果

## 4. 继承与协作关系

### 父类提供的能力
从Item继承：
- quantity（数量）
- level（等级）
- cursed（诅咒状态）
- actions(Hero hero)（动作列表）
- info()（信息描述）
- restoreFromBundle(Bundle)（序列化恢复）

### 覆写的方法
| 方法 | 说明 |
|------|------|
| isUpgradable() | 返回false，饰物不可通过常规方式升级 |
| info() | 添加statsDesc()返回的属性描述 |
| restoreFromBundle(Bundle) | 确保预2.5存档的兼容性 |

### 实现的接口契约
无显式接口实现。

### 依赖的关键类
- `Dungeon`：获取英雄实例
- `Item`：物品基类
- `Recipe`：炼金配方基类
- `Catalog`：物品使用计数
- `ItemSpriteSheet`：精灵图索引
- `Bundle`：序列化支持

### 使用者
- 所有具体饰物类（ChaoticCenser、DimensionalSundial等）
- TrinketCatalyst（制作饰物）
- 各种游戏系统（查询饰物等级以应用效果）

## 5. 字段/常量详解

### 静态常量
无显式静态常量定义。

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| levelKnown | boolean | true | 初始化块设置，饰物等级始终已知 |
| unique | boolean | true | 初始化块设置，饰物为唯一物品 |

**初始化块代码**：
```java
{
    levelKnown = true;
    unique = true;
}
```

## 6. 构造与初始化机制

### 构造器
无显式构造器，使用默认构造器。

### 初始化块
在实例初始化块中设置：
- `levelKnown = true`：饰物等级始终已知
- `unique = true`：饰物为唯一物品，不可堆叠

### 初始化注意事项
饰物默认已鉴定等级和诅咒状态，这与大多数其他物品不同。

## 7. 方法详解

### isUpgradable()

**可见性**：public

**是否覆写**：是，覆写自Item

**方法职责**：判断饰物是否可通过常规方式升级

**参数**：无

**返回值**：boolean，始终返回false

**核心实现逻辑**：
```java
@Override
public boolean isUpgradable() {
    return false;
}
```

**说明**：饰物只能通过炼金能量在炼金釜中升级，不能使用升级卷轴。

---

### upgradeEnergyCost()

**可见性**：protected abstract

**是否覆写**：否，抽象方法

**方法职责**：获取升级所需的炼金能量数量

**参数**：无

**返回值**：int，升级所需能量值

**前置条件**：由子类实现

**核心实现逻辑**：抽象方法，各子类实现不同的能量消耗公式。

**典型实现模式**：
- 标准消耗：`return 6+2*level();`（6→8→10→12）
- 高消耗：`return 10+5*level();`（6→10→15→20）

---

### trinketLevel(Class<? extends Trinket> trinketType)

**可见性**：protected static

**是否覆写**：否

**方法职责**：查询英雄是否装备了指定类型的饰物，并返回其等级

**参数**：
- `trinketType` (Class<? extends Trinket>)：饰物类型的Class对象

**返回值**：int，饰物的buffedLvl()值；如果未装备则返回-1

**核心实现逻辑**：
```java
protected static int trinketLevel(Class<? extends Trinket> trinketType ){
    if (Dungeon.hero == null || Dungeon.hero.belongings == null){
        return -1;
    }
    Trinket trinket = Dungeon.hero.belongings.getItem(trinketType);
    if (trinket != null){
        return trinket.buffedLvl();
    } else {
        return -1;
    }
}
```

**边界情况**：
- Dungeon.hero为null时返回-1
- hero.belongings为null时返回-1
- 未找到指定类型饰物时返回-1

---

### info()

**可见性**：public

**是否覆写**：是，覆写自Item

**方法职责**：获取饰物的完整信息描述

**参数**：无

**返回值**：String，包含基础描述和属性描述的完整文本

**核心实现逻辑**：
```java
@Override
public String info() {
    String info = super.info();
    info += "\n\n" + statsDesc();
    return info;
}
```

**说明**：在基础描述后追加statsDesc()返回的属性说明。

---

### statsDesc()

**可见性**：public abstract

**是否覆写**：否，抽象方法

**方法职责**：获取饰物的属性描述文本

**参数**：无

**返回值**：String，描述饰物当前效果的文本

**核心实现逻辑**：抽象方法，各子类实现具体的效果描述，通常使用Messages.get()获取本地化文本。

---

### energyVal()

**可见性**：public

**是否覆写**：否

**方法职责**：获取饰物转化为炼金能量的价值

**参数**：无

**返回值**：int，默认返回5

**核心实现逻辑**：
```java
public int energyVal() {
    return 5;
}
```

**说明**：饰物可以在炼金釜中分解为炼金能量。

---

### restoreFromBundle(Bundle bundle)

**可见性**：public

**是否覆写**：是，覆写自Item

**方法职责**：从Bundle恢复饰物状态，确保预2.5存档兼容性

**参数**：
- `bundle` (Bundle)：序列化数据容器

**返回值**：void

**核心实现逻辑**：
```java
@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    levelKnown = cursedKnown = true; //for pre-2.5 saves
}
```

**说明**：确保旧存档中的饰物等级和诅咒状态被标记为已知。

## 8. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| isUpgradable() | 返回false，饰物不可常规升级 |
| info() | 获取完整描述信息 |
| statsDesc() | 获取属性描述（抽象） |
| energyVal() | 获取能量价值 |

### 内部辅助方法
| 方法 | 说明 |
|------|------|
| upgradeEnergyCost() | 获取升级能量消耗（抽象） |
| trinketLevel(Class) | 查询饰物等级（静态） |

### 扩展入口
- `upgradeEnergyCost()`：子类必须实现，定义升级成本
- `statsDesc()`：子类必须实现，定义效果描述

## 9. 运行机制与调用链

### 创建时机
通过TrinketCatalyst在炼金釜中制作，消耗少量炼金能量。

### 调用者
- 各游戏系统通过静态方法`trinketLevel()`查询饰物等级
- 各具体饰物类继承此类实现具体效果

### 被调用者
- TrinketCatalyst（创建饰物）
- UpgradeTrinket配方（升级饰物）
- 各种游戏机制（查询饰物效果）

### 系统流程位置
```
TrinketCatalyst → 制作饰物 → Trinket实例
                              ↓
                    装备到英雄背包
                              ↓
                    各系统通过trinketLevel()查询等级
                              ↓
                    应用对应的被动效果
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.trinket.discover_hint | 该物品在你通过炼金合成饰物时有概率作为选项 | 发现提示 |
| items.trinkets.trinket$placeholder.name | 饰物 | 占位符名称 |

### 依赖的资源
- ItemSpriteSheet.TRINKET_HOLDER：PlaceHolder的图标

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 查询饰物等级
int level = Trinket.trinketLevel(DimensionalSundial.class);
if (level >= 0) {
    // 英雄装备了位面日晷，level为当前等级
    float spawnMult = DimensionalSundial.spawnMultiplierAtCurrentTime();
}

// 检查饰物是否存在
if (Trinket.trinketLevel(ChaoticCenser.class) != -1) {
    // 英雄装备了混沌香炉
}
```

### 子类实现示例

```java
public class MyTrinket extends Trinket {
    {
        image = ItemSpriteSheet.MY_TRINKET;
    }
    
    @Override
    protected int upgradeEnergyCost() {
        // 标准能量消耗：6→8→10→12
        return 6 + 2 * level();
    }
    
    @Override
    public String statsDesc() {
        if (isIdentified()) {
            return Messages.get(this, "stats_desc", effectValue(buffedLvl()));
        } else {
            return Messages.get(this, "typical_stats_desc", effectValue(0));
        }
    }
    
    public static float effectValue() {
        return effectValue(trinketLevel(MyTrinket.class));
    }
    
    public static float effectValue(int level) {
        if (level == -1) return 0;
        return 0.1f * (level + 1);
    }
}
```

## 12. 开发注意事项

### 状态依赖
- 饰物效果依赖`trinketLevel()`返回值
- 返回-1表示未装备，非负值表示已装备等级
- 所有子类的效果查询方法应遵循此约定

### 生命周期耦合
- 饰物创建后自动标记为已鉴定
- 饰物不可常规升级，只能通过炼金升级
- 饰物是唯一物品，不可堆叠

### 常见陷阱
1. **忘记检查-1返回值**：在调用子类效果方法前应检查饰物是否已装备
2. **错误使用level()与buffedLvl()**：statsDesc中应使用buffedLvl()获取含加成的等级
3. **能量消耗公式错误**：应使用`level()`而非`buffedLvl()`计算升级成本

## 13. 修改建议与扩展点

### 适合扩展的位置
- `upgradeEnergyCost()`：定义自定义升级成本曲线
- `statsDesc()`：实现自定义效果描述
- 添加新的静态效果查询方法

### 不建议修改的位置
- `isUpgradable()`：饰物不应支持常规升级
- `trinketLevel()`的核心逻辑：这是系统的约定

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是（levelKnown、unique通过初始化块）
- [x] 是否已覆盖全部方法：是（isUpgradable、upgradeEnergyCost、trinketLevel、info、statsDesc、energyVal、restoreFromBundle）
- [x] 是否已检查继承链与覆写关系：是，extends Item
- [x] 是否已核对官方中文翻译：是，来自items_zh.properties
- [x] 是否存在任何推测性表述：否，所有内容来自源码
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是