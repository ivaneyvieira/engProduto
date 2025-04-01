package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.TabValidadeViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosValidade(val viewModel: TabValidadeViewModel, var nota: NotaRecebimento) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  private var edtCodigoBarra: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota = nota.nfEntrada ?: ""
    val fornecedor = nota.fornecedor ?: ""
    val emissao = nota.emissao.format()
    val loja = nota.lojaSigla ?: ""
    val pedido = nota.pedComp?.toString() ?: ""
    val natureza = nota.natureza()
    val transportadora = nota.transportadora
    val cte = nota.cte

    val linha1 = "Fornecedor: $fornecedor"
    val linha2 = "Ped Compra: $loja$pedido - NFO: $numeroNota - Emissão: $emissao"
    val linha3 = "Natureza: $natureza"
    val linha4 = "Transportadora: $transportadora      CTE: $cte"

    form = SubWindowForm(
      title = "$linha1 |$linha2 |$linha3 |$linha4",
      toolBar = {
        edtCodigoBarra = textField("Código de barras") {
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.selecionaProdutos(nota, it.value)
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

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(NotaRecebimentoProduto::codigo, "Código")
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição", width = "250px")
      columnGrid(NotaRecebimentoProduto::grade, "Grade", width = "80px")
      columnGrid(NotaRecebimentoProduto::localizacao, "Loc App")
      columnGrid(NotaRecebimentoProduto::quant, "Quant")
      columnGrid(NotaRecebimentoProduto::estoque, "Estoque")
      columnGrid(NotaRecebimentoProduto::validadeStr, "Val").right()
      columnGrid(NotaRecebimentoProduto::fabricacao, "Fab", width = "120px", pattern = "MM/yy")
      columnGrid(
        NotaRecebimentoProduto::vencimento,
        "Venc",
        width = "120px",
        pattern = "MM/yy"
      )

      addColumnButton(VaadinIcon.DATE_INPUT, "Validade", "Validade") { produto ->
        val form = FormValidadeQuant(produto)
        DialogHelper.showForm(caption = "Validade", form = form) {
          viewModel.salvaValidades(produto)
        }
      }

      columnGrid(NotaRecebimentoProduto::qtty01, "QTD 1")
      columnGrid(NotaRecebimentoProduto::venc01, "Vence 1", width = "80px") {
        this.setComparator(Comparator.comparingInt { produto -> produto.venc01.toMesAno() })
      }

      columnGrid(NotaRecebimentoProduto::qtty02, "QTD 2")
      columnGrid(NotaRecebimentoProduto::venc02, "Vence 2", width = "80px") {
        this.setComparator(Comparator.comparingInt { produto -> produto.venc02.toMesAno() })
      }

      columnGrid(NotaRecebimentoProduto::qtty03, "QTD 3")
      columnGrid(NotaRecebimentoProduto::venc03, "Vence 3", width = "80px") {
        this.setComparator(Comparator.comparingInt { produto -> produto.venc03.toMesAno() })
      }

      columnGrid(NotaRecebimentoProduto::qtty04, "QTD 4")
      columnGrid(NotaRecebimentoProduto::venc04, "Vence 4", width = "80px") {
        this.setComparator(Comparator.comparingInt { produto -> produto.venc04.toMesAno() })
      }
    }
    this.addAndExpand(gridDetail)
    gridDetail.setPartNameGenerator {
      when {
        it.marcaEnum == EMarcaRecebimento.RECEBIDO -> "primary"
        it.selecionado == true                     -> "amarelo"
        else                                       -> null
      }
    }
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    val user = AppConfig.userLogin() as? UserSaci
    val selecionados = gridDetail.selectedItems.toList()
    val marcados = gridDetail.dataProvider.fetchAll().filter { it.selecionado == true }
    return if (user?.admin == true)
      (selecionados + marcados).distinctBy { "${it.ni} ${it.prdno} ${it.grade}" }
    else
      selecionados
  }

  fun update() {
    val listProdutos = nota.produtos
    gridDetail.setItems(listProdutos)
  }

  fun update(nota: NotaRecebimento) {
    this.nota = nota
    update()
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

  fun close() {
    onClose?.invoke()
    form?.close()
  }

  fun focusCodigoBarra() {
    edtCodigoBarra?.value = ""
    edtCodigoBarra?.focus()
  }

  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit) {
    val form = FormValidade(tipoValidade, tempoValidade)
    DialogHelper.showForm(caption = "Validade", form = form) {
      block(form.validadeSaci)
    }
  }

  fun reloadGrid() {
    gridDetail.dataProvider.refreshAll()
  }

  fun String?.toMesAno(): Int {
    this ?: return 0
    val mes = this.substring(0, 2).toIntOrNull() ?: return 0
    val ano = this.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }
}