# Notes.java - 笔记系统

## 概述
`Notes` 类管理游戏中的位置相关笔记，包括地标（商店、水井、NPC）、钥匙和自定义文本记录。所有笔记都按地牢深度进行组织，并在游戏会话间持久化。

## 核心组件

### Record 抽象基类
- 实现 `Comparable<Record>` 和 `Bundlable` 接口
- 定义通用方法：`depth()`、`icon()`、`title()`、`desc()`、`quantity()`
- 提供排序逻辑（按深度和类型顺序）

### LandmarkRecord（地标记录）
跟踪各种重要位置：
- **楼层类型**：悬崖层、水域层、草地层、黑暗层、大型层、陷阱层、秘密层
- **功能区域**：商店、炼金室、花园、远古水井、生命之泉、觉醒之泉、献祭火焰、雕像
- **特殊物品**：丢失的背包、返回信标位置
- **NPC位置**：幽灵、鼠王、制杖师、巨魔铁匠、小恶魔、恶魔生成器

每个地标都有对应的图标和本地化描述。

### KeyRecord（钥匙记录）
- 跟踪各类钥匙的持有情况
- 支持数量统计（相同类型的钥匙合并计数）
- 自动处理钥匙的添加、移除和计数查询

### CustomRecord（自定义记录）
- 支持玩家创建的自定义笔记
- 四种类型：纯文本、深度相关、物品类型、特定物品
- 提供编辑功能（修改标题和内容）
- 每个记录有唯一ID，支持最多5个自定义记录

## 使用方法

### 添加笔记
```java
// 添加地标笔记（当前深度）
Notes.add(Notes.Landmark.SHOP);

// 添加指定深度的地标笔记
Notes.add(Notes.Landmark.WELL_OF_HEALTH, 5);

// 添加钥匙记录
Notes.add(new GoldenKey());

// 添加自定义记录
Notes.add(new Notes.CustomRecord("我的笔记", "这是自定义内容"));
```

### 查询笔记
```java
// 检查是否包含某地标
boolean hasShop = Notes.contains(Notes.Landmark.SHOP);

// 获取指定深度的所有记录
ArrayList<Notes.Record> records = Notes.getRecords(10);

// 获取特定类型的记录
ArrayList<Notes.KeyRecord> keys = Notes.getRecords(Notes.KeyRecord.class);
```

### 移除笔记
```java
// 移除地标笔记
Notes.remove(Notes.Landmark.SHOP);

// 移除钥匙（自动减少数量）
Notes.remove(new GoldenKey());
```

## 持久化
- 所有笔记数据通过 `Bundle` 系统序列化
- 集成到全局日记保存系统（Journal.saveGlobal()）
- 支持跨游戏会话的数据保持

## 设计特点
- **类型安全**：使用枚举和泛型确保类型正确性
- **内存效率**：使用 ArrayList 存储，避免不必要的开销
- **扩展性**：Record 抽象类便于添加新的记录类型
- **用户体验**：提供直观的API和自动的数量管理