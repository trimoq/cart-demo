package io.axoniq.demo.ticket_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TicketDemoApplication

fun main(args: Array<String>) {
    runApplication<TicketDemoApplication>(*args)
}
