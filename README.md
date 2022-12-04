# Merch Shop Demo

This repository contains a demo application using the [Axon Framework](https://github.com/AxonFramework/AxonFramework) to provide a backend application for a merch shop.
The focus of this demo is to be a minimal and easy-to-understand application, explainable in 5 minutes at a booth you would encounter at an expo.

To see more features in action, see the [giftcard-demo](https://github.com/AxonIQ/giftcard-demo) or the [hotel-demo](https://github.com/AxonIQ/hotel-demo).

## Features
Running this application, you can place items in a shopping cart, remove them and proceed to checkout. The UI shows a list of all events encountered by the backend to easily explain the benefits of Event sourcing.

This is the basic app that marks the starting point of the "journey" of our imaginarey developer.

On a second branch, this journey is taken a few steps further. The scenario is that your manager requests some new information that was not yet known upon the inception of this application. In the spirit of CQRS, we build an in-memory read model to calculate statistics on how often certain items have been removed from the cart.

As a third addition, a persistent token store is added and an endpoint to reset this store is provided. That allows us to replay all the relevant events to fix an immaginary bug we initially added in the projection.

Note that this demo application uses the easiest possible route to showcase functionality and does therefore not represent the optimal way to solve the issue at hand. For example, we do not even use queries in the initial version.

## The event store
All event sourcing applicaitons need some way to store events, the so-called event store. This application uses the [Axon Server](https://developer.axoniq.io/axon-server/overview) to store events. This allows us to make use of many other convenient features valueable in this early stage of the application, like manually pausing certain event processors or searching the event store based on an aggregateId.

## Running the demo

To run the demo, you need to have axon-server running. For this, simply start it via the provided `docker-compose.yml` file, running in this directory:

```
docker-compose up
```

If you do not have docker installed, you can download axon-server directly from the [website](https://developer.axoniq.io/download).

When axon-server is running, you can start the backend either by importing it in your IDE and starting it from there (you want to look at the code anyways, don't you?) or using gradle.

If you choose to go the gradle route, run 
```
gradle bootRun
```
using a reasonably recent gradle version. I used `Gradle 7.5.1` and `OpenJDK 17` for the demo.


## Running the frontend
The demo has a [companion-repo](https://github.com/trimoq/cart-demo-ui) containing a frontend application to ease interaction with the backend.

Follow the instructions found in that repo on how to run it.