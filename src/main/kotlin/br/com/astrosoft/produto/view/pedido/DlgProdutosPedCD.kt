package br.com.astrosoft.produto.view.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.framework.view.comboFieldEditor
import br.com.astrosoft.framework.view.withEditor
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoBarcode
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoCodigo
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoDescricao
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoEstoque
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoGrade
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoGradeAlternativa
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoLocalizacao
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoQuantidade
import br.com.astrosoft.produto.viewmodel.pedido.TabPedidoCDViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosPedCD(val viewModel: TabPedidoCDViewModel, val pedido: PedidoVenda) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoPedidoVenda::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da Pedido ${pedido.ordno} loja: ${pedido.loja}", toolBar = {
      button("Entregue") {
        val user = Config.user as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_RIGHT.create()
        onLeftClick {
          viewModel.marcaEnt()
        }
      }
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaEntProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
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
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      withEditor(ProdutoPedidoVenda::class, openEditor = {
        (getColumnBy(ProdutoPedidoVenda::gradeAlternativa).editorComponent as? Focusable<*>)?.focus()
        it.bean?.clno?.startsWith("01") == true
      }, closeEditor = { binder ->
        this.dataProvider.refreshItem(binder.bean)
      })

      addItemDoubleClickListener { e ->
        editor.editItem(e.item)
        val editorComponent: Component = e.column.editorComponent
        if (editorComponent is Focusable<*>) {
          (editorComponent as Focusable<*>).focus()
        }
      }

      produtoPedidoCodigo()
      produtoPedidoBarcode()
      produtoPedidoDescricao()
      produtoPedidoGrade()
      produtoPedidoGradeAlternativa().comboFieldEditor { combo: Select<String> ->
        combo.setItems("")
        editor.addOpenListener { e ->
          if (e.source.isOpen) {
            val produto = e.item
            val list = mutableListOf<PrdGrade>()

            viewModel.findGrade(produto) { prds ->
              list.addAll(prds)
            }
            combo.style.set("--vaadin-combo-box-overlay-width", "300px")
            combo.setItems(list.map { it.grade })
            combo.setItemLabelGenerator { grade ->
              val saldo = list.firstOrNull { it.grade == grade }?.saldo ?: 0
              if (grade == null) ""
              else "$grade Saldo: $saldo"
            }
          }
        }
      }
      produtoPedidoLocalizacao()
      produtoPedidoQuantidade()
      produtoPedidoEstoque()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoPedidoVenda> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos(EMarcaPedido.CD)
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoVenda? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }
}