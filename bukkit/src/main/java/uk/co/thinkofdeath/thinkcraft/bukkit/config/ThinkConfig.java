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

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public abstract class ThinkConfig {

    private final FileConfiguration configuration;
    private final File file;

    public ThinkConfig(File file) {
        this.file = file;
        configuration = new YamlConfiguration();
    }

    public void load() throws IOException, InvalidConfigurationException, InvalidConfigFieldException {
        if (file.exists()) {
            configuration.load(file);
        }

        try {
            for (Field field : getClass().getDeclaredFields()) {
                ConfigField configField = field.getAnnotation(ConfigField.class);
                if (configField == null) continue;
                field.setAccessible(true);

                Object defValue = field.get(this);
                if (defValue != null) {
                    configuration.addDefault(configField.value(), defValue);
                }
                Object value = configuration.get(configField.value());
                if (field.getType().isInstance(value)) {
                    field.set(this, value);
                } else if (field.getType().isPrimitive()) {
                    if (value instanceof Number) {
                        Number number = (Number) value;
                        if (field.getType() == int.class) {
                            field.setInt(this, number.intValue());
                        } else if (field.getType() == long.class) {
                            field.setLong(this, number.longValue());
                        } else if (field.getType() == short.class) {
                            field.setShort(this, number.shortValue());
                        } else if (field.getType() == byte.class) {
                            field.setByte(this, number.byteValue());
                        } else if (field.getType() == float.class) {
                            field.setFloat(this, number.floatValue());
                        } else if (field.getType() == double.class) {
                            field.setDouble(this, number.doubleValue());
                        } else {
                            throw new InvalidConfigFieldException(field, value);
                        }
                    } else if (value instanceof Boolean) {
                        if (field.getType() == boolean.class) {
                            field.setBoolean(this, (Boolean) value);
                        } else {
                            throw new InvalidConfigFieldException(field, value);
                        }
                    } else {
                        throw new InvalidConfigFieldException(field, value);
                    }
                } else {
                    throw new InvalidConfigFieldException(field, value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws IOException {
        try {
            for (Field field : getClass().getDeclaredFields()) {
                ConfigField configField = field.getAnnotation(ConfigField.class);
                if (configField == null) continue;
                field.setAccessible(true);
                Object value = field.get(this);
                configuration.set(configField.value(), value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        configuration.save(file);
    }
}
