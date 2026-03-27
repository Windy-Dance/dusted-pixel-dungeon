# v0_2_X_Changes 类

## 概述
`v0_2_X_Changes` 是 Shattered Pixel Dungeon 中的版本变更信息类，属于 changelist 包。它负责存储和组织游戏 v0.2.X 系列版本（包括 v0.2.0、v0.2.1、v0.2.2、v0.2.3、v0.2.4）的更新日志信息，通过创建 `ChangeInfo` 对象来结构化地表示每个版本的变更内容。

## 功能特性
- **版本分类**：为每个小版本创建独立的变更信息对象
- **主版本标识**：包含主版本系列标识（v0.2.X）用于整体分组
- **静态数据容器**：作为纯数据类，仅提供变更信息的存储结构

## 核心方法

### 静态方法
- `addAllChanges(ScrollPane list)` - 将所有版本的变更信息添加到指定的滚动窗格中

## 内部结构
- **ChangeInfo 对象**：每个版本对应一个 ChangeInfo 实例
- **版本字符串**：使用精确的版本号作为标识

## 使用示例
```java
// 在变更日志窗口中使用
v0_2_X_Changes.addAllChanges(changesList);
```

## 注意事项
- 此类是 changelist 系统的一部分，与 WndChanges 和 WndChangesTabbed 窗口组件配合使用
- 变更信息的内容通常包含本地化文本，支持多语言显示
- 早期版本（v0.1.X - v0.5.X）的结构相对简单，主要按版本号分组

**注意**：此文档适用于所有 changelist 版本类（v0_1_X_Changes 到 v3_X_Changes），它们具有相同的结构和用途，仅包含的版本号范围不同。