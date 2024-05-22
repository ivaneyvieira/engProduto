package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoValidade
import br.com.astrosoft.produto.model.beans.ProdutoValidade
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoValidade
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoValidadeViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoValidade(val viewModel: TabProdutoValidadeViewModel) :
  TabPanelGrid<ProdutoValidade>(ProdutoValidade::class),
  ITabProdutoValidade {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: TextField
  private lateinit var edtValidade: IntegerField
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
    edtValidade = integerField("Validade") {
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
  }

  override fun Grid<ProdutoValidade>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoValidade::codigo, header = "Código")
    columnGrid(ProdutoValidade::descricao, header = "Descrição").expand()
    columnGrid(ProdutoValidade::grade, header = "Grade")
    columnGrid(ProdutoValidade::unidade, header = "Un")
    columnGrid(ProdutoValidade::validade, header = "Val")
    columnGrid(ProdutoValidade::estoqueTotal, header = "Total")
    columnGrid(ProdutoValidade::estoqueDS, header = "Est")
    columnGrid(ProdutoValidade::vencimentoDS, header = "Venc")
    columnGrid(ProdutoValidade::estoqueMR, header = "Est")
    columnGrid(ProdutoValidade::vencimentoMR, header = "Venc")
    columnGrid(ProdutoValidade::estoqueMF, header = "Est")
    columnGrid(ProdutoValidade::vencimentoMF, header = "Venc")
    columnGrid(ProdutoValidade::estoquePK, header = "Est")
    columnGrid(ProdutoValidade::vencimentoPK, header = "Venc")
    columnGrid(ProdutoValidade::estoqueTM, header = "Est")
    columnGrid(ProdutoValidade::vencimentoTM, header = "Venc")

    val headerRow = prependHeaderRow()
    headerRow.join(
      this.getColumnBy(ProdutoValidade::codigo),
      this.getColumnBy(ProdutoValidade::descricao),
      this.getColumnBy(ProdutoValidade::grade),
      this.getColumnBy(ProdutoValidade::unidade),
      this.getColumnBy(ProdutoValidade::validade),
      this.getColumnBy(ProdutoValidade::estoqueTotal),
    ).text = "Produto"
    headerRow.join(
      this.getColumnBy(ProdutoValidade::estoqueDS),
      this.getColumnBy(ProdutoValidade::vencimentoDS),
    ).text = "DS"
    headerRow.join(
      this.getColumnBy(ProdutoValidade::estoqueMR),
      this.getColumnBy(ProdutoValidade::vencimentoMR),
    ).text = "MR"
    headerRow.join(
      this.getColumnBy(ProdutoValidade::estoqueMF),
      this.getColumnBy(ProdutoValidade::vencimentoMF),
    ).text = "MF"
    headerRow.join(
      this.getColumnBy(ProdutoValidade::estoquePK),
      this.getColumnBy(ProdutoValidade::vencimentoPK),
    ).text = "PK"
    headerRow.join(
      this.getColumnBy(ProdutoValidade::estoqueTM),
      this.getColumnBy(ProdutoValidade::vencimentoTM),
    ).text = "TM"
  }

  override fun filtro(): FiltroProdutoValidade {
    return FiltroProdutoValidade(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtCodigo.value ?: "",
      validade = edtValidade.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
    )
  }

  override fun updateProdutos(produtos: List<ProdutoValidade>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoValidade> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoValidade == true
  }

  override val label: String
    get() = "Val Inv"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}