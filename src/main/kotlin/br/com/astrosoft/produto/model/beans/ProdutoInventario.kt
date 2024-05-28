package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoInventario(
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var grade: String?,
  var unidade: String?,
  var validade: Int?,
  var vendno: Int?,
  var fornecedorAbrev: String?,
  var dataEntrada: LocalDate?,
  var vencimento: Int?,
  var estoqueTotalDS: Int?,
  var estoqueTotalMR: Int?,
  var estoqueTotalMF: Int?,
  var estoqueTotalPK: Int?,
  var estoqueTotalTM: Int?,
  var estoqueTotal: Int?,
  var seq: Int?,
  var estoqueDS: Int?,
  var estoqueMR: Int?,
  var estoqueMF: Int?,
  var estoquePK: Int?,
  var estoqueTM: Int?,
  var vendasDS: Int? = null,
  var vendasMR: Int? = null,
  var vendasMF: Int? = null,
  var vendasPK: Int? = null,
  var vendasTM: Int? = null,
  var vencimentoDS: Int?,
  var vencimentoMR: Int?,
  var vencimentoMF: Int?,
  var vencimentoPK: Int?,
  var vencimentoTM: Int?,
) {
  val venda : Int
    get() = (vendasDS ?: 0) + (vendasMR ?: 0) + (vendasMF ?: 0) + (vendasPK ?: 0) + (vendasTM ?: 0)

  val saldo: Int
    get() = (estoqueDS ?: 0) + (estoqueMR ?: 0) + (estoqueMF ?: 0) + (estoquePK ?: 0) + (estoqueTM ?: 0) - venda

  var vencimentoStr: String?
    get() = vencimento(vencimento)
    set(value) {
      vencimento = mesAno(value)
    }

  var vencimentoDSStr: String?
    get() = vencimento(vencimentoDS)
    set(value) {
      vencimentoDS = mesAno(value)
    }

  var vencimentoMRStr: String?
    get() = vencimento(vencimentoMR)
    set(value) {
      vencimentoMR = mesAno(value)
    }

  var vencimentoMFStr: String?
    get() = vencimento(vencimentoMF)
    set(value) {
      vencimentoMF = mesAno(value)
    }

  var vencimentoPKStr: String?
    get() = vencimento(vencimentoPK)
    set(value) {
      vencimentoPK = mesAno(value)
    }

  var vencimentoTMStr: String?
    get() = vencimento(vencimentoTM)
    set(value) {
      vencimentoTM = mesAno(value)
    }

  fun mesAno(value: String?): Int {
    value ?: return 0
    val mes = value.substring(0, 2).toIntOrNull() ?: return 0
    val ano = value.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  fun vencimento(vencimento: Int?): String {
    vencimento ?: return ""
    val vencimentoStr = vencimento.toString()
    if (vencimentoStr.length != 6) return ""
    val mes = vencimentoStr.substring(4, 6)
    val ano = vencimentoStr.substring(2, 4)
    return "$mes/$ano"
  }

  fun update() {
    saci.updateProdutoValidade(this)
  }

  fun remove() {
    saci.removeProdutoValidade(this)
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = produtos.filter {
        it.estoqueDS != 0 || it.estoqueMR != 0 || it.estoqueMF != 0 || it.estoquePK != 0 || it.estoqueTM != 0
      }.mapNotNull { it.dataEntrada }.minOrNull() ?: return produtos
      val saidas = ProdutoInventarioSaida.find(dataInicial)

      val produtosGrupo = produtos.groupBy { "${it.prdno} ${it.grade} ${it.dataEntrada}" }
      val produtosNovos = produtosGrupo.flatMap { (chave, produtosList) ->
        val prdno = produtosList.first().prdno
        val grade = produtosList.first().grade
        val data = produtosList.first().dataEntrada

        var saidaLoja2 = saidas.filter {
          it.prdno == prdno && it.grade == grade && it.loja == 2 && it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja3 = saidas.filter {
          it.prdno == prdno && it.grade == grade && it.loja == 3 && it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja4 = saidas.filter {
          it.prdno == prdno && it.grade == grade && it.loja == 4 && it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja5 = saidas.filter {
          it.prdno == prdno && it.grade == grade && it.loja == 5 && it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja8 = saidas.filter {
          it.prdno == prdno && it.grade == grade && it.loja == 8 && it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }

        produtosList.forEach { produtoInventario ->
          val estoqueDS = produtoInventario.estoqueDS ?: 0
          if (estoqueDS > 0) {
            if (saidaLoja2 > 0) {
              val saida = minOf(estoqueDS, saidaLoja2)
              produtoInventario.vendasDS = saida
              saidaLoja2 -= saida
            }
          }
          val estoqueMR = produtoInventario.estoqueMR ?: 0
          if (estoqueMR > 0) {
            if (saidaLoja3 > 0) {
              val saida = minOf(estoqueMR, saidaLoja3)
              produtoInventario.vendasDS = saida
              saidaLoja3 -= saida
            }
          }
          val estoqueMF = produtoInventario.estoqueMF ?: 0
          if (estoqueMF > 0) {
            if (saidaLoja4 > 0) {
              val saida = minOf(estoqueMF, saidaLoja4)
              produtoInventario.vendasMF = saida
              saidaLoja4 -= saida
            }
          }
          val estoquePK = produtoInventario.estoquePK ?: 0
          if (estoquePK > 0) {
            if (saidaLoja5 > 0) {
              val saida = minOf(estoquePK, saidaLoja5)
              produtoInventario.vendasPK = saida
              saidaLoja5 -= saida
            }
          }
          val estoqueTM = produtoInventario.estoqueTM ?: 0
          if (estoqueTM > 0) {
            if (saidaLoja8 > 0) {
              val saida = minOf(estoqueTM, saidaLoja8)
              produtoInventario.vendasTM = saida
              saidaLoja8 -= saida
            }
          }
        }

        produtosList
      }
      return produtosNovos
    }
  }
}

data class FiltroProdutoInventario(
  val pesquisa: String,
  val codigo: String,
  val validade: Int,
  val grade: String,
  val caracter: ECaracter,
  val mes: Int,
  val ano: Int,
  val loja: Int,
)