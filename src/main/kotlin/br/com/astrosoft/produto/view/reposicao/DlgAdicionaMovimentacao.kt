package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.Movimentacao
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoRepViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgAdicionaMovimentacao(
  val viewModel: TabReposicaoRepViewModel,
  val pedido: Movimentacao,
  val onClose: (DlgAdicionaMovimentacao) -> Unit = {}
) : Dialog() {
  private val produtosAdd = mutableListOf<ProdutoMovimentacao>()
  private val produtoLinha: List<LinhaProduto> = buildList {
    repeat(10) { num ->
      val linha = LinhaProduto(viewModel = viewModel, acerto = pedido, index = num){linha ->
        selecionaLinha(linha)
      }
      add(linha)
    }
  }

  fun selecionaLinha(linha: Int){
    val linhaProduto = produtoLinha.getOrNull(linha)
    linhaProduto?.focus()
  }

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false

      setSizeFull()
      produtoLinha.forEach {
        this.add(it)
      }
      produtoLinha.firstOrNull()?.focus()
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
          this@DlgAdicionaMovimentacao.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    return "Adiciona Produto"
  }

  private fun closeForm() {
    val user = AppConfig.userLogin()
    val produtos = produtoLinha.mapNotNull { linha ->
      linha.prdno ?: return@mapNotNull null

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

        this.prdno = linha.prdno
        this.grade = linha.grade
        this.barcode = linha.codigoBarras
        this.noGravado = 0
        this.gravadoLogin = pedido.gravadoLogin

        this.noEntregue = pedido.noEntregue
        this.entregue = pedido.entregue
        this.entregueNome = pedido.entregueNome

        this.noRecebido = pedido.noRecebido
        this.recebido = pedido.recebido
        this.recebidoNome = pedido.recebidoNome

        this.movimentacao = linha.movimentacao ?: 0
      }
    }

    produtosAdd.clear()
    produtosAdd.addAll(produtos)
    viewModel.updateProduto(pedido = pedido, produtos)

    onClose.invoke(this)
    this.close()
  }

  fun produtos(): List<ProdutoMovimentacao> {
    return produtosAdd.toList()
  }
}

class LinhaProduto(
  val viewModel: TabReposicaoRepViewModel,
  val acerto: Movimentacao,
  val index: Int,
  val selecionaLinha: (linha: Int) -> Unit
) :
  HorizontalLayout() {
  private val produtos = mutableListOf<PrdGrade>()
  private var edtCodigo: TextField
  private var edtCodigoBarras: TextField
  private var edtDescricao: TextField
  private var edtGrade: Select<String>
  private var edtMovimentacao: IntegerField

  init {
    this.isPadding = false
    this.isMargin = false
    this.isSpacing = true
    this.setWidthFull()

    edtCodigo = textField("Código") {
      if (index > 0) this.label = ""
      this.width = "120px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000

      this.addValueChangeListener { e ->
        if (e.isFromClient) {
          val lista = viewModel.findProdutos(codigo = this.value, acerto.numloja)
          produtos.clear()
          produtos.addAll(lista)

          edtGrade.setItems(produtos.map { it.grade })
          edtGrade.value = produtos.firstOrNull()?.grade
          edtDescricao.value = produtos.firstOrNull()?.descricao
          edtCodigoBarras.value = produtos.firstOrNull()?.codigoBarras
          edtGrade.isEnabled = produtos.size > 1

          if (produtos.isNotEmpty()) {
            if (produtos.size > 1) {
              edtGrade.focus()
            } else {
              edtMovimentacao.focus()
            }
          }
        }
      }
    }

    edtGrade = select("Grade") {
      if (index > 0) this.label = ""
      this.width = "120px"

      this.addValueChangeListener { event ->
        val grade = event.value
        edtCodigoBarras.value = produtos.firstOrNull { it.grade == grade }?.codigoBarras ?: ""
      }
    }

    edtMovimentacao = integerField("Quant") {
      if (index > 0) this.label = ""
      this.width = "100px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000

      this.addValueChangeListener {
        if(it.isFromClient){
         selecionaLinha(index + 1)
        }
      }
    }

    edtCodigoBarras = textField("Código de Barras") {
      if (index > 0) this.label = ""
      this.width = "8rem"
      this.isReadOnly = false
      this.tabIndex = -1
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000

      this.addValueChangeListener { e ->
        if (e.isFromClient) {
          val lista = viewModel.findProdutos(codigo = this.value, loja = acerto.numloja)
          produtos.clear()
          produtos.addAll(lista)

          edtGrade.setItems(produtos.map { it.grade })
          edtGrade.value = produtos.firstOrNull()?.grade
          edtDescricao.value = produtos.firstOrNull()?.descricao
          edtCodigo.value = produtos.firstOrNull()?.codigo
          edtGrade.isEnabled = produtos.size > 1

          if (produtos.isNotEmpty()) {
            if (produtos.size > 1) {
              edtGrade.focus()
            } else {
              edtMovimentacao.focus()
            }
          }
        }
      }
    }

    edtDescricao = textField("Descrição") {
      if (index > 0) this.label = ""
      this.setWidthFull()
      this.isReadOnly = true
      this.tabIndex = -1
    }
  }

  val prdno: String?
    get() = produtos.firstOrNull()?.prdno
  val grade: String?
    get() = edtGrade.value
  val saldo: Int?
    get() = produtos.firstOrNull {
      it.grade == edtGrade.value
    }?.saldo
  val movimentacao: Int?
    get() = edtMovimentacao.value
  val codigoBarras: String
    get() = edtCodigoBarras.value ?: ""

  fun focus() {
    edtCodigo.focus()
  }
}