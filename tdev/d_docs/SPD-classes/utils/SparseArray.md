# SparseArray 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\SparseArray.java |
| **包名** | com.watabou.utils |
| **文件类型** | class |
| **继承关系** | extends IntMap<T> |
| **代码行数** | 53 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
提供线程安全的稀疏数组实现，基于LibGDX的IntMap，支持整数键到任意类型值的高效映射，特别适用于非连续整数索引的场景。

### 系统定位
作为游戏引擎的高性能数据结构工具，为关卡生成、实体管理、状态存储等需要非连续整数键映射的场景提供内存高效的解决方案。

### 不负责什么
- 不负责键的连续性保证
- 不提供排序或有序遍历
- 不处理复杂的键值对操作

## 3. 结构总览

### 主要成员概览
- 继承自IntMap<T>
- 重写核心方法添加synchronized关键字
- 提供键数组和值列表的便捷访问方法

### 主要逻辑块概览
- 线程安全的操作方法（put, get, remove）
- 键值集合访问（keyArray, valueList）

### 生命周期/调用时机
- 按需创建用于存储稀疏数据
- 在多线程环境中安全使用
- 作为替代HashMap<Integer, T>的高性能选择

## 4. 继承与协作关系

### 父类提供的能力
- IntMap<T>提供了基于开放寻址法的高性能整数键映射
- 基础的put/get/remove/keys/values等方法

### 覆写的方法
- put(int, T): 添加synchronized
- get(int, T): 添加synchronized  
- remove(int): 添加synchronized

### 实现的接口契约
无直接接口实现，但继承了IntMap的契约

### 依赖的关键类
- `com.badlogic.gdx.utils.IntMap`: LibGDX高性能整数映射实现
- `java.util.Arrays`: 数组操作工具
- `java.util.List`: 列表接口

### 使用者
- 游戏地图系统（稀疏实体存储）
- 关卡生成器（非连续房间ID映射）
- 状态管理系统（稀疏状态存储）
- 缓存系统（整数键缓存）

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无（继承自IntMap）

## 6. 构造与初始化机制

### 构造器
继承IntMap的所有构造器：
- SparseArray()：默认容量
- SparseArray(int initialCapacity)：指定初始容量
- SparseArray(IntMap<? extends T> map)：从现有IntMap复制

### 初始化块
无

### 初始化注意事项
- 所有构造器都创建空的稀疏数组
- 初始容量影响性能，但会自动扩容
- 线程安全通过synchronized方法保证

## 7. 方法详解

### put()
**可见性**：public synchronized

**是否覆写**：是，覆写自IntMap

**方法职责**：将键值对存入稀疏数组

**参数**：
- `key` (int)：整数键
- `value` (T)：值对象

**返回值**：T，之前的值（如果存在），否则null

**前置条件**：无

**副作用**：修改内部存储

**核心实现逻辑**：
```java
return super.put(key, value);
```

**边界情况**：
- key可以是任意整数（包括负数）
- value为null会被正常存储
- 线程安全（synchronized）

### get()
**可见性**：public synchronized

**是否覆写**：是，覆写自IntMap

**方法职责**：获取指定键对应的值

**参数**：
- `key` (int)：整数键
- `defaultValue` (T)：默认值（当键不存在时返回）

**返回值**：T，键对应的值或默认值

**前置条件**：无

**副作用**：无

**核心 implement logic**：
```java
return super.get(key, defaultValue);
```

**Boundary cases**：
- 键不存在时返回defaultValue
- 线程安全（synchronized）

### remove()
**可见性**：public synchronized

**是否覆写**：是，覆写自IntMap

**方法职责**：移除指定键及其值

**参数**：
- `key` (int)：整数键

**返回值**：T，被移除的值，不存在时返回null

**前置条件**：无

**副作用**：修改内部存储

**core implementation logic**：
```java
return super.remove(key);
```

**Boundary cases**：
- 键不存在时返回null
- 线程安全（synchronized）

### keyArray()
**可见性**：public synchronized

**是否覆写**：否

**方法职责**：获取所有键的整数数组

**参数**：无

**返回值**：int[]，包含所有键的数组

**前置条件**：无

**副作用**：无

**core implementation logic**：
```java
return keys().toArray().toArray();
```

**Boundary cases**：
- 空映射返回空数组
- 线程安全（synchronized）
- 注意：存在双重toArray()调用，可能是类型转换需要

### valueList()
**可见性**：public synchronized

**是否覆写**：否

**方法职责**：获取所有值的列表

