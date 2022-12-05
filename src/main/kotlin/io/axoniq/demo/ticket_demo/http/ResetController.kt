package io.axoniq.demo.ticket_demo.http

import org.axonframework.config.Configuration
import org.axonframework.eventhandling.StreamingEventProcessor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * Allow to reset the two event processors we have
 */
@RestController
@RequestMapping("reset")
class ResetController(
    var configuration: Configuration
) {

    @PostMapping("/projections")
    fun resetEventProcessorProjector(){
        resetEventProcessor("http")
    }

    @PostMapping("/stats")
    fun resetEventProcessorStats(){
        resetEventProcessor("stats")
    }

    private fun resetEventProcessor(processorName: String) {
        configuration.eventProcessingConfiguration()
            .eventProcessorByProcessingGroup(
                "io.axoniq.demo.ticket_demo.$processorName",
                StreamingEventProcessor::class.java
            )
            .ifPresent {
                if (it.supportsReset()) {
                    it.shutDown()
                    it.resetTokens()
                    it.start()
                }
            }
    }
}