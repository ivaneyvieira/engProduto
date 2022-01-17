package br.com.astrosoft.framework.model

class SqlLazy(private val orders: List<SqlOrder> = emptyList(), private val limit: Int = -1, private val offset: Int = -1) {

  private fun orderStt(): String {
    return if (orders.isEmpty()) ""
    else "ORDER BY ${
      orders.joinToString(separator = ", ") {
        "${it.property} ${it.direction.sql}"
      }
    }"
  }

  private fun limitStt() = if(limit == -1) "" else "LIMIT $limit"

  private fun offsetStt() = if(offset == -1) "" else "OFFSET $offset"

  fun toSQL(): String {
    return "${orderStt()} ${limitStt()} ${offsetStt()}"
  }
}

data class SqlOrder(val property: String, val direction: EDirection)

enum class EDirection(val sql: String) {
  ASC("ASC"), DESC("DESC")
}