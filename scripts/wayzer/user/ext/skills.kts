@file:Depends("wayzer/user/userService")
package wayzer.user.ext

import arc.util.io.Writes
import mindustry.content.Blocks
import mindustry.content.UnitTypes
import mindustry.gen.Building
import wayzer.user.ext.Skills.Api.skill
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.time.Duration


import cf.wayzer.placehold.DynamicVar
import cf.wayzer.placehold.PlaceHoldApi.with

import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.entities.Units
import mindustry.Vars
import mindustry.type.Item

import kotlin.math.min
import kotlin.math.max

import mindustry.world.Block;
import mindustry.game.Team

import mindustry.world.*;
import mindustry.world.blocks.environment.*;

@Savable(false)
val used = mutableMapOf<String, Long>()
customLoad(::used, used::putAll)
listen<EventType.ResetEvent> { used.clear() }

@Suppress("unused")
companion object Api {
    lateinit var script: Skills
    private val used get() = script.used

    @DslMarker
    annotation class SkillScopeMarker

    @Suppress("MemberVisibilityCanBePrivate")
    class SkillScope(val name: String, val player: Player, val ctx: CommandContext) {
        @SkillScopeMarker
        fun returnReply(msg: PlaceHoldString): Nothing = ctx.returnReply(msg)

        @SkillScopeMarker
        fun checkNotPvp() {
            if (state.rules.pvp)
               returnReply("[red]当前模式禁用".with())
        }

        @SkillScopeMarker
        fun checkWave(Waves: Int) {
            if (state.wave < Waves)
                returnReply("[red]波数不符合要求".with())
        }
        


        /** @param coolDown in ms,  -1一局冷却 */
        //$id
        @SkillScopeMarker
        fun checkOrSetCoolDown(coolDown: Int) {
            val id = PlayerData[player.uuid()].profile?.id?.value ?: 0
            val key = "${name}@$id"
            if (key in used) {
                if (coolDown == -1)
                    returnReply("[red]该技能每局限用一次".with())
                else if (used[key]!! >= System.currentTimeMillis())
                    returnReply("[red]技能冷却，还剩{time:秒}".with("time" to Duration.ofMillis(used[key]!! - System.currentTimeMillis())))
            }
            used[key] = System.currentTimeMillis() + coolDown
        }

        @SkillScopeMarker
        fun broadcastSkill(skill: String) = broadcast(
            "[yellow][技能][green]{player.name}[white]使用了[green]{skill}[white]技能."
                .with("player" to player, "skill" to skill), quite = true, type = MsgType.InfoToast
        )
    }

    /**
     *
     */
    fun Script.skill(name: String, desc: String, vararg aliases: String, body: SkillScope.() -> Unit) {
        command(name, desc) {
            permission = "wayzer.user.skills.$name"
            this.aliases = aliases.toList()
            type = CommandType.Client
            body {
                @Suppress("MemberVisibilityCanBePrivate")
                if (player!!.dead())
                    returnReply("[red]你已死亡".with())
                SkillScope(name, player!!, this).body()
            }
        }
    }

    fun syncTile(vararg builds: Building) {
        val outStream = ByteArrayOutputStream()
        val write = DataOutputStream(outStream)
        builds.forEach {
            write.writeInt(it.pos())
            it.writeAll(Writes.get(write))
        }
        Call.blockSnapshot(builds.size.toShort(), outStream.toByteArray())
    }
}
Api.script = this

skill("mono", "技能: 召唤采矿机,一局限一次,PVP禁用", "矿机") {
    if (state.rules.bannedBlocks.contains(Blocks.airFactory))
        returnReply("[red]该地图采矿机已禁封,禁止召唤".with())
    checkNotPvp()
    checkOrSetCoolDown(-1)
    val unit = player.unit()
    UnitTypes.mono.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
    }
    broadcastSkill("采矿机?")
}

skill("creeper", "技能: 召唤四只爬虫,两分钟冷却,PVP禁用", "爬虫") {
    checkNotPvp()
    checkOrSetCoolDown(120000)
    val unit = player.unit()

    UnitTypes.crawler.create(player.team()).apply {
        set(unit.x, unit.y)
        shield = 99999f
        add()
    }
    UnitTypes.crawler.create(player.team()).apply {
        set(unit.x, unit.y)
        shield = 99999f
        add()
    }
    UnitTypes.crawler.create(player.team()).apply {
        set(unit.x, unit.y)
        shield = 99999f
        add()
    }
    UnitTypes.crawler.create(player.team()).apply {
        set(unit.x, unit.y)
        shield = 99999f
        add()
    }
    broadcastSkill("Creeper?")
}



