package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraImpresso
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraImpressoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.icon.VaadinIcon.CLOSE
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabRetiraImpresso(val viewModel: PedidoRetiraImpressoViewModel) :
  TabPanelGrid<Pedido>(Pedido::class),
  IPedidoRetiraImpresso {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var cmbTipoRetira: Select<ETipoRetira>

  override val label = "Impresso"

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    val loja = user?.lojaRetira ?: 0
    cmbLoja.isReadOnly = loja != 0 || user?.admin == true
    cmbLoja.value = viewModel.findLoja(loja) ?: Loja.lojaZero
  }

  override fun updateComponent() {
    viewModel.updateGridImpresso()
  }

  override fun filtro(): FiltroPedido {
    return FiltroPedido(
      tipo = ETipoPedido.RETIRA,
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value ?: LocalDate.now(),
      dataFinal = edtDataFinal.value ?: LocalDate.now(),
      tipoRetira = cmbTipoRetira.value ?: ETipoRetira.TODOS
    )
  }

  override fun isAuthorized(): Boolean {
    val userSaci = (AppConfig.userLogin() as? UserSaci) ?: return false
    return userSaci.retiraImpresso
  }

  override fun HorizontalLayout.toolBarConfig() {
    if (AppConfig.isAdmin) button("Desmarcar") {
      icon = CLOSE.create()
      addClickListener {
        viewModel.desmarcaSemNota()
      }
    }

    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateGridImpresso()
      }
    }

    init()

    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateGridImpresso()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateGridImpresso()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateGridImpresso()
      }
    }
    cmbTipoRetira = select("Tipo Retira") {
      this.setItems(ETipoRetira.entries)
      val userSaci = AppConfig.userLogin() as? UserSaci
      this.value = userSaci?.retiraTipo?.firstOrNull() ?: ETipoRetira.TODOS
      this.isReadOnly = userSaci?.admin != true
      this.setItemLabelGenerator { it.descricao }
      addValueChangeListener {
        viewModel.updateGridImpresso()
      }
    }
  }

  override fun Grid<Pedido>.gridPanel() {
    addColumnButton(VaadinIcon.PRINT, "Imprimir", "Imprimir") { pedido ->
      viewModel.confirmaPrint(pedido)
    }

    columnGrid(Pedido::tipoEcommece, "Tipo")
    columnGrid(Pedido::loja, "Loja")
    columnGrid(Pedido::pedido, "Pedido")
    columnGrid(Pedido::dataHoraPrint, "Impressão")

    columnGrid(Pedido::data, "Data")
    columnGrid(Pedido::hora, "Hora")

    columnGrid(Pedido::nfFat, "NF Fat")

    columnGrid(Pedido::dataFat, "Data")
    columnGrid(Pedido::horaFat, "Hora")
    columnGrid(Pedido::vendno, "Vendedor")

    columnGrid(Pedido::frete, "R$ Frete")
    columnGrid(Pedido::valorComFrete, "R$ Nota")
    columnGrid(Pedido::cliente, "Cliente").expand()
  }
}