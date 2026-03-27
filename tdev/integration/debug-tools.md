# 调试工具指南

## 概述
本指南介绍 Shattered Pixel Dungeon 开发中可用的调试工具和技巧。

## 开发者控制台

### 启用方式
1. 游戏设置 → 开发者选项 → 启用控制台
2. 或在代码中设置 `SPDSettings.debug(true)`

### 控制台命令

#### 物品管理
| 命令 | 说明 |
|------|------|
| `give <item>` | 给予物品 |
| `drop <item>` | 掉落物品到地面 |
| `identify` | 鉴定所有物品 |
| `upgrade` | 升级装备物品 |

#### 角色管理
| 命令 | 说明 |
|------|------|
| `level <n>` | 设置英雄等级 |
| `exp <n>` | 设置经验值 |
| `str <n>` | 设置力量值 |
| `hp <n>` | 设置生命值 |
| `gold <n>` | 设置金币数量 |

#### 传送与关卡
| 命令 | 说明 |
|------|------|
| `depth <n>` | 传送到指定层 |
| `pos <x> <y>` | 传送到指定坐标 |
| `floor` | 显示当前层数和坐标 |

#### 怪物控制
| 命令 | 说明 |
|------|------|
| `spawn <mob>` | 生成怪物 |
| `killall` | 杀死所有怪物 |
| `peaceful` | 怪物不再主动攻击 |

#### 游戏状态
| 命令 | 说明 |
|------|------|
| `god` | 无敌模式 |
| `invisible` | 隐身模式 |
| `maphack` | 显示整个地图 |
| `die` | 测试死亡 |

---

## 日志系统

### GLog 使用

```java
import com.dustedpixel.dustedpixeldungeon.utils.GLog;

// 信息（灰色）
GLog.i("普通信息: %s",value);

// 警告（黄色）
GLog.

w("警告信息");

// 负面/错误（红色）
GLog.

n("错误信息");

// 正面（绿色）
GLog.

p("成功信息");

// 新行
GLog.

newLine();
```

### 日志级别控制

```java
// 设置日志级别
GLog.setLogLevel(GLog.DEBUG);  // 显示所有日志
GLog.setLogLevel(GLog.INFO);   // 仅显示信息及以上
GLog.setLogLevel(GLog.WARN);   // 仅显示警告及以上
```

---

## 可视化调试

### 绘制调试图形

```java
import com.dustedpixel.dustedpixeldungeon.effects.CellEmitter;
import com.dustedpixel.dustedpixeldungeon.effects.Speck;

// 在单元格显示特效
CellEmitter.get(cell).

burst(Speck.factory(Speck.DISCOVER), 10);

// 显示粒子效果
        CellEmitter.

center(cell).

burst(Speck.factory(Speck.KIT), 5);
```

### 地图调试

```java
// 显示视野范围
Dungeon.level.updateFieldOfView(hero, hero.fieldOfView);

// 检查单元格状态
boolean passable = Dungeon.level.passable[cell];
boolean solid = Dungeon.level.solid[cell];
boolean visited = Dungeon.level.visited[cell];
```

---

## 性能分析

### 计时代码

```java
long start = System.nanoTime();

// 要测试的代码
performAction();

long end = System.nanoTime();
long durationMs = (end - start) / 1_000_000;
GLog.i("耗时: " + durationMs + "ms");
```

### 内存分析

```java
// 获取内存使用情况
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
GLog.i("内存使用: " + (usedMemory / 1024 / 1024) + "MB");
```

---

## 存档调试

### 检查存档

```java
import com.dustedpixel.dustedpixeldungeon.utils.Bundle;

// 读取存档
Bundle bundle = Dungeon.gameBundle(Dungeon.depth);

        // 检查特定值
        int hp = bundle.getInt("HP");
        String className = bundle.getString("heroClass");

GLog.

        i("存档HP: "+hp);
```

### 清除存档

```java
// 清除当前存档
GamesInProgress.delete(GamesInProgress.curSlot);

// 重置游戏
Dungeon.deleteGame(GamesInProgress.curSlot, true);
```

---

## 常见问题调试

### 物品不生效
1. 检查 `doPickUp()` 返回值
2. 检查 `execute()` 是否被调用
3. 检查 `actions()` 是否包含正确的动作

### 怪物不行动
1. 检查 `act()` 方法是否正确
2. 检查 AI 状态机是否正常
3. 检查 `chooseEnemy()` 返回值

### 关卡生成问题
1. 检查 `createMobs()` 和 `createItems()`
2. 检查房间放置逻辑
3. 检查 `Builder` 和 `Painter`

---

## 调试配置

### Debug 模式

```java
// 在主类中启用
if (BuildConfig.DEBUG) {
    SPDSettings.debug(true);
}
```

### 断言

```java
// 使用断言检查条件
assert hero != null : "Hero should not be null";

// 启用断言需要添加 JVM 参数: -ea
```

---

## 相关资源

- [测试指南](testing-guide.md)
- [开发者控制台命令列表](https://shatteredpixel.com/wiki/)