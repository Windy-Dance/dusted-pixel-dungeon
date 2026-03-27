# CustomTilemap.java - 自定义瓦片装饰系统

## 概述
`CustomTilemap` 是自定义关卡装饰的抽象基类，支持保存/恢复功能和复杂的多瓦片视觉效果。它为关卡设计师和游戏逻辑提供了一种创建持久化、可互动装饰元素的机制。

## 核心功能

### 多瓦片支持
- **矩形区域**：支持任意尺寸的瓦片装饰（1x1 到 NxM）
- **位置控制**：精确的瓦片坐标定位
- **相对定位**：相对于地图原点的位置设置

### 持久化系统
- **Bundlable 接口**：实现 `restoreFromBundle()` 和 `storeInBundle()`
- **游戏存档集成**：自动与游戏保存/加载系统协同工作  
- **状态保持**：跨游戏会话保持装饰状态

### 交互支持
- **点击检测**：`overlapsPoint()` 系列方法提供精确的点击区域检测
- **描述信息**：可重写的 `name()` 和 `desc()` 方法用于调试和UI
- **事件响应**：支持与其他游戏系统的事件集成

## 主要字段

### 定位字段
- `tileX`, `tileY` - 装饰在瓦片坐标系中的位置
- `tileW`, `tileH` - 装饰的宽度和高度（以瓦片为单位）
- `vis` - 内部的 Tilemap 视觉对象

### 配置字段
- `needsSetRect` - 标记是否需要设置矩形区域
- 可扩展的自定义字段（由子类添加）

## 核心方法

### 定位辅助方法
```java
// 设置位置（瓦片坐标）
public void pos(int x, int y)

// 设置矩形区域  
public void setRect(int x, int y, int w, int h)

// 获取位置信息
public int left() { return tileX; }
public int right() { return tileX + tileW - 1; }
public int top() { return tileY; }
public int bottom() { return tileY + tileH - 1; }
```

### 渲染工具方法
```java
// 生成纹理坐标数据
protected float[] mapSimpleImage(Image image, int tw, int th)

// 创建完整的 Tilemap 视觉
protected void create()
```

### 描述方法（可重写）
```java
public String name() { return null; }
public String desc() { return null; }
```

### 持久化方法
```java
@Override
public void restoreFromBundle(Bundle bundle)

@Override  
public void storeInBundle(Bundle bundle)
```

## 技术实现

### 纹理坐标映射
```java
protected float[] mapSimpleImage(Image image, int tw, int th) {
    // 生成标准的纹理坐标数组
    // 支持多瓦片的UV映射
    // 处理纹理重复和边缘情况
}
```

### 光照集成
- 自动应用游戏的光照系统
- 支持动态亮度调整
- 与 FogOfWar 系统正确集成

### 内存管理
- **延迟创建**：`vis` 对象在首次需要时创建
- **资源复用**：共享纹理和图像资源
- **清理机制**：支持显式的资源释放

## 使用模式

### 基本自定义装饰
```java
public class MyDecoration extends CustomTilemap {
    public MyDecoration() {
        // 设置位置和尺寸
        setRect(5, 5, 2, 2);
        
        // 创建视觉效果
        Image img = new Image(Assets.Sprites.MY_DECORATION);
        float[] texCoords = mapSimpleImage(img, 2, 2);
        vis = new Tilemap(texCoords, img.texture());
    }
    
    @Override
    public String name() {
        return "我的装饰";
    }
    
    @Override
    public String desc() {
        return "这是一个自定义的关卡装饰";
    }
}
```

### 互动装饰
```java
// 重写点击检测
@Override
public boolean overlapsPoint(float x, float y) {
    // 自定义点击区域逻辑
    return super.overlapsPoint(x, y) && isActive();
}

// 响应点击事件
public void onClick() {
    // 执行互动逻辑
    GameScene.show(new WndMessage(desc()));
}
```

### 动态装饰
```java
// 支持状态变化
public void setState(int newState) {
    // 更新视觉效果
    updateVisual();
    
    // 触发更新
    if (vis != null) {
        vis.killAndErase();
        create();
    }
}

// 保存状态
@Override
public void storeInBundle(Bundle bundle) {
    super.storeInBundle(bundle);
    bundle.put("state", currentState);
}

@Override
public void restoreFromBundle(Bundle bundle) {
    super.restoreFromBundle(bundle);
    currentState = bundle.getInt("state");
}
```

## 性能考虑

### 渲染优化
- **批处理友好**：与游戏的批处理渲染器兼容
- **视锥剔除**：自动跳过屏幕外的装饰
- **脏标记**：只在状态变更时重绘

### 内存效率  
- **按需加载**：大型装饰只在接近时创建
- **纹理压缩**：支持压缩纹理格式
- **对象池**：重用临时计算对象

### 更新频率
- **静态装饰**：创建后很少更新
- **动态装饰**：支持高效的增量更新
- **事件驱动**：基于游戏事件触发更新

## 扩展性设计

### 子类扩展点
- **视觉创建**：重写 `create()` 方法自定义渲染
- **交互逻辑**：重写 `overlapsPoint()` 和相关方法
- **状态管理**：添加自定义状态字段和持久化逻辑

### 组合模式
- **嵌套装饰**：一个 CustomTilemap 可包含多个子装饰
- **复合效果**：组合多个视觉元素创建复杂装饰
- **层级管理**：支持前后层叠关系

### 系统集成
- **关卡生成器**：作为关卡生成过程的一部分
- **事件系统**：响应游戏事件改变状态
- **物品系统**：与可收集物品或互动对象集成

## 实际应用场景

### 关卡装饰
- 背景壁画和装饰性元素
- 区域特定的主题装饰
- 节日或特殊事件的临时装饰

### 互动元素  
- 可点击的信息板或提示
- 隐藏的开关或机关
- 故事相关的可检查对象

### 动态环境
- 可破坏的装饰物
- 响应玩家行为的环境元素
- 时间或状态变化的视觉反馈

## 调试和开发支持

### 开发者工具
- 自动显示装饰边界（调试模式）
- 支持实时预览和位置调整
- 提供详细的描述信息用于调试

### 性能监控
- 装饰数量统计
- 渲染时间测量
- 内存使用跟踪

### 错误处理
- 安全的空值处理
- 优雅的资源加载失败处理
- 详细的错误日志记录

## 最佳实践

### 设计原则
- **单一职责**：每个装饰专注于单一功能
- **性能意识**：避免不必要的更新和渲染
- **用户体验**：确保装饰不影响游戏玩法

### 内存管理
- 及时释放不再需要的资源
- 使用适当的纹理格式和压缩
- 避免内存泄漏（特别是在持久化装饰中）

### 兼容性
- 保持向后兼容的持久化格式
- 支持不同分辨率和设备
- 考虑本地化对装饰文本的影响