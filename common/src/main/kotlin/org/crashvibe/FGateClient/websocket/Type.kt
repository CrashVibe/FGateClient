package org.crashvibe.FGateClient.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable

@Serializable
data class JsonRpcRequest<P>(
  val jsonrpc: String, val method: String, val params: P? = null, val id: String?
)

@Serializable
data class JsonRpcResponse<R, E>(
  val jsonrpc: String, val result: R? = null, val error: JsonRpcError<E>? = null, val id: String?
) {
  @Serializable
  data class JsonRpcError<E>(
    val code: Int, val message: String, val data: E? = null
  )
}

data class PendingRequest<T>(
  val deferred: CompletableDeferred<T>, val requestId: String
) {
  suspend fun await(timeoutMillis: Long): T {
    return withTimeout(timeoutMillis) {
      deferred.await()
    }
  }

  fun resolve(value: T) {
    deferred.complete(value)
  }

  fun reject(reason: Throwable) {
    deferred.completeExceptionally(reason)
  }
}

enum class MethodType(val method: String) {
  KICK_PLAYER("kick.player"),
  GET_CLIENT_INFO("get.client.info"),
  CHAT_BROADCAST("chat.broadcast"),
}