skill("kill", "技能: 自杀,5秒冷却", "自杀") {
    checkOrSetCoolDown(5000)
    val unit = player.unit()
    
    unit.kill()
    
    broadcastSkill("自杀")
}



skill("boat", "技能: 随机召唤潜螺或棱鱼,120秒冷却,PVP禁用（5%出龙王！）", "船只") {
    checkNotPvp()
    checkOrSetCoolDown(120000)
    
    val boat = (0 until 101).random()
    val unit = player.unit()
    
    if(boat <= 50)
    {
        UnitTypes.retusa.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }
    else if(boat <= 98)
    {
        UnitTypes.risso.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }    
    }
    else
    {
        UnitTypes.navanax.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }      
    }
    broadcastSkill("船只？")
}


skill("bloodArmor", "技能: 把自己的一半血量转换成两倍护盾，60秒冷却", "血盾") {
    checkOrSetCoolDown(60000)
    checkNotPvp()
    val unit = player.unit()

    unit.shield = unit.shield + unit.health
    unit.health = unit.health / 2f

    unit.shield = min(unit.shield,unit.maxHealth * 2)
    
    broadcastSkill("血盾")
}


//{state.playerSize}

skill("startCall", "技能: 在前三波召唤三个可控制的贝塔，一局限一次，PVP禁用", "呼叫支援") {
    checkOrSetCoolDown(-1)
    checkNotPvp()

    val unit = player.unit()
    
    if(state.wave < 3)
    {
        UnitTypes.beta.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
        UnitTypes.beta.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
        UnitTypes.beta.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }
    else
    {
        returnReply("[red]波数已超过要求".with())
    }
    
    broadcastSkill("呼叫支援")
}

skill("poly", "技能: 召唤一个建造机，一局限一次，PVP禁用", "幻型") {
    checkOrSetCoolDown(-1)
    checkNotPvp()

    val unit = player.unit()
    
    
    UnitTypes.poly.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
        apply(StatusEffects.freezing, Float.MAX_VALUE)
    }
    
    broadcastSkill("幻型？")
}


skill("zenith", "技能: 召唤三只无法攻击的苍穹,三分钟冷却,PVP禁用", "新概念神风") {
    checkNotPvp()
    checkOrSetCoolDown(180000)
    val unit = player.unit()

    UnitTypes.zenith.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
        apply(StatusEffects.disarmed, Float.MAX_VALUE)
    }
    UnitTypes.zenith.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
        apply(StatusEffects.disarmed, Float.MAX_VALUE)
    }
    UnitTypes.zenith.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
        apply(StatusEffects.disarmed, Float.MAX_VALUE)
    }
    broadcastSkill("新概念神风")
}

skill("items", "技能: 给核心添加随机500资源，300秒冷却，PVP禁用", "物资支援") {
    checkOrSetCoolDown(300000)
    checkNotPvp()
    
    val unit = player.unit()
    var randomItem = Vars.content.items().random()
    var items = 500

    unit.core()?.items?.add(randomItem, items)
    
    broadcastSkill("物资支援，开出了"+randomItem)
}

skill("angry", "技能: 给自己控制的单位打上所有正面属性，120秒冷却", "愤怒") {
    checkOrSetCoolDown(120000)
    checkNotPvp()

    val unit = player.unit()

    unit.apply(StatusEffects.overdrive, Float.MAX_VALUE)
    unit.apply(StatusEffects.overclock, Float.MAX_VALUE)
    unit.apply(StatusEffects.boss, Float.MAX_VALUE)
    
    broadcastSkill("愤怒")
}

skill("fortress", "技能: 召唤一个全宇宙速度最慢的堡垒，240秒冷却", "堡垒") {
    checkOrSetCoolDown(240000)
    checkNotPvp()
    
    val unit = player.unit()
    
    UnitTypes.fortress.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
        apply(StatusEffects.sporeSlowed, Float.MAX_VALUE)
        apply(StatusEffects.freezing, Float.MAX_VALUE)
        apply(StatusEffects.tarred, Float.MAX_VALUE)
        apply(StatusEffects.electrified, Float.MAX_VALUE)
        apply(StatusEffects.sapped, Float.MAX_VALUE)
        apply(StatusEffects.wet, Float.MAX_VALUE)
        
        apply(StatusEffects.boss, Float.MAX_VALUE)
        
        maxHealth = 900f
        health = 900f
    }
    
    broadcastSkill("堡垒？！")
}

