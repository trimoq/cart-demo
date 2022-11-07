package io.axoniq.demo.ticket_demo.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Instant

// Commands
data class CreateCartCommand(@TargetAggregateIdentifier val id: String)
data class AddItemCommand(@TargetAggregateIdentifier val id: String, val itemId: String, val amount: Int)
data class RemoveItemCommand(@TargetAggregateIdentifier val id: String, val itemId: String)
data class CheckoutCommand(@TargetAggregateIdentifier val id: String)


// Events

data class CartCreatedEvent(@TargetAggregateIdentifier val id: String)
data class ItemAddedEvent(@TargetAggregateIdentifier val id: String, val itemId: String, val amount: Int)
data class ItemRemovedEvent(@TargetAggregateIdentifier val id: String, val itemId: String)
data class CheckoutEvent(@TargetAggregateIdentifier val id: String)