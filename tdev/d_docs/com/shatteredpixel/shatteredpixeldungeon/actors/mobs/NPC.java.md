# NPC 类文档

## 1. 基本信息
| 属性 | 值 |
|------|-----|
| 文件路径 | D:\Develop\Workspace\DustedPixelDungeon\core\src\main\java\com\shatteredpixel\shatteredpixeldungeon\actors\mobs\npcs\NPC.java |
| 包名 | com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs |
| 类类型 | abstract class |
| 继承关系 | extends Mob |
| 代码行数 | 51 |

## 2. 类职责说明
NPC是所有非玩家角色的基类，继承自Mob但具有中立阵营、被动状态和固定的生命值设置。主要用于游戏中的对话角色、商人和其他功能性NPC。

## 4. 继承与协作关系
```mermaid
classDiagram
    Char <|-- Mob
    Mob <|-- NPC
    NPC <|-- 具体NPC类
    
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