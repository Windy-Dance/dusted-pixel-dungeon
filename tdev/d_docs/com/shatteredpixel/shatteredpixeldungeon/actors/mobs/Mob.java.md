# Mob 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\actors\mobs\Mob.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs |
| 类类型 | abstract class |
| 继承关系 | extends Char |
| 代码行数 | 1511 |

## 2. 类职责说明
Mob是所有移动实体（敌人、盟友、NPC）的基类，提供了AI状态机、目标选择、战斗逻辑、寻路系统和战利品掉落等核心功能。该抽象类定义了标准的游戏内可移动角色行为框架。

## 4. 继承与协作关系
```mermaid
classDiagram
    Char <|-- Mob
    Mob <|-- NPC
    Mob <|-- 具体怪物类
    
    class Char {
        +int pos
        +int HP
        +int HT
        +Alignment alignment
        +CharSprite sprite
        +boolean[] fieldOfView
        +abstract boolean act()
        +void damage(int dmg, Object src)
        +void die(Object cause)
    }
    
    class Mob {
        +AiState SLEEPING
        +AiState HUNTING
        +AiState WANDERING
        +AiState FLEEING
        +AiState PASSIVE
        +Class~CharSprite~ spriteClass
        +int target
        +int defenseSkill
        +int EXP
        +int maxLvl
        +Char enemy
        +boolean enemySeen
        +boolean alerted
        +Object loot
        +float lootChance
        
        +Char chooseEnemy()
        +void aggro(Char ch)
        +void clearEnemy()
        +boolean heroShouldInteract()
        +void beckon(int cell)
        +Item createLoot()
        +void rollToDropLoot()
        +float lootChance()
        +boolean surprisedBy(Char enemy)
        +boolean canAttack(Char enemy)
        +boolean getCloser(int target)
        +boolean getFurther(int target)
        +void updateSpriteState()
        +float attackDelay()
        +boolean doAttack(Char enemy)
        +void onAttackComplete()
        +int defenseSkill(Char enemy)
        +int defenseProc(Char enemy, int damage)
        +float speed()
        +String description()
        +String info()
        +Notes.Landmark landmark()
        
        +static void holdAllies(Level level)
        +static void restoreAllies(Level level, int pos)
    }
    
    class NPC {
        +HP = HT = 1
        +EXP = 0
        +alignment = Alignment.NEUTRAL
        +state = PASSIVE
        
        +boolean act()
        +void beckon(int cell)
    }
    
    class AiState {
        <<interface>>
        +boolean act(boolean enemyInFOV, boolean justAlerted)
    }
    
    Mob --> AiState : 使用