skill("creeperbox", "技能: 召唤随机t1-t4爬爬。限制120秒，PVP禁用", "爬爬盲盒") {
    checkOrSetCoolDown(120000)
    checkNotPvp()

    val unit = player.unit()
    val num = (0..140).random()

    if(num <= 50){
        UnitTypes.crawler.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }else if(num <= 80){
        UnitTypes.atrax.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }else if(num <= 95){
        UnitTypes.spiroct.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }else if(num <= 100){
        UnitTypes.arkyid.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }else if(num <= 125){
        UnitTypes.merui.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }else if(num <= 135){
        UnitTypes.cleroi.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }else{
        UnitTypes.anthicus.create(player.team()).apply {
            set(unit.x, unit.y)
            add()
        }
    }
    
    broadcastSkill("爬爬盲盒")
}

skill("super", "技能: 让自己无敌30秒，60秒冷却，PVP禁用", "无敌") {
    checkOrSetCoolDown(60000)
    checkNotPvp()

    val unit = player.unit()
    unit.apply(StatusEffects.invincible, 1800f)
    unit.apply(StatusEffects.disarmed, 1800f)
    
    broadcastSkill("无敌？")
}

skill("flare", "技能: 召唤一个600血的星辉，240秒冷却，PVP禁用", "星辉") {
    checkOrSetCoolDown(240000)
    checkNotPvp()

    val unit = player.unit()
    UnitTypes.flare.create(player.team()).apply {
        set(unit.x, unit.y)
        add()

        health = 600f
        maxHealth = 600f
    }
    
    broadcastSkill("星辉www")
}

skill("cut", "技能: 从自身分裂出一个相同单位，血上限都是原来的一半，300秒冷却", "分裂") {
    checkOrSetCoolDown(300000)
    checkNotPvp()

    val unit = player.unit()
    val type = player.unit().type

    if(unit.maxHealth <= type.health / 2){
        returnReply("[red]该单位无法分裂".with())
    }
    
    if(type == UnitTypes.block){
        returnReply("[red]方块无法分裂".with())
    }

    type.create(player.team()).apply {
        set(unit.x, unit.y)
        add()

        health = unit.health / 2
        maxHealth =  unit.maxHealth / 2
    }
    
        unit.health = unit.health / 2
        unit.maxHealth =  unit.maxHealth / 2
    
    broadcastSkill("分裂")
}

skill("healing", "技能: 回满血量，移除所有负面属性。冷却600秒", "治疗") {
    checkOrSetCoolDown(600000)
    checkNotPvp()

    val unit = player.unit()
    
    unit.health = unit.maxHealth

    unit.unapply(StatusEffects.unmoving)
    unit.unapply(StatusEffects.sporeSlowed)
    unit.unapply(StatusEffects.freezing)
    unit.unapply(StatusEffects.tarred)
    unit.unapply(StatusEffects.electrified)
    unit.unapply(StatusEffects.sapped)
    unit.unapply(StatusEffects.wet)
    unit.unapply(StatusEffects.disarmed)


    broadcastSkill("治疗")
}

skill("quasar", "技能: 花费200硅召唤一个耀星，360秒冷却，PVP禁用", "购买耀星") {
    checkOrSetCoolDown(240000)
    checkNotPvp()

    val unit = player.unit()
    
    unit.core()?.items?.remove(Items.silicon, 200)
    
    UnitTypes.quasar.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
    }
    
    broadcastSkill("耀星！")
}


skill("tank", "技能: 召唤三个减速的t2坦克，冷却600秒", "坦克") {
    checkOrSetCoolDown(600000)
    checkNotPvp()

    val unit = player.unit()
    UnitTypes.locus.create(player.team()).apply {
        set(unit.x, unit.y)
        add()

        apply(StatusEffects.freezing, Float.MAX_VALUE)
        apply(StatusEffects.tarred, Float.MAX_VALUE)
        apply(StatusEffects.electrified, Float.MAX_VALUE)
        apply(StatusEffects.sapped, Float.MAX_VALUE)
    }
    UnitTypes.locus.create(player.team()).apply {
        set(unit.x, unit.y)
        add()

        apply(StatusEffects.freezing, Float.MAX_VALUE)
        apply(StatusEffects.tarred, Float.MAX_VALUE)
        apply(StatusEffects.electrified, Float.MAX_VALUE)
        apply(StatusEffects.sapped, Float.MAX_VALUE)
    }
    UnitTypes.locus.create(player.team()).apply {
        set(unit.x, unit.y)
        add()

        apply(StatusEffects.freezing, Float.MAX_VALUE)
        apply(StatusEffects.tarred, Float.MAX_VALUE)
        apply(StatusEffects.electrified, Float.MAX_VALUE)
        apply(StatusEffects.sapped, Float.MAX_VALUE)
    }
    
    broadcastSkill("坦克")
}

