# Group API 参考

## 类声明
```java
public class Group extends Gizmo
```

## 类职责
Group类是Noosa引擎中的容器类，用于管理一组Gizmo对象（可视组件）。它继承自Gizmo，提供了添加、删除、排序和遍历子对象的功能。Group负责维护其子对象的生命周期，并在更新和绘制阶段按顺序处理所有存活的子对象。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| members | ArrayList<Gizmo> | protected | new ArrayList<>() | 存储所有子对象的列表 |
| length | int | public | 0 | 子对象的数量，比调用members.size()更快 |

## 子对象管理 (Group)

### 添加子对象
- **add(Gizmo g)**: 添加子对象到组中。如果该对象已有父级，则先从原父级中移除。
- **addToFront(Gizmo g)**: 添加子对象到组的前面位置。
- **addToBack(Gizmo g)**: 添加子对象到组的后面位置。

### 移除子对象
- **remove(Gizmo g)**: 完全移除子对象，将其从列表中删除并减少length计数。
- **erase(Gizmo g)**: 快速移除子对象，用null替换而不是实际删除，保持列表结构。
- **clear()**: 清空所有子对象。

### 对象重用
- **recycle(Class<? extends Gizmo> c)**: 获取指定类型的第一个可用（已销毁）对象，如果没有则创建新实例并添加到组中。
- **getFirstAvailable(Class<? extends Gizmo> c)**: 获取指定类型的第一个可用对象（exists为false的对象）。

### 层级控制
- **bringToFront(Gizmo g)**: 将指定子对象移到列表末尾（绘制时在最上层）。
- **sendToBack(Gizmo g)**: 将指定子对象移到列表开头（绘制时在最下层）。
- **replace(Gizmo oldOne, Gizmo newOne)**: 替换组中的一个对象。

### 查询和统计
- **indexOf(Gizmo g)**: 返回子对象在组中的索引位置。
- **countLiving()**: 统计存活（exists且alive为true）的子对象数量。
- **countDead()**: 统计死亡（alive为false）的子对象数量。
- **random()**: 随机返回一个子对象。

### 排序
- **sort(Comparator c)**: 根据指定的比较器对子对象进行排序。

## 使用示例
```java
// 创建组
Group group = new Group();

// 添加子对象
Sprite sprite = new Sprite();
group.add(sprite);

// 将精灵移到前面
group.bringToFront(sprite);

// 获取可用的对象进行重用
Actor actor = group.recycle(Actor.class);
if (actor == null) {
    // 如果没有可用对象，recycle会自动创建并添加
}

// 统计存活对象
int livingCount = group.countLiving();
```

## 相关子类
- Scene: Group的直接子类，代表游戏场景
- 其他自定义的游戏对象容器类