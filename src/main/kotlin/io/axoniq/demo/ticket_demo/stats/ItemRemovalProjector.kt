package io.axoniq.demo.ticket_demo.stats

import io.axoniq.demo.ticket_demo.api.ItemRemovedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class ItemRemovalProjector(
    var simpMessagingTemplate: SimpMessagingTemplate
) {

    private var itemRemovedStats: MutableMap<String, RemovedModel> = HashMap()

    @ResetHandler
    fun onReset(){
        println("ItemRemovalProjector was reset")
        itemRemovedStats.clear()
    }

    @EventHandler
    fun on(event: ItemRemovedEvent){
        itemRemovedStats.computeIfAbsent(event.itemId){ RemovedModel() }.removals += 1
        pushRemovalUpdateToUi()
    }




    private fun pushRemovalUpdateToUi() {
        simpMessagingTemplate.convertAndSend("/topic/stats", itemRemovedStats)
    }

    fun  getItemRemovedStats():MutableMap<String, RemovedModel>{
        return itemRemovedStats
    }

}

data class RemovedModel (
    var removals: Int = 0
)