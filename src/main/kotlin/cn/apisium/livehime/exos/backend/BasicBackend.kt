package cn.apisium.livehime.exos.backend

import cn.apisium.livehime.exos.song.ExoSong
import java.lang.UnsupportedOperationException
import javax.sound.sampled.*


class BasicBackend : Backend, LineListener {
    var clip: Clip = AudioSystem.getClip()
    var song: ExoSong? = null

    init {
        clip.addLineListener(this)
    }

    override fun isAsync(): Boolean {
        return false
    }

    override fun createStream(song: ExoSong): AudioInputStream {
        this.song = song
        var stream = song.stream()
        val format = stream.format
        if (format.encoding !== AudioFormat.Encoding.PCM_SIGNED) {
            stream = AudioSystem.getAudioInputStream(
                AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.sampleRate,
                    if (format.sampleSizeInBits > 0) format.sampleSizeInBits else 16,
                    format.channels,
                    if (format.sampleSizeInBits > 0) format.channels * format.sampleSizeInBits / 8 else format.channels * 2,
                    format.sampleRate,
                    false
                ), stream
            )
        }
        return stream
    }

    override fun pause() {
        throw UnsupportedOperationException("Clip should not be paused")
    }

    override fun resume() { /* Do nothing */
    }

    override fun isPaused(): Boolean = false

    var stoppedByUs = false
    override fun playStream(stream: AudioInputStream) {
        if (clip.isRunning) {
            stoppedByUs = true
            clip.stop()
        }
        if (clip.isOpen) {
            clip.close()
        }
        clip.open(stream)
        clip.start()
    }

    override fun update(event: LineEvent?) {
        if (event?.type == LineEvent.Type.STOP && !stoppedByUs) {
            stoppedByUs = false
            song?.whenFinish?.run()
        }
    }
}