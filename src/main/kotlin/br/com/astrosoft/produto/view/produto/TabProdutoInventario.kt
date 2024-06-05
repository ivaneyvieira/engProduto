package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoInventarioViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabProdutoInventario(val viewModel: TabProdutoInventarioViewModel) :
  TabPanelGrid<ProdutoInventario>(ProdutoInventario::class),
  ITabProdutoInventario {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: TextField
  private lateinit var edtInventario: IntegerField
  private lateinit var edtAno: IntegerField
  private lateinit var edtMes: IntegerField
  private lateinit var edtGrade: TextField
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbCartacer: Select<ECaracter>
  private lateinit var btnAdiciona: Button
  private lateinit var btnRemover: Button

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaProduto != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaProduto ?: 0) ?: Loja.lojaZero
  }

  override fun HorizontalLayout.toolBarConfig() {
    verticalBlock {
      horizontalBlock {
        isSpacing = true
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
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        init()

        edtCodigo = textField("Código") {
          this.width = "110px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtGrade = textField("Grade") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtInventario = integerField("Validade") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtMes = integerField("Mês") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          valueChangeTimeout = 500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtAno = integerField("Ano") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          valueChangeTimeout = 500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbCartacer = select("Caracter") {
          this.setItems(ECaracter.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ECaracter.TODOS
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalBlock {
        isSpacing = true

        btnAdiciona = button("Adicionar") {
          this.icon = VaadinIcon.PLUS.create()
          addClickListener {
            viewModel.adicionarLinha()
          }
        }

        btnRemover = button("Remover") {
          this.icon = VaadinIcon.TRASH.create()
          addClickListener {
            viewModel.removerLinha()
          }
        }

        downloadExcel(PlanilhaProdutoInventario())

        val user = AppConfig.userLogin() as? UserSaci
        if (user?.admin == true) {
          button("Atualizar") {
            onLeftClick {
              viewModel.atualizarTabelas()
            }
          }
        }
      }
    }
  }

  override fun Grid<ProdutoInventario>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.withEditor(
      ProdutoInventario::class,
      openEditor = {
        this.focusEditor(ProdutoInventario::estoque)
      },
      closeEditor = {
        viewModel.salvaInventario(it.bean)
      })

    this.addColumnSeq("Seq", width = "50px")
    this.columnGrid(ProdutoInventario::codigo, header = "Código")
    this.columnGrid(ProdutoInventario::descricao, header = "Descrição").expand()
    this.columnGrid(ProdutoInventario::grade, header = "Grade", width = "100px")
    this.columnGrid(ProdutoInventario::lojaAbrev, header = "Loja", width = "70px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.loja ?: 0 })
    }
    this.columnGrid(ProdutoInventario::estoqueLoja, header = "Total")
    this.columnGrid(ProdutoInventario::saldo, header = "Saldo")
    columnGrid(ProdutoInventario::vencimentoStr, header = "Venc", width = "130px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.vencimento ?: 0 })
    }.mesAnoFieldEditor()
    this.columnGrid(ProdutoInventario::estoque, header = "Inv", width = "85px").integerFieldEditor()
    this.columnGrid(ProdutoInventario::compras, header = "Compra", width = "85px").integerFieldEditor()
    // columnGroup("Entrada") {
    //    this.columnGrid(ProdutoInventario::estoque, header = "Inv", width = "85px").integerFieldEditor()
    //  this.columnGrid(ProdutoInventario::entradaTransf, header = "Tranf", width = "85px")
    //  this.columnGrid(ProdutoInventario::entradaCompra, header = "Compra", width = "85px")
    // }
    // columnGroup("Saída") {
    //   this.columnGrid(ProdutoInventario::saidaVenda, header = "Venda", width = "85px")
    //   this.columnGrid(ProdutoInventario::saidaTransf, header = "Transf", width = "85px")
    // }

    columnGrid(ProdutoInventario::dataEntrada, header = "Data Entrada", width = "120px").dateFieldEditor {
      it.value = LocalDate.now()
    }
    columnGrid(ProdutoInventario::validade, header = "Val")
    columnGrid(ProdutoInventario::unidade, header = "Un")
    columnGrid(ProdutoInventario::vendno, header = "For")

    this.sort(
      ProdutoInventario::lojaAbrev.asc,
      ProdutoInventario::codigo.asc,
      ProdutoInventario::grade.asc,
      ProdutoInventario::vencimentoStr.asc
    )
  }

  override fun filtro(): FiltroProdutoInventario {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroProdutoInventario(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtCodigo.value ?: "",
      validade = edtInventario.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      ano = edtAno.value ?: 0,
      mes = edtMes.value ?: 0,
      storeno = cmbLoja.value?.no ?: user?.lojaProduto ?: 0,
    )
  }

  override fun updateProdutos(produtos: List<ProdutoInventario>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoInventario> {
    return itensSelecionados()
  }

  override fun formAdd(produtoInicial: ProdutoInventario, callback: (novoEditado: ProdutoInventario) -> Unit) {
    val edtMesAno = mesAnoFieldComponente().apply {
      this.label = "Validade"
      this.value = produtoInicial.vencimentoStr
    }
    val edtInventario = IntegerField().apply {
      this.label = "Inventário"
      this.value = produtoInicial.estoque
    }
    val edtDataEntrada = DatePicker().apply {
      this.localePtBr()
      this.label = "Data Entrada"
      this.value = LocalDate.now()
    }
    val form = FormLayout().apply {
      textField("Loja") {
        this.value = produtoInicial.lojaAbrev
        this.isReadOnly = true
      }
      textField("Código") {
        this.value = produtoInicial.codigo
        this.isReadOnly = true
      }
      textField("Descrição") {
        this.value = produtoInicial.descricao
        this.isReadOnly = true
      }
      textField("Grade") {
        this.value = produtoInicial.grade
        this.isReadOnly = true
      }
      add(edtMesAno)
      add(edtInventario)
      add(edtDataEntrada)
    }

    DialogHelper.showForm("Inventário", form) {
      produtoInicial.vencimentoStr = edtMesAno.value
      produtoInicial.estoque = edtInventario.value
      produtoInicial.dataEntrada = edtDataEntrada.value
      callback(produtoInicial)
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoInventario == true
  }

  override val label: String
    get() = "Inventário"

  override fun updateComponent() {
    viewModel.updateView()
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaProdutoInventario) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.text = "Planilha"
    //button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    add(button)
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produto$textTime.xlsx"
  }
}

private fun mesAnoFieldComponente() = ComboBox<String>().apply {
  this.isClearButtonVisible = true
  this.setWidthFull()
  this.setSizeFull()
  this.isAllowCustomValue = true
  val dateListDepois = (0..12 * 15).map { num ->
    LocalDate.now().withDayOfMonth(15).plusMonths(num.toLong())
  }
  val dateListAntes = (1..6).map { num ->
    LocalDate.now().withDayOfMonth(15).minusMonths(num.toLong())
  }
  val dateList = dateListAntes + dateListDepois
  this.setItems(dateList.sorted().map { it.format("MM/yy") }.distinct())
}