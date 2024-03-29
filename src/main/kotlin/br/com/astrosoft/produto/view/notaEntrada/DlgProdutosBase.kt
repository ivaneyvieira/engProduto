package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEBarcode
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFECodigo
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEDescricao
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEGrade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEMesesGarantia
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidadePacote
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEReferencia
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEUnidade
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaBaseViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosBase(val viewModel: TabNotaEntradaBaseViewModel, val nota: NotaEntrada) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFE::class.java, false)
  private val userSaci
    get() = AppConfig.userLogin() as? UserSaci

  fun showDialog(onClose: () -> Unit) {
    val status = if (userSaci?.receberProcessar == true) "Pronto para processar" else ""
    val txtStatus = if (status == "") "" else "($status)"
    form = SubWindowForm("Produtos da Nota de Entrada ${nota.nota} loja ${nota.loja} $txtStatus", toolBar = {
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
      setSelectionMode(Grid.SelectionMode.SINGLE)

      produtoNFECodigo()
      produtoNFEReferencia()
      produtoNFEBarcode()
      produtoNFEDescricao()
      produtoNFEGrade()
      produtoNFEUnidade()
      produtoNFEQuantidade()
      produtoNFEMesesGarantia()
      produtoNFEQuantidadePacote()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = viewModel.produtos()
    gridDetail.setItems(listProdutos)
  }
}