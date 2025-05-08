package br.edu.ifsp.dmo.feitopormim.model

import android.graphics.Bitmap

class Post(private val descricao: String, private val foto: Bitmap, private val loc: String) {
    public fun getDescricao() : String{
        return descricao
    }

    public fun getFoto() : Bitmap{
        return foto
    }

    public fun getLoc(): String{
        return loc
    }

}