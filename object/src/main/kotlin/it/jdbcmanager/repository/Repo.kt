package it.jdbcmanager.repository

import it.jdbcmanager.models.GenericObject
import it.jdbcmanager.utils.JdbcManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository("genericObjectRepo")
class Repo {
    @Autowired
    @Qualifier("jdbcTemplate")
    lateinit var jdbcTemplate: JdbcTemplate

    fun getGenericObject(conditionsList: List<Triple<String, String, Any?>>, orderBy: String? = null, orderType: String? = null, queryForObject: Boolean) =
            JdbcManager.select<GenericObject>(jdbcTemplate = jdbcTemplate, mapper = GenericObjectRowMapper(), conditionsList = conditionsList, orderBy = orderBy, orderType = orderType, queryForObject = queryForObject)

    fun postGenericObject(genericObject: GenericObject) =
            JdbcManager.insert<GenericObject>(jdbcTemplate, genericObject, true)

    fun putGenericObject(genericObject: GenericObject) =
            JdbcManager.update<GenericObject>(jdbcTemplate, genericObject, true)

    fun patchGenericObject(genericObject: GenericObject): Boolean =
            JdbcManager.update<GenericObject>(jdbcTemplate, genericObject, false)

    fun deleteGenericObject(id: Int): Boolean =
            JdbcManager.delete<GenericObject>(jdbcTemplate, id)

    class GenericObjectRowMapper: org.springframework.jdbc.core.RowMapper<GenericObject> {
        override fun mapRow(rs: ResultSet, i: Int): GenericObject =
                GenericObject(
                    rs.getInt("id"),
                    rs.getString("name")
                )
    }
}
