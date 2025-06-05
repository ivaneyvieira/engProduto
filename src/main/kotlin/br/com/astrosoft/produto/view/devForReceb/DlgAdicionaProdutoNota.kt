package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaViewModel
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

class DlgAdicionaProdutoNota(
  val viewModel: ITabNotaViewModel,
  val nota: NotaRecebimentoDev,
  val onClose: () -> Unit = {}
) : Dialog() {
  private val listaRow = mutableListOf<LinhaNota>()
  private var edtNi: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      this.isSpacing = false
      this.isMargin = false
      if (nota.tipoDevolucaoEnun?.notasMultiplas == true) {
        edtNi = integerField("NI") {
          this.isAutoselect = true
          this.isAutofocus = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
        }
      }
      for (i in 4..10) {
        val linha = LinhaNota(viewModel, nota)
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
          this@DlgAdicionaProdutoNota.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    return "Adiciona Produto"
  }

  private fun closeForm() {
    val seqMax = nota.produtos.maxOfOrNull { it.seq ?: 0 } ?: 0
    listaRow.forEachIndexed { index, linha ->
      save(linha, seqMax + index + 1)
    }
    onClose.invoke()
    this.close()
  }

  private fun save(linha: LinhaNota, seq: Int) {
    val produtoNota = nota.produtos.firstOrNull() ?: return

    val prdno = linha.prdno() ?: return
    val grade = linha.grade()
    val saldo = linha.saldo()
    val invno = if (nota.tipoDevolucaoEnun?.notasMultiplas == true) {
      edtNi?.value ?: 0
    } else {
      nota.niPrincipal
    }
    if (saldo == null || saldo <= 0) return

    val produto = produtoNota.copy(
      seq = seq,
      ni = invno,
      prdno = prdno,
      grade = grade ?: "",
      quantDevolucao = saldo
    )

    viewModel.addProduto(produto)
  }
}

class LinhaNota(val viewModel: ITabNotaViewModel, val nota: NotaRecebimentoDev) : HorizontalLayout() {
  private var edtCodigo: TextField? = null
  private var edtDescricao: TextField? = null
  private var edtGrade: Select<String>? = null
  private var edtQuant: IntegerField? = null

  private val produtos = mutableListOf<PrdGrade>()

  fun prdno() = produtos.firstOrNull()?.prdno
  fun grade() = edtGrade?.value
  fun saldo() = edtQuant?.value

  init {
    this.setWidthFull()
    edtCodigo = textField("Código") {
      this.width = "180px"
      this.isClearButtonVisible = true
      this.isAutoselect = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 2000
      this.addValueChangeListener {
        val lista = viewModel.findProdutos(this.value)
        produtos.clear()
        produtos.addAll(lista)
        if (produtos.isEmpty()) {
          edtDescricao?.value = ""
          edtGrade?.isEnabled = false
          edtGrade?.value = null
          edtQuant?.value = null

          if (this.value != "" || this.value != null) {
            //Notification.show("Produto não encontrado", 3000, Notification.Position.MIDDLE).apply {
            //  this.addThemeVariants(NotificationVariant.LUMO_ERROR)
            //}
            edtCodigo?.focus()
            //edtCodigo?.selectAll()
          }
        } else if (produtos.size == 1) {
          edtDescricao?.value = produtos.firstOrNull()?.descricao ?: ""
          edtGrade?.isEnabled = false
          edtQuant?.value = 0
          edtGrade?.setItems(produtos.map { it.grade })
          edtGrade?.value = produtos.firstOrNull()?.grade
          edtQuant?.focus()
        } else {
          edtDescricao?.value = produtos.firstOrNull()?.descricao ?: ""
          edtGrade?.isEnabled = true
          edtGrade?.setItems(produtos.map { it.grade })
          edtGrade?.value = produtos.firstOrNull()?.grade
          edtQuant?.value = 0
          edtGrade?.focus()
        }
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
      this.isAutoselect = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
    }
  }
}