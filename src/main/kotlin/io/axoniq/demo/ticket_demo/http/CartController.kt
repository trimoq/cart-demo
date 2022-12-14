package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.api.AddItemCommand
import io.axoniq.demo.ticket_demo.api.CheckoutCommand
import io.axoniq.demo.ticket_demo.api.CreateCartCommand
import io.axoniq.demo.ticket_demo.api.RemoveItemCommand
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("cart")
class CartController(
    val commandGateway: CommandGateway,
    val cartProjector: CartProjector
) {
    @PostMapping("/")
    fun createCart():ResponseEntity<String>{
        val id = commandGateway.sendAndWait<String>(
            CreateCartCommand(randomId())
        )
        return ResponseEntity.ok().body(id)
    }

    @PostMapping("/{cartId}/items")
    fun addItemToCart(
        @PathVariable cartId: String,
        @RequestBody addItemDto: AddItemDTO
    ):ResponseEntity<String>{
        return try {
            commandGateway.sendAndWait<Void>(addItemDto.toAddItemCommand(cartId))
            ResponseEntity.ok().build()
        }catch (e: CommandExecutionException){
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    fun removeItemsFromCart(
        @PathVariable cartId: String,
        @PathVariable itemId: String,
    ):ResponseEntity<String>{
        return try {
            commandGateway.sendAndWait<Void>(RemoveItemCommand(cartId,itemId))
            ResponseEntity.ok().build()
        }catch (e: CommandExecutionException){
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/{cartId}/checkout")
    fun checkoutCart(
        @PathVariable cartId: String
    ):ResponseEntity<String>{
        return try {
            commandGateway.sendAndWait<Void>(CheckoutCommand(cartId))
            ResponseEntity.ok().build()
        }catch (e: CommandExecutionException){
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{cartId}/")
    fun getCart(
        @PathVariable cartId: String,
    ):CartReadModel?{
       return cartProjector.getCart(cartId)
    }

    @GetMapping("/")
    fun getAllCachedCarts(): MutableMap<String, CartReadModel>{
        return cartProjector.getAllCachedCarts()
    }

    private fun randomId() = UUID.randomUUID().toString().substring(0, 8)

}

data class AddItemDTO (
    val itemId: String,
    val amount: Int
){
    fun toAddItemCommand(cartId: String): AddItemCommand {
        return AddItemCommand(cartId,itemId,amount)
    }
}
