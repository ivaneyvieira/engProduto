package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowFormMobile
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoMobileViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.onClick
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.theme.lumo.LumoUtility

class DlgEstoqueAcertoMobile(val viewModel: TabEstoqueAcertoMobileViewModel, val acerto: EstoqueAcerto) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowFormMobile? = null
  private val virtualGrid = VirtualList<ProdutoEstoqueAcerto>()

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = acerto.numero
    val loja = acerto.lojaSigla
    val gravado = if (acerto.gravado == true) "(Gravado ${acerto.gravadoLoginStr})" else ""

    form = SubWindowFormMobile(
      "Produtos do Acerto $numero - Loja $loja $gravado",
      toolBar = {
        this.isWrap = true
        button("Grava Acerto") {
          this.addThemeVariants(ButtonVariant.LUMO_SMALL)
          this.icon = VaadinIcon.CHECK.create()
          this.addClickListener {
            viewModel.gravaAcerto(acerto)
            closeForm()
          }
        }

        this.button("Adiciona") {
          this.addThemeVariants(ButtonVariant.LUMO_SMALL)
          this.icon = VaadinIcon.PLUS.create()
          this.addClickListener {
            if (acerto.processado == true) {
              DialogHelper.showWarning("Acerto já processado")
              return@addClickListener
            }
            val dlg = DlgAdicionaAcertoMobile(viewModel, acerto) {
              virtualGrid.dataProvider.refreshAll()
            }
            dlg.open()
          }
        }
      },
      onClose = {
        closeForm()
      }) {
      virtualGrid.apply {
        this.setSizeFull()
        this.isExpand = true
        this.setRenderer(renderProduto)
      }
      update()
      virtualGrid
    }
    form?.open()
  }

  val renderProduto = ComponentRenderer<Component, ProdutoEstoqueAcerto> { produto ->
    VerticalLayout().apply {
      this.setWidthFull()
      this.isSpacing = false
      isMargin = false
      isPadding = false
      this.addClassNames(
        LumoUtility.BorderRadius.LARGE,
        LumoUtility.BoxShadow.LARGE,
        LumoUtility.Border.ALL,
        LumoUtility.BorderColor.CONTRAST_50
      )
      horizontalLayout {
        this.isPadding = true
        this.isSpacing = true
        this.isMargin = false
        this.isWrap = true
        hBlock {
          fieldPanel(produto.codigo, "Código", width = 6.00)
          fieldPanel(produto.grade, "Grade", width = 6.0)
        }
        hBlock {
          fieldPanel(produto.descricao, "Descrição", isExpand = true)
        }
        hBlock {
          fieldPanel(produto.estoqueSis?.toString(), "Est Sist", isExpand = true, isRight = true)
          fieldPanel(produto.estoqueCD?.toString(), "Est CD", isExpand = true, isRight = true)
          fieldPanel(produto.estoqueLoja?.toString(), "Est Loja", isExpand = true, isRight = true)
        }
        hBlock {
          fieldPanel(produto.diferenca?.toString(), "Diferença", isExpand = true, isRight = true)
          fieldPanel(produto.estoqueReal.toString(), "Est Real", isExpand = true, isRight = true)
        }
      }
      hBlock {
        this.isPadding = true
        this.isSpacing = true
        this.isMargin = false
        this.isWrap = true
        button("Conferência") {
          this.icon = VaadinIcon.DATE_INPUT.create()
          this.addThemeVariants(ButtonVariant.LUMO_SMALL)

          this.onClick {
            val user = AppConfig.userLogin()
            when {

              acerto.login != user?.login && user?.admin != true -> {
                DialogHelper.showWarning("Usuário não é o responsável pelo acerto")
              }

              acerto.processado == true                          -> {
                DialogHelper.showWarning("Acerto já processado")
              }

              else                                               -> {
                val dlgConferencia = DlgConferenciaAcertoMobile(viewModel, produto) {
                  virtualGrid.dataProvider.refreshAll()
                }
                dlgConferencia.open()
              }
            }
          }
        }
      }
    }
  }

  fun update() {
    val produtos = estoqueAcertos()
    virtualGrid.setItems(produtos)
  }

  private fun estoqueAcertos(): List<ProdutoEstoqueAcerto> {
    return acerto.findProdutos()
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  fun updateAcerto(acertos: List<EstoqueAcerto>) {
    val acerto = acertos.firstOrNull {
      it.numloja == this.acerto.numloja && it.numero == this.acerto.numero
    }
    virtualGrid.setItems(acerto?.produtos.orEmpty())
  }
}