package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.api.IssueCardCommand
import io.axoniq.demo.ticket_demo.api.RedeemCardCommand
import io.axoniq.demo.ticket_demo.command.GiftCard
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.gateway.AbstractCommandGateway
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api")
class TicketController(
    val commandGateway: CommandGateway
) {

//    @GetMapping("/")
//    fun getTicket(){
//
//    }
    @PostMapping("/")
    fun createTicket():String{
        var card =  commandGateway.sendAndWait<String>(IssueCardCommand(UUID.randomUUID().toString(),7))
        var newCard =  commandGateway.sendAndWait<GiftCard>(RedeemCardCommand(card,1))
        println("card: $card")
        println("card: $newCard")
        return "a"

    }

}