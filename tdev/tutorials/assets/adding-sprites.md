# 添加精灵图教程

## 目标
完成本教程后，你将能够添加自定义精灵图到游戏中。

## 前置知识
- 了解像素艺术基础
- 了解精灵图概念

## 最终成果
为自定义物品添加精灵图。

## 步骤

### 步骤1：创建精灵图
**目标**：创建符合规格的精灵图
**规格**：物品精灵图为16x16像素

精灵图（Sprite）是游戏开发中常用的技术，它将多个小图像组合在一张大图片中，以提高渲染效率。在Shattered Pixel Dungeon中，所有物品精灵图都存储在单个文件 `items.png` 中。

**创建要求**：
- 物品精灵图必须是 **16x16像素** 的正方形
- 使用透明背景（PNG格式支持Alpha通道）
- 遵循游戏的像素艺术风格
- 确保精灵图在16x16网格内居中或适当对齐

**工具推荐**：
- Aseprite（付费，专业的像素艺术软件）
- GraphicsGale（免费）
- GIMP（免费，功能强大）
- Paint.NET（免费，Windows平台）

**示例**：
如果你要创建一个药水物品的精灵图，确保它是16x16像素，在中心位置绘制药水瓶，周围留有适当的边距。

### 步骤2：添加到精灵图表
**目标**：将精灵图添加到items.png

精灵图表文件位于：
```
D:\Develop\Workspace\DustedPixelDungeon\core\src\main\assets\sprites\items.png
```

**操作步骤**：
1. 打开 `items.png` 文件（这是一个256x512像素的大图，包含多个16x16的格子）
2. 找到一个空的16x16格子（通常在文件末尾或未使用的区域）
3. 将你创建的16x16精灵图粘贴到这个空格子中
4. 保存文件

**重要注意事项**：
- 不要覆盖现有的精灵图
- 确保精灵图完全对齐到16x16的网格边界
- 保持PNG格式和透明背景
- 记录下你的精灵图在网格中的位置（行号和列号，从1开始计数）

**网格坐标系统**：
- 图表宽度：256像素 ÷ 16像素 = 16列
- 图表高度：512像素 ÷ 16像素 = 32行
- 每个格子可以用坐标 (x, y) 表示，其中 x 是列号（1-16），y 是行号（1-32）

### 步骤3：在ItemSpriteSheet中注册
**目标**：分配精灵图索引
**文件位置**：
```
D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\sprites\ItemSpriteSheet.java
```

在 `ItemSpriteSheet.java` 文件中，你需要：

1. **定义常量**：在合适的位置添加你的精灵图常量
2. **分配坐标**：使用 `xy()` 函数计算网格位置
3. **注册尺寸**：使用 `assignItemRect()` 注册精灵图的尺寸

**代码示例**：
```java
// 在文件的适当位置（通常是按类别分组的地方）添加
private static final int MY_CUSTOM_ITEMS = xy(1, 33); // 假设你的精灵图在第33行第1列

public static final int MY_CUSTOM_POTION = MY_CUSTOM_ITEMS + 0;
public static final int MY_CUSTOM_SCROLL = MY_CUSTOM_ITEMS + 1;

static{
    assignItemRect(MY_CUSTOM_POTION, 12, 14); // 药水通常是12x14像素
    assignItemRect(MY_CUSTOM_SCROLL, 15, 14); // 卷轴通常是15x14像素
}
```

**关键函数说明**：
- `xy(x, y)`：将网格坐标转换为线性索引。参数 x 和 y 从1开始计数
- `assignItemRect(itemIndex, width, height)`：为指定的精灵图索引分配实际的绘制尺寸

**常见物品尺寸参考**：
- 药水：12x14像素
- 卷轴：15x14像素  
- 戒指：8x10像素
- 武器：13-16x13-16像素（根据具体武器类型）
- 食物：15-16x11-12像素

### 步骤4：在物品中使用
**目标**：设置物品的image字段

在你的自定义物品类中，需要设置 `image` 字段来引用你注册的精灵图索引。

**代码示例**：

