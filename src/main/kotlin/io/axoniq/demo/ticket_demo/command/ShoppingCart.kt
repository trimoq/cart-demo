package io.axoniq.demo.ticket_demo.command

import io.axoniq.demo.ticket_demo.api.AddItemCommand
import io.axoniq.demo.ticket_demo.api.CartCreatedEvent
import io.axoniq.demo.ticket_demo.api.CheckoutCommand
import io.axoniq.demo.ticket_demo.api.CheckoutEvent
import io.axoniq.demo.ticket_demo.api.CreateCartCommand
import io.axoniq.demo.ticket_demo.api.ItemAddedEvent
import io.axoniq.demo.ticket_demo.api.ItemRemovedEvent
import io.axoniq.demo.ticket_demo.api.RemoveItemCommand
import io.axoniq.demo.ticket_demo.command.Constants.MAX_ITEM_SLOTS_PER_CART
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import org.axonframework.modelling.command.AggregateLifecycle.apply

object Constants{
    const val MAX_ITEM_SLOTS_PER_CART = 7;

}

@Aggregate
class ShoppingCart {
    @AggregateIdentifier
    private var cardId: String? = null
    private var items: MutableMap<String,ItemInCart> = HashMap()
    private var modifiable = true

    @CommandHandler
    constructor(command: CreateCartCommand){
        println("Handling CreateCartCommand: $command")
        apply ( CartCreatedEvent(command.id) )
    }

    @CommandHandler
    fun handle(command: AddItemCommand){
        require(command.amount>0) { "Must at least add one item" }
        check(items.size<=MAX_ITEM_SLOTS_PER_CART) { "Cannot add any more items" }
        check(modifiable) { "Must not modify checked-out cart" }
        apply(ItemAddedEvent(command.id,command.itemId,command.amount) )
    }

    @CommandHandler
    fun handle(command: RemoveItemCommand){
        check(items.containsKey(command.itemId)) { "Item to remove must exist" }
        check(modifiable) { "Must not modify checked-out cart" }
        apply(ItemRemovedEvent(command.id,command.itemId))
    }

    @CommandHandler
    fun handle(command: CheckoutCommand){
        check(items.isNotEmpty()) { "Must have items in cart to check out" }
        check(modifiable) { "Must not modify checked-out cart" }
        apply(CheckoutEvent(command.id) )
    }

    @EventSourcingHandler
    fun on(event: CartCreatedEvent){
        cardId = event.id
        items = HashMap()
        println("Create: $event")
    }

    @EventSourcingHandler
    fun on(event: ItemAddedEvent){
        val amount =  items[event.itemId]?.amount ?: 0
        items[event.itemId] = ItemInCart(event.itemId, amount)
        println("Add: $event")
    }

    @EventSourcingHandler
    fun on(event: ItemRemovedEvent){
        items -= event.itemId
        println("Remove: $event")
    }
    @EventSourcingHandler
    fun on(event: CheckoutEvent){
        modifiable = false
        println("Checkout: $event")
    }

    constructor()

}

data class ItemInCart (val itemId: String, val amount: Int)