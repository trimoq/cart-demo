package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.stats.ItemRemovalProjector
import io.axoniq.demo.ticket_demo.stats.RemovedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("stats")
class StatsController(
    var itemRemovalProjector: ItemRemovalProjector
) {
    @GetMapping("/")
    fun createCart():MutableMap<String, RemovedModel>{
        return itemRemovalProjector.getItemRemovedStats()
    }

}