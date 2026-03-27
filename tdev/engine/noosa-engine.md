# Noosa 游戏引擎

一个基于 LibGDX 构建的轻量级跨平台 2D 游戏引擎，专为像素艺术游戏设计。最初由 Oleg Dolya 为 Pixel Dungeon 创建，现由 Shattered Pixel Dungeon 维护。

## 架构概述

```
┌─────────────────────────────────────────────────────────────────┐
│                         游戏层                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │   场景     │  │   摄像机    │  │   游戏 (主循环)      │ │
│  │  (屏幕)    │  │  (视口)     │  │   (LibGDX 集成)     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                       渲染层                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │   视觉     │  │   组        │  │   NoosaScript (着色器)  │ │
│  │  (可绘制)   │  │ (容器)      │  │   (OpenGL 渲染)      │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                         核心层                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │   Gizmo     │  │  TextureFilm│  │   输入系统          │ │
│  │ (基类)      │  │ (精灵图集)  │  │   (指针/键盘)       │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                       平台层                             │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    LibGDX + OpenGL ES                      ││
│  │              (Android, iOS, 桌面, Web)                     ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

## 核心类

### Gizmo - 基础游戏对象

所有游戏对象的基本构建块。每个视觉和交互元素都继承自 `Gizmo`。

```java
public class Gizmo {
    public boolean exists;   // 此对象是否在游戏世界中？
    public boolean alive;    // 此对象是否活跃（未被销毁）？
    public boolean active;   // 是否应调用 update()？
    public boolean visible;  // 是否应调用 draw()？
    public Group parent;     // 包含此 gizmo 的容器
    public Camera camera;    // 用于渲染的摄像机
    
    // 生命周期方法
    public void destroy() { }  // 清理资源
    public void update() { }   // 每帧调用（当活跃时）
    public void draw() { }     // 每帧调用（当可见时）
    public void kill() { }     // 标记为死亡和不存在
    public void revive() { }   // 恢复到活跃状态
}
```

**状态管理:**
- `exists` + `alive` + `active` → 调用 `update()`
- `exists` + `visible` → 调用 `draw()`
- `kill()` 将 `exists` 和 `alive` 都设为 false
- `revive()` 将两者都恢复为 true

### Visual - 可绘制对象

扩展 `Gizmo`，具有位置、大小、变换和颜色属性。

```java
public class Visual extends Gizmo {
    // 位置和大小
    public float x, y;
    public float width, height;
    
    // 变换
    public PointF scale;    // 缩放因子（默认 1,1）
    public PointF origin;   // 旋转/缩放原点
    public float angle;     // 旋转角度（度）
    
    // 颜色乘法（调节纹理颜色）
    public float rm, gm, bm, am;  // 乘数（默认 1）
    
    // 颜色加法（着色/光照）
    public float ra, ga, ba, aa;  // 加法值（默认 0）
    
    // 物理（简单运动）
    public PointF speed;       // 速度
    public PointF acc;         // 加速度
    public float angularSpeed; // 旋转速度
    
    // 颜色操作方法
    public void alpha(float value);      // 设置透明度
    public void tint(int color);         // 应用颜色着色
    public void brightness(float value); // 调整亮度
    public void hardlight(int color);    // 应用强光
    public void invert();                // 反转颜色
}
```

**最终颜色公式:**
```
finalColor = textureColor * (rm, gm, bm, am) + (ra, ga, ba, aa)
```

### Group - Gizmos 容器

`Group` 管理一组 `Gizmo` 对象，处理它们的生命周期和渲染顺序。

```java
public class Group extends Gizmo {
    protected ArrayList<Gizmo> members;
    public int length;  // 为性能缓存的大小
    
    // 添加/移除
    public Gizmo add(Gizmo g);           // 添加到末尾
    public Gizmo addToFront(Gizmo g);    // 添加到前面
    public Gizmo addToBack(Gizmo g);     // 添加到后面
    public Gizmo remove(Gizmo g);        // 移除并缩小
    public Gizmo erase(Gizmo g);         // 用 null 替换（更快）
    
    // 回收（对象池）
    public Gizmo recycle(Class<? extends Gizmo> c);
    public Gizmo getFirstAvailable(Class<? extends Gizmo> c);
    
