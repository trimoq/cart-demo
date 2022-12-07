package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.api.CartCreatedEvent
import io.axoniq.demo.ticket_demo.api.CheckoutEvent
import io.axoniq.demo.ticket_demo.api.ItemAddedEvent
import io.axoniq.demo.ticket_demo.api.ItemRemovedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component


@Component
class CartProjector (
    var simpMessagingTemplate:SimpMessagingTemplate
        ){

    private var cartDB: MutableMap<String, CartReadModel> = HashMap()

    @EventHandler
    fun on(event: CartCreatedEvent){
        cartDB[event.id]= CartReadModel(event.id, false, mutableMapOf())
        publishUpdate(WrappedEvent(event))
    }

    @EventHandler
    fun on(event: ItemAddedEvent){
        val items = cartDB[event.id]?.items
        items?.set(
            event.itemId,
            ItemInCartReadModel(event.itemId,(items[event.itemId]?.amount ?: 0) + event.amount)
        )
        publishUpdate(WrappedEvent(event))
    }

    @EventHandler
    fun on(event: ItemRemovedEvent){
        cartDB[event.id]?.items?.remove(event.itemId)
        publishUpdate(WrappedEvent(event))
    }

    @EventHandler
    fun on(event: CheckoutEvent){
        cartDB[event.id]?.checkedOut=true
        publishUpdate(WrappedEvent(event))
    }


    private fun publishUpdate(event: WrappedEvent) {
        simpMessagingTemplate.convertAndSend("/topic/events", event)
        println("[${event.cartId}]: ${event.eventType}")
        pushCartUpdate(event.cartId)

    }

    private fun pushCartUpdate(id: String) {
        cartDB[id]?.let { simpMessagingTemplate.convertAndSend("/topic/cart", it) }
    }

    fun getCart(id: String): CartReadModel? {
        return cartDB[id]
    }

    fun getAllCachedCarts(): MutableMap<String, CartReadModel> {
        return cartDB
    }
}

// For the In-Memory datastore

data class CartReadModel (
    val id: String,
    var checkedOut: Boolean,
    var items: MutableMap<String, ItemInCartReadModel>
)

data class ItemInCartReadModel (
    val itemId: String ,
    var amount: Int
)



// For showing events in the frontend

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
