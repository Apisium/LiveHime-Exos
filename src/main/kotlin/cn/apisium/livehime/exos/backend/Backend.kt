package cn.apisium.livehime.exos.backend

import cn.apisium.livehime.exos.song.ExoSong
import javax.sound.sampled.AudioInputStream

interface Backend {
    fun isAsync(): Boolean
    fun createStream(song: ExoSong): AudioInputStream

    fun song(): ExoSong?
    fun length(): Long

    var position: Long
        get() = 0L
        set(value) = TODO()

    fun pause()
    fun resume()
    fun isPaused(): Boolean

    fun playStream(stream: AudioInputStream)

    fun play(song: ExoSong) {
        playStream(createStream(song))
    }

    fun toggle() {
        if (isPaused()) {
            resume()
        } else {
            pause()
        }
    }

}