package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ValidadeSaci {
  var prdno: String? = null
  var tipoValidade: Int? = null
  var tempoValidade: Int? = null

  var tipoValidadeEnum: TipoValidade
    get() = TipoValidade.entries.firstOrNull { it.tipo == tipoValidade } ?: TipoValidade.DIAS
    set(value) {
      tipoValidade = value.tipo
    }

  fun isErro(): Boolean {
    return msgErro().isNotEmpty()
  }

  fun msgErro(): String {
    return when {
      tipoValidade == null -> "Tipo de validade não informado"
      tempoValidade == null -> "Tempo de validade não informado"
      tipoValidadeEnum == TipoValidade.MESES -> validaMeses()
      tipoValidadeEnum == TipoValidade.ANOS -> validaAnos()
      else -> "Tipo de validade(${tipoValidadeEnum.descricao}) não usado"
    }
  }

  private fun validaAnos(): String {
    return if (tempoValidade != 999) "Tempo de validade deve ser 999" else ""
  }

  private fun validaMeses(): String {
    val meses = saci.listValidade().map { it.validade }
    return if (tempoValidade !in meses) "Tempo de validade não cadastrado" else ""
  }

  fun save() {
    saci.updateValidadeSaci(this)
  }
}

enum class TipoValidade(val tipo: Int, val descricao: String) {
  DIAS(0, "Dias"),
  SEMANAS(1, "Semanas"),
  MESES(2, "Meses"),
  ANOS(3, "Anos");
}