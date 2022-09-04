package mirai

import net.mamoe.mirai.Bot

import kotlin.text.toRegex

import kotlin.text.split
import kotlin.text.get

import net.mamoe.mirai.message.data.MessageSource.Key.quote


val groupId by config.key(659151737L, "广播的群号,0代表不启用")
val groupId2 by config.key(605315932L, "广播的群号,0代表不启用")

fun broadcast(msg: String) {
    if (groupId <= 0) return
    Bot.instancesSequence.forEach {
        launch {
            val group = it.getGroup(groupId)
            group?.sendMessage(msg)
            val group2 = it.getGroup(groupId2)
            group2?.sendMessage(msg)
        }
    }
}

fun gameover(msg: Long) {
    if (groupId <= 0) return
    Bot.instancesSequence.forEach {
        launch {
            val group = it.getGroup(groupId)
            group?.sendMessage("游戏已结束！")
        }
    }
}

export(::broadcast)
export(::gameover)


command("send", "向此服务器交流群发送消息") {
    this.type = CommandType.Client
    usage = "<消息(忽略空格)>"
    body{
        val text = player?.name() + "：" + "\"" + arg.get(0).substring(0,20) + "\""
        val text2 = "[yellow]" + player?.name() + " 对QQ群说：" + "\"" + arg.get(0).substring(0,20) + "\""
        broadcast(text)
        player?.sendMessage("[royal]消息发送成功！".with())
        broadcast(text2.with())
    }
}