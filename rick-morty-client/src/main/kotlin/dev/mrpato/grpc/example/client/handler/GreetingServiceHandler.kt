package dev.mrpato.grpc.example.client.handler

import dev.mrpato.grpc.example.client.utils.createManagedChannel
import dev.mrpato.grpc.example.proto.GreetServiceGrpcKt
import dev.mrpato.grpc.example.proto.GreetServiceProto
import kotlinx.coroutines.flow.Flow

class GreetingServiceHandler {

    private val channel = createManagedChannel()
    private val greetingStub = GreetServiceGrpcKt.GreetServiceCoroutineStub(channel)

    data class JoinResult(val joinStream: Flow<GreetServiceProto.Greeting>)

    fun join(joinRequestStream: Flow<GreetServiceProto.JoinRequest>): JoinResult {
        return JoinResult(
            joinStream = greetingStub.join(joinRequestStream)
        )
    }

}