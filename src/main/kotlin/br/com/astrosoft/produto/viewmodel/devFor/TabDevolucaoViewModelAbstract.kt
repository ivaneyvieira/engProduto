package br.com.astrosoft.produto.viewmodel.devFor

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.IViewModelUpdate
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import java.time.LocalDate

abstract class TabDevolucaoViewModelAbstract<T : IDevolucaoAbstractView>(val viewModel: DevolucaoAbstractViewModel<T>) :
  IViewModelUpdate {
  protected abstract val subView: ITabNota

  open fun salvaSituacao(situacao: ESituacaoPendencia?, itens: List<NotaSaidaDevolucao>) = viewModel.exec {
    situacao ?: fail("A situação não foi selecionada")
    itens.ifEmpty {
      fail("Não foi selecionado nenhuma nota")
    }
    itens.forEach { nota ->
      val userSaci = (AppConfig.userLogin() as? UserSaci)?.login ?: ""
      nota.situacao = situacao.valueStr ?: ""
      if (!situacao.valueStr.isNullOrBlank()) {
        nota.dataSituacao = LocalDate.now()
        nota.usuarioSituacao = userSaci
      }
      NotaSaidaDevolucao.salvaDesconto(nota)
    }
    subView.updateComponent()
  }

  fun salvaSituacaoPedido(situacao: ESituacaoPedido?, itens: List<NotaSaidaDevolucao>) = viewModel.exec {
    situacao ?: fail("A situação não foi selecionada")
    itens.ifEmpty {
      fail("Não foi selecionado nenhum pedido")
    }
    itens.forEach { nota ->
      val userSaci = (AppConfig.userLogin() as? UserSaci)?.login ?: ""
      nota.situacao = situacao.valueStr
      nota.dataSituacao = LocalDate.now()
      nota.usuarioSituacao = userSaci
      NotaSaidaDevolucao.salvaDesconto(nota)
    }
    subView.updateNota()
  }

  fun imprimirNotaFornecedor(notas: List<NotaSaidaDevolucao>, ocorrencias: List<String>) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma item foi selecionado")
    }
    subView.imprimeNotaFornecedor(notas, ocorrencias)
  }

  fun imprimirNotaDevolucao(notas: List<NotaSaidaDevolucao>, resumida: Boolean = false) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma item foi selecionado")
    }
    subView.imprimeSelecionados(notas, resumida)
  }

  fun imprimirRelatorioFornecedor(notas: List<NotaSaidaDevolucao>) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma item foi selecionado")
    }
    subView.imprimirRelatorioFornecedor(notas)
  }

  fun imprimirRelatorio(notas: List<NotaSaidaDevolucao>) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma item foi selecionado")
    }
    subView.imprimirRelatorio(notas)
  }

  fun excelRelatorio(notas: List<NotaSaidaDevolucao>) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma item foi selecionado")
    }
    return@exec subView.excelRelatorio(notas)
  }

  fun excelPedido(notas: List<NotaSaidaDevolucao>) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma item foi selecionado")
    }
    return@exec subView.excelPedido(notas)
  }

  override fun updateView() = viewModel.exec {
    subView.updateGrid(listFornecedores())
    FornecedorNdd.updateNotas()
  }

  fun listFornecedores(): List<FornecedorDevolucao> {
    NotaSaidaDevolucao.updateNotasDevolucao(subView)
    return NotaSaidaDevolucao.findFornecedores(subView.filtro().query)
  }

  fun editRmk(nota: NotaSaidaDevolucao) {
    subView.editRmk(nota) { notaSaida ->
      notaSaida.saveRmk()
    }
  }

  fun enviarEmail(notas: List<NotaSaidaDevolucao>) = viewModel.exec {
    if (notas.isEmpty()) fail("Nenhuma nota selecionada")
    subView.enviaEmail(notas)
  }

  fun editRmkVend(fornecedor: FornecedorDevolucao) {
    subView.editRmkVend(fornecedor) { forn ->
      forn.saveRmkVend()
    }
  }

  fun imprimirRelatorioResumido(fornecedores: List<FornecedorDevolucao>) = viewModel.exec {
    fornecedores.ifEmpty {
      fail("Nenhuma fornecedor foi selecionada")
    }
    subView.imprimirRelatorioResumido(fornecedores)
  }

  fun findLojas(): List<Loja> = Loja.allLojas().toList()

  fun salvaDesconto(notaSaida: NotaSaidaDevolucao?) = viewModel.exec {
    notaSaida ?: fail("Nenhuma nota selecionada")
    NotaSaidaDevolucao.salvaDesconto(notaSaida)
  }

  fun imprimirRelatorioPedidos(notas: List<NotaSaidaDevolucao>) = viewModel.exec {
    notas.ifEmpty {
      fail("Nenhuma fornecedor foi selecionada")
    }
    subView.imprimirRelatorioPedidos(notas)
  }
}

