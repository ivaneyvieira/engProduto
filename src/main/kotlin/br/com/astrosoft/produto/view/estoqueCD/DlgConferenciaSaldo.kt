package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.helper.superDoubleField
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
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.miki.superfields.numbers.SuperDoubleField
import kotlin.math.roundToInt

class DlgConferenciaSaldo(val viewModel: IModelConferencia, val produto: ProdutoEstoque, val onClose: () -> Unit = {}) :
    Dialog() {
  private var edtConferencia: IntegerField? = null
  private var edtEmbalagem: SuperDoubleField? = null
  private var edtDataInicial: DatePicker? = null
  
  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()
    
    verticalLayout {
      setSizeFull()
      horizontalLayout {
        this.setWidthFull()
        edtDataInicial = datePicker("Início Kardex") {
          this.width = "8.5rem"
          this.value = produto.dataInicial
          this.isClearButtonVisible = true
          this.isClearButtonVisible = true
          this.localePtBr()
        }
        
        edtConferencia = integerField("Est CD") {
          this.isVisible = viewModel.itensSelecionados().isEmpty()
          this.isAutoselect = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          value = produto.qtConferencia ?: 0
          this.valueChangeMode = ValueChangeMode.LAZY
          this.addValueChangeListener {
            if (it.isFromClient) {
              edtEmbalagem?.value = processaEmbalagem(it.value ?: 0)
            }
          }
        }
        
        edtEmbalagem = superDoubleField("Est Emb") {
          this.isVisible = viewModel.itensSelecionados().isEmpty()
          this.isAutoselect = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.value = processaEmbalagem(edtConferencia?.value ?: 0)
          this.valueChangeMode = ValueChangeMode.LAZY
          this.addValueChangeListener {
            if (it.isFromClient) {
              edtConferencia?.value = processaConferencia(it.value ?: 0.00)
            }
          }
        }
      }
    }
    this.width = "30%"
    this.height = "30%"
  }
  
  private fun processaEmbalagem(saldo: Int): Double {
    val prdno = produto.prdno ?: ""
    return ProdutoEmbalagem.findEmbalagem(prdno)?.let { embalagem ->
      val fator = embalagem.qtdEmbalagem ?: 1.0
      val saldoEmb = saldo * 1.00 / fator
      saldoEmb
    } ?: (saldo * 1.0)
  }
  
  private fun processaConferencia(emb: Double): Int? {
    val prdno = produto.prdno ?: ""
    return ProdutoEmbalagem.findEmbalagem(prdno)?.let { embalagem ->
      val fator = embalagem.qtdEmbalagem ?: 1.0
      val saldoEmb = emb * fator
      saldoEmb.roundToInt()
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
    
    val localizacao = produto.locApp //val dataConferencia = produto.dataConferencia.format()
    val saldo = produto.saldo ?: 0
    
    return "$codigo $descricao$grade ($localizacao) Estoque: $saldo"
  }
  
  private fun updateProduto(produtoUpd: ProdutoEstoque) {
    produtoUpd.dataInicial = edtDataInicial?.value
    produtoUpd.qtConferencia = edtConferencia?.value
    produtoUpd.dataUpdate = null
    viewModel.updateConferencia(produto)
  }
  
  private fun updateProdutoLista(listaProduto: List<ProdutoEstoque>) {
    listaProduto.forEach { produtoUpd: ProdutoEstoque ->
      produtoUpd.dataInicial = edtDataInicial?.value
      viewModel.updateConferencia(produto)
    }
  }
  
  private fun closeForm() {
    val listaProduto = viewModel.itensSelecionados()
    if (listaProduto.isEmpty()) {
      updateProduto(produtoUpd = produto)
    } else {
      updateProdutoLista(listaProduto)
    }
    onClose.invoke()
    this.close()
  }
}