package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoDev(
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
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var observacaoNota: String?,
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
  var produtos: List<NotaRecebimentoProdutoDev>,
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

  fun produtosCodigoBarras(codigoBarra: String?): NotaRecebimentoProdutoDev? {
    if (codigoBarra.isNullOrBlank()) return null
    return produtos.firstOrNull { it.containBarcode(codigoBarra) }
  }

  fun refreshProdutosDev(): NotaRecebimentoDev? {
    val notaRefresh = findAllDev(
      FiltroNotaRecebimentoProdutoDev(
        loja = this.loja ?: return null,
        pesquisa = "",
      ),
      EStituacaoDev.entries.firstOrNull { it.num == situacaoDev } ?: EStituacaoDev.PENDENTE
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

  fun save(nota: NotaRecebimentoDev) {
    val userno = AppConfig.userLogin()?.no ?: 0
    saci.saveInvAdicional(nota, userno)
  }

  fun marcaSituacao(situacao: EStituacaoDev) {
    this.situacaoDev = situacao.num
    val userno = AppConfig.userLogin()?.no ?: 0
    saci.saveInvAdicionalDev(this, userno)
  }

  companion object {
    fun findAllDev(
      filtro: FiltroNotaRecebimentoProdutoDev,
      situacaoDev: EStituacaoDev,
    ): List<NotaRecebimentoDev> {
      val filtroTodos = filtro.copy()
      return saci.findNotaRecebimentoProdutoDev(filtroTodos, situacaoDev.num).toNota()
        .filter { nota ->
          ((nota.tipoDevolucao ?: 0) > 0)
        }
    }
  }
}

fun List<NotaRecebimentoProdutoDev>.toNota(): List<NotaRecebimentoDev> {
  return this.groupBy { it.chaveDevolucao }.mapNotNull { entry ->
    val produtos = entry.value.distinctBy { "${it.codigo}${it.grade}" }
    val nota = produtos.firstOrNull()
    nota?.let {
      NotaRecebimentoDev(
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