    // 查询
    public int countLiving();  // 计算活跃成员数量
    public int countDead();    // 计算非活跃成员数量
    public Gizmo random();     // 获取随机成员
    
    // 顺序
    public Gizmo bringToFront(Gizmo g);
    public Gizmo sendToBack(Gizmo g);
    public void sort(Comparator c);
}
```

**更新/绘制顺序:** 成员按数组顺序处理（索引 0 优先）。

### Scene - 游戏屏幕容器

场景（屏幕）的基类。扩展 `Group` 并带有生命周期管理。

```java
public class Scene extends Group {
    public void create() { }     // 初始化场景
    public void destroy() { }    // 清理场景
    public void onPause() { }    // 应用失去焦点
    public void onResume() { }   // 应用获得焦点
    
    @Override
    public Camera camera() { return Camera.main; }
    
    protected void onBackPressed() { }  // 处理返回按钮
}
```

### Camera - 视口控制器

定义用于渲染的可视区域和投影。

```java
public class Camera extends Visual {
    public static Camera main;  // 默认摄像机
    
    public float zoom;          // 缩放级别
    public PointF scroll;       // 摄像机位置偏移
    
    public int screenWidth;     // 以像素为单位的显示宽度
    public int screenHeight;    // 以像素为单位的显示高度
    
    public boolean fullScreen;  // 覆盖整个屏幕？
    
    protected float[] matrix;   // 投影矩阵
    
    // 创建
    public static Camera create(int width, int height);
    public static Camera createFullscreen();
    
    // 效果
    public void shake(float magnitude, float duration);
    
    // 转换
    public PointF screenToCamera(int x, int y);
    public PointF cameraToScreen(PointF point);
    public boolean hitTest(int x, int y);
}
```

## 渲染类

### Image - 纹理精灵

显示纹理化的四边形（精灵）。

```java
public class Image extends Visual {
    public SmartTexture texture;    // 纹理源
    protected RectF frame;          // UV 坐标
    
    public boolean flipHorizontal;  // 水平镜像
    public boolean flipVertical;    // 垂直镜像
    
    // 构造函数
    public Image() { }
    public Image(Object tx) { }                           // 完整纹理
    public Image(Object tx, int left, int top,            // 区域
                 int width, int height) { }
    
    // 帧操作
    public void texture(Object tx);       // 设置纹理
    public void frame(RectF frame);       // 设置 UV 区域
    public void frame(int left, int top, int width, int height);
    
    // 从另一个图像复制
    public void copy(Image other);
}
```

### MovieClip - 动画精灵

扩展 `Image` 并带有基于帧的动画。

```java
public class MovieClip extends Image {
    public boolean paused = false;
    public Listener listener;
    
    // 播放动画
    public void play(Animation anim);
    public void play(Animation anim, boolean force);
    
    // 检查状态
    public boolean looping();
    
    // 动画定义
    public static class Animation {
        public float delay;      // 帧之间的时间
        public RectF[] frames;   // 每帧的 UV 坐标
        public boolean looped;   // 循环或播放一次
        
        public Animation(int fps, boolean looped);
        public Animation frames(RectF... frames);
        public Animation frames(TextureFilm film, Object... frames);
    }
    
    // 完成回调
    public interface Listener {
        void onComplete(Animation anim);
    }
}
```

**动画示例:**
```java
TextureFilm frames = new TextureFilm(texture, 16, 16);
Animation walk = new Animation(12, true)
    .frames(frames, 0, 1, 2, 3);  // 帧索引

MovieClip hero = new MovieClip(texture);
hero.play(walk);
```

### Tilemap - 基于网格的地图渲染

使用单个绘制调用高效渲染大型瓦片网格。

```java
public class Tilemap extends Visual {
    protected SmartTexture texture;
    protected TextureFilm tileset;
    protected int[] data;        // 瓦片 ID
    protected int mapWidth;
    protected int mapHeight;
    
    public Tilemap(Object tx, TextureFilm tileset);
    
    // 设置瓦片数据
    public void map(int[] data, int cols);
    
