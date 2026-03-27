# LiquidMetal 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/LiquidMetal.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items |
| 类类型 | public class |
| 继承关系 | extends Item |
| 代码行数 | 245 行 |

## 2. 类职责说明
LiquidMetal（液态金属）用于修复投掷武器的耐久度。使用后选择投掷武器进行修复，修复量基于武器等级和金属数量。也可以通过炼金将投掷武器转化为液态金属。飞镖不适用。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Item {
        +image int
        +stackable boolean
        +defaultAction String
        +bones boolean
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
    }
    
    class LiquidMetal {
        +image LIQUID_METAL
        +stackable true
        +defaultAction AC_APPLY
        +bones true
        -AC_APPLY String
        -itemSelector ItemSelector
        +actions() ArrayList
        +execute() void
        +onThrow() void
        +isUpgradable() boolean
        +isIdentified() boolean
        +value() int
        +Recipe inner class
    }
    
    Item <|-- LiquidMetal
    LiquidMetal +-- Recipe
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| AC_APPLY | String | "APPLY" | 应用动作标识 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | 初始化块 | 精灵图为 LIQUID_METAL |
| stackable | boolean | 初始化块 | 可堆叠 true |
| defaultAction | String | 初始化块 | 默认动作 AC_APPLY |
| bones | boolean | 初始化块 | 可从骨头继承 true |

## 7. 方法详解

### actions
**签名**: `public ArrayList<String> actions(Hero hero)`
**功能**: 获取可用动作列表
**返回值**: ArrayList\<String\> - 包含应用动作

### execute
**签名**: `public void execute(Hero hero, String action)`
**功能**: 执行动作，打开投掷武器选择界面

### onThrow
**签名**: `protected void onThrow(int cell)`
**功能**: 投掷处理，投到井或深坑正常处理，否则洒落
**实现逻辑**:
```java
// 第83-98行：投掷处理
if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {
    super.onThrow(cell);
} else {
    Dungeon.level.pressCell(cell);
    if (Dungeon.level.heroFOV[cell]) {
        GLog.i(Messages.get(Potion.class, "shatter"));
        Sample.INSTANCE.play(Assets.Sounds.SHATTER);
        Splash.at(cell, 0xBFBFBF, 5);
    }
}
```

### isUpgradable / isIdentified / value
标准实现。

### itemSelector (内部)
**功能**: 选择投掷武器进行修复
**实现逻辑**:
```java
// 第115-189行：修复逻辑
// 计算最大使用量：5*(tier+1) * 1.35^level
// 计算每个金属恢复的耐久度
// 修复武器耐久度

// 特殊处理：武器数量低于默认数量时可增加数量
// 消耗金属并修复武器
```

## 内部类详解

### Recipe
**类型**: public static class extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe
**功能**: 炼金配方，将投掷武器转化为液态金属
**实现逻辑**:
```java
// 第191-243行：炼金配方
// 需要：已鉴定且未诅咒的投掷武器（非飞镖）
// 花费：3能量
// 产出量基于武器等级和数量
```

## 11. 使用示例
```java
// 创建液态金属
LiquidMetal metal = new LiquidMetal();
metal.quantity(10);

// 修复投掷武器
// 高等级武器需要更多金属
// 飞镖不能修复
```

## 注意事项
1. 只能修复投掷武器，飞镖除外
2. 武器等级越高需要越多金属
3. 可以通过炼金将武器转化为金属
4. 投掷到地面会洒落

## 最佳实践
1. 优先修复常用投掷武器
2. 低级武器可以转化为金属
3. 保持一定量的金属储备
4. 配合魔法箭袋使用更有效