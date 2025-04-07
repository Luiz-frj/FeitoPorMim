package br.edu.ifsp.dmo.feitopormim

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

class Base64Converter
{
    companion object {
        fun drawableToString(drawable: Drawable) : String {
            val pictureDrawable = drawable as BitmapDrawable
            val bitmap = pictureDrawable.bitmap.scale(150, 150)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val imageString = Base64.encodeToString(outputStream.toByteArray(),0)
            return imageString
        }
        fun stringToBitmap(imageString: String) : Bitmap {
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
            return decodedImage
        }
    }
}