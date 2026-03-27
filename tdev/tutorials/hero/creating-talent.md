# 创建新天赋教程

## 目标
本教程将指导你创建一个自定义天赋。

## 前置知识
- 熟悉 Java 基础语法
- 了解 Talent 系统

---

## 第一部分：天赋定义

在 `Talent.java` 中添加新天赋：

```java
public enum Talent {
    // ... 现有天赋
    
    // 新天赋 - 第1层
    CRYSTAL_SKIN,
    
    // 新天赋 - 第2层
    FROZEN_HEART,
    
    // ... 其他天赋
}
```

---

## 第二部分：天赋配置

```java
// 在 Talent.java 静态块中配置
static {
    // 为战士添加天赋
    classTalents.get(HeroClass.WARRIOR).get(0).put(CRYSTAL_SKIN, 0);
    classTalents.get(HeroClass.WARRIOR).get(1).put(FROZEN_HEART, 0);
}

// 天赋图标
public int icon(){
    switch (this){
        case CRYSTAL_SKIN: return Icons.get(Icons.CRYSTAL);
        case FROZEN_HEART: return Icons.get(Icons.FROST);
        default: return 0;
    }
}

// 天赋标题
public String title(){
    return Messages.get(this, name() + "_title");
}

// 天赋描述
public String desc(){
    return Messages.get(this, name() + "_desc");
}
```

---

## 第三部分：天赋效果实现

```java
// 在 Hero 类或相关位置实现天赋效果
public int defenseSkill(Char enemy) {
    int defense = super.defenseSkill(enemy);
    
    // 水晶皮肤天赋：增加防御
    if (hasTalent(Talent.CRYSTAL_SKIN)) {
        defense += 2 * pointsInTalent(Talent.CRYSTAL_SKIN);
    }
    
    return defense;
}

// 受伤时触发
public void damage(int dmg, Object src) {
    super.damage(dmg, src);
    
    // 冰冻之心天赋：受伤时有概率冰冻攻击者
    if (src instanceof Char && hasTalent(Talent.FROZEN_HEART)) {
        Char attacker = (Char) src;
        if (Random.Float() < 0.1f * pointsInTalent(Talent.FROZEN_HEART)) {
            Buff.affect(attacker, Frost.class, 3f);
        }
    }
}
```

---

## 第四部分：本地化

```properties
# messages.properties
talents.crystal_skin_title=Crystal Skin
talents.crystal_skin_desc=Your skin hardens like crystal, granting +2/+4/+6 defense per talent point.

talents.frozen_heart_title=Frozen Heart
talents.frozen_heart_desc=When damaged, there is a 10%/20%/30% chance to freeze the attacker for 3 turns.

# messages_zh.properties
talents.crystal_skin_title=水晶皮肤
talents.crystal_skin_desc=你的皮肤如水晶般硬化，每点天赋点提供 +2/+4/+6 防御。

talents.frozen_heart_title=冰冻之心
talents.frozen_heart_desc=受伤时，有 10%/20%/30% 的概率冰冻攻击者 3 回合。
```

---

## 测试验证

```
level 6    -- 解锁第1层天赋
level 12   -- 解锁第2层天赋
```

---

## 相关资源

- [天赋系统详解](../../systems/talent-system-detailed.md)
- [Hero API 参考](../../reference/actors/hero-api.md)