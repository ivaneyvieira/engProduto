package br.com.astrosoft.framework.view.vaadin

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.vaadin.flow.component.HasComponents
import org.vaadin.hugerte.HugeRte
import org.vaadin.hugerte.HugeRteVariant
import org.vaadin.hugerte.Menubar
import org.vaadin.hugerte.Plugin

private fun HugeRte.configure() {
  this.configureLanguage(org.vaadin.hugerte.Language.PORTUGUESE_BRAZIL)
  this.configureMenubar(
    Menubar.FILE,
    Menubar.EDIT,
    Menubar.INSERT,
    Menubar.VIEW,
    Menubar.FORMAT,
    Menubar.TABLE,
    Menubar.TOOLS
  )
  this.configurePlugins(Plugin.LISTS, Plugin.LINK, Plugin.IMAGE, Plugin.TABLE, Plugin.FULLSCREEN/*, Plugin.AUTORESIZE*/)

  this.configure(
    "toolbar", "undo redo | blocks fontsize | bold italic underline | forecolor | " +
               "alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | " +
               "link image table | fullscreen"
  )
  this.configure("branding", true)
  this.configure("statusbar", true)
  this.configure(
    "content_style",
    """body { font-family: Arial,Helvetica,sans-serif; }
    |h1 { font-family: Arial,Helvetica,sans-serif; }
    |h2 { font-family: Arial,Helvetica,sans-serif; }
    |h3 { font-family: Arial,Helvetica,sans-serif; }
    |h4 { font-family: Arial,Helvetica,sans-serif; }
    |h5 { font-family: Arial,Helvetica,sans-serif; }
    |h6 { font-family: Arial,Helvetica,sans-serif; }
  """.trimMargin()
  )
  this.configure("ui_mode", "split")
  this.configure("toolbar_sticky", true)
 // this.configure("min_height", "300")
}

fun (@VaadinDsl HasComponents).hugeRTE(
  label: String = "",
  block: (@VaadinDsl HugeRte).() -> Unit
) {
  val htmlField = HugeRte(label)
  htmlField.configure()
  htmlField.addThemeVariants(HugeRteVariant.NO_EDITOR_FOCUS_HIGHLIGHT, HugeRteVariant.NO_INVALID_BACKGROUND)

  add(htmlField)

  htmlField.block()
}