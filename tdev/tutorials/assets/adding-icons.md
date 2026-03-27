# 添加图标教程

## 目标
本教程将指导你如何添加新的图标资源。

## 前置知识
- 了解像素画基础
- 熟悉项目资源结构

---

## 第一部分：图标系统

### 图标类型

| 类型 | 尺寸 | 用途 |
|------|------|------|
| 英雄图标 | 16x16 | 角色选择界面 |
| Buff 图标 | 7x7 | 状态效果指示器 |
| UI 图标 | 可变 | 按钮、标签等 |
| 物品图标 | 16x16 | 物品显示 |

---

## 第二部分：Buff 图标

### 步骤 1：创建图标图像

创建一个 7x7 像素的图标，保存到 `core/assets/icons/buff.png`。

### 步骤 2：注册图标

在 `BuffIndicator.java` 中：

```java
public static final int CUSTOM_BUFF = 50;

// 在静态块中添加
private static final int[] ICONS = {
    // ... 现有图标
    CUSTOM_BUFF
};

// 图标坐标
private static final Rect CUSTOM_BUFF_RECT = new Rect(350, 0, 357, 7);
```

### 步骤 3：使用图标

```java
public class CustomBuff extends Buff {
    
    @Override
    public int icon() {
        return BuffIndicator.CUSTOM_BUFF;
    }
}
```

---

## 第三部分：英雄图标

### 步骤 1：创建图标

创建一个 16x16 像素的英雄图标。

### 步骤 2：注册图标

在 `HeroIcon.java` 中：

```java
public static final int CUSTOM_HERO = 20;

private static final int[] HERO_ICONS = {
    // ... 现有图标
    CUSTOM_HERO
};
```

---

## 第四部分：UI 图标

### Icons 类

```java
public class Icons {
    
    public static final int CUSTOM_ICON = 100;
    
    private static final TextureFilm ICONS = new TextureFilm(
        Assets.Interfaces.ICONS, 16, 16
    );
    
    public static Image get(int index) {
        return new Image(ICONS.get(index));
    }
}
```

---

## 注意事项

1. **尺寸一致**: 确保图标尺寸与现有图标匹配
2. **透明背景**: 使用透明背景以便图标叠加
3. **颜色对比**: 确保图标在各种背景下都清晰可见
4. **测试不同分辨率**: 在不同屏幕密度下测试图标效果

---

## 相关资源

- [添加精灵图教程](adding-sprites.md)
- [资源管线指南](../../integration/asset-pipeline.md)