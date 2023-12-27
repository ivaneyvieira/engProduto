package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class EntradaDevCli(
  val invno: Int,
  var loja: Int,
  var nomeLoja: String?,
  var notaFiscal: String?,
  var data: LocalDate?,
  var hora: String?,
  var vendno: Int?,
  var fornecedor: String?,
  var custnoDev: Int?,
  var clienteDev: String?,
  var remarks: String?,
  var valor: Double?,
  var storeno: Int?,
  var pdvno: Int?,
  var xano: Int?,
  var custno: Int?,
  var nfVenda: String?,
  var nfData: LocalDate?,
  var nfValor: Double?,
  var cliente: String?,
  var cfo: Int?,
  var empno: Int?,
  var vendedor: String?,
  var impressora: String?,
  var userName: String?,
  var userLogin: String?,
  var pdvVenda: Int?,
  var nfVendaVenda: String?,
  var dataVenda: LocalDate?,
  var clienteVenda: Int?,
  var clienteNome: String?,
  var nfValorVenda: Double?,
  var fezTroca: String?,
) {
  val observacao01: String
    get() {
      val parte1 = remarks?.split(")")?.getOrNull(0) ?: return ""
      return "$parte1)"
    }

  val observacao02: String
    get() {
      val parte2 = remarks?.split(")")?.getOrNull(1) ?: return ""
      return parte2.trim()
    }

  fun produtos() = saci.entradaDevCliPro(invno)
  fun marcaImpresso(impressora: Impressora) {
    saci.marcaImpresso(invno, impressora)
    val lojaNaoInformado = saci.findLojaNaoInformada(custno ?: 0)
    when {
      isReembolso()    -> {
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno ?: 0,
          custnoMuda = lojaNaoInformado?.codigo ?: 0,
          saldo = valor ?: 0.00
        )
        saci.marcaReembolso(saldoDevolucao)
      }

      isMuda()         -> {
        val mudaCliente = mudaCodigo()
        val custno = custno ?: 0
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno,
          custnoMuda = mudaCliente,
          saldo = valor ?: 0.00
        )
        saci.marcaMudaCliente(saldoDevolucao)
      }

      isNaoInformado() -> {
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = lojaNaoInformado?.codigo ?: 0,
          custnoMuda = custno ?: 0,
          saldo = valor ?: 0.00
        )
        saci.marcaMudaCliente(saldoDevolucao)
      }
    }
  }

  private fun isNaoInformado(): Boolean {
    return custno == 200 || custno == 300 || custno == 400 || custno == 500 || custno == 800
  }

  private val MUDA_CLIENTE = "MUDA[^0-9]*([0-9]+)".toRegex()

  private fun isReembolso(): Boolean {
    return remarks?.contains("EST CARTAO", ignoreCase = true) == true ||
           remarks?.contains("EST BOLETO", ignoreCase = true) == true ||
           remarks?.contains("REEMBOLSO", ignoreCase = true) == true ||
           remarks?.contains("EST DEP", ignoreCase = true) == true
  }

  private fun isMuda(): Boolean {
    return remarks?.contains(MUDA_CLIENTE) == true
  }

  private fun mudaCodigo(): Int {
    val matchResult = MUDA_CLIENTE.find(remarks ?: "")
    return matchResult?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
  }

  fun mudaCliente(): String {
    val codigo = mudaCodigo()
    val cliente = saci.mudaCliente(codigo) ?: return ""
    return "${cliente.codigo} - ${cliente.nome}"
  }

  companion object {
    fun findAll(filtro: FiltroEntradaDevCli) = saci.entradaDevCli(filtro)
  }
}

data class FiltroEntradaDevCli(
  val loja: Int,
  val query: String,
  val dataI: LocalDate?,
  val dataF: LocalDate?,
  val dataLimiteInicial: LocalDate?,
  val impresso: Boolean?,
)