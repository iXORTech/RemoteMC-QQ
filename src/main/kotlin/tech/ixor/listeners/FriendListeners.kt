package tech.ixor.listeners

// import net.mamoe.mirai.Bot
import love.forte.di.annotation.Beans
import love.forte.simboot.annotation.*
import love.forte.simboot.filter.MatchType
import love.forte.simbot.ID
import love.forte.simbot.event.*

@Beans
class FriendListeners {
    @Listener
    @Filter("Hi", matchType = MatchType.TEXT_EQUALS)
    suspend fun hiListener(event: FriendMessageEvent) {
        event.friend().send("Hello World!")
    }

    @Listener
    @Filter("/sendToGroup", matchType = MatchType.TEXT_STARTS_WITH)
    suspend fun sendToGroup(event: FriendMessageEvent) {
        val msg = event.messageContent.plainText
        println("msg: $msg")
        val params = msg.split(" ")
        if (params.size < 3) {
            event.friend().send("/sendToGroup 命令使用方法：/sendToGroup [群号] [消息]")
            return
        }
        val groupCode = params[1].toLong()
        var message = ""
        for (i in 2 until params.size) {
            message += params[i] + " "
        }
        event.bot.logger.info("Sending message to group $groupCode: $message")
//        val group = Bot.instances[0].getGroup(groupCode)
//        group?.sendMessage(message)
        val group = event.bot.group(groupCode.ID)
        if (group != null) {
            group.send(message)
        } else {
            val errorMsg = "Group $groupCode cannot be found. The bot probably isn't in the group."
            event.bot.logger.info(errorMsg)
            event.friend().send(errorMsg)
        }
    }
}
