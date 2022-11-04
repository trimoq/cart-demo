package io.axoniq.demo.ticket_demo.command


import io.axoniq.demo.ticket_demo.api.CancelCardCommand
import io.axoniq.demo.ticket_demo.api.CardCanceledEvent
import io.axoniq.demo.ticket_demo.api.CardIssuedEvent
import io.axoniq.demo.ticket_demo.api.CardRedeemedEvent
import io.axoniq.demo.ticket_demo.api.IssueCardCommand
import io.axoniq.demo.ticket_demo.api.RedeemCardCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import org.axonframework.modelling.command.AggregateLifecycle.apply

@Aggregate
class GiftCard {

    @AggregateIdentifier
    private var giftCardId: String? = null
    private var remainingValue = 0

    @CommandHandler
    constructor(command: IssueCardCommand) {
        println("Handling IssueCardCommand: $command")
        require(command.amount > 0) { "amount <= 0" }
        apply(CardIssuedEvent(command.id, command.amount))
    }

    @CommandHandler
    fun handle(command: RedeemCardCommand) {
        require(command.amount > 0) { "amount <= 0" }
        check(command.amount <= remainingValue) { "amount > remaining value" }
        apply(CardRedeemedEvent(giftCardId!!, command.amount))
    }

    @CommandHandler
    fun handle(command: CancelCardCommand?) {
        apply(CardCanceledEvent(giftCardId!!))
    }

    @EventSourcingHandler
    fun on(event: CardIssuedEvent) {
        giftCardId = event.id
        remainingValue = event.amount
    }

    @EventSourcingHandler
    fun on(event: CardRedeemedEvent) {
        remainingValue -= event.amount
    }

    @EventSourcingHandler
    fun on(event: CardCanceledEvent?) {
        remainingValue = 0
    }

    constructor() {
        // Required by Axon to construct an empty instance to initiate Event Sourcing.
    }
}

