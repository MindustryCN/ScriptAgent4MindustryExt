@file:Depends("coreMindustry")
package mirai
import cf.wayzer.placehold.PlaceHoldApi.with
//import org.jetbrains.exposed.sql.transactions.transaction
//import wayzer.user.UserService
import java.time.Duration
import java.util.*
import kotlin.random.Random
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import mindustry.Vars.netServer
import mindustry.gen.Call
import mindustry.gen.Groups
import mindustry.gen.Player


 import cf.wayzer.placehold.PlaceHoldApi
 import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
 import java.awt.image.BufferedImage
 import java.io.File
 import javax.imageio.ImageIO
 //import wayzer.lib.PermissionExt
// PermissionExt

val AdminQQ: Long = 674971336
// version 0.2
globalEventChannel().subscribeMessages {
    case("服务器玩家") {
       
        val generate = depends("wayzer/user/ext/profileBind")?.import<(Long) -> Int>("generate")
        if (generate == null) {
            subject.sendMessage("玩家列表服务暂不可用，请联系管理")
            return@case
        
	}

	var users = ""
    	var players = Groups.player
	var playern = players.size()
	players.forEach{
	//PlayerData.forEach{
    	//val profile = PlayerData("").profile
        //var theqq = profile!!.qq
		var theid = it.id
		var thename = it.name
		users = users + """id${theid}:${thename}
		"""
	}   
	var startPos = users.indexOf("[")
	var endPos = users.indexOf("]")
	while( startPos >= 0 && endPos >=0)
	{
		users = users.replaceRange(startPos,endPos+1,"")?:users
		startPos = users.indexOf("[")
		endPos = users.indexOf("]")
	}
        startPos = users.indexOf("<")
        endPos = users.indexOf(">")
        while( startPos >= 0 && endPos >= 0)
        {
                users = users.replaceRange(startPos,endPos+1,"")?:users
                startPos = users.indexOf("<")
                endPos = users.indexOf(">")
        }


	if(playern==0)
	{
                subject.sendMessage(
                    """现在服务器没有人在玩呢。
                """.trimIndent()
                )
	}
	else
	{
	        subject.sendMessage(
	            """服务器现在一共有${playern}人呢:
		    ${users}
	        """.trimIndent()
	        )
	}
	
    }

    startsWith("-sc") reply { cmd ->
        if (this is GroupMessageEvent) {
            ""
        }
		else
		{
			val qq = sender.id
			if( qq == AdminQQ )
			{
				var purecmd = cmd.substring(3)
				var resp = coreMindustry.lib.RootCommands.handleInput(purecmd, null)
				"命令 $purecmd 已经执行:$resp"
			}
			else
			{
			//	"Testing"
			}
		}
    }
	//    contains("ip") {
    //    
	//    subject.sendMessage(
    //    "IP地址见公告&输入“服务器地址”可快速进入服务器"
    //)};
	//    contains("IP") {
    //    
	//    subject.sendMessage(
    //    "IP地址见公告或输入“服务器地址”可快速进入服务器"
    //)};    
}
globalEventChannel().subscribeMessages {
    case("服务器封禁") {
        "finished"
        val sorted = netServer.admins.banned.sortedByDescending { it.lastKicked }
        val list = sorted.map {
            "{info.name}({info.lastBan:MM/dd})：\n\"{info.uuid}\" \n"
                .with("info" to it)
        }
        var msg = ""
        msg = msg + "服务器当前禁封人数：{state.allBan}\n".with("state" to state)
        msg = msg + "服务器随xem8k5小恶魔心情清空黑名单，请珍惜自己的账号\n\n"
		msg = msg + "破坏封神榜：\n"
        list.forEach {
            msg = msg + it
        }
        subject.sendMessage(msg)
    }
}
 
// globalEventChannel().subscribeGroupMessages {
//     content { message.filterIsInstance<PlainText>().any { it.content.contains("绑定") } }
//         .reply { "绑定请私聊我“绑定”\n更多指令：\n/S：服务器状态\n/P:玩家列表\n/B：封神榜\n管理员申请方式见群公告\n\n-----steam服务器群" }
// }