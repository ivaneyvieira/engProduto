package br.com.astrosoft.framework.model.planilha

fun interface ProduceValue<B, T : Any?> {
  fun value(bean: B): T
}

class Column<B, T : Any?>(val header: String, val pattern: String?, val property: ProduceValue<B, T>)