package dev.mrpato.grpc.example.server

import dev.mrpato.grpc.example.proto.GreetServiceProto.Greeting
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.internal.DnsNameResolverProvider

fun createGreet(init: Greeting.Builder.() -> Unit): Greeting =
    Greeting.newBuilder()
        .apply(init)
        .build()

fun createManagedChannel(target: String): ManagedChannel {
    return ManagedChannelBuilder
        .forTarget(target)
        .nameResolverFactory(DnsNameResolverProvider())
        .usePlaintext()
        .build()
}

fun createManagedChannel(): ManagedChannel {
    return ManagedChannelBuilder
        .forTarget("localhost")
        .usePlaintext()
        .build()
}