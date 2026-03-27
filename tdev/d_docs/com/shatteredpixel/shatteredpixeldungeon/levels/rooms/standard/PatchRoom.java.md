# PatchRoom 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/rooms/standard/PatchRoom.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard |
| **文件类型** | abstract class |
| **继承关系** | extends StandardRoom |
| **代码行数** | 139 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
PatchRoom 类是一个抽象基类，为使用补丁系统(Patch system)填充房间内部的房间类型提供通用功能。它负责生成和管理不规则的补丁区域，并提供确保路径连通性和清理边缘的工具方法。

### 系统定位
作为标准房间的抽象基类，PatchRoom 在地牢关卡生成过程中被用作多种具体房间类型的父类（如 CaveRoom、ChasmRoom、CircleBasinRoom 等），为这些房间提供统一的补丁生成机制。

### 不负责什么
- 不负责具体的地形类型设置（由子类实现）
- 不负责关卡的整体布局规划（由 Level 和 Room 相关类处理）
- 不负责具体的绘制逻辑（由子类的 paint() 方法实现）

## 3. 结构总览

### 主要成员概览
- boolean[] patch: 补丁区域的布尔数组

### 主要逻辑块概览
- 抽象配置方法（fill, clustering, ensurePath, cleanEdges）
- 补丁设置方法（setupPatch）
- 补丁填充方法（fillPatch）
- 边缘清理方法（cleanDiagonalEdges）
- 坐标转换方法（xyToPatchCoords）

### 生命周期/调用时机
- 在具体子类实例化时作为基类初始化
- setupPatch() 方法在子类的 paint() 方法中调用以生成补丁
- fillPatch() 方法在子类的 paint() 方法中调用以应用补丁到关卡

## 4. 继承与协作关系

### 父类提供的能力
- 继承了 StandardRoom 的所有公共和受保护方法
- 继承了 Room 基类的基础功能

### 覆写的方法
- 无（作为抽象基类，主要提供新方法）

### 实现的接口契约
- 定义了四个抽象方法供子类实现：
  - fill(): 返回补丁填充密度
  - clustering(): 返回聚类程度参数
  - ensurePath(): 是否确保路径连通性
  - cleanEdges(): 是否清理对角线边缘

### 依赖的关键类
- Level: 关卡数据结构
- Patch: 补丁生成工具类
- BArray: 布尔数组工具类
- PathFinder: 路径查找工具类

### 使用者
- CaveRoom、ChasmRoom、CircleBasinRoom 等具体房间类型
- LevelGenerator: 间接通过具体子类使用

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| patch | boolean[] | null | 表示房间内部补丁区域的二维布尔数组（展平为一维） |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，无自定义构造逻辑。

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
- patch 字段初始为 null
- 必须在子类中调用 setupPatch() 方法来初始化 patch 数组
- 该类不能直接实例化，必须通过具体子类使用

## 7. 方法详解

### fill()

**可见性**：protected abstract

**是否覆写**：否（抽象方法）

**方法职责**：返回补丁区域的填充密度

**参数**：无

**返回值**：float，填充密度（0.0-1.0之间）

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，决定补丁区域占房间内部总面积的比例。

**边界情况**：无

### clustering()

**可见性**：protected abstract

**是否覆写**：否（抽象方法）

**方法职责**：返回补丁区域的聚类程度参数

**参数**：无

**返回值**：int，聚类参数值

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，控制补丁区域的分布模式（高值表示更集中的补丁，低值表示更分散的补丁）。

**边界情况**：无

### ensurePath()

**可见性**：protected abstract

**是否覆写**：否（抽象方法）

**方法职责**：确定是否确保房间内有连续的可行走路径

**参数**：无

**返回值**：boolean，是否确保路径连通

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，如果返回 true，则 setupPatch() 会确保所有非补丁区域都是连通的。

**边界情况**：无

### cleanEdges()

**可见性**：protected abstract

**是否覆写**：否（抽象方法）

**方法职责**：确定是否清理补丁区域的对角线边缘

**参数**：无

**返回值**：boolean，是否清理边缘

**前置条件**：无

**副作用**：无

**核心实现逻辑**：
由子类实现，如果返回 true，则 setupPatch() 会调用 cleanDiagonalEdges() 清理对角线相邻的补丁。

**边界情况**：无

### setupPatch()

**可见性**：protected

**是否覆写**：否

**方法职责**：生成并初始化补丁区域

**参数**：
- `level` (Level)：关卡对象

**返回值**：void

**前置条件**：房间边界已设置

**副作用**：
- 初始化 patch 数组
- 可能修改 PathFinder 的地图尺寸设置

**核心实现逻辑**：
1. 如果 ensurePath() 返回 true：
   - 设置 PathFinder 地图尺寸为房间内部尺寸
   - 循环直到生成有效的连通补丁：
     - 调用 Patch.generate() 生成补丁
     - 确保门口附近区域不被补丁覆盖
     - 使用 PathFinder.buildDistanceMap() 检查连通性
     - 如果不连通且尝试次数超过100次，则降低填充密度重新尝试
   - 恢复 PathFinder 地图尺寸
