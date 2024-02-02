package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.acertoEstoque.*
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabAcertoMovManualSaida(val viewModel: TabAcertoMovManualSaidaViewModel) :
  TabPanelGrid<MovManual>(MovManual::class),
  ITabAcertoMovManualSaida {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.storeno != 0
    cmbLoja.value = viewModel.findLoja(user?.storeno ?: 0) ?: Loja.lojaZero
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<MovManual>.gridPanel() {
    this.addClassName("styling")
    columnGrid(MovManual::loja, header = "Loja")
    columnGrid(MovManual::codigoProduto, header = "Código")
    columnGrid(MovManual::nomeProduto, header = "Descrição").expand()
    columnGrid(MovManual::grade, header = "Grade")
    columnGrid(MovManual::data, header = "Data")
    columnGrid(MovManual::pedido, header = "Pedido")
    columnGrid(MovManual::transacao, header = "Transação")
    columnGrid(MovManual::qtty, header = "Quant Mov")
    columnGrid(MovManual::estVarejo, header = "Est Varejo")
    columnGrid(MovManual::estAtacado, header = "Est Atacado")
    columnGrid(MovManual::estTotal, header = "Est Total")
  }

  override fun filtro(): MovManualFilter {
    return MovManualFilter(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataI = edtDataInicial.value,
      dataF = edtDataFinal.value,
      tipo = ETipoMovManul.SAIDA
                            )
  }

  override fun updateNotas(notas: List<MovManual>) {
    updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.acertoMovManualSaida == true
  }

  override val label: String
    get() = "Sai Manual"

  override fun updateComponent() {
    viewModel.updateView()
  }
}