package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DadosValidade {
  var seq: Int = 0
  var loja: Int = 0
  var abrevLoja: String = ""
  var prdno: String = ""
  var codigo: Int = 0
  var descricao: String = ""
  var grade: String = ""
  var estoqueLoja: Int = 0
  var vencimento: Int = 0
  var inventario: Int = 0
  var dataEntrada: LocalDate? = null
  var validade: Int = 0
  var unidade: String = ""
  var vendno: Int = 0

  var vencimentoStr: String?
    get() = vencimentoToStr(vencimento)
    set(value) {
      vencimento = mesAno(value)
    }

  private fun mesAno(value: String?): Int {
    value ?: return 0
    val mes = value.substring(0, 2).toIntOrNull() ?: return 0
    val ano = value.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  private fun vencimentoToStr(vencimentoPar: Int?): String {
    val venc = vencimentoPar ?: 0
    val vencimentoStr = venc.toString()
    if (vencimentoStr.length != 6) {
      return ""
    } else {
      val mes = vencimentoStr.substring(4, 6)
      val ano = vencimentoStr.substring(2, 4)
      return "$mes/$ano"
    }
  }

  fun update() {
    saci.dadosValidadeUpdate(this)
  }

  fun delete() {
    saci.dadosValidadeDelete(this)
  }

  companion object {
    fun findAll(filtro: FiltroDadosValidade): List<DadosValidade> {
      return saci.dadosValidade(filtro)
    }

    fun insert(loja: Int, codigo: String, grade: String): Int {
      return saci.dadosValidadeInsert(loja, codigo, grade)
    }
  }
}

data class FiltroDadosValidade(
  val pesquisa: String,
  val codigo: String,
  val validade: Int,
  val grade: String,
  val caracter: ECaracter,
  val mes: Int,
  val ano: Int,
  val storeno: Int,
)