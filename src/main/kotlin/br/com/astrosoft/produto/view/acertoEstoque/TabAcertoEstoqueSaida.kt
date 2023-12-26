package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.AcertoSaidaNota
import br.com.astrosoft.produto.model.beans.FiltroAcertoSaida
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.acertoEstoque.ITabAcertoEstoqueSaida
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoEstoqueSaidaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabAcertoEstoqueSaida(val viewModel: TabAcertoEstoqueSaidaViewModel) :
  TabPanelGrid<AcertoSaidaNota>(AcertoSaidaNota::class),
  ITabAcertoEstoqueSaida {
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

  override fun Grid<AcertoSaidaNota>.gridPanel() {
    this.addClassName("styling")
    columnGrid(AcertoSaidaNota::loja, header = "Loja")
    columnGrid(AcertoSaidaNota::notaFiscal, header = "Nota Fiscal").right()
    columnGrid(AcertoSaidaNota::dataEmissao, header = "Data")
    columnGrid(AcertoSaidaNota::cliente, header = "Cód Cli")
    columnGrid(AcertoSaidaNota::nomeCliente, header = "Cliente")
    columnGrid(AcertoSaidaNota::observacao, header = "Observação")
  }

  override fun filtro(): FiltroAcertoSaida {
    return FiltroAcertoSaida(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(nota: List<AcertoSaidaNota>) {
    updateGrid(nota)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.acertoSaida == true
  }

  override val label: String
    get() = "Saida"

  override fun updateComponent() {
    viewModel.updateView()
  }
}