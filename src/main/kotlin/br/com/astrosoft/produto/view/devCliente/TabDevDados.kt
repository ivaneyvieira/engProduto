package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevDados
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevDadosViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevDados(val viewModel: TabDevDadosViewModel) :
  TabPanelGrid<DadosDev>(DadosDev::class), ITabDevDados {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosDadosDev? = null

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: Loja.lojaZero
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraDev.orEmpty().toList()
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    init()
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    val user = AppConfig.userLogin() as? UserSaci

    val dataInicial = LocalDate.of(2026, 8, 22)

    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()

      if (user?.admin == false) {
        this.min = dataInicial
      }

      addValueChangeListener {
        if (user?.admin == false) {
          val data = it.value
          if (data != null && data.isBefore(dataInicial)) {
            this.value = dataInicial
          }
        }
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()

      if (user?.admin == false) {
        this.min = dataInicial
      }

      addValueChangeListener {
        if (user?.admin == false) {
          val data = it.value
          if (data != null && data.isBefore(dataInicial)) {
            this.value = dataInicial
          }
        }

        viewModel.updateView()
      }
    }
  }

  override fun Grid<DadosDev>.gridPanel() {
    this.addClassName("styling")

    columnGrid(DadosDev::loja, header = "Loja")

    addColumnButton(iconButton = VaadinIcon.PRINT, tooltip = "Imprimir vale troca", header = "Imprimir") { nota ->
      imprimeVale(nota)
    }

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosDadosDev(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    val user = AppConfig.userLogin() as? UserSaci

    addColumnButton(
      iconButton = VaadinIcon.SIGN_IN,
      tooltip = "Autoriza Solicitação",
      header = "Solicitação",
      configIcon = { icon, nota ->
        if (nota.tipoDevEnum != null && nota.produtoTrocaEnum != null) {
          icon.color = "yellow"
        }
      }
    ) { nota ->
      execSolicitacoes(nota)
    }

    if (user?.defazSolicitacao == true) {
      addColumnButton(VaadinIcon.TRASH, "Desfazer Solicitação", "Desfaz") { nota ->
        execDesfazSolicitacoes(nota)
      }
    }

    columnGrid(DadosDev::loginSolicitacao, header = "Autorização")
    columnGrid(DadosDev::loginTroca, header = "Assina Troca")

    columnGrid(DadosDev::ni, header = "NI")
    columnGrid(DadosDev::nfDevolucao, header = "NF Dev")
    columnGrid(DadosDev::dataDevolucao, header = "Data", width = null)
    columnGrid(DadosDev::valorDev, header = "Valor Dev")
    columnGrid(DadosDev::obsTipo, header = "Tipo do Crédito") {
      this.setPartNameGenerator() { nota ->
        if ((nota.custnoObs ?: 0) == 0) {
          null
        } else
          if (nota.nomeClienteObs.isNullOrBlank()) {
            "vermelho"
          } else {
            null
          }
      }
    }
    columnGrid(DadosDev::nfVenda, header = "NF Venda").right()
    columnGrid(DadosDev::dataVenda, header = "Data", width = null)
    columnGrid(DadosDev::custnoVend, header = "Cliente")
    columnGrid(DadosDev::codCliente, header = "For")
    columnGrid(DadosDev::nomeCliente, header = "Nome")
  }

  private fun imprimeVale(nota: DadosDev) {
    val custnoObs = nota.custnoObs ?: 0
    val naoInformado = nota.isNaoInformado()
    val semClienteObs = custnoObs != 0 && nota.nomeClienteObs.isNullOrBlank()
    val tipoTroca = nota.tipoDevEnum == ESolicitacaoTroca.Troca

    if(naoInformado && semClienteObs && tipoTroca) {
      DialogHelper.showWarning("Falta adicionar o código do cliente na observação")
      return
    }

    if (custnoObs != 0 && nota.nomeClienteObs.isNullOrBlank()) {
      DialogHelper.showWarning("O cliente da observação não existe")
      return
    }

    val assinatura = nota.loginTroca ?: ""

    if (assinatura.isBlank()) {
      DialogHelper.showWarning("Devolução sem Assinatura de Troca.")
      return
    }

    if (nota.loginSolicitacao == null) {
      val formAutoriza = FormAutoriza()
      DialogHelper.showForm(caption = "Autoriza Impressão", form = formAutoriza) {
        viewModel.imprimeValeTroca(nota, formAutoriza.login, formAutoriza.senha)
        viewModel.updateView()
      }
    } else {
      viewModel.imprimeValeTroca(nota)
      viewModel.updateView()
    }
  }

  private fun execDesfazSolicitacoes(nota: DadosDev) {
    if (nota.tipoDevEnum == null && nota.produtoTrocaEnum == null && nota.loginSolicitacao == null && nota.loginTroca == null) {
      DialogHelper.showError("Não existe solicitação para desfazer")
    } else {
      DialogHelper.showQuestion("Desfaz a solicitação?") {
        viewModel.desfazSolicitacao(nota)
      }
    }
  }

  private fun execSolicitacoes(nota: DadosDev) {
    val form = FormSolicitacaoDevDados(nota)

    val custnoObs = nota.custnoObs ?: 0
    val naoInformado = nota.isNaoInformado()
    val semClienteObs = custnoObs != 0 && nota.nomeClienteObs.isNullOrBlank()
    val tipoTroca = nota.tipoDevEnum == ESolicitacaoTroca.Troca

    if(naoInformado && semClienteObs && tipoTroca) {
      DialogHelper.showWarning("Falta adicionar o código do cliente na observação")
      return
    }

    DialogHelper.showForm(caption = "Autoriza Devolução", form = form) {
      val result = form.validaFiltro()
      result.onFailure {
        DialogHelper.showWarning(it.message ?: "Erro no filtro")
      }
      result.onSuccess { solicitacaoTroca ->
        val solicitacaoTroca: SolicitacaoTrocaSimples = solicitacaoTroca
        viewModel.autorizaSolicitacao(nota, solicitacaoTroca)
      }
    }
  }

  override fun filtro(): FiltroDadosDev {
    return FiltroDadosDev(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      impresso = false
    )
  }

  override fun updateNotas(notas: List<DadosDev>) {
    updateGrid(notas)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun fechaFormProduto() {
    dlgProduto?.fecha()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devDados == true
  }

  override val label: String
    get() = "Imp Crédito"

  override fun updateComponent() {
    viewModel.updateView()
  }
}