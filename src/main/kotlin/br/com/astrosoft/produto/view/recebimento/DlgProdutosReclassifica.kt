package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.TabReclassificaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class DlgProdutosReclassifica(val viewModel: TabReclassificaViewModel, val nota: NotaRecebimento) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  private var edtCodigoBarra: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota = nota.nfEntrada ?: ""

    form = SubWindowForm("Produtos da nota $numeroNota", toolBar = {
      edtCodigoBarra = textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.selecionaProdutos(nota, it.value)
          }
        }
      }

      button("Envia") {
        onClick {
          viewModel.enviaProdutoSelecionado()
        }
      }

      button("Cadastra Validade") {
        onClick {
          viewModel.cadastraValidade()
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
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      this.withEditor(NotaRecebimentoProduto::class,
        openEditor = {
          this.focusEditor(NotaRecebimentoProduto::vencimento)
        },
        closeEditor = {
          viewModel.salvaNotaProduto(it.bean)
        })

      columnGrid(NotaRecebimentoProduto::codigo, "Código")
      columnGrid(NotaRecebimentoProduto::barcodeStrList, "Código de Barras")
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição")
      columnGrid(NotaRecebimentoProduto::grade, "Grade")
      columnGrid(NotaRecebimentoProduto::localizacao, "Loc App")
      columnGrid(NotaRecebimentoProduto::usuarioRecebe, "Recebe")
      columnGrid(NotaRecebimentoProduto::quant, "Quant")
      columnGrid(NotaRecebimentoProduto::estoque, "Estoque")
      columnGrid(NotaRecebimentoProduto::tempoValidade, "Tempo")
      columnGrid(NotaRecebimentoProduto::tipoValidade, "Tipo")
      columnGrid(NotaRecebimentoProduto::validadeStr, "Val").right()
      columnGrid(NotaRecebimentoProduto::fabricacao, "Fab", width = "120px", pattern = "MM/yy")
      columnGrid(
        NotaRecebimentoProduto::vencimento,
        "Venc",
        width = "120px",
        pattern = "MM/yy"
      ).comboFieldEditor<NotaRecebimentoProduto, LocalDate?> {
        val datas = MesAno.valuesFuture().map { mesAno ->
          mesAno.lastDay
        }
        it.setItems(datas)
        it.isEmptySelectionAllowed = true
        it.setItemLabelGenerator { data ->
          data.format("MM/yy")
        }
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    val user = AppConfig.userLogin() as? UserSaci
    val selecionados = gridDetail.selectedItems.toList()
    val marcados = gridDetail.dataProvider.fetchAll().filter { it.selecionado }
    return if (user?.admin == true)
      (selecionados + marcados).distinctBy { "${it.ni} ${it.prdno} ${it.grade}" }
    else
      selecionados
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
}