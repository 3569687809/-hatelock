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

RodRailgun
