package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper.showError
import br.com.astrosoft.produto.model.beans.EMarcaRessuprimento
import br.com.astrosoft.produto.model.beans.PedidoRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoPedido
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoBarcode
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoCodigo
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDescricao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoEstoque
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoGrade
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoLocalizacao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtPedido
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtPedidoEdt
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoValidade
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabPedidoRessuprimentoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.desc
import com.github.mvysny.kaributools.getColumnBy
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.ValidationResult
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosPedidoRessuprimento(val viewModel: TabPedidoRessuprimentoViewModel, val pedido: PedidoRessuprimento) {
  private var form: SubWindowForm? = null
  private var edtPesquisa: TextField? = null
  private val gridDetail = Grid(ProdutoRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val ressuprimentoTitle = "${pedido.pedido}"

    form = SubWindowForm(
      "Produtos do ressuprimento $ressuprimentoTitle",
      toolBar = {
        val user = AppConfig.userLogin() as? UserSaci
        button("Separa") {
          this.isVisible = user?.ressuprimentoSepara == true
          this.icon = VaadinIcon.SPLIT.create()
          onClick {
            viewModel.separaPedido()
          }
        }
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            update()
          }
        }
      }, onClose = {
        onClose()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_WRAP_CELL_CONTENT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI
      this.withEditor(
        classBean = ProdutoRessuprimento::class,
        openEditor = {
          this.focusEditor(ProdutoRessuprimento::qttyEdit)
        },
        closeEditor = {
          viewModel.saveProduto(it.bean)
        }
      )

      produtoRessuprimentoCodigo()
      produtoRessuprimentoBarcode()
      produtoRessuprimentoDescricao()
      produtoRessuprimentoGrade()
      produtoRessuprimentoLocalizacao()
      produtoRessuprimentoValidade()
      produtoRessuprimentoQtPedidoEdt().integerFieldEditor()
      produtoRessuprimentoEstoque()
      this.columnGrid(ProdutoRessuprimento::selecionadoOrdemENT, "Selecionado") {
        this.isVisible = false
      }
      this.columnGrid(ProdutoRessuprimento::posicao, "Posicao") {
        this.isVisible = false
      }

      this.setPartNameGenerator {
        if (it.selecionado == EMarcaRessuprimento.ENT.num) {
          "amarelo"
        } else null
      }
      gridDetail.isMultiSort = true
      gridDetail.sort(
        gridDetail.getColumnBy(ProdutoRessuprimento::selecionadoOrdemENT).asc,
        gridDetail.getColumnBy(ProdutoRessuprimento::posicao).desc,
      )
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return gridDetail.selectedItems.toList()
  }

  fun ProdutoRessuprimento.dadosStr(): String {
    return "${this.codigo} ${this.barcodes} ${this.descricao} ${this.grade} ${this.localizacao}"
  }

  fun update() {
    val pesquisa = edtPesquisa?.value ?: ""
    val listProdutos = pedido.produtos().filter {
      it.dadosStr().contains(pesquisa, ignoreCase = true)
    }.map {
      it.qttyEdit = it.qtPedido ?: 0
      it
    }
    gridDetail.setItems(listProdutos)
  }

  fun updateProduto(produto: ProdutoRessuprimento) {
    gridDetail.dataProvider.refreshItem(produto)
    gridDetail.isMultiSort = true
    gridDetail.sort(
      gridDetail.getColumnBy(ProdutoRessuprimento::selecionadoOrdemENT).asc,
      gridDetail.getColumnBy(ProdutoRessuprimento::posicao).desc,
    )
    update()
    val index = gridDetail.list().indexOf(produto)
    gridDetail.scrollToIndex(index)
    gridDetail.select(produto)
  }
}
