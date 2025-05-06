package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devFor2.ITabNotaGarantia
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaGarantiaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaGarantia(val viewModel: TabNotaGarantiaViewModel) :
  TabPanelGrid<NotaRecebimentoDev>(NotaRecebimentoDev::class), ITabNotaGarantia {
  private var dlgProduto: DlgProdutosNotaGarantia? = null
  private var dlgArquivo: DlgArquivoNotaGarantia? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = Loja.lojaZero
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
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Remove") {
      this.icon = VaadinIcon.TRASH.create()
      this.onClick {
        viewModel.removeNota()
      }
    }

    button("Pendencia") {
      this.icon = VaadinIcon.ARROW_LEFT.create()
      this.onClick {
        viewModel.marcaSituacao(EStituacaoDev.PENDENCIA)
      }
    }

    button("Transportadora") {
      this.icon = VaadinIcon.ARROW_RIGHT.create()
      this.onClick {
        viewModel.marcaSituacao(EStituacaoDev.TRANSPORTADORA)
      }
    }
  }

  override fun Grid<NotaRecebimentoDev>.gridPanel() {
    this.addClassName("styling")
    this.format()

    this.withEditor(
      classBean = NotaRecebimentoDev::class,
      openEditor = {
        val edit = getColumnBy(NotaRecebimentoDev::observacaoDev) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.saveNota(nota = it.bean, updateGrid = true)
      })

    columnGrid(NotaRecebimentoDev::loja, header = "Loja")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNotaGarantia(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo", configIcon = { icon, bean ->
      if (bean.countArq?.let { it > 0 } == true) {
        icon.element.style.set("color", "yellow")
      }
    }) { nota ->
      dlgArquivo = DlgArquivoNotaGarantia(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }

    this.selectionMode = Grid.SelectionMode.MULTI

    this.removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT)

    columnGrid(NotaRecebimentoDev::tipoDevolucaoName, header = "Motivo Devolução")
    columnGrid(NotaRecebimentoDev::niListStr, header = "NI", width = "5.5rem")
    columnGrid(NotaRecebimentoDev::numeroDevolucao, header = "NI Dev").right()
    columnGrid(NotaRecebimentoDev::nfEntrada, header = "NF Entrada").right()
    columnGrid(NotaRecebimentoDev::emissao, header = "Emissão", width = null)
    columnGrid(NotaRecebimentoDev::data, header = "Entrada", width = null)
    columnGrid(NotaRecebimentoDev::vendnoNF, header = "For NF")
    columnGrid(NotaRecebimentoDev::fornecedorNF, header = "Nome Fornecedor")
    columnGrid(NotaRecebimentoDev::valorNFDevolucao, header = "Valor NF")
    columnGrid(NotaRecebimentoDev::notaDevolucao, header = "NFD", width = null)
    columnGrid(NotaRecebimentoDev::emissaoDevolucao, header = "Emissão", width = null)
    columnGrid(NotaRecebimentoDev::valorDevolucao, header = "Valor Nota", width = null)
    columnGrid(NotaRecebimentoDev::userDevolucao, header = "Usuário")
    columnGrid(NotaRecebimentoDev::observacaoDev, header = "Observação", width = "200px").textFieldEditor()
  }

  override fun filtro(): FiltroNotaRecebimentoProdutoDev {
    return FiltroNotaRecebimentoProdutoDev(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNota(notas: List<NotaRecebimentoDev>) {
    this.updateGrid(notas)
  }

  override fun updateArquivos() {
    dlgArquivo?.update()
  }

  override fun arquivosSelecionados(): List<InvFileDev> {
    return dlgArquivo?.produtosSelecionados().orEmpty()
  }

  override fun produtosSelecionados(): List<NotaRecebimentoProdutoDev> {
    return this.dlgProduto?.produtosSelecionados().orEmpty()
  }

  override fun notasSelecionadas(): List<NotaRecebimentoDev> {
    return this.itensSelecionados()
  }

  override fun updateProduto(): NotaRecebimentoDev? {
    return dlgProduto?.updateProduto()
  }

  fun showDlgProdutos(nota: NotaRecebimentoDev) {
    dlgProduto = DlgProdutosNotaGarantia(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaGarantia == true
  }

  override val label: String
    get() = "Garantia"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraRec.orEmpty().toList()
  }
}