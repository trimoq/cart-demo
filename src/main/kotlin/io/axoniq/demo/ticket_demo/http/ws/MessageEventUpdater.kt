package io.axoniq.demo.ticket_demo.http.ws

import io.axoniq.demo.ticket_demo.api.CardIssuedEvent
import io.axoniq.demo.ticket_demo.api.CardRedeemedEvent
import io.axoniq.demo.ticket_demo.api.CardSummary
import io.axoniq.demo.ticket_demo.api.CartCreatedEvent
import io.axoniq.demo.ticket_demo.api.CheckoutEvent
import io.axoniq.demo.ticket_demo.api.ItemAddedEvent
import io.axoniq.demo.ticket_demo.api.ItemRemovedEvent
import io.axoniq.demo.ticket_demo.api.RemoveItemCommand
import org.axonframework.eventhandling.EventHandler
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.collections.HashMap


@Component
@Controller
class MessageEventUpdater (
    var simpMessagingTemplate:SimpMessagingTemplate
        ){

    private var cartDB: MutableMap<String, CartReadModel> = HashMap()

    @EventHandler
    fun on(event: CartCreatedEvent){
        cartDB[event.id]=CartReadModel(event.id, false, mutableMapOf())
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
    }
    @EventHandler
    fun on(event: ItemAddedEvent){
        println("pre:"+cartDB[event.id]?.items)

        if(cartDB[event.id]?.items?.containsKey(event.itemId) == true){
            val amount = (cartDB[event.id]?.items?.get(event.itemId)?.amount ?: 0) + event.amount
            cartDB[event.id]?.items?.set(event.itemId, ItemInCartReadModel(event.itemId,amount))
            println("Post1:"+cartDB[event.id]?.items)
        }else{
            cartDB[event.id]?.items?.set(event.itemId, ItemInCartReadModel(event.itemId,event.amount))
            println("Post2:"+cartDB[event.id]?.items)

        }
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
    }
    @EventHandler
    fun on(event: ItemRemovedEvent){
        cartDB[event.id]?.items?.remove(event.itemId)
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
    }
    @EventHandler
    fun on(event: CheckoutEvent){
        cartDB[event.id]?.checkedOut=true;
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
    }
    private fun pushCartUpdate(id: String) {
        cartDB[id]?.let { simpMessagingTemplate.convertAndSend("/topic/cart", it) }
    }
}

data class CartReadModel (
    val id: String,
    var checkedOut: Boolean,
    var items: MutableMap<String,ItemInCartReadModel>
)

data class ItemInCartReadModel (
    val itemId: String ,
    var amount: Int
)

class WrappedEvent(
    var eventType: EventTypeDTO,
    var cartId: String
) {
    constructor(event: CartCreatedEvent) : this(EventTypeDTO.CartCreatedEvent, event.id)
    constructor(event: ItemAddedEvent) : this(EventTypeDTO.ItemAddedEvent, event.id)
    constructor(event: ItemRemovedEvent) : this(EventTypeDTO.ItemRemovedEvent, event.id)
    constructor(event: CheckoutEvent) : this(EventTypeDTO.CheckoutEvent, event.id)
}

enum class EventTypeDTO {
    CartCreatedEvent,
    ItemAddedEvent,
    ItemRemovedEvent,
    CheckoutEvent
}
