# Key API 参考

## 类声明
```java
public abstract class Key extends Item
```

## 类职责
Key 类是 Shattered Pixel Dungeon 中所有钥匙物品的抽象基类。它定义了钥匙的基本行为，包括深度关联、唯一性约束、堆叠逻辑以及与游戏笔记系统的集成。由于是抽象类，不能直接实例化，必须通过具体的子类（如 IronKey、GoldenKey、CrystalKey 等）来创建实际的钥匙对象。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|-------|------|---------|-------|------|
| TIME_TO_UNLOCK | float | public static final | 1f | 开锁所需时间 |
| depth | int | public | - | 钥匙关联的地下城深度 |

## 构造方法
Key 类没有显式的构造方法，使用 Java 默认的无参构造方法，并通过实例初始化块设置默认属性。

## 实例初始化块
```java
{
    stackable = true;   // 钥匙可堆叠（相同深度的）
    unique = true;      // 唯一物品（死亡后保留）
}
```

## 可重写方法
| 方法签名 | 返回值 | 必须重写？ | 默认行为 | 说明 |
|---------|-------|----------|---------|------|
| isSimilar(Item item) | boolean | 否 | 检查物品是否为同类型且深度相同 | 判断钥匙是否相似（可堆叠） |

## 公开方法
Key 类继承了 Item 的所有公开方法，但重写了以下关键方法：

| 方法签名 | 返回值 | 说明 |
|---------|-------|------|
| doPickUp(Hero hero, int pos) | boolean | 执行拾取操作，集成笔记系统和统计数据 |
| storeInBundle(Bundle bundle) | void | 序列化深度信息到存档 |
| restoreFromBundle(Bundle bundle) | void | 从存档中恢复深度信息 |

## 生命周期
1. **创建**: 通过具体子类实例化 Key 对象，并设置 depth 字段
2. **生成**: 在指定深度的地牢中生成，depth 字段标识其归属
3. **拾取**: 英雄拾取时自动添加到笔记系统，更新统计数据
4. **存储**: 游戏保存时将 depth 信息序列化到 Bundle
5. **加载**: 游戏加载时从 Bundle 恢复 depth 信息
6. **使用**: 用于开启对应深度的门锁或宝箱
7. **保留**: 由于 unique = true，英雄死亡后钥匙会在遗骸中保留

## 与其他系统的交互
- **笔记系统 (Notes/Journal)**: 自动添加到游戏笔记，更新日记显示
- **统计数据 (Statistics)**: 将钥匙类型添加到 itemTypesDiscovered 集合
- **目录系统 (Catalog)**: 标记为已发现的物品类型
- **游戏场景 (GameScene)**: 更新钥匙显示界面，处理拾取动画
- **音效系统 (Sample)**: 播放物品拾取音效
- **遗物系统 (SkeletonKey)**: 与 SkeletonKey.KeyReplacementTracker 交互，处理多余钥匙
- **存档系统 (Bundle)**: 处理 depth 字段的序列化和反序列化
- **英雄系统 (Hero)**: 影响拾取延迟和背包管理

## 使用示例
### 示例1: 创建自定义钥匙类型

```java
package com.dustedpixel.dustedpixeldungeon.items.keys;

import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;

public class CustomKey extends Key {

    {
        image = ItemSpriteSheet.CUSTOM_KEY; // 自定义图像
        // 继承 stackable = true 和 unique = true
    }

    // 通常不需要重写其他方法，除非有特殊需求

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public int value() {
        return 0; // 钥匙通常没有金币价值
    }
}

// 使用示例
CustomKey key = new CustomKey();
key.depth =Dungeon.depth; // 设置关联的深度
key.

collect(); // 收集到英雄背包
```

### 示例2: 钥匙拾取和管理
```java
// 模拟钥匙生成
IronKey ironKey = new IronKey();
ironKey.depth = currentDepth; // 设置为当前深度

// 钥匙落在地上某个位置
ironKey.drop(targetCell);

// 英雄移动到该位置并拾取
if (Dungeon.hero.pos == targetCell) {
    // doPickUp 会自动处理：
    // 1. 添加到笔记系统
    // 2. 更新统计数据
    // 3. 播放音效
    // 4. 更新钥匙显示
    ironKey.doPickUp(Dungeon.hero, targetCell);
}

// 检查是否有相同深度的钥匙可以堆叠
for (Item item : Dungeon.hero.belongings.backpack.items) {
    if (item instanceof Key && ((Key)item).depth == ironKey.depth) {
        // 相同深度的钥匙会自动堆叠
        break;
    }
}
```

### 示例3: 存档处理
```java
// 游戏保存时
Bundle bundle = new Bundle();
key.storeInBundle(bundle); // 保存 depth 信息

// 游戏加载时  
Bundle savedBundle = loadSavedBundle();
Key loadedKey = new IronKey();
loadedKey.restoreFromBundle(savedBundle); // 恢复 depth 信息
System.out.println("Loaded key for depth: " + loadedKey.depth);
```

## 相关子类
Key 类的具体实现子类包括：

- **IronKey**: 铁钥匙，用于开启普通上锁的门
- **GoldenKey**: 金钥匙，用于开启宝箱
- **CrystalKey**: 水晶钥匙，用于开启特殊房间或机制
- **SkeletalKey**: 骷髅钥匙，特殊的高价值钥匙（注意与 SkeletonKey 遗物区分）

每个子类通常只设置不同的图像和描述，核心逻辑由 Key 基类提供。

## 常见错误
1. **忘记设置 depth 字段**: 创建钥匙实例后忘记设置 depth，导致钥匙无法正常使用或在错误深度出现

2. **错误的堆叠逻辑**: 修改 isSimilar() 方法但没有正确处理 depth 比较，导致不同深度的钥匙错误堆叠

3. **遗漏存档处理**: 添加新字段但忘记在 storeInBundle() 和 restoreFromBundle() 中处理，导致存档数据丢失

4. **忽略笔记系统集成**: 在自定义拾取逻辑中绕过 doPickUp() 方法，导致笔记系统和统计数据不更新

5. **unique 属性误解**: 修改 unique 属性但没有理解其对死亡后物品保留的影响

6. **抽象类实例化**: 尝试直接实例化 Key 类而不是具体子类，导致编译错误

7. **深度验证缺失**: 在使用钥匙时没有验证 depth 是否匹配目标锁的深度要求

8. **并发修改问题**: 在遍历钥匙列表时修改列表内容而没有正确处理迭代器

9. **图像资源遗漏**: 设置了自定义 image 字段但没有在 ItemSpriteSheet 中添加对应的图像资源

10. **价值计算错误**: 为不应该有金币价值的钥匙设置了非零 value() 返回值