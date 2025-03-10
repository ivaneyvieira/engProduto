package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.ETipoRota
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.SelectVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.value.ValueChangeMode
import kotlin.reflect.KMutableProperty1

abstract class TabPanelUser(val viewModel: TabUsrViewModel) : TabPanelGrid<UserSaci>(UserSaci::class) {
  lateinit var edtPesquisa: TextField

  abstract fun Grid<UserSaci>.configGrid()
  override fun Grid<UserSaci>.gridPanel() {
    this.format()

    columnGrid(UserSaci::no, "Código")
    columnGrid(UserSaci::login, "Login")
    columnGrid(UserSaci::name, "Nome")

    this.configGrid()
  }

  fun filter(): String {
    return edtPesquisa.value ?: ""
  }

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.TIMEOUT
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }
    button("Adicionar") {
      this.icon = VaadinIcon.PLUS.create()

      addClickListener {
        viewModel.adicionaUsuario()
      }
    }
    button("Atualizar") {
      this.icon = VaadinIcon.REFRESH.create()

      addClickListener {
        viewModel.modificarUsuario()
      }
    }
    button("Remove") {
      this.icon = VaadinIcon.TRASH.create()

      addClickListener {
        viewModel.removeUsuario()
      }
    }
  }

  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin()
    return user?.admin == true
  }

  override val label: String
    get() = "Usuários"

  override fun updateComponent() {
    viewModel.updateView()
  }

  fun updateUsuarios(usuarios: List<UserSaci>) {
    updateGrid(usuarios)
  }

  fun selectedItem(): UserSaci? {
    return itensSelecionados().firstOrNull()
  }

  abstract fun FormUsuario.configFields()

  private fun FormUsuario.configFieldsDefault(isReadOnly: Boolean) {
    textField("Login do Usuário") {
      this.isReadOnly = isReadOnly
      this.width = "300px"
      binder.bind(this, UserSaci::login.name)
    }
    textField("Nome do Usuário") {
      this.isReadOnly = isReadOnly
      this.width = "300px"
      this.isReadOnly = true
      binder.bind(this, UserSaci::name.name)
    }
  }

  fun formUpdUsuario(usuario: UserSaci) {
    val form = FormUsuario(usuario) {
      this.width = "60%"
      this.configFieldsDefault(true)
      this.configFields()
    }
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.updUser(form.userSaci)
    }
  }

  fun formAddUsuario() {
    val form = FormUsuario(UserSaci()) {
      this.width = "60%"
      this.configFieldsDefault(false)
      this.configFields()
    }
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.addUser(form.userSaci)
    }
  }

  protected fun HasComponents.filtroLoja(
    binder: Binder<UserSaci>,
    property: KMutableProperty1<UserSaci, Int?>,
    label: String = "Nome Loja"
  ) {
    select<Int>(label) {
      this.setWidthFull()
      this.addThemeVariants(SelectVariant.LUMO_SMALL)
      val lojas = viewModel.findAllLojas()
      val lojasNum = lojas.map { it.no } + listOf(0)
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
      binder.bind(this, property.name)
    }
  }

  protected fun HasComponents.filtroImpressoraTermica(
    binder: Binder<UserSaci>,
    property: KMutableProperty1<UserSaci, Set<String>>,
    block: MultiSelectComboBox<String>.() -> Unit = {}
  ): MultiSelectComboBox<String> {
    return multiSelectComboBox("Impressora Cupom") {
      this.setWidthFull()
      this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
      setItems(listOf("TODAS") + viewModel.allTermica().map { it.name })
      block()
      binder.bind(this, property.name)
    }
  }

  protected fun HasComponents.filtroImpressoraExpedicao(
    binder: Binder<UserSaci>,
    property: KMutableProperty1<UserSaci, Set<String>>
  ) {
    multiSelectComboBox<String>("Impressora") {
      this.setWidthFull()
      this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
      val impressoras = viewModel.allTermica().map { it.name }
      val impressorasRotaLoja = ETipoRota.impressoraLojas().map { it.name }
      val rotas = ETipoRota.entries - ETipoRota.TODAS
      val impressorasRota = rotas.map { it.nome }
      val itens = impressoras + impressorasRotaLoja + impressorasRota
      setItems(listOf("Todas") + itens.distinct().sorted())
      binder.bind(this, property.name)
    }
  }

  protected fun HasComponents.filtroImpressoraEtiqueta(
    binder: Binder<UserSaci>,
    property: KMutableProperty1<UserSaci, Set<String>>
  ) {
    multiSelectComboBox<String>("Impressora Etiqueta") {
      this.setWidthFull()
      this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
      setItems(listOf("TODAS") + viewModel.allEtiqueta().map { it.name })
      binder.bind(this, property.name)
    }
  }

  protected fun HasComponents.filtroImpressoraTodas(
    binder: Binder<UserSaci>,
    property: KMutableProperty1<UserSaci, Set<String>>
  ) {
    multiSelectComboBox<String>("Impressora") {
      this.setWidthFull()
      this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
      setItems(listOf("TODAS") + viewModel.allImpressoras().map { it.name })
      binder.bind(this, property.name)
    }
  }

  protected fun HasComponents.filtroLocalizacao(
    binder: Binder<UserSaci>,
    property: KMutableProperty1<UserSaci, Set<String>>
  ) {
    multiSelectComboBox<String>("Localização") {
      this.setWidthFull()
      this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
      setItems(listOf("TODOS") + viewModel.allLocalizacao())
      binder.bind(this, property.name)
    }
  }
}