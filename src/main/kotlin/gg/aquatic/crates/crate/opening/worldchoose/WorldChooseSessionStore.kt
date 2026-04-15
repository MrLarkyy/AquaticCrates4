package gg.aquatic.crates.crate.opening.worldchoose

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object WorldChooseSessionStore {
    private val sessions = ConcurrentHashMap<UUID, WorldChooseSession>()

    fun register(session: WorldChooseSession): WorldChooseSession {
        sessions[session.player.uniqueId] = session
        return session
    }

    fun current(playerId: UUID): WorldChooseSession? {
        return sessions[playerId]
    }

    fun finish(session: WorldChooseSession) {
        sessions.remove(session.player.uniqueId)
    }
}
