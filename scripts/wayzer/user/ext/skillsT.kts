@file:Depends("wayzer/user/userService")
package wayzer.user.ext

import arc.util.io.Writes
import mindustry.content.Blocks
import mindustry.content.UnitTypes
import mindustry.gen.Building
import wayzer.user.ext.SkillsT.Api.skill
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
    lateinit var script: SkillsT
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
            "[{player.name}] [white]:{skill}"
                .with("player" to player, "skill" to skill), quite = true
        )
    }

    /**
     *
     */
    fun Script.skill(name: String, desc: String, vararg aliases: String, body: SkillScope.() -> Unit) {
        command(name, desc) {
            permission = "wayzer.user.skillsC.$name"
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




skill("imsb", "技能: 发表属于你的《独立宣言》", "独立宣言") {
    checkOrSetCoolDown(1)

    val unit = player.unit()
    
    
    launch(Dispatchers.game) {
    	broadcastSkill("OK兄弟们")
    	delay(3000L)
    	broadcastSkill("全体目光向我看齐")
    	delay(3000L)
    	broadcastSkill("看我看我")
    	delay(3000L)
		 broadcastSkill("我宣布个事")
    	delay(3000L) 
		 broadcastSkill("我是个傻逼！")
   }
}


