# 添加粒子效果教程

## 目标
本教程将指导你如何创建自定义粒子效果。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Noosa 引擎基础

---

## 第一部分：粒子系统

### 核心类

| 类 | 说明 |
|---|------|
| `PixelParticle` | 像素粒子基类 |
| `Emitter` | 粒子发射器 |
| `Factory` | 粒子工厂接口 |

---

## 第二部分：创建粒子类

```java
package com.dustedpixel.dustedpixeldungeon.effects.particles;

import com.watabou.noosa.PixelParticle;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

public class CrystalParticle extends PixelParticle {

    // 粒子工厂
    public static final Emitter.Factory FACTORY = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            emitter.recycle(CrystalParticle.class).reset(x, y);
        }
    };

    public void reset(float x, float y) {
        revive();

        this.x = x;
        this.y = y;

        // 随机速度
        speed.set(
                Random.Float(-8, +8),
                Random.Float(-16, -4)
        );

        // 生命周期
        lifespan = Random.Float(0.5f, 1f);

        // 初始颜色和大小
        color(0x4488FF);
        size(Random.Float(2, 4));
    }

    @Override
    public void update() {
        super.update();

        // 逐渐变暗
        float p = left / lifespan;
        am = p > 0.5f ? 1 : p * 2;

        // 添加重力效果
        speed.y += 0.5f;
    }
}
```

---

## 第三部分：使用粒子

### 在精灵中使用

```java
public class CrystalMobSprite extends MobSprite {
    
    private Emitter emitter;
    
    @Override
    public void link(Char ch) {
        super.link(ch);
        
        emitter = new Emitter();
        emitter.pos(width / 2, height / 2);
        emitter.pour(CrystalParticle.FACTORY, 0.1f);
        add(emitter);
    }
    
    @Override
    public void kill() {
        super.kill();
        if (emitter != null) {
            emitter.on = false;
        }
    }
}
```

### 在关卡效果中使用

```java
// 在特定位置发射粒子
CellEmitter.get(cell).burst(CrystalParticle.FACTORY, 10);

// 持续发射
CellEmitter.get(cell).pour(CrystalParticle.FACTORY, 0.1f);
```

---

## 第四部分：内置粒子工厂

| 工厂 | 效果 |
|------|------|
| `Speck.factory(Speck.HEALING)` | 治愈效果 |
| `Speck.factory(Speck.STAR)` | 星光效果 |
| `Speck.factory(Speck.KIT)` | 物品效果 |
| `FlameParticle.FACTORY` | 火焰效果 |
| `ShadowParticle.CURSE` | 诅咒效果 |
| `EarthParticle.FACTORY` | 泥土效果 |
| `LeafParticle.LEVEL_SPECIFIC` | 树叶效果 |
| `SnowParticle.FACTORY` | 雪花效果 |

---

## 相关资源

- [添加精灵图教程](adding-sprites.md)
- [Mob API 参考](../../reference/actors/mob-api.md)