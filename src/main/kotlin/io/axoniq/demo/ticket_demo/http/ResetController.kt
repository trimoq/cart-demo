package io.axoniq.demo.ticket_demo.http

import io.axoniq.demo.ticket_demo.stats.ItemRemovalProjector
import org.axonframework.config.Configuration
import org.axonframework.eventhandling.StreamingEventProcessor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("reset")
class ResetController(
    var itemRemovalProjector: ItemRemovalProjector,
    var configuration: Configuration
) {
    @PostMapping("/")
    fun resetEventProcessor(){
        configuration.eventProcessingConfiguration()
                .eventProcessorByProcessingGroup("io.axoniq.demo.ticket_demo.http", StreamingEventProcessor::class.java)
                .ifPresent {
                    if (it.supportsReset()) {
                        it.shutDown()
                        it.resetTokens()
                        it.start()
                    }
                }
    }

}