# InventoryScroll 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/InventoryScroll.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.items.scrolls |
| 类类型 | abstract class |
| 继承关系 | extends Scroll |
| 代码行数 | 148 |

## 2. 类职责说明
InventoryScroll 是需要选择物品的卷轴的抽象基类。当使用此类卷轴时，会打开物品选择界面，让玩家选择一个物品来应用卷轴效果。这包括鉴定卷轴、升级卷轴、解咒卷轴等。该类处理了物品选择的通用逻辑，包括未鉴定时使用的选择确认流程。

## 4. 继承与协作关系
```mermaid
classDiagram
    class Scroll {
        <<abstract>>
        +doRead(): void
        +readAnimation(): void
        +isKnown(): boolean
    }
    
    class InventoryScroll {
        <<abstract>>
        #identifiedByUse: boolean
        #preferredBag: Class~Bag~
        +doRead(): void
        #usableOnItem(Item): boolean
        #onItemSelected(Item)*: void
        -confirmCancelation(): void
        -inventoryTitle(): String
    }
    
    class WndConfirmCancel {
        +onSelect(int): void
        +onBackPressed(): void
        +getItemSelector(): ItemSelector
    }
    
    class ItemSelector {
        +textPrompt(): String
        +preferredBag(): Class~Bag~
        +itemSelectable(Item): boolean
        +onSelect(Item): void
    }
    
    Scroll <|-- InventoryScroll
    InventoryScroll +-- WndConfirmCancel
    InventoryScroll +-- ItemSelector
    
    note for InventoryScroll "需要选择物品的卷轴基类\n打开物品选择界面\n处理未鉴定时的确认流程"
```

## 静态常量表
| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| 无 | - | - | 本类无静态常量 |

## 实例字段表
| 字段名 | 类型 | 修饰符 | 说明 |
|--------|------|--------|------|
| identifiedByUse | boolean | protected static | 是否通过使用鉴定（用于取消确认） |
| preferredBag | Class<? extends Bag> | protected | 优先显示的背包类型 |
| itemSelector | WndBag.ItemSelector | private | 物品选择器 |

## 7. 方法详解

### doRead()
**签名**: `@Override public void doRead()`
**功能**: 执行阅读卷轴的逻辑，打开物品选择界面
**实现逻辑**:
```java
// 第39-50行
if (!isKnown()) {
    // 未鉴定时：先鉴定，再从背包分离
    identify();
    curItem = detach(curUser.belongings.backpack);
    identifiedByUse = true;
} else {
    identifiedByUse = false;
}

// 打开物品选择界面
GameScene.selectItem(itemSelector);
```
- 未鉴定时先鉴定再分离物品
- 打开物品选择界面让玩家选择

### usableOnItem(Item item)
**签名**: `protected boolean usableOnItem(Item item)`
**功能**: 检查物品是否可以被此卷轴使用
**参数**:
- item: Item - 待检查的物品
**返回值**: boolean - 是否可用
**实现逻辑**:
```java
// 第91-93行
return true; // 默认所有物品可用
```
- 子类应重写此方法实现具体筛选逻辑

### onItemSelected(Item item)
**签名**: `protected abstract void onItemSelected(Item item)`
**功能**: 当玩家选择物品后执行的效果
**参数**:
- item: Item - 被选中的物品
**说明**: 抽象方法，由子类实现具体效果

### itemSelector.onSelect(Item item)
**签名**: `public void onSelect(Item item)`
**功能**: 物品选择器的回调方法
**参数**:
- item: Item - 被选中的物品（null表示取消）
**实现逻辑**:
```java
// 第115-146行
if (item != null) {
    // 升级卷轴打开单独窗口，其他卷轴直接分离
    if (!identifiedByUse && !(curItem instanceof ScrollOfUpgrade)) {
        curItem = detach(curUser.belongings.backpack);
    }
    
    // 执行选择效果
    ((InventoryScroll)curItem).onItemSelected(item);

    // 非升级卷轴播放动画和音效
    if (!(curItem instanceof ScrollOfUpgrade)) {
        ((InventoryScroll)curItem).readAnimation();
        Sample.INSTANCE.play(Assets.Sounds.READ);
    }
    
} else if (identifiedByUse && !((Scroll)curItem).anonymous) {
    // 未鉴定时取消需要确认
    ((InventoryScroll)curItem).confirmCancelation();
    
} else if (((Scroll)curItem).anonymous) {
    // 匿名卷轴取消直接消耗时间
    curUser.spendAndNext(TIME_TO_READ);
}
```

### confirmCancelation()
**签名**: `private void confirmCancelation()`
**功能**: 显示取消确认窗口
**实现逻辑**:
```java
// 第52-54行
GameScene.show(new WndConfirmCancel());
```
- 未鉴定时使用后取消需要确认，因为卷轴已经被消耗

## 11. 使用示例

### 创建自定义物品选择卷轴
```java
public class ScrollOfExample extends InventoryScroll {
    
    {
        icon = ItemSpriteSheet.Icons.SCROLL_EXAMPLE;
        preferredBag = Belongings.Backpack.class;
    }
    
    @Override
    protected boolean usableOnItem(Item item) {
        // 只能用于未鉴定的物品
        return !item.isIdentified();
    }
    
    @Override
    protected void onItemSelected(Item item) {
        // 鉴定选中的物品
        item.identify();
        GLog.i("物品已鉴定：" + item.name());
    }
}
```

### 使用物品选择卷轴
```java
// 创建并使用鉴定卷轴
ScrollOfIdentify scroll = new ScrollOfIdentify();
scroll.execute(hero, Scroll.AC_READ);

// 流程：
// 1. 打开物品选择界面
// 2. 玩家选择一个未鉴定的物品
// 3. 物品被鉴定
// 4. 播放动画和音效
```

## 注意事项

1. **未鉴定使用**: 未鉴定的卷轴使用后会自动鉴定，取消时需要确认

2. **升级卷轴特殊处理**: 升级卷轴打开单独窗口，可以取消

3. **物品筛选**: 重写 usableOnItem() 限制可选物品

4. **优先背包**: preferredBag 决定默认显示哪个背包

## 最佳实践

1. 继承此类实现需要选择物品的卷轴

2. 重写 usableOnItem() 限制可选物品范围

3. 实现 onItemSelected() 定义具体效果

4. 设置 preferredBag 指定优先显示的背包