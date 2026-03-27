# 测试指南

## 概述
本指南介绍如何测试 Shattered Pixel Dungeon 的代码修改和新增内容。

## 开发者控制台

### 启用控制台
在游戏设置中启用开发者选项，或修改代码启用。

### 常用命令

| 命令 | 说明 | 示例 |
|------|------|------|
| `spawn <mob>` | 生成怪物 | `spawn Rat` |
| `give <item>` | 给予物品 | `give PotionOfHealing` |
| `gold <amount>` | 设置金币 | `gold 1000` |
| `level <number>` | 设置等级 | `level 10` |
| `depth <number>` | 传送到指定层 | `depth 5` |
| `identify` | 鉴定所有物品 | `identify` |
| `god` | 无敌模式 | `god` |
| `die` | 死亡测试 | `die` |

---

## 单元测试

### 测试框架
项目使用 JUnit 进行单元测试。

### 创建测试类

```java
package com.dustedpixel.dustedpixeldungeon.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class DamageCalculationTest {

    @Test
    public void testDamageRoll() {
        // 测试伤害计算
        int minDmg = 5;
        int maxDmg = 10;
        int roll = Random.NormalIntRange(minDmg, maxDmg);

        assertTrue(roll >= minDmg);
        assertTrue(roll <= maxDmg);
    }

    @Test
    public void testCriticalHit() {
        // 测试暴击伤害
        float critMultiplier = 1.5f;
        int baseDamage = 10;
        int critDamage = Math.round(baseDamage * critMultiplier);

        assertEquals(15, critDamage);
    }
}
```

### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行特定测试类
./gradlew test --tests "DamageCalculationTest"
```

---

## 集成测试

### 游戏内测试

#### 测试新物品
```java
// 1. 使用控制台命令给予物品
// give CustomItem

// 2. 测试物品功能
// - 使用物品
// - 装备物品
// - 投掷物品
// - 检查属性变化
```

#### 测试新怪物
```java
// 1. 生成怪物
// spawn CustomMob

// 2. 测试怪物行为
// - AI 是否正常
// - 攻击是否正确
// - 掉落是否正常
// - 特殊能力是否触发
```

#### 测试新关卡
```java
// 1. 传送到关卡
// depth <新关卡层数>

// 2. 检查关卡生成
// - 房间布局
// - 敌人分布
// - 物品生成
// - 特殊机制
```

---

## 调试技巧

### 日志输出

```java
import com.dustedpixel.dustedpixeldungeon.utils.GLog;

// 信息日志
GLog.i("This is an info message");

// 警告日志
GLog.

w("This is a warning message");

// 错误日志
GLog.

n("This is a negative/error message");

// 正面日志（绿色）
GLog.

p("This is a positive message");
```

### 断点调试

在 IDE 中设置断点：
1. 在代码行号处点击设置断点
2. 以调试模式运行游戏
3. 当执行到断点时暂停

### 性能分析

```java
// 计时代码
long startTime = System.currentTimeMillis();

// 执行代码
doSomething();

long endTime = System.currentTimeMillis();
GLog.i("Execution time: " + (endTime - startTime) + "ms");
```

---

## 测试清单

### 新物品测试
- [ ] 物品名称正确显示
- [ ] 物品描述正确显示
- [ ] 物品图标正确
- [ ] 使用功能正常
- [ ] 装备功能正常（如适用）
- [ ] 升级效果正确
- [ ] 诅咒效果正确（如适用）
- [ ] 掉落权重正确
- [ ] 价格正确
- [ ] 本地化文本正确

### 新怪物测试
- [ ] 怪物名称正确
- [ ] 怪物描述正确
- [ ] 怪物精灵正确显示
- [ ] 移动行为正常
- [ ] 攻击行为正常
- [ ] 特殊能力正常
- [ ] 掉落物品正确
- [ ] 经验值正确
- [ ] 生成权重正确

### 新关卡测试
- [ ] 关卡生成正常
- [ ] 房间类型正确
- [ ] 敌人生成正确
- [ ] 物品生成正确
- [ ] 楼梯位置正确
- [ ] 特殊机制正常
- [ ] 性能可接受

---

## 自动化测试

### 创建测试场景

```java
// 创建测试用的模拟游戏状态
public class TestDungeon {
    
    public static Hero createTestHero() {
        Hero hero = new Hero();
        hero.HT = hero.HP = 100;
        hero.STR = 10;
        hero.lvl = 10;
        return hero;
    }
    
    public static Mob createTestMob() {
        Mob mob = new Mob() {
            @Override
            public int damageRoll() { return 10; }
        };
        mob.HT = mob.HP = 50;
        return mob;
    }
}
```

---

## 相关资源

- [调试工具指南](debug-tools.md)
- [注册指南](registration-guide.md)
- [项目文档](../README.md)