skill("unloader", "技能: 召唤一个塞普罗装卸器，可以在埃里克尔科技线使用", "装卸器") {
    checkOrSetCoolDown(-1)
    checkNotPvp()

    val unit = player.unit()
    val tile = world.tiles.get(        
        unit.tileX(),
        unit.tileY()
    )
    world.tiles.getc(unit.tileX(), unit.tileY()).apply {
        if (block() != Blocks.air){
            returnReply("[red]这里已经有一个方块了".with())
        }
    }
    
    tile?.setNet(Blocks.unloader, player.team(), 0)
    
    broadcastSkill("装卸器???")
}
/*
skill("cargo", "技能: t2随机物品，生成一个仓库，里面有两种随机物品", "空投") {
    checkOrSetCoolDown(1)
    checkNotPvp()

    val unit = player.unit()
    val tile = world.tiles.get(        
        unit.tileX(),
        unit.tileY()
    )
    
    world.tiles.getc(unit.tileX(), unit.tileY()).apply {
        if (block() != Blocks.air){
            returnReply("[red]这里已经有一个方块了".with())
        }
    }
    
    tile?.setNet(Blocks.vault, player.team(), 0)
    
    var randomItem = Vars.content.items().random()
    var randomItem2 = Vars.content.items().random()
    
    tile?.build?.items?.add(randomItem, 1000)
    tile?.build?.items?.add(randomItem2, 1000)
    
    broadcastSkill("空投")
}


skill("itemSource", "技能: 召唤物品源，仅限管理", "物品源") {
    checkOrSetCoolDown(1)
    checkNotPvp()

    val unit = player.unit()
    val tile = world.tiles.get(        
        unit.tileX(),
        unit.tileY()
    )
    
    world.tiles.getc(unit.tileX(), unit.tileY()).apply {
        if (block() != Blocks.air){
            returnReply("[red]这里已经有一个方块了".with())
        }
    } 
    
    tile?.setNet(Blocks.itemSource, player.team(), 0)
    
    broadcastSkill("管理技能-物品源")
}
*/

skill("era", "技能: 生成一个7x7的反应装甲，冷却200秒", "反应装甲") {
    checkOrSetCoolDown(200000)
    checkNotPvp()

    val unit = player.unit()

    for(x in -3..3){
        for(y in -3..3){

            val tile = world.tiles.get(        
                unit.tileX() + x,
                unit.tileY() + y
            )

            world.tiles.getc(unit.tileX() + x, unit.tileY() + y).apply {
                if (block() == Blocks.air){
                    tile?.setNet(Blocks.thoriumWall, player.team(), 0)
                }
            }
        
        }
    }
    
    broadcastSkill("反应装甲")
}


skill("bryde", "技能: 召唤一艘会飞天的戟鲸，冷却900秒", "飞船") {
    checkNotPvp()
    checkOrSetCoolDown(900000)
    val unit = player.unit()
    UnitTypes.bryde.create(player.team()).apply {
        set(unit.x, unit.y)
        add()
        elevation = 1f
    }
    broadcastSkill("飞船")
}

skill("loan", "技能: 借走某资源的一半，3分钟之后两倍返还，冷却900秒", "高利贷") {
    checkOrSetCoolDown(900000)
    checkNotPvp()
    
    val unit = player.unit()
    var randomItem = Vars.content.items().random()
    var items =  unit.core()?.items?.get(randomItem)
    var loan = 0
    
    while(items == 0 || items == null){
        randomItem = Vars.content.items().random()
        items =  unit.core()?.items?.get(randomItem)
    }
    
    loan = min(items / 2, 8000)

    unit.core()?.items?.remove(randomItem, loan)
    
    broadcastSkill("高利贷，放债了"+loan+"个"+randomItem)
    launch(Dispatchers.game) {
        delay(180000L)
        unit.core()?.items?.add(randomItem, loan * 2)
        broadcastSkill("高利贷，得到了"+loan * 2+"个"+randomItem)
   }
}

