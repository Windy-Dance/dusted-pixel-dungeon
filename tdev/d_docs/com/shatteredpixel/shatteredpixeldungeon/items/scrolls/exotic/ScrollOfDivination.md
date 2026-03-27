# ScrollOfDivination 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfDivination.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic |
| **文件类型** | class |
| **继承关系** | extends ExoticScroll |
| **代码行数** | 155 行 |
| **所属模块** | core |
| **官方中文名** | 预知秘卷 |

## 2. 文件职责说明

### 核心职责
预知秘卷是一种阅读型秘卷，阅读后随机鉴定4种未鉴定的物品类型（药剂颜色、卷轴符文或戒指宝石）。

### 系统定位
作为鉴定卷轴的升级版本，对应普通卷轴为鉴定卷轴（ScrollOfIdentify）。

### 不负责什么
- 不鉴定具体物品实例
- 不鉴定装备属性

## 3. 结构总览

### 主要成员概览
- `icon`: 图标标识
- `WndDivination`: 内部窗口类

### 主要逻辑块概览
- `doRead()`: 阅读效果，随机鉴定物品类型
- `WndDivination`: 显示鉴定结果的窗口

## 4. 继承与协作关系

### 父类提供的能力
从 ExoticScroll 继承：
- 鉴定状态共享机制
- 价值计算（基于鉴定卷轴 +30金币）
- 符文和图像设置

### 覆写的方法
| 方法 | 覆写目的 |
|------|----------|
| `doRead()` | 实现阅读效果：随机鉴定物品类型 |

### 依赖的关键类
- `Potion`: 药剂类
- `Scroll`: 卷轴类
- `Ring`: 戒指类
- `Reflection`: 反射工具

## 5. 字段/常量详解

### 实例字段

| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `icon` | int | ItemSpriteSheet.Icons.SCROLL_DIVINATE | 物品图标标识 |

## 6. 构造与初始化机制

### 构造器
使用默认构造器，通过初始化块设置图标。

### 初始化块
```java
{
    icon = ItemSpriteSheet.Icons.SCROLL_DIVINATE;
}
```

## 7. 方法详解

### doRead()

**可见性**：public

**是否覆写**：是，覆写自 ExoticScroll

**方法职责**：实现阅读效果，随机鉴定4种未鉴定的物品类型。

**核心实现逻辑**：
```java
@Override
public void doRead() {
    detach(curUser.belongings.backpack);
    curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
    
    Sample.INSTANCE.play( Assets.Sounds.READ );
    
    HashSet<Class<? extends Potion>> potions = Potion.getUnknown();
    HashSet<Class<? extends Scroll>> scrolls = Scroll.getUnknown();
    HashSet<Class<? extends Ring>> rings = Ring.getUnknown();
    
    int total = potions.size() + scrolls.size() + rings.size();
    
    ArrayList<Item> IDed = new ArrayList<>();
    int left = 4;
    
    // 随机选择4种未鉴定物品类型进行鉴定
    while (left > 0 && total > 0) {
        // ... 随机选择逻辑
    }

    if (left == 4){
        GLog.n( Messages.get(this, "nothing_left") );
    } else {
        GameScene.show(new WndDivination(IDed));
    }

    readAnimation();
    identify();
}
```

**边界情况**：
- 如果没有任何未鉴定物品，显示提示消息
- 鉴定数量最多为4种

## 8. 对外暴露能力

### 显式 API
- `doRead()`: 阅读效果

### 内部辅助方法
- 内部类 `WndDivination` 的所有方法

## 9. 运行机制与调用链

### 创建时机
- 通过炼金转换（鉴定卷轴 + 6能量）
- 通过 Generator 随机生成

### 系统流程位置
```
阅读 → doRead() → 获取未鉴定列表 → 随机选择4种 → 
鉴定 → 显示结果窗口
```

## 10. 资源、配置与国际化关联

### 引用的 messages 文案

| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.scrolls.exotic.scrollofdivination.name | 预知秘卷 | 物品名称 |
| items.scrolls.exotic.scrollofdivination.nothing_left | 没有可以鉴定的道具了！ | 无未鉴定物品提示 |
| items.scrolls.exotic.scrollofdivination.desc | 这张秘卷会随机鉴定四种你尚未明确的道具。它可能帮你鉴定出某药剂的颜色，某卷轴的符文，或是某戒指的宝石。不过被鉴定的道具未必是你包裹中有的。 | 物品描述 |
| items.scrolls.exotic.scrollofdivination$wnddivination.desc | 你的预知秘卷鉴定了下列道具： | 结果窗口描述 |

### 依赖的资源
- ItemSpriteSheet.Icons.SCROLL_DIVINATE: 物品图标
- Assets.Sounds.READ: 阅读音效
- Identification: 鉴定视觉效果

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 11. 使用示例

### 基本用法

```java
// 阅读预知秘卷
ScrollOfDivination scroll = new ScrollOfDivination();
scroll.doRead(); // 随机鉴定4种未鉴定物品类型

// 鉴定结果示例：
// - 治疗药剂的颜色（猩红）
// - 升级卷轴的符文（KAUNAN）
// - 力量之戒的宝石（钻石）
// - 隐形药剂的颜色（碧绿）
```

### 鉴定概率

```java
// 基础概率：药剂、卷轴、戒指各3
// 选择后对应概率减1
// 某类物品鉴定完毕后概率置0
```

## 12. 开发注意事项

### 状态依赖
- 鉴定结果随机，不保证鉴定玩家需要的物品

### 生命周期耦合
- 鉴定结果持久保存

### 常见陷阱
1. **随机性**：鉴定的物品类型随机，可能鉴定玩家不需要的
2. **不鉴定实例**：只鉴定物品类型，不鉴定具体物品属性

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可修改鉴定数量
- 可修改选择概率

### 不建议修改的位置
- 随机选择的核心逻辑

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是
- [x] 是否遗漏资源/配置/本地化关联：否
- [x] 是否明确说明了注意事项与扩展点：是