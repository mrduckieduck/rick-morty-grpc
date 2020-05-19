package dev.mrpato.grpc.example.server.handler

import dev.mrpato.grpc.example.proto.GreetServiceGrpcKt
import dev.mrpato.grpc.example.proto.GreetServiceProto
import dev.mrpato.grpc.example.server.createGreet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class GreetingServiceHandler : GreetServiceGrpcKt.GreetServiceCoroutineImplBase() {

    private val listeners = mutableListOf<ProducerScope<GreetServiceProto.Greeting>>()

    override fun join(requests: Flow<GreetServiceProto.JoinRequest>): Flow<GreetServiceProto.Greeting> = channelFlow {
        listeners.add(this)
        GlobalScope.launch {
            requests.collect { joinRequest ->
                println(">> Attempt to join ${joinRequest.name}[${joinRequest.id}]")
                listeners.forEach {
                    val greet = createGreet {
                        this.value = ">> Recently join: ${joinRequest.name}[${joinRequest.id}]"
                    }
                    it.send(greet)
                }
            }
        }
        awaitClose {}
    }

}
