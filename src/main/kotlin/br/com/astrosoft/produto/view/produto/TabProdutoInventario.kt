package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoInventarioViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
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

    downloadExcel(PlanilhaProdutoInventario())
  }

  override fun Grid<ProdutoInventario>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoInventario::codigo, header = "Código")
    columnGrid(ProdutoInventario::descricao, header = "Descrição").expand()
    columnGrid(ProdutoInventario::grade, header = "Grade")
    columnGrid(ProdutoInventario::unidade, header = "Un")
    columnGrid(ProdutoInventario::fornecedorAbrev, header = "Fornecedor")
    columnGrid(ProdutoInventario::validade, header = "Val")
    columnGrid(ProdutoInventario::estoqueTotal, header = "Total")
    columnGrid(ProdutoInventario::estoqueDS, header = "Est")
    columnGrid(ProdutoInventario::vencimentoDS, header = "Venc", pattern = "mm/yy")
    columnGrid(ProdutoInventario::estoqueMR, header = "Est")
    columnGrid(ProdutoInventario::vencimentoMR, header = "Venc", pattern = "mm/yy")
    columnGrid(ProdutoInventario::estoqueMF, header = "Est")
    columnGrid(ProdutoInventario::vencimentoMF, header = "Venc", pattern = "mm/yy")
    columnGrid(ProdutoInventario::estoquePK, header = "Est")
    columnGrid(ProdutoInventario::vencimentoPK, header = "Venc", pattern = "mm/yy")
    columnGrid(ProdutoInventario::estoqueTM, header = "Est")
    columnGrid(ProdutoInventario::vencimentoTM, header = "Venc", pattern = "mm/yy")

    val headerRow = prependHeaderRow()
    headerRow.join(
      this.getColumnBy(ProdutoInventario::codigo),
      this.getColumnBy(ProdutoInventario::descricao),
      this.getColumnBy(ProdutoInventario::grade),
      this.getColumnBy(ProdutoInventario::unidade),
      this.getColumnBy(ProdutoInventario::validade),
      this.getColumnBy(ProdutoInventario::fornecedorAbrev),
      this.getColumnBy(ProdutoInventario::estoqueTotal),
    ).text = "Produto"
    headerRow.join(
      this.getColumnBy(ProdutoInventario::estoqueDS),
      this.getColumnBy(ProdutoInventario::vencimentoDS),
    ).text = "DS"
    headerRow.join(
      this.getColumnBy(ProdutoInventario::estoqueMR),
      this.getColumnBy(ProdutoInventario::vencimentoMR),
    ).text = "MR"
    headerRow.join(
      this.getColumnBy(ProdutoInventario::estoqueMF),
      this.getColumnBy(ProdutoInventario::vencimentoMF),
    ).text = "MF"
    headerRow.join(
      this.getColumnBy(ProdutoInventario::estoquePK),
      this.getColumnBy(ProdutoInventario::vencimentoPK),
    ).text = "PK"
    headerRow.join(
      this.getColumnBy(ProdutoInventario::estoqueTM),
      this.getColumnBy(ProdutoInventario::vencimentoTM),
    ).text = "TM"
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