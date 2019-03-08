package com.lisocean.musicplayer.model.local

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore.Audio.Media as M
import com.lisocean.musicplayer.model.data.local.AudioMediaBean
import com.lisocean.musicplayer.helper.parse
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CpDatabase private constructor(private val context: Context){
    /**
     * get content provider's music list<AudioMediaBean>
     */
    private val observable =  object : Single<List<AudioMediaBean>>(){
        override fun subscribeActual(observer: SingleObserver<in List<AudioMediaBean>>) {
            val cursor =
                context.applicationContext.contentResolver.query(
                    M.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        M._ID,
                        M.DATA,
                        M.SIZE,
                        M.DISPLAY_NAME,
                        M.ARTIST,
                        M.DURATION,
                        M.TITLE,
                        M.ALBUM,
                        M.ALBUM_ID,
                        M.MIME_TYPE,
                        M.ARTIST_ID),
                    null, null, null)
            val musicOfList = cursor.parse()
            cursor?.close()

            observer.onSuccess(musicOfList)
        }

    }

    fun getCpMusic() =
        observable
              //Eliminate quick serial clicks caused by hand shake
            .subscribeOn(Schedulers.io())                                //io thread
            .observeOn(AndroidSchedulers.mainThread())                     //block will run in main thread
            .onErrorReturn { listOf() }





    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE : CpDatabase? = null
        fun getInstance(context: Context) : CpDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CpDatabase(context).also { INSTANCE = it }
            }
    }
}