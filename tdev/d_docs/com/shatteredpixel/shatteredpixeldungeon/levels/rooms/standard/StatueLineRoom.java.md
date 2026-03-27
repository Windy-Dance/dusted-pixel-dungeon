# StatueLineRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/StatueLineRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | class |
| **继承关系** | extends com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom |
| **代码行数** | 109 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
StatueLineRoom 实现一个装饰性房间，在房间的一面墙上放置一排雕像（STATUE 地形）。它会智能选择使用最少门的墙面来放置装饰，确保装饰不会与门口冲突。

### 系统定位
在关卡生成系统中，StatueLineRoom 作为装饰性标准房间使用，为关卡添加视觉多样性。它属于图书馆/学术区域的装饰风格房间，通过雕像线来增加环境氛围。

### 不负责什么
- 不处理雕像的交互逻辑（雕像作为静态地形存在）
- 不影响游戏玩法机制（仅视觉装饰）
- 不包含复杂的房间布局（保持标准矩形结构）

## 3. 结构总览

### 主要成员概览
- 静态常量：N、E、S、W（方向枚举值）
- 无实例字段（完全继承自 StandardRoom）

### 主要逻辑块概览
- 尺寸限制逻辑（minWidth/minHeight）
- 墙面选择逻辑（基于门的位置计算偏好）
- 装饰绘制逻辑（fillAlongSide 方法）
- 可扩展的装饰地形方法（decoTerrain）

### 生命周期/调用时机
- 关卡生成过程中随机选择到 StatueLineRoom 类型时创建
- 房间放置完成后调用 paint() 方法进行装饰绘制

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承的能力包括：
- 尺寸类别管理
- 尺寸计算和权重管理
- 基础房间结构

从 Room 基类继承的能力包括：
- 空间几何操作
- 连接管理（doors 信息）
- 放置点过滤

### 覆写的方法
- `minWidth()`：最小宽度限制为 5
- `minHeight()`：最小高度限制为 5  
- `paint(Level level)`：实现雕像线装饰逻辑

### 实现的接口契约
- 继承 StandardRoom 和 Room 的所有接口契约
- 实现 Bundlable 接口用于存档

### 依赖的关键类
- `com.shatteredpixel.shatteredpixeldungeon.levels.Level`：关卡数据结构
- `com.shatteredpixel.shatteredpixeldungeon.levels.Terrain`：地形常量（STATUE、WALL、EMPTY）
- `com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter`：绘制工具类
- `com.watabou.utils.Point`：坐标操作
- `com.watabou.utils.Random`：随机选择

### 使用者
- 关卡生成器：通过 StandardRoom.createRoom() 创建
- 房间连接算法：处理 StatueLineRoom 的连接逻辑

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| N | Integer | 0 | 北墙（顶部）方向标识 |
| E | Integer | 1 | 东墙（右侧）方向标识 |
| S | Integer | 2 | 南墙（底部）方向标识 |
| W | Integer | 3 | 西墙（左侧）方向标识 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| 无 | 无 | 无 | 完全继承自父类，无额外字段 |

## 6. 构造与初始化机制

### 构造器
StatueLineRoom 使用默认构造器，继承自 StandardRoom 的无参构造器。

### 初始化块
无额外的初始化块，使用父类的初始化逻辑。

### 初始化注意事项
- 实例化时自动继承 StandardRoom 的尺寸类别设置逻辑
- 最小尺寸被覆写为 5x5，确保有足够的空间放置雕像线

## 7. 方法详解

### minWidth()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小宽度，确保至少为 5

**参数**：无

**返回值**：int，Math.max(5, super.minWidth())

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(5, super.minWidth());
```
确保房间宽度至少为 5，以便有足够空间放置雕像线。

**边界情况**：如果父类的 minWidth() 返回值大于 5，则使用父类的值

### minHeight()
**可见性**：public

**是否覆写**：是，覆写自 StandardRoom

**方法职责**：返回房间的最小高度，确保至少为 5

**参数**：无

**返回值**：int，Math.max(5, super.minHeight())

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Math.max(5, super.minHeight());
```
确保房间高度至少为 5，以便有足够空间放置雕像线。

**边界情况**：如果父类的 minHeight() 返回值大于 5，则使用父类的值

### paint()
**可见性**：public

**是否覆写**：是，覆写自 Room

**方法职责**：绘制房间基础结构并添加雕像线装饰

**参数**：
- `level` (Level)：要绘制到的关卡实例

**返回值**：void

**前置条件**：房间必须已经被正确放置到关卡中

**副作用**：修改 level.map 数组中的地形数据

**核心实现逻辑**：
1. 绘制基础房间结构（墙壁和空地）
2. 计算各墙面的门位置偏好分数
3. 根据偏好分数随机选择最佳墙面
4. 在选中的墙面上绘制雕像线
5. 确保门的位置正确设置为空地

