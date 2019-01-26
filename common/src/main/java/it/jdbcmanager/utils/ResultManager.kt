package it.jdbcmanager.utils

import it.jdbcmanager.models.ResultObject
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.context.request.async.DeferredResult

object ResultManager {
    fun getDeferredResult(result : ListenableFuture<*>): DeferredResult<ResponseEntity<*>> {
        val deferredResult = DeferredResult<ResponseEntity<*>>()

        result.addCallback(
                object : ListenableFutureCallback<Any> {
                    override fun onSuccess(result: Any) {
                        deferredResult.setResult(ResponseEntity(ResultObject(result), HttpStatus.OK))
                    }

                    override fun onFailure(t: Throwable) {
                        deferredResult.setResult(ResponseEntity<Any>(t, HttpStatus.INTERNAL_SERVER_ERROR))
                    }
                }
        )

        return deferredResult
    }

    fun getAsyncResult(result : Any) = AsyncResult(result)
}