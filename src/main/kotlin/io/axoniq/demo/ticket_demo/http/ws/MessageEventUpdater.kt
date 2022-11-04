package io.axoniq.demo.ticket_demo.http.ws

import io.axoniq.demo.ticket_demo.api.CardIssuedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller


@Component
@Controller
class MessageEventUpdater (
    var simpMessagingTemplate:SimpMessagingTemplate
        ){

//    @Scheduled(fixedDelay = 1000)
//    fun sched() {
//        simpMessagingTemplate.convertAndSend("/topic/messages", "aaaaa");
//        simpMessagingTemplate.convertAndSend("/topic/app/messages", "aaaaa");
//        simpMessagingTemplate.convertAndSend("messages", "aaaaa");
//        simpMessagingTemplate.convertAndSend("/app/messages", "aaaaa");
//
////        on(CardIssuedEvent("324", 3))
//    }

        @EventHandler
    fun on(event: CardIssuedEvent){
        println("handling event");
        simpMessagingTemplate.convertAndSend("/topic/messages", event);

//            send("rcv CardIssuedEvent 1")
//        send2("rcv CardIssuedEvent 2")
//        send3("rcv CardIssuedEvent 3")
//        onMsg("fuck this")
    }

//    @SendTo("/topic/messages")
//    fun send(s: String): String {
//        println("Sending to client: $s");
//        return "a"
//    }
//
//    @SendTo("/app/messages")
//    fun send2(s: String): String {
//        println("Sending to client: $s");
//        return "b"
//    }
//
//    @SendTo("/messages")
//    fun send3(s: String): String {
//        println("Sending to client: $s");
//        return "c"
//    }
//
//    @MessageMapping("/hello")
//    @SendTo("/topic/messages")
//    fun onMsg(s: String): String {
//        println("onMsg: $s");
//        return "aaaaa"
//    }
//
//    @SubscribeMapping("{topic}")
//    fun onSubscribe(
//        @DestinationVariable topic: String
//    ) : String{
//        println("new subscription: $topic");
//        return "lol wat?"
//    }
}

class OutputMessage(var content: String)