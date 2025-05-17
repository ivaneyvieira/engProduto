package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoExp
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFEstoque
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.nfd.columns.ProdutoNFNFSViewColumns.produtoNFUsuarioSep
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.h5
import com.github.mvysny.karibudsl.v10.onClick
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.theme.lumo.LumoUtility

class DlgProdutosNFDAberta(val viewModel: TabNotaNFDAbertaViewModel, val nota: NotaSaidaDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)
  val lblCancel = if (nota.cancelada == "S") " (Cancelada)" else ""
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da expedicao ${nota.nota} loja: ${nota.loja}${lblCancel}", toolBar = {
      button("Imprimir") {
        this.icon = VaadinIcon.PRINT.create()
        this.isVisible = nota.cancelada == "N"
        onClick {
          val itensSelecionados = gridDetail.selectedItems.toList()
          viewModel.imprimeProdutosNota(nota, itensSelecionados)
        }
      }
    }, onClose = {
      onClose()
    }) {
      VerticalLayout().apply {
        val grid = HorizontalLayout().apply {
          setSizeFull()
          createGridProdutos()
        }
        val obs = HorizontalLayout().apply {
          this.setWidthFull()
          this.addClassNames(LumoUtility.BorderRadius.MEDIUM, LumoUtility.Border.ALL)
          this.isMargin = false
          this.isPadding = true

          h5(nota.observacaoPrint ?: "") {
            this.setSizeFull()
          }
        }
        addAndExpand(grid)
        add(obs)
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      addItemDoubleClickListener { e ->
        editor.editItem(e.item)
        val editorComponent: Component = e.column.editorComponent
        if (editorComponent is Focusable<*>) {
          (editorComponent as Focusable<*>).focus()
        }
      }

      produtoNFCodigo()
      produtoNFBarcode()
      produtoAutorizacaoExp()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFGradeAlternativa()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()
      produtoNFUsuarioSep()
      produtoNFEstoque()

      this.setPartNameGenerator {
        val marca = it.marca
        val marcaImpressao = it.marcaImpressao ?: 0
        when {
          marcaImpressao > 0         -> "azul"
          marca == EMarcaNota.CD.num -> "amarelo"
          else                       -> null
        }
      }
    }
    this.addAndExpand(gridDetail)

    update()
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(todosLocais = true)
    gridDetail.setItems(listProdutos)
  }
}