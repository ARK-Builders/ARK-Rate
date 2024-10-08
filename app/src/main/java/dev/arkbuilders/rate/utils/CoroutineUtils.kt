package dev.arkbuilders.rate.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun <T> withContextAndLock(
    context: CoroutineContext,
    mutex: Mutex,
    block: suspend CoroutineScope.() -> T,
): T =
    withContext(context) {
        mutex.withLock {
            block()
        }
    }
