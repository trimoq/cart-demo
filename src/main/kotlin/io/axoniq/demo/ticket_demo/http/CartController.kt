package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.api.AddItemCommand
import io.axoniq.demo.ticket_demo.api.CheckoutCommand
import io.axoniq.demo.ticket_demo.api.CreateCartCommand
import io.axoniq.demo.ticket_demo.api.RemoveItemCommand
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.*

@RestController
@RequestMapping("cart")
class CartController(
    val commandGateway: CommandGateway
) {
    @PostMapping("/")
    fun createCart():ResponseEntity<String>{
        val id = commandGateway.sendAndWait<String>(CreateCartCommand(UUID.randomUUID().toString().substring(0,8)))
        return ResponseEntity.ok().body(id)
    }

    @PostMapping("/{cartId}/items")
    fun addItemToCart(
        @PathVariable cartId: String,
        @RequestBody addItemDto: AddItemDTO
    ):ResponseEntity<String>{
        try {
            commandGateway.sendAndWait<Void>(addItemDto.toAddItemCommand(cartId))
        }catch (e: CommandExecutionException){
            return ResponseEntity.badRequest().body(e.message)
        }
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    fun removeItemsFromCart(
        @PathVariable cartId: String,
        @PathVariable itemId: String,
    ):ResponseEntity<String>{
        try {
            commandGateway.sendAndWait<Void>(RemoveItemCommand(cartId,itemId))
        }catch (e: CommandExecutionException){
            return ResponseEntity.badRequest().body(e.message)
        }
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{cartId}/checkout")
    fun checkoutCart(
        @PathVariable cartId: String
    ):ResponseEntity<String>{
        try {
            commandGateway.sendAndWait<Void>(CheckoutCommand(cartId))
        }catch (e: CommandExecutionException){
            return ResponseEntity.badRequest().body(e.message)
        }
        return ResponseEntity.ok().build()
    }
}

data class AddItemDTO (
    val itemId: String,
    val amount: Int
){
    fun toAddItemCommand(cartId: String):AddItemCommand{
        return AddItemCommand(cartId,itemId,amount)
    }
}
