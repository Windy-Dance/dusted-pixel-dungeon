# Noisemaker 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items.bombs/Noisemaker.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.bombs |
| 类类型 | public class |
| 继承关系 | extends Bomb |
| 代码行数 | 164行 |

## 2. 类职责说明
噪音器是一种陷阱型炸弹，投掷后不会立即爆炸，而是等待有角色靠近时才会爆炸。如果没有人靠近，会每6回合发出警报声吸引敌人。爆炸范围为2格。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Bomb {
        <<abstract>>
        +int explosionRange()
        +Fuse createFuse()
    }
    
    class Noisemaker {
        +int explosionRange()
        +Fuse createFuse()
        +boolean doPickUp(Hero, int)
        +int value()
    }
    
    class NoisemakerFuse {
        -boolean triggered
        -int left
        +boolean act()
        +void trigger(Heap)
        +boolean freeze()
    }
    
    class Fuse {
        <<abstract>>
    }
    
    Bomb <|-- Noisemaker
    Fuse <|-- NoisemakerFuse
    Noisemaker +-- NoisemakerFuse
    
    note for Noisemaker "爆炸范围: 2格\n等待敌人靠近\n每6回合发出警报"
```

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| image | int | - | 物品图标（NOISEMAKER） |

## 7. 方法详解

### explosionRange()
**签名**: `int explosionRange()`
**功能**: 获取爆炸范围
**参数**: 无
**返回值**: int - 2格
**实现逻辑**:
- 返回2（第44行）

### createFuse()
**签名**: `Fuse createFuse()`
**功能**: 创建噪音器专用的引信
**参数**: 无
**返回值**: Fuse - NoisemakerFuse实例
**实现逻辑**:
- 返回新的NoisemakerFuse实例（第49行）

### doPickUp(Hero hero, int pos)
**签名**: `boolean doPickUp(Hero hero, int pos)`
**功能**: 尝试拾取噪音器
**参数**:
- hero: Hero - 拾取的英雄
- pos: int - 拾取位置
**返回值**: boolean - 是否成功
**实现逻辑**:
- 如果引信已触发，不允许拾取（第55-56行）
- 否则调用父类doPickUp方法（第58行）

### value()
**签名**: `int value()`
**功能**: 获取物品价值
**参数**: 无
**返回值**: int - 价值（60 * 数量）

## 内部类 NoisemakerFuse

继承自Fuse，表示噪音器的特殊引信，具有延迟触发机制。

### act()
**签名**: `boolean act()`
**功能**: 每帧执行的行为
**参数**: 无
**返回值**: boolean - 是否继续执行
**实现逻辑**:
1. 如果未触发，按普通引信工作（第71-72行）
2. 如果已触发（第73-117行）：
   - 如果引信被移除，立即爆炸（第79-81行）
   - 如果有角色靠近，立即爆炸（第83-92行）
   - 否则每6回合发出警报（第94-107行）

### trigger(Heap heap)
**签名**: `void trigger(Heap heap)`
**功能**: 触发爆炸机制
**参数**:
- heap: Heap - 炸弹所在的堆
**返回值**: void
**实现逻辑**:
1. 如果未触发，设置为触发状态（第124-126行）
2. 如果已触发，执行爆炸（第127行）

### freeze()
**签名**: `boolean freeze()`
**功能**: 冻结引信
**参数**: 无
**返回值**: boolean - 是否成功
**实现逻辑**:
- 未触发时可以冻结（第133-134行）
- 已触发后不能冻结（第136行）

## 噪音器效果

| 阶段 | 行为 |
|------|------|
| 初始 | 普通引信计时 |
| 触发后 | 等待角色靠近 |
| 警报 | 每6回合发出声音 |
| 爆炸 | 角色靠近时 |

## 11. 使用示例
```java
// 创建噪音器
Noisemaker noisemaker = new Noisemaker();

// 投掷噪音器
noisemaker.execute(hero, Bomb.AC_LIGHTTHROW);
// 2回合后引信触发
// 等待敌人靠近

// 角色靠近时爆炸
// 如果没人靠近，每6回合发出警报吸引敌人

// 合成配方
// 炸弹 + 愤怒卷轴 = 噪音器
// 成本: 1点炼金能量
```

## 注意事项
1. 触发后无法拾取
2. 触发后引信无法熄灭
3. 会发出声音吸引敌人
4. 敌人靠近时爆炸
5. 合成成本低

## 最佳实践
1. 作为陷阱使用
2. 吸引敌人到特定位置
3. 在探索时引开敌人
4. 配合其他陷阱使用
5. 低成本的有效工具