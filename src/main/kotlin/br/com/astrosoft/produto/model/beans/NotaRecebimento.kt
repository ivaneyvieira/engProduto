package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
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
  var cliente: String?,
  var cnpjCliente: String?,
  var enderecoCliente: String?,
  var bairroCliente: String?,
  var cidadeCliente: String?,
  var ufCliente: String?,
  var vendno: Int?,
  var vendnoProduto: Int?,
  var fornecedor: String?,
  var cnpjFornecedor: String?,
  var enderecoFornecedor: String?,
  var bairroFornecedor: String?,
  var cidadeFornecedor: String?,
  var ufFornecedor: String?,
  var valorNF: Double?,
  var pedComp: Int?,
  var transp: Int?,
  var transportadora: String?,
  var cnpjTransportadora: String?,
  var enderecoTransportadora: String?,
  var bairroTransportadora: String?,
  var cidadeTransportadora: String?,
  var ufTransportadora: String?,
  var cte: Int?,
  var dataDevolucao: LocalDate?,
  var volume: Int?,
  var peso: Double?,
  val marcaSelecionada: Int?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var empNoTermo: Int?,
  var empNomeTermo: String?,
  var empCpfTermo: String?,
  var empEmailTermo: String?,
  var usernoEnvio: Int?,
  var loginEnvio: String?,
  var usernoReceb: Int?,
  var loginReceb: String?,
  var observacaoNota: String?,
  var tipoNota: String?,
  var countLocalizacao: Int?,
  var motivoDevolucao: Int?,
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
  val dataStr get() = data?.format("dd/MM/yy") ?: ""

  val emissaoStr get() = emissao?.format("dd/MM/yy") ?: ""

  val niStr get() = ni?.toString()?.lpad(7, " ") ?: ""

  val nfEntradaStr get() = nfEntrada?.lpad(12, " ") ?: ""

  val valorTotal
    get() = produtos.sumOf { it.valorTotal ?: 0.0 }

  val frete
    get() = produtos.sumOf { it.frete ?: 0.0 }

  val valorDesconto
    get() = produtos.sumOf { it.valorDesconto ?: 0.0 }

  val outDesp
    get() = produtos.sumOf { it.outDesp ?: 0.0 }

  val baseIcms
    get() = produtos.sumOf { it.baseIcms ?: 0.0 }

  val valIcms
    get() = produtos.sumOf { it.valIcms ?: 0.0 }

  val baseSubst
    get() = produtos.sumOf { it.baseSubst ?: 0.0 }

  val icmsSubst
    get() = produtos.sumOf { it.icmsSubst ?: 0.0 }

  val valIPI
    get() = produtos.sumOf { it.valIPI ?: 0.0 }

  val totalGeral
    get() = produtos.sumOf { it.totalGeral }

  val valorNFDevolucao
    get() = produtos.sumOf { it.totalGeralDevolucao }

  var motivoDevolucaoEnun
    get() = EMotivoDevolucao.findByNum(motivoDevolucao ?: 0)
    set(value) {
      motivoDevolucao = value?.num
    }

  val motivoDevolucaoName
    get() = motivoDevolucaoEnun?.descricao

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
        tipoNota = EListaContas.TODOS,
        temAnexo = ETemAnexo.TODOS,
      )
    ).firstOrNull()
    this.produtos = notaRefresh?.produtos ?: emptyList()
    return notaRefresh
  }

  fun arquivos(): List<InvFile> {
    val tipoName = motivoDevolucaoEnun?.name
    val outrosTipos = EMotivoDevolucao.entries.filter { it.name != tipoName }.map { it.name }
    val listFile = InvFile.findAll(this.ni ?: 0)
    val marcaDevolucao = (motivoDevolucao ?: 0) > 0
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

  fun save() {
    saci.saveInvAdicional(this)
  }

  companion object {
    fun findAll(
      filtro: FiltroNotaRecebimentoProduto,
    ): List<NotaRecebimento> {
      val filtroTodos = filtro.copy(marca = EMarcaRecebimento.TODOS)
      return saci.findNotaRecebimentoProduto(filtroTodos).toNota()
        .filter { nota ->
          nota.produtos.any { it.marca == filtro.marca.codigo } || filtro.marca == EMarcaRecebimento.TODOS
        }
    }
  }
}

fun List<NotaRecebimentoProduto>.toNota(): List<NotaRecebimento> {
  return this.groupBy { it.chaveNi }.mapNotNull { entry ->
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
        observacaoNota = nota.observacaoNota,
        tipoNota = nota.tipoNota,
        lojaSigla = nota.lojaSigla,
        transportadora = nota.transportadora,
        motivoDevolucao = nota.motivoDevolucao ?: 0,
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
        cliente = nota.cliente,
        cnpjCliente = nota.cnpjCliente,
        enderecoCliente = nota.enderecoCliente,
        bairroCliente = nota.bairroCliente,
        cidadeCliente = nota.cidadeCliente,
        ufCliente = nota.ufCliente,
        cnpjFornecedor = nota.cnpjFornecedor,
        enderecoFornecedor = nota.enderecoFornecedor,
        bairroFornecedor = nota.bairroFornecedor,
        cidadeFornecedor = nota.cidadeFornecedor,
        ufFornecedor = nota.ufFornecedor,
        cnpjTransportadora = nota.cnpjTransportadora,
        enderecoTransportadora = nota.enderecoTransportadora,
        bairroTransportadora = nota.bairroTransportadora,
        cidadeTransportadora = nota.cidadeTransportadora,
        ufTransportadora = nota.ufTransportadora,
        empNoTermo = nota.empNoTermo,
        empNomeTermo = nota.empNomeTermo,
        empCpfTermo = nota.empCpfTermo,
        empEmailTermo = nota.empEmailTermo,
        usernoEnvio = nota.usernoEnvio,
        loginEnvio = nota.loginEnvio,
        usernoReceb = nota.usernoReceb,
        loginReceb = nota.loginReceb,
      )
    }
  }
}

