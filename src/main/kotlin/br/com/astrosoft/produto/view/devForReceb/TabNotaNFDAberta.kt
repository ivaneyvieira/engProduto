package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaNFDAberta
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaNFDAberta(val viewModel: TabNotaNFDAbertaViewModel) : TabPanelGrid<NotaSaidaDev>(NotaSaidaDev::class),
  ITabNotaNFDAberta {
  //private var colRota: Grid.Column<NotaSaida>? = null
  private var dlgProduto: DlgProdutosNFDAberta? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtPesquisa: TextField
  private var dlgArquivo: DlgArquivoNotaNFDAberta? = null

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val lojaUSer = user?.devFor2Loja ?: 0
    val lojas = if (lojaUSer == 0) {
      viewModel.findAllLojas() + listOf(Loja.lojaZero)
    } else {
      viewModel.findAllLojas().filter { it.no == lojaUSer }
    }
    cmbLoja.setItems(lojas)
    cmbLoja.value = lojas.firstOrNull { it.no == lojaUSer }
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
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
      this.localePtBr()
      this.width = "7rem"
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.width = "7rem"
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "planilhaDev") {
      val produtos = gridPanel.list()
      if (produtos.isEmpty()) {
        ByteArray(0)
      } else {
        viewModel.geraPlanilha(produtos)
      }
    }
  }

  override fun Grid<NotaSaidaDev>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(NotaSaidaDev::loja) {
      this.setHeader("Loja")
    }

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNFDAberta(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    
    addColumnButton(iconButton = VaadinIcon.NEWSPAPER, tooltip = "Observação", header = "Obs") { nota: NotaSaidaDev ->
      adicionaObsercacao(nota)
    }
    
    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo", configIcon = { icon, bean ->
      if (bean.quantArquivos?.let { it > 0 } == true) {
        icon.element.style.set("color", "yellow")
      }
    }) { nota ->
      dlgArquivo = DlgArquivoNotaNFDAberta(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }
    
    addColumnButton(VaadinIcon.PHONE_LANDLINE, "Representantes", "Rep") { nota: NotaSaidaDev ->
      DlgRepresentante().showDialogRepresentante(nota)
    }
    
    addColumnButton(
      iconButton = VaadinIcon.MAILBOX, tooltip = "Envia email", header = "E-mail", configIcon = { icon, nota ->
        if (nota.contaChave().quant > 0) {
          icon.element.style.set("color", "yellow")
        }
      }) { nota: NotaSaidaDev ->
      val dlgEMail = DlgEnviaEmailNotaSaida(viewModel, nota)
      dlgEMail.showDialog {
        viewModel.updateView()
      }
    }
    
    
    columnGrid(NotaSaidaDev::dataColetaStr, header = "Coleta").right()

    columnGrid(NotaSaidaDev::situacaoDevName, width = "7rem") {
      this.setHeader("Aba")
    }

    columnGrid(NotaSaidaDev::nota) {
      this.setHeader("Nota")
    }
    columnGrid(NotaSaidaDev::dataEmissao) {
      this.setHeader("Data")
    }
    columnGrid(NotaSaidaDev::cfop) {
      this.setHeader("CFOP")
    }
    columnGrid(NotaSaidaDev::cliente) {
      this.setHeader("Cliente")
    }
    columnGrid(NotaSaidaDev::nomeCliente, width = "20rem") {
      this.setHeader("Nome Cliente")
    }
    columnGrid(NotaSaidaDev::valorNota, width = "7rem") {
      this.setHeader("Valor")
    }
    columnGrid(NotaSaidaDev::situacaoDup) {
      this.setHeader("Status Dup")
    }
    columnGrid(NotaSaidaDev::duplicata) {
      this.setHeader("Duplicata")
      this.right()
    }
    columnGrid(NotaSaidaDev::observacaoNota, width = "14rem") {
      this.setHeader("Observação")
    }
  }
  
  private fun adicionaObsercacao(nota: NotaSaidaDev) {
    val dlgObservacao = DlgObservacaoNotaSaida(nota) { nota ->
      viewModel.salvaObservacao(nota)
    }
    dlgObservacao.showDialog {
      viewModel.updateView()
    }
  }
  
  private fun adicionaNota(nota: NotaSaidaDev, processa: (nota: NotaSaidaDev) -> Unit) {
    var edtNota: TextField? = null
    DialogHelper.showForm("Adiciona Nota") {
      VerticalLayout().apply {
        this.isPadding = false
        this.isMargin = false
        this.setSpacing(false)
        this@showForm.width = "20rem"
        this.setWidthFull()
        
        edtNota = textField("Nota Fiscal") {
          this.setWidthFull()
        }
        
        this@showForm.setCancelable(true)
        this@showForm.setCancelText("Cancela")
        
        this@showForm.setClassName("custom-top-position")
        
        this@showForm.addConfirmListener {
          viewModel.addNota(nota = nota, nfSaida = edtNota.value ?: "")
          processa(nota)
        }
      }
    }
    return
  }

  override fun filtro(): FiltroNotaDev {
    return FiltroNotaDev(
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNotas(notas: List<NotaSaidaDev>) {
    updateGrid(notas)
    val colValor = gridPanel.getColumnBy(NotaSaidaDev::valorNota)
    colValor.setFooter(notas.sumOf { it.valorNota ?: 0.00 }.format())
  }

  override fun findNota(): NotaSaidaDev? {
    return dlgProduto?.nota
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<NotaSaidaDevProduto> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun arquivosSelecionados(): List<NotaSaidaDevFile> {
    return dlgArquivo?.produtosSelecionados().orEmpty()
  }

  override fun updateViewFile() {
    dlgArquivo?.update()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaNFDAberta == true
  }

  override val label: String
    get() = "NFD Aberta"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraNotaTermica?.toList().orEmpty()
  }
}