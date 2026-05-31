package com.example.newsapp.data.dataSource.network

import android.util.Log
import com.example.newsapp.BuildConfig
import com.example.newsapp.data.dto.WsMessageDto
import com.example.newsapp.data.dto.WsTradeDto
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// data/remote/FinnhubWebSocket.kt

class FinnhubWebSocket(
    private val client: OkHttpClient,
    private val gson: Gson,
) {
    private var webSocket: WebSocket? = null

    // Returns a Flow that emits a new price every time Finnhub sends a trade
    fun observePrices(symbols: List<String>): Flow<WsTradeDto> = callbackFlow {

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=${BuildConfig.FINNHUB_API_KEY}")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                // subscribe to each symbol once connection is open
                Log.d("WS", "Connected!")
                symbols.forEach { symbol ->
                    val msg = """{"type":"subscribe","symbol":"$symbol"}"""
                    webSocket.send(msg)
                    Log.d("FINNHUB_WS", "Subscribed to $symbol")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WS", "Message received: $text")
                val message = runCatching {
                    gson.fromJson(text, WsMessageDto::class.java)
                }.getOrNull() ?: return

                if (message.type == "trade") {
                    message.data?.forEach { trade ->
                        trySend(trade) // push into the Flow
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("FINNHUB_WS", "Error: ${t.message}")
                Log.e("WS", "Failed: ${t.message}")
                Log.e("WS", "Response: ${response?.code}")
                close(t) // end the Flow with error
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("FINNHUB_WS", "Closed: $reason")
                channel.close()
            }
        })

        // when the Flow collector cancels, unsubscribe and close
        awaitClose {
            symbols.forEach { symbol ->
                webSocket?.send("""{"type":"unsubscribe","symbol":"$symbol"}""")
            }
            webSocket?.close(1000, "Flow cancelled")
            webSocket = null
        }
    }
}