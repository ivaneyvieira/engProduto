package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.Movimentacao
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoMovViewModel
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
  val viewModel: TabReposicaoMovViewModel,
  val acerto: Movimentacao,
  val onClose: () -> Unit = {}
) : Dialog() {
  private val produtoLinha: List<LinhaProduto> = buildList {
    repeat(10) {
      add(LinhaProduto(viewModel, acerto))
    }
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
        this.numero = acerto.numero
        this.numloja = acerto.numloja
        this.data = acerto.data
        this.hora = acerto.hora
        this.login = acerto.login
        this.usuario = acerto.usuario
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.gravadoLogin = user?.no
        this.gravado = acerto.gravado
        this.movimentacao = linha.movimentacao ?: 0
      }
    }

    produtos.forEach {
      viewModel.addProduto(it)
    }

    onClose.invoke()
    this.close()
  }
}

class LinhaProduto(val viewModel: TabReposicaoMovViewModel, val acerto: Movimentacao) : HorizontalLayout() {
  private val produtos = mutableListOf<PrdGrade>()
  private var edtCodigo: TextField
  private var edtDescricao: TextField
  private var edtGrade: Select<String>
  private var edtMovimentacao: IntegerField

  init {
    this.isPadding = false
    this.isMargin = false
    this.isSpacing = true
    this.setWidthFull()

    edtCodigo = textField("Código") {
      this.width = "120px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      this.addValueChangeListener {
        val lista = viewModel.findProdutos(this.value, acerto.numloja)
        produtos.clear()
        produtos.addAll(lista)

        edtGrade.setItems(produtos.map { it.grade })
        edtGrade.value = produtos.firstOrNull()?.grade
        edtDescricao.value = produtos.firstOrNull()?.descricao
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

    edtDescricao = textField("Descrição") {
      this.setWidthFull()
      this.isReadOnly = true
      this.tabIndex = -1
    }

    edtGrade = select("Grade") {
      this.width = "120px"
    }

    edtMovimentacao = integerField("Movimentação") {
      this.width = "100px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
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

  fun focus() {
    edtCodigo.focus()
  }
}