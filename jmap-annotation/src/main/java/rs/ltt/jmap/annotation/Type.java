/*
 * Copyright 2021 Daniel Gultsch
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
 *
 */

package rs.ltt.jmap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Type {
    /**
     * The {@code @type} tag of this data type, or the default {@code @type} value if applied to a sealed class/interface.
     * If the default value (the empty string) is found, the value will be deduced from the class name.
     * For sealed classes/interface, the default value here indicates that there is no default type, and a missing {@code @type} property will error.
     *
     * @return the {@code @type} value, or the empty string for default behavior
     */
    String value() default "";

    interface Dynamic<D> {
        String type();

        D data();
    }
}
