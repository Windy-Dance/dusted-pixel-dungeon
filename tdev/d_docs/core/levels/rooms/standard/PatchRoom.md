# PatchRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\levels\rooms\standard\PatchRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | abstract class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 139 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
PatchRoom 是一个抽象基类，为使用补丁系统（patch system）的房间提供通用功能。它集成了补丁生成算法，允许子类通过简单的配置参数来创建具有随机分布区域的房间。补丁系统用于生成各种主题的房间内容，如烧焦区域、洞穴岩石、深渊等。

### 系统定位
作为 StandardRoom 和具体补丁房间实现之间的中间层，PatchRoom 提供了补丁生成的框架和工具方法，简化了具有随机分布内容的房间的开发。

### 不负责什么
- 不负责补丁生成算法的具体实现（由 Patch 类处理）
- 不负责具体的房间绘制逻辑（由子类实现 paint 方法）
- 不管理补丁区域的具体内容（由子类决定填充什么地形）

## 3. 结构总览

### 主要成员概览
- `patch` 字段：布尔数组，表示补丁区域的分布
- 抽象方法：`fill()`, `clustering()`, `ensurePath()`, `cleanEdges()`
- 工具方法：`setupPatch()`, `fillPatch()`, `cleanDiagonalEdges()`, `xyToPatchCoords()`

### 主要逻辑块概览
- 补丁生成配置（抽象方法供子类实现）
- 补丁初始化逻辑（setupPatch）
- 地形填充辅助（fillPatch）
- 边缘清理逻辑（cleanDiagonalEdges）
- 坐标转换工具（xyToPatchCoords）

### 生命周期/调用时机
- 在地牢生成过程中，具体子类被创建时继承此功能
- setupPatch() 在子类的 paint() 方法中被调用以生成补丁数据
- fillPatch() 在子类的 paint() 方法中被调用以填充地形

## 4. 继承与协作关系

### 父类提供的能力
从 StandardRoom 继承：
- 尺寸类别系统（SizeCategory）
- 权重计算方法
- 基础的房间尺寸和连接逻辑
From Room 继承：
- 空间几何操作
- 邻居和连接管理
- 随机点生成
- 抽象的 paint() 方法

### 覆写的方法
- 此类没有覆写父类的方法，而是添加了新的抽象方法和具体实现

### 实现的接口契约
- Graph.Node 接口（通过 Room 继承）
- Bundlable 接口（通过 Room 继承）

### 依赖的关键类
- `Level` - 地牢关卡对象
- `Patch` - 补丁生成算法
- `PathFinder` - 路径查找，用于确保连通性
- `BArray` - 布尔数组操作工具
- `Point` - 点坐标操作

### 使用者
- 各种具体的补丁房间实现（如 BurnedRoom, CaveRoom, ChasmRoom 等）
- 子类在 paint() 方法中调用 setupPatch() 和 fillPatch()

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| patch | boolean[] | null | 表示补丁区域分布的布尔数组，true 表示该位置属于补丁区域 |

## 6. 构造与初始化机制

### 构造器
使用默认的无参构造器，继承自 StandardRoom。

### 初始化块
继承了 StandardRoom 的 `{ setSizeCat(); }` 初始化块。

### 初始化注意事项
- patch 字段初始为 null，在 setupPatch() 被调用后才被初始化
- 由于是抽象类，不能直接实例化，必须通过具体子类使用

## 7. 方法详解

### fill()

**可见性**：protected abstract

**是否覆写**：否

**方法职责**：返回补丁的填充率（0-1之间的值）

**参数**：无

**返回值**：float，填充率比例

**前置条件**：房间的宽度和高度必须已确定

**副作用**：无

**核心实现逻辑**：
抽象方法，由子类实现。通常基于房间尺寸动态计算或返回固定值。

**边界情况**：返回值应在 0-1 范围内，超出范围可能导致补丁生成异常

### clustering()

