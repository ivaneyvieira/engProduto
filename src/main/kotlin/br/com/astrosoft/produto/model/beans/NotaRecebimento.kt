package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
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
  var transportadora: String?,
  var cte: Int?,
  var dataDevolucao: LocalDate?,
  var volume: Int?,
  var peso: Double?,
  val marcaSelecionada: Int?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var observacaoNota: String?,
  var quantFile: Int = 0,
  var tipoNota: String?,
  var countLocalizacao: Int?,
  var tipoDevolucao: Int?,
  var pesoDevolucao: Double?,
  var volumeDevolucao: Int?,
  var transpDevolucao: Int?,
  var transportadoraDevolucao: String?,
  var cteDevolucao: String?,
  var situacaoDev: Int?,
  var userDevolucao: String?,
  var notaDevolucao: String?,
  var emissaoDevolucao: LocalDate?,
  var valorDevolucao: Double?,
  var obsDevolucao: String?,
  var produtos: List<NotaRecebimentoProduto>,
) {
  val valorNFDevolucao
    get() = produtos.sumOf { it.totalGeralDevolucao }

  var tipoDevolucaoEnun
    get() = ETipoDevolucao.findByNum(tipoDevolucao ?: 0)
    set(value) {
      tipoDevolucao = value?.num
    }

  val tipoDevolucaoName
    get() = tipoDevolucaoEnun?.descricao

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

  fun refreshProdutos(marcaDevolucao: Boolean): NotaRecebimento? {
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
        tipoNota = EListaContas.TODOS,
        temAnexo = ETemAnexo.TODOS,
      ),
      marcaDevolucao
    ).firstOrNull()
    this.produtos = notaRefresh?.produtos ?: emptyList()
    return notaRefresh
  }

  fun arquivos(): List<InvFile> {
    val tipoName = tipoDevolucaoEnun?.name
    val outrosTipos = ETipoDevolucao.entries.filter { it.name != tipoName }.map { it.name }
    val listFile = InvFile.findAll(this.ni ?: 0)
    val marcaDevolucao = (tipoDevolucao ?: 0) > 0
    return if (marcaDevolucao) {
      if (tipoName == null) {
        listFile
      } else {
        listFile.filter {
          (it.title == tipoName) || (it.title !in outrosTipos)
        }
      }
    } else {
      listFile
    }
  }

  fun save(nota: NotaRecebimento) {
    val userno = AppConfig.userLogin()?.no ?: 0
    saci.saveInvAdicional(nota, userno)
  }

  fun marcaSituacao(situacao: EStituacaoDev) {
    this.situacaoDev = situacao.num
    val userno = AppConfig.userLogin()?.no ?: 0
    saci.saveInvAdicional(this, userno)
  }

  companion object {
    fun findAll(
      filtro: FiltroNotaRecebimentoProduto,
      marcaDevolucao: Boolean,
    ): List<NotaRecebimento> {
      val filtroTodos = filtro.copy(marca = EMarcaRecebimento.TODOS)
      return saci.findNotaRecebimentoProduto(filtroTodos).toNota(marcaDevolucao)
        .filter { nota ->
          (nota.produtos.any { it.marca == filtro.marca.codigo } || filtro.marca == EMarcaRecebimento.TODOS) &&
          (if (marcaDevolucao) (nota.tipoDevolucao ?: 0) > 0 else true)
        }
    }

    fun findAllDev(
      filtro: FiltroNotaRecebimentoProduto,
      marcaDevolucao: Boolean,
      situacaoDev: EStituacaoDev,
    ): List<NotaRecebimento> {
      val filtroTodos = filtro.copy(marca = EMarcaRecebimento.TODOS)
      return saci.findNotaRecebimentoProdutoDev(filtroTodos, situacaoDev.num).toNota(marcaDevolucao)
        .filter { nota ->
          (nota.produtos.any { it.marca == filtro.marca.codigo } || filtro.marca == EMarcaRecebimento.TODOS) &&
          (if (marcaDevolucao) (nota.tipoDevolucao ?: 0) > 0 else true)
        }
    }
  }
}

fun List<NotaRecebimentoProduto>.toNota(marcaDevolucao: Boolean): List<NotaRecebimento> {
  return this.groupBy { "${it.ni} ${if (marcaDevolucao) it.tipoDevolucao else 0}" }.mapNotNull { entry ->
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
        transportadora = nota.transportadora,
        tipoDevolucao = nota.tipoDevolucao ?: 0,
        pesoDevolucao = nota.pesoDevolucao ?: 0.00,
        volumeDevolucao = nota.volumeDevolucao ?: 0,
        countLocalizacao = produtos.filter { !it.localizacao.isNullOrBlank() }.size,
        transpDevolucao = nota.transpDevolucao,
        cteDevolucao = nota.cteDevolucao,
        situacaoDev = nota.situacaoDev,
        userDevolucao = nota.userDevolucao,
        notaDevolucao = nota.notaDevolucao,
        emissaoDevolucao = nota.emissaoDevolucao,
        valorDevolucao = nota.valorDevolucao,
        obsDevolucao = nota.obsDevolucao,
        dataDevolucao = nota.dataDevolucao,
        transportadoraDevolucao = nota.transportadoraDevolucao,
      )
    }
  }
}

