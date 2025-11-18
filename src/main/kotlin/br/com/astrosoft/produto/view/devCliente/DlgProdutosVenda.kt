package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoExp
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DlgProdutosVenda(val viewModel: TabDevAutorizaViewModel, val nota: NotaVenda) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)

  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null

  fun showDialog(onClose: () -> Unit) {
    val readOnly = (nota.userSolicitacao ?: 0) != 0
    form = SubWindowForm(
      "Produtos da expedicao ${nota.nota} loja: ${nota.loja}",
      toolBar = {
        edtTipo = select("Tipo") {
          this.isReadOnly = readOnly
          this.setItems(ESolicitacaoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "300px"
          this.value = nota.solicitacaoTrocaEnnum ?: ESolicitacaoTroca.Troca
        }

        edtProduto = select("Produto") {
          this.isReadOnly = readOnly
          this.setItems(EProdutoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "300px"
          this.value = nota.produtoTrocaEnnum ?: EProdutoTroca.Com

          addValueChangeListener {
            updateSelectionProdutos(it.value)
          }
        }

        button("Processar") {
          this.icon = VaadinIcon.COG.create()
        }
      },
      onClose = {
        onClose()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun updateSelectionProdutos(value: EProdutoTroca?) {
    when (value) {

      EProdutoTroca.Com   -> {
        gridDetail.selectionMode = Grid.SelectionMode.MULTI
        val allItens = gridDetail.dataProvider.fetchAll()
        gridDetail.asMultiSelect().select(allItens)
      }

      EProdutoTroca.Sem   -> {
        gridDetail.selectionMode = Grid.SelectionMode.NONE
      }

      EProdutoTroca.Misto -> {
        gridDetail.selectionMode = Grid.SelectionMode.MULTI
        gridDetail.asMultiSelect().deselectAll()
      }

      else                -> {}
    }
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
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()

      this.setPartNameGenerator {
        val marca = it.marca
        val marcaImpressao = it.marcaImpressao ?: 0
        when {
          marcaImpressao > 0          -> "azul"
          marca == EMarcaNota.CD.num  -> "amarelo"
          marca == EMarcaNota.ENT.num -> "amarelo"
          else                        -> null
        }
      }
    }
    this.addAndExpand(gridDetail)

    update()
    updateSelectionProdutos(edtProduto?.value)
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos()
    gridDetail.setItems(listProdutos)
  }
}