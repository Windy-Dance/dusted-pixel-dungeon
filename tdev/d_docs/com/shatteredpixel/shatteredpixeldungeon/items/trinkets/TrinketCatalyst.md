# TrinketCatalyst 物品文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/trinkets/TrinketCatalyst.java |
| **包名** | com.shatteredpixel.shatteredpixeldungeon.items.trinkets |
| **文件类型** | class |
| **继承关系** | extends Item |
| **代码行数** | 265 |
| **所属模块** | core |

## 2. 文件职责说明

### 核心职责
魔能触媒是泛着微光的金色粉尘饰物，在暗无边际的地牢中熠熠生辉。它可被置入炼金釜中，结合少量炼金能量制成一件独特的饰物。

### 系统定位
作为饰物系统的入口物品，魔能触媒是制作所有饰物的原料。

### 不负责什么
- 不直接提供饰物效果
- 不处理饰物的升级

## 3. 结构总览

### 主要成员概览
- **图像索引**：ItemSpriteSheet.TRINKET_CATA
- **实例字段**：rolledTrinkets
- **覆写方法**：isIdentified()、isUpgradable()、doPickUp()、storeInBundle()、restoreFromBundle()
- **内部类**：Recipe、RandomTrinket、WndTrinket

### 主要逻辑块概览
- 饰物选择窗口
- 饰物随机生成
- 序列化支持

### 生命周期/调用时机
- 拾取时：提示阅读炼金指南
- 炼金时：显示饰物选择窗口

## 4. 继承与协作关系

### 父类提供的能力
从Item继承所有功能。

### 覆写的方法
| 方法 | 说明 |
|------|------|
| isIdentified() | 始终返回true |
| isUpgradable() | 始终返回false |
| doPickUp(Hero, int) | 拾取时提示阅读指南 |
| storeInBundle(Bundle) | 序列化rolledTrinkets |
| restoreFromBundle(Bundle) | 反序列化 |

### 依赖的关键类
- `Assets`、`Badges`、`Dungeon`、`Statistics`：游戏状态
- `Generator`：随机生成
- `Hero`：英雄
- `Item`、`Trinket`：物品类
- `Catalog`、`Document`：日志系统
- `Messages`：本地化文本
- `AlchemyScene`、`GameScene`、`PixelScene`：场景
- `ItemSprite`、`ItemSpriteSheet`：视觉
- `ItemButton`、`RedButton`、`RenderedTextBlock`、`Window`：UI
- `GLog`：日志
- `WndInfoItem`、`WndSadGhost`：窗口
- `Bundle`：序列化

### 使用者
- 炼金系统
- 掉落系统

## 5. 字段/常量详解

### 静态常量
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| ROLLED_TRINKETS | String | "rolled_trinkets" | Bundle键名 |

### 实例字段
| 字段名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| rolledTrinkets | ArrayList\<Trinket\> | 空 | 已随机的饰物列表 |
| unique | boolean | true | 唯一物品 |
| image | int | ItemSpriteSheet.TRINKET_CATA | 图像索引 |

## 6. 构造与初始化机制

### 构造器
无显式构造器。

### 初始化块
```java
{
    image = ItemSpriteSheet.TRINKET_CATA;
    unique = true;
}
```

## 7. 方法详解

### isIdentified()

**可见性**：public

**是否覆写**：是，覆写自Item

**返回值**：boolean，始终返回true

---

### isUpgradable()

**可见性**：public

**是否覆写**：是，覆写自Item

**返回值**：boolean，始终返回false

---

### doPickUp(Hero hero, int pos)

**可见性**：public

**是否覆写**：是，覆写自Item

**方法职责**：拾取时提示阅读炼金指南

**核心实现逻辑**：
```java
@Override
public boolean doPickUp(Hero hero, int pos) {
    if (super.doPickUp(hero, pos)){
        if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_ALCHEMY)){
            GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_ALCHEMY);
        }
        return true;
    } else {
        return false;
    }
}
```

---

