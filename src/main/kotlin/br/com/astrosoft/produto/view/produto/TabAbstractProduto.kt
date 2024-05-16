package br.com.astrosoft.produto.view.produto


import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.promocao.viewmodel.produto.ITabAbstractProdutoViewModel
import br.com.astrosoft.promocao.viewmodel.produto.TabAbstractProdutoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.header2
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
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
  private lateinit var cmbPontos: Select<EMarcaPonto>
  private lateinit var edtListVend: TextField
  private lateinit var edtType: IntegerField
  private lateinit var edtCl: IntegerField
  private lateinit var edtTributacao: TextField

  private lateinit var edtDiVenda: DatePicker
  private lateinit var edtDfVenda: DatePicker
  private lateinit var edtDiCompra: DatePicker
  private lateinit var edtDfCompra: DatePicker

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
          this.valueChangeMode = LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtListVend = textField("Fornecedores") {
          this.valueChangeMode = LAZY
          this.width = "250px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtTributacao = textField("Tributação") {
          this.valueChangeMode = LAZY
          this.width = "80px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtType = integerField("Tipo") {
          this.valueChangeMode = LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtCl = integerField("Centro de Lucro") {
          this.valueChangeMode = LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbPontos = select("Caracteres Especiais") {
          setItems(EMarcaPonto.values().toList())
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
          this.width = "80px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }

      horizontalLayout {
        edtDiCompra = datePicker("Data Compra Inicial") {
          this.localePtBr()
          this.value = LocalDate.now().withDayOfMonth(1)

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtDfCompra = datePicker("Data Compra Final") {
          this.localePtBr()
          this.value = LocalDate.now()

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtDiVenda = datePicker("Data Venda Inicial") {
          this.localePtBr()
          this.value = LocalDate.now().withDayOfMonth(1)

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtDfVenda = datePicker("Data Venda Final") {
          this.localePtBr()
          this.value = LocalDate.now()

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }

        addAditionaisFields()
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
    todoEstoque = viewModel.todoEstoque(),
    inativo = EInativo.NAO,
    codigo = 0,
    listVend = edtListVend.value?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
    tributacao = edtTributacao.value ?: "",
    typeno = edtType.value ?: 0,
    clno = edtCl.value ?: 0,
    lojaEstoque = lojaEstoque(),
    estoqueTotal = estoqueTotal(),
    diVenda = edtDiVenda.value,
    dfVenda = edtDfVenda.value,
    diCompra = edtDiCompra.value,
    dfCompra = edtDfCompra.value,
    temGrade = chkGrade.value,
    grade = edtGrade.value ?: "",
    estoque = estoque(),
    saldo = saldo(),
  )

  abstract fun estoqueTotal(): EEstoqueTotal
  abstract fun lojaEstoque(): Int
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
}


