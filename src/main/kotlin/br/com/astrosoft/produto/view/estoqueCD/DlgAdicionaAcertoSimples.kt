package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoSimplesViewModel
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

class DlgAdicionaAcertoSimples(
  val viewModel: TabEstoqueAcertoSimplesViewModel,
  val acerto: EstoqueAcerto,
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
          this@DlgAdicionaAcertoSimples.close()
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

      val produto = ProdutoEstoqueAcerto()
      produto.apply {
        this.numero = acerto.numero
        this.numloja = acerto.numloja
        this.acertoSimples = true
        this.data = acerto.data
        this.hora = acerto.hora
        this.login = acerto.login
        this.usuario = acerto.usuario
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.estoqueSis = linha.saldo
        this.diferenca = linha.diferenca
        this.processado = false
        this.transacao = null
        this.gravadoLogin = user?.no
        this.observacao = acerto.observacao
        this.gravado = acerto.gravado
      }
    }

    produtos.forEach {
      viewModel.addProduto(it)
    }

    onClose.invoke()
    this.close()
  }
}

class LinhaProduto(val viewModel: TabEstoqueAcertoSimplesViewModel, val acerto: EstoqueAcerto) : HorizontalLayout() {
  private val produtos = mutableListOf<PrdGrade>()
  private var edtCodigo: TextField
  private var edtDescricao: TextField
  private var edtGrade: Select<String>
  private var edtDiferenca: IntegerField

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
            edtDiferenca.focus()
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

    edtDiferenca = integerField("Diferença") {
      this.width = "100px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
    }
  }

  val prdno: String?
    get() = produtos.firstOrNull()?.prdno
  val grade: String?
    get() = edtGrade.value
  val diferenca: Int?
    get() = edtDiferenca.value
  val saldo: Int?
    get() = produtos.firstOrNull {
      it.grade == edtGrade.value
    }?.saldo

  fun focus() {
    edtCodigo.focus()
  }
}