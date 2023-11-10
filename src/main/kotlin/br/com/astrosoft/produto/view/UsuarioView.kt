package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.view.vaadin.UserLayout
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.*
import org.vaadin.crudui.crud.impl.GridCrud

@Route(layout = ProdutoLayout::class)
@PageTitle("Usuário")
@PermitAll
class UsuarioView : UserLayout<UserSaci, UsuarioViewModel>(), IUsuarioView {
  override val viewModel = UsuarioViewModel(this)

  override fun columns(): List<String> {
    return listOf(UserSaci::no.name, UserSaci::login.name, UserSaci::name.name, UserSaci::impressora.name)
  }

  override fun createGrid() = GridCrud(UserSaci::class.java)

  override fun formCrud(operation: CrudOperation?,
                        domainObject: UserSaci?,
                        readOnly: Boolean,
                        binder: Binder<UserSaci>): Component {
    return VerticalLayout().apply {
      val lojas = viewModel.allLojas()
      val values = lojas.map { it.no } + listOf(0)

      isPadding = false
      isMargin = false
      formLayout {
        if (operation in listOf(READ, DELETE, UPDATE)) integerField("Número") {
          isReadOnly = readOnly
          binder.bind(this, UserSaci::no.name)
        }
        if (operation in listOf(ADD, READ, DELETE, UPDATE)) textField("Login") {
          isReadOnly = readOnly
          binder.bind(this, UserSaci::login.name)
        }
        if (operation in listOf(READ, DELETE, UPDATE)) textField("Nome") {
          isReadOnly = true
          binder.bind(this, UserSaci::name.name)
        }

        if (operation in listOf(ADD, READ, DELETE, UPDATE)) {
          select<Int>("Nome Loja") {
            isReadOnly = readOnly
            setItems(values.distinct().sorted())
            this.setItemLabelGenerator { storeno ->
              when (storeno) {
                0    -> "Todas as lojas"
                else -> lojas.firstOrNull { loja ->
                  loja.no == storeno
                }?.descricao ?: ""
              }
            }
            binder.bind(this, UserSaci::storeno.name)
          }
          checkBoxGroup<String> {
            colspan = 2
            label = "Locais"
            isReadOnly = readOnly

            val locais = viewModel.allLocais()
            val valuesCD = listOf("TODOS") + locais.map { it.abreviacao }.sorted()
            setItems(valuesCD)
            binder.bind(this, UserSaci::listLocais.name)
          }
          formLayout {
            h4("Produto") {
              colspan = 2
            }
            checkBox("Produtos") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::produtoList.name)
            }
            checkBox("Retira/Entrega") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::produtoRetiraEntrega.name)
            }
            checkBox("Retira/Entrega/Edit") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::produtoRetiraEntregaEdit.name)
            }
            checkBox("Reserva") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::produtoReserva.name)
            }
          }
          formLayout {
            h4("Tipo Notas Saida"){
              colspan = 2
            }

            checkBox("NFCE") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::nfceExpedicao.name)
            }
            checkBox("Vendas") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::vendaExpedicao.name)
            }
            checkBox("Ent/Ret") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::entRetExpedicao.name)
            }
            checkBox("Transferencia") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::transfExpedicao.name)
            }
            checkBox("Venda Futura") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::vendaFExpedicao.name)
            }
          }
          formLayout {
            h4("Nota de Saida"){
              colspan = 2
            }
            formLayout {
              checkBox("Exp") {
                isReadOnly = readOnly
                binder.bind(this, UserSaci::notaExp.name)
              }
              select<Int>("Loja Exp") {
                isReadOnly = readOnly
                setItems(values.distinct().sorted())
                this.setItemLabelGenerator { storeno ->
                  when (storeno) {
                    0    -> "Todas as lojas"
                    else -> lojas.firstOrNull { loja ->
                      loja.no == storeno
                    }?.descricao ?: ""
                  }
                }
                binder.bind(this, UserSaci::lojaSaidaExp.name)
              }
            }
            formLayout {
              checkBox("CD") {
                isReadOnly = readOnly
                binder.bind(this, UserSaci::notaCD.name)
              }
              select<Int>("Loja CD") {
                isReadOnly = readOnly
                setItems(values.distinct().sorted())
                this.setItemLabelGenerator { storeno ->
                  when (storeno) {
                    0    -> "Todas as lojas"
                    else -> lojas.firstOrNull { loja ->
                      loja.no == storeno
                    }?.descricao ?: ""
                  }
                }
                binder.bind(this, UserSaci::lojaSaidaCD.name)
             }
            }
            formLayout {
              checkBox("Entrege") {
                isReadOnly = readOnly
                binder.bind(this, UserSaci::notaEnt.name)
              }
              select<Int>("Loja Entregue") {
                isReadOnly = readOnly
                setItems(values.distinct().sorted())
                this.setItemLabelGenerator { storeno ->
                  when (storeno) {
                    0    -> "Todas as lojas"
                    else -> lojas.firstOrNull { loja ->
                      loja.no == storeno
                    }?.descricao ?: ""
                  }
                }
                binder.bind(this, UserSaci::lojaSaidaEntregue.name)
              }
            }
            checkBox("Voltar CD") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::voltarCD.name)
            }
            checkBox("Voltar Ent") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::voltarEnt.name)
            }
          }
          formLayout {
            h4("Nota de Entrada"){
              colspan = 2
            }
            checkBox("Base") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::notaEntradaBase.name)
            }
            checkBox("Receber") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::notaEntradaReceber.name)
            }
            checkBox("Recebido") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::notaEntradaRecebido.name)
            }
          }
          formLayout {
            h4("Pedido"){
              colspan = 2
            }
            checkBox("CD") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoCD.name)
            }
            checkBox("Entrege") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoEnt.name)
            }
          }
          formLayout {
            h4("Ressuprimento"){
              colspan = 2
            }
            checkBox("CD") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::ressuprimentoCD.name)
            }
            checkBox("Entrege") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::ressuprimentoEnt.name)
            }
          }
          formLayout {
            h4("Pedido Transf"){
              colspan = 2
            }
            checkBox("Reserva") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoTransfReserva.name)
            }
            checkBox("Entrege") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoTransfEnt.name)
            }
          }
          formLayout {
            h4("Receber Entrada") {
              colspan = 2
            }
            checkBox("Alterar") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::receberQuantidade.name)
            }
            checkBox("Excluir") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::receberExcluir.name)
            }
            checkBox("Adicionar") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::receberAdicionar.name)
            }
            checkBox("Processar") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::receberProcessar.name)
            }
          }
        }
      }
    }
  }
}


