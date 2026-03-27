# Scroll API 参考

## 类声明
```java
public abstract class Scroll extends Item
```

## 类职责
Scroll是所有卷轴的抽象基类，提供阅读功能、随机符文系统、识别机制等。所有具体的卷轴实现都必须继承此类并实现`doRead()`方法。

## 关键字段

### 基础字段
- `AC_READ`: 标准行动常量，值为"READ"
- `TIME_TO_READ`: 阅读所需时间，值为1.0f
- `rune`: 当前卷轴的符文名称（如"KAUNAN"、"SOWILO"等）
- `talentFactor`: 影响卷轴相关天赋触发强度的系数，默认值为1
- `talentChance`: 卷轴触发天赋的概率（0-1范围），默认值为1
- `anonymous`: 标记卷轴是否为匿名状态，匿名卷轴总是被视为已识别且不影响识别状态

### 静态字段
- `runes`: 包含12种符文名称和对应精灵图的LinkedHashMap
- `handler`: ItemStatusHandler实例，用于管理卷轴的状态、识别和符文分配

## 标准行动常量
| 常量 | 值 | 说明 |
|------|-----|------|
| AC_READ | "READ" | 卷轴的标准使用行动 |

## 可重写方法

| 方法签名 | 返回值 | 默认行为 | 说明 |
|----------|--------|----------|------|
| `doRead()` | void | **抽象方法**，必须在子类中实现 | 执行卷轴的具体效果逻辑 |
| `readAnimation()` | void | 执行阅读动画：驱散隐身、消耗时间、播放动画、记录使用统计、触发天赋 | 处理阅读时的视觉和游戏逻辑 |
| `empower(Hero hero)` | void | **未在基类中定义**，某些子类可能实现 | 增强卷轴效果（特定子类） |

## 符文系统

Scroll类使用`ItemStatusHandler`实现随机符文系统：

1. **符文定义**：在静态代码块中定义了12种北欧符文：
   - KAUNAN, SOWILO, LAGUZ, YNGVI, GYFU, RAIDO
   - ISAZ, MANNAZ, NAUDIZ, BERKANAN, ODAL, TIWAZ

2. **初始化**：通过`initLabels()`方法初始化`ItemStatusHandler`
   ```java
   handler = new ItemStatusHandler<>((Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes);
   ```

3. **符文分配**：在构造函数中调用`reset()`方法，根据`ItemStatusHandler`为每个卷轴实例分配随机符文
   ```java
   image = handler.image(this);
   rune = handler.label(this);
   ```

4. **持久化**：通过`save()`、`restore()`等静态方法处理符文状态的保存和加载

5. **匿名处理**：匿名卷轴（通过`anonymize()`方法设置）会使用占位符精灵图`SCROLL_HOLDER`

## 识别系统

Scroll类实现了完整的物品识别机制：

### 识别状态检查
```java
public boolean isKnown() {
    return anonymous || (handler != null && handler.isKnown(this));
}
```

### 设置识别状态
```java
public void setKnown() {
    if (!anonymous) {
        if (!isKnown()) {
            handler.know(this);
            updateQuickslot();
        }
        
        if (Dungeon.hero.isAlive()) {
            Catalog.setSeen(getClass());
            Statistics.itemTypesDiscovered.add(getClass());
        }
    }
}
```

### 自动识别
- 调用`identify()`方法时自动设置为已知状态
- 阅读卷轴时自动调用`setKnown()`

### 名称和描述处理
- **未知卷轴**：显示符文名称而非实际名称（`Messages.get(this, rune)`）
- **未知描述**：显示通用未知描述（`Messages.get(this, "unknown_desc")`）
- **匿名卷轴**：跳过自定义笔记，直接显示基础描述

## 使用示例

### 示例1: 创建简单卷轴
```java
public class ScrollOfHealing extends Scroll {
    
    @Override
    public void doRead() {
        // 执行阅读动画
        readAnimation();
        
        // 实现具体效果
        curUser.HP = Math.min(curUser.HT, curUser.HP + 20);
        curUser.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
        
        // 设置为已知
        identify();
        
        // 更新UI
        Sample.INSTANCE.play(Assets.Sounds.HEALING);
    }
    
    @Override
    public String desc() {
        return "这卷轴包含了强大的愈合魔法，能恢复使用者的生命值。";
    }
}
```

### 示例2: 创建有目标选择的卷轴
```java
public class ScrollOfTeleportation extends Scroll {
    
    @Override
    public void doRead() {
        // 对于需要目标选择的卷轴，通常在doRead中启动目标选择器
        GameScene.selectCell(new Teleporter());
    }
    
    private static class Teleporter implements CellSelector.Listener {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                // 执行传送逻辑
                Dungeon.level.pressCell(cell);
                Dungeon.hero.pos = cell;
                Dungeon.observe();
                
                // 完成阅读动画和识别
                ((ScrollOfTeleportation)curItem).readAnimation();
                curItem.identify();
            }
        }
        
        @Override
        public String prompt() {
            return "选择要传送到的位置";
        }
    }
}
```

## 相关子类

### 基础卷轴类（12个）
- `ScrollOfUpgrade` - 升级物品
- `ScrollOfTransmutation` - 物品转换  
- `ScrollOfTerror` - 恐惧效果
- `ScrollOfTeleportation` - 传送
- `ScrollOfRetribution` - 报应伤害
- `ScrollOfRemoveCurse` - 移除诅咒
- `ScrollOfRecharging` - 充能
- `ScrollOfRage` - 愤怒狂暴
- `ScrollOfMirrorImage` - 镜像分身
- `ScrollOfMagicMapping` - 魔法地图
- `ScrollOfLullaby` - 催眠曲
- `ScrollOfIdentify` - 识别物品

### 异域卷轴类（12个）
- `ScrollOfSirensSong` - 海妖之歌
- `ScrollOfPsionicBlast` - 心灵爆破
- `ScrollOfPrismaticImage` - 棱镜镜像
- `ScrollOfPassage` - 通道
- `ScrollOfMysticalEnergy` - 神秘能量
- `ScrollOfMetamorphosis` - 变形
- `ScrollOfForesight` - 预知
- `ScrollOfEnchantment` - 附魔
- `ScrollOfDread` - 恐惧
- `ScrollOfDivination` - 占卜
- `ScrollOfChallenge` - 挑战
- `ScrollOfAntiMagic` - 反魔法

### 特殊类
- `InventoryScroll` - 库存卷轴（特殊用途）
- `ExoticScroll` - 异域卷轴基类
- `Scroll.PlaceHolder` - 卷轴占位符
- `Scroll.ScrollToStone` - 卷轴转石配方

## 常见错误

1. **忘记调用identify()**: 在`doRead()`实现中忘记调用`identify()`会导致卷轴不被标记为已知

2. **未调用readAnimation()**: 忘记调用`readAnimation()`会导致缺少阅读动画和天赋触发

3. **符文系统理解错误**: 直接修改`rune`字段而不是通过`ItemStatusHandler`会导致保存/加载问题

4. **匿名卷轴处理不当**: 对匿名卷轴调用`setKnown()`不会生效，需要在创建时就调用`anonymize()`

5. **继承错误**: 忘记实现抽象方法`doRead()`会导致编译错误

6. **资源管理**: 在异步操作（如目标选择）中需要正确管理`curItem`引用

7. **魔法免疫检查**: 忽略魔法免疫buff检查会导致在魔法免疫状态下仍能使用卷轴