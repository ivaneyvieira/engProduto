package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoSimplesViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgAdicionaFornecedor(
  val viewModel: TabEstoqueAcertoSimplesViewModel,
  val acerto: EstoqueAcerto,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtVendno: IntegerField? = null
  private var edtFornecedor: TextField? = null
  private var cmbCaracter: Select<ECaracter>? = null
  private var prdGrade: Grid<ProdutoEstoqueAcerto>? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = true
      this.setSizeFull()

      horizontalLayout {
        this.isMargin = false
        this.isPadding = false
        this.isSpacing = true

        setWidthFull()

        edtVendno = integerField("Fornecedor") {
          this.width = "5rem"
          this.isAutofocus = true
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.addValueChangeListener {
            val fornecedor = viewModel.findFornecedor(it.value)
            edtFornecedor?.value = fornecedor?.nome ?: ""
            updateGrid()
          }
        }

        edtFornecedor = textField("Nome") {
          this.isReadOnly = true
          this.setWidthFull()
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

      prdGrade = grid<ProdutoEstoqueAcerto> {
        this.setSizeFull()
        this.isMultiSort = true
        this.selectionMode = Grid.SelectionMode.MULTI

        this.columnGrid(ProdutoEstoqueAcerto::prdno, "Código")
        this.columnGrid(ProdutoEstoqueAcerto::descricao, "Descrição", isExpand = true)
        this.columnGrid(ProdutoEstoqueAcerto::grade, "Grade")
        this.columnGrid(ProdutoEstoqueAcerto::estoqueSis, "Estoque Sis")
        this.columnGrid(ProdutoEstoqueAcerto::diferenca, "Diferença")

        this.setItems(findProdutos())
      }
    }
    this.width = "50%"
    this.height = "80%"
  }

  fun HasComponents.toolBar() {
    horizontalLayout {
      this.justifyContentMode = FlexComponent.JustifyContentMode.END
      button("Confirma") {
        this.setPrimary()
        onClick {
          closeForm()
        }
      }

      button("Cancelar") {
        this.addThemeVariants(ButtonVariant.LUMO_ERROR)
        onClick {
          this@DlgAdicionaFornecedor.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    return "Adiciona Produto"
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
      pesquisa = "",
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
        this.diferenca = linha.diferenca
        this.processado = false
        this.transacao = null
        this.gravadoLogin = user?.no
        this.observacao = acerto.observacaoAcerto
        this.gravado = acerto.gravado
      }
    }

  }

  private fun closeForm() {
    val produtos = prdGrade?.list().orEmpty()

    produtos.forEach {
      viewModel.addProduto(it)
    }

    onClose.invoke()
    this.close()
  }

  private fun updateGrid() {
    val produtos = findProdutos()
    prdGrade?.setItems(produtos)
  }
}

