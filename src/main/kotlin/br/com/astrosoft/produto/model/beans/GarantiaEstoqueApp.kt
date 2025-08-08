package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.estoque
import java.time.LocalDate

class GarantiaEstoqueApp(
  val loja: Int,
  val codigo: Int,
  val descricao: String,
  val vendno: Int?,
  val grade: String,
  val localizacao: String?,
  val estoqueNerus: Int?,
  val estoqueApp: Int?,
  val saldo: Int?,
  val entrada: Int?,
  val nfEntrada: String?,
  val dataEntrada: LocalDate?,
) {

  val estoqueLoja
    get() = (estoqueNerus ?: 0) - (estoqueApp ?: 0)

  //val abrev = listVend.firstOrNull { it.vendno == vendno }?.abrev ?: ""

  companion object {
    fun findAll(filtro: FiltroEstoqueApp): List<GarantiaEstoqueApp> {
      val prdNota = PrdCodigo.findPrdNfe(filtro.nfe)
      val nfe = prdNota.firstOrNull()?.nfe ?: ""
      return estoque.consultaEstoqueApp(filtro.copy(nfe = nfe))
    }

    //val listVend: List<Fornecedor> = saci.findFornecedores()
  }
}

data class FiltroEstoqueApp(
  val query: String,
  val marca: EMarcaPonto,
  val listVend: List<Int>,
  val typeno: Int,
  val clno: Int,
  val estoque: EEstoqueTotal,
  val nfe: String,
  val temGrade: Boolean,
  val grade: String?,
  val codigo: Int?,
)