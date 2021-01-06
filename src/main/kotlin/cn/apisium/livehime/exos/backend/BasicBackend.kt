package cn.apisium.livehime.exos.backend

import cn.apisium.livehime.exos.song.ExoSong
import javax.sound.sampled.*
import kotlin.math.ln
import kotlin.math.pow


class BasicBackend : Backend, LineListener {
    private var clip: Clip = AudioSystem.getClip()

    init {
        clip.addLineListener(this)
    }

    private var song: ExoSong? = null

    private var stoppedByUs = false
    private var paused = false
    override var position: Long
        get() = this.clip.microsecondPosition
        set(value) {
            this.clip.microsecondPosition = value
        }

    private var volumeControl: FloatControl? = null

    override var volume: Float
        get() {
            volumeControl =
                if (volumeControl == null) clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl else volumeControl
            return volumeControl?.value?.let { toPercentageVolume(it) } ?: 0F
        }
        set(value) {
            volumeControl =
                if (volumeControl == null) clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl else volumeControl
            volumeControl?.value = toDBVolume(value)
        }

    private fun toDBVolume(percentage: Float): Float = limit(20 * ln(percentage))
    private fun toPercentageVolume(dB: Float): Float = Math.E.pow(dB.toDouble() / 20).toFloat()

    private fun limit(level: Float): Float {
        return if (volumeControl == null) {
            0F
        } else {
            volumeControl!!.maximum.coerceAtMost(volumeControl!!.minimum.coerceAtLeast(level))
        }
    }

    private var pausedPosition = 0L

    override fun isAsync() = true

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

    override fun song(): ExoSong? = this.song

    override fun length(): Long = this.clip.microsecondLength

    override fun pause() {
        if (clip.isActive && clip.isOpen && clip.isRunning && !paused) {
            paused = true
            stoppedByUs = true
            pausedPosition = clip.microsecondPosition
            clip.stop()
        }
    }

    override fun resume() {
        if (clip.isOpen && paused) {
            paused = false
            stoppedByUs = false
            position = pausedPosition
            clip.start()
        }
    }

    override fun isPaused(): Boolean = this.paused

    override fun playStream(stream: AudioInputStream) {
        if (clip.isRunning) {
            stoppedByUs = true
            clip.stop()
        }
        if (clip.isOpen) {
            clip.close()
        }
        paused = false
        pausedPosition = 0L
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