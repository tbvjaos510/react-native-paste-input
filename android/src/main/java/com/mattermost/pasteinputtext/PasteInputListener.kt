package com.mattermost.pasteinputtext

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.webkit.MimeTypeMap
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.events.EventDispatcher
import java.io.File
import java.io.FileOutputStream

class PasteInputListener(editText: PasteInputEditText, surfaceId: Int) : IPasteInputListener {
  private val mEditText = editText
  private val mSurfaceId = surfaceId

  /**
   * Copy content from URI to app's cache directory.
   * This avoids SecurityException when accessing clipboard URIs.
   */
  private fun copyUriToCache(context: Context, uri: Uri, mimeType: String): File? {
    try {
      val cacheDir = File(context.cacheDir, PasteTextInputManager.CACHE_DIR_NAME)
      if (!cacheDir.exists()) {
        cacheDir.mkdirs()
      }

      val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
      val fileName = "paste_${System.currentTimeMillis()}.$extension"
      val destFile = File(cacheDir, fileName)

      context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(destFile).use { output ->
          input.copyTo(output)
        }
      } ?: return null

      return destFile
    } catch (e: Exception) {
      return null
    }
  }

  override fun onPaste(itemUri: Uri, eventDispatcher: EventDispatcher?) {
    val reactContext = mEditText.context as ReactContext
    val uriString: String = itemUri.toString()

    // Handle HTTP URLs separately
    if (uriString.startsWith("http")) {
      val pastImageFromUrlThread = Thread(PasteInputFileFromUrl(
        mEditText,
        uriString,
        mSurfaceId,
        eventDispatcher
      ))
      pastImageFromUrlThread.start()
      return
    }

    // Try to get content type
    var mimeType = reactContext.contentResolver.getType(itemUri)

    // If content type is null, try to get from ClipData
    if (mimeType == null) {
      val clipboardManager = reactContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = clipboardManager.primaryClip
      if (clipData != null && clipData.description.mimeTypeCount > 0) {
        mimeType = clipData.description.getMimeType(0)
      }
    }

    // Default to image/jpeg if still null
    if (mimeType == null) {
      mimeType = "image/jpeg"
    }

    // Special handle for Google docs
    if (uriString == "content://com.google.android.apps.docs.editors.kix.editors.clipboard") {
      val clipboardManager = reactContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = clipboardManager.primaryClip ?: return
      val item = clipData.getItemAt(0) ?: return
      val htmlText = item.htmlText ?: return

      // Find uri from html
      val matcher = Patterns.WEB_URL.matcher(htmlText)
      if (matcher.find()) {
        val urlString = htmlText.substring(matcher.start(1), matcher.end())
        val pastImageFromUrlThread = Thread(PasteInputFileFromUrl(
          mEditText,
          urlString,
          mSurfaceId,
          eventDispatcher
        ))
        pastImageFromUrlThread.start()
      }
      return
    }

    // Copy content URI to cache file
    val cachedFile = copyUriToCache(reactContext, itemUri, mimeType)
    if (cachedFile == null) {
      val error = Arguments.createMap()
      error.putString("message", "Failed to copy image from clipboard")
      val event = Arguments.createMap()
      event.putArray("data", null)
      event.putMap("error", error)
      eventDispatcher?.dispatchEvent(PasteTextInputPasteEvent(mSurfaceId, mEditText.id, event))
      return
    }

    val fileName = cachedFile.name
    val fileSize = cachedFile.length()
    val filePath = cachedFile.absolutePath

    val file = Arguments.createMap()
    file.putString("type", mimeType)
    file.putDouble("fileSize", fileSize.toDouble())
    file.putString("fileName", fileName)
    file.putString("uri", "file://$filePath")

    val files = Arguments.createArray()
    files.pushMap(file)

    val event = Arguments.createMap()
    event.putArray("data", files)
    event.putMap("error", null)

    eventDispatcher?.dispatchEvent(PasteTextInputPasteEvent(mSurfaceId, mEditText.id, event))
  }
}
