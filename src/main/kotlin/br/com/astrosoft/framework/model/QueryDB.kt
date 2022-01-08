package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.util.SystemUtils.readFile
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder
import org.sql2o.Connection
import org.sql2o.Query
import org.sql2o.Sql2o
import org.sql2o.converters.Converter
import org.sql2o.quirks.NoQuirks
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KClass

typealias QueryHandle = Query.() -> Unit

open class QueryDB(driver: String, url: String, username: String, password: String) {
  protected val sql2o: Sql2o

  init {
    registerDriver(driver)
    val maps = HashMap<Class<*>, Converter<*>>()
    maps[LocalDate::class.java] = LocalDateConverter()
    maps[LocalTime::class.java] = LocalSqlTimeConverter()
    maps[ByteArray::class.java] = ByteArrayConverter()
    this.sql2o = Sql2o(url, username, password, NoQuirks(maps))
  }

  private fun registerDriver(driver: String) {
    try {
      Class.forName(driver)
    } catch (e: ClassNotFoundException) { //throw RuntimeException(e)
    }
  }

  protected fun <T : Any> query(file: String, classes: KClass<T>, lambda: QueryHandle = {}): List<T> {
    val statements = toStratments(file)
    if (statements.isEmpty()) return emptyList()
    val lastIndex = statements.lastIndex
    val query = statements[lastIndex]
    val updates = if (statements.size > 1) statements.subList(0, lastIndex) else emptyList()
    return transaction { con ->
      scriptSQL(con, updates, lambda)
      val ret: List<T> = querySQL(con, query, classes, lambda)
      ret
    }
  }

  protected fun <R : Any> querySerivce(file: String,
                                       complemento: String?,
                                       lambda: QueryHandle = {},
                                       result: (Query) -> R): R {
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

  private fun querySQLResult(con: Connection, sql: String?, lambda: QueryHandle = {}): Query {
    val query = con.createQueryConfig(sql)
    query.lambda()
    return query
  }

  private fun <T : Any> querySQL(con: Connection, sql: String?, classes: KClass<T>, lambda: QueryHandle = {}): List<T> {
    val query = con.createQueryConfig(sql)
    query.lambda()
    println(sql)
    return query.executeAndFetch(classes.java)
  }

  protected fun script(file: String, lambda: QueryHandle = {}) {
    val stratments = toStratments(file)
    transaction { con ->
      scriptSQL(con, stratments, lambda)
    }
  }

  protected fun script(file: String, lambda: List<QueryHandle>) {
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

  private fun scriptSQL(con: Connection, stratments: List<String>, lambda: QueryHandle = {}) {
    stratments.forEach { sql ->
      val query = con.createQueryConfig(sql)
      query.lambda()
      query.executeUpdate()
      println(sql)
    }
  }

  private fun scriptSQL(con: Connection, stratments: List<String>, lambda: List<QueryHandle>) {
    stratments.forEach { sql ->
      val query = con.createQueryConfig(sql)
      lambda.forEach { lamb ->
        query.lamb()
        query.executeUpdate()
        println(sql)
      }
    }
  }

  fun Query.addOptionalParameter(name: String, value: String?): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: LocalDate?): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: ByteArray?): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Int): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: List<String>): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Long): Query {
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

