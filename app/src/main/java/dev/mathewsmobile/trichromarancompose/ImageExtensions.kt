package dev.mathewsmobile.trichromarancompose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.createBitmap


fun saveColorChannelToImage(imageProxy: ImageProxy, channel: Channel): Bitmap? {
    return try {
        val bitmap = imageProxyToBitmap(imageProxy) //Crucial step: Convert ImageProxy to Bitmap

        if (bitmap == null) {
            //log and handle
            Log.e("TriChromaran", "bitmap from imageProxy is null while generating $channel")
            return null;
        }
        val colorChannelBitmap = createChannelBitmap(bitmap, channel)

        colorChannelBitmap

    } catch (e: IOException) {
        e.printStackTrace() // Or handle the exception in a more appropriate way
        null
    } finally {
        imageProxy.close() // Always close the ImageProxy! VERY IMPORTANT
    }
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return when (image.format) {
        ImageFormat.YUV_420_888 -> yuv420toNV21(image).let { nv21 ->
            nv21ToBitmap(nv21, image.width, image.height)
        }
        ImageFormat.JPEG -> jpegToBitmap(image)

        else -> null // Unsupported format
    }
}


private fun yuv420toNV21(image: ImageProxy): ByteArray {
    val crop = image.cropRect
    val format = image.format
    val width = crop.width()
    val height = crop.height()
    val planes = image.planes
    val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
    val rowData = ByteArray(planes[0].rowStride)

    var channelOffset = 0
    var outputStride = 1

    for (i in planes.indices) {
        when (i) {
            0 -> {
                channelOffset = 0
                outputStride = 1
            }
            1 -> {
                channelOffset = width * height + 1
                outputStride = 2
            }
            2 -> {
                channelOffset = width * height
                outputStride = 2
            }
        }

        val buffer = planes[i].buffer
        val rowStride = planes[i].rowStride
        val pixelStride = planes[i].pixelStride

        val shift = if (i == 0) 0 else 1
        val w = width shr shift
        val h = height shr shift
        buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
        for (row in 0 until h) {
            var length: Int
            if (pixelStride == 1 && outputStride == 1) {
                length = w
                buffer.get(data, channelOffset, length)
                channelOffset += length
            } else {
                length = (w - 1) * pixelStride + 1
                buffer.get(rowData, 0, length)
                for (col in 0 until w) {
                    data[channelOffset] = rowData[col * pixelStride]
                    channelOffset += outputStride
                }
            }
            if (row < h - 1) {
                buffer.position(buffer.position() + rowStride - length)
            }
        }
    }
    return data
}

private fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap {
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 90, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

private fun jpegToBitmap(image: ImageProxy): Bitmap? {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
}


enum class Channel {
    Red, Green, Blue
}

fun createChannelBitmap(originalBitmap: Bitmap, channels: Channel): Bitmap {
    val matrix = when (channels) {
        Channel.Red -> floatArrayOf(
            1f, 0f, 0f, 0f, 0f,  // Red channel
            0f, 0f, 0f, 0f, 0f,  // Green channel to 0
            0f, 0f, 0f, 0f, 0f,  // Blue channel to 0
            0f, 0f, 0f, 1f, 0f   // Alpha channel remains the same
        )
        Channel.Green -> floatArrayOf(
            0f, 0f, 0f, 0f, 0f,  // Red channel
            0f, 1f, 0f, 0f, 0f,  // Green channel to 0
            0f, 0f, 0f, 0f, 0f,  // Blue channel to 0
            0f, 0f, 0f, 1f, 0f   // Alpha channel remains the same
        )
        Channel.Blue -> floatArrayOf(
            0f, 0f, 0f, 0f, 0f,  // Red channel
            0f, 0f, 0f, 0f, 0f,  // Green channel to 0
            0f, 0f, 1f, 0f, 0f,  // Blue channel to 0
            0f, 0f, 0f, 1f, 0f   // Alpha channel remains the same
        )
    }

    val width = originalBitmap.width
    val height = originalBitmap.height
    val colorChannelBitmap = createBitmap(width, height)
    val canvas = Canvas(colorChannelBitmap)
    val paint = Paint()

    val colorMatrix = ColorMatrix().apply {
        set(matrix)
    }

    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

    return colorChannelBitmap
}

fun combineRGBChannels(redBitmap: Bitmap, greenBitmap: Bitmap, blueBitmap: Bitmap): Bitmap {
    // 1. Input Validation and Size Matching:
    require(
        redBitmap.width == greenBitmap.width && redBitmap.width == blueBitmap.width &&
                redBitmap.height == greenBitmap.height && redBitmap.height == blueBitmap.height
    ) { "Input bitmaps must have the same dimensions" }

    val width = redBitmap.width
    val height = redBitmap.height

    // 2. Create the Output Bitmap:
    val combinedBitmap = createBitmap(width, height)

    // 3. Efficient Pixel-by-Pixel Combination:
    val redPixels = IntArray(width * height)
    val greenPixels = IntArray(width * height)
    val bluePixels = IntArray(width * height)

    redBitmap.getPixels(redPixels, 0, width, 0, 0, width, height)
    greenBitmap.getPixels(greenPixels, 0, width, 0, 0, width, height)
    blueBitmap.getPixels(bluePixels, 0, width, 0, 0, width, height)

    val combinedPixels = IntArray(width * height)
    for (i in 0 until width * height) {
        val red = Color.red(redPixels[i])   // Extract red component
        val green = Color.green(greenPixels[i]) // Extract green component
        val blue = Color.blue(bluePixels[i])  // Extract blue component

        combinedPixels[i] = Color.rgb(red, green, blue) // Combine into a single ARGB color
    }

    combinedBitmap.setPixels(combinedPixels, 0, width, 0, 0, width, height)

    return combinedBitmap
}