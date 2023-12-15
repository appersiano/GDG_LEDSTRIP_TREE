package com.appersiano.gdg_ledstrip_tree.client

import java.util.UUID


object TreeLedUUID {

    interface IBleElement {
        val uuid: UUID
        val description: String
    }

    object TreeLedLightService : IBleElement {
        override val uuid: UUID = UUID.fromString("1e03ce00-b8bc-4152-85e2-f096236d2833")
        override val description: String = "Tree Light Service"

        object LEDColor : IBleElement {
            override val uuid: UUID = UUID.fromString("1e03ce01-b8bc-4152-85e2-f096236d2833")
            override val description: String = "Led Status"
        }

        object LEDEffect : IBleElement {
            override val uuid: UUID = UUID.fromString("1e03ce02-b8bc-4152-85e2-f096236d2833")
            override val description: String = "Led Color"
        }
    }
}

