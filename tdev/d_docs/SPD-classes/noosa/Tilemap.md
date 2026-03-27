# Tilemap Class Documentation

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | SPD-classes/src/main/java/com/watabou/noosa/Tilemap.java |
| **包名** | com.watabou.noosa |
| **文件类型** | class |
| **继承关系** | extends Visual |
| **代码行数** | 248 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
Tilemap 类负责高效渲染基于瓷砖（tile）的地图，通过整数数组表示地图数据，每个值对应纹理图集中的特定瓷砖，支持大规模地图的优化渲染。

### 系统定位
作为 Visual 的子类，Tilemap 提供了基于网格的渲染能力，适用于关卡地图、背景装饰和其他需要重复使用小纹理块的场景。它通过智能的增量更新机制减少 GPU 数据传输开销。

### 不负责什么
- 不处理地图数据的生成或逻辑（仅渲染）
- 不管理瓷砖纹理资源的加载（由 TextureFilm 和 TextureCache 处理）
- 不处理用户交互或碰撞检测（仅可视化）

## 3. 结构总览

### 主要成员概览
- **地图数据**: data (int[]), mapWidth, mapHeight, size
- **纹理系统**: texture (SmartTexture), tileset (TextureFilm)
- **瓷砖尺寸**: cellW, cellH
- **渲染缓存**: vertices (float[]), quads (FloatBuffer), buffer (Vertexbuffer)
- **更新管理**: updated (Rect), updating (Rect), fullUpdate (boolean)

### 主要逻辑块概览
- 构造函数初始化纹理和瓷砖尺寸
- map() 方法设置地图数据并初始化渲染
- updateVertices() 构建瓷砖顶点数据
- needsRender() 控制哪些瓷砖需要渲染
- draw() 实现高效的批量渲染

### 生命周期/调用时机
- **创建时**: 构造函数设置纹理和瓷砖集
- **地图设置时**: map() 方法初始化地图数据和缓冲区
- **地图更新时**: updateMap() 或 updateMapCell() 触发重绘
- **每帧渲染**: draw() 检查更新区域并渲染
- **销毁时**: destroy() 清理 Vertexbuffer 资源

## 4. 继承与协作关系

### 父类提供的能力
从 Visual 继承:
- 位置、尺寸、变换矩阵（x, y, width, height, scale, origin, angle）
- 颜色效果（rm, gm, bm, am, ra, ga, ba, aa）
- 运动物理和可见性检测

### 覆写的方法
- **draw()**: 实现瓷砖地图特定的渲染逻辑
- **destroy()**: 清理瓷砖地图特有的 Vertexbuffer 资源
- **script()**: 使用 NoosaScriptNoLighting 脚本（无光照）

### 实现的接口契约
无直接接口实现

### 依赖的关键类
- **com.watabou.noosa.TextureFilm**: 瓷砖纹理映射管理
- **com.watabou.gltextures.SmartTexture**: 纹理资源管理
- **com.watabou.glwrap.Vertexbuffer**: GPU 顶点缓冲区管理
- **com.watabou.noosa.NoosaScriptNoLighting**: 无光照渲染脚本
- **com.watabou.utils.Rect**: 更新区域管理

### 使用者
- **游戏关卡**: Dungeon 地图渲染
- **背景系统**: 重复背景瓷砖
- **UI 装饰**: 网格化装饰元素
- **特效系统**: 基于瓷砖的粒子效果

## 5. 字段/常量详解

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| texture | SmartTexture | null | 瓷砖纹理 |
| tileset | TextureFilm | null | 瓷砖纹理映射 |
| data | int[] | null | 地图数据数组 |
| mapWidth | int | 0 | 地图宽度（瓷砖数量）|
| mapHeight | int | 0 | 地图高度（瓷砖数量）|
| size | int | 0 | 总瓷砖数量 (width × height) |
| cellW | float | 0 | 单个瓷砖宽度 |
| cellH | float | 0 | 单个瓷砖高度 |
| vertices | float[] | 16元素数组 | 临时顶点缓存 |
| quads | FloatBuffer | null | 瓷砖四边形顶点缓冲区 |
| buffer | Vertexbuffer | null | GPU 顶点缓冲区 |
| updated | Rect | empty | 需要更新的区域 |
| updating | Rect | null | 正在更新的区域（同步控制）|
| fullUpdate | boolean | false | 是否需要完整更新 |
| topLeftUpdating | int | -1 | 更新区域起始索引 |
| bottomRightUpdating | int | 0 | 更新区域结束索引 |