    // 更新特定瓦片
    public void updateMapCell(int cell);
    
    // 将瓦片作为图像获取
    public Image image(int x, int y);
    
    // 重写以控制哪些瓦片渲染
    protected boolean needsRender(int pos);
}
```

### BitmapText - 位图字体渲染

使用位图字体渲染文本。

```java
public class BitmapText extends Visual {
    protected String text;
    protected Font font;
    public int realLength;  // 字符数
    
    public BitmapText() { }
    public BitmapText(Font font) { }
    public BitmapText(String text, Font font) { }
    
    public void font(Font value);
    public void text(String str);
    public void measure();        // 计算边界而不渲染
    public float baseLine();      // 字体基线
    
    // 字体定义
    public static class Font extends TextureFilm {
        public SmartTexture texture;
        public float tracking;    // 字母间距
        public float baseLine;    // 基线偏移
        public float lineHeight;  // 行高
        
        public Font(SmartTexture tx, int width, int height, String chars);
        
        // 从颜色分离的字体图像创建
        public static Font colorMarked(SmartTexture tex, int color, String chars);
    }
}
```

### ColorBlock - 实心颜色矩形

渲染实心颜色四边形。

```java
public class ColorBlock extends Visual {
    public ColorBlock(float width, float height, int color);
}
```

### NinePatch - 可拉伸的 UI 背景

渲染用于可调整大小的 UI 元素的 9 片图像。

```java
public class NinePatch extends Image {
    public NinePatch(Object tx, int margin);
    public NinePatch(Object tx, int left, int top, int right, int bottom);
    
    // 调整大小以保持边框完整性
}
```

### SkinnedBlock - 平铺背景

渲染重复/平铺的纹理。

```java
public class SkinnedBlock extends Visual {
    public SkinnedBlock(float width, float height, Object tx);
}
```

## UI 框架

### Component - UI 容器基础

UI 元素的基类，具有布局功能。

```java
public class Component extends Group {
    protected float x, y, width, height;
    
    public Component setPos(float x, float y);
    public Component setSize(float width, float height);
    public Component setRect(float x, float y, float width, float height);
    
    public boolean inside(float x, float y);
    public void fill(Component c);  // 匹配另一个组件的边界
    
    // 布局助手
    public float left(), right(), top(), bottom();
    public float centerX(), centerY();
    
    // 重写以实现自定义布局
    protected void createChildren() { }
    protected void layout() { }
}
```

### PointerArea - 触摸/鼠标输入处理程序

捕获区域内指针（鼠标/触摸）事件。

```java
public class PointerArea extends Visual {
    public Visual target;  // 要监控的区域
    public int blockLevel;
    
    // 阻塞级别
    public static final int ALWAYS_BLOCK = 0;      // 总是消耗
    public static final int BLOCK_WHEN_ACTIVE = 1; // 仅当活跃时
    public static final int NEVER_BLOCK = 2;       // 传递
    
    public PointerArea(Visual target);
    public PointerArea(float x, float y, float width, float height);
    
    // 重写以处理事件
    protected void onPointerDown(PointerEvent event) { }
    protected void onPointerUp(PointerEvent event) { }
    protected void onClick(PointerEvent event) { }
    protected void onDrag(PointerEvent event) { }
    protected void onHoverStart(PointerEvent event) { }
    protected void onHoverEnd(PointerEvent event) { }
    
    public void givePointerPriority();  // 移动到事件队列前面
}
```

### ScrollArea - 可滚动容器

启用内容的滚动/拖动。

```java
public class ScrollArea extends PointerArea {
    public ScrollArea(Visual target);
    public ScrollArea(float x, float y, float width, float height);
    
    protected void onScroll(PointerEvent event);  // 处理滚动
}
```

## 动画与缓动

### Tweener - 动画基类

属性动画的抽象基类。

```java
abstract public class Tweener extends Gizmo {
    public Gizmo target;
    public float interval;  // 以秒为单位的持续时间
    public float elapsed;   // 当前时间
    public Listener listener;
    
    public Tweener(Gizmo target, float interval);
    
    // 控制
    public void stop(boolean complete);  // 结束动画
    
