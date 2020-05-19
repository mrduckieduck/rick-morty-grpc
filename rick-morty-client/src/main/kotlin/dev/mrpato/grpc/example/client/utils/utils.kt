package dev.mrpato.grpc.example.client.utils

import dev.mrpato.grpc.example.proto.GreetServiceProto
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.internal.DnsNameResolverProvider

fun createJoinRequest(init: GreetServiceProto.JoinRequest.Builder.() -> Unit): GreetServiceProto.JoinRequest =
    GreetServiceProto.JoinRequest.newBuilder()
        .apply(init)
        .build()

fun createManagedChannel(target: String = "localhost:50051"): ManagedChannel {
    return ManagedChannelBuilder
        .forTarget(target)
        .nameResolverFactory(DnsNameResolverProvider())
        .usePlaintext()
        .build()
}