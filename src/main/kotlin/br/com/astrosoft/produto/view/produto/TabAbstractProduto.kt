package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProduto
import br.com.astrosoft.produto.viewmodel.produto.ITabAbstractProdutoViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabAbstractProdutoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.header2
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataCommunicator
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.value.ValueChangeMode.LAZY
import com.vaadin.flow.function.SerializablePredicate
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import java.util.stream.Stream

abstract class TabAbstractProduto<T : ITabAbstractProdutoViewModel>(
  open val viewModel: TabAbstractProdutoViewModel<T>,
  val showDatas: Boolean = true
) :
  TabPanelGrid<Produtos>(Produtos::class), ITabAbstractProdutoViewModel {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: IntegerField
  private lateinit var edtVal: IntegerField
  private lateinit var cmbPontos: Select<EMarcaPonto>
  private lateinit var edtListVend: TextField
  private lateinit var edtType: IntegerField
  private lateinit var edtCl: IntegerField
  private lateinit var edtTributacao: TextField

  private lateinit var edtVenda: Select<MesAno>
  private lateinit var edtCompra: Select<MesAno>
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var chkGrade: Checkbox
  private lateinit var edtGrade: TextField

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun produtosSelecionados(): List<Produtos> {
    val sortGrid = this.gridPanel.sortOrder
    val dataProvider: ListDataProvider<Produtos> = gridPanel.dataProvider as ListDataProvider<Produtos>
    val size = dataProvider.items.size
    val dataCommunicator: DataCommunicator<Produtos> = gridPanel.dataCommunicator
    val stream: Stream<Produtos> =
        dataProvider.fetch(
          Query<Produtos, SerializablePredicate<Produtos>>(
            0,
            size,
            dataCommunicator.backEndSorting,
            dataCommunicator.inMemorySorting,
            null
          )
        )
    val list: List<Produtos> = stream.collect(Collectors.toList())
    val selecionado = this.itensSelecionados()
    return list.filter { selecionado.contains(it) }
  }

  override fun HorizontalLayout.toolBarConfig() {
    flexLayout {
      this.isExpand = true
      this.flexWrap = FlexLayout.FlexWrap.WRAP
      this.alignContent = FlexLayout.ContentAlignment.SPACE_BETWEEN

      horizontalLayout {
        edtPesquisa = textField("Pesquisa") {
          this.width = "200px"
          this.valueChangeMode = LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtCodigo = integerField("Código") {
          this.width = "100px"
          this.valueChangeMode = LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtVal = integerField("Val") {
          this.width = "80px"
          this.valueChangeMode = LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtListVend = textField("Fornecedores") {
          this.valueChangeMode = LAZY
          this.width = "150px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtTributacao = textField("Trib") {
          this.valueChangeMode = LAZY
          this.width = "80px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtType = integerField("Tipo") {
          this.valueChangeMode = LAZY
          this.width = "100px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtCl = integerField("CL") {
          this.width = "100px"
          this.valueChangeMode = LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbPontos = select("Caracteres") {
          this.width = "100px"
          setItems(EMarcaPonto.entries)
          value = EMarcaPonto.TODOS
          this.setItemLabelGenerator {
            it.descricao
          }
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        chkGrade = checkBox("Grade") {
          this.value = true
          this.addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtGrade = textField("Grade") {
          this.valueChangeMode = LAZY
          this.width = "100px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }

      horizontalLayout {
        cmbLoja = select("Loja") {
          val lojaTodas = Loja(0, "Todas", "Todas")
          val lojas = listOf(lojaTodas) + viewModel.allLojas()
          setItems(lojas)
          value = lojaTodas
          this.setItemLabelGenerator {
            it.sname
          }
          addValueChangeListener {
            viewModel.updateView()
          }
          this.width = "6em"
        }

        edtCompra = select("Compra ") {
          this.width = "150px"
          this.setItems(MesAno.values())
          this.value = MesAno.now()
          this.setItemLabelGenerator {
            it.mesAnoFormat
          }

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtVenda = select("Venda") {
          this.width = "150px"
          this.setItems(MesAno.values())
          this.value = MesAno.now()
          this.setItemLabelGenerator {
            it.mesAnoFormat
          }

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }

        addAditionaisFields()
        downloadExcel(PlanilhaProduto())
      }
    }
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produto$textTime.xlsx"
  }

  protected abstract fun HorizontalLayout.addAditionaisFields()

  override fun filtro() = FiltroListaProduto(
    pesquisa = edtPesquisa.value ?: "",
    marcaPonto = cmbPontos.value ?: EMarcaPonto.TODOS,
    inativo = EInativo.NAO,
    codigo = edtCodigo.value ?: 0,
    listVend = edtListVend.value?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
    tributacao = edtTributacao.value ?: "",
    typeno = edtType.value ?: 0,
    clno = edtCl.value ?: 0,
    loja = cmbLoja.value?.no ?: 0,
    diVenda = edtVenda.value.firstDay,
    dfVenda = edtVenda.value.lastDay,
    diCompra = edtCompra.value.firstDay,
    dfCompra = edtCompra.value.lastDay,
    temGrade = chkGrade.value,
    grade = edtGrade.value ?: "",
    estoque = estoque(),
    saldo = saldo(),
    validade = edtVal.value ?: 0
  )


  abstract fun estoque(): EEstoqueList
  abstract fun saldo(): Int

  override fun Grid<Produtos>.gridPanel() {
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.colunasGrid()
    printColunas()
  }

  abstract fun Grid<Produtos>.colunasGrid()

  private fun printColunas() {
    val colList = gridPanel.columns.joinToString("\n") { column ->
      "CampoNumber(\"${column.header2}\") { ${column.key} ?: 0.00 },"
    }
    println(label)
    println(colList)
  }

    private fun HasComponents.downloadExcel(planilha: PlanilhaProduto) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    add(button)
  }
}


