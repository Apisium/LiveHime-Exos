package cn.apisium.livehime.exos

import cn.apisium.livehime.exos.backend.Backend
import cn.apisium.livehime.exos.backend.BasicBackend
import cn.apisium.livehime.exos.song.ExoSong
import java.io.File
import java.net.URI
import java.util.function.Consumer

class LiveHimeExos private constructor(private val backend: Backend) {
    companion object {
        fun create(): LiveHimeExos {
            return LiveHimeExos(BasicBackend())
        }
    }

    fun backend(): Backend = this.backend

    fun play(uri: URI, whenFinish: Runnable = Runnable { }) {
        backend().play(ExoSong(uri, whenFinish))
    }

    fun play(file: File, whenFinish: Runnable = Runnable { }) {
        play(file.toURI(), whenFinish)
    }

    fun play(name: String, whenFinish: Runnable = Runnable { }) {
        play(File(name), whenFinish)
    }

    fun play(name: String, file: File, whenFinish: Runnable = Runnable { }) {
        play(File(file, name), whenFinish)
    }
}