    // 重写以实现
    abstract protected void updateValues(float progress);
    
    public interface Listener {
        void onComplete(Tweener tweener);
    }
}
```

### 内置缓动器

```java
// 淡入/淡出
public class AlphaTweener extends Tweener {
    public AlphaTweener(Visual target, float alpha, float interval);
}

// 移动到位置
public class PosTweener extends Tweener {
    public PosTweener(Visual target, PointF pos, float interval);
}

// 缩放
public class ScaleTweener extends Tweener {
    public ScaleTweener(Visual target, PointF scale, float interval);
}

// 摄像机平移
public class CameraScrollTweener extends Tweener {
    public CameraScrollTweener(Camera camera, PointF scroll, float interval);
}

// 延迟回调
public class Delayer extends Tweener {
    public Delayer(float interval);
}
```

**使用示例:**
```java
Visual sprite = new Image(texture);

// 在 1 秒内淡出
AlphaTweener fade = new AlphaTweener(sprite, 0, 1f);
fade.listener = new Tweener.Listener() {
    public void onComplete(Tweener t) {
        sprite.killAndErase();
    }
};
parent.add(fade);
```

## 粒子系统

### Emitter - 粒子生成器

生成和管理粒子。

```java
public class Emitter extends Group {
    public float x, y, width, height;  // 生成区域
    public boolean on;                 // 是否正在发射？
    public boolean autoKill = true;    // 完成后是否自动销毁？
    
    // 位置
    public void pos(float x, float y);
    public void pos(float x, float y, float width, float height);
    public void pos(Visual target);
    
    // 发射模式
    public void burst(Factory factory, int quantity);     // 一次性全部
    public void pour(Factory factory, float interval);    // 连续
    public void start(Factory factory, float interval, int quantity);
    
    // 冻结控制
    public static boolean freezeEmitters = false;
    
    // 粒子工厂
    abstract public static class Factory {
        abstract public void emit(Emitter emitter, int index, float x, float y);
        public boolean lightMode() { return false; }
    }
}
```

### PixelParticle - 简单粒子

带有大小和寿命的基础粒子。

```java
public class PixelParticle extends PseudoPixel {
    protected float size;
    protected float lifespan;
    protected float left;  // 剩余生命
    
    public void reset(float x, float y, int color, float size, float lifespan);
    
    // 缩小变体
    public static class Shrinking extends PixelParticle {
        @Override
        public void update() {
            super.update();
            size(size * left / lifespan);
        }
    }
}
```

**自定义粒子示例:**
```java
public class FireParticle extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((FireParticle)emitter.recycle(FireParticle.class))
                .reset(x, y);
        }
    };
    
    public void reset(float x, float y) {
        revive();
        this.x = x;
        this.y = y;
        speed.set(Random.Float(-10, 10), Random.Float(-30, -10));
        color(0xFF6600);
        size = 4;
        lifespan = left = 1f;
    }
    
    @Override
    public void update() {
        super.update();
        // 在生命周期内淡出和缩小
        am = left / lifespan;
        size(size * left / lifespan);
    }
}

// 使用
Emitter emitter = new Emitter();
emitter.pos(100, 100, 50, 50);
emitter.pour(FireParticle.FACTORY, 0.05f);
```

## 音频系统

### Music - 背景音乐播放器

音乐播放的单例。

```java
public enum Music {
    INSTANCE;
    
    // 单曲
    public void play(String assetName, boolean looping);
    
    // 播放列表
    public void playTracks(String[] tracks, float[] chances, boolean shuffle);
    
    // 控制
    public void stop();
    public void pause();
    public void resume();
    public void end();
    
    // 淡出
    public void fadeOut(float duration, Callback onComplete);
    
    // 音量 (0-1)
    public void volume(float value);
    
    // 启用/禁用
    public void enable(boolean value);
    public boolean isEnabled();
    
    // 状态
    public boolean isPlaying();
    public boolean paused();
}
```

### Sample - 音效播放器

短音效的单例。

```java
public enum Sample {
    INSTANCE;
    
    // 加载声音
    public void load(String asset);
    public void load(String[] assets);
    public void unload(Object src);
    
