package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.model.exceptions.EModelFail
import br.com.astrosoft.framework.util.SystemUtils.readFile
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.withContext
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder
import org.sql2o.Connection
import org.sql2o.Query
import org.sql2o.Sql2o
import java.time.LocalDate
import kotlin.reflect.KClass

typealias QueryHandler = Query.() -> Unit

open class QueryDB(database: DatabaseConfig) {
  private val sql2o: Sql2o

  init {
    try {
      Class.forName(database.driver)
      val config = HikariConfig()
      config.jdbcUrl = database.url
      config.username = database.user
      config.password = database.password
      config.addDataSourceProperty("cachePrepStmts", "true")
      config.addDataSourceProperty("prepStmtCacheSize", "250")
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
      config.isAutoCommit = false
      val ds = HikariDataSource(config)
      ds.maximumPoolSize = 2
      this.sql2o = Sql2o(database.url, database.user, database.password)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  private fun registerDriver(driver: String) {
    try {
      Class.forName(driver)
    } catch (e: ClassNotFoundException) { //throw RuntimeException(e)
    }
  }

  private suspend fun scriptSQLFlow(con: Connection, stratments: List<String>, lambda: QueryHandler = {}) =
      withContext(Dispatchers.IO) {
        try {
          stratments.forEach { sql ->
            val query = con.createQueryConfig(sql)
            query.lambda()
            query.executeUpdate()
          }
        } catch (e: Exception) {
          failDB(e.message)
        }
      }

  protected fun <T : Any> queryFlow(
    file: String,
    classes: KClass<T>,
    lambda: QueryHandler = {}
  ) = flow {
    val statements = toStratments(file)
    if (statements.isEmpty()) return@flow
    val lastIndex = statements.lastIndex
    val querySql = statements[lastIndex]
    val updates = if (statements.size > 1) statements.subList(0, lastIndex) else emptyList()
    try {
      val con = sql2o.beginTransaction()
      scriptSQLFlow(con, updates, lambda)

      val iterator = querySequence(con, querySql, lambda, classes)

      iterator.collect { row ->
        emit(row)
      }
      con.commit()
      con.close()
    } catch (e: Exception) {
      failDB(e.message)
    }
  }.flowOn(Dispatchers.IO)

  private fun <T : Any> querySequence(
    con: Connection,
    querySql: String,
    lambda: QueryHandler,
    classes: KClass<T>
  ) = flow<T> {
    val query = con.createQueryConfig(querySql).also { q ->
      q.lambda()
    }

    val iterator = query.executeAndFetchLazy(classes.java)
    iterator.forEach { row ->
      emit(row)
    }
  }

  protected fun <T : Any> query(
    file: String,
    classes: KClass<T>,
    sqlLazy: SqlLazy = SqlLazy(),
    lambda: QueryHandler = {}
  ): List<T> {
    val statements = toStratments(file)
    if (statements.isEmpty()) return emptyList()
    val lastIndex = statements.lastIndex
    val query = statements[lastIndex] + " " + sqlLazy.toSQL()
    val updates = if (statements.size > 1) statements.subList(0, lastIndex) else emptyList()
    return transaction { con ->
      scriptSQL(con, updates, lambda)
      val ret: List<T> = querySQL(con, query, classes, lambda)
      ret
    }
  }

  protected fun <R : Any> querySerivce(
    file: String,
    complemento: String?,
    lambda: QueryHandler = {},
    result: (Query) -> R
  ): R {
    val statements = toStratments(file, complemento)
    val lastIndex = statements.lastIndex
    val query = statements[lastIndex]
    val updates = if (statements.size > 1) statements.subList(0, lastIndex) else emptyList()
    return transaction { con ->
      scriptSQL(con, updates, lambda)
      val q = querySQLResult(con, query, lambda)
      result(q)
    }
  }

  private fun Connection.createQueryConfig(sql: String?): Query {
    val query = createQuery(sql)
    query.isAutoDeriveColumnNames = true
    query.resultSetHandlerFactoryBuilder = SfmResultSetHandlerFactoryBuilder()
    return query
  }

  private fun querySQLResult(con: Connection, sql: String?, lambda: QueryHandler = {}): Query {
    val query = con.createQueryConfig(sql)
    query.lambda()
    return query
  }

  private fun <T : Any> querySQL(
    con: Connection,
    sql: String?,
    classes: KClass<T>,
    lambda: QueryHandler = {}
  ): List<T> {
    try {
      val query = con.createQueryConfig(sql)
      query.lambda()
      //println(sql)
      return query.executeAndFetch(classes.java)
    } catch (e: Exception) {
      failDB(e.message)
    }
  }

  protected fun script(file: String, lambda: QueryHandler = {}) {
    val stratments = toStratments(file)
    transaction { con ->
      scriptSQL(con, stratments, lambda)
    }
  }

  protected fun script(file: String, lambda: List<QueryHandler>) {
    val stratments = toStratments(file)
    transaction { con ->
      scriptSQL(con, stratments, lambda)
    }
  }

  fun toStratments(file: String, complemento: String? = null): List<String> {
    val sql = if (file.startsWith("/")) readFile(file)
    else file
    val sqlComplemento = if (complemento == null) sql else "$sql\n$complemento"
    return sqlComplemento.split(";").filter { it.isNotBlank() || it.isNotEmpty() }
  }

  private fun scriptSQL(con: Connection, stratments: List<String>, lambda: QueryHandler = {}) {
    try {
      stratments.forEach { sql ->
        val query = con.createQueryConfig(sql)
        query.lambda()
        query.executeUpdate()
        query.paramNameToIdxMap
      }
    } catch (e: Exception) {
      failDB(e.message)
    }
  }

  private fun scriptSQL(con: Connection, stratments: List<String>, lambda: List<QueryHandler>) {
    try {
      stratments.forEach { sql ->
        val query = con.createQueryConfig(sql)
        lambda.forEach { lamb ->
          query.lamb()
          query.executeUpdate()
          //println(sql)
        }
      }
    } catch (e: Exception) {
      failDB(e.message)
    }
  }

  fun Query.addOptionalParameter(name: String, value: Any?): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: String): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: LocalDate): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: ByteArray): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  //fun Query.addOptionalParameter(name: String, value: Int?): Query {
  //  if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
  //  return this
  // }

  @JvmName("addOptionalParameterString")
  fun Query.addOptionalParameter(name: String, value: List<String>): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  @JvmName("addOptionalParameterInt")
  fun Query.addOptionalParameter(name: String, value: List<Int>): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Long): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Boolean): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Double): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  protected fun <T> transaction(block: (Connection) -> T): T {
    return sql2o.beginTransaction().use { con ->
      val ret = block(con)
      con.commit()
      ret
    }
  }
}

data class ScripyUpdate(val query: Query, val queryText: String)
data class DatabaseConfig(val url: String, val user: String, val password: String, val driver: String)

fun failDB(message: String?): Nothing {
  throw EModelFail(message)
}