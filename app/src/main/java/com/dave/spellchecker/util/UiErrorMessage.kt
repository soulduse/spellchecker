/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dave.spellchecker.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class Message(@StringRes val messageRes: Int?, vararg formats: Any?) {
    val formatArgs = formats
}

data class ErrorMessage(val isVisible: Boolean, val errorMessage: Message, @DrawableRes val errorImageRes: Int? = 0) {
    companion object {
        fun empty() = ErrorMessage(false, Message(null), null)
    }
}