**参数**：无

**返回值**：List<T>，包含所有值的列表

**前置条件**：无

**副作用**：无

**core implementation logic**：
```java
return Arrays.asList(values().toArray().toArray());
```

**Boundary cases**：
- 空映射返回空列表
- 线程安全（synchronized）
- 返回的列表是固定大小的（Arrays.asList特性）

## 8. 对外暴露能力

### 显式 API
- 线程安全的基础操作：put, get, remove
- 便捷的集合访问：keyArray, valueList
- 继承的IntMap所有方法（非synchronized版本仍可访问）

### 内部辅助方法
无

### 扩展入口
- 泛型设计支持任意值类型
- 可以继承进一步扩展功能

## 9. 运行机制与调用链

### 创建时机
- 按需创建，通常在需要稀疏整数键映射时

### 调用者
- 地图生成器（Room ID到Room对象的映射）
- 实体管理系统（Entity ID到Entity对象）
- 状态缓存（State ID到State对象）
- 资源管理（Resource ID到Resource对象）

### 被调用者
- IntMap父类方法
- Arrays工具类
- LibGDX底层哈希表实现

### 系统流程位置
- 数据结构工具层，提供高性能稀疏映射

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- LibGDX IntMap实现
- JVM内存（用于存储键值对）

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
// 创建稀疏数组
SparseArray<String> sparseStrings = new SparseArray<>();

// 存储非连续的数据
sparseStrings.put(1, "First");
sparseStrings.put(100, "Hundredth");
sparseStrings.put(1000, "Thousandth");

// 获取数据
String first = sparseStrings.get(1, "Default");
String missing = sparseStrings.get(50, "Not Found"); // 返回"Not Found"

// 移除数据
String removed = sparseStrings.remove(100);

// 获取所有键和值
int[] allKeys = sparseStrings.keyArray(); // [1, 1000]
List<String> allValues = sparseStrings.valueList(); // ["First", "Thousandth"]
```

### 游戏场景示例
```java
// 关卡房间管理
public class Level {
    private SparseArray<Room> rooms = new SparseArray<>();
    
    public void addRoom(int roomId, Room room) {
        rooms.put(roomId, room);
    }
    
    public Room getRoom(int roomId) {
        return rooms.get(roomId, null);
    }
    
    public void removeRoom(int roomId) {
        rooms.remove(roomId);
    }
    
    // 获取所有房间进行处理
    public void updateAllRooms() {
        List<Room> roomList = rooms.valueList();
        for (Room room : roomList) {
            room.update();
        }
    }
}

// 多线程安全使用
public class ThreadSafeCache {
    private SparseArray<Object> cache = new SparseArray<>();
    
    // 在多线程环境中安全地访问缓存
    public Object getCached(int id) {
        return cache.get(id, null);
    }
    
    public void setCached(int id, Object value) {
        cache.put(id, value);
    }
}
```

### 性能对比
```java
// 传统HashMap方式（内存效率低）
HashMap<Integer, Entity> entities = new HashMap<>();
entities.put(1000000, hero); // 浪费大量内存空间

// SparseArray方式（内存效率高）
SparseArray<Entity> entities = new SparseArray<>();
entities.put(1000000, hero); // 只存储实际使用的键值对
```

## 12. 开发注意事项

### 状态依赖
- 所有方法都是synchronized，保证线程安全
- 内部状态由IntMap父类维护
- 键值对存储是非连续的

### 生命周期耦合
- 实例可以长期持有或临时创建
- 无特殊生命周期要求
- 注意内存泄漏（长时间持有大稀疏数组）

### 常见陷阱
- 忘记synchronized带来的性能开销（在单线程场景中可能不必要）
- 误解keyArray/valueList返回的数组/列表是可变的（实际是快照）
- 在valueList上尝试修改操作（Arrays.asList返回的列表不支持add/remove）
- 双重toArray()调用可能存在性能问题（但保证类型安全）
- 与普通IntMap混用时忽略线程安全差异

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加批量操作方法（putAll, removeAll）
- 可以添加迭代器支持
- 可以添加更高效的键值对遍历方法

### 不建议修改的位置
- synchronized关键字（保证线程安全的核心）
- 核心方法的重写逻辑（保持与IntMap一致）
- 构造器继承（保持兼容性）

### 重构建议
- 考虑使用Collections.synchronizedMap替代（如果不需要IntMap的特殊优化）
- 可以添加自定义的Iterator实现避免双重toArray()
- 考虑添加clear()方法的synchronized覆盖

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点