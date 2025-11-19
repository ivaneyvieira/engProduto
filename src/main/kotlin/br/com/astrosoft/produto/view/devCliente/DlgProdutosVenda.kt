package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.checkBoxEditor
import br.com.astrosoft.framework.view.vaadin.helper.focusEditor
import br.com.astrosoft.framework.view.vaadin.helper.integerFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.framework.view.vaadin.helper.withEditor
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
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidadeDevolucao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFTemProduto
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
    /*when (value) {

      EProdutoTroca.Com   -> {
        gridDetail.selectionMode = Grid.SelectionMode.MULTI
        val allItem = gridDetail.list()
        gridDetail.asMultiSelect().select(allItem)
      }

      EProdutoTroca.Sem   -> {
        gridDetail.selectionMode = Grid.SelectionMode.NONE
      }

      EProdutoTroca.Misto -> {
        gridDetail.selectionMode = Grid.SelectionMode.MULTI
        gridDetail.asMultiSelect().deselectAll()
      }

      else                -> {}
    }*/
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.NONE

      this.withEditor(
        classBean = ProdutoNFS::class,
        isBuffered = false,
        openEditor = {
          this.focusEditor(ProdutoNFS::temProduto)
        },
        closeEditor = {
          viewModel.updateProduto(it.bean)
          abreProximo(it.bean)
        },
        saveEditor = {
          viewModel.updateProduto(it.bean)
          abreProximo(it.bean)
        }
      )

      produtoNFCodigo()
      produtoNFBarcode()
      produtoAutorizacaoExp()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFTemProduto().checkBoxEditor()
      produtoNFQuantidadeDevolucao().integerFieldEditor()
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

  private fun abreProximo(bean: ProdutoNFS) {
    val items = gridDetail.list()
    val index = items.indexOf(bean)
    if (index >= 0) {
      val nextIndex = index + 1
      if (nextIndex < items.size) {
        val nextBean = items[nextIndex]
        //gridDetail.select(nextBean)
        gridDetail.editor.editItem(nextBean)
      }
    }
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