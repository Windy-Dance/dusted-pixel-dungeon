# 破碎像素地牢 - 技术文档

> DustedPixelDungeon 代码库的全面技术文档

## 概述

破碎像素地牢（Shattered Pixel Dungeon）是一款使用 Java 11+ 和 libGDX 构建的肉鸽类地牢探险游戏。本文档涵盖了完整的架构、系统和游戏数据。

## 快速链接

- [架构概览](architecture/overview.md)
- [行动者系统](systems/actor-system.md)
- [物品系统](systems/item-system.md)
- [关卡系统](systems/level-system.md)
- [游戏数据](game-design/)

## 项目结构

| 目录 | 用途 |
|------|------|
| architecture/ | 模块结构、设计模式 |
| systems/ | 核心游戏系统文档 |
| engine/ | Noosa 渲染引擎 |
| api/ | 类和包索引 |
| game-design/ | 怪物属性、物品数据、游戏机制 |

## 核心类

| 类名 | 用途 |
|------|------|
| ShatteredPixelDungeon | 游戏主入口 |
| Dungeon | 游戏状态单例 |
| Actor | 回合制调度器 |
| Hero | 玩家角色 |
| Level | 地牢楼层 |

## 入门指南

1. 阅读 [架构概览](architecture/overview.md)
2. 探索 [行动者系统](systems/actor-system.md)
3. 查看 [游戏数据](game-design/) 获取属性信息

## 外部资源

- [官方网站](https://shatteredpixel.com)
- [源代码](https://github.com/00-Evan/shattered-pixel-dungeon)