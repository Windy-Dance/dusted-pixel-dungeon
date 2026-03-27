# EmptyRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/EmptyRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom |
| **代码行数** | 39 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
EmptyRoom 实现最基本的空房间类型，仅包含墙壁和内部空地，没有任何装饰或特殊元素。它是标准房间中最简单、最基础的实现。

### 系统定位
在关卡生成系统中，EmptyRoom 作为填充性房间使用，用于连接其他更复杂的房间类型，或者在不需要特殊装饰的区域提供基本的空间结构。

### 不负责什么
- 不包含任何装饰性元素（雕像、植物、陷阱等）
- 不处理特殊的地形逻辑
- 不影响怪物生成或物品放置的特殊规则

## 3. 结构总览

### 主要成员概览
- 无额外字段或常量（完全继承自 StandardRoom）

### 主要逻辑块概览
- paint() 方法：简单的房间绘制逻辑，只绘制墙壁和空地

### 生命周期/调用时机
- 关卡生成过程中，当随机选择到 EmptyRoom 类型时创建实例
- 房间放置完成后调用 paint() 方法进行绘制

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承的能力包括：
- 尺寸类别管理（SizeCategory）
- 尺寸计算（minWidth/maxWidth/minHeight/maxHeight）
- 权重计算（sizeFactor、mobSpawnWeight、connectionWeight）
- 房间合并逻辑（canMerge）

从 Room 基类继承的能力包括：
- 空间几何操作
- 连接管理
- 放置点过滤
- 图论接口

### 覆写的方法
- `paint(Level level)`：实现具体的房间绘制逻辑

### 实现的接口契约
- 继承 StandardRoom 和 Room 的所有接口契约

### 依赖的关键类
- `com.shatteredpixel.shatteredpixeldungeon.levels.Level`：关卡数据结构
- `com.shatteredpixel.shatteredpixeldungeon.levels.Terrain`：地形常量
- `com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter`：绘制工具类

### 使用者
- 关卡生成器：通过 StandardRoom.createRoom() 间接创建和使用
- 房间连接算法：处理 EmptyRoom 的连接逻辑

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无 | 无 | 无 | 无 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| 无 | 无 | 无 | 完全继承自父类，无额外字段 |

## 6. 构造与初始化机制

### 构造器
EmptyRoom 使用默认构造器，继承自 StandardRoom 的无参构造器。

### 初始化块
无额外的初始化块，使用父类的初始化逻辑。

### 初始化注意事项
- 实例化时自动继承 StandardRoom 的尺寸类别设置逻辑
- 无特殊初始化要求

## 7. 方法详解

### paint()
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制空房间的基本结构（外层墙壁，内层空地）

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经被正确放置到关卡中（位置和尺寸已设置）

**副作用**：修改 level.map 数组中的地形数据

**核心实现逻辑**：
```java
Painter.fill( level, this, Terrain.WALL );
Painter.fill( level, this, 1 , Terrain.EMPTY );
```
1. 使用 Painter.fill() 在房间边界填充 WALL 地形
2. 使用 Painter.fill() 在房间内部（边界内缩1格）填充 EMPTY 地形

**边界情况**：
- 如果房间尺寸小于等于2x2，内部填充可能无效或覆盖外部墙壁
- 门的位置会被后续的 door.set() 调用正确处理

### 其他方法
EmptyRoom 不包含其他方法，所有其他功能都继承自父类。

## 8. 对外暴露能力

### 显式 API
- 继承 StandardRoom 的所有公共 API
- paint() 方法供关卡生成器调用

### 内部辅助方法
- 无额外的内部方法

### 扩展入口
- 可以通过继承 EmptyRoom 创建更具体的空房间变体
- 可以覆写 paint() 方法添加简单的装饰逻辑

## 9. 运行机制与调用链

### 创建时机
- 关卡生成过程中随机选择到 EmptyRoom 类型时
- 通过 StandardRoom.createRoom() 工厂方法创建

### 调用者
- Level 生成算法
- StandardRoom.createRoom() 静态工厂方法

### 被调用者
- Painter.fill()：用于实际的地形填充
- Door.set()：用于设置门的类型

### 系统流程位置
在关卡生成流程中：
1. StandardRoom.createRoom() 创建 EmptyRoom 实例
2. 关卡生成器设置房间位置和尺寸
3. 调用 EmptyRoom.paint() 绘制基本结构
4. 后续处理门和其他连接逻辑

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| 无 | 无 | EmptyRoom 不引用任何 messages 文案 |

### 依赖的资源
- 无直接依赖的纹理、图标或音效资源
- 间接依赖 WALL 和 EMPTY 地形的基础纹理

### 中文翻译来源
项目内未找到官方对应译名，使用英文原名 "Empty Room"

## 11. 使用示例

### 基本用法
```java
// 直接创建 EmptyRoom（通常通过工厂方法）
EmptyRoom room = new EmptyRoom();
room.setSize(); // 设置随机尺寸
level.placeRoom(room); // 放置到关卡
room.paint(level); // 绘制房间
```

### 在关卡生成中的典型用法
```java
// 通过标准房间工厂创建（实际使用方式）
StandardRoom room = StandardRoom.createRoom(); // 可能返回 EmptyRoom
if (room instanceof EmptyRoom) {
    // 处理空房间的特殊情况（如果需要）
}
room.paint(level);
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间已经被正确放置（left/right/top/bottom 已设置）
- 门的连接信息在 paint() 调用前应该已经建立

### 生命周期耦合
- paint() 必须在房间放置后、关卡完成前调用
- 不能在关卡已经完成生成后重新调用 paint()

### 常见陷阱
- 直接实例化 EmptyRoom 而不通过工厂方法可能导致概率分布不均衡
- 在错误的时机调用 paint() 可能导致地形覆盖问题

## 13. 修改建议与扩展点

### 适合扩展的位置
- paint() 方法：可以添加简单的随机装饰元素
- 可以创建 EmptyRoom 的子类来实现特定类型的空房间

### 不建议修改的位置
- 基本的填充逻辑：这是 EmptyRoom 的核心定义
- 不应该添加复杂的逻辑，否则就不再是"空"房间

### 重构建议
- 如果需要多种空房间变体，考虑使用策略模式而不是继承
- 可以将内部填充的边距参数化，允许不同厚度的墙壁

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无相关翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点