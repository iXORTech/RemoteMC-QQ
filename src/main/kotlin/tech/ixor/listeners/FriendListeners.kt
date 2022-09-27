package tech.ixor.listeners

import love.forte.di.annotation.Beans
import love.forte.simboot.annotation.*
import love.forte.simboot.filter.MatchType
import love.forte.simbot.event.*

@Beans
class FriendListeners {
    @Listener
    @Filter("Hi", matchType = MatchType.TEXT_CONTAINS)
    suspend fun hiListener(event: FriendMessageEvent) {
        event.friend().send("Hello World!")
    }
}
