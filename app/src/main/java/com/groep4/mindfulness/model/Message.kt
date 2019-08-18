package com.groep4.mindfulness.model

import java.util.Date

/**
 * Simpele Class voor chat
 */
class Message {

    var content: String = ""
    var messageUser: String = ""
    var messageTime: Long = 0
    var isGelezen: Boolean = false

    /**
     * Chat list heeft no-args constructor nodig.
     */
    constructor() {
    }

    constructor(content: String, messageUser: String) {

        this.content = content
        this.messageUser = messageUser
        this.messageTime = Date().time
        this.isGelezen = false
    }

}