    // 播放声音
    public long play(Object id);
    public long play(Object id, float volume);
    public long play(Object id, float volume, float pitch);
    public long play(Object id, float leftVol, float rightVol, float pitch);
    
    // 延迟播放
    public void playDelayed(Object id, float delay);
    
    // 控制
    public void pause();
    public void resume();
    public void reset();
    
    // 音量 (0-1)
    public void volume(float value);
    public void enable(boolean value);
}
```

## 游戏循环

### Game - 应用程序入口点

管理游戏循环和场景的主类。

```java
public class Game implements ApplicationListener {
    public static Game instance;
    
    // 显示
    public static int width, height;
    public static float density;
    
    // 时间
    public static float timeScale = 1f;
    public static float elapsed;     // Delta time (已缩放)
    public static float timeTotal;   // 总游戏时间
    public static long realTime;     // 以毫秒为单位的系统时间
    
    // 当前场景
    public static Scene scene();
    
    // 场景管理
    public static void switchScene(Class<? extends Scene> c);
    public static void switchScene(Class<? extends Scene> c, SceneChangeCallback callback);
    public static void resetScene();
    
    // 平台
    public static PlatformSupport platform;
    public static InputHandler inputHandler;
    
    // 工具
    public static void reportException(Throwable tr);
    public static void runOnRenderThread(Callback c);
    public static void vibrate(int milliseconds);
}
```

**创建游戏:**
```java
public class MyGame extends Game {
    public MyGame() {
        super(TitleScene.class, new DesktopPlatformSupport());
    }
}

public class TitleScene extends Scene {
    @Override
    public void create() {
        // 添加 UI 元素
        Image logo = new Image("logo.png");
        add(logo);
    }
}
```

## 渲染管道

### NoosaScript - 着色器程序

所有 Noosa 渲染的自定义着色器。

```java
public class NoosaScript extends Script {
    // Uniforms
    public Uniform uCamera;  // 摄像机矩阵
    public Uniform uModel;   // 模型矩阵
    public Uniform uTex;     // 纹理
    public Uniform uColorM;  // 颜色乘数
    public Uniform uColorA;  // 颜色加法值
    
    // 属性
    public Attribute aXY;    // 位置
    public Attribute aUV;    // 纹理坐标
    
    // 绘制
    public void drawQuad(FloatBuffer vertices);
    public void drawQuad(Vertexbuffer buffer);
    public void drawQuadSet(Vertexbuffer buffer, int length, int offset);
    
    // 光照（颜色调整）
    public void lighting(float rm, float gm, float bm, float am,
                        float ra, float ga, float ba, float aa);
    
    // 摄像机设置
    public void camera(Camera camera);
    
    public static NoosaScript get();
}
```

**着色器代码:**
```glsl
// 顶点着色器
uniform mat4 uCamera;
uniform mat4 uModel;
attribute vec4 aXYZW;
attribute vec2 aUV;
varying vec2 vUV;

void main() {
    gl_Position = uCamera * uModel * aXYZW;
    vUV = aUV;
}

// 片段着色器
varying vec2 vUV;
uniform sampler2D uTex;
uniform vec4 uColorM;
uniform vec4 uColorA;

void main() {
    gl_FragColor = texture2D(uTex, vUV) * uColorM + uColorA;
}
```

## 纹理管理

### TextureFilm - 精灵图集

将纹理区域映射到精灵表的键。

```java
public class TextureFilm {
    // 作为单帧的完整纹理
    public TextureFilm(Object tx);
    
    // 基于网格的精灵表
    public TextureFilm(Object tx, int width, int height);
    
    // 现有图集的子区域
    public TextureFilm(TextureFilm atlas, Object key, int width, int height);
    
    // 手动帧定义
    public void add(Object id, RectF rect);
    public void add(Object id, float left, float top, float right, float bottom);
    
    // 获取帧 UV 坐标
    public RectF get(Object id);
    
    // 获取尺寸
    public float width(Object id);
    public float height(Object id);
}
```

### SmartTexture - 托管纹理

带有元数据和缓存的纹理。

```java
public class SmartTexture extends Texture {
    public Pixmap bitmap;
    public int width, height;
    public int fMode;  // 过滤模式
    public int wMode;  // 包装模式
    
