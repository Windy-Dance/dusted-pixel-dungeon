# Bundlable 文档

## 1. 基本信息

| 属性 | 值 |
|------|-----|
| **文件路径** | D:\Develop\Workspace\DustedPixelDungeon\SPD-classes\src\main\java\com\watabou\utils\Bundlable.java |
| **包名** | com.watabou.utils |
| **文件类型** | interface |
| **继承关系** | 无继承，无实现接口 |
| **代码行数** | 29 |
| **所属模块** | SPD-classes |

## 2. 文件职责说明

### 核心职责
定义可序列化对象的契约接口，要求实现类提供从Bundle恢复数据和将数据存储到Bundle的能力。

### 系统定位
作为序列化框架的核心接口，为游戏存档、配置保存和数据持久化提供统一的序列化机制。

### 不负责什么
- 不负责实际的序列化格式（由Bundle类处理）
- 不处理具体的字段映射逻辑
- 不管理序列化的I/O操作

## 3. 结构总览

### 主要成员概览
- 接口定义了两个抽象方法

### 主要逻辑块概览
- 数据恢复（restoreFromBundle）
- 数据存储（storeInBundle）

### 生命周期/调用时机
- 对象创建时调用restoreFromBundle进行反序列化
- 对象需要保存时调用storeInBundle进行序列化

## 4. 继承与协作关系

### 父类提供的能力
无

### 覆写的方法
无

### 实现的接口契约
所有实现类必须提供：
- `restoreFromBundle(Bundle bundle)`: 从Bundle中读取数据并恢复对象状态
- `storeInBundle(Bundle bundle)`: 将对象状态写入Bundle

### 依赖的关键类
- `com.watabou.utils.Bundle`: 序列化数据容器

### 使用者
- 游戏中的所有可持久化对象（Hero、Item、Level等）
- 存档系统
- 配置管理系统

## 5. 字段/常量详解

### 静态常量
无

### 实例字段
无

## 6. 构造与初始化机制

### 构造器
接口无构造器

### 初始化块
无

### 初始化注意事项
- 实现类通常需要提供无参构造器以便通过反射实例化
- 恢复过程在构造后立即执行

## 7. 方法详解

### restoreFromBundle()
**可见性**：public abstract

**是否覆写**：否（接口方法）

**方法职责**：从Bundle中读取数据并恢复对象的内部状态

**参数**：
- `bundle` (Bundle)：包含序列化数据的Bundle对象

**返回值**：void

**前置条件**：bundle不能为null，且包含该对象序列化时的数据

**副作用**：修改对象的内部状态

**核心实现逻辑**：
由具体实现类提供，通常通过bundle.getXXX()方法读取各个字段

**边界情况**：新版本字段在旧存档中不存在时需要处理默认值

### storeInBundle()
**可见性**：public abstract

**是否覆写**：否（接口方法）

**方法职责**：将对象的内部状态写入Bundle以便序列化

**参数**：
- `bundle` (Bundle)：用于存储数据的Bundle对象

**返回值**：void

**前置条件**：bundle不能为null

**副作用**：修改bundle的内容

**核心实现逻辑**：
由具体实现类提供，通常通过bundle.put(key, value)方法存储各个字段

**边界情况**：null值字段需要特殊处理

## 8. 对外暴露能力

### 显式 API
- restoreFromBundle: 反序列化入口
- storeInBundle: 序列化入口

### 内部辅助方法
无

### 扩展入口
- 任何类都可以实现此接口以支持序列化

## 9. 运行机制与调用链

### 创建时机
- 存档加载时，通过Bundle.get()方法自动调用
- 手动序列化时显式调用

### 调用者
- Bundle类（自动调用）
- 存档管理器
- 开发者手动调用

### 被调用者
- Bundle的各种get/put方法

### 系统流程位置
- 数据持久化层的核心接口

## 10. 资源、配置与国际化关联

### 引用的 messages 文案
无

### 依赖的资源
- 存档文件（.dat格式）
- 配置文件

### 中文翻译来源
不适用

## 11. 使用示例

### 基本用法
```java
public class Hero implements Bundlable {
    private int health;
    private String name;
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        health = bundle.getInt("health");
        name = bundle.getString("name");
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("health", health);
        bundle.put("name", name);
    }
}
```

### 扩展示例
```java
// 使用Bundle自动处理复杂对象
@Override
public void restoreFromBundle(Bundle bundle) {
    inventory = new ArrayList<>();
    inventory.addAll(bundle.getCollection("inventory"));
}
```

## 12. 开发注意事项

### 状态依赖
- 对象状态完全由序列化数据决定
- 实现类必须保证序列化/反序列化的一致性

### 生命周期耦合
- 恢复过程必须在对象完全构造后进行
- 存储过程应该在对象状态稳定时进行

### 常见陷阱
- 忘记处理版本兼容性（添加新字段时）
- 在storeInBundle中存储不必要的临时状态
- 在restoreFromBundle中假定所有字段都存在
- 循环引用导致无限递归

## 13. 修改建议与扩展点

### 适合扩展的位置
- 可以添加版本号支持
- 可以添加验证逻辑确保数据完整性

### 不建议修改的位置
- 接口本身（会影响所有实现类）

### 重构建议
- 考虑使用注解驱动的序列化减少样板代码
- 可以添加异步序列化支持

## 14. 事实核查清单

- [x] 是否已覆盖全部字段
- [x] 是否已覆盖全部方法
- [x] 是否已检查继承链与覆写关系
- [x] 是否已核对官方中文翻译
- [x] 是否存在任何推测性表述
- [x] 示例代码是否真实可用
- [x] 是否遗漏资源/配置/本地化关联
- [x] 是否明确说明了注意事项与扩展点