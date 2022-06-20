package com.matejdro.runpeewear.wear.util

import android.net.Uri
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Get flow that will receive flow of specific data item in the Wear network.
 */
fun DataClient.getDataItemFlow(uri: Uri): Flow<DataItem?> {
    return callbackFlow {
        val initialItems = getDataItems(uri).await()

        initialItems.use {
            val firstMatch = it.firstOrNull()
            trySend(firstMatch?.freeze())
        }

        val listener = DataClient.OnDataChangedListener { dataEventBuffer ->
            dataEventBuffer.use {
                for (event in it) {
                    when (event.type) {
                        DataEvent.TYPE_CHANGED -> {
                            trySend(event.dataItem.freeze())
                        }
                        DataEvent.TYPE_DELETED -> {
                            trySend(null)
                        }
                    }
                }
            }
        }

        addListener(listener, uri, DataClient.FILTER_LITERAL)

        this.awaitClose {
            removeListener(listener)
        }
    }.buffer(Channel.CONFLATED)
}
