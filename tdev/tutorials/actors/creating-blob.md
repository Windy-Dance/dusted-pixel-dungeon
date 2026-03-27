# 创建新 Blob 教程

## 目标
本教程将指导你创建一个自定义 Blob（区域效果）。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Blob 基类

---

## 第一部分：Blob 基础结构

```java
package com.dustedpixel.dustedpixeldungeon.actors.blobs;

import com.dustedpixel.dustedpixeldungeon.Dungeon;
import com.dustedpixel.dustedpixeldungeon.actors.Actor;
import com.dustedpixel.dustedpixeldungeon.actors.Char;
import com.dustedpixel.dustedpixeldungeon.actors.buffs.Buff;
import com.dustedpixel.dustedpixeldungeon.effects.BlobEmitter;
import com.dustedpixel.dustedpixeldungeon.effects.particles.ShadowParticle;
import com.dustedpixel.dustedpixeldungeon.messages.Messages;

public class DarkMist extends Blob {

    @Override
    protected void evolve() {
        // 计算新区域
        int from = WIDTH + 1;
        int to = WIDTH + HEIGHT - 1;

        for (int pos = from; pos < to; pos++) {
            // 继承上一帧的状态（衰减）
            int oldValue = cur[pos];

            if (oldValue > 0) {
                // 衰减
                cur[pos] = oldValue - 1;

                // 对角色造成影响
                Char ch = Actor.findChar(pos);
                if (ch != null) {
                    affectChar(ch);
                }
            }

            // 更新体积
            volume += cur[pos];
        }
    }

    private void affectChar(Char ch) {
        // 施加失明效果
        Buff.prolong(ch, Blindness.class, 2f);

        // 施加隐蔽效果
        Buff.affect(ch, Invisibility.class, 2f);
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);

        // 设置粒子效果
        emitter.pour(ShadowParticle.MIST, 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
```

---

## 第二部分：Blob 核心 API

| 方法 | 说明 |
|------|------|
| `evolve()` | 每帧更新逻辑 |
| `seed(Level level, int cell, int amount)` | 在指定位置生成 Blob |
| `clear(int cell)` | 清除指定位置的 Blob |
| `use(BlobEmitter emitter)` | 设置视觉效果 |

---

## 第三部分：使用 Blob

```java
// 生成 Blob
GameScene.add(Blob.seed(Dungeon.level, targetPos, 10, DarkMist.class));

// 检查位置是否有 Blob
DarkMist mist = Dungeon.level.blobs.get(DarkMist.class);
if (mist != null && mist.volume > 0) {
    // 有效果存在
}
```

---

## 测试验证

```java
// 在物品或技能中使用
GameScene.add(Blob.seed(Dungeon.level, pos, 20, DarkMist.class));
```

---

## 相关资源

- [Blob API 参考](../../reference/actors/blob-api.md)
- [Buff API 参考](../../reference/actors/buff-api.md)