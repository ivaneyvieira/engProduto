package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.model.exceptions.EModelFail
import br.com.astrosoft.framework.util.SystemUtils.readFile
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder
import org.sql2o.Connection
import org.sql2o.Query
import org.sql2o.Sql2o
import org.sql2o.converters.Converter
import org.sql2o.quirks.NoQuirks
import java.time.LocalDate
import kotlin.reflect.KClass

typealias QueryHandler = Query.() -> Unit

open class QueryDB(database: DatabaseConfig) {
  private val sql2o: Sql2o

  init {
    try {
      Class.forName(database.driver)
      val maps = HashMap<Class<*>, Converter<*>>()
      //maps[LocalDate::class.java] = LocalDateConverter()
      //maps[LocalTime::class.java] = LocalSqlTimeConverter()
      //maps[ByteArray::class.java] = ByteArrayConverter()
      this.sql2o = Sql2o(database.url, database.user, database.password, NoQuirks(maps))
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
      println(sql)
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
        println(sql)
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
          println(sql)
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