package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.ProdutoEmbalagem
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.viewmodel.estoqueCD.IModelConferencia
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgConferenciaSaldo(
  val viewModel: IModelConferencia,
  val produto: ProdutoEstoque,
  val onClose: () -> Unit = {}
) :
  Dialog() {
  private var edtConferencia: IntegerField? = null
  private var edtEmbalagem: TextField? = null
  private var edtDataInicial: DatePicker? = null
  //private var edtDataConf: DatePicker? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        this.setWidthFull()
        edtDataInicial = datePicker("Início Kardex CD") {
          this.width = "6rem"
          this.value = produto.dataInicial
          this.isClearButtonVisible = true
          this.isClearButtonVisible = true
          this.localePtBr()
        }
        //edtDataConf = datePicker("Data Conf") {
        //  this.setWidthFull()
        //  this.value = LocalDate.now()
        //  this.localePtBr()
        //}
        edtConferencia = integerField("Est CD") {
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          value = produto.qtConferencia ?: 0
          this.valueChangeMode = ValueChangeMode.LAZY
          this.addValueChangeListener {
            edtEmbalagem?.value = processaEmbalagem(it.value ?: 0).format()
          }
        }

        edtEmbalagem = textField("Est Emb") {
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.setWidthFull()
          this.isReadOnly = true
          this.value = processaEmbalagem(edtConferencia?.value ?: 0).format()
        }
      }
    }
    this.width = "30%"
    this.height = "30%"
  }

  private fun processaEmbalagem(saldo: Int): Double? {
    val prdno = produto.prdno ?: ""
    return ProdutoEmbalagem.findEmbalagem(prdno)?.let { embalagem ->
      val fator = embalagem.qtdEmbalagem ?: 1.0
      val saldoEmb = saldo * 1.00 / fator
      saldoEmb
    }
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
          this@DlgConferenciaSaldo.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }

    val localizacao = produto.locApp
    //val dataConferencia = produto.dataConferencia.format()
    val saldo = produto.saldo ?: 0

    return "$codigo $descricao$grade ($localizacao) Estoque: $saldo"
  }

  private fun closeForm() {
    produto.dataInicial = edtDataInicial?.value
    //produto.dataConferencia = edtDataConf?.value
    produto.qtConferencia = edtConferencia?.value
    produto.dataUpdate = null
    viewModel.updateProduto(produto, false)
    onClose.invoke()
    this.close()
  }
}