### hasRolledTrinkets()

**可见性**：public

**方法职责**：检查是否已有随机饰物

**返回值**：boolean，rolledTrinkets非空时返回true

## 8. 内部类详解

### Recipe

**类型**：extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe

**职责**：定义魔能触媒的炼金配方

**主要方法**：

#### testIngredients(ArrayList\<Item\> ingredients)
检查是否为单个TrinketCatalyst。

#### cost(ArrayList\<Item\> ingredients)
返回6，表示消耗6点炼金能量。

#### brew(ArrayList\<Item\> ingredients)
- 重新添加催化剂（防止玩家退出时丢失）
- 显示饰物选择窗口WndTrinket
- 保存游戏

#### sampleOutput(ArrayList\<Item\> ingredients)
返回Trinket.PlaceHolder作为预览。

---

### RandomTrinket

**类型**：extends Item

**职责**：表示随机饰物选项

**图像**：ItemSpriteSheet.SOMETHING

---

### WndTrinket

**类型**：extends Window

**职责**：饰物选择窗口

**常量**：
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| WIDTH | int | 120 | 窗口宽度 |
| BTN_SIZE | int | 24 | 按钮尺寸 |
| BTN_GAP | int | 4 | 按钮间距 |
| GAP | int | 2 | 间距 |
| NUM_TRINKETS | int | 4 | 可选饰物数量 |

**主要逻辑**：
1. 显示标题和说明文字
2. 随机生成4个饰物选项
3. 点击饰物显示详细信息窗口
4. 确认后获得选择的饰物

## 9. 对外暴露能力

### 显式 API
| 方法 | 说明 |
|------|------|
| hasRolledTrinkets() | 检查是否已有随机饰物 |

## 10. 运行机制与调用链

### 调用时机
- 拾取时提示阅读指南
- 放入炼金釜时显示选择窗口

### 系统流程位置
```
拾取TrinketCatalyst
    ↓
提示阅读炼金指南（如未读）
    ↓
放入炼金釜
    ↓
消耗6点炼金能量
    ↓
显示WndTrinket选择窗口
    ↓
玩家选择4个饰物之一
    ↓
获得选择的饰物
```

## 11. 资源、配置与国际化关联

### 引用的 messages 文案
| 键名 | 中文翻译 | 用途 |
|------|---------|------|
| items.trinkets.trinketcatalyst.name | 魔能触媒 | 名称 |
| items.trinkets.trinketcatalyst.window_title | 制作一件饰物 | 窗口标题 |
| items.trinkets.trinketcatalyst.window_text | 随着魔能触媒的置入，釜中的水逐渐泛起微光... | 窗口说明 |
| items.trinkets.trinketcatalyst.desc | 这团泛着微光的金色粉尘在暗无边际的地牢中熠熠生辉... | 描述 |
| items.trinkets.trinketcatalyst$randomtrinket.name | 随机饰物 | 随机选项名称 |
| items.trinkets.trinketcatalyst$randomtrinket.desc | 就近的一件物品被塞进了一个你打不开的包裹内... | 随机选项描述 |

### 中文翻译来源
core/src/main/assets/messages/items/items_zh.properties

## 12. 使用示例

### 基本用法

```java
// 在炼金釜中使用
TrinketCatalyst catalyst = new TrinketCatalyst();
// Recipe.brew()会显示选择窗口
// 玩家选择后获得饰物
```

## 13. 开发注意事项

### 状态依赖
- 唯一物品，不可堆叠
- 已鉴定的，不可升级

### 常见陷阱
1. **退出游戏处理**：催化剂会被重新添加，防止丢失
2. **选择持久化**：已随机的饰物列表会被保存

## 14. 事实核查清单

- [x] 是否已覆盖全部字段：是
- [x] 是否已覆盖全部方法：是
- [x] 是否已检查继承链与覆写关系：是，extends Item
- [x] 是否已核对官方中文翻译：是
- [x] 是否存在任何推测性表述：否
- [x] 示例代码是否真实可用：是