package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoInventarioViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.github.mvysny.kaributools.sortProperty
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabProdutoInventario(val viewModel: TabProdutoInventarioViewModel) :
  TabPanelGrid<ProdutoInventario>(ProdutoInventario::class),
  ITabProdutoInventario {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: TextField
  private lateinit var edtInventario: IntegerField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCartacer: Select<ECaracter>

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtCodigo = textField("Código") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtInventario = integerField("Validade") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtGrade = textField("Grade") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    cmbCartacer = select("Caracter") {
      this.setItems(ECaracter.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = ECaracter.TODOS
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Adicionar") {
      this.icon = VaadinIcon.PLUS.create()
      addClickListener {
        viewModel.adicionarLinha()
      }
    }

    button("Remover") {
      this.icon = VaadinIcon.TRASH.create()
      addClickListener {
        viewModel.removerLinha()
      }
    }

    downloadExcel(PlanilhaProdutoInventario())
  }

  override fun Grid<ProdutoInventario>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.withEditor(
      ProdutoInventario::class,
      openEditor = {
        this.focusEditor(ProdutoInventario::estoqueDS)
      },
      closeEditor = {
        viewModel.salvaInventario(it.bean)
      })

    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoInventario::codigo, header = "Código")
    columnGrid(ProdutoInventario::descricao, header = "Descrição").expand()
    columnGrid(ProdutoInventario::grade, header = "Grade")
    columnGrid(ProdutoInventario::unidade, header = "Un")
    columnGrid(ProdutoInventario::vendno, header = "Cod For")
    columnGrid(ProdutoInventario::fornecedorAbrev, header = "Fornecedor")
    columnGrid(ProdutoInventario::validade, header = "Val")
    columnGrid(ProdutoInventario::estoqueTotal, header = "Total")

    val user = AppConfig.userLogin() as? UserSaci

    if (user?.lojaProduto == 2 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueDS, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vencimentoDSStr, header = "Venc", width = "130px").mesAnoFieldEditor()
    }
    if (user?.lojaProduto == 3 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueMR, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vencimentoMRStr, header = "Venc", width = "130px").mesAnoFieldEditor()
    }
    if (user?.lojaProduto == 4 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueMF, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vencimentoMFStr, header = "Venc", width = "130px").mesAnoFieldEditor()
    }
    if (user?.lojaProduto == 5 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoquePK, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vencimentoPKStr, header = "Venc", width = "130px").mesAnoFieldEditor()
    }
    if (user?.lojaProduto == 8 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueTM, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vencimentoTMStr, header = "Venc", width = "130px").mesAnoFieldEditor()
    }
    val headerRow = prependHeaderRow()
    headerRow.join(
      this.getColumnBy(ProdutoInventario::codigo),
      this.getColumnBy(ProdutoInventario::descricao),
      this.getColumnBy(ProdutoInventario::grade),
      this.getColumnBy(ProdutoInventario::unidade),
      this.getColumnBy(ProdutoInventario::validade),
      this.getColumnBy(ProdutoInventario::vendno),
      this.getColumnBy(ProdutoInventario::fornecedorAbrev),
      this.getColumnBy(ProdutoInventario::estoqueTotal),
    ).text = "Produto"
    if (user?.lojaProduto == 2 || user?.lojaProduto == 0) {
      headerRow.join(
        this.getColumnBy(ProdutoInventario::estoqueDS),
        this.getColumnBy(ProdutoInventario::vencimentoDSStr),
      ).text = "DS"
    }
    if (user?.lojaProduto == 3 || user?.lojaProduto == 0) {
      headerRow.join(
        this.getColumnBy(ProdutoInventario::estoqueMR),
        this.getColumnBy(ProdutoInventario::vencimentoMRStr),
      ).text = "MR"
    }
    if (user?.lojaProduto == 4 || user?.lojaProduto == 0) {
      headerRow.join(
        this.getColumnBy(ProdutoInventario::estoqueMF),
        this.getColumnBy(ProdutoInventario::vencimentoMFStr),
      ).text = "MF"
    }
    if (user?.lojaProduto == 5 || user?.lojaProduto == 0) {
      headerRow.join(
        this.getColumnBy(ProdutoInventario::estoquePK),
        this.getColumnBy(ProdutoInventario::vencimentoPKStr),
      ).text = "PK"
    }
    if (user?.lojaProduto == 8 || user?.lojaProduto == 0) {
      headerRow.join(
        this.getColumnBy(ProdutoInventario::estoqueTM),
        this.getColumnBy(ProdutoInventario::vencimentoTMStr),
      ).text = "TM"
    }
  }

  override fun filtro(): FiltroProdutoInventario {
    return FiltroProdutoInventario(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtCodigo.value ?: "",
      validade = edtInventario.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
    )
  }

  override fun updateProdutos(produtos: List<ProdutoInventario>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoInventario> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoInventario == true
  }

  override val label: String
    get() = "Inventário"

  override fun updateComponent() {
    viewModel.updateView()
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaProdutoInventario) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.text = "Planilha"
    button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    add(button)
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produto$textTime.xlsx"
  }
}