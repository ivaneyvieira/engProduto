package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.view.vaadin.UserLayout
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.produto.model.beans.ETipoRota
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
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
    return listOf(
      UserSaci::no.name, UserSaci::login.name, UserSaci::name.name, UserSaci::impressora.name,
      UserSaci::ativoSaci.name
    )
  }

  override fun createGrid() = GridCrud(UserSaci::class.java)

  override fun formCrud(
    operation: CrudOperation?,
    domainObject: UserSaci?,
    readOnly: Boolean,
    binder: Binder<UserSaci>
  ): Component {
    return VerticalLayout().apply {
      val lojas = viewModel.allLojas()
      val lojasNum = lojas.map { it.no } + listOf(0)
      val impressoras = viewModel.allImpressoras()
        .map { it.name }

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
            setItems(lojasNum.distinct().sorted())
            this.isEmptySelectionAllowed = true
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

          formLayout {
            h4("Ressuprimento") {
              colspan = 2
            }
            checkBox("Separar") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::ressuprimentoCD.name)
            }
            horizontalLayout {
              this.isMargin = false
              this.isPadding = false
              checkBox("Entrege") {
                isReadOnly = readOnly
                binder.bind(this, UserSaci::ressuprimentoEnt.name)
              }
              select<String>("Impressora") {
                this.isExpand = true
                isReadOnly = readOnly
                setItems(listOf("TODAS") + viewModel.allImpressoras().map { it.name })
                binder.bind(this, UserSaci::impressoraRessu.name)
              }
            }
            checkBox("Recebido") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::ressuprimentoRec.name)
            }
            checkBox("Usuário Recebedor") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::ressuprimentoRecebedor.name)
            }
            multiSelectComboBox<String>("Localização") {
              isReadOnly = readOnly
              setItems(listOf("TODOS") + viewModel.allLocalizacao())
              binder.bind(this, UserSaci::listaRessuprimento.name)
            }
            select<Int>("Nome Loja") {
              isReadOnly = readOnly
              setItems(lojasNum.distinct().sorted())
              this.isEmptySelectionAllowed = true
              this.setItemLabelGenerator { storeno ->
                when (storeno) {
                  0    -> "Todas as lojas"
                  else -> lojas.firstOrNull { loja ->
                    loja.no == storeno
                  }?.descricao ?: ""
                }
              }
              binder.bind(this, UserSaci::lojaRessu.name)
            }
          }
          formLayout {
            h4("Pedido Transf") {
              colspan = 2
            }
            multiSelectComboBox<String>("Impressoras") {
              isReadOnly = readOnly
              setItems(impressoras.distinct().sorted() + ETipoRota.entries.map { it.nome })
              this.value = emptySet()
              binder.bind(this, UserSaci::impressoraTrans.name)
              colspan = 2
            }
            checkBox("Reserva") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoTransfReserva.name)
            }
            checkBox("Autorizada") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoTransfAutorizada.name)
            }
            checkBox("Entrege") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoTransfEnt.name)
            }
            checkBox("Ressu4") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::pedidoTransfRessu4.name)
            }
          }
          formLayout {
            h4("Dev Cliente") {
              colspan = 2
            }
            select<String>("Impressora") {
              isReadOnly = readOnly
              setItems(impressoras.distinct().sorted())
              this.isEmptySelectionAllowed = true
              binder.bind(this, UserSaci::impressoraDev.name)
            }
            select<Int>("Nome Loja") {
              isReadOnly = readOnly
              setItems(lojasNum.distinct().sorted())
              this.isEmptySelectionAllowed = true
              this.setItemLabelGenerator { storeno ->
                when (storeno) {
                  0    -> "Todas as lojas"
                  else -> lojas.firstOrNull { loja ->
                    loja.no == storeno
                  }?.descricao ?: ""
                }
              }
              binder.bind(this, UserSaci::lojaVale.name)
            }
            checkBox("VC Imprimir") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devCliImprimir.name)
            }
            checkBox("Autoriza Devolução") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::autorizaDevolucao.name)
            }
            checkBox("VC Impresso") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devCliImpresso.name)
            }
            checkBox("Produto") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devCliValeTrocaProduto.name)
            }
            checkBox("Crédito") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devCliCredito.name)
            }
            checkBox("Editor") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devCliEditor.name)
            }
            checkBox("Troca") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devClienteTroca.name)
            }
            checkBox("Venda") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::devVenda.name)
            }
          }
          formLayout {
            h4("Retira") {
              colspan = 2
            }
            select<String>("Impressora") {
              isReadOnly = readOnly
              setItems(impressoras.distinct().sorted())
              this.isEmptySelectionAllowed = true
              binder.bind(this, UserSaci::impressoraRet.name)
            }
            checkBox("Imprimir") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::retiraImprimir.name)
            }
            checkBox("Impresso") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::retiraImpresso.name)
            }
          }
          formLayout {
            h4("Acerto Estoque") {
              colspan = 2
            }
            checkBox("Entrada") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::acertoEntrada.name)
            }
            checkBox("Saída") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::acertoSaida.name)
            }
            checkBox("Ent Manual") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::acertoMovManualEntrada.name)
            }
            checkBox("Sai Manual") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::acertoMovManualSaida.name)
            }
            checkBox("Atacado") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::acertoMovAtacado.name)
            }
          }
          formLayout {
            h4("CD MF") {
              colspan = 2
            }
            checkBox("Estoque") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::estoqueMF.name)
            }
            checkBox("Cad Loc") {
              isReadOnly = readOnly
              binder.bind(this, UserSaci::estoqueCad.name)
            }
            select<String>("Localização") {
              isReadOnly = readOnly
              setItems(listOf("TODOS") + viewModel.allLocalizacao())
              binder.bind(this, UserSaci::localEstoque.name)
            }
          }
        }
      }
    }
  }
}


