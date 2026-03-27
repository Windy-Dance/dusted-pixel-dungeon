# ScrollOfPrismaticImage 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfPrismaticImage.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 72 行 |
| **所属模块** | core |
| **官方中文名** | 虹卫秘卷 |

## 2. 文件职责说明

### 核心职责
虹卫秘卷是一种召唤型秘卷，阅读后会治疗已存在的虹光守卫或为英雄添加虹光守卫Buff。

### 系统定位
作为镜像卷轴的升级版本，对应普通卷轴为镜像卷轴（ScrollOfMirrorImage）。

### 不负责什么
- 不直接创建虹光幻像NPC（由 PrismaticGuard Buff 创建）
- 不对敌人造成伤害

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识（ItemSpriteSheet.Icons.SCROLL_PRISIMG）

### 主要逻辑块概览
- `doRead()`: 阅读逻辑，治疗或创建虹光守卫

### 生命周期/调用时机
阅读后立即生效

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读逻辑：治疗或创建虹光守卫 |

### 依赖的关键类
- `PrismaticImage`: 虹光幻像NPC类
- `PrismaticGuard`: 虹光守卫Buff类
- `Stasis`: 静滞状态处理（检查静滞中的虹光幻像）
- `Dungeon.level.mobs`: 当前层怪物列表
- `Buff`: Buff管理类

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_PRISIMG | 物品图标标识 |

### 静态常量
无

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_PRISIMG;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读逻辑，治疗已存在的虹光幻像或为英雄添加虹光守卫Buff。

**前置条件**：
- 英雄必须可以阅读

**副作用**：
- 从背包移除物品
- 鉴定物品
- 播放音效和动画
- 可能治疗现有虹光幻像或添加Buff

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    boolean found = false;
    
    // 1. 遍历当前层所有怪物，寻找虹光幻像
    for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
        if (m instanceof PrismaticImage){
            found = true;
            m.HP = m.HT; // 恢复满血
            m.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(m.HT), FloatingText.HEALING );
        }
    }

    // 2. 检查静滞状态中的虹光幻像
    if (!found){
        if (Stasis.getStasisAlly() instanceof PrismaticImage){
            found = true;
            Stasis.getStasisAlly().HP = Stasis.getStasisAlly().HT;
        }
    }
    
    // 3. 如果没有虹光幻像，创建虹光守卫Buff
    if (!found) {
        Buff.affect(curUser, PrismaticGuard.class).set( PrismaticGuard.maxHP( curUser ) );
    }

    identify();
    Sample.INSTANCE.play( Assets.Sounds.READ );
    readAnimation();
}
```

**边界情况**：
- 如果存在多个虹光幻像，全部都会被治疗
- 静滞状态中的虹光幻像也会被治疗
- 只有在没有任何虹光幻像时才创建Buff

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读逻辑

### 内部辅助方法
无

### 扩展入口
- 可覆写 `doRead()` 自定义治疗或召唤逻辑

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（镜像卷轴 + 6能量）
- 通过 Generator 随机生成

### 调用者
- 英雄使用物品时调用

### 被调用者
- `Dungeon.level.mobs`: 获取怪物列表
- `Buff.affect()`: 添加Buff
- `PrismaticGuard.set()`: 设置虹光守卫生命值
- `PrismaticGuard.maxHP()`: 计算最大生命值
- `Stasis.getStasisAlly()`: 获取静滞中的盟友

### 系统流程位置
```
阅读 → doRead() → 检查虹光幻像是否存在 → 
  存在 → 恢复满血
  不存在 → 添加虹光守卫Buff
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofprismaticimage.name | 虹卫秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofprismaticimage.desc | 这张秘卷上的咒文会创造使用者的一个虹光守卫。这个像使用者的弱化版克隆体的幻像有着相同的防御，但生命值和造成的伤害更低。\n\n虹光守卫将吸引敌人的火力从而保护使用者。\n\n当虹光守卫存在时阅读这张秘卷将会为其恢复所有生命。 | 物品描述 |
| actors.buffs.prismaticguard.name | 虹光守卫 | Buff名称 |
| actors.buffs.prismaticguard.desc | 你正在被一个目前看不见的虹光守卫所保护。当有敌人出现时这个虹光守卫将出现并为你战斗！\n\n当虹光守卫未激活时，它将逐渐恢复所有所受到的伤害。\n\n当前生命值：%d/%d | Buff描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_PRISIMG: 物品图标
- Assets.Sounds.READ: 阅读音效

### 中文翻译来源
- core/src/main/assets/messages/items/items_zh.properties
- core/src/main/assets/messages/actors/actors_zh.properties

## 11. 使用示例

### 基本用法

```java
// 阅读虹卫秘卷
ScrollOfPrismaticImage scroll = new ScrollOfPrismaticImage();
scroll.doRead();

// 首次使用：添加虹光守卫Buff
// 再次使用：治疗已存在的虹光幻像
```

### 效果说明

```java
// 如果没有虹光幻像：
Buff.affect(curUser, PrismaticGuard.class).set( PrismaticGuard.maxHP( curUser ) );
// 创建一个基于英雄属性的虹光守卫

// 如果有虹光幻像：
m.HP = m.HT; // 完全恢复生命值
m.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(m.HT), FloatingText.HEALING );
// 显示治疗特效
```

## 12. 开发注意事项

### 状态依赖
- 依赖 `Dungeon.level.mobs` 获取当前层怪物列表
- 依赖 `Stasis.getStasisAlly()` 检查静滞状态

### 生命周期耦合
- 虹光守卫Buff会在战斗中自动激活
- 虹光幻像死亡后需要重新使用秘卷

### 常见陷阱
1. **多个虹光幻像**：如果有多个虹光幻像，都会被治疗
2. **静滞状态**：静滞中的虹光幻像也会被检查和治疗
3. **Buff vs NPC**：虹光守卫是Buff，虹光幻像是NPC实体

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改虹光守卫的生命值计算
- 可添加额外效果（如临时增强）

### 不建议修改的位置
- 虹光幻像的治疗逻辑

### 重构建议
无重大重构建议

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是（仅 icon）
- [x] 是否已覆盖全部方法：是（仅 doRead）
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是