package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgAdicionaAcertoSimples(
  val viewModel: TabEstoqueAcertoViewModel,
  val acerto: EstoqueAcerto,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtCodigo: TextField? = null
  private var edtDescricao: TextField? = null
  private var edtGrade: Select<String>? = null
  private var edtEstoqueCD: IntegerField? = null
  private var edtEstoqueLoja: IntegerField? = null

  private val produtos = mutableListOf<PrdGrade>()

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        this.setWidthFull()
        edtCodigo = textField("Código") {
          this.width = "120px"
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.valueChangeMode = ValueChangeMode.LAZY
          this.addValueChangeListener {
            val lista = viewModel.findProdutos(this.value, acerto.numloja)
            produtos.clear()
            produtos.addAll(lista)

            edtGrade?.setItems(produtos.map { it.grade })
            edtGrade?.value = produtos.firstOrNull()?.grade
            edtDescricao?.value = produtos.firstOrNull()?.descricao
            edtGrade?.isEnabled = produtos.size > 1
          }
        }

        edtDescricao = textField("Descrição") {
          this.setWidthFull()
          this.isReadOnly = true
        }

        edtGrade = select("Grade") {
          this.width = "120px"
        }
      }
      horizontalLayout {
        this.isExpand = true
        edtEstoqueCD = integerField("Estoque CD") {
          this.setWidthFull()
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }

        edtEstoqueLoja = integerField("Estoque Loja") {
          this.setWidthFull()
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
      }
    }
    this.width = "40%"
    this.height = "35%"
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
    val produto = ProdutoEstoqueAcerto()
    produto.apply {
      this.numero = acerto.numero
      this.numloja = acerto.numloja
      this.data = acerto.data
      this.hora = acerto.hora
      this.login = acerto.login
      this.usuario = acerto.usuario
      this.prdno = produtos.firstOrNull()?.prdno
      this.grade = edtGrade?.value
      this.estoqueSis = produtos.firstOrNull {
        it.grade == edtGrade?.value
      }?.saldo
      this.estoqueCD = edtEstoqueCD?.value
      this.estoqueLoja = edtEstoqueLoja?.value
      this.processado = false
      this.transacao = null
      this.gravadoLogin = user?.no
      this.observacao = acerto.observacao
      this.gravado = acerto.gravado
    }

    viewModel.addProduto(produto)
    onClose.invoke()
    this.close()
  }
}