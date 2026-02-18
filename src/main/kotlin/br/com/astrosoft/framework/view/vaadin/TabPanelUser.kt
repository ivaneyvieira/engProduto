package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.ETipoRota
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.SelectVariant
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
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

    this.sort(UserSaci::no.asc)
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
    button("Editar") {
      this.icon = VaadinIcon.EDIT.create()

      addClickListener {
        viewModel.modificarUsuario()
      }
    }
    button("Remover") {
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

  abstract fun HorizontalLayout.configFields(binder: Binder<UserSaci>)

  private fun KFormLayout.configFieldsDefault(binder: Binder<UserSaci>, isReadOnly: Boolean) {
    setResponsiveSteps(FormLayout.ResponsiveStep("0", 12))

    integerField("Número") {
      this.colspan = 2
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.isReadOnly = isReadOnly
      this.isAutoselect = true
      this.isAutofocus = true
      bind(binder).bind(UserSaci::no)
      this.valueChangeMode = ValueChangeMode.EAGER

      this.addValueChangeListener { event ->
        if (event.isFromClient) {
          val numUser = event.value

          if (numUser == null) {
            invalidaNumero(event.source, "Preencha com um numero")
            return@addValueChangeListener
          }

          val user = viewModel.findUser(numUser)
          if (user != null) {
            binder.bean = user
          } else {
            val userNew = UserSaci()
            userNew.no = numUser
            binder.bean = userNew
          }
          event.source.isInvalid = false
        }
      }
    }
    textField("Nome do Usuário") {
      this.colspan = 10
      this.isAutoselect = true
      bind(binder).withNullRepresentation("").bind(UserSaci::name)
    }

    textField("Login do Usuário") {
      this.colspan = 6
      this.isAutoselect = true
      bind(binder).bind(UserSaci::login)
    }
    passwordField("Senha") {
      this.colspan = 6
     // this.isAutoselect = true
      bind(binder).bind(UserSaci::senha)
    }

    select<Int>("Loja") {
      val lojas = listOf(Loja.lojaZero) + viewModel.findAllLojas()
      this.colspan = 6
      this.setItems(lojas.map { it.no })
      this.setItemLabelGenerator { numLoja ->
        lojas.firstOrNull { loja -> loja.no == numLoja }?.descricao ?: ""
      }
      bind(binder).bind(UserSaci::storeno)
    }
    select<Int>("Impressora") {
      val impressoras = viewModel.allImpressoras()
      this.colspan = 6
      this.setItems(impressoras.map { it.no })
      this.setItemLabelGenerator { numImpressora ->
        impressoras.firstOrNull { impressora -> impressora.no == numImpressora }?.name ?: ""
      }
      bind(binder).bind(UserSaci::storeno)
    }
  }

  fun invalidaNumero(field: IntegerField, message: String) {
    val notification = Notification()
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
    notification.add(Text(message))
    notification.duration = 3000
    notification.position = Notification.Position.TOP_CENTER
    notification.open()
    field.isInvalid = true
    field.focus()
  }

  fun formUpdUsuario(usuario: UserSaci) {
    val form = FormUsuario(usuario) { binder: Binder<UserSaci> ->
      this.width = "60%"
      this.isMargin = false
      this.isPadding = false
      this.formLayout {
        this.borderRountend()
        this.configFieldsDefault(binder, true)
      }

      this.horizontalBlock {
        this.isSpacing = true
        this.setWidthFull()
        this.borderRountend()
        this.configFields(binder)
      }
    }
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.updUser(form.userSaci)
    }
  }

  fun formAddUsuario() {
    val form = FormUsuario(UserSaci()) { binder: Binder<UserSaci> ->
      this.width = "60%"
      this.formLayout {
        this.configFieldsDefault(binder = binder, isReadOnly = false)
        this.borderRountend()
      }

      this.horizontalBlock {
        this.isSpacing = true
        this.setWidthFull()
        this.borderRountend()

        this.configFields(binder)
      }
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