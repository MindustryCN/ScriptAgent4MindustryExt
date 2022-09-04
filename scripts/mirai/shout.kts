package mirai
import net.mamoe.mirai.message.data.MessageSource.Key.quote

import coreMindustry.lib.broadcast
import kotlin.text.split
import kotlin.text.get

import net.mamoe.mirai.Bot


globalEventChannel().subscribeGroupMessages {
    
    contains(".发送", true).reply {
        val messagett = this.message.content.split(".发送 ")
        subject.sendMessage(message.quote() + "消息发送成功！")
        broadcast("{Nickname}: \"{s}\"".with("Nickname" to sender.nick, "s" to messagett.get(1).substring(0,20)))
    }
    contains(".send", true).reply {
        val messagett = this.message.content.split(".send ")
        subject.sendMessage(message.quote() + "消息发送成功！")
        broadcast("{Nickname}: \"{s}\"".with("Nickname" to sender.nick, "s" to messagett.get(1).substring(0,20)))
    }

}