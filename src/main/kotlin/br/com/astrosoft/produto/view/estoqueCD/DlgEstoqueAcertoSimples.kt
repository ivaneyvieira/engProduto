package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.EInativo
import br.com.astrosoft.produto.model.beans.EUso
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoSimplesViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgEstoqueAcertoSimples(val viewModel: TabEstoqueAcertoSimplesViewModel, val acerto: EstoqueAcerto) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoEstoqueAcerto::class.java, false)

  //Componentes de filtro
  private var edtVendno: IntegerField? = null
  private var edtPesquisa: TextField? = null
  private var cmbCaracter: Select<ECaracter>? = null

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = acerto.numero
    val loja = acerto.lojaSigla
    val gravado = if (acerto.gravado == true) "(Gravado ${acerto.gravadoLoginStr})" else ""

    form = SubWindowForm(
      "Produtos do Acerto $numero - Loja $loja $gravado",
      toolBar = {
        verticalBlock {
          horizontalBlock {
            this.isSpacing = true
            this.setWidthFull()

            this.button("Pedido") {
              this.icon = VaadinIcon.PRINT.create()
              this.addClickListener {
                viewModel.imprimirPedido(acerto)
              }
            }

            this.button("Acerto") {
              this.icon = VaadinIcon.PRINT.create()
              this.addClickListener {
                viewModel.imprimirAcerto(acerto)
              }
            }

            this.button("Grava Acerto") {
              this.icon = VaadinIcon.CHECK.create()
              this.addClickListener {
                viewModel.gravaAcerto(acerto)
                closeForm()
              }
            }

            this.button("Relatório") {
              this.icon = VaadinIcon.FILE_TEXT.create()
              this.addClickListener {
                viewModel.imprimirRelatorio(acerto)
              }
            }

            this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "acertoEstoque") {
              val produtos = estoqueAcertos()
              viewModel.geraPlanilha(produtos)
            }

            this.button("Ad For") {
              this.icon = VaadinIcon.PLUS.create()
              this.addClickListener {
                if (acerto.processado == true) {
                  DialogHelper.showWarning("Acerto já processado")
                  return@addClickListener
                }
                val dlg = DlgAdicionaFornecedor(viewModel, acerto) {
                  update()
                }
                dlg.open()
              }
            }

            this.button("Adiciona") {
              this.icon = VaadinIcon.PLUS.create()
              this.addClickListener {
                if (acerto.processado == true) {
                  DialogHelper.showWarning("Acerto já processado")
                  return@addClickListener
                }
                val dlg = DlgAdicionaAcertoSimples(viewModel, acerto) {
                  update()
                }
                dlg.open()
              }
            }

            this.button("Remove") {
              this.icon = VaadinIcon.TRASH.create()
              this.addClickListener {
                viewModel.removeAcerto()
              }
            }
          }
          horizontalBlock {
            this.isSpacing = true
            this.setWidthFull()

            edtVendno = integerField("Fornecedor") {
              this.width = "5rem"
              this.isAutofocus = true
              this.valueChangeMode = ValueChangeMode.LAZY
              this.valueChangeTimeout = 500
              this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
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

            cmbCaracter = select("Caracter") {
              this.width = "90px"
              this.setItems(ECaracter.entries)
              this.setItemLabelGenerator { item ->
                item.descricao
              }
              this.value = ECaracter.NAO

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

      columnGrid(ProdutoEstoqueAcerto::codigo, "Código").right()
      columnGrid(ProdutoEstoqueAcerto::descricao, "Descrição", width = "300px")
      columnGrid(ProdutoEstoqueAcerto::grade, "Grade", width = "120px")
      addColumnButton(VaadinIcon.DATE_INPUT, "Conferência", "Conf") { produto ->
        val user = AppConfig.userLogin()
        when {
          acerto.login != user?.login -> {
            DialogHelper.showWarning("Usuário não é o responsável pelo acerto")
          }

          acerto.processado == true   -> {
            DialogHelper.showWarning("Acerto já processado")
          }

          else                        -> {
            val dlgConferencia = DlgConferenciaAcertoSimples(viewModel, produto) {
              gridDetail.dataProvider.refreshAll()
            }
            dlgConferencia.open()
          }
        }
      }
      columnGrid(ProdutoEstoqueAcerto::estoqueSis, "Est Sist")
      //columnGrid(ProdutoEstoqueAcerto::estoqueCD, "Est CD")
      //columnGrid(ProdutoEstoqueAcerto::estoqueLoja, "Est Loja")
      columnGrid(ProdutoEstoqueAcerto::diferencaAcerto, "Diferença")
      columnGrid(ProdutoEstoqueAcerto::estoqueReal, "Est Real")

      this.addSelectionListener {
        updateProdutos()
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoEstoqueAcerto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val produtos = estoqueAcertos()
    gridDetail.setItems(produtos)
  }

  private fun estoqueAcertos(): List<ProdutoEstoqueAcerto> {
    return acerto.findProdutos(true)
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  fun produtosSelecionado(): List<ProdutoEstoqueAcerto> {
    return gridDetail.selectedItemsSort()
  }

  private fun findProdutos(): List<ProdutoEstoqueAcerto> {
    val user = AppConfig.userLogin()
    val caracter = cmbCaracter?.value ?: ECaracter.NAO
    val fornecedor = edtVendno?.value ?: 0
    if (fornecedor == 0) {
      return emptyList()
    }
    val filtro = FiltroProdutoEstoque(
      loja = acerto.numloja,
      pesquisa = edtPesquisa?.value ?: "",
      codigo = 0,
      grade = "",
      caracter = caracter,
      localizacao = "",
      fornecedor = fornecedor.toString(),
      inativo = EInativo.TODOS,
      uso = EUso.TODOS,
      listaUser = listOf("TODOS"),
    )
    val produtosFornecedor: List<ProdutoEstoque> = ProdutoEstoque.findProdutoEstoque(filtro).filter {
      it.codForn == fornecedor
    }
    return produtosFornecedor.mapNotNull { linha ->
      linha.prdno ?: return@mapNotNull null

      val produto = ProdutoEstoqueAcerto()
      produto.apply {
        this.numero = acerto.numero
        this.numloja = acerto.numloja
        this.acertoSimples = true
        this.data = acerto.data
        this.hora = acerto.hora
        this.login = acerto.login
        this.descricao = linha.descricao ?: ""
        this.usuario = acerto.usuario
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.estoqueSis = linha.saldo
        this.diferenca = null
        this.processado = false
        this.transacao = null
        this.gravadoLogin = user?.no
        this.observacao = acerto.observacao
        this.gravado = acerto.gravado
      }
    }
  }

  private fun updateGrid() {
    val selecionados = (gridDetail.selectedItems.toList() + estoqueAcertos()).distinct()
    val produtos = findProdutos().filter {
      it !in selecionados
    }
    gridDetail.setItems(selecionados + produtos)
    selecionados.forEach {
      gridDetail.select(it)
    }
    updateProdutos()
  }

  private fun updateProdutos() {
    val selecionados = gridDetail.selectedItems.toList()
    viewModel.updateProduto(selecionados)
  }
}