package br.com.astrosoft.produto.view.estoqueCD

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.icon
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import kotlinx.css.button
import org.vaadin.vcamera.VCamera
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DlgBarcodeScanner2 : Dialog() {
  private var latest: File? = null
  init {
    setSizeFull()
    val camera = VCamera()

    camera.setReceiver { mimeType: String ->
      val suffix = if (mimeType.contains("jpeg")) {
        ".jpeg"
      } else if (mimeType.contains("matroska")) {
        ".mkv"
      } else {
        ".mp4"
      }
      if (latest != null) {
        latest?.delete()
      }
      try {
        val arquivo = File.createTempFile("camera", suffix)
        latest = arquivo
        println("Streaming to temp file $arquivo")
        return@setReceiver FileOutputStream(arquivo)
      } catch (e: IOException) {
        throw RuntimeException(e)
      }
    }
    add(camera)
    button ("Leitura"){
      this.icon = VaadinIcon.CAMERA.create()
      this.addClickListener {
        camera.takePicture()
      }
    }
    camera.openCamera()
  }
}