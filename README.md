# Hatelock

一个轻量级 Fabric 模组，用于强制指定怪物将仇恨锁定到指定玩家。

---

## 功能特性

* 强制怪物攻击指定玩家
* 支持永久仇恨规则
* 支持临时仇恨规则
* 动态 TAB 补全
* 自动显示中文怪物名称
* 支持配置文件热重载
* 支持修改仇恨检测范围
* JSON 配置文件存储

---

## 支持版本

* Minecraft 1.21.11
* Fabric Loader

---

# 指令大全

## 开关系统

```text
/aggro toggle
```

开启或关闭仇恨系统。

---

## 重载配置

```text
/aggro reload
```

无需重启服务器即可重新加载配置文件。

---

## 修改检测范围

```text
/aggro radius <数值>
```

示例：

```text
/aggro radius 64
/aggro radius 128
```

---

## 查看规则

```text
/aggro list
```

显示当前所有仇恨规则。

永久规则显示为红色。

临时规则显示为黄色。

---

# 添加仇恨规则

## 永久规则

```text
/aggro set <玩家名> <怪物ID>
```

示例：

```text
/aggro set Steve zombie
```

---

## 临时规则

```text
/aggro set <玩家名> <怪物ID> <时间>
```

示例：

```text
/aggro set Steve zombie 60s
/aggro set Alex skeleton 5m
/aggro set Steve creeper 1h
```

支持单位：

| 单位 | 含义 |
| -- | -- |
| s  | 秒  |
| m  | 分钟 |
| h  | 小时 |
| d  | 天  |

组合示例：

```text
30s
10m
2h
1d
1d12h30m
```

---

# 删除规则

删除指定规则：

```text
/aggro remove <玩家名> <怪物ID>
```

示例：

```text
/aggro remove Steve zombie
```

删除玩家全部规则：

```text
/aggro remove <玩家名>
```

示例：

```text
/aggro remove Steve
```

---

# 配置文件

路径：

```text
config/mobaggro/aggro.json
```

示例：

```json
{
  "enabled": true,
  "radius": 128,
  "rules": [
    {
      "player": "Steve",
      "mobId": "zombie",
      "expireAt": -1
    }
  ]
}
```

---

## 字段说明

| 字段       | 说明              |
| -------- | --------------- |
| enabled  | 是否启用系统          |
| radius   | 仇恨检测范围          |
| player   | 玩家名称            |
| mobId    | 怪物 ID           |
| expireAt | -1 为永久，否则为过期时间戳 |

---

# 开源协议

MIT License

---

# 作者

空空  /  GPT(协助开发)

# 💬 Q & A（开发说明）
Q：为什么要做这个模组？

A：
我在网上找了很久，没有找到一个可以自由配置1.21.11 Fabric版本“任意怪物对任意玩家仇恨关系”的模组。

虽然有一些类似功能的方案，比如数据包或者复杂指令系统，但：

数据包配置比较麻烦
指令实现太繁琐
灵活性也不够

所以我就干脆自己做了一个模组，来解决这个问题。

Q：这个模组是你一个人做的吗？

A：
是的，基本是个人开发。

我本身是编程小白，只对 Java 有一点基础了解。

Minecraft 的源码结构确实比较复杂，一开始看起来非常吃力。

这个项目主要是在 ChatGPT 的辅助下完成开发的，边学边做。

Q：以后还会更新吗？

A：
会的，后续计划包括：

支持更多 Minecraft 版本
优化仇恨系统逻辑
增加更多自定义规则
可能加入 GUI 配置界面（看情况）
Q：这个模组适合什么人用？

A：
适合：

想做自定义怪物行为的玩家
服务器管理员
喜欢整活 / 自定义机制的人

不太适合：

只想纯生存、不想折腾配置的玩家
🧠 最后

这个项目本质上是一个“学习型作品”。

如果后续功能越来越多，我会继续把它完善成一个更稳定的系统。