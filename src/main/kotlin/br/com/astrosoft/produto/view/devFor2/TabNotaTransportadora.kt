package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devFor2.ITabNotaTransportadora
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaTransportadoraViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaTransportadora(val viewModel: TabNotaTransportadoraViewModel) :
  TabPanelGrid<NotaRecebimento>(NotaRecebimento::class), ITabNotaTransportadora {
  private var dlgProduto: DlgProdutosNotaTransportadora? = null
  private var dlgArquivo: DlgArquivoNotaTransportadora? = null
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

    button("NFD") {
      this.icon = VaadinIcon.ARROW_LEFT.create()
      this.onClick {
        viewModel.marcaNFD()
      }
    }

    button("E-Mail") {
      this.icon = VaadinIcon.ARROW_RIGHT.create()
      this.onClick {
        viewModel.marcaEmail()
      }
    }

  }

  override fun Grid<NotaRecebimento>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(NotaRecebimento::loja, header = "Loja")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNotaTransportadora(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo", configIcon = { icon, bean ->
      if (bean.quantFile > 0) {
        icon.element.style.set("color", "yellow")
      }
    }) { nota ->
      dlgArquivo = DlgArquivoNotaTransportadora(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }

    this.selectionMode = Grid.SelectionMode.MULTI

    columnGrid(NotaRecebimento::tipoDevolucaoName, header = "Motivo Devolução")
    columnGrid(NotaRecebimento::ni, header = "NI").right()
    columnGrid(NotaRecebimento::nfEntrada, header = "NF Entrada").right()
    columnGrid(NotaRecebimento::emissao, header = "Emissão", width = null)
    columnGrid(NotaRecebimento::data, header = "Entrada", width = null)
    columnGrid(NotaRecebimento::vendno, header = "For NF")
    columnGrid(NotaRecebimento::fornecedor, header = "Nome Fornecedor")
    columnGrid(NotaRecebimento::valorNFDevolucao, header = "Valor NF")
    columnGrid(NotaRecebimento::notaDevolucao, header = "NFD", width = null)
    columnGrid(NotaRecebimento::emissaoDevolucao, header = "Emissão", width = null)
    columnGrid(NotaRecebimento::valorDevolucao, header = "Valor Nota", width = null)
    columnGrid(NotaRecebimento::userDevolucao, header = "Usuário")
  }

  override fun filtro(): FiltroNotaRecebimentoProduto {
    val usr = AppConfig.userLogin() as? UserSaci
    return FiltroNotaRecebimentoProduto(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      marca = EMarcaRecebimento.TODOS,
      dataFinal = null,
      dataInicial = LocalDate.of(2024, 9, 1),
      localizacao = usr?.localizacaoRec.orEmpty().toList(),
      tipoNota = EListaContas.TODOS,
      temAnexo = ETemAnexo.TODOS,
    )
  }

  override fun updateNota(notas: List<NotaRecebimento>) {
    this.updateGrid(notas)
  }

  override fun updateArquivos() {
    dlgArquivo?.update()
  }

  override fun arquivosSelecionados(): List<InvFile> {
    return dlgArquivo?.produtosSelecionados().orEmpty()
  }

  override fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    return this.dlgProduto?.produtosSelecionados().orEmpty()
  }

  override fun notasSelecionadas(): List<NotaRecebimento> {
    return this.itensSelecionados()
  }

  override fun updateProduto(): NotaRecebimento? {
    return dlgProduto?.updateProduto()
  }

  fun showDlgProdutos(nota: NotaRecebimento) {
    dlgProduto = DlgProdutosNotaTransportadora(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaTransportadora == true
  }

  override val label: String
    get() = "Transportadora"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraRec.orEmpty().toList()
  }
}