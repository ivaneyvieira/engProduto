package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoMovViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import java.util.Locale.getDefault

class DlgReposicaoMov(val viewModel: TabReposicaoMovViewModel, val movimentacao: Movimentacao) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoMovimentacao::class.java, false)

  //Componentes de filtro
  private var edtCodPrd: IntegerField? = null
  private var edtPesquisa: TextField? = null
  private var edtCodigoBarra: TextField? = null

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = movimentacao.numero
    val loja = movimentacao.lojaSigla
    val gravado = if (movimentacao.gravado == true) "(Gravado ${movimentacao.gravadoLoginStr})" else ""

    form = SubWindowForm(
      title = "Produtos do Pedido $numero - Loja $loja $gravado",
      hasButtonClose = false,
      toolBar = {
        verticalBlock {
          horizontalBlock {
            this.isSpacing = true
            this.setWidthFull()

            button("Fechar") {
              icon = VaadinIcon.CLOSE.create()
              onClick {
                closeForm()
                form?.close()
              }
            }

            this.button("Grava Pedido") {
              this.icon = VaadinIcon.CHECK.create()
              this.addClickListener {
                viewModel.gravaPedido(movimentacao)
              }
            }

            this.button("Adiciona") {
              this.icon = VaadinIcon.PLUS.create()
              this.addClickListener {
                val dlg = DlgAdicionaMovimentacao(viewModel, movimentacao) {
                  update()
                }
                dlg.open()
              }
            }

            this.button("Remove") {
              this.icon = VaadinIcon.TRASH.create()
              this.addClickListener {
                viewModel.removePedido()
              }
            }
          }
          horizontalBlock {
            this.isSpacing = true
            this.setWidthFull()

            edtCodigoBarra = textField("Código Barras") {
              this.width = "200px"
              this.valueChangeTimeout = 500
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addValueChangeListener {
                updateGrid()
              }
            }

            edtCodPrd = integerField("Cod") {
              this.width = "5rem"
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              this.valueChangeTimeout = 500
              this.addValueChangeListener {
                updateGrid()
              }
            }

            edtPesquisa = textField("Pesquisa") {
              this.width = "200px"
              this.valueChangeTimeout = 500
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addValueChangeListener {
                updateGrid()
              }
            }
          }
        }
      },
      onClose = {
        closeForm()
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
      this.setSelectionMode(Grid.SelectionMode.MULTI)
      isMultiSort = false

      this.withEditor(
        classBean = ProdutoMovimentacao::class,
        isBuffered = false,
        openEditor = {
          this.focusEditor(ProdutoMovimentacao::movimentacao)
        },
        closeEditor = {
          viewModel.updateProduto(it.bean)
          abreProximo(it.bean)
        },
        saveEditor = {
          viewModel.updateProduto(it.bean)
          abreProximo(it.bean)
        },
      )

      columnGrid(ProdutoMovimentacao::codigo, "Código").right()
      columnGrid(ProdutoMovimentacao::barcode, "Código de Barras").right()
      columnGrid(ProdutoMovimentacao::descricao, "Descrição", width = "300px")
      columnGrid(ProdutoMovimentacao::grade, "Grade", width = "100px")
      columnGrid(ProdutoMovimentacao::codFor, "For", width = "5rem")
      columnGrid(ProdutoMovimentacao::localAbrev, "Loc App", width = "5rem")
      columnGrid(ProdutoMovimentacao::movimentacao, "Quant", width = "5rem").integerFieldEditor()
      columnGrid(ProdutoMovimentacao::estoque, "Estoque", width = "5rem")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoMovimentacao> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val produtos = Movimentacaos()
    gridDetail.setItems(produtos)
  }

  private fun Movimentacaos(): List<ProdutoMovimentacao> {
    return movimentacao.findProdutos()
  }

  fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  fun produtosSelecionado(): List<ProdutoMovimentacao> {
    return gridDetail.selectedItemsSort()
  }

  private fun findProdutos(): List<ProdutoMovimentacao> {
    val user = AppConfig.userLogin()
    val codigoBarra = edtCodigoBarra?.value?.trim()?.uppercase(getDefault()) ?: ""

    val filtro = FiltroLocalizaProduto(
      loja = movimentacao.numloja,
      codForn = 0,
      pesquisa = edtPesquisa?.value?.trim()?.uppercase(getDefault()) ?: "",
      tipo = 0,
      cl = 0,
      barcode = codigoBarra
    )

    val localizaProduto: List<LocalizaProduto> = LocalizaProduto.findAll(filtro)

    return localizaProduto.mapNotNull { linha ->
      linha.prdno ?: return@mapNotNull null

      val produto = ProdutoMovimentacao()
      produto.apply {
        this.numero = this@DlgReposicaoMov.movimentacao.numero
        this.numloja = this@DlgReposicaoMov.movimentacao.numloja
        this.barcode = linha.barcode
        this.data = this@DlgReposicaoMov.movimentacao.data
        this.hora = this@DlgReposicaoMov.movimentacao.hora
        this.login = this@DlgReposicaoMov.movimentacao.login
        this.descricao = linha.descricao ?: ""
        this.usuario = this@DlgReposicaoMov.movimentacao.usuario
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.codFor = linha.codForn
        this.gravadoLogin = user?.no
        this.gravado = this@DlgReposicaoMov.movimentacao.gravado
        this.locApp = linha.locApp
        this.estoque = linha.estoqueLoja
      }
    }
  }

  private fun updateGrid(usaFiltro: Boolean = true) {
    val findProdutos = if (usaFiltro) {
      findProdutos()
    } else {
      emptyList()
    }
    val selecionados = (gridDetail.selectedItems.toList() + Movimentacaos()).distinct()
    val produtos = findProdutos.filter {
      it !in selecionados
    }
    gridDetail.setItems(selecionados + produtos)
    selecionados.forEach {
      gridDetail.select(it)
    }
  }

  private fun gravaProdutos() {
    val selecionados = gridDetail.selectedItems.toList()
    viewModel.updateProduto(selecionados)
  }

  private fun abreProximo(bean: ProdutoMovimentacao) {
    val items = gridDetail.list()
    val index = items.indexOf(bean)
    if (index >= 0) {
      val nextIndex = index + 1
      if (nextIndex < items.size) {
        val nextBean = items[nextIndex]
        //gridDetail.select(nextBean)
        gridDetail.editor.editItem(nextBean)
      } else {
        gridDetail.deselectAll()
      }
    }
  }

  fun gravaSelecao() {
    gravaProdutos()
    updateGrid(false)
  }
}