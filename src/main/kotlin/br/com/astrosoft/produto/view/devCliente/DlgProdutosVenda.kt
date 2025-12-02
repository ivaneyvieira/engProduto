package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.focusEditor
import br.com.astrosoft.framework.view.vaadin.helper.integerFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoExp
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDev
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFNI
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFNIData
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantDevNI
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidadeDevolucao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFSeq
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFTemProduto
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgProdutosVenda(val viewModel: TabDevAutorizaViewModel, val nota: NotaVenda) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)

  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null
  private var edtMotivo: Select<EMotivoTroca>? = null

  fun showDialog(onClose: () -> Unit) {
    //val readOnly = (nota.ni ?: 0) > 0
    val readOnly = false
    val espaco = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"
    val nomeCliente = if (nota.nomeCliente.isNullOrBlank())
      "NÃO INFORMADO"
    else
      nota.nomeCliente
    val linha1 =
        "Loja: ${nota.loja.format("00")}${espaco}NF: ${nota.nota}${espaco}Data: ${nota.data.format()}${espaco}Vendedor: ${nota.vendedor}"
    val linha2 =
        "Tipo NF: ${nota.tipoNf}${espaco}Tipo Pgto: ${nota.tipoPgto}${espaco}Cliente: ${nota.cliente} - $nomeCliente"
    form = SubWindowForm(
      title = "$linha1|$linha2",
      toolBar = {
        edtTipo = select("Tipo") {
          this.isReadOnly = readOnly
          this.setItems(ESolicitacaoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        edtProduto = select("Produto") {
          this.isReadOnly = readOnly
          this.setItems(EProdutoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        if (nota.tipoNf == "ENTRE FUT") {
          edtNotaEntRet = integerField("Entrega Fut") {
            this.isReadOnly = readOnly
            this.width = "6rem"
            this.isAutoselect = true
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          }
        }

        edtMotivo = select("Motivo:") {
          this.isReadOnly = readOnly
          this.setItems(EMotivoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        button("Autoriza") {
          this.icon = VaadinIcon.SIGN_IN.create()
          this.isEnabled = !readOnly

          onClick {
            nota.solicitacaoTrocaEnnum = edtTipo?.value
            nota.produtoTrocaEnnum = edtProduto?.value
            nota.nfEntRet = edtNotaEntRet?.value ?: 0
            nota.setMotivoTroca = setOf(edtMotivo?.value).filterNotNull().toSet()
            val produtos: List<ProdutoNFS> = gridDetail.dataProvider.fetchAll().filterNotNull()
            val validacao = viewModel.validaProcesamento(nota, produtos)

            if (validacao) {
              val form = FormAutorizaNota()
              DialogHelper.showForm(caption = "Autoriza Devolução", form = form) {
                viewModel.autorizaNotaVenda(nota, produtos, form.login, form.senha)
              }
            }
          }
        }

        /*button("Desfaz") {
          val user = AppConfig.userLogin() as? UserSaci

          isVisible = user?.desautorizaDev == true

          this.icon = VaadinIcon.TRASH.create()
          this.isEnabled = !readOnly

          onClick {
            val produtos: List<ProdutoNFS> = gridDetail.dataProvider.fetchAll().filterNotNull()
            viewModel.desatorizaTroca(nota, produtos)
          }
        }*/
      },
      onClose = {
        onClose()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun updateNota() {
    edtTipo?.value = nota.solicitacaoTrocaEnnum
    edtProduto?.value = nota.produtoTrocaEnnum
    edtNotaEntRet?.value = nota.nfEntRet
    edtMotivo?.value = nota.setMotivoTroca.firstOrNull()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.NONE

      this.addItemClickListener {
        when {
          it.column.key == ProdutoNFS::dev.name                               -> {
            it.item.dev = !(it.item.dev ?: false)
            if (it.item.dev == true) {
              it.item.temProduto = true
            } else {
              it.item.temProduto = null
              it.item.quantDev = it.item.quantidade
            }
            this.dataProvider.refreshAll()
          }

          it.column.key == ProdutoNFS::temProduto.name && it.item.dev == true -> {
            it.item.temProduto = !(it.item.temProduto ?: false)
            this.dataProvider.refreshAll()
          }

          it.column.key == ProdutoNFS::quantDev.name && it.item.dev == true   -> {
            this.editor.editItem(it.item)
            this.focusEditor(ProdutoNFS::quantDev)
          }
        }
      }

      produtoNFDev()
      produtoNFTemProduto()
      produtoNFQuantidadeDevolucao().integerFieldEditor()
      produtoNFNI()
      produtoNFNIData()
      addColumnButton(iconButton = VaadinIcon.TRASH, tooltip = "Desfaz troca", header = "Desfaz") { produto ->
        viewModel.desatorizaTroca(nota, produto)
      }
      produtoNFCodigo()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFBarcode()
      produtoAutorizacaoExp()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario().apply {
        this.setFooter(Html("\"<b><span style=\"font-size: medium; \">Total</span></b>\""))
      }
      produtoNFPrecoTotal()
      produtoNFSeq()
      produtoNFQuantDevNI()

      this.setPartNameGenerator {
        val marca = it.marca
        val marcaImpressao = it.marcaImpressao ?: 0
        when {
          marcaImpressao > 0          -> "azul"
          marca == EMarcaNota.CD.num  -> "amarelo"
          marca == EMarcaNota.ENT.num -> "amarelo"
          else                        -> null
        }
      }
    }
    this.addAndExpand(gridDetail)

    update()

    gridDetail.setPartNameGenerator {
      if (it.dev == true) {
        "amarelo"
      } else {
        null
      }
    }
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos().expande()
    gridDetail.setItems(listProdutos)
    updateNota()

    val totalValor = listProdutos.sumOf { it.total ?: 0.0 }
    val totalCol = gridDetail.getColumnBy(ProdutoNFS::total)
    totalCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
  }

  fun produtos(): List<ProdutoNFS> {
    return gridDetail.list()
  }
}