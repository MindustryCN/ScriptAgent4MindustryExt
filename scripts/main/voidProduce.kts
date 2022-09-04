@file:Depends("coreMindustry/contentsLoader")
@file:Depends("wayzer/maps", "提前获取换图信息")

package private.map

import cf.wayzer.ContentsLoader
import wayzer.MapChangeEvent


val packName = "EX-factoryNotConsume"
val desc = "[violet]无中生有模式[orange]: 工厂生产不需要原料和液体\n  [yellow](使用[sky]内容包动态加载器[]MOD获得最佳体验)"
//val desc = "[violet]无中生有模式[orange]: 工厂生产不需要原料和液体[white]本服独创[]"
listenTo<MapChangeEvent>(Event.Priority.Watch) {
    val enable = rules.tags.containsKey("@voidProduce")//地图标识
  // val enable = true
    if (enable)
        ContentsLoader.Api.toLoadPacks.add(packName)
    registerVar("scoreBroad.ext.voidProduceMode", "无中生有状态显示", desc.takeIf { enable })
}
// fun showInfo(player: Player) {
//     player.sendMessage(
//         """
//         [violet]当前地图为[gold]塔防模式
//         [magenta]===规则介绍===
//         [violet]1.怪物只会攻击核心,可以放心建筑
//         2.过于靠近怪物的兵可能会被杀掉
//         3.道路上不准建筑(原因由上)
//         4.核心附加的道路允许放置武装传送带
//         5.怪物会掉落大量战利品(不限距离)
//         6.部分物品被禁用(保证平衡)
//     """.trimIndent(), MsgType.InfoMessage
//     )
// }
// listen<EventType.PlayEvent> {
//     if (enable) Core.app.post {
//         Groups.player.forEach(::showInfo)
//     }
// }
// listen<EventType.PlayerJoin> {
//     if (enable) showInfo(it.player)
// }