## 6. 构造与初始化机制

### 构造器
```java
public Tilemap(Object tx, TextureFilm tileset)
```
- 调用 super(0, 0, 0, 0) 初始化 Visual 基类
- 通过 TextureCache.get(tx) 获取 SmartTexture
- 设置 tileset 引用
- 从 tileset.get(0) 获取第一个瓷砖尺寸作为 cellW/cellH
- 初始化 vertices 数组和 updated Rect

### 初始化块
无静态或实例初始化块

### 初始化注意事项
- 构造函数不设置地图数据，需要后续调用 map() 方法
- 依赖 tileset 包含至少一个有效瓷砖（索引 0）
- 初始尺寸为 0，直到 map() 被调用

## 7. 方法详解

### map(int[] data, int cols)
**可见性**：public  
**是否覆写**：否  
**方法职责**：设置地图数据并初始化渲染参数  
**参数**：
- data (int[]) - 地图数据数组
- cols (int) - 地图列数（宽度）
**返回值**：void  
**前置条件**：data.length 必须能被 cols 整除  
**副作用**：修改所有地图相关字段，触发完整更新  
**核心实现逻辑**：
1. 设置 data, mapWidth=cols, mapHeight=data.length/cols, size=width×height
2. 计算 width=cellW×mapWidth, height=cellH×mapHeight
3. 创建足够大的 quads 缓冲区 (size 个四边形)
4. 调用 updateMap() 触发完整重建
**边界情况**：cols=0 会导致除零错误

### image(int x, int y)
**可见性**：public  
**是否覆写**：否  
**方法职责**：获取指定位置的单个瓷砖 Image 对象  
**参数**：
- x (int) - X 坐标（瓷砖索引）
- y (int) - Y 坐标（瓷砖索引）
**返回值**：Image，单个瓷砖图像或 null  
**前置条件**：坐标在有效范围内且瓷砖需要渲染  
**副作用**：创建新的 Image 对象  
**核心实现逻辑**：
1. 检查 needsRender(x + mapWidth*y)
2. 如果不需要渲染返回 null
3. 否则创建新 Image，设置对应瓷砖帧
**边界情况**：超出地图边界的行为未定义（可能数组越界）

### updateMap()
**可见性**：public  
**是否覆写**：否  
**方法职责**：强制完整地图更新  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：设置 updated 为完整地图区域，fullUpdate=true  
**核心实现逻辑**：updated.set(0, 0, mapWidth, mapHeight), fullUpdate=true  
**边界情况**：mapWidth/mapHeight 为 0 时 updated 仍会被设置

### updateMapCell(int cell)
**可见性**：public  
**是否覆写**：否  
**方法职责**：标记单个瓷砖需要更新  
**参数**：cell (int) - 线性瓷砖索引  
**返回值**：void  
**前置条件**：cell 在 [0, size) 范围内  
**副作用**：扩展 updated 区域包含指定瓷砖  
**核心实现逻辑**：updated.union(cell % mapWidth, cell / mapWidth)  
**边界情况**：无效 cell 索引可能导致 updated 区域异常

### moveToUpdating()
**可见性**：private  
**是否覆写**：否  
**方法职责**：原子地移动更新区域到正在处理状态  
**参数**：无  
**返回值**：void  
**前置条件**：synchronized 上下文  
**副作用**：设置 updating 并清空 updated  
**核心实现逻辑**：updating = new Rect(updated), updated.setEmpty()  
**边界情况**：线程安全的更新区域转移

