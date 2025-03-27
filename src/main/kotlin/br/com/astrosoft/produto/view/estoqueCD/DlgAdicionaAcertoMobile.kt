package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoMobileViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgAdicionaAcertoMobile(
  val viewModel: TabEstoqueAcertoMobileViewModel,
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
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false
      this.setWidthFull()
      horizontalLayout {
        this.isMargin = false
        this.isPadding = false
        this.isWrap = true
        this.setWidthFull()
        edtCodigo = textField("Código") {
          this.isAutofocus = true
          this.isAutoselect = true
          this.width = "6em"
          this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
          this.addClassName("mobile")
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

        button {
          icon = VaadinIcon.BARCODE.create()
          addClickListener {
            val dlg = DlgBarcodeScanner()
            dlg.open()
          }
        }

        edtDescricao = textField("Descrição") {
          this.isAutoselect = true
          this.width = null
          this.minWidth = "10em"
          this.isExpand = true
          this.addClassName("mobile")
          this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
          this.isReadOnly = true
        }

        edtGrade = select("Grade") {
          this.addClassName("mobile")
          this.width = "6em"
        }
      }
      horizontalLayout {
        this.isMargin = false
        this.isPadding = false

        edtEstoqueCD = integerField("Estoque CD") {
          this.isAutoselect = true
          this.addClassName("mobile")
          this.width = "8em"
          this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }

        edtEstoqueLoja = integerField("Estoque Loja") {
          this.isAutoselect = true
          this.addClassName("mobile")
          this.width = "8em"
          this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
      }
    }
    this.width = "100%"
    //this.height = "35%"
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
          this@DlgAdicionaAcertoMobile.close()
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
      this.estoqueSis = produtos.firstOrNull()?.saldo
      this.estoqueCD = edtEstoqueCD?.value
      this.estoqueLoja = edtEstoqueLoja?.value
      this.processado = false
      this.transacao = null
      this.gravadoLogin = user?.no
      this.gravado = acerto.gravado
    }

    viewModel.addProduto(produto)
    onClose.invoke()
    this.close()
  }
}