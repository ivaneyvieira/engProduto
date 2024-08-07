package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userRessuprimentoLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoEstoque(
  var loja: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var unidade: String?,
  var grade: String?,
  var embalagem: Int?,
  var qtdEmbalagem: Int?,
  var estoque: Int?,
  var locSaci: String?,
  var locApp: String?,
  var codForn: Int,
  var fornecedor: String,
  var saldo: Int?,
) {
  fun update() {
    saci.updateProdutoEstoque(this)
  }

  fun recebimentos(): List<ProdutoKardec> {
    val filtro = FiltroNotaRecebimentoProduto(
      loja = 0,
      pesquisa = "",
      marca = EMarcaRecebimento.RECEBIDO,
      dataFinal = null,
      dataInicial = LocalDate.of(2024, 8, 1),
      localizacao = listOf("TODOS"),
      prdno = prdno ?: "",
      grade = grade ?: "SEM GRADE"
    )
    return saci.findNotaRecebimentoProduto(filtro).mapNotNull { nota ->
      ProdutoKardec(
        loja = nota.loja ?: return@mapNotNull null,
        prdno = prdno ?: "",
        grade = grade ?: "",
        data = nota.data ?: return@mapNotNull null,
        doc = nota.nfEntrada ?: "",
        tipo = "Recebimento",
        qtde = nota.quant ?: 0,
        saldo = 0
      )
    }
  }

  private fun filtroRessuprimento(marca :EMarcaRessuprimento): FiltroRessuprimento {
    return FiltroRessuprimento(
      numero = 0,
      pesquisa = "",
      dataNotaInicial = LocalDate.of(2024, 8, 1),
      dataPedidoInicial = LocalDate.of(2024, 7, 1),
      marca = marca,
      temNota = ETemNota.TEM_NOTA,
      lojaRessu = 0,
      codigo = this.codigo?.toString() ?: "",
      grade = grade ?: "",
    )
  }

  private fun ressuprimento(marca: EMarcaRessuprimento): List<ProdutoKardec> {
    val filtro = filtroRessuprimento(marca)
    val ressuprimentos = saci.findRessuprimento(filtro, userRessuprimentoLocais())
    return ressuprimentos.flatMap { ressuprimento ->
      val prdno = prdno ?: return emptyList()
      val grade = grade ?: return emptyList()
      val mult = when (marca) {
        EMarcaRessuprimento.REC -> 1
        EMarcaRessuprimento.ENT -> -1
        else                    -> 0
      }
      ressuprimento.produtos(prdno, grade).mapNotNull { produto ->
        ProdutoKardec(
          loja = ressuprimento.loja ?: 0,
          prdno = prdno,
          grade = grade,
          data = ressuprimento.dataBaixa ?: return@mapNotNull null,
          doc = ressuprimento.notaBaixa ?: "",
          tipo = "Ressuprimento ${marca.descricao.subSequence(0, 3)}",
          qtde = (produto.qtQuantNF ?: 0) * mult,
          saldo = 0
        )
      }
    }
  }

  fun ressuprimento(): List<ProdutoKardec> {
    return ressuprimento(EMarcaRessuprimento.REC) + ressuprimento(EMarcaRessuprimento.ENT)
  }

  companion object {
    fun findProdutoEstoque(filter: FiltroProdutoEstoque): List<ProdutoEstoque> {
      return saci.findProdutoEstoque(filter)
    }
  }
}

data class FiltroProdutoEstoque(
  val loja: Int,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val localizacao: String?,
  val fornecedor: String,
)