**边界情况**：
- 如果所有墙面都有门，会选择门最少的墙面
- 如果多个墙面有相同偏好分数，随机选择其中一个

### decoTerrain()
**可见性**：protected

**是否覆写**：否

**方法职责**：返回用于装饰的地形类型

**参数**：无

**返回值**：int，Terrain.STATUE

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
```java
return Terrain.STATUE;
```
返回 STATUE 地形常量。

**边界情况**：子类可以覆写此方法使用不同的装饰地形

### fillAlongSide()
**可见性**：private

**是否覆写**：否

**方法职责**：在指定墙面绘制装饰线

**参数**：
- `side` (Integer)：墙面方向（N=0, E=1, S=2, W=3）
- `level` (Level)：关卡实例

**返回值**：void

**前置条件**：side 必须是有效的方向值（0-3）

**副作用**：修改 level.map 数组中的地形数据

**核心实现逻辑**：
使用 Painter.drawLine() 在指定墙面的内侧一格位置绘制装饰线：
- N: top+1 行，left+1 到 right-1 列
- E: right-1 列，top+1 到 bottom-1 行  
- S: bottom-1 行，left+1 到 right-1 列
- W: left+1 列，top+1 到 bottom-1 行

**边界情况**：如果房间尺寸过小，可能无法正确绘制线段

## 8. 对外暴露能力

### 显式 API
- 继承 StandardRoom 的所有公共 API
- paint() 方法供关卡生成器调用

### 内部辅助方法
- decoTerrain()：供子类覆写以改变装饰类型
- fillAlongSide()：内部绘制逻辑

### 扩展入口
- decoTerrain() 方法：子类可以覆写以使用不同的装饰地形
- 可以通过继承创建不同装饰风格的房间变体

## 9. 运行机制与调用链

### 创建时机
- 关卡生成过程中随机选择到 StatueLineRoom 类型时
- 通过 StandardRoom.createRoom() 工厂方法创建

### 调用者
- Level 生成算法
- StandardRoom.createRoom() 静态工厂方法

### 被调用者
- Painter.fill()：绘制基础房间结构
- Painter.drawLine()：绘制装饰线
- Random.chances()：选择墙面方向
- Door.set()：设置门类型

### 系统流程位置
在关卡生成流程中：
1. StandardRoom.createRoom() 创建 StatueLineRoom 实例
2. 关卡生成器设置房间位置和尺寸（确保 >= 5x5）
3. 调用 StatueLineRoom.paint() 绘制基础结构和装饰
4. 后续处理门和其他连接逻辑

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| 无 | 无 | StatueLineRoom 不直接引用任何 messages 文案 |

### 依赖的资源
- STATUE 地形的基础纹理资源
- WALL 和 EMPTY 地形的基础纹理

### 中文翻译来源
项目内未找到官方对应译名，使用英文原名 "Statue Line Room"

## 11. 使用示例

### 基本用法
```java
// 直接创建 StatueLineRoom（通常通过工厂方法）
StatueLineRoom room = new StatueLineRoom();
room.setSize(); // 设置随机尺寸（至少 5x5）
level.placeRoom(room); // 放置到关卡
room.paint(level); // 绘制房间和雕像线
```

### 自定义装饰变体
```java
// 创建使用不同装饰地形的子类
public class SkullLineRoom extends StatueLineRoom {
    @Override
    protected int decoTerrain() {
        return Terrain.SKELETON; // 使用骷髅地形代替雕像
    }
}
```

## 12. 开发注意事项

### 状态依赖
- paint() 方法依赖于房间已经被正确放置
- 门的连接信息在 paint() 调用前应该已经建立
- 房间尺寸必须 >= 5x5 才能正常工作

### 生命周期耦合
- paint() 必须在房间放置后、关卡完成前调用
- 不能在关卡已经完成生成后重新调用 paint()

### 常见陷阱
- 如果房间尺寸小于 5x5，可能导致装饰绘制异常
- 直接修改 sidePreferences 数组可能影响随机选择的公平性
- 忘记调用 super.paint() 会导致基础房间结构缺失

## 13. 修改建议与扩展点

### 适合扩展的位置
- decoTerrain() 方法：改变装饰地形类型
- fillAlongSide() 方法：改变装饰的绘制位置或模式
- paint() 方法：添加额外的装饰逻辑

### 不建议修改的位置
- 基础的墙面选择逻辑：这是 StatueLineRoom 的核心智能特性
- 尺寸限制逻辑：确保房间有足够的装饰空间

### 重构建议
- 考虑将方向常量改为 enum 类型提高可读性
- 可以将装饰线的厚度参数化，支持多行装饰
- 装饰偏好计算逻辑可以提取为独立方法便于测试

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无相关翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点