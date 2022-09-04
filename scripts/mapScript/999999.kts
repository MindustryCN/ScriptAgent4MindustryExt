@file:Depends("coreMindustry/utilNext", "调用菜单") @file:Depends("coreMindustry/utilMapRule", "修改核心单位,单位属性")
package mapScript

import arc.math.geom.Geometry
import arc.util.Align
import arc.util.Tmp
import coreLibrary.lib.util.loop
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.core.World
import mindustry.entities.Units
import mindustry.game.Rules.TeamRule
import mindustry.game.Team
import mindustry.gen.Groups
import mindustry.gen.Iconc
import mindustry.gen.WaterMovec
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.Tile
import mindustry.world.blocks.storage.CoreBlock.CoreBuild
import kotlin.reflect.KMutableProperty1
import mindustry.world.blocks.production.Drill
import mindustry.Vars

/**@author xem8k5*/
name = "PlanetWar"
/**部分代码直接照搬别的kts，感谢klp和miner的代码嗷*/

val menu = contextScript<coreMindustry.UtilNext>()

val playerSpawnCore: MutableMap<Player, CoreBuild?> = mutableMapOf()
val playerDrill: MutableMap<Player, Drill?> = mutableMapOf()

val spawnRadius = 5


var Player.spawnCore: CoreBuild?
    get() = playerSpawnCore[this]
    set(core) {
        playerSpawnCore[this] = core
    }

var Player.drill: Drill?
    get() = playerDrill[this]
    set(drill) {
        playerDrill[this] = drill
    }

fun Player.teamMessage(message: String) {
    Groups.player.filter { it.team() == team() }.forEach { Call.infoToast(it.con, message, 4f) }
}

fun getUnitCost(cost: Int, count: Int): Int = ((1 + count / 6f) * cost).toInt()


fun spawnUnit(core: CoreBuild?, type: UnitType, statusDur: Float, cost: Int): Boolean {

    if (core == null) return false
    if (core.items.get(Items.copper) < cost) return false
    val team = core.team
    if (!Units.canCreate(team, type)) return false
    val unit = type.create(team)
    
    var times = 0
    while (true) {
    
        Tmp.v1.rnd(spawnRadius.toFloat() * tilesize)

        val sx = core.x + Tmp.v1.x
        val sy = core.y + Tmp.v1.y

        if (unit.canPass(World.toTile(sx), World.toTile(sy))) {
            unit.set(sx, sy)
            break
        }
    
        if (++times > 20) {

            return false
        }
    }
    
    unit.apply {
        apply(StatusEffects.electrified, statusDur * 60)
        add()
    }

    core.items.remove(Items.copper, cost)
    return true
}

suspend fun Player.sendMenu() {
    menu.sendMenuBuilder<Unit>(
        this, 30_000, "提示",
        """
            [yellow]行星争霸[green]模式只需要玩家收集铜就可以搞单位
            [red]大部分在本模式可用的建筑都经过造价魔改了！客户端不同步
            [cyan]机械钻头15铜，脉冲钻头30铜，激光钻头100铜，爆破钻头300铜
            
        """.trimIndent()
    ) {
        fun spawnUnit(
        type: UnitType,
        icon: Char, 
        statusDur: Float = 0f, 
        defaultCost: Int,
        costGetter: (spawnCount: Int) -> Int = { getUnitCost(defaultCost, it) }
        ) = "${Iconc.itemCopper}${costGetter(team().data().countType(type))}" to suspend{
        
            val cost = costGetter(team().data().countType(type))
            val announce = spawnUnit(spawnCore, type, statusDur, cost)
            
            if (announce) {
                teamMessage(
                    "[#${team().color}]${team().name}[white]" +
                    "${coloredName()}[white]花费${Iconc.itemCopper}${cost}" +
                    "购买了单位${type.emoji()}"
                )
                sendMenu()
            }
            
        }
        this += listOf(
            spawnUnit(UnitTypes.crawler, Iconc.unitCrawler, 1f, 42),
            spawnUnit(UnitTypes.flare, Iconc.unitFlare, 1f, 55),
            spawnUnit(UnitTypes.risso, Iconc.unitRisso, 1f, 71),
            spawnUnit(UnitTypes.retusa, Iconc.unitRetusa, 1f, 71),
        )
        
    
    }
}

