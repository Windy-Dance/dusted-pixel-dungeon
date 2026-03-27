# HeroSubClass - 英雄子职业类

## 概述
`HeroSubClass` 枚举类定义了 Shattered Pixel Dungeon 中英雄的进阶子职业系统。当英雄达到一定等级后，可以从其基础职业的两个子职业选项中选择一个进行专精，获得独特的技能和能力。

子职业系统为游戏提供了更深层次的角色定制和策略选择，每个子职业都有其独特的游戏风格和战术优势。

## 枚举常量

### NONE
- **图标**: `HeroIcon.NONE`
- **说明**: 表示尚未选择子职业的状态

### 战士系子职业
- **BERSERKER** (狂战士): `HeroIcon.BERSERKER`
  - 专注于狂暴战斗和生存能力
  - 在低血量时变得更强大
  
- **GLADIATOR** (角斗士): `HeroIcon.GLADIATOR`  
  - 专注于连击系统和精准打击
  - 通过连续攻击积累威力

### 法师系子职业
- **BATTLEMAGE** (战斗法师): `HeroIcon.BATTLEMAGE`
  - 将法杖与近战结合
  - 法杖效果影响近战攻击
  
- **WARLOCK** (术士): `HeroIcon.WARLOCK`
  - 专注于生命吸取和召唤
  - 通过伤害敌人来恢复自己

### 盗贼系子职业  
- **ASSASSIN** (刺客): `HeroIcon.ASSASSIN`
  - 专注于偷袭和致命一击
  - 对未察觉的敌人造成巨大伤害
  
- **FREERUNNER** (自由奔跑者): `HeroIcon.FREERUNNER`
  - 专注于机动性和闪避
  - 通过移动获得战斗优势

### 猎人系子职业
- **SNIPER** (狙击手): `HeroIcon.SNIPER`
  - 专注于远程精准射击
  - 远距离战斗专家
  
- **WARDEN** (守护者): `HeroIcon.WARDEN`
  - 专注于自然之力和防御
  - 利用环境获得优势

### 决斗者系子职业
- **CHAMPION** (冠军): `HeroIcon.CHAMPION`
  - 专注于双武器战斗
  - 同时使用主副武器
  
- **MONK** (武僧): `HeroIcon.MONK`
  - 专注于徒手格斗和能量系统
  - 武器能力与特殊能力结合

### 牧师系子职业
- **PRIEST** (祭司): `HeroIcon.PRIEST`
  - 专注于神圣攻击和区域控制
  - 创造神圣区域影响战场
  
- **PALADIN** (圣骑士): `HeroIcon.PALADIN`
  - 专注于治疗和保护
  - 既能输出伤害又能支援队友

## 字段

### 核心字段
- **icon**: `int` - 子职业在 UI 中显示的图标索引

## 构造函数

### HeroSubClass(int icon)
初始化子职业及其对应的 UI 图标。

## 核心方法

### title()
获取子职业的本地化标题名称。

### shortDesc()
获取子职业的简短描述文本（用于选择界面）。

### desc()
获取子职业的详细描述文本，包含完整的技能说明。

### icon()
返回子职业的 UI 图标索引。

## 特殊功能

### BATTLEMAGE 描述动态生成
对于战斗法师子职业，描述文本会动态包含当前装备法杖的效果说明：

```java
if (this == BATTLEMAGE){
    String desc = Messages.get(this, name() + "_desc");
    if (Game.scene() instanceof GameScene){
        MagesStaff staff = Dungeon.hero.belongings.getItem(MagesStaff.class);
        if (staff != null && staff.wandClass() != null){
            desc += "\n\n" + Messages.get(staff.wandClass(), "bmage_desc");
            desc = desc.replaceAll("_", "");
        }
    }
    return desc;
}
```

## 使用示例

```java
// 设置英雄子职业
hero.subClass = HeroSubClass.BERSERKER;

// 获取子职业信息
String title = HeroSubClass.GLADIATOR.title();
String description = HeroSubClass.ASSASSIN.desc();

// 检查当前子职业
if (hero.subClass == HeroSubClass.SNIPER) {
    // 应用狙击手相关逻辑
}
```

## 注意事项

1. **选择时机**：子职业通常在游戏中期（约10级左右）解锁选择
2. **不可更改**：一旦选择子职业，通常无法更改（除非特定游戏机制）
3. **天赋影响**：子职业选择会影响可用的天赋树
4. **本地化**：所有文本都通过 Messages 系统进行本地化支持
5. **UI 集成**：每个子职业都有对应的 UI 图标，用于职业选择界面
6. **平衡性**：各子职业在设计时考虑了相互之间的平衡性