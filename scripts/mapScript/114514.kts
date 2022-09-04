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


fun itemDropCopper(x :Float, y :Float, maxRange: Float = 16f){
    var dorpAmount = 1
    val dropX = x - maxRange / 2 + Random.nextFloat() * maxRange
    val dropY = y - maxRange / 2 + Random.nextFloat() * maxRange
    val dorpTime = Time.millis()
    launch(Dispatchers.game){
        while(true){
            delay(200)
            if (Time.millis() - dorpTime >= 60_000) break
            val units = buildList {
                Units.nearby(null, dropX, dropY, 54 * 8f) {
                    add(it)
                }
            }
            if (units.isEmpty()) continue
            units.forEach{
                if (it.within(dropX, dropY, it.hitSize + 4) && it.stack.amount < it.itemCapacity()){
                    if (it.stack.amount + dorpAmount > it.itemCapacity()) {
                        dorpAmount -= it.itemCapacity() - it.stack.amount
                        it.addItem(Items.copper, it.itemCapacity())
                    }else{
                        it.addItem(Items.copper,dorpAmount)
                        return@launch
                    }

                }
                Call.label(
                    it.player?.con ?: return@forEach, "$itemCopper \n[lightgray]${((Time.millis() - dorpTime) / 1000)}s/30s", 0.21f,
                    dropX, dropY
                )
            }
        }
    }
}
    
fun itemDropLead(x :Float, y :Float, maxRange: Float = 16f){
    var dorpAmount = 1
    val dropX = x - maxRange / 2 + Random.nextFloat() * maxRange
    val dropY = y - maxRange / 2 + Random.nextFloat() * maxRange
    val dorpTime = Time.millis()
    launch(Dispatchers.game){
        while(true){
            delay(200)
            if (Time.millis() - dorpTime >= 60_000) break
            val units = buildList {
                Units.nearby(null, dropX, dropY, 54 * 8f) {
                    add(it)
                }
            }
            if (units.isEmpty()) continue
            units.forEach{
                if (it.within(dropX, dropY, it.hitSize + 4) && it.stack.amount < it.itemCapacity()){
                    if (it.stack.amount + dorpAmount > it.itemCapacity()) {
                        dorpAmount -= it.itemCapacity() - it.stack.amount
                        it.addItem(Items.lead, it.itemCapacity())
                    }else{
                        it.addItem(Items.lead,dorpAmount)
                        return@launch
                    }

                }
                Call.label(
                    it.player?.con ?: return@forEach, "$itemLead\n[lightgray]${((Time.millis() - dorpTime) / 1000)}s/30s", 0.21f,
                    dropX, dropY
                )
            }
        }
    }
}

fun itemDropTitanium(x :Float, y :Float, maxRange: Float = 16f){
    var dorpAmount = 1
    val dropX = x - maxRange / 2 + Random.nextFloat() * maxRange
    val dropY = y - maxRange / 2 + Random.nextFloat() * maxRange
    val dorpTime = Time.millis()
    launch(Dispatchers.game){
        while(true){
            delay(200)
            if (Time.millis() - dorpTime >= 60_000) break
            val units = buildList {
                Units.nearby(null, dropX, dropY, 54 * 8f) {
                    add(it)
                }
            }
            if (units.isEmpty()) continue
            units.forEach{
                if (it.within(dropX, dropY, it.hitSize + 4) && it.stack.amount < it.itemCapacity()){
                    if (it.stack.amount + dorpAmount > it.itemCapacity()) {
                        dorpAmount -= it.itemCapacity() - it.stack.amount
                        it.addItem(Items.titanium, it.itemCapacity())
                    }else{
                        it.addItem(Items.titanium,dorpAmount)
                        return@launch
                    }

                }
                Call.label(
                    it.player?.con ?: return@forEach, "$itemTitanium\n[lightgray]${((Time.millis() - dorpTime) / 1000)}s/30s", 0.21f,
                    dropX, dropY
                )
            }
        }
    }
}