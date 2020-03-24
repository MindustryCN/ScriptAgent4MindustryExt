//WayZer 版权所有(请勿删除版权注解)
import arc.util.Align
import cf.wayzer.script_agent.util.ScheduleTask
import mindustry.gen.Call
import java.time.Duration
import java.util.*

name.set("扩展功能: 积分榜")
//建议只修改下面一段,其他地方代码请勿乱动
val msg = """
    [magenta]欢迎[goldenrod]{player.name}[magenta]来到WZ服务器
    [violet]当前地图为: [orange]{map.name}
    [violet]服务器FPS: [orange]{fps}
    [royal]输入/broad可以开关该显示
""".trimIndent()

val disabled = mutableSetOf<String>()

registerScheduleTask("loop", true, ScheduleTask<Unit> {
    playerGroup.forEach {
        if(disabled.contains(it.uuid))return@forEach
        if (it.isMobile) {
            Call.onInfoPopup(it.con, msg.with("player" to it).toString(), 2.013f, Align.topLeft, 210, 0, 0, 0)
        } else
            Call.onInfoPopup(it.con, msg.with("player" to it).toString(), 2.013f, Align.topLeft, 155, 0, 0, 0)
    }
    return@ScheduleTask Duration.ofSeconds(2).delayToDate()
})

command("broad","开关积分板显示",type = CommandType.Client){arg,p->
    if(!disabled.remove(p!!.uuid))
        disabled.add(p.uuid)
    p.sendMessage("[green]切换成功")
}

onEnable{
    getScheduleTask<Unit>("loop").start()
}