**可见性**：protected abstract

**是否覆写**：否

**方法职责**：返回补丁的聚类程度

**参数**：无

**返回值**：int，聚类值（值越大聚类越强）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
抽象 method，由子类实现。控制补丁区域的聚集程度。

**边界情况**：通常使用较小的整数值（1-3）

### ensurePath()

**可见性**：protected abstract

**是否覆写**：否

**方法职责**：指定是否确保补丁区域路径连通

**参数**：无

**返回值**：boolean，true 表示确保路径连通

**前置条件**：connected 集合应该已正确设置（如果需要）

**副作用**：无

**核心实现逻辑**：
抽象 method，由子类实现。决定是否使用 PathFinder 确保所有非补丁区域都是可到达的。

**边界情况**：孤立房间可能不需要路径连通性

### cleanEdges()

**可见性**：protected abstract

**是否覆写**：否

**方法职责**：指定是否清理补丁的对角线边缘

**参数**：无

**返回值**：boolean，true 表示清理对角线边缘

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
抽象 method，由子类实现。控制是否移除仅对角线相邻的补丁区域，使边缘更平滑。

**边界情况**：清理边缘会略微降低填充率

### setupPatch(Level level)

**可见性**：protected

**是否覆写**：否

**方法职责**：生成补丁数据并初始化 patch 字段

**参数**：
- `level` (Level)：当前关卡

**返回值**：void

**前置条件**：房间的位置和尺寸必须已确定，connected 集合应该已正确设置

**副作用**：
- 初始化 patch 字段
- 可能多次重试以确保路径连通性（如果 ensurePath() 返回 true）

**核心实现逻辑**：
1. 如果 ensurePath() 返回 true：
   - 设置 PathFinder 地图尺寸
   - 循环生成补丁直到找到有效布局：
     - 调用 Patch.generate() 生成补丁
     - 标记连接门附近的区域为非补丁（确保通道）
     - 使用 PathFinder 验证所有非补丁区域的连通性
     - 如果验证失败且尝试次数过多，降低填充率重新尝试
   - 恢复 PathFinder 地图尺寸
2. 否则：
   - 直接调用 Patch.generate() 生成补丁
3. 如果 cleanEdges() 返回 true，调用 cleanDiagonalEdges() 清理边缘

**边界情况**：
- 路径验证循环有最大尝试次数限制（100次）
- 填充率会逐步降低以确保生成有效布局
- 连接门附近的区域被强制设为非补丁以确保通道

### fillPatch(Level level, int terrain)

**可见性**：protected

**是否覆写**：否

**方法职责**：将补丁区域填充为指定的地形类型

**参数**：
- `level` (Level)：当前关卡
- `terrain` (int)：要填充的地形类型

**返回值**：void

**前置条件**：patch 字段必须已通过 setupPatch() 初始化

**副作用**：
- 修改 level.map 数组中的地形

**核心实现逻辑**：
遍历房间内部每个格子，如果是补丁区域（patch[xyToPatchCoords(j, i)] 为 true），则将对应单元格设置为指定地形。

**边界情况**：坐标转换确保在有效范围内

### cleanDiagonalEdges()

**可见性**：protected

**是否覆写**：否

**方法职责**：移除仅对角线相邻的补丁区域

**参数**：无

**返回值**：void

**前置条件**：patch 字段必须已初始化

**副作用**：
- 修改 patch 数组，移除对角线边缘

**核心实现逻辑**：
遍历补丁数组，检查每个已填充的格子：
- 如果其右下对角线邻居已填充，但右侧和下方邻居都未填充，则移除对角线邻居
- 如果其左下对角线邻居已填充，但左侧和下方邻居都未填充，则移除对角线邻居

**边界情况**：
- 从上到下、从左到右遍历，避免重复处理
- 仅处理内部格子，边界格子不参与对角线检查

### xyToPatchCoords(int x, int y)

