package com.yourname.hatelock;

import java.util.ArrayList;
import java.util.List;

public class AggroConfig {

    // ================= 基本设置 =================
    public boolean enabled = true;        // 模组总开关
    public int radius = 128;              // 扫描半径
    public int scanInterval = 40;         // 扫描间隔，单位 tick（20tick = 1秒）

    // ================= 规则 =================
    public List<AggroRule> rules = new ArrayList<>();

}