```java
package com.dustedpixel.dustedpixeldungeon.items;

import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;

public class MyCustomPotion extends Potion {

    {
        // 设置精灵图索引
        image = ItemSpriteSheet.MY_CUSTOM_POTION;
    }

    // ... 其他物品逻辑
}
```

**完整示例**：

```java
package com.dustedpixel.dustedpixeldungeon.items.potions;

import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfHealing extends Potion {

    {
        image = ItemSpriteSheet.POTION_CRIMSON;
    }

    @Override
    public void apply(Hero hero) {
        // 药水效果逻辑
        // ...
    }
}
```

## 完整代码

**ItemSpriteSheet.java 修改示例**：
```java
// 在文件末尾或其他合适位置添加
private static final int CUSTOM_ITEMS = xy(1, 33); // 第33行开始

public static final int POTION_MY_CUSTOM = CUSTOM_ITEMS + 0;
public static final int SCROLL_MY_CUSTOM = CUSTOM_ITEMS + 1;

static{
    assignItemRect(POTION_MY_CUSTOM, 12, 14);
    assignItemRect(SCROLL_MY_CUSTOM, 15, 14);
}
```

**自定义物品类示例**：

```java
package com.dustedpixel.dustedpixeldungeon.items.potions;

import com.dustedpixel.dustedpixeldungeon.actors.hero.Hero;
import com.dustedpixel.dustedpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfMyCustom extends Potion {

    {
        image = ItemSpriteSheet.POTION_MY_CUSTOM;
    }

    @Override
    public void apply(Hero hero) {
        // 实现你的自定义效果
        identify();

        // 示例效果：恢复一些生命值
        hero.HP = Math.min(hero.HT, hero.HP + 10);
        hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
    }
}
```

## 测试验证

**测试步骤**：
1. **编译项目**：确保没有编译错误
   ```bash
   ./gradlew build
   ```

2. **运行游戏**：启动游戏并找到你的物品
   - 可以通过控制台命令生成物品进行测试
   - 或者在游戏中正常获得物品

3. **验证显示**：
   - 精灵图是否正确显示（不是默认的占位符）
   - 尺寸是否正确（没有被拉伸或裁剪）
   - 透明背景是否正常工作

4. **调试技巧**：
   - 如果显示为红色问号，说明精灵图索引未正确注册
   - 如果显示为默认占位符，检查 `image` 字段是否正确设置
   - 使用调试模式查看物品信息确认精灵图索引

## 常见问题

**Q1: 我的精灵图显示为红色问号怎么办？**
- 检查 `ItemSpriteSheet.java` 中是否正确注册了常量和 `assignItemRect` 调用
- 确认网格坐标计算正确（xy函数的参数从1开始）
- 验证 `items.png` 中对应位置确实有你的精灵图

**Q2: 精灵图显示但尺寸不对怎么办？**
- 检查 `assignItemRect` 中的 width 和 height 参数
- 确保精灵图实际绘制的内容与注册的尺寸匹配
- 参考同类物品的尺寸设置

**Q3: 如何找到空的网格位置？**
- 打开 `items.png` 文件，从底部开始寻找空白的16x16区域
- 查看 `ItemSpriteSheet.java` 文件，找到最后一个使用的区域
- 通常可以安全地在现有分组之后添加新的分组

**Q4: 精灵图有白色背景而不是透明怎么办？**
- 确保在图像编辑软件中使用透明背景（Alpha通道）
- 保存为PNG格式时保留透明度
- 检查图像编辑软件的导出设置

**Q5: 如何批量添加多个精灵图？**
- 在 `ItemSpriteSheet.java` 中定义连续的常量
- 使用数组或循环简化 `assignItemRect` 调用（参考已有的代码模式）
- 确保所有精灵图都在 `items.png` 中按顺序排列

**Q6: 游戏启动时报错 "TextureFilm out of bounds" 怎么办？**
- 检查网格坐标是否超出了 `items.png` 的范围（最大32行×16列）
- 确认 `TX_WIDTH` 和 `TX_HEIGHT` 常量与实际图片尺寸匹配
- 验证 `xy()` 函数的计算结果是否合理

通过遵循本教程的步骤，你应该能够成功地为Shattered Pixel Dungeon添加自定义精灵图和物品。