# ScrollOfDread 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfDread.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 62 行 |
| **所属模块** | core |
| **官方中文名** | 梦魇秘卷 |

## 2. 文件职责说明

### 核心职责
梦魇秘卷是一种阅读型秘卷，阅读后使视野内所有敌人陷入梦魇状态，导致它们逃离地牢。

### 系统定位
作为恐惧卷轴的升级版本，对应普通卷轴为恐惧卷轴（ScrollOfTerror）。

### 不负责什么
- 不对免疫梦魇的敌人生效（改为施加恐惧）

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识

### 主要逻辑块概览
- `doRead()`: 阅读效果，施加梦魇或恐惧

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算（基于恐惧卷轴 +30金币）
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读效果：施加梦魇或恐惧 |

### 依赖的关键类
- `Dread`: 梦魇Buff类
- `Terror`: 恐惧Buff类
- `Mob`: 怪物类

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_DREAD | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_DREAD;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读效果，使视野内所有敌人陷入梦魇或恐惧状态。

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
    Sample.INSTANCE.play( Assets.Sounds.READ );

    for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
        if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
            if (!mob.isImmune(Dread.class)){
                Buff.affect( mob, Dread.class ).object = curUser.id();
            } else {
                Buff.affect( mob, Terror.class, Terror.DURATION ).object = curUser.id();
            }
        }
    }

    identify();
    
    readAnimation();
}
```

**边界情况**：
- 免疫梦魇的敌人改为受到恐惧效果
- 盟友不受影响

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读效果

### 内部辅助方法
无

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（恐惧卷轴 + 6能量）
- 通过 Generator 随机生成

### 系统流程位置
```
阅读 → doRead() → 遍历视野内敌人 → 
检查免疫 → 施加梦魇或恐惧
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofdread.name | 梦魇秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofdread.desc | 诵读的时候，梦魇秘卷会爆射出一道极度可怖的红色闪光，其中的杀气仿佛已经凝成实体。这足以令你视野中所有的敌人吓得魂飞魄散，不顾一切地想要逃离这座地牢，永远也不回来了！\n\n与恐惧效果一样，逃命的敌人也会随着时间流逝逐渐冷静下来，来自外界的伤害更能加速这一过程。\n\n意志坚定的敌人，比如Boss们，将能抵抗逃命的冲动，但仍会感到恐惧。 | 物品描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_DREAD: 物品图标
- Assets.Sounds.READ: 阅读音效
- Flare: 视觉效果（红色）

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 阅读梦魇秘卷
ScrollOfDread scroll = new ScrollOfDread();
scroll.doRead(); // 使视野内所有敌人陷入梦魇或恐惧

// 效果
// 普通敌人：梦魇状态（逃离地牢）
// 免疫梦魇的敌人：恐惧状态（暂时逃跑）
```

### 梦魇 vs 恐惧

```java
// 梦魇：敌人永久逃离地牢
// 恐惧：敌人暂时逃跑，之后可能返回
```

## 12. 开发注意事项

### 状态依赖
- 梦魇免疫的敌人改为受到恐惧

### 生命周期耦合
- 梦魇效果持续直到敌人逃离或被攻击

### 常见陷阱
1. **Boss免疫**：Boss通常免疫梦魇，但仍受恐惧影响
2. **攻击中断**：攻击梦魇中的敌人可能使其恢复

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可添加额外的视觉效果

### 不建议修改的位置
- 梦魇/恐惧的选择逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是