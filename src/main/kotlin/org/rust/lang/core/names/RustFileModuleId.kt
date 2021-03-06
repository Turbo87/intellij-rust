package org.rust.lang.core.names

import org.rust.lang.core.names.parts.RustFileModuleIdPart
import org.rust.lang.core.resolve.indexes.RustModulePath

class RustFileModuleId(path: RustModulePath) : RustQualifiedNameBase<RustFileModuleIdPart>(RustFileModuleIdPart(path))
