package wayzer.ext

import mindustry.game.EventType

val type by config.key(MsgType.InfoMessage, "发送方式")
val template by config.key("""
    [orange]服务器游玩必看须知：
    [blue]一：萌新最好先通关陨石带了解各种游戏特性后再来多人联机，可以看看别的玩家的建筑来观摩观摩
    [brown]二：如果有人拆你的建筑，先再看看他后来发了些什么消息，如果是什么蓝图不太好、规划冲突等理由，大可不必无理取闹（点名批评抽水机熔毁爷新）
    [blue]三：准备起大型蓝图或很贵的建筑时，一定要注意一下当前资源还有多少，千万不能盲目起建筑导致资源短缺
    [brown]四：对应第二条，如果你发现有人建的建筑极不恰当，可以拆除并更改，但是聊天栏必须要给出理由，由于本服成分复杂，聊天口气尽量友善点。
    [pink]五：文明可是十二价值观其中的一个，聊天时不能口吐芬芳、问候家人、扣大帽、刷屏等不文明操作。如果发现有玩家确实这么干，可以投票踢出或截图并记录该玩家前三位id发给好游快爆的小恶魔封禁处理
    [brown]六：蓝图有些也是高血压蓝图，例如抽水机水电，黄沙油硅。不要乱搞，废材费电。
    [pink]七：严禁熊服，如反应堆炸核心、故意搞乱传送带、大范围故意拆除生产工厂，如发现类似玩家，可以投票踢出或截图并记录该玩家前三位id发给好游快爆的小恶魔封禁处理
    [green]八：/votekick kick指令默认踢出15分钟
""".trimIndent(), "欢迎信息模板")

listen<EventType.PlayerJoin> {
    it.player.sendMessage(template.with(),type)
}