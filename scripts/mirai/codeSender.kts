package mirai

import net.mamoe.mirai.message.data.MessageSource.Key.quote



globalEventChannel().subscribeMessages {
    case("绑定") {
        //if (this is GroupMessageEvent) {
        //    subject.sendMessage(message.quote() + "绑定请加好友并私聊我！")
        //    return@case
        //}
        val qq = sender.id
        val generate = depends("wayzer/user/ext/profileBind")?.import<(Long) -> Int>("generate")
        if (generate == null) {
            subject.sendMessage("绑定服务暂不可用，请联系管理")
            return@case
        }
        subject.sendMessage(
            """
            你好${sender.nick}
            复制'/bind ${generate(qq).toString().padStart(6, '0')}'到游戏中，即可完成绑定。
        """.trimIndent()
        )
    }
    case("服务器地址") {
        subject.sendMessage(
            """
            主服：dc8.i8mc.cn:8169
            单挑服：dc8.i8mc.cn:8920
            大厅服：150.158.213.23:8888
            """.trimIndent()
        )
        return@case
    }
    case("帮助") {
        subject.sendMessage(
            """
            下面的指令你可以使用
            服务器地址（查询可用的服务器地址）
            服务器状态（查询服务器的情况）
            服务器玩家（查询当前在线玩家）
            服务器封禁（查询当前服务器黑名单）
            服务器技能（查询服务器可用技能）
            绑定（以qq号绑定服务器）
            签到（绑定可用，每天签到获得经验）
            个人信息（查询你个人的账号信息）
            """.trimIndent()
        )
        return@case
    }
    
    case("服务器技能") {
        subject.sendMessage(
            """
            可用技能:
            /mono 召唤矿机 只要绑定就能解锁
            /bloodArmor 用血量换取盾 三级解锁
            /healing 治疗所有伤痛 三级解锁
            /creeperbox 召唤随机t1-t4爬爬 五级解锁
            /boat 召唤随机t1船，五级解锁
            /items 获得随机500资源，五级解锁
            /angry 获得所有无限时间正面属性，八级解锁
            /flare 召唤10倍血量的星辉，八级解锁
            /creeper 召唤爬虫，十级解锁
            /poly 召唤幻型，十级解锁
            /startCall 召唤贝塔群，十二级解锁
            /cut 分裂自身，十二级解锁
            /zenith 召唤无法攻击苍穹，十五级解锁
            /fortress 召唤全宇宙最慢的堡垒，十五级解锁
            /super 暂时性无敌，十八级解锁
            /quasar 召唤耀星，二十级解锁
            /vela 召唤低血并无法行动的灾星，三十级解锁
            更多技能coming soon......
            """.trimIndent()
        )
        return@case
    }


//    Event.on(GameOveEvent.class, event –> {
//        if(inGameOverWait) return;
//        if(state.rules.waves)
//        {
//            subject.sendMessage("游戏结束！" + [{map.id}]{map.name} + "失败于" + {state.wave})
//        }
    
    
//    }
    
//    )
}