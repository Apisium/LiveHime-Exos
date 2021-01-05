package cn.apisium.livehime.exos.backend

import cn.apisium.livehime.exos.song.ExoSong
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.DataLine

interface Backend {
    fun isAsync(): Boolean
    fun createStream(song: ExoSong): AudioInputStream

    fun pause()
    fun resume()
    fun isPaused(): Boolean

    fun playStream(stream: AudioInputStream)

    fun play(song: ExoSong) {
        playStream(createStream(song))
    }

    fun toggle(song: ExoSong) {
        if (isPaused()) {
            resume()
        } else {
            pause()
        }
    }

}