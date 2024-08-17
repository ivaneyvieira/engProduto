package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimento(
  var loja: Int?,
  var login: String?,
  var data: LocalDate?,
  var emissao: LocalDate?,
  var ni: Int?,
  var nfEntrada: String?,
  var custno: Int?,
  var vendno: Int?,
  var vendnoProduto: Int?,
  var fornecedor: String?,
  var valorNF: Double?,
  var pedComp: Int?,
  var transp: Int?,
  var cte: Int?,
  var volume: Int?,
  var peso: Double?,
  val marcaSelecionada: Int?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var produtos: List<NotaRecebimentoProduto>,
) {
  val usuarioLogin: String
    get() = if(login.isNullOrBlank()) usuarioRecebe ?: "" else login ?: ""

  fun marcaSelecionadaEnt(): EMarcaRecebimento {
    return EMarcaRecebimento.entries.firstOrNull { it.codigo == marcaSelecionada } ?: EMarcaRecebimento.TODOS
  }

  fun produtosCodigoBarras(codigoBarra: String?): NotaRecebimentoProduto? {
    if (codigoBarra.isNullOrBlank()) return null
    return produtos.firstOrNull { it.containBarcode(codigoBarra) }
  }

  fun refreshProdutos(): NotaRecebimento? {
    val marcaEng = marcaSelecionadaEnt()
    val notaRefresh = findAll(
      FiltroNotaRecebimentoProduto(
        loja = this.loja ?: return null,
        pesquisa = "",
        marca = marcaEng,
        invno = this.ni ?: return null,
        dataFinal = data,
        dataInicial = data,
        localizacao = listOf("TODOS")
      )
    ).firstOrNull()
    this.produtos = notaRefresh?.produtos ?: emptyList()
    return notaRefresh
  }

  fun arquivos(): List<InvFile> {
    return InvFile.findAll(this.ni ?: 0)
  }

  fun recebe(user:UserSaci) {
    this.usernoRecebe = user.no
    saci.updateNotaRecebimento(this)
  }

  companion object {
    fun findAll(filtro: FiltroNotaRecebimentoProduto): List<NotaRecebimento> {
      return saci.findNotaRecebimentoProduto(filtro).toNota()
    }
  }
}

fun List<NotaRecebimentoProduto>.toNota(): List<NotaRecebimento> {
  return this.groupBy { it.ni }.mapNotNull { entry ->
    val produtos = entry.value
    val nota = produtos.firstOrNull()
    nota?.let {
      NotaRecebimento(
        loja = nota.loja,
        login = produtos.asSequence().mapNotNull { it.login }
          .filter { it != "" }
          .distinct().sorted().joinToString(separator = ", ") { login ->
            login
          },
        data = nota.data,
        emissao = nota.emissao,
        ni = nota.ni,
        nfEntrada = nota.nfEntrada,
        custno = nota.custno,
        vendno = nota.vendno,
        fornecedor = nota.fornecedor,
        valorNF = nota.valorNF,
        pedComp = nota.pedComp,
        transp = nota.transp,
        cte = nota.cte,
        volume = nota.volume,
        peso = nota.peso,
        produtos = produtos,
        vendnoProduto = produtos.groupBy { it.vendnoProduto }.entries.sortedBy {
          -it.value.size
        }.firstOrNull()?.key,
        usernoRecebe = nota.usernoRecebe,
        usuarioRecebe = nota.usuarioRecebe,
        marcaSelecionada = nota.marcaSelecionada
      )
    }
  }
}