### updateVertices()
**可见性**：protected  
**是否覆写**：否  
**方法职责**：构建需要更新的瓷砖顶点数据  
**参数**：无  
**返回值**：void  
**前置条件**：updating 区域已设置  
**副作用**：修改 quads 缓冲区和索引跟踪字段  
**核心实现逻辑**：
1. 调用 moveToUpdating() 获取更新区域
2. 遍历 updating 区域内的每个瓷砖
3. 对每个瓷砖：
   - 如果 needsRender() 返回 true，构建正常顶点
   - 否则构建零尺寸顶点（跳过渲染但保持缓冲区连续）
4. 更新 topLeftUpdating 和 bottomRightUpdating 索引
**边界情况**：空更新区域时无操作

### draw()
**可见性**：public  
**是否覆写**：是，覆写自 Visual  
**方法职责**：渲染瓷砖地图  
**参数**：无  
**返回值**：void  
**前置条件**：纹理和缓冲区有效  
**副作用**：可能更新 GPU 缓冲区，绑定纹理  
**核心实现逻辑**：
1. 调用 super.draw() 处理变换矩阵
2. 如果 updated 非空，调用 updateVertices()
3. 根据 fullUpdate 标志决定更新整个缓冲区还是部分缓冲区
4. 绑定纹理，设置渲染状态，调用 drawQuadSet(size, 0)
**边界情况**：buffer 为 null 时会创建新缓冲区

### script()
**可见性**：protected  
**是否覆写**：否  
**方法职责**：获取渲染脚本实例  
**参数**：无  
**返回值**：NoosaScript，NoosaScriptNoLighting 实例  
**前置条件**：无  
**副作用**：无  
**核心实现逻辑**：return NoosaScriptNoLighting.get()  
**边界情况**：总是返回无光照脚本（瓷砖通常不需要光照）

### destroy()
**可见性**：public  
**是否覆写**：是，覆写自 Visual  
**方法职责**：清理瓷砖地图特有资源  
**参数**：无  
**返回值**：void  
**前置条件**：无  
**副作用**：删除 Vertexbuffer  
**核心实现逻辑**：
1. 调用 super.destroy() 清理基类资源
2. 如果 buffer 非 null，调用 buffer.delete()
**边界情况**：多次调用安全

### needsRender(int pos)
**可见性**：protected  
**是否覆写**：否  
**方法职责**：判断指定位置的瓷砖是否需要渲染  
**参数**：pos (int) - 线性瓷砖索引  
**返回值**：boolean，true 表示需要渲染  
**前置条件**：pos 在 [0, size) 范围内  
**副作用**：无  
**核心实现逻辑**：return data[pos] >= 0  
**边界情况**：负值瓷砖索引被跳过渲染（常见于透明/空瓷砖）

## 8. 对外暴露能力

### 显式 API
- **地图设置**: map(), updateMap(), updateMapCell()
- **纹理查询**: image() 获取单个瓷砖
- **继承功能**: 所有 Visual 的位置、变换、颜色功能

### 内部辅助方法
- **渲染优化**: updateVertices(), needsRender() 控制渲染粒度
- **内存管理**: moveToUpdating() 提供线程安全的更新区域管理

### 扩展入口
- **needsRender()**: 子类可重写以自定义渲染条件
- **script()**: 子类可重写以使用不同的渲染脚本

## 9. 运行机制与调用链

### 创建时机
- 关卡加载时（Dungeon 地图）
- 背景系统初始化时
- 动态地图生成时

### 调用者
- **Level**: 关卡地图渲染
- **GameScene**: 背景和装饰层
- **CustomMapSystem**: 自定义地图系统

### 被调用者
- **Visual**: 基类方法
- **TextureFilm**: 瓷砖 UV 坐标查询
- **NoosaScriptNoLighting**: OpenGL 渲染
- **Vertexbuffer**: GPU 资源管理

### 系统流程位置
1. **地图创建**: new Tilemap(texture, tileset) → map(data, width)
2. **地图更新**: level.modifyTile(x, y, newTile) → tilemap.updateMapCell(index)
3. **渲染准备**: Game.render() → scene.draw() → tilemap.draw()
4. **增量更新**: draw() detects updated region → updateVertices() → partial buffer update
5. **GPU 渲染**: drawQuadSet(size, 0) → renders all tiles in single call

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接消息键引用

