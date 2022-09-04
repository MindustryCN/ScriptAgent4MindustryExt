package main
//WayZer 版权所有(请勿删除版权注解)
import arc.util.Align
import mindustry.gen.Groups
import java.time.Duration

name = "扩展功能: 积分榜"
//建议只修改下面一段,其他地方代码请勿乱动
val msg = """
[magenta]欢迎[goldenrod]{player.name}[magenta]来到[#ff2a00]梦[#b41e00]魔[#781400]短[#500d00]祷
[magenta]本服已经与STEAM飘飘服合并
[violet]当前地图为: [yellow][{map.id}][orange]{map.name}
[violet]本局游戏时间: [orange]{state.gameTime:分钟}
[yellow]当前服务器：[blue]备用服
[sky]进群659151737绑定即享各种有用技能！
[royal]输入/broad可以开关该显示
""".trimIndent()

val disabled = mutableSetOf<String>()

command("broad", "开关积分板显示") {
    this.type = CommandType.Client
    body {
        if (!disabled.remove(player!!.uuid()))
            disabled.add(player!!.uuid())
        reply("[green]切换成功".with())
    }
}

onEnable {
    launch {
        while (true) {
            withContext(Dispatchers.game) {
                Groups.player.forEach {
                    if (disabled.contains(it.uuid())) return@forEach
                    val mobile = it.con?.mobile == true
                    Call.infoPopup(
                        it.con, msg.with("player" to it, "receiver" to it).toString(),
                        2.013f, Align.topLeft, if (mobile) 210 else 155, 0, 0, 0
                    )
                }
            }
            delay(Duration.ofSeconds(2).toMillis())
        }
    }
}
//2.013f