2. 否则直接生成补丁而不检查连通性
3. 如果 cleanEdges() 返回 true，则调用 cleanDiagonalEdges() 清理对角线边缘

**边界情况**：
- 当填充密度过高时，可能需要多次尝试才能生成连通的补丁
- 门口附近的补丁会被强制清除以确保可访问性

### fillPatch()

**可见性**：protected

**是否覆写**：否

**方法职责**：将补丁区域应用到关卡地图上

**参数**：
- `level` (Level)：关卡对象
- `terrain` (int)：要应用的地形类型

**返回值**：void

**前置条件**：patch 数组已初始化

**副作用**：
- 修改 level.map 数组中的地形数据

**核心实现逻辑**：
遍历房间内部的每个位置，如果对应位置在 patch 数组中标记为 true，则将该位置的地形设置为指定的 terrain 类型。

**边界情况**：无

### cleanDiagonalEdges()

**可见性**：protected

**是否覆写**：否

**方法职责**：清理对角线相邻的补丁区域

**参数**：无

**返回值**：void

**前置条件**：patch 数组已初始化

**副作用**：
- 修改 patch 数组

**核心实现逻辑**：
遍历 patch 数组，移除所有仅通过对角线相邻的补丁区域，使补丁区域的边缘更加平滑和自然。

**边界情况**：
- 此操作会略微降低实际的填充率
- 只处理向下-向左和向下-向右的对角线情况，避免重复处理

### xyToPatchCoords()

**可见性**：protected

**是否覆写**：否

**方法职责**：将绝对坐标转换为补丁数组的一维索引

**参数**：
- `x` (int)：绝对x坐标
- `y` (int)：绝对y坐标

**返回值**：int，patch数组中的一维索引

**前置条件**：坐标在房间内部

**副作用**：无

**核心实现逻辑**：
将绝对坐标减去房间左上角偏移（并额外减1以排除边缘），然后转换为一维数组索引。

**边界情况**：当坐标超出房间范围时会产生无效索引

## 8. 对外暴露能力

### 显式 API
- 无公共方法（作为抽象基类，主要通过子类暴露功能）

### 内部辅助方法
- setupPatch(), fillPatch(), cleanDiagonalEdges(), xyToPatchCoords() 是受保护的内部方法

### 扩展入口
- 子类必须实现四个抽象配置方法
- 子类可以在 paint() 方法中调用提供的工具方法

## 9. 运行机制与调用链

### 创建时机
- 作为具体子类的一部分在 LevelGenerator 选择房间类型时创建

### 调用者
- 具体子类（如 CaveRoom.paint()）调用 setupPatch() 和 fillPatch()

### 被调用者
- Patch.generate(): 生成补丁区域
- PathFinder.setMapSize()/buildDistanceMap(): 检查路径连通性
- BArray.not(): 反转布尔数组用于路径查找

### 系统流程位置
- 位于关卡生成流程的房间绘制阶段

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用的消息键。PatchRoom 作为抽象基类不直接使用任何消息资源。

### 依赖的资源
- 无直接依赖的资源（具体的地形资源由子类决定）

### 中文翻译来源
项目内未找到官方对应译名，保留英文名称"PatchRoom"。

## 11. 使用示例

### 基本用法
```java
// 在具体子类中实现抽象方法
public class MyPatchRoom extends PatchRoom {
    @Override
    protected float fill() {
        return 0.5f;
    }
    
    @Override
    protected int clustering() {
        return 3;
    }
    
    @Override
    protected boolean ensurePath() {
        return true;
    }
    
    @Override
    protected boolean cleanEdges() {
        return true;
    }
    
    @Override
    public void paint(Level level) {
        // 基础地形绘制
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        
        // 生成和应用补丁
        setupPatch(level);
        fillPatch(level, Terrain.WATER);
    }
}
```

### 扩展示例
不适用，该类作为抽象基类主要用于被继承。

## 12. 开发注意事项

### 状态依赖
- 依赖于房间边界已正确设置
- 依赖于关卡的 map 数组已初始化
- patch 数组必须在 fillPatch() 调用前通过 setupPatch() 初始化

### 生命周期耦合
- setupPatch() 必须在 fillPatch() 之前调用
- PathFinder 的地图尺寸会在 setupPatch() 中被临时修改，必须在结束时恢复

### 常见陷阱
- 忘记实现四个抽象方法会导致编译错误
- 在没有调用 setupPatch() 的情况下调用 fillPatch() 会导致 NullPointerException
- 路径连通性检查可能会在高填充密度下导致性能问题（最多100次重试）

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加新的配置选项来控制补丁生成的其他方面
- cleanDiagonalEdges() 方法可以扩展为支持更多类型的边缘清理

### 不建议修改的位置
- 四个抽象方法的签名不应更改，这会影响所有子类
- 坐标转换逻辑不应修改，这会影响补丁的正确应用

### 重构建议
- 可以考虑将 PathFinder 相关的逻辑提取到单独的工具类中
- 当前实现已经很好地平衡了功能性和简洁性，无需重大重构

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点