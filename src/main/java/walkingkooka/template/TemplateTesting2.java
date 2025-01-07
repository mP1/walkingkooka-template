/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.template;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.text.printer.Printers;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface TemplateTesting2<T extends Template> extends TemplateTesting,
        HashCodeEqualsDefinedTesting2<T>,
        ToStringTesting<T>,
        ClassTesting<T> {

    @Test
    default void testRenderWithNullPrinterFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createTemplate()
                        .render(
                                null,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testRenderWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createTemplate()
                        .render(
                                Printers.fake(),
                                null
                        )
        );
    }

    T createTemplate();

    TemplateContext createContext();

    // hashCode/Object..................................................................................................

    @Override
    default T createObject() {
        return this.createTemplate();
    }
}
