package br.com.astrosoft.produto.view.estoqueCD

//import com.wontlost.zxing.Constants
//import com.wontlost.zxing.ZXingVaadinReader
/*
class DlgBarcodeScanner : Dialog() {
  init {
    setSizeFull()
    val reader = ZXingVaadinReader()
    reader.setFrom(Constants.From.camera)
    reader.setId("video")
    reader.setStyle("border : 1px solid gray")
    reader.width = "350"
    reader.onZxingErrorListener = SerializableConsumer { e: ZXingVaadinReader.DOMError ->
      // the exceptionName is NotAllowedError if the access is rejected by the user. For other names, please check
      // https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia
      println("Got permission error from the browser: " + e.name + ": " + e.message)
      var msg = "Unknown error accessing the camera"
      when (e.name) {
        "AbortError"                            -> {
          msg = "Unspecified error preventing the use of the camera"
        }

        "NotAllowedError"                       -> {
          msg =
              "Browser denies access to the camera. Please allow access to the camera in your browser and/or use secure connection"
        }

        "NotFoundError", "OverconstrainedError" -> {
          msg = "No camera found"
        }

        "NotReadableError"                      -> {
          msg = "Hardware error"
        }
      }
      if (e.name.isNotBlank()) {
        msg = msg + " (" + e.name + ")"
      }
      DialogHelper.showError(msg)
      close()
    }
    add(reader)
    reader.addValueChangeListener { e ->
      println(e)
    }
  }
}*/