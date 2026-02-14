package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
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

class DlgReposicaoMov(val viewModel: TabReposicaoMovViewModel, val acerto: Movimentacao) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoMovimentacao::class.java, false)

  //Componentes de filtro
  private var edtCodFor: IntegerField? = null
  private var edtCodPrd: IntegerField? = null
  private var edtPesquisa: TextField? = null
  private var edtCodigoBarra: TextField? = null
  private var cmbCaracter: Select<ECaracter>? = null
  private var cmbEstoque: Select<EEstoque>? = null
  private var edtSaldo: IntegerField? = null
  private var edtSaldo2: IntegerField? = null
  private var edtTipo: IntegerField? = null
  private var edtCL: IntegerField? = null

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = acerto.numero
    val loja = acerto.lojaSigla
    val gravado = if (acerto.gravado == true) "(Gravado ${acerto.gravadoLoginStr})" else ""

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
                viewModel.gravaPedido(acerto)
              }
            }

            this.button("Adiciona") {
              this.icon = VaadinIcon.PLUS.create()
              this.addClickListener {
                val dlg = DlgAdicionaMovimentacao(viewModel, acerto) {
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

            edtPesquisa = textField("Pesquisa") {
              this.width = "200px"
              this.valueChangeTimeout = 500
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addValueChangeListener {
                updateGrid()
              }
            }

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

            edtCodFor = integerField("For") {
              this.width = "5rem"
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              this.valueChangeTimeout = 500
              this.addValueChangeListener {
                updateGrid()
              }
            }

            edtTipo = integerField("Tipo") {
              this.width = "80px"
              this.valueChangeMode = ValueChangeMode.LAZY
              this.valueChangeTimeout = 500
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              this.addValueChangeListener {
                updateGrid()
              }
            }

            edtCL = integerField("CL") {
              this.width = "80px"
              this.valueChangeMode = ValueChangeMode.LAZY
              this.valueChangeTimeout = 500
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              this.addValueChangeListener {
                updateGrid()
              }
            }

            cmbCaracter = select("Caracter") {
              this.isVisible = false
              this.width = "90px"
              this.setItems(ECaracter.entries)
              this.setItemLabelGenerator { item ->
                item.descricao
              }
              this.value = ECaracter.TODOS

              this.addValueChangeListener {
                updateGrid()
              }
            }

            cmbEstoque = select("Estoque") {
              this.isVisible = false
              this.width = "80px"
              this.setItems(EEstoque.entries)
              this.setItemLabelGenerator { item ->
                item.descricao
              }
              this.value = EEstoque.TODOS
              addValueChangeListener {
                val value = it.value
                edtSaldo2?.isVisible = value == EEstoque.ENTRE
                edtSaldo?.isVisible = value != EEstoque.TODOS
                edtSaldo?.label = if (value == EEstoque.ENTRE) "Saldo Ini" else "Saldo"
                updateGrid()
              }
            }

            edtSaldo = integerField("Saldo") {
              this.width = "80px"
              this.isVisible = false
              this.valueChangeMode = ValueChangeMode.LAZY
              this.valueChangeTimeout = 1500
              this.value = 0
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              addValueChangeListener {
                updateGrid()
              }
            }

            edtSaldo2 = integerField("Saldo Fin") {
              this.width = "80px"
              this.isVisible = false
              this.valueChangeMode = ValueChangeMode.LAZY
              this.valueChangeTimeout = 1500
              this.value = 0
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              addValueChangeListener {
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
    return acerto.findProdutos()
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
    val caracter = cmbCaracter?.value ?: ECaracter.NAO
    val codPrd = edtCodPrd?.value ?: 0
    val codFor = edtCodFor?.value ?: 0
    val tipo = edtTipo?.value ?: 0
    val cl = edtCL?.value ?: 0
    val estoque = cmbEstoque?.value ?: EEstoque.TODOS
    val saldo = edtSaldo?.value ?: 0
    val saldo2 = edtSaldo2?.value ?: 0
    val codigoBarra = edtCodigoBarra?.value?.trim()?.uppercase(getDefault()) ?: ""

    val filtro = FiltroProdutoEstoque(
      loja = acerto.numloja,
      pesquisa = "",
      codigo = codPrd,
      grade = "",
      caracter = caracter,
      localizacao = "",
      fornecedor = "",
      inativo = EInativo.TODOS,
      uso = EUso.TODOS,
      listaUser = listOf("TODOS"),
    )

    val produtosFornecedor: List<ProdutoEstoque> = ProdutoEstoque.findProdutoEstoque(filtro).filter {
      codFor == 0 ||
      it.codForn == codFor
    }.filter {
      val pesquisa = edtPesquisa?.value?.trim()?.uppercase(getDefault()) ?: ""
      val pesquisa2 = ""
      val pesquisa3 = if (pesquisa2.isBlank()) {
        "${pesquisa}ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"
      } else {
        "${pesquisa2}ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"
      }
      val descricao = it.descricao?.trim() ?: ""
      pesquisa.isBlank() || (
          descricao in pesquisa..pesquisa3
                            )
    }.filter {
      val saldoSaci = it.saldo ?: 0
      estoque == EEstoque.TODOS ||
      when (estoque) {
        EEstoque.IGUAL -> saldoSaci == saldo
        EEstoque.MAIOR -> saldoSaci > saldo
        EEstoque.MENOR -> saldoSaci < saldo
        EEstoque.ENTRE -> (saldoSaci > saldo) && (saldoSaci < saldo2)
        else           -> false
      }
    }.filter {
      it.tipo == tipo ||
      tipo == 0
    }.filter {
      it.cl == cl ||
      cl == 0
    }.filter {
      val barcode = it.barcode?.trim()?.uppercase(getDefault()) ?: ""
      barcode == codigoBarra
      || codigoBarra.isEmpty()
    }
    return produtosFornecedor.mapNotNull { linha ->
      linha.prdno ?: return@mapNotNull null

      val produto = ProdutoMovimentacao()
      produto.apply {
        this.numero = acerto.numero
        this.numloja = acerto.numloja
        this.barcode = linha.barcode
        this.data = acerto.data
        this.hora = acerto.hora
        this.login = acerto.login
        this.descricao = linha.descricao ?: ""
        this.usuario = acerto.usuario
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.codFor = linha.codForn
        this.gravadoLogin = user?.no
        this.gravado = acerto.gravado
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