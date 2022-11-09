package io.axoniq.demo.ticket_demo.stats

import io.axoniq.demo.ticket_demo.api.ItemRemovedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class ItemRemovalProjector {

    private var itemRemovedStats: MutableMap<String, RemovedModel> = HashMap()

    @EventHandler
    fun on(event: ItemRemovedEvent){
        itemRemovedStats.computeIfAbsent(event.itemId){ RemovedModel() }.removals += 1
    }

    fun  getItemRemovedStats():MutableMap<String, RemovedModel>{
        return itemRemovedStats
    }

}

data class RemovedModel (
    var removals: Int = 0
)