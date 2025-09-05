package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaColetaRep
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaColetaRepViewModel
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

class TabNotaColetaRep(val viewModel: TabNotaColetaRepViewModel) :
  TabPanelGrid<NotaRecebimentoDev>(NotaRecebimentoDev::class), ITabNotaColetaRep {
  private var dlgProduto: DlgProdutosNotaColetaRep? = null
  private var dlgArquivo: DlgArquivoNotaColetaRep? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField

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

    button("Pedido") {
      this.icon = VaadinIcon.ARROW_LEFT.create()
      this.onClick {
        viewModel.marcaSituacao(EStituacaoDev.PEDIDO)
      }
    }
  }

  override fun Grid<NotaRecebimentoDev>.gridPanel() {
    this.addClassName("styling")
    this.selectionMode = Grid.SelectionMode.MULTI
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
      dlgProduto = DlgProdutosNotaColetaRep(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo", configIcon = { icon, bean ->
      if (bean.countArq?.let { it > 0 } == true) {
        icon.element.style.set("color", "yellow")
      }
    }) { nota ->
      dlgArquivo = DlgArquivoNotaColetaRep(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }

    this.removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT)

    columnGrid(NotaRecebimentoDev::motivoDevolucaoName, header = "Motivo Devolução")
    columnGrid(NotaRecebimentoDev::numeroDevolucao, header = "ColetaRep").right()
    columnGrid(NotaRecebimentoDev::valorNFDevolucao, header = "Valor Ped")
    columnGrid(NotaRecebimentoDev::notaDevolucao, header = "NFD", width = "5.5rem")
    columnGrid(NotaRecebimentoDev::emissaoDevolucao, header = "Emissão", width = "5.5rem")
    columnGrid(NotaRecebimentoDev::valorDevolucao, header = "Valor NFD", width = null)
    columnGrid(NotaRecebimentoDev::vendnoNF, header = "For NF")
    columnGrid(NotaRecebimentoDev::fornecedorNF, header = "Nome Fornecedor")
    columnGrid(NotaRecebimentoDev::userDevolucao, header = "Usuário")
    columnGrid(NotaRecebimentoDev::observacaoDev, header = "Observação", isExpand = true).textFieldEditor()

    this.setPartNameGenerator {
      if (it.diferenca()) {
        "amarelo"
      } else {
        null
      }
    }
  }

  override fun filtro(): FiltroNotaRecebimentoProdutoDev {
    return FiltroNotaRecebimentoProdutoDev(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNota(notas: List<NotaRecebimentoDev>) {
    this.updateGrid(notas)
    this.gridPanel.getColumnBy(NotaRecebimentoDev::motivoDevolucaoName).setFooter("Total R$:")
    this.gridPanel.getColumnBy(NotaRecebimentoDev::valorNFDevolucao).setFooter(
      notas.sumOf { it.valorNFDevolucao }.format()
    )
    this.gridPanel.getColumnBy(NotaRecebimentoDev::valorDevolucao).setFooter(
      notas.sumOf { it.valorDevolucao ?: 0.00 }.format()
    )
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
    dlgProduto = DlgProdutosNotaColetaRep(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaColetaRep == true
  }

  override val label: String
    get() = "Coleta Rep"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraRec.orEmpty().toList()
  }
}