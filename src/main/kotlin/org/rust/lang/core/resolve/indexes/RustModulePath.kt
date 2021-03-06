package org.rust.lang.core.resolve.indexes

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.io.IOUtil
import org.rust.cargo.project.module.util.getSourceRoots
import org.rust.cargo.project.module.util.relativise
import org.rust.lang.core.psi.RustFileModItem
import org.rust.lang.core.psi.impl.RustFileImpl
import org.rust.lang.core.psi.util.getCrate
import java.io.DataInput
import java.io.DataOutput
import java.util.*

/**
 * URI for the particular module of the Crate
 */
data class RustModulePath private constructor (private val name: String, val path: String) {

    fun findModuleIn(p: Project): RustFileModItem? =
        run {
            ModuleManager.getInstance(p).findModuleByName(name)?.let { crate ->
                crate.getSourceRoots(includingTestRoots = true)
                    .mapNotNull { sourceRoot ->
                        sourceRoot.findChild(path)
                    }
                    .firstOrNull()
                   ?.let {
                       PsiManager.getInstance(p).findFile(it)
                   }
            } as? RustFileImpl
        }
            ?.let { it.mod as RustFileModItem }

    override fun hashCode(): Int =
        Objects.hash(name, path)

    override fun equals(other: Any?): Boolean =
        (other as? RustModulePath)?.let {
            it.name.equals(name) && it.path.equals(path)
        } ?: false

    companion object {

        fun devise(f: PsiFile): RustModulePath? =
            f.getCrate().let { crate ->
                crate.relativise(f.virtualFile ?: f.viewProvider.virtualFile)?.let { path ->
                    RustModulePath(crate.name, path)
                }
            }

        fun writeTo(out: DataOutput, path: RustModulePath) {
            IOUtil.writeUTF(out, path.name)
            IOUtil.writeUTF(out, path.path)
        }

        fun readFrom(`in`: DataInput): RustModulePath? {
            return RustModulePath(IOUtil.readUTF(`in`), IOUtil.readUTF(`in`))
        }

    }
}