enum class SimNao(val value: String) {
  SIM("S"), NAO("N"), NONE("")
}

enum class Serie(val value: String) {
  AVA("AVA"),
}

interface IFiltro {
  val serie: Serie
  val pago66: SimNao
  val pago01: SimNao
  val coleta01: SimNao
  val remessaConserto: SimNao
  val situacaoPendencia: ESituacaoPendencia?
  val situacaoPedido: List<ESituacaoPedido>
  val filterSituacao: ESituacaoPedido
  val filterSituacaoPendencia: ESituacaoPendencia
  fun filtro(): FiltroFornecedor
}

interface ITabNota : ITabView, IFiltro {
  fun updateNota()
  fun updateGrid(itens: List<FornecedorDevolucao>)
  fun itensSelecionados(): List<FornecedorDevolucao>
  fun imprimeSelecionados(notas: List<NotaSaidaDevolucao>, resumida: Boolean)
  fun imprimeNotaFornecedor(notas: List<NotaSaidaDevolucao>, ocorrencias: List<String>)
  fun imprimirRelatorioFornecedor(notas: List<NotaSaidaDevolucao>)
  fun imprimirRelatorio(notas: List<NotaSaidaDevolucao>)
  fun excelRelatorio(notas: List<NotaSaidaDevolucao>): ByteArray
  fun excelPedido(notas: List<NotaSaidaDevolucao>): ByteArray
  fun imprimirRelatorioResumido(fornecedores: List<FornecedorDevolucao>)
  fun editRmk(nota: NotaSaidaDevolucao, save: (NotaSaidaDevolucao) -> Unit)
  override fun filtro(): FiltroFornecedor
  fun setFiltro(filtro: FiltroFornecedor)
  fun enviaEmail(notas: List<NotaSaidaDevolucao>)
  fun editRmkVend(fornecedor: FornecedorDevolucao, save: (FornecedorDevolucao) -> Unit)
  fun imprimirRelatorioPedidos(notas: List<NotaSaidaDevolucao>)
}

