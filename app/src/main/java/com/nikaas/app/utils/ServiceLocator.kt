package com.nikaas.app.utils

import com.nikaas.app.data.repository.NikaasRepository
import com.nikaas.app.data.repository.NikaasRepositoryImpl

object ServiceLocator {
    
    // Single shared repository instance for sharing reports & incidents in memory
    val repository: NikaasRepository by lazy {
        NikaasRepositoryImpl()
    }
}
