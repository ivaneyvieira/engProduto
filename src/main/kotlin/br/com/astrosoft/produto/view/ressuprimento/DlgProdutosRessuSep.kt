package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EMarcaRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoBarcode
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoCodigo
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoCodigoCorrecao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDataNF
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDescricao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDescricaoCorrecao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoGrade
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoGradeCorrecao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoLocalizacao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoNumeroNF
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtNf
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtPedido
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtRecebido
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoSepViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosRessuSep(val viewModel: TabRessuprimentoSepViewModel, val ressuprimentos: List<Ressuprimento>) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val ressuprimentoTitle = if(ressuprimentos.size == 1) {
      val ressuprimento = ressuprimentos.first()
      "${ressuprimento.numero}     NF ${ressuprimento.notaBaixa} DE ${ressuprimento.dataBaixa.format()}"
    } else {
      ""
    }

    form = SubWindowForm("Produtos do ressuprimento $ressuprimentoTitle", toolBar = {
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.selecionaProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
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
    val user = AppConfig.userLogin() as? UserSaci
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      if (user?.ressuprimentoRecebedor == true) {
        this.withEditor(classBean = ProdutoRessuprimento::class,
          openEditor = {
            this.focusEditor(ProdutoRessuprimento::qtRecebido)
          },
          closeEditor = {
            viewModel.saveQuant(it.bean)
          }
        )
      }

      produtoRessuprimentoCodigo()
      produtoRessuprimentoBarcode()
      produtoRessuprimentoDescricao().expand()
      produtoRessuprimentoGrade()
      produtoRessuprimentoLocalizacao()
      produtoRessuprimentoQtPedido()
      if (user?.ressuprimentoRecebedor == true) {
        produtoRessuprimentoQtRecebido().integerFieldEditor()
      } else {
        produtoRessuprimentoQtRecebido()
      }
      //produtoRessuprimentoEstoque()
      if (user?.ressuprimentoRecebedor == true) {
        produtoRessuprimentoCodigoCorrecao().textFieldEditor {
          this.valueChangeMode = ValueChangeMode.ON_CHANGE
          this.addValueChangeListener { event ->
            val codigo = event.value ?: ""
            val listGrades = viewModel.findGrades(codigo)

            val colGrade = this@apply.getColumnBy(ProdutoRessuprimento::gradeCorrecao)
            val compGrade = colGrade.editorComponent as? Select<String>
            compGrade?.setItems(listGrades.map { it.grade })

            val colDescricao = this@apply.getColumnBy(ProdutoRessuprimento::descricaoCorrecao)
            val compDescricao = colDescricao.editorComponent as? TextField
            compDescricao?.value = listGrades.firstOrNull()?.descricao ?: ""
          }
        }
        produtoRessuprimentoDescricaoCorrecao().expand().textFieldEditor {
          this.isReadOnly = true
        }
        produtoRessuprimentoGradeCorrecao().comboFieldEditor<ProdutoRessuprimento, String>()
      } else {
        produtoRessuprimentoCodigoCorrecao()
        produtoRessuprimentoDescricaoCorrecao().expand()
        produtoRessuprimentoGradeCorrecao()
      }
      this.columnGrid(ProdutoRessuprimento::selecionadoOrdemREC, "Selecionado") {
        this.isVisible = false
      }
      this.columnGrid(ProdutoRessuprimento::posicao, "Posicao") {
        this.isVisible = false
      }

      this.setPartNameGenerator {
        if (it.selecionado == EMarcaRessuprimento.REC.num) {
          "amarelo"
        } else null
      }
      gridDetail.isMultiSort = true
      gridDetail.sort(
        gridDetail.getColumnBy(ProdutoRessuprimento::selecionadoOrdemREC).asc,
        gridDetail.getColumnBy(ProdutoRessuprimento::posicao).desc,
      )
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoRessuprimento> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = ressuprimentos.flatMap {
      it.produtos()
    }
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }

  fun updateProduto(produto: ProdutoRessuprimento) {
    gridDetail.dataProvider.refreshItem(produto)
    gridDetail.isMultiSort = true
    gridDetail.sort(
      gridDetail.getColumnBy(ProdutoRessuprimento::selecionadoOrdemREC).asc,
      gridDetail.getColumnBy(ProdutoRessuprimento::posicao).desc,
    )
    update()
    val index = gridDetail.list().indexOf(produto)
    gridDetail.scrollToIndex(index)
    gridDetail.select(produto)
  }
}