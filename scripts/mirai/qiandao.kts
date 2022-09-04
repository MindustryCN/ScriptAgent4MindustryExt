package mirai
import net.mamoe.mirai.message.data.MessageSource.Key.quote

 
import net.mamoe.mirai.Bot
val groupId by config.key(585904503L, "广播的群号,0代表不启用")


globalEventChannel().subscribeGroupMessages {
 
    contains("w1", true).reply {
        if (groupId> 0)
        {
            Bot.instancesSequence.forEach {
                launch {
                    val group = it.getGroup(groupId)
                   subject.sendMessage(" 你好"+   group?.members) 
                }
            }
        }
    }
    
	  contains("嘤嘤嘤(ಥ_ಥ)", true).reply {
        QuoteReply(message) + "嘤嘤嘤(ಥ_ಥ)" + At(sender)
    } 

}

 
 globalEventChannel().subscribeMessages {
 
 startsWith("info-")  {cmd ->
    val searchNames = depends("wayzer/user/ext/qianDao")?.import<(String) -> String>("searchNames")
    if (searchNames == null) {
          subject.sendMessage("查询功能不可用，请联系管理")  
    } else
 
{
 
   subject.sendMessage("查询信息： ${searchNames(cmd) } ")
   }
} 

 startsWith("Ainfo-")  {cmd ->
    val searchNames = depends("wayzer/user/ext/qianDao")?.import<(String) -> String>("AdminsearchNames")
    if (searchNames == null) {
          subject.sendMessage("查询功能不可用，请联系管理")  
    } else
    if (sender.id != 2593890337.toLong()){
          subject.sendMessage("你不是小恶魔本人，快爬！")  
    } else
 
{
 
   subject.sendMessage("查询信息： ${searchNames(cmd) } ")
   }
} 

startsWith("minfo-")  {cmd ->

      val mInfo = depends("wayzer/user/ext/qianDao")?.import<(Long) -> String>("mInfo")
        if (mInfo == null) {
        subject.sendMessage("查询功能不可用，请联系管理")
       
    }
 
   val uuid =   depends("wayzer/admin")?.import<(String) -> String?>("getUUIDbyShort")?.invoke(cmd)
            ?:  "[red]请输入目标3位ID,不清楚可通过/list查询" 
			
			
    subject.sendMessage("游戏信息：  "+cmd+uuid)
}

	
  case("签到" ) {
  
   val qq = sender.id
        val qd = depends("wayzer/user/ext/qianDao")?.import<(Long) -> String>("qd") 
        if (qd == null) {
            subject.sendMessage("签到功能不可用，请联系管理")
            return@case
        }
        

		
		var s="""
		签到： ${qd(qq) }
		""".trimIndent()
     subject.sendMessage(s)
		
       
    }
	
	  case("个人信息" ) {
  
   val qq = sender.id
        val qd = depends("wayzer/user/ext/qianDao")?.import<(Long) -> String>("info")
        if (qd == null) {
            subject.sendMessage("个人信息功能不可用，请联系管理")
            return@case
        }
        
        subject.sendMessage("游戏信息： ${qd(qq) } ")
    }
}