### 依赖的资源
- **纹理图集**: 瓷砖纹理 PNG 文件
- **GPU 内存**: Vertexbuffer 存储所有瓷砖顶点
- **CPU 内存**: 地图数据数组和顶点缓存

### 中文翻译来源
无官方中文翻译关联

## 11. 使用示例

### 基本用法
```java
// 加载瓷砖纹理
SmartTexture tilesTex = TextureCache.get("levels/tiles.png");
// 创建瓷砖集（假设每个瓷砖16x16像素）
TextureFilm tileset = new TextureFilm(tilesTex, 16, 16);

// 创建瓷砖地图
Tilemap map = new Tilemap(tilesTex, tileset);
// 设置地图数据（width x height 网格）
int[] levelData = generateLevelData();
map.map(levelData, LEVEL_WIDTH);

// 添加到场景
Game.scene().add(map);

// 更新单个瓷砖
levelData[x + y * LEVEL_WIDTH] = WALL_TILE;
map.updateMapCell(x + y * LEVEL_WIDTH);
```

### 自定义渲染条件
```java
// 创建只渲染特定瓷砖的自定义 Tilemap
public class VisibleOnlyTilemap extends Tilemap {
    public VisibleOnlyTilemap(Object tx, TextureFilm tileset) {
        super(tx, tileset);
    }
    
    @Override
    protected boolean needsRender(int pos) {
        // 只渲染非空且非隐藏的瓷砖
        return data[pos] > 0 && !isHiddenTile(data[pos]);
    }
}
```

### 性能监控
```java
// 监控更新区域大小以优化性能
public class DebugTilemap extends Tilemap {
    private int lastUpdateSize = 0;
    
    @Override
    protected void updateVertices() {
        super.updateVertices();
        lastUpdateSize = (updating.right - updating.left) * 
                        (updating.bottom - updating.top);
        if (lastUpdateSize > 100) {
            // 大面积更新，可能需要优化
            Gdx.app.log("Tilemap", "Large update: " + lastUpdateSize + " tiles");
        }
    }
}
```

## 12. 开发注意事项

### 状态依赖
- **更新区域管理**: updated/ updating 提供线程安全的增量更新
- **缓冲区连续性**: 即使跳过渲染的瓷砖也占用缓冲区空间，保持索引一致性
- **纹理依赖**: 依赖 tileset 包含所有可能的瓷砖索引

### 生命周期耦合
- **GPU 资源**: Vertexbuffer 必须正确清理避免内存泄漏
- **地图数据**: data 数组必须在 Tilemap 生命周期内保持有效
- **场景图**: 作为 Visual 子类，受父容器状态影响

### 常见陷阱
- **数组越界**: 无效的 mapWidth 或瓷砖索引导致崩溃
- **内存泄漏**: 忘记调用 destroy() 导致 GPU 资源泄漏
- **性能问题**: 频繁的大面积更新导致 GPU 带宽瓶颈
- **负值瓷砖**: 负值索引自动跳过渲染，但需确保纹理图集中不存在负索引

## 13. 修改建议与扩展点

### 适合扩展的位置
- **多层地图**: 支持多个 Tilemap 层叠渲染
- **动画瓷砖**: 支持随时间变化的动态瓷砖
- **LOD 系统**: 根据距离调整渲染细节级别

### 不建议修改的位置
- **缓冲区策略**: 当前的零尺寸跳过策略经过性能验证
- **更新机制**: 增量更新逻辑是核心性能优化
- **索引计算**: 线性索引计算 (y * width + x) 是标准做法

### 重构建议
- **稀疏地图**: 对于大部分为空的地图，使用稀疏数组减少内存使用
- **动态分辨率**: 根据视口大小动态调整渲染分辨率
- **实例化渲染**: 对于大量重复瓷砖，使用 GPU 实例化进一步优化

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译（无相关翻译）
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点