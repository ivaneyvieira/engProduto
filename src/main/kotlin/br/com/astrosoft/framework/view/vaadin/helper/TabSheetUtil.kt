package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.viewmodel.ITabView
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.tabs.TabSheet

fun TabSheet.tabPanel(tabPanel: ITabPanel) {
  val button = Button(tabPanel.label) {
    tabPanel.updateComponent()
  }
  button.addThemeVariants(ButtonVariant.LUMO_SMALL)

  val label = Div(tabPanel.label)

  this.add(label, tabPanel.createComponent())
  label.addSingleClickListener { tabPanel.updateComponent() }
}

interface ITabPanel : ITabView {
  fun createComponent(): Component
}



