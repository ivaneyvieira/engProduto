package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import br.com.astrosoft.produto.viewmodel.recebimento.TabNotaEntradaViewModel
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosNotaEntrada(val viewModel: TabNotaEntradaViewModel, val nota: NotaRecebimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nfEntrada ?: ""
    val fornecedor = nota.fornecedor ?: ""
    val emissao = nota.emissao.format()
    val loja = nota.lojaSigla ?: ""
    val pedido = nota.pedComp?.toString() ?: ""
    val natureza = nota.natureza()
    val numeroInterno = nota.ni
    val transp = nota.transp
    val transportadora = nota.transportadora
    val cte = nota.cte
    val linha1 = "Fornecedor: $fornecedor"
    val linha2 = "NI: $numeroInterno - Nota: $numeroNota - Emissão: $emissao - Ped Compra: $loja$pedido"
//    val linha3 = "Natureza: $natureza"
    val linha3 = "Transportadora: $transp - $transportadora     CTE: $cte"

    form = SubWindowForm(
      title = "$linha1|$linha2|$linha3",
      toolBar = {
        /*
        val user = AppConfig.userLogin()
        if (user?.admin == true) {
          this.button("Volta") {
            this.icon = VaadinIcon.ARROW_LEFT.create()
            this.onClick {
              viewModel.voltar()
            }
          }
        }*/
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
      addThemeVariants(GridVariant.LUMO_COMPACT)
      removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(NotaRecebimentoProduto::codigo, "Código").right()
      columnGrid(NotaRecebimentoProduto::barcodeStrListEntrada, "Código de Barras").right()
      columnGrid(NotaRecebimentoProduto::refFabrica, "Ref Fabrica").right()
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição")
      columnGrid(NotaRecebimentoProduto::grade, "Grade", width = "80px")
      //columnGrid(NotaRecebimentoProduto::localizacao, "Loc App")
      columnGrid(NotaRecebimentoProduto::cfop, "CFOP")
      columnGrid(NotaRecebimentoProduto::cst, "CST")
      columnGrid(NotaRecebimentoProduto::un, "UN")
      columnGrid(NotaRecebimentoProduto::quant, "Quant")
      columnGrid(NotaRecebimentoProduto::valorUnit, "Valor Unit", pattern = "#,##0.0000", width = "90px")
      columnGrid(NotaRecebimentoProduto::valorTotal, "Valor Total", width = "90px")
      columnGrid(NotaRecebimentoProduto::valorDesconto, "Desc", width = "60px")
      columnGrid(NotaRecebimentoProduto::frete, "Frete", width = "60px")
      columnGrid(NotaRecebimentoProduto::outDesp, "Desp", width = "60px")
      columnGrid(NotaRecebimentoProduto::baseIcms, "Base ICMS", width = "90px")
      columnGrid(NotaRecebimentoProduto::valIcms, "V. ICMS", width = "70px")
      columnGrid(NotaRecebimentoProduto::valIPI, "F. IPI", width = "60px")
      columnGrid(NotaRecebimentoProduto::icms, "ICMS", width = "60px")
      columnGrid(NotaRecebimentoProduto::ipi, "IPI", width = "50px")
      //columnGrid(NotaRecebimentoProduto::estoque, "Estoque")
      //columnGrid(NotaRecebimentoProduto::validade, "Val", width = "100px")
      //columnGrid(NotaRecebimentoProduto::fabricacao, "Fab", width = "120px", pattern = "MM/yy")
      //columnGrid(NotaRecebimentoProduto::vencimento, "Venc", width = "120px", pattern = "MM/yy")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProduto? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }

  fun updateProduto(): NotaRecebimento? {
    val nota = nota.refreshProdutos()
    update()
    return nota
  }
}