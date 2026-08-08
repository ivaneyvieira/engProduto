package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Movimentacao
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntrada
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoRepViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgAdicionaNotaEntrada(
  val viewModel: TabReposicaoRepViewModel,
  val pedido: Movimentacao,
  val onClose: (DlgAdicionaNotaEntrada) -> Unit = {}
) : Dialog() {
  private lateinit var gridProdutos: Grid<ProdutoNotaEntrada>
  private lateinit var edtNota: TextField
  private lateinit var edtPedido: IntegerField

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = true

      setSizeFull()
      this.filtroBar()
      this.gridProdutos()
    }
    this.width = "50%"
    this.height = "80%"
  }

  private fun VerticalLayout.filtroBar() {
    horizontalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = true

      edtNota = textField("Nota Fiscal") {
        this.isAutofocus = true
        this.valueChangeMode = ValueChangeMode.LAZY
        this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        this.addValueChangeListener {
          updateProdutos()
        }
      }

      edtPedido = integerField("Pedido") {
        this.valueChangeMode = ValueChangeMode.LAZY
        this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        this.addValueChangeListener {
          updateProdutos()
        }
      }
    }
  }

  private fun updateProdutos() {
    val user = AppConfig.userLogin() as? UserSaci ?: return
    val lojaUser = user.lojaUsuario
    val loja = pedido.numloja
    val numeroNF = edtNota.value ?: ""
    val numeroPedido = edtPedido.value ?: 0
    val lista: List<ProdutoNotaEntrada> = if (loja > 0) {
      val nfno = numeroNF.split("/").getOrNull(0)?.trim() ?: ""
      val nfse = numeroNF.split("/").getOrNull(1)?.trim() ?: ""
      viewModel.movimentacaoFindByNf(
        loja = loja,
        nfno = nfno,
        nfse = nfse,
        lojaUser = lojaUser,
        pedido = numeroPedido
      )
    } else {
      emptyList()
    }
    gridProdutos.setItems(lista)
    gridProdutos.recalculateColumnWidths()
  }

  private fun VerticalLayout.gridProdutos() {
    gridProdutos = grid {
      this.isExpand = true
      this.setWidthFull()

      this.columnGrid(property = ProdutoNotaEntrada::codigo, header = "Código")
      this.columnGrid(property = ProdutoNotaEntrada::barcode, header = "Código de Barras")
      this.columnGrid(property = ProdutoNotaEntrada::grade, header = "Grade")
      this.columnGrid(property = ProdutoNotaEntrada::movimentacao, header = "Quant")
      this.columnGrid(property = ProdutoNotaEntrada::descricao, header = "Descrição", isExpand = true)
    }
    updateProdutos()
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
          this@DlgAdicionaNotaEntrada.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    return "Adiciona Produto"
  }

  val user = AppConfig.userLogin()

  private fun List<ProdutoNotaEntrada>.toProdutoMovimentacao(): List<ProdutoMovimentacao> {
    return this.map { prd ->
      val produto = ProdutoMovimentacao()
      produto.apply {
        this.numero = pedido.numero
        this.numloja = pedido.numloja
        this.data = pedido.data
        this.hora = pedido.hora
        this.noLogin = user?.no
        this.login = pedido.login
        this.usuario = pedido.usuario
        this.noRota = pedido.noRota

        this.prdno = prd.prdno
        this.grade = prd.grade
        this.barcode = prd.barcode
        this.noGravado = pedido.noGravado
        this.gravadoLogin = pedido.gravadoLogin

        this.noEntregue = pedido.noEntregue
        this.entregue = pedido.entregue
        this.entregueNome = pedido.entregueNome

        this.noRecebido = pedido.noRecebido
        this.recebido = pedido.recebido
        this.recebidoNome = pedido.recebidoNome

        this.movimentacao = prd.movimentacao ?: 0
      }
    }
  }

  private fun closeForm() {
    val produtos: List<ProdutoMovimentacao> = gridProdutos.dataProvider.fetchAll().toProdutoMovimentacao()

    viewModel.updateProduto(pedido = pedido, produtos)

    onClose.invoke(this)
    this.close()
  }
}