listen<EventType.TapEvent> {
    if (it.tile.block() is CoreBlock) {
        val player = it.player
        if (!player.dead() && it.tile.team() == player.team()) {
            launch(Dispatchers.game) { player.sendMenu() }
        }
    }
    
    if (it.tile.block() is Drill) {
        val player = it.player
        if (!player.dead() && it.tile.team() == player.team()) {
            val items = it.tile.build.items
            if(items != null){
                player.unit().core()?.items?.add(Items.copper, items.get(Items.copper).toInt())
                it.tile.build.items?.remove(Items.copper, items.get(Items.copper).toInt())
                
                player.unit().core()?.items?.add(Items.beryllium, items.get(Items.beryllium).toInt())
                it.tile.build.items?.remove(Items.beryllium, items.get(Items.beryllium).toInt())
            }
        
        
        }
    }
}

onEnable {
    contextScript<coreMindustry.UtilMapRule>().apply {
        registerMapRule(Blocks.coreShard::itemCapacity) { Int.MAX_VALUE }
        registerMapRule(Blocks.coreFoundation::itemCapacity) { Int.MAX_VALUE }
        registerMapRule(Blocks.coreNucleus::itemCapacity) { Int.MAX_VALUE }
        
        //钻头
        registerMapRule(Blocks.mechanicalDrill::requirements){ ItemStack.with(Items.copper, 15) }
        registerMapRule(Blocks.pneumaticDrill::requirements){ ItemStack.with(Items.copper, 50) }
        registerMapRule(Blocks.laserDrill::requirements) { ItemStack.with(Items.copper, 800) }
        registerMapRule(Blocks.blastDrill::requirements) { ItemStack.with(Items.copper, 2000) }
        
        //炮塔
        registerMapRule(Blocks.breach::requirements){ ItemStack.with(Items.copper, 200) }
        registerMapRule(Blocks.diffuse::requirements){ ItemStack.with(Items.copper, 350) }
        
        registerMapRule(Blocks.disperse::requirements){ ItemStack.with(Items.copper, 1000) }
        registerMapRule(Blocks.lustre::requirements){ ItemStack.with(Items.copper, 1600) }
        registerMapRule(Blocks.sublimate::requirements){ ItemStack.with(Items.copper, 2000) }
        
        registerMapRule(Blocks.afflict::requirements){ ItemStack.with(Items.copper, 3500) }
        registerMapRule(Blocks.titan::requirements){ ItemStack.with(Items.copper, 5000) }
        registerMapRule(Blocks.scathe::requirements){ ItemStack.with(Items.copper, 8000) }
        
        //registerMapRule(Blocks.smite::requirements){ ItemStack.with(Items.copper, 15000) }
        //registerMapRule(Blocks.malign::requirements){ ItemStack.with(Items.copper, 20000) } //几把俩最终炮台，我拿它们有什么办法
        
        //兵厂
        registerMapRule(Blocks.airFactory::requirements){ ItemStack.with(Items.copper, 200) } //mono！

    }

    launch(Dispatchers.gamePost) {
        state.teams.getActive().each { data ->
            data.core()?.run { health += 20000f }
        }
    }
    
    loop(Dispatchers.game) {
            delay(5000)
            state.teams.getActive().each { data ->
                val core = data.core() ?: return@each
                core.maxHealth = core.maxHealth + core.items.get(Items.beryllium)

                data.units.each {

                }
            }

    }
}

//TODO
//1.购买单位 √
//2.以铍作为科技物品
//3.锁科技
//4.优化钻头