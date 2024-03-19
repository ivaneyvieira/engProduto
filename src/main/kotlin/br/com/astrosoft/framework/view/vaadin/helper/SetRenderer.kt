package br.com.astrosoft.framework.view.vaadin.helper

import com.vaadin.flow.data.renderer.BasicRenderer
import com.vaadin.flow.function.ValueProvider

class SetRenderer<SOURCE>(valueProvider: ValueProvider<SOURCE, Set<Any>>) :
  BasicRenderer<SOURCE, Set<Any>>(valueProvider) {
  override fun getFormattedValue(value: Set<Any>?): String {
    return value?.map { it.toString() }.orEmpty().toList().sorted().joinToString(", ")
  }
}