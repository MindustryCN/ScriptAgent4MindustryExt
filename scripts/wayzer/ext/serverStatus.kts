package wayzer.ext

command("status", "获取服务器信息") {
    aliases = listOf("服务器状态")
    body {
        reply(
            """
            |[green]服务器状态[]
            |   [green]地图: [ [yellow]{map.id} [green]][yellow]{map.name}[green] 模式: [yellow]{map.mode} [green]波数[yellow]{state.wave}
            |   [green]{tps} TPS, {heapUse} MB used[]
            |   [green]总单位数: {state.allUnit} 玩家数: {state.playerSize}
            |   [yellow]被禁封总数: {state.allBan}
            """.trimMargin().with()
        )
    }
}

command("skills", "查询服务器目前可用技能") {
    aliases = listOf("可用技能")
    body {
        reply(
            """
            |[green]可用技能[]
            |        [green]/mono 召唤矿机 只要绑定就能解锁
            |        /bloodArmor 用血量换取盾 三级解锁
            |        /healing 治疗所有伤痛 三级解锁
            |        /creeperbox 召唤随机t1-t4爬爬 五级解锁
            |        /boat 召唤随机t1船，五级解锁
            |        /items 获得随机500资源，五级解锁
            |        /angry 获得所有无限时间正面属性，八级解锁
            |        /flare 召唤10倍血量的星辉，八级解锁
            |        /creeper 召唤爬虫群，十级解锁
            |        /poly 召唤幻型，十级解锁
            |        /startCall 召唤贝塔群，十二级解锁
            |        /cut 分裂自身，十二级解锁
            |        /tank 召唤小坦克，十二级解锁
            |        /zenith 召唤无法攻击苍穹，十五级解锁
            |        /fortress 召唤全宇宙最慢的堡垒，十五级解锁
            |        /super 暂时性无敌，十八级解锁
            |        /quasar 召唤耀星，二十级解锁
            |        /vela 召唤低血并无法行动的灾星，三十级解锁
            |        /latum 召唤残血latum，三十级解锁
            |        [red]更多技能coming soon......[]
            """.trimMargin().with()
        )
    }
}