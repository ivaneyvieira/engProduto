package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoRepViewModel
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

class DlgReposicaoRep(val viewModel: TabReposicaoRepViewModel, val movimentacao: Movimentacao) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoMovimentacao::class.java, false)

  //Componentes de filtro
  private var edtCodPrd: IntegerField? = null
  private var edtPesquisa: TextField? = null
  private var edtCodigoBarra: TextField? = null
  private var cmbRota: Select<ERota>? = null

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = movimentacao.numero
    val loja = movimentacao.lojaSigla
    val gravado = if (movimentacao.noGravado > 0) "(Gravado ${movimentacao.gravadoLogin})" else ""

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
              this.isVisible = true
              this.icon = VaadinIcon.PLUS.create()
              this.onClick {
                if (movimentacao.noEntregue > 0) {
                  DialogHelper.showError("O pedido já está assinado a Entrega")
                  return@onClick
                }
                val dlg = DlgAdicionaMovimentacao(viewModel, movimentacao) {
                  update()
                }
                dlg.open()
              }
            }

            val user = AppConfig.userLogin() as? UserSaci

            this.button("Assina Entrega") {
              this.isVisible = user?.reposicaoAssinaEntrega == true
              this.icon = VaadinIcon.SIGN_IN.create()
              this.onClick {
                viewModel.assinaEntrega(movimentacao)
              }
            }

            this.button("Assina Recebimento") {
              this.isVisible = user?.reposicaoAssinaRecebimento == true
              this.icon = VaadinIcon.SIGN_OUT.create()
              this.onClick {
                viewModel.assinaRecebimento(movimentacao)
              }
            }

            this.button("Remove") {
              this.isVisible = true
              this.icon = VaadinIcon.TRASH.create()
              this.onClick {
                viewModel.removePedido(movimentacao)
              }
            }

            this.button("Desfaz Ass") {
              this.isVisible = user?.reposicaoDesfazAssina == true
              this.icon = VaadinIcon.UNLINK.create()
              this.onClick {
                viewModel.desfazAssinatura(movimentacao)
              }
            }
          }
          horizontalBlock {
            this.isSpacing = true
            this.setWidthFull()

            edtCodPrd = integerField("Cod") {
              this.width = "5rem"
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              this.valueChangeTimeout = 500
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

            edtPesquisa = textField("Pesquisa") {
              this.width = "200px"
              this.valueChangeTimeout = 500
              this.valueChangeMode = ValueChangeMode.LAZY
              this.addValueChangeListener {
                updateGrid()
              }
            }

            cmbRota = select("Rota") {
              this.setItems(ERota.entries)
              this.setItemLabelGenerator {
                it.descricao
              }
              this.value = movimentacao.enumRota
              addValueChangeListener {
                if (it.isFromClient) {
                  val rota = it.value
                  movimentacao.enumRota = rota
                  gridDetail.list().forEach { produto ->
                    produto.noRota = rota.numero
                  }
                  viewModel.gravaRota(movimentacao)
                }
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
          viewModel.updateProduto(produtos = listOf(it.bean))
          abreProximo(it.bean)
        },
        saveEditor = {
          viewModel.updateProduto(produtos = listOf(it.bean))
          abreProximo(it.bean)
        },
        canEdit = { _ ->
          movimentacao.noEntregue == 0
        }
      )

      columnGrid(ProdutoMovimentacao::codigo, "Código").right()
      columnGrid(ProdutoMovimentacao::barcode, "Código de Barras").right()
      columnGrid(ProdutoMovimentacao::descricao, "Descrição", width = "300px")
      columnGrid(ProdutoMovimentacao::grade, "Grade", width = "100px")
      columnGrid(ProdutoMovimentacao::codFor, "For", width = "5rem")
      columnGrid(ProdutoMovimentacao::localAbrev, "Loc App", width = "5rem")
      columnGrid(ProdutoMovimentacao::movimentacao, "Quant", width = "5rem").integerFieldEditor()
      columnGrid(ProdutoMovimentacao::estoque, "Estoque", width = "5rem")

      this.setPartNameGenerator { produto ->
        val entregue = produto.noEntregue ?: 0
        val recebido = produto.noRecebido ?: 0
        if (entregue > 0 && recebido > 0) {
          "amarelo"
        } else {
          null
        }
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoMovimentacao> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val produtos = produtosMovimentacoes()
    gridDetail.setItems(produtos)
  }

  private fun produtosMovimentacoes(): List<ProdutoMovimentacao> {
    val produtos = movimentacao.findProdutos()
    val produtosAgrupados = produtos.agrupa().firstOrNull()
    if (produtosAgrupados != null) {
      movimentacao.noEntregue = produtosAgrupados.noEntregue
      movimentacao.entregue = produtosAgrupados.entregue
      movimentacao.entregueNome = produtosAgrupados.entregueNome

      movimentacao.noGravado = produtosAgrupados.noGravado
      movimentacao.gravadoLogin = produtosAgrupados.gravadoLogin

      movimentacao.noRecebido = produtosAgrupados.noRecebido
      movimentacao.recebido = produtosAgrupados.recebido
      movimentacao.recebidoNome = produtosAgrupados.recebidoNome
      movimentacao.noRota = produtosAgrupados.noRota
    }
    cmbRota?.isReadOnly = movimentacao.noEntregue > 0 || movimentacao.noRecebido > 0
    return produtos
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
      codPrd = edtCodPrd?.value?.toString() ?: "",
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
        this.numero = this@DlgReposicaoRep.movimentacao.numero
        this.numloja = this@DlgReposicaoRep.movimentacao.numloja
        this.barcode = linha.barcode
        this.data = this@DlgReposicaoRep.movimentacao.data
        this.hora = this@DlgReposicaoRep.movimentacao.hora
        this.login = this@DlgReposicaoRep.movimentacao.login
        this.descricao = linha.descricao ?: ""
        this.usuario = this@DlgReposicaoRep.movimentacao.usuario
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.codFor = linha.codForn
        this.gravadoLogin = user?.login
        this.locApp = linha.locApp
        this.estoque = linha.estoqueLoja
        this.noRota = cmbRota?.value?.numero
      }
    }
  }

  private fun updateGrid(usaFiltro: Boolean = true) {
    val findProdutos = if (usaFiltro) {
      findProdutos()
    } else {
      emptyList()
    }
    val selecionados = (gridDetail.selectedItems.toList() + produtosMovimentacoes()).distinct()
    val produtos = findProdutos.filter {
      it !in selecionados
    }
    gridDetail.setItems(selecionados + produtos)
    selecionados.forEach {
      gridDetail.select(it)
    }
    if (produtos.size == 1) {
      produtos.forEach {
        gridDetail.select(it)
      }
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

  fun produtosNaoSelecionado(): List<ProdutoMovimentacao> {
    return gridDetail.list() - produtosSelecionado().toSet()
  }
}