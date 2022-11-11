package top.lanscarlos.vulpecula.kether.live

import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.run
import taboolib.platform.util.toProxyLocation
import top.lanscarlos.vulpecula.utils.*

/**
 * Vulpecula
 * top.lanscarlos.vulpecula.kether.live
 *
 * @author Lanscarlos
 * @since 2022-11-10 15:17
 */
class VectorLiveData(
    val value: Any
) : LiveData<Vector> {

    override fun get(frame: ScriptFrame, def: Vector): Vector {

        val it = if (value is ParsedAction<*>) {
            frame.run(value).join()
        } else value

        return when (it) {
            is Vector -> it
            is org.bukkit.util.Vector -> Vector(it.x, it.y, it.z)
            is Location -> it.direction
            is org.bukkit.Location -> it.toProxyLocation().direction
            is Triple<*, *, *> -> {
                val x = (it.first as? DoubleLiveData)?.get(frame, def.x) ?: def.x
                val y = (it.second as? DoubleLiveData)?.get(frame, def.x) ?: def.x
                val z = (it.third as? DoubleLiveData)?.get(frame, def.x) ?: def.x
                Vector(x, y, z)
            }
            else -> def
        }
    }

    companion object {

        /**
         * ~ x y z
         * ~ &x &y &z
         * ~ to &vec
         *
         * */
        fun read(reader: QuestReader): LiveData<Vector> {
            val value: Any = if (reader.hasNextToken("to")) {
                reader.nextBlock()
            } else {
                val x = reader.readDouble()
                val y = reader.readDouble()
                val z = reader.readDouble()
                Triple(x, y, z)
            }
            return VectorLiveData(value)
        }
    }
}