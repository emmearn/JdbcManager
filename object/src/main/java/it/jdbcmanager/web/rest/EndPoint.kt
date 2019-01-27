package it.jdbcmanager.web.rest

import it.jdbcmanager.services.Service
import it.jdbcmanager.models.GenericObject
import it.jdbcmanager.models.ResultObject
import it.jdbcmanager.utils.ResultManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult

@RestController
@RequestMapping("/genericobject")
class EndPoint {
    @Autowired
    private lateinit var genericObjectService: Service

    // TODO
    /**
        * add enabled parameter
        * add log.debug
     */

    @GetMapping(produces = arrayOf("application/json"))
    fun getGroups(@RequestParam("orderBy", required = false) orderBy: String?, @RequestParam("orderType", required = false) orderType: String?,
                  @RequestParam("enabled", required = false) enabled: Boolean?) : DeferredResult<ResponseEntity<Any>> {
        val deferredResult = DeferredResult<ResponseEntity<Any>>()

        val result : ListenableFuture<List<GenericObject>> = genericObjectService.getGenericObject(enabled = enabled, orderBy = orderBy,  orderType = orderType, queryForObject = false) as ListenableFuture<List<GenericObject>>

        result.addCallback(
                object : ListenableFutureCallback<List<GenericObject>> {
                    override fun onSuccess(result: List<GenericObject>) {
                        deferredResult.setResult(ResponseEntity(ResultObject(result), HttpStatus.OK))
                    }

                    override fun onFailure(t: Throwable) {
                        deferredResult.setResult(ResponseEntity(t, HttpStatus.INTERNAL_SERVER_ERROR))
                    }
                }
        )

        return deferredResult
    }

    @GetMapping("/{id}", produces = arrayOf("application/json"))
    fun getGroup(@PathVariable("id") id: Int?) : DeferredResult<ResponseEntity<Any>> {
        val deferredResult = DeferredResult<ResponseEntity<Any>>()
        val result : ListenableFuture<GenericObject> = genericObjectService.getGenericObject(id = id, queryForObject = true) as ListenableFuture<GenericObject>

        result.addCallback(
                object : ListenableFutureCallback<GenericObject> {
                    override fun onSuccess(result: GenericObject) {
                        deferredResult.setResult(ResponseEntity(ResultObject(result), HttpStatus.OK))
                    }

                    override fun onFailure(t: Throwable) {
                        deferredResult.setResult(ResponseEntity(t, HttpStatus.INTERNAL_SERVER_ERROR))
                    }
                }
        )

        return deferredResult
    }

    @PostMapping(produces = arrayOf("application/json"))
    fun postGroup(@RequestBody group: GenericObject): DeferredResult<ResponseEntity<*>> =
            ResultManager.getDeferredResult(genericObjectService.postGenericObject(group))

    @PutMapping(produces = arrayOf("application/json"))
    fun putGroup(@RequestBody group: GenericObject): DeferredResult<ResponseEntity<*>> =
            ResultManager.getDeferredResult(genericObjectService.putGenericObject(group))

    @PatchMapping(produces = arrayOf("application/json"))
    fun patchGroup(@RequestBody group: GenericObject): DeferredResult<ResponseEntity<*>> =
            ResultManager.getDeferredResult(genericObjectService.patchGenericObject(group))

    @DeleteMapping("/{id}", produces = arrayOf("application/json"))
    fun deleteGroup(@PathVariable id: Int): DeferredResult<ResponseEntity<*>> =
            ResultManager.getDeferredResult(genericObjectService.deleteGenericObject(id))

    @PatchMapping("/{id}/disable", produces = arrayOf("application/json"))
    fun disableGroup(@PathVariable id: Int): DeferredResult<ResponseEntity<*>> =
            ResultManager.getDeferredResult(genericObjectService.disableGenericObject(id))
}