    // UV 坐标计算
    public RectF uvRect(int left, int top, int right, int bottom);
}
```

## 输入系统

### PointerEvent - 触摸/鼠标事件

```java
public class PointerEvent {
    public enum Type { DOWN, UP, CANCEL, HOVER }
    
    public Type type;
    public PointF current;  // 当前位置
    public PointF start;    // 初始位置
    public boolean handled;
    public int id;          // 指针 ID（用于多点触控）
}
```

### KeyEvent - 键盘事件

```java
public class KeyEvent {
    public int code;      // 键码
    public boolean pressed;
}
```

## 类层次结构图

```
Gizmo (基础)
├── Visual (可绘制带变换)
│   ├── Image (纹理四边形)
│   │   ├── MovieClip (动画图像)
│   │   └── NinePatch (9 片)
│   ├── BitmapText (文本渲染)
│   ├── Tilemap (网格渲染)
│   ├── ColorBlock (实心颜色)
│   ├── SkinnedBlock (平铺纹理)
│   ├── Halo (发光效果)
│   ├── PseudoPixel (单像素)
│   ├── PointerArea (输入处理程序)
│   ├── ScrollArea (滚动容器)
│   └── Camera (视口)
├── Group (容器)
│   ├── Scene (屏幕)
│   ├── Component (UI 容器)
│   ├── Emitter (粒子系统)
│   └── Tweener (动画)
└── Tweener (动画基础)
    ├── AlphaTweener
    ├── PosTweener
    ├── ScaleTweener
    ├── CameraScrollTweener
    └── Delayer
```

## 最佳实践

### 对象池

使用 `recycle()` 而不是创建新对象：

```java
// 不好 - 创建垃圾
Particle p = new Particle();
add(p);

// 好 - 重用对象
Particle p = (Particle)recycle(Particle.class);
if (p == null) {
    p = new Particle();
    add(p);
}
```

### 场景生命周期

始终在 `destroy()` 中清理：

```java
public class GameScene extends Scene {
    private Emitter emitter;
    
    @Override
    public void create() {
        emitter = new Emitter();
        add(emitter);
    }
    
    @Override
    public void destroy() {
        emitter = null;  // 允许 GC
        super.destroy();
    }
}
```

### 高效更新

使用 `active` 标志跳过不必要的更新：

```java
public class Monster extends MovieClip {
    @Override
    public void update() {
        if (!active) return;  // 如果不活跃则跳过
        super.update();
    }
}
```

### 颜色操作

高效使用颜色方法：

```java
// 淡出
sprite.alpha(0.5f);

// 着色红色
sprite.tint(0xFF0000, 0.5f);  // 50% 红色着色

// 强光（替换颜色）
sprite.hardlight(0xFF0000);    // 完全红色

// 亮度
sprite.brightness(0.5f);       // 50% 亮度

// 重置为正常
sprite.resetColor();
```

## 性能提示

1. **明智使用 Groups**: Group.update() 迭代所有成员。使用嵌套组来剔除更新。

2. **最小化绘制调用**: Tilemap 对所有瓦片使用一次绘制调用。

3. **回收对象**: 避免在更新循环中使用 `new`。

4. **使用脏标志**: 仅在数据更改时更新顶点缓冲区。

5. **批量相似对象**: 绘制顺序影响批处理效率。

6. **屏幕外禁用**: 使用 `visible = false` 进行剔除。

7. **首选 TextureFilm**: 一个纹理 = 一次绑定。为你的精灵制作图集。

## 平台说明

- **Android**: 处理 EGL 上下文丢失，纹理重新加载
- **iOS**: 在运行时将 .ogg 转换为 .mp3
- **桌面**: 更高性能，无上下文丢失
- **Web (GWT)**: 限于 WebGL 功能

## 版本历史

- 最初由 Oleg Dolya 为 Pixel Dungeon 创建 (2012-2015)
- 由 Evan Debenham 为 Shattered Pixel Dungeon 维护和扩展 (2014-至今)
- 基于 LibGDX 框架构建
- 跨平台：Android、iOS、桌面、Web