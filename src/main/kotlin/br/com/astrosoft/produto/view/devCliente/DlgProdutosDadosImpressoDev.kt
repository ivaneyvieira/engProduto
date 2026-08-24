package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.produto.model.beans.DadosDev
import br.com.astrosoft.produto.model.beans.DadosDevProduto
import br.com.astrosoft.produto.model.beans.EProdutoTroca
import br.com.astrosoft.produto.model.beans.ESolicitacaoTroca
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevDadosImpressoViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosDadosImpressoDev(val viewModel: TabDevDadosImpressoViewModel, val nota: DadosDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(DadosDevProduto::class.java, false)

  private var edtPesquisa: TextField? = null
  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null

  fun showDialog(onClose: () -> Unit) {
    val readOnly = false
    val espaco = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"
    val linha1 =
        "Loja: ${nota.loja.format("00")}${espaco}NF: ${nota.nfDevolucao}${espaco}Data: ${nota.dataDevolucao.format()}${espaco}Vendedor: ${nota.vendedor}"
    val linha2 = "Tipo NF: ${nota.tipoNf}${espaco}Tipo Pgto: ${nota.tipoPgto}${espaco}Cliente: ${nota.custnoVend} - ${nota.nomeVend}"
    form = SubWindowForm(
      title = "$linha1|$linha2",
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            update()
          }
        }
        edtTipo = select("Tipo do Crédito") {
          this.isReadOnly = readOnly
          val tipos = ESolicitacaoTroca.entries
          this.setItems(tipos)
          this.value = nota.tipoDevEnum
          this.isReadOnly = true
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        edtProduto = select("Tipo da Devolução") {
          this.isReadOnly = readOnly
          val produtoTrocas = EProdutoTroca.entries
          this.setItems(produtoTrocas)
          this.value = nota.produtoTrocaEnum
          this.isReadOnly = true
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        if (nota.nfTipo == 4 /*"ENTRE FUT"*/) {
          edtNotaEntRet = integerField("NF Ent Fut") {
            this.isReadOnly = readOnly
            this.width = "6rem"
            this.isAutoselect = true
            val nfNumero = nota.notaEntrega?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 0
            this.value = nfNumero
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.valueChangeMode = ValueChangeMode.LAZY

            viewModel.salvaNfEntRet(nota, nfNumero)

            addValueChangeListener {
              if (it.isFromClient) {
                viewModel.salvaNfEntRet(nota, this.value)
              }
            }
          }
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

  private fun updateNota() {
    edtTipo?.value = nota.tipoDevEnum
    edtProduto?.value = nota.produtoTrocaEnum
    edtNotaEntRet?.value = nota.nfEntRet
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.SINGLE

      columnGrid(DadosDevProduto::codigo, header = "Código")
      columnGrid(DadosDevProduto::descricao, header = "Descrição")
      columnGrid(DadosDevProduto::grade, header = "Grade")
      columnGrid(DadosDevProduto::quantidadeSem, header = "Sem Produto") {
        this.setPartNameGenerator {
          "negrito"
        }
      }
      columnGrid(DadosDevProduto::quantidadeCom, header = "Com Produto") {
        this.setPartNameGenerator {
          "negrito"
        }
      }
      columnGrid(DadosDevProduto::quantidadeDev, header = "Quant")
      columnGrid(DadosDevProduto::valorUnitario, header = "Preço") {
        this.setFooter(Html("\"<b><span style=\"font-size: medium; \">Total</span></b>\""))
      }
      columnGrid(DadosDevProduto::valorTotal, header = "Total")
    }
    this.addAndExpand(gridDetail)

    update()

    gridDetail.setPartNameGenerator {
      if (it.produtoTrocaItemEnum != null) {
        "amarelo"
      } else {
        null
      }
    }
  }

  fun itensSelecionados(): List<DadosDevProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {

    val pesquisa = edtPesquisa?.value.orEmpty()
    val produtos = nota.produtos
    produtos.forEach { prd ->
      if (nota.produtoTrocaEnum == EProdutoTroca.Com) {
        if (prd.quantidadeTotal == 0) {
          prd.quantidadeCom = prd.quantidadeDev
          prd.quantidadeSem = null
        }
      } else if (nota.produtoTrocaEnum == EProdutoTroca.Sem) {
        if (prd.quantidadeTotal == 0) {
          prd.quantidadeSem = prd.quantidadeDev
          prd.quantidadeCom = null
        }
      } else if (nota.produtoTrocaEnum == EProdutoTroca.Misto) {
        if (prd.quantidadeTotal == 0) {
          prd.quantidadeSem = null
          prd.quantidadeCom = null
        }
      }
    }

    val listProdutos = produtos.filter { prd ->
      pesquisa == "" || (prd.codigo ?: "") == pesquisa ||
      (prd.descricao ?: "").contains(pesquisa, ignoreCase = true) ||
      (prd.ni == pesquisa.toIntOrNull())
    }
    gridDetail.setItems(listProdutos)
    updateNota()

    val totalValor = listProdutos.sumOf { it.valorTotal }
    val totalCol = gridDetail.getColumnBy(DadosDevProduto::valorTotal)
    totalCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
  }

  fun produtos(): List<DadosDevProduto> {
    return gridDetail.list()
  }

  fun fecha() {
    form?.close()
  }
}