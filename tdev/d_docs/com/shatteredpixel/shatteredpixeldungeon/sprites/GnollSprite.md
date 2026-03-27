# GnollSprite 类文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/GnollSprite.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.sprites |
| **类类型** | class |
| **继承关系** | extends MobSprite |
| **代码行数** | 50 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
GnollSprite 是豺狼人（Gnoll）怪物的精灵类，负责定义豺狼人的视觉表现，包括动画帧的加载和播放。

### 系统定位
位于 sprites 包中，作为 MobSprite 的子类，专门为豺狼人类型的怪物提供渲染支持。

### 不负责什么
- 不负责豺狼人的游戏逻辑（如AI、属性）
- 不负责战斗计算
- 不负责掉落物品

## 3. 结构总览

### 主要成员概览
- 构造器：GnollSprite()
- 继承的字段：idle, run, attack, die, texture 等（来自 MobSprite）

### 主要逻辑块概览
1. 纹理加载：加载 GNOLL 纹理资源
2. 动画定义：定义 idle、run、attack、die 四种动画

### 生命周期/调用时机
在游戏创建豺狼人怪物时，由对应 Mob 类实例化并关联。

## 4. 继承与协作关系

### 父类提供的能力
MobSprite 提供：
- 基础精灵渲染功能
- 动画播放机制
- 与 Mob 实体的关联
- 位置同步

### 覆写的方法
仅构造器，无显式覆写方法。

### 实现的接口契约
无直接实现的接口，继承自 MobSprite。

### 依赖的关键类
- `Assets.Sprites`：提供纹理资源键
- `TextureFilm`：管理纹理帧

### 使用者
- `Gnoll`（豺狼人怪物）
- `GnollTrickster`（豺狼诡术师）
- `GnollSapper`（豺狼工兵）

## 5. 字段/常量详解

### 静态常量
无自定义静态常量。

### 实例字段
无自定义实例字段，全部继承自 MobSprite。

## 6. 构造与初始化机制

### 构造器
```java
public GnollSprite()
```
初始化豺狼人精灵的完整过程：
1. 调用父类构造器
2. 加载 GNOLL 纹理
3. 创建 12x15 像素的帧序列
4. 定义四种动画

### 初始化块
无静态或实例初始化块。

### 初始化注意事项
帧尺寸为 12x15 像素，与纹理资源匹配。

## 7. 方法详解

### GnollSprite()

**可见性**：public

**是否覆写**：否

**方法职责**：初始化豺狼人精灵的所有动画。

**参数**：无

**返回值**：无

**前置条件**：Assets.Sprites.GNOLL 纹理资源已加载。

**副作用**：设置 texture、idle、run、attack、die 字段。

**核心实现逻辑**：
```java
texture( Assets.Sprites.GNOLL );
TextureFilm frames = new TextureFilm( texture, 12, 15 );

idle = new Animation( 2, true );
idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );

run = new Animation( 12, true );
run.frames( frames, 4, 5, 6, 7 );

attack = new Animation( 12, false );
attack.frames( frames, 2, 3, 0 );

die = new Animation( 12, false );
die.frames( frames, 8, 9, 10 );

play( idle );
```

**边界情况**：无特殊边界处理。

## 8. 对外暴露能力

### 显式 API
仅构造器，其余功能继承自 MobSprite。

### 内部辅助方法
无。

### 扩展入口
可通过覆写 animDeferred() 等方法进行扩展。

## 9. 运行机制与调用链

### 创建时机
在创建 Gnoll 或其变种怪物实例时，由 Mob.link() 方法关联。

### 调用者
- Mob.link()
- GameScene.addMob()

### 被调用者
- Assets.Sprites（纹理资源）
- TextureFilm（帧管理）

### 系统流程位置
渲染层，位于怪物实体与屏幕显示之间。

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无直接引用。豺狼人相关文案：
- `actors.mobs.gnoll.name` = 豺狼人（来自 actors_zh.properties）

### 依赖的资源
- 纹理：Assets.Sprites.GNOLL

### 中文翻译来源
actors_zh.properties 文件中定义了豺狼人相关的官方翻译。

## 11. 使用示例

### 基本用法
```java
// 在 Mob 子类中
@Override
public CharSprite sprite() {
    return new GnollSprite();
}
```

### 扩展示例
```java
// 创建自定义豺狼人精灵
public class CustomGnollSprite extends GnollSprite {
    @Override
    public void play(Animation anim) {
        super.play(anim);
        // 添加自定义效果
    }
}
```

## 12. 开发注意事项

### 状态依赖
依赖 Assets.Sprites.GNOLL 纹理的正确加载。

### 生命周期耦合
精灵生命周期与 Mob 实体绑定。

### 常见陷阱
- 修改帧尺寸需同步更新纹理资源
- 动画帧索引必须与纹理资源匹配

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可覆写 idle、run 等动画以创建变种豺狼人

### 不建议修改的位置
- 帧尺寸常量（12x15）与纹理资源强绑定

### 重构建议
无当前重构需求。

## 14. 事实核查清单

- [x] 已覆盖全部字段（无自定义字段）
- [x] 已覆盖全部方法（仅构造器）
- [x] 已检查继承链与覆写关系
- [x] 已核对官方中文翻译（豺狼人）
- [x] 无推测性表述
- [x] 示例代码真实可用
- [x] 已标注资源关联
- [x] 已明确说明注意事项与扩展点