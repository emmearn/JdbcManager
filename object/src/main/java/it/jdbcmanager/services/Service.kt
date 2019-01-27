package it.jdbcmanager.services

import it.jdbcmanager.models.GenericObject
import it.jdbcmanager.repository.Repo
import it.jdbcmanager.utils.ResultManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.concurrent.ListenableFuture

@Component("genericObjectService")
@Transactional(transactionManager = "txManager")
class Service {
    @Autowired
    private lateinit var repo : Repo

    fun getGenericObject(id: Int? = null, orderBy: String? = null, orderType: String? = null, queryForObject: Boolean) : ListenableFuture<*> {
        val conditionsList = ArrayList<Triple<String, String, Any?>>()
        id?.let {
            conditionsList.add(Triple("id", "=", id))
            return ResultManager.getAsyncResult(repo.getGenericObject(conditionsList = conditionsList, queryForObject = queryForObject))
        }?:
        return ResultManager.getAsyncResult(repo.getGenericObject(conditionsList, orderBy, orderType, queryForObject))
    }

    fun postGenericObject(genericObject: GenericObject) =
            ResultManager.getAsyncResult(repo.postGenericObject(genericObject))

    fun putGenericObject(genericObject: GenericObject) =
            ResultManager.getAsyncResult(repo.putGenericObject(genericObject))

    fun patchGenericObject(genericObject: GenericObject) =
            ResultManager.getAsyncResult(repo.patchGenericObject(genericObject))

    fun deleteGenericObject(id: Int) =
            ResultManager.getAsyncResult(repo.deleteGenericObject(id))

    fun disableGenericObject(id: Int) =
            ResultManager.getAsyncResult(repo.disableGenericObject(id))
}