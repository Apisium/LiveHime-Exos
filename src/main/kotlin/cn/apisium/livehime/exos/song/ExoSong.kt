package cn.apisium.livehime.exos.song

import java.io.File
import java.net.URI
import java.util.function.Consumer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

data class ExoSong(val uri: URI, val whenFinish:Runnable){
    constructor(uri:URI) : this(uri,{})
    fun stream():AudioInputStream = AudioSystem.getAudioInputStream(this.uri.toURL())
}