enum class ESituacaoPendencia(
  val title: String,
  val valueStr: String?,
  val descricao: String,
  val userCol: String? = null,
  val dataSitCol: String? = null,
  val situacaoCol: String? = null,
  val notaCol: String? = "",
  val docCol: String? = null,
  val numeroCol: String? = null,
  val niCol: String? = null,
  val dataCol: String? = null,
  val cssCor: String = "marcaRed"
) {
  BASE(title = "Todas", valueStr = null, descricao = "Todas"),
  NOTA(
    title = "Nota",
    valueStr = "",
    descricao = "Nota",
    notaCol = "Aguadar",
    userCol = "",
    dataSitCol = "",
    docCol = "",
    numeroCol = "",
    niCol = "",
    dataCol = "Saída"
  ),
  EMAIL(title = "E-mail", valueStr = "E-MAIL", descricao = "E-mail", docCol = "", numeroCol = "", niCol = ""),
  TRANSITO(
    title = "Trânsito",
    valueStr = "TRANSITO",
    descricao = "Trânsito",
    dataCol = "Saída",
    docCol = "",
    numeroCol = "",
    niCol = ""
  ),
  FABRICA(title = "Fabrica", valueStr = "FABRICA", descricao = "Fábrica", docCol = "", numeroCol = "", niCol = ""),
  CREDITO_AGUARDAR(
    title = "Aguarda",
    valueStr = "CREDITO_AGUARDAR",
    descricao = "Aguardar Crédito",
    docCol = "",
    niCol = "",
    numeroCol = "Tipo:L"
  ),
  CREDITO_CONCEDIDO(
    title = "Concedido",
    valueStr = "CREDITO_CONCEDIDO",
    descricao = "Crédito Concedido",
    dataCol = "Previsão",
    docCol = "Título",
    numeroCol = ""
  ),
  CREDITO_APLICADO(
    title = "Aplicado",
    valueStr = "CREDITO_APLICADO",
    descricao = "Crédito Aplicado",
    docCol = "",
    numeroCol = "Título",
    dataCol = "Vencimento"
  ),
  CREDITO_CONTA(
    title = "Conta",
    valueStr = "CREDITO_CONTA",
    descricao = "Crédito Conta",
    docCol = "Ag",
    numeroCol = "CC",
    niCol = "Banco:L",
    dataCol = "Previsão"
  ),
  BONIFICADA(
    title = "Bonificada",
    valueStr = "BONIFICADA",
    descricao = "Bonificado",
    docCol = "",
    numeroCol = "Nota",
    dataCol = "Emissão"
  ),
  REPOSICAO(
    title = "Reposição",
    valueStr = "REPOSICAO",
    descricao = "Reposição",
    docCol = "",
    numeroCol = "Nota",
    dataCol = "Emissão"
  ),
  RETORNO(
    title = "Retorno",
    valueStr = "RETORNO",
    descricao = "Retorno",
    docCol = "",
    numeroCol = "Nota",
    dataCol = "Emissão"
  ),
  AGUARDA_COLETA(
    title = "Coleta",
    valueStr = "Aguarda Coleta",
    descricao = "Aguarda Coleta",
    docCol = "",
    numeroCol = "",
    niCol = "",
    cssCor = "marcaDiferenca"
  ),
  ASSINA_CTE(
    title = "CTe",
    valueStr = "Assinar CTe",
    descricao = "Assinar CTe",
    docCol = "",
    numeroCol = "",
    niCol = "",
    cssCor = "marcaDiferenca"
  ),
  BANCO121(
    title = "Banco 121",
    valueStr = "Banco 121",
    descricao = "Banco 121",
    docCol = "",
    numeroCol = "",
    niCol = "",
    cssCor = "marcaDiferenca"
  ),
}

enum class ESituacaoPedido(
  val valueStr: String,
  val descricao: String,
  val pendente: Boolean,
  val avaria: Boolean = false
) {
  VAZIO(valueStr = "", descricao = "", pendente = true),
  PAGO(valueStr = "PAGO", descricao = "Pago", pendente = false),
  RETORNO(valueStr = "RETORNO", descricao = "Retorno", pendente = false),
  EMAIL_ENVIADO(valueStr = "PED_EML_EVD", descricao = "E-mail", pendente = true, avaria = true),
  NFD_AUTOZ(valueStr = "PED_NFD_ATZ", descricao = "NFD", pendente = false, avaria = true),
  TRANSPORTADORA(valueStr = "PED_COLETADO", descricao = "Transportadora", pendente = false, avaria = true),
  ACERTO(valueStr = "PED_ACERTO", descricao = "Acerto", pendente = false, avaria = true),
  REPOSTO(valueStr = "PED_REPOSTO", descricao = "Reposto", pendente = false, avaria = true),
  BAIXA(valueStr = "PED_AJT_GAR", descricao = "Baixa", pendente = false),

  //PRODUTO_BAIXADO(valueStr = "PROD_BAIX", descricao = "Prod Baixado"),
  ASSISTENCIA(valueStr = "ASSISTENCIA", descricao = "Assistencia", pendente = true),
  ASSISTENCIA_RETORNO(valueStr = "ASSIST_RET", descricao = "Assistencia Retorno", pendente = false),

  //SUCATA(valueStr = "SUCATA", descricao = "Sucata"),
  LIBERADO(valueStr = "LIBERADO", descricao = "Liberado", pendente = true),
  PERCA(valueStr = "PERCA", descricao = "Perca", pendente = false),
  DESCARTE(valueStr = "DESCARTE", descricao = "Descarte", pendente = false),
}
