package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.viewmodel.ITabView
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.tabs.TabSheet

fun TabSheet.tabPanel(tabPanel: ITabPanel) {
  val button = Button(tabPanel.label) {
    tabPanel.updateComponent()
  }
  button.addThemeVariants(ButtonVariant.LUMO_SMALL)

  this.add(button, tabPanel.createComponent())
}

interface ITabPanel : ITabView {
  fun createComponent(): Component
}



