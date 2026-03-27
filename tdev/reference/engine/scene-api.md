# Scene API 参考

## 类声明
```java
public class Scene extends Group
```

## 类职责
Scene类是Noosa引擎中表示游戏场景的基类，继承自Group。它作为游戏的主要容器，管理场景中的所有可视元素和交互对象。Scene负责处理场景的生命周期事件，包括创建、销毁、暂停和恢复，并提供默认的按键事件处理（特别是返回键）。

## 关键字段
| 字段名 | 类型 | 访问级别 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| keyListener | Signal.Listener<KeyEvent> | private | null | 处理键盘事件的监听器 |

## 场景生命周期 (Scene)

### 创建阶段
- **create()**: 初始化场景，注册键盘事件监听器。当检测到返回键按下时，调用onBackPressed()方法。

### 销毁阶段
- **destroy()**: 清理资源，移除键盘事件监听器，并调用父类的destroy()方法销毁所有子对象。

### 暂停/恢复
- **onPause()**: 当应用暂停时调用（如切换到后台）。默认实现为空，子类可重写。
- **onResume()**: 当应用恢复时调用（如从后台回到前台）。默认实现为空，子类可重写。

### 其他方法
- **camera()**: 返回主相机实例（Camera.main），用于场景的视图控制。
- **onBackPressed()**: 处理返回键按下事件，默认实现是结束当前游戏实例。

## 使用示例
```java
public class GameScene extends Scene {
    
    @Override
    public void create() {
        super.create();
        
        // 添加游戏UI元素
        add(new GameUI());
        add(new HeroSprite());
        
        // 初始化游戏逻辑
        initializeGame();
    }
    
    @Override
    public void destroy() {
        // 清理游戏特定资源
        cleanupGameResources();
        super.destroy();
    }
    
    @Override
    public void onPause() {
        // 保存游戏状态
        saveGameState();
    }
    
    @Override
    public void onResume() {
        // 恢复游戏状态
        loadGameState();
    }
    
    @Override
    protected void onBackPressed() {
        // 自定义返回键行为，显示暂停菜单而不是直接退出
        showPauseMenu();
    }
}
```

## 相关子类
- StartScene: 游戏开始场景
- GameScene: 主游戏场景  
- RankingsScene: 排行榜场景
- PreferencesScene: 设置场景
- 其他具体的游戏场景实现类