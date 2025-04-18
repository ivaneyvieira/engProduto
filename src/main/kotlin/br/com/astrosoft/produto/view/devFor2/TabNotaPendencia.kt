package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devFor2.ITabNotaPendencia
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaPendenciaViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaPendencia(val viewModel: TabNotaPendenciaViewModel) :
  TabPanelGrid<NotaRecebimentoDev>(NotaRecebimentoDev::class), ITabNotaPendencia {
  private var dlgProduto: DlgProdutosNotaPendencia? = null
  private var dlgArquivo: DlgArquivoNotaPendencia? = null
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

    select("Motivo Devoulucao") {
      this.setItems(ETipoDevolucao.entries)
      this.addValueChangeListener {
        if (it.isFromClient) {
          viewModel.updateMotivo(it.value)
        }
      }
    }

    select("Enviar") {
      this.setItems(EStituacaoDev.entries - EStituacaoDev.PENDENCIA)
      this.setItemLabelGenerator {sit ->
        sit.descricao
      }
      this.addValueChangeListener {
        if (it.isFromClient) {
          viewModel.marcaSituacao(it.value)
        }
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
      dlgProduto = DlgProdutosNotaPendencia(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo", configIcon = { icon, bean ->
      if (bean.arquivos().isNotEmpty()) {
        icon.element.style.set("color", "yellow")
      }
    }) { nota ->
      dlgArquivo = DlgArquivoNotaPendencia(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }

    columnGrid(
      NotaRecebimentoDev::tipoDevolucaoEnun, key = "tipoDevolucaoEnun", header = "Motivo Devolução"
    )
    columnGrid(NotaRecebimentoDev::ni, header = "NI").right()
    columnGrid(NotaRecebimentoDev::numeroDevolucao, header = "NI Dev").right()
    columnGrid(NotaRecebimentoDev::nfEntrada, header = "NF Entrada").right()
    columnGrid(NotaRecebimentoDev::emissao, header = "Emissão", width = null)
    columnGrid(NotaRecebimentoDev::data, header = "Entrada", width = null)
    columnGrid(NotaRecebimentoDev::vendno, header = "For NF")
    columnGrid(NotaRecebimentoDev::fornecedor, header = "Nome Fornecedor")
    columnGrid(NotaRecebimentoDev::valorNFDevolucao, header = "Valor NF")
    columnGrid(NotaRecebimentoDev::notaDevolucao, header = "NFD", width = null)
    columnGrid(NotaRecebimentoDev::emissaoDevolucao, header = "Emissão", width = null)
    columnGrid(NotaRecebimentoDev::valorDevolucao, header = "Valor Nota", width = null)
    columnGrid(NotaRecebimentoDev::userDevolucao, header = "Usuário")
    columnGrid(NotaRecebimentoDev::observacaoDev, header = "Observação", width="200px").textFieldEditor()
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
    dlgProduto = DlgProdutosNotaPendencia(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaPendencia == true
  }

  override val label: String
    get() = "Pendencia"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraRec.orEmpty().toList()
  }
}