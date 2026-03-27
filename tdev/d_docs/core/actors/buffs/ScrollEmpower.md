# ScrollEmpower 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/ScrollEmpower.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.buffs |
| 类类型 | class |
| 继承关系 | extends Buff |
| 代码行数 | 94 |

## 2. 类职责说明
ScrollEmpower（卷轴强化）是一个正面Buff，使下一次使用的卷轴效果增强。使用卷轴会消耗一次强化次数。主要用于卷轴强化效果、特定技能效果等场景。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Buff {
        <<abstract>>
        +type: buffType
        +detach(): void
    }
    class ScrollEmpower {
        -left: int
        +reset(int): void
        +use(): void
        +icon(): int
        +tintIcon(Image): void
        +iconFadePercent(): float
        +iconTextDisplay(): String
        +desc(): String
    }
    Buff <|-- ScrollEmpower
    ScrollEmpower --> Item : 更新快捷栏
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| LEFT | String | "left" | Bundle存储键 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| left | int | private | 剩余使用次数 |
| type | buffType | - | POSITIVE（正面Buff） |

## 7. 方法详解

### reset(int left)
**签名**: `public void reset(int left)`
**功能**: 设置使用次数。
**参数**:
- left: int - 使用次数
**实现逻辑**:
```java
this.left = Math.max(this.left, left);  // 取较大值
Item.updateQuickslot();  // 更新快捷栏显示
```

### use()
**签名**: `public void use()`
**功能**: 使用一次强化效果。
**实现逻辑**:
```java
left--;
if (left <= 0) {
    detach();  // 次数用尽则移除
}
```

### detach()
**签名**: `public void detach()`
**功能**: 重写移除方法，更新快捷栏。
**实现逻辑**:
```java
super.detach();
Item.updateQuickslot();  // 更新快捷栏显示
```

### icon()
**签名**: `public int icon()`
**功能**: 返回Buff图标的索引标识符。
**返回值**: int - 返回BuffIndicator.WAND（法杖图标，复用）。

### tintIcon(Image icon)
**签名**: `public void tintIcon(Image icon)`
**功能**: 为Buff图标设置颜色色调。
**参数**:
- icon: Image - 需要着色的图标图像
**实现逻辑**:
```java
icon.hardlight(0.84f, 0.79f, 0.65f);  // 设置卷轴颜色
```

### iconFadePercent()
**签名**: `public float iconFadePercent()`
**功能**: 计算Buff图标的淡出百分比。
**返回值**: float - 图标完整度比例（基于剩余次数）。
**实现逻辑**:
```java
return Math.max(0, (3f - left) / 3f);
```

### iconTextDisplay()
**签名**: `public String iconTextDisplay()`
**功能**: 返回图标上显示的文本（剩余次数）。
**返回值**: String - 剩余次数的字符串表示。

### desc()
**签名**: `public String desc()`
**功能**: 返回Buff的详细描述文本。
**返回值**: String - 包含剩余次数的描述。

## 11. 使用示例
```java
// 添加卷轴强化，3次使用
ScrollEmpower empower = Buff.affect(hero, ScrollEmpower.class);
empower.reset(3);

// 使用卷轴时消耗
if (hero.buff(ScrollEmpower.class) != null) {
    hero.buff(ScrollEmpower.class).use();
    // 卷轴效果增强
}
```

## 注意事项
1. 每次使用卷轴消耗一次
2. 次数用尽后Buff移除
3. 对所有卷轴有效
4. 添加和移除都会更新快捷栏
5. 是正面Buff

## 最佳实践
1. 在关键时刻使用强化卷轴
2. 配合强力卷轴效果更佳
3. 注意剩余使用次数
4. 叠加使用次数可延长效果