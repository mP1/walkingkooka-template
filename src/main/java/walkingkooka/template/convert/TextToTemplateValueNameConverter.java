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

package walkingkooka.template.convert;

import walkingkooka.Cast;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.TextToTryingShortCircuitingConverter;
import walkingkooka.template.TemplateValueName;

/**
 * Converts text into a {@link TemplateValueName}.
 */
final class TextToTemplateValueNameConverter<C extends ConverterContext> implements TextToTryingShortCircuitingConverter<C> {

    /**
     * Type safe getter
     */
    static <C extends ConverterContext> TextToTemplateValueNameConverter<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static TextToTemplateValueNameConverter<?> INSTANCE = new TextToTemplateValueNameConverter<>();

    private TextToTemplateValueNameConverter() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final C context) {
        return type == TemplateValueName.class;
    }

    @Override
    public Object parseText(final String text,
                            final Class<?> type,
                            final C context) {
        return TemplateValueName.with(text);
    }

    @Override
    public String toString() {
        return "String to " + TemplateValueName.class.getSimpleName();
    }
}
