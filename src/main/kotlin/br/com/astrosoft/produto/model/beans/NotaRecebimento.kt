package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimento(
  var loja: Int?,
  var lojaSigla: String?,
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
  var observacaoNota: String?,
  var quantFile: Int = 0,
  var tipoNota: String?,
  var countLocalizacao: Int,
  var produtos: List<NotaRecebimentoProduto>,
) {
  val usuarioLogin: String
    get() = if (usuarioRecebe.isNullOrBlank()) login ?: "" else usuarioRecebe ?: ""

  fun marcaSelecionadaEnt(): EMarcaRecebimento {
    return EMarcaRecebimento.entries.firstOrNull { it.codigo == marcaSelecionada } ?: EMarcaRecebimento.TODOS
  }

  fun produtosCodigoBarras(codigoBarra: String?): NotaRecebimentoProduto? {
    if (codigoBarra.isNullOrBlank()) return null
    return produtos.firstOrNull { it.containBarcode(codigoBarra) }
  }

  fun natureza(): String {
    val filter = FiltroNotaEntradaXML(
      loja = loja ?: 0,
      dataInicial = emissao ?: LocalDate.now(),
      dataFinal = emissao ?: LocalDate.now(),
      numero = nfEntrada?.split("/")?.get(0)?.toIntOrNull() ?: 0,
      cnpj = "",
      fornecedor = "",
      preEntrada = EEntradaXML.TODOS,
      entrada = EEntradaXML.TODOS,
      query = "",
      pedido = 0,
    )
    val notaXml = NotaEntradaXML.findAll(filter)
    return notaXml.firstOrNull()?.natureza ?: tipoNota ?: ""
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
        localizacao = listOf("TODOS"),
        tipoNota = EListaContas.TODOS
      )
    ).firstOrNull()
    this.produtos = notaRefresh?.produtos ?: emptyList()
    return notaRefresh
  }

  fun arquivos(): List<InvFile> {
    return InvFile.findAll(this.ni ?: 0)
  }

  companion object {
    fun findAll(filtro: FiltroNotaRecebimentoProduto): List<NotaRecebimento> {
      val filtroTodos = filtro.copy(marca = EMarcaRecebimento.TODOS)
      return saci.findNotaRecebimentoProduto(filtroTodos).toNota().filter { nota ->
        nota.produtos.any { it.marca == filtro.marca.codigo } || filtro.marca == EMarcaRecebimento.TODOS
      }
    }
  }
}

fun List<NotaRecebimentoProduto>.toNota(): List<NotaRecebimento> {
  return this.groupBy { it.ni }.mapNotNull { entry ->
    val produtos = entry.value.distinctBy { "${it.codigo}${it.grade}" }
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
        vendnoProduto = produtos.groupBy { it.vendnoProduto }.entries.minByOrNull {
          -it.value.size
        }?.key,
        usernoRecebe = produtos.firstOrNull { it.usernoRecebe != 0 }?.usernoRecebe,
        usuarioRecebe = produtos.filter { !it.usuarioRecebe.isNullOrBlank() }.mapNotNull { it.usuarioRecebe }.distinct()
          .joinToString(),
        marcaSelecionada = nota.marcaSelecionada,
        quantFile = nota.quantFile ?: 0,
        observacaoNota = nota.observacaoNota,
        tipoNota = nota.tipoNota,
        lojaSigla = nota.lojaSigla,
        countLocalizacao = produtos.filter { !it.localizacao.isNullOrBlank() }.size,
      )
    }
  }
}