package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.api.CartCreatedEvent
import io.axoniq.demo.ticket_demo.api.CheckoutEvent
import io.axoniq.demo.ticket_demo.api.ItemAddedEvent
import io.axoniq.demo.ticket_demo.api.ItemRemovedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component


data class CartReadModel (
    val id: String,
    var checkedOut: Boolean,
    var items: MutableMap<String, ItemInCartReadModel>
)

data class ItemInCartReadModel (
    val itemId: String ,
    var amount: Int
)

/**
 * Projector subscribing on events created by the shopping cart.
 * This implementation uses these events to do two things:
 *   - Keep an in-memory representation of all shopping carts to present them via the `/{cartId}/` endpoint
 *   - Push changes via spring messaging (via websockets) to the frontend for live updates
 * Note that storing this data in a database may be a better option for some applications
 */
@Component
class CartProjector (
    var simpMessagingTemplate:SimpMessagingTemplate
        ){

    private var cartDB: MutableMap<String, CartReadModel> = HashMap()

    @EventHandler
    fun on(event: CartCreatedEvent){
        cartDB[event.id]= CartReadModel(event.id, false, mutableMapOf())
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
        println("Create $event")
    }

    @EventHandler
    fun on(event: ItemAddedEvent){
        var items = cartDB[event.id]?.items
        items?.set(
            event.itemId,
            ItemInCartReadModel(event.itemId,(items[event.itemId]?.amount ?: 0)+event.amount)
        )
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
        println("Add $event")
    }

    @EventHandler
    fun on(event: ItemRemovedEvent){
        cartDB[event.id]?.items?.remove(event.itemId)
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
        println("Removed $event")
    }

    @EventHandler
    fun on(event: CheckoutEvent){
        cartDB[event.id]?.checkedOut=true;
        simpMessagingTemplate.convertAndSend("/topic/events", WrappedEvent(event))
        pushCartUpdate(event.id)
        println("Checkout $event")
    }

    private fun pushCartUpdate(id: String) {
        cartDB[id]?.let { simpMessagingTemplate.convertAndSend("/topic/cart", it) }
    }

    fun getCart(id: String): CartReadModel? {
        return cartDB[id]
    }
}





// For showing events in the app

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
