package com.mattermost.pasteinputtext

import android.text.InputType
import androidx.core.view.ViewCompat
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.views.textinput.ReactEditText
import com.facebook.react.views.textinput.ReactTextInputManager

@ReactModule(name = "PasteTextInput")
class PasteTextInputManager(context: ReactApplicationContext) : ReactTextInputManager() {
  private var disableCopyPaste: Boolean = false
  private val mContext = context

  override fun getName(): String = NAME

  @ReactProp(name = "disableCopyPaste", defaultBoolean = false)
  fun setDisableCopyPaste(editText: PasteInputEditText, disabled: Boolean) {
    disableCopyPaste = disabled
    val eventDispatcher = getEventDispatcher(mContext, editText)
    editText.customInsertionActionModeCallback = PasteInputActionCallback(editText, disabled, eventDispatcher)
    editText.customSelectionActionModeCallback = PasteInputActionCallback(editText, disabled, eventDispatcher)
    editText.setDisableCopyPaste(disabled)
  }

  private fun getEventDispatcher(reactContext: ReactContext, editText: ReactEditText): EventDispatcher? {
    return UIManagerHelper.getEventDispatcherForReactTag(reactContext, editText.id)
  }

  override fun createViewInstance(context: ThemedReactContext): PasteInputEditText {
    val editText = PasteInputEditText(context)
    val inputType = editText.inputType

    editText.inputType = inputType and (InputType.TYPE_TEXT_FLAG_MULTI_LINE.inv())
    editText.returnKeyType = "done"
    val eventDispatcher = getEventDispatcher(mContext, editText)
    editText.customInsertionActionModeCallback = PasteInputActionCallback(editText, disableCopyPaste, eventDispatcher)
    editText.customSelectionActionModeCallback = PasteInputActionCallback(editText, disableCopyPaste, eventDispatcher)

    return editText
  }

  override fun addEventEmitters(reactContext: ThemedReactContext, editText: ReactEditText) {
    super.addEventEmitters(reactContext, editText)

    val pasteInputEditText = editText as PasteInputEditText
    val eventDispatcher = getEventDispatcher(reactContext, editText)
    pasteInputEditText.setOnPasteListener(PasteInputListener(pasteInputEditText, reactContext.surfaceId), eventDispatcher)

    // Set up OnReceiveContentListener for unified content handling (drag & drop, etc.)
    setupOnReceiveContentListener(pasteInputEditText, eventDispatcher)
  }

  private fun setupOnReceiveContentListener(
    editText: PasteInputEditText,
    eventDispatcher: EventDispatcher?
  ) {
    val mimeTypes = arrayOf("image/*")

    ViewCompat.setOnReceiveContentListener(
      editText,
      mimeTypes,
      androidx.core.view.OnReceiveContentListener { _, payload ->
        val clip = payload.clip
        val description = clip.description

        if (description != null && description.hasMimeType("image/*")) {
          for (i in 0 until clip.itemCount) {
            val item = clip.getItemAt(i)
            val uri = item.uri
            if (uri != null) {
              editText.getOnPasteListener().onPaste(uri, eventDispatcher)
            }
          }
          null
        } else {
          payload
        }
      }
    )
  }

  override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
    val map = super.getExportedCustomBubblingEventTypeConstants()?.toMutableMap()
      ?: return mutableMapOf()

    map["onPaste"] = MapBuilder.of(
      "phasedRegistrationNames",
      MapBuilder.of("bubbled", "onPaste")
    )

    return map
  }

  companion object {
    const val NAME = "PasteTextInput"
    const val CACHE_DIR_NAME = "mmPasteInput"
  }
}
