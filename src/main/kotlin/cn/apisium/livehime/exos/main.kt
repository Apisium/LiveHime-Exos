package cn.apisium.livehime.exos

import kotlin.system.exitProcess

fun main() {
    println("Type a sound path:")
    val l = LiveHimeExos.create()
    readLine()?.let { l.play(it) { println("fin") } }
    while (true) {
        print("Type a command: ")
        when (val s = readLine()?.toLowerCase()) {
            "t" -> l.backend().toggle()
            "q" -> exitProcess(0)
            else -> {
                when (s?.get(0)) {
                    'g' -> println("Volume is ${l.backend().volume}")
                    's' -> {
                        if (s.length > 2) {
                            val volume = s.substring(2).toFloat()
                            println("Volume set to $volume")
                            l.backend().volume = volume
                        }
                    }
                }
            }
        }
    }
}