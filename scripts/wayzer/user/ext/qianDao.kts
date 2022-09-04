@file:Depends("wayzer/user/userService")

package wayzer.user.ext



import cf.wayzer.placehold.PlaceHoldApi.with
import org.jetbrains.exposed.sql.transactions.transaction
import wayzer.user.UserService
import java.time.Duration
import java.util.*
import kotlin.random.Random
import java.time.Instant
import java.text.DateFormat
import org.jetbrains.exposed.sql.transactions.transaction
//import wayzer.lib.dao.PlayerProfile as DB
//import wayzer.user.Achievement as DB
import coreLibrary.DBApi.DB.registerTable
import org.jetbrains.exposed.sql.transactions.transaction

import java.text.SimpleDateFormat
import mindustry.net.Administration
import arc.struct.ObjectSet

import wayzer.user.*


val userService = contextScript<UserService>()
export(::qd)
export(::info)
export(::mInfo)
export(::searchNames)
export(::AdminsearchNames)

 fun format(instant: Instant) = DateFormat.getDateTimeInstance().format(Date.from(instant))
  fun searchNames(usage: String ): String? {
    
   var  u=""
   
    val result = netServer.admins.searchNames(usage) 
    
    
   
        result.forEach { 
        var str = it.id.toString()
 u=u+ 
"""
"""+it.lastName+" // "+str.substring(0,3)+"*******"//it.id
        }

 
 
 return    u 
}

 fun AdminsearchNames(usage: String ): String? {
    
   var  u=""
   
    val result = netServer.admins.searchNames(usage) 
    
    
   
        result.forEach { 
        var str = it.id.toString()
 u=u+ 
"""
"""+it.lastName+" // "+it.id
        }

 
 
 return    u 
}

 
 fun mInfo(usage: String ): String? {
  
   
        if (usage.isEmpty()) return("[red]请输入玩家uuid")
       
val uuid = netServer.admins.getInfoOptional(usage)?.id
            ?: depends("wayzer/admin")?.import<(String) -> String?>("getUUIDbyShort")?.invoke(usage)
            ?: return "[red]请输入目标3位ID,不清楚可通过/list查询" 
   
 return ""
}


fun qd(qq: Long ): String? {
 
 
	  val profile = qq.let {
    PlayerProfile.findByQQ(it)
		}
val jy =(1..100).random()

if(profile==null)   return "未绑定游戏，请先绑定再签到！"

		userService.finishAchievement(profile, SimpleDateFormat("YYYY-MM-dd").format(Date()), jy, false) 
     return "QQ："+qq.toString()+"，时间："+ SimpleDateFormat("YYYY-MM-dd").format(Date())+"，经验（首签有效）：+"+jy+"，经验:"+profile.totalExp
}
fun info(qq: Long ): String? {
 
 
	  val profile = qq.let {
    PlayerProfile.findByQQ(it)
		}
if(profile==null)   return "未绑定游戏，请先加好友并私聊我“绑定”再签到！"
       
   
     return ""+  profileTemplate.with("profile" to profile)
	 }


val profileTemplate by config.key(
    """
    | 当前绑定账号:{profile.id}
    | 总在线时间:{profile.onlineTime:分钟}
    | 当前等级:{profile.levelIcon}{profile.level}
    | 当前经验(下一级所需经验):{profile.totalExp}({profile.nextLevel})
    | 注册时间:{profile.registerTime:YYYY-MM-dd}
""".trimMargin(), "统一账号信息介绍"
)

 
val template by config.key(
    """
    | [#DEA82A] {player.name} [#DEA82A]个人信息[]
    | [#2B60DE]=======================================[]
    | [green]用户名[]:{player.name}
    | [green]代表3位ID[white]:{player.shortID}
    | [green]最早进服时间[]:{player.ext.firstJoin:YYYY-MM-dd}
    | {profileInfo}
    | [#2B60DE]=======================================[]
""".trimMargin(), "个人信息模板"
)