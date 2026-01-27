package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.devCliente.FormAutorizaNota
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoExp
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDev
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFNI
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFNIData
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantDevNI
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidadeDevolucao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFSeq
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFTemProduto
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
import br.com.astrosoft.produto.viewmodel.vendaRef.TabVendaRefViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosVenda(val viewModel: TabVendaRefViewModel, val nota: NotaVendaRef) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    val readOnly = false
    val espaco = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"
    val nomeCliente = if (nota.nomeCliente.isNullOrBlank())
      "NÃO INFORMADO"
    else
      nota.nomeCliente
    val linha1 =
        "Loja: ${nota.loja.format("00")}${espaco}NF: ${nota.nota}${espaco}Data: ${nota.data.format()}${espaco}Vendedor: ${nota.vendedor}"
    val linha2 =
        "Tipo NF: ${nota.tipoNf}${espaco}Tipo Pgto: ${nota.tipoPgto}${espaco}Cliente: ${nota.cliente} - $nomeCliente"
    form = SubWindowForm(
      title = "$linha1|$linha2",
      toolBar = {
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

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.NONE

      produtoNFQuantidadeDevolucao().integerFieldEditor()
      produtoNFNI()
      produtoNFNIData()
      produtoNFCodigo()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFBarcode()
      produtoAutorizacaoExp()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario().apply {
        this.setFooter(Html("\"<b><span style=\"font-size: medium; \">Total</span></b>\""))
      }
      produtoNFPrecoTotal()
      produtoNFSeq()
      produtoNFQuantDevNI()

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

    gridDetail.setPartNameGenerator {
      if (it.dev == true) {
        "amarelo"
      } else {
        null
      }
    }
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {

    val listProdutos = nota.produtos()
    gridDetail.setItems(listProdutos)

    val totalValor = listProdutos.sumOf { it.total ?: 0.0 }
    val totalCol = gridDetail.getColumnBy(ProdutoNFS::total)
    totalCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
  }

  fun produtos(): List<ProdutoNFS> {
    return gridDetail.list()
  }

  fun fecha() {
    form?.close()
  }
}