**可见性**：protected

**是否覆写**：否

**方法职责**：将绝对坐标转换为补丁数组的一维索引

**参数**：
- `x` (int)：绝对x坐标
- `y` (int)：绝对y坐标

**返回值**：int，补丁数组的一维索引

**前置条件**：坐标必须在房间内部（left+1 到 right-1，top+1 到 bottom-1）

**副作用**：无

**核心实现逻辑**：
计算 `(x-left-1) + ((y-top-1) * (width()-2))`，将二维坐标映射到一维数组索引。

**边界情况**：坐标超出房间内部范围会导致负索引或数组越界

## 8. 对外暴露能力

### 显式 API
- 所有 protected 方法都是为子类提供的API
- fillPatch() 是主要的公共工具方法

### 内部辅助方法
- setupPatch() 是主要的初始化方法
- cleanDiagonalEdges() 和 xyToPatchCoords() 是辅助工具

### 扩展入口
- 四个抽象方法（fill, clustering, ensurePath, cleanEdges）是主要的扩展点
- 子类通过实现这些方法来自定义补丁行为

## 9. 运行机制与调用链

### 创建时机
- 通过具体子类的实例化间接使用

### 调用者
- 具体的 PatchRoom 子类在 paint() 方法中调用 setupPatch() 和 fillPatch()

### 被调用者
- 调用 Patch.generate() 生成补丁数据
- 调用 PathFinder 进行路径验证（如果需要）
- 调用父类 StandardRoom 和 Room 的方法

### 系统流程位置
- 处于地牢生成的房间绘制阶段
- 在房间连接完成后、内容填充前执行

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。

### 依赖的资源
- 无直接资源依赖，但子类会使用各种地形资源

### 中文翻译来源
项目内未找到官方对应译名。"PatchRoom" 直译为"补丁房间"，但由于 levels_zh.properties 文件中没有对应的官方翻译，保留英文名称。

## 11. 使用示例

### 基本用法
```java
// 具体子类实现示例
public class ExampleRoom extends PatchRoom {
    @Override
    protected float fill() {
        return 0.5f; // 50% 填充率
    }
    
    @Override
    protected int clustering() {
        return 2; // 中等聚类
    }
    
    @Override
    protected boolean ensurePath() {
        return true; // 确保路径连通
    }
    
    @Override
    protected boolean cleanEdges() {
        return true; // 清理边缘
    }
    
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        
        setupPatch(level); // 生成补丁
        fillPatch(level, Terrain.WATER); // 填充水域
        
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- patch 字段必须在 fillPatch() 之前通过 setupPatch() 初始化
- ensurePath() 依赖于 connected 集合的正确设置
- 坐标转换方法依赖于房间的正确位置和尺寸

### 生命周期耦合
- 必须在房间连接完成后调用 setupPatch()
- fillPatch() 必须在 setupPatch() 之后调用
- PathFinder 的地图尺寸必须在 setupPatch() 前后正确设置和恢复

### 常见陷阱
- 忘记调用 setupPatch() 直接调用 fillPatch() 会导致 NullPointerException
- ensurePath() 在 connected 未正确设置时可能产生意外结果
- 填充率过高可能导致路径验证失败，需要合理设置
- 坐标转换超出范围会导致数组越界

## 13. 修改建议与扩展点

### 适合扩展的位置
- 四个抽象方法是主要的配置扩展点
- 可以添加新的工具方法来支持不同的补丁模式

### 不建议修改的位置
- setupPatch() 的核心逻辑经过充分测试，不应随意修改
- 坐标转换公式是关键的数学计算，修改会影响所有子类
- 路径验证逻辑确保游戏可玩性，不应简化

### 重构建议
- 可以考虑将补丁生成参数封装到配置对象中
- 路径验证逻辑可以提取为独立的工具类以提高复用性
- 坐标转换方法可以添加边界检查以提高安全性

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（确认无官方翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点