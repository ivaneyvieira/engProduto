package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFUsuarioSep
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaEntViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosEnt(val viewModel: TabNotaEntViewModel, val nota: NotaSaida) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)
  val lblCancel = if (nota.cancelada == "S") " (Cancelada)" else ""
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da notaSaida ${nota.nota} loja: ${nota.loja}$lblCancel", toolBar = {
      button("Volta") {
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarEnt == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
          viewModel.marcaCD()
          gridDetail.setItems(nota.produtos(EMarcaNota.ENT))
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
      produtoNFCodigo()
      produtoNFBarcode()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFGradeAlternativa()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()
      produtoNFUsuarioSep()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(EMarcaNota.ENT)
    gridDetail.setItems(listProdutos)
  }
}