package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardex
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabControleCDViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class DlgProdutoKardexCD(
  val viewModel: TabControleCDViewModel,
  val produto: ProdutoEstoque,
  val dataIncial: LocalDate?
) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoKardex::class.java, false)
  private lateinit var edtPesquisa: TextField

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }
    val locApp = produto.locApp

    val localizacao = locApp
    val dataInicial = produto.dataInicial ?: LocalDate.now().withDayOfMonth(1)

    form = SubWindowForm(
      "$codigo $descricao$grade ($localizacao) Data Inicial: ${dataInicial.format()} Estoque: ${produto.saldo ?: 0}",
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            update()
          }
        }
      },
      onClose = {
        closeForm()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(ProdutoKardex::loja, "Loja")
      columnGrid(ProdutoKardex::userLogin, "Usuário")
      columnGrid(ProdutoKardex::entLogin, "Entregue")
      columnGrid(ProdutoKardex::recLogin, "Recebido")
      columnGrid(ProdutoKardex::data, "Data")
      columnGrid(ProdutoKardex::ljDoc, "Lj Doc").right()
      //columnGrid(ProdutoKardex::quantDevolucao, "Garantia").right()
      columnGrid(ProdutoKardex::pedido, "Pedido").right()
      columnGrid(ProdutoKardex::doc, "NF Fat").right()
      columnGrid(ProdutoKardex::nfEnt, "NF Ent").right()
      columnGrid(ProdutoKardex::tipoDescricao, "Tipo")
      columnGrid(ProdutoKardex::observacaoAbrev, "Observação")
      columnGrid(ProdutoKardex::vencimento, "Vencimento", pattern = "MM/yyyy", width = null)
      columnGrid(ProdutoKardex::qtde, "Qtd")
      columnGrid(ProdutoKardex::saldo, "Est CD")
      columnGrid(ProdutoKardex::saldoEmb, "Est Emb")
    }

    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoKardex> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val kardex = viewModel.kardex(produto, dataIncial).filter {
      val pesquisa = edtPesquisa.value?.trim() ?: ""
      if (pesquisa.isEmpty()) return@filter true
      val doc = it.doc ?: ""
      val obs = it.observacao ?: ""
      val data = it.data?.format() ?: ""
      val user = it.userLogin ?: ""
      val nfEnt = it.nfEnt
      val tipo = it.tipoDescricao
      doc.contains(pesquisa, ignoreCase = true) || obs.contains(
        pesquisa,
        ignoreCase = true
      ) || data.startsWith(pesquisa)
      || user.contains(pesquisa, ignoreCase = true) || nfEnt.toString() == pesquisa || tipo.contains(
        pesquisa,
        ignoreCase = true
      )
    }
    gridDetail.setItems(kardex)
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }
}