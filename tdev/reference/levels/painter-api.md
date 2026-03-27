# Painter API 参考

## 类声明
public abstract class Painter

## 类职责
Painter是房间装饰的抽象基类，负责将房间的抽象定义转换为具体的地形数据。

## 抽象方法
| 方法签名 | 返回值 | 说明 |
|---------|--------|------|
| paint(Level level, ArrayList<Room> rooms) | boolean | 抽象方法，由具体子类实现，用于将房间布局转换为实际的关卡地形数据 |

## 静态工具方法
| 方法 | 说明 |
|------|------|
| set(Level level, int cell, int value) | 设置指定单元格的地形值 |
| set(Level level, int x, int y, int value) | 设置指定坐标(x, y)的地形值 |
| set(Level level, Point p, int value) | 设置指定点p的地形值 |
| fill(Level level, int x, int y, int w, int h, int value) | 填充矩形区域的地形值 |
| fill(Level level, Rect rect, int value) | 填充矩形区域的地形值 |
| fill(Level level, Rect rect, int m, int value) | 填充矩形内部区域（边距为m）的地形值 |
| fill(Level level, Rect rect, int l, int t, int r, int b, int value) | 填充矩形内部区域（指定各边边距）的地形值 |
| drawLine(Level level, Point from, Point to, int value) | 绘制两点之间的直线 |
| fillEllipse(Level level, Rect rect, int value) | 填充椭圆形区域 |
| fillEllipse(Level level, Rect rect, int m, int value) | 填充椭圆形内部区域（边距为m） |
| fillEllipse(Level level, int x, int y, int w, int h, int value) | 填充指定位置和尺寸的椭圆形区域 |
| fillDiamond(Level level, Rect rect, int value) | 填充菱形区域 |
| fillDiamond(Level level, Rect rect, int m, int value) | 填充菱形内部区域（边距为m） |
| fillDiamond(Level level, int x, int y, int w, int h, int value) | 填充指定位置和尺寸的菱形区域 |
| drawInside(Level level, Room room, Point from, int n, int value) | 从房间边界向内绘制指定长度的路径 |

## 相关子类
[SewerPainter, PrisonPainter, CavesPainter, CityPainter, HallsPainter]