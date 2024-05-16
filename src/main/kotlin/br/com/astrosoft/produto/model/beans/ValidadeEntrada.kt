package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.estoque
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ValidadeEntrada(
  val loja: Int?,
  val codigo: Int?,
  val descricao: String?,
  val grade: String?,
  val validade: Int?,
  val nfEntrada: String?,
  val dataEntrada: LocalDate?,
  val dataFabricacao: LocalDate?,
  val mesesFabricacao: Int?,
  val vencimento: LocalDate?,
  val mesesVencimento: Int?,
  val entrada: Int?,
  val saldo: Int?,
  val estoque: Int?,
  val status: String?,
  val mfno: Int?,
  val typeno: Int?,
  val clno: Int?,
  val deptno: Int?,
  val groupno: Int?,
  val taxno: Int?,
  val saldoDS: Int?,
  val saldoMR: Int?,
  val saldoMF: Int?,
  val saldoPK: Int?,
  val saldoTM: Int?,
  var totalVenda: Int? = null,
  val localizacao: String?,
) {
  companion object {
    fun findAll(filtro: FiltroValidadeEntrada): List<ValidadeEntrada> {
      val prdNota = PrdCodigo.findPrdNfe(filtro.nfe)
      val nfe = prdNota.firstOrNull()?.nfe ?: ""
      val listVenda = saci.saldoData(filtro.diVenda, filtro.dfVenda)
      val listaValidade = estoque.consultaValidadeEntrada(filtro.copy(nfe = nfe))
      return listaValidade.asSequence().filter { venda ->
        when {
          filtro.nfe.isBlank() -> true
          prdNota.isEmpty() -> false
          else -> prdNota.any { prd ->
            prd.prdno.trim().toIntOrNull() == venda.codigo && prd.grade == venda.grade
          }
        }
      }.map {
        it.totalVenda = listVenda.filter { venda ->
          venda.codigo == it.codigo && venda.grade == it.grade
        }.sumOf { venda ->
          venda.quant
        }
        it
      }.toList()
    }
  }
}
