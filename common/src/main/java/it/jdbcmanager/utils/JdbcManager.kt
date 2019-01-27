package it.jdbcmanager.utils

import com.google.common.base.CaseFormat
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

object JdbcManager {
    inline fun <reified T> select(jdbcTemplate: JdbcTemplate, mapper: RowMapper<*>, conditionsList: List<Triple<String, String, Any?>>, joinsList: List<Triple<KClass<*>, String, String>>? = null,
                                  orderBy: String? = null, orderType: String? = null, queryForObject: Boolean) : Any {
        val tableName = formatTableName(T::class.simpleName)
        val orderTypes = arrayOf("ASC", "DESC")
        val query = StringBuilder()
        val fields = T::class.declaredMembers
        val fieldsNames = fields.map { field -> formatFieldName(field.name) }

        if(joinsList != null && joinsList.isNotEmpty()) {
            val selections = StringBuilder()
            selections.append("SELECT T.*, ")

            for ((index, value) in joinsList.withIndex()) {
                val currentTableName = formatTableName(value.first.simpleName)
                val currentFields = value.first.memberProperties
                val currentFieldsNames = currentFields.map { currentField -> formatFieldName(currentField.name) }

                currentFieldsNames.forEach { currentFieldName -> selections.append("T$index.$currentFieldName AS ${currentTableName}_$currentFieldName, ") }
            }

            query.append(selections.substring(0, selections.lastIndexOf(",")))
            query.append(" FROM $tableName T")

            for ((index, value) in joinsList.withIndex()) {
                val currentTableName = formatTableName(value.first.simpleName)
                query.append(" INNER JOIN ")
                query.append("$currentTableName T$index").append(" ON ").append("T.${value.second} = T$index.${value.third}")
            }
        } else
            query.append("SELECT * FROM $tableName T")



        if(conditionsList.isNotEmpty()) {
            val conditions = StringBuilder()

            query.append(" WHERE ")

            conditionsList.forEach { condition ->
                if (condition.third is String)
                    conditions.append("T." + formatFieldName(condition.first)).append(" ${condition.second} ").append("\'${condition.third}\'").append(" AND ")
                else
                    conditions.append("T." + formatFieldName(condition.first)).append(" ${condition.second} ").append(condition.third).append(" AND ")
            }

            query.append(conditions.substring(0, conditions.lastIndexOf("AND")))
        }


        orderBy?.let {
            if(orderBy in fieldsNames) {
                orderType?.let {
                    if(orderType.toUpperCase() in orderTypes)
                        query.append(" ORDER BY T.$orderBy $orderType")
                    else JdbcManager.throwRequiredArgument(orderType) as T
                } ?: query.append(" ORDER BY T.$orderBy")
            } else JdbcManager.throwIllegalArgument(orderBy) as T
        }

        return if(queryForObject)
            jdbcTemplate.queryForObject("$query;", mapper)
        else
            jdbcTemplate.query("$query;", mapper)
    }

    inline fun <reified T: Any> insert(jdbcTemplate: JdbcTemplate, obj: T, fullInsert: Boolean, returnId: Boolean = false): Any {
        val tableName = formatTableName(T::class.simpleName)
        val fields = StringBuilder()
        val values = StringBuilder()

        obj::class.memberProperties.forEach {
            prop ->
            when (prop.name) {
                "id", "enabled" -> prop.getter.call(obj)?.let { JdbcManager.throwTooManyArgument(prop.name) }
                else -> {
                    prop.getter.call(obj)?.let {
                        fields.append(JdbcManager.formatFieldName(prop.name)).append(",")
                        if(prop.returnType.jvmErasure.isSubclassOf(String::class) || prop.returnType.jvmErasure.isSubclassOf(LocalDateTime::class))
                            values.append("\'"+ prop.getter.call(obj) + "\'").append(',')
                        else
                            values.append(prop.getter.call(obj)).append(',')
                    }?: if (fullInsert) throwRequiredArgument(prop.name)
                }
            }
        }
        val formattedFields = fields.substring(0, fields.lastIndexOf(","))
        val formattedValues = values.substring(0, values.lastIndexOf(","))

        return if(returnId)
            jdbcTemplate.queryForObject("INSERT INTO $tableName ($formattedFields) VALUES ($formattedValues) RETURNING id;", idRowMapper())
        else
            jdbcTemplate.update("INSERT INTO $tableName ($formattedFields) VALUES ($formattedValues);") == 1
    }

    inline fun <reified T: Any> update(jdbcTemplate: JdbcTemplate, obj: T, fullUpdate: Boolean) : Boolean {
        val tableName = formatTableName(T::class.simpleName)
        val fields = StringBuilder()
        var id : Int? = null

        obj::class.memberProperties.forEach {
            prop ->
            when(prop.name) {
                "id" -> {
                    check(prop.getter.call(obj) is Int)
                    id = prop.getter.call(obj) as Int
                }
                "enabled" -> prop.getter.call(obj)?.let { JdbcManager.throwTooManyArgument(prop.name) }
                else -> {
                    prop.getter.call(obj)?.let {
                        fields.append(JdbcManager.formatFieldName(prop.name)).append(" = ")

                        if(prop.returnType.jvmErasure.isSubclassOf(String::class) || prop.returnType.jvmErasure.isSubclassOf(LocalDateTime::class))
                            fields.append("\'"+ prop.getter.call(obj) + "\'").append(',')
                        else
                            fields.append(prop.getter.call(obj)).append(',')
                    }?: if(fullUpdate) throwRequiredArgument(prop.name)
                }
            }
        }

        return if(fields.isNotEmpty()) {
            val formattedFields = fields.substring(0, fields.lastIndexOf(","))
            jdbcTemplate.update("UPDATE $tableName SET $formattedFields WHERE id = $id;") == 1
        } else false
    }

    inline fun <reified T: Any> disable(jdbcTemplate: JdbcTemplate, id: Int) : Boolean {
        val tableName = formatTableName(T::class.simpleName)
        return jdbcTemplate.update("UPDATE $tableName SET enabled = false WHERE id = $id;") == 1
    }

    inline fun <reified T: Any> delete(jdbcTemplate: JdbcTemplate, id: Int) : Boolean {
        val tableName = formatTableName(T::class.simpleName)
        return jdbcTemplate.update("DELETE FROM $tableName WHERE id = $id;") == 1
    }

    class idRowMapper: RowMapper<Int> {
        override fun mapRow(rs: ResultSet, i: Int): Int = rs.getInt("id")
    }

    fun formatTableName(tableName: String?) = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName)

    fun formatFieldName(fieldName: String?) = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName)

    fun throwTooManyArgument(fieldName: String) : Unit =
            throw IllegalArgumentException("Parameter $fieldName not required.")

    fun throwRequiredArgument(fieldName: String) : Unit =
            throw IllegalArgumentException("Parameter $fieldName required.")

    fun throwIllegalArgument(fieldName: String): Unit =
            throw IllegalArgumentException("Illegal parameter $fieldName")
}