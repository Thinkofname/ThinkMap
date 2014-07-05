/*
 * Copyright 2014 Matthew Collins
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

package uk.co.thinkofdeath.thinkcraft.bukkit.config;

import java.lang.reflect.Field;

public class InvalidConfigFieldException extends Exception {
    public InvalidConfigFieldException(Field field, Object value) {
        super(constructMessage(field, value));
    }

    private static String constructMessage(Field field, Object value) {
        StringBuilder builder = new StringBuilder();
        ConfigField configField = field.getAnnotation(ConfigField.class);
        builder.append("Cannot set '")
                .append(configField.value())
                .append("' to '")
                .append(value)
                .append("'. '")
                .append(configField.value())
                .append("' should be ")
                .append(field.getType().getSimpleName())
                .append(" not ")
                .append(value.getClass().getSimpleName());
        return builder.toString();
    }
}
