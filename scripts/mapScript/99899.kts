@file:Depends("coreMindustry/utilNext", "调用菜单")
@file:Depends("coreMindustry/utilMapRule", "修改核心单位,单位属性")

package mapScript

import arc.graphics.Color
import arc.struct.ObjectIntMap
import arc.struct.ObjectMap
import arc.util.Align
import arc.util.Time
import coreLibrary.lib.util.loop
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import mindustry.Vars
import mindustry.content.*
import mindustry.entities.Units
import mindustry.game.Team
import mindustry.gen.Call
import mindustry.gen.Groups
import mindustry.gen.Iconc.*
import mindustry.gen.Player
import mindustry.type.StatusEffect
import mindustry.type.UnitType
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.blocks.storage.CoreBlock.CoreBuild
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.random.Random

val menu = contextScript<coreMindustry.UtilNext>()


val playerKiller: ObjectIntMap<Boolean> = ObjectIntMap()//被指定杀手的团队

fun Player.toKiller(){
    playerKiller.put(uuid(), playerMoney.get(uuid()) = true)
    sendMessage("[yellow]你的职业是：[red]杀手\n" +
            "[red]隐藏与杀戮")
}

onEnable {
    launch(Dispatchers.game){
        var leftTime = 120
        while (leftTime > 0) {
            broadcast("[yellow]正在确认杀手身份中......还有{time}秒".with("time" to leftTime), quite = true)
            delay(30_000)
            leftTime -= 30
        }
        var count = ceil(Groups.player.size() * 0.2).toInt()
        
        Groups.player.shuffled().take(count).forEach {
            it.toKiller()
        }
        broadcast("[yellow]已确认杀手！做出你的行动！".with(), quite = true)
        state.rules.pvp = true
    }
}