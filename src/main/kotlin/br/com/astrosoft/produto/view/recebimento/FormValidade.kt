package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.produto.model.beans.TipoValidade
import br.com.astrosoft.produto.model.beans.ValidadeSaci
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField

class FormValidade(private val tipoValidade: Int, private val tempoValidade: Int) : FormLayout() {
  private var edtTipoValidade: Select<TipoValidade>? = null
  private var edtTempoValidade: IntegerField? = null

  init {
    edtTipoValidade = select<TipoValidade>("Tipo de validade").apply {
      setItems(TipoValidade.entries)
      setItemLabelGenerator { it.descricao }
      value = TipoValidade.entries.firstOrNull { it.tipo == tipoValidade }
      setWidthFull()
    }

    edtTempoValidade = integerField("Tempo de validade") {
      isAutoselect = true
      isClearButtonVisible = true
      setWidthFull()
      isRequired = true
      value = tempoValidade
    }
  }

  val validadeSaci: ValidadeSaci
    get() {
      val tipoValidade = edtTipoValidade?.value?.tipo
      val tempoValidade = edtTempoValidade?.value
      return ValidadeSaci().also {
        it.prdno = ""
        it.tipoValidade = tipoValidade
        it.tempoValidade = tempoValidade
      }
    }
}
