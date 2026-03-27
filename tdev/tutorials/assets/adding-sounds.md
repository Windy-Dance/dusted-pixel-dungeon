# 添加音效教程

## 目标
本教程将指导你如何添加新的音效资源。

## 前置知识
- 了解音频格式要求
- 熟悉项目资源结构

---

## 第一部分：音效格式

### 支持格式

| 格式 | 推荐场景 |
|------|---------|
| OGG | 音乐、长音效 |
| WAV | 短音效 |

### 音效规范

- 采样率：44100 Hz
- 位深度：16-bit
- 声道：单声道（音效）/ 立体声（音乐）
- 音量：标准化到 -3dB

---

## 第二部分：添加音效

### 步骤 1：放置文件

将音效文件放入 `core/assets/sounds/` 目录：
```
core/assets/sounds/
├── hit.ogg
├── miss.ogg
├── custom_sound.ogg  -- 新音效
```

### 步骤 2：注册音效

在 `Assets.java` 中：

```java
public static class Sounds {
    // ... 现有音效
    
    public static final String CUSTOM_SOUND = "sounds/custom_sound.ogg";
}
```

---

## 第三部分：播放音效

### 基本播放

```java
import com.watabou.noosa.audio.Sample;

// 播放一次
Sample.INSTANCE.play(Assets.Sounds.CUSTOM_SOUND);

// 调整音调和音量
Sample.INSTANCE.play(Assets.Sounds.CUSTOM_SOUND, volume, pitch);

// 示例：高音调
Sample.INSTANCE.play(Assets.Sounds.CUSTOM_SOUND, 1, 1.5f);

// 示例：低音调
Sample.INSTANCE.play(Assets.Sounds.CUSTOM_SOUND, 1, 0.8f);
```

### 在物品中使用

```java
public class CustomWeapon extends MeleeWeapon {
    
    @Override
    public void hitSound(float pitch) {
        Sample.INSTANCE.play(Assets.Sounds.CUSTOM_SOUND, 1, pitch);
    }
}
```

### 在怪物中使用

```java
public class CustomMob extends Mob {
    
    @Override
    public void damage(int dmg, Object src) {
        super.damage(damage, src);
        Sample.INSTANCE.play(Assets.Sounds.CUSTOM_SOUND);
    }
}
```

---

## 第四部分：内置音效

### 常用音效

| 常量 | 说明 |
|------|------|
| `HIT` | 击中音效 |
| `MISS` | 未击中音效 |
| `STEP` | 脚步声 |
| `WATER` | 水声 |
| `GRASS` | 草地声 |
| `TRAP` | 陷阱触发 |
| `DOOR_OPEN` | 开门 |
| `DOOR_CLOSE` | 关门 |
| `UNLOCK` | 解锁 |
| `ITEM` | 拾取物品 |
| `GOLD` | 获得金币 |
| `BADGE` | 成就解锁 |
| `LEVELUP` | 升级 |
| `DEATH` | 死亡 |
| `CHALLENGE` | 挑战 |
| `CURSED` | 诅咒 |
| `EVOKE` | 触发效果 |
| `SHATTER` | 破碎 |
| `DESCEND` | 下楼 |
| `ASCEND` | 上楼 |

---

## 第五部分：音乐系统

### 播放音乐

```java
import com.watabou.noosa.audio.Music;

// 播放并循环
Music.INSTANCE.play(Assets.Music.SEWERS, true);

// 停止
Music.INSTANCE.stop();

// 暂停/恢复
Music.INSTANCE.pause();
Music.INSTANCE.resume();

// 设置音量
Music.INSTANCE.volume(0.5f);
```

### 音乐注册

```java
public static class Music {
    public static final String SEWERS = "music/sewers.ogg";
    public static final String PRISON = "music/prison.ogg";
    // ...
}
```

---

## 注意事项

1. **文件大小**: 保持音效文件小于 100KB
2. **音量平衡**: 确保新音效与现有音效音量一致
3. **版权问题**: 使用无版权或授权的音效资源
4. **测试不同设备**: 在不同设备上测试音效效果

---

## 相关资源

- [资源管线指南](../../integration/asset-pipeline.md)
- [添加精灵图教程](adding-sprites.md)