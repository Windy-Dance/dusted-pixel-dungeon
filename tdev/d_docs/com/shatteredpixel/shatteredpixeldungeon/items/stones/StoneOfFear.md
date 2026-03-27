# StoneOfFear 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/stones/StoneOfFear.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.stones |
| **文件类型** | class |
| **继承关系** | extends Runestone |
| **代码行数** | 55 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StoneOfFear（恐惧符石）是一种投掷型符石，被投掷到目标位置后，会对命中的非盟友角色施加恐惧效果，使其无法克制地逃离。

### 系统定位
位于 Runestone → StoneOfFear 继承链中，是一种控制型符石，可用于让敌人暂时远离战场。

### 不负责什么
- 不负责直接造成伤害
- 不负责对盟友产生效果

## 3. 结构总览

### 主要成员概览
- `image` - 精灵图设置

### 主要逻辑块概览
- `activate(int cell)` - 施加恐惧效果

### 生命周期/调用时机
1. 玩家投掷符石到目标位置
2. 符石激活
3. 对命中的非盟友角色施加 Terror Buff

## 4. 继承与协作关系

### 父类提供的能力
从 Runestone 继承：
- `stackable = true` - 可堆叠
- `defaultAction = AC_THROW` - 默认动作为投掷
- `onThrow()` - 投掷逻辑
- `activate()` - 激活方法（需覆写）

### 覆写的方法
| 方法 | 覆写逻辑 |
|------|----------|
| `activate(int cell)` | 对目标位置的非盟友角色施加恐惧 |

### 依赖的关键类
| 类名 | 用途 |
|------|------|
| `Actor` | 查找位置上的角色 |
| `Char` | 角色基类 |
| `Buff` | Buff 管理器 |
| `Terror` | 恐惧 Buff |
| `Flare` | 闪光视觉效果 |
| `DungeonTilemap` | 地图坐标转换 |
| `ItemSpriteSheet` | 精灵图定义 |
| `Sample` | 音效播放 |

## 5. 字段/常量详解

### 静态常量
无静态常量定义。

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `image` | int | ItemSpriteSheet.STONE_FEAR | 符石精灵图 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过实例初始化块设置属性：

```java
{
    image = ItemSpriteSheet.STONE_FEAR;
}
```

## 7. 方法详解

### activate(int cell)

**可见性**：protected

**是否覆写**：是，覆写自 Runestone

**方法职责**：对目标位置的非盟友角色施加恐惧效果。

**参数**：
- `cell` (int)：激活位置的格子坐标

**返回值**：void

**副作用**：
- 对目标施加 Terror Buff
- 播放红色闪光视觉效果
- 播放音效

**核心实现逻辑**：
```java
@Override
protected void activate(int cell) {
    Char ch = Actor.findChar( cell );

    // 只对非盟友角色生效
    if (ch != null && ch.alignment != Char.Alignment.ALLY ){
        Buff.affect( ch, Terror.class, Terror.DURATION ).object = curUser.id();
    }

    // 红色闪光效果
    new Flare( 5, 16 ).color( 0xFF0000, true ).show(
        Dungeon.hero.sprite.parent, 
        DungeonTilemap.tileCenterToWorld(cell), 
        2f 
    );
    Sample.INSTANCE.play( Assets.Sounds.READ );
}
```

**边界情况**：
- 目标位置无角色时，仅播放视觉效果
- 只对非盟友角色生效（敌人和中立单位）
- 盟友不受影响

## 8. 对外暴露能力

### 显式 API
| 方法 | 用途 |
|------|------|
| `activate(int cell)` | 激活符石效果（由父类调用） |

## 9. 运行机制与调用链

```
投掷动作 → Runestone.onThrow() → activate()
    → Actor.findChar() 查找目标
    → 检查是否为非盟友
    → Buff.affect(Terror.class) 施加恐惧
    → 播放视觉和音效
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.stones.stoneoffear.name | 恐惧符石 | 物品名称 |
| items.stones.stoneoffear.desc | 当把这颗符石掷向一个盟友或敌人时，被命中的角色会陷入深深的恐惧中... | 物品描述 |

### 依赖的资源
- `ItemSpriteSheet.STONE_FEAR` - 符石精灵图
- `Assets.Sounds.READ` - 阅读音效
- `Flare` - 闪光效果（红色）

### 中文翻译来源
来自 `items_zh.properties` 文件。

## 11. 使用示例

### 基本用法
```java
// 创建并投掷恐惧符石
StoneOfFear stone = new StoneOfFear();
stone.quantity = 1;

// 投掷到敌人位置
stone.doThrow(hero, enemyCell);

// 敌人会陷入恐惧并逃离
```

### 战术应用
```java
// 用于让敌人暂时远离
// 可创造逃跑或治疗的机会
// 注意：攻击会减少恐惧持续时间
```

## 12. 开发注意事项

### 状态依赖
- 恐惧效果依赖 Terror Buff 的实现
- Terror.object 设置为当前使用者 ID

### 常见陷阱
- 对盟友无效（这是设计如此）
- 攻击恐惧中的敌人会缩短效果

## 13. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述（无）
- [x] 示例代码是否真实可用

---

## 附：类关系图

```mermaid
classDiagram
    class Runestone {
        <<abstract>>
        #activate(int cell)*
    }
    
    class StoneOfFear {
        +activate(int cell)
    }
    
    Runestone <|-- StoneOfFear
```