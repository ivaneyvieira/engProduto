package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.AjusteProduto
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormAjustaProduto(val nota: EntradaDevCli) : FormLayout() {
  private val linhas: MutableList<Linha> = mutableListOf()

  init {
    this.width = "650px"
    verticalLayout {
      this.width = this@FormAjustaProduto.width
      this.isSpacing = false
      this.isMargin = false
      nota.produtos().forEach { produto ->
        val linha = Linha(produto)
        this.add(linha)
        linhas.add(linha)
      }
    }
  }

  fun listAjustes(): List<AjusteProduto> {
    return linhas.map { linha ->
      AjusteProduto(
        produto = linha.produto,
        temProduto = linha.temProduto(),
        quant = linha.quant(),
      )
    }
  }
}

private class Linha(val produto: EntradaDevCliPro) : HorizontalLayout() {
  private var check: Checkbox? = null
  private var quant: IntegerField? = null

  init {
    this.isMargin = false
    this.width = "600px"

    content {
      align(horizontalAlignment = right, verticalAlignment = bottom)
    }

    textField("Código") {
      this.isReadOnly = true
      this.tabIndex = 0
      this.width = "5rem"
      this.value = produto.codigo
    }

    textField("Descrição") {
      this.isReadOnly = true
      this.tabIndex = 0
      this.isExpand = true
      this.value = produto.descricao
    }

    textField("Grade") {
      this.isReadOnly = true
      this.tabIndex = 0
      this.width = "8rem"
      this.value = produto.grade
    }

    check = checkBox("Tem Produto") {
      this.style.set("prdno", produto.prdno)
      this.style.set("grade", produto.grade)
      this.width = "8rem"
      this.value = produto.tipoPrd?.contains(" P") ?: false

      addValueChangeListener {
        val value = it.value ?: false
        quant?.isEnabled = value
        if(value){
          quant?.focus()
        }
      }
    }

    quant = integerField("Quant") {
      this.value = produto.tipoQtd ?: 0
      this.isClearButtonVisible = true
      this.isAutoselect = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.width = "6rem"
      this.isEnabled = produto.tipoPrd?.contains(" P") ?: false
      this.value = produto.tipoQtdEfetiva
    }
  }

  fun temProduto() = check?.value ?: false

  fun quant(): Int = quant?.value ?: 0
}