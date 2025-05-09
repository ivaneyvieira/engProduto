package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.produto.model.beans.PedidoGarantia
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoPedidoGarantia
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabPedidoGarantiaViewModel
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

class DlgAdicionaGarantia(
  val viewModel: TabPedidoGarantiaViewModel,
  val garantia: PedidoGarantia,
  val onClose: () -> Unit = {}
) : Dialog() {

  private val listaRow = mutableListOf<LinhaGarantia>()

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      this.isSpacing = false
      this.isMargin = false
      for (i in 4..10) {
        val linha = LinhaGarantia(viewModel, garantia)
        listaRow.add(linha)
        add(linha)
      }
    }
    this.width = "60%"
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
          this@DlgAdicionaGarantia.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    return "Adiciona Produto"
  }

  private fun closeForm() {
    listaRow.forEach {
      it.save()
    }
    onClose.invoke()
    this.close()
  }
}

class LinhaGarantia(val viewModel: TabPedidoGarantiaViewModel, val garantia: PedidoGarantia) : HorizontalLayout() {
  private var edtCodigo: TextField? = null
  private var edtDescricao: TextField? = null
  private var edtGrade: Select<String>? = null
  private var edtQuant: IntegerField? = null

  private val produtos = mutableListOf<PrdGrade>()

  init {
    this.setWidthFull()
    edtCodigo = textField("Código") {
      this.width = "180px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.addValueChangeListener {
        val lista = viewModel.findProdutos(this.value, garantia.numloja)
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

    edtQuant = integerField("Quant") {
      this.width = "120px"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
    }
  }

  fun save() {
    val produto = ProdutoPedidoGarantia()
    val prdno = produtos.firstOrNull()?.prdno ?: return
    val grade = edtGrade?.value
    val saldo = edtQuant?.value

    produto.apply {
      this.numero = garantia.numero
      this.numloja = garantia.numloja
      this.data = garantia.data
      this.hora = garantia.hora
      this.usuario = garantia.usuario
      this.prdno = prdno
      this.grade = grade
      this.estoqueDev = saldo
      this.observacao = garantia.observacao
    }

    viewModel.addProduto(produto)
  }
}