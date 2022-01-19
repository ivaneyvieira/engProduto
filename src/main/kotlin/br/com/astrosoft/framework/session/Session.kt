package br.com.astrosoft.framework.session

import com.vaadin.flow.server.VaadinRequest
import com.vaadin.flow.server.VaadinResponse
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.VaadinSession
import java.io.Serializable
import javax.servlet.http.Cookie
import kotlin.reflect.KClass

object Session {
  val current: VaadinSession get() = VaadinSession.getCurrent() ?: throw IllegalStateException("Not in UI thread")

  operator fun get(key: String): Any? = current.getAttribute(key)

  operator fun <T : Any> get(key: KClass<T>): T? = current.getAttribute(key.java)

  operator fun set(key: String, value: Any?) {
    current.setAttribute(key, value)
  }

  operator fun <T : Any> set(key: KClass<T>, value: T?) {
    current.setAttribute(key.java, value)
  }

  inline fun <reified T : Serializable> getOrPut(noinline defaultValue: () -> T): T = getOrPut(T::class, defaultValue)

  fun <T : Serializable> getOrPut(key: KClass<T>, defaultValue: () -> T): T {
    val value: T? = get(key)
    return if (value == null) {
      val answer: T = defaultValue()
      set(key, answer)
      answer
    } else {
      value
    }
  }
}

val currentRequest: VaadinRequest
  get() = VaadinService.getCurrentRequest() ?: throw IllegalStateException("No current request")
val currentResponse: VaadinResponse
  get() = VaadinService.getCurrentResponse() ?: throw IllegalStateException("No current response")

object Cookies {
  operator fun get(name: String): Cookie? = currentRequest.cookies?.firstOrNull { it.name == name }

  operator fun set(name: String, cookie: Cookie?) {
    if (cookie == null) {
      val newCookie = Cookie(name, null)
      newCookie.maxAge = 0  // delete immediately
      newCookie.path = "/"
      currentResponse.addCookie(newCookie)
    } else {
      currentResponse.addCookie(cookie)
    }
  }

  fun delete(name: String) {
    set(name, null)
  }
}

infix operator fun Cookies.plusAssign(cookie: Cookie) {
  set(cookie.name, cookie)
}

infix operator fun Cookies.minusAssign(cookie: Cookie) {
  set(cookie.name, null)
}

