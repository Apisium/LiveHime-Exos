package cn.apisium.livehime.exos.song

import java.net.URI
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

data class ExoSong(val uri: URI, val whenFinish: Runnable = Runnable {}) {
    fun stream(): AudioInputStream = AudioSystem.getAudioInputStream(this.uri.toURL())

    override fun equals(other: Any?): Boolean {
        return if (other is ExoSong) {
            this.uri == other.uri
        } else {
            other?.equals(this) ?: false
        }
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}

