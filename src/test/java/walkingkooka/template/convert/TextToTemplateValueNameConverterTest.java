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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.template.TemplateValueName;

public final class TextToTemplateValueNameConverterTest implements ConverterTesting2<TextToTemplateValueNameConverter<FakeConverterContext>, FakeConverterContext>,
        ToStringTesting<TextToTemplateValueNameConverter<FakeConverterContext>> {

    @Test
    public void testConvertStringBuilderToTemplateValueName() {
        final TemplateValueName name = TemplateValueName.with("abc");

        this.convertAndCheck(
                new StringBuilder(name.value()),
                TemplateValueName.class,
                name
        );
    }

    @Test
    public void testConvertStringToTemplateValueName() {
        final TemplateValueName name = TemplateValueName.with("abc");

        this.convertAndCheck(
                name.value(),
                TemplateValueName.class,
                name
        );
    }

    @Override
    public TextToTemplateValueNameConverter<FakeConverterContext> createConverter() {
        return TextToTemplateValueNameConverter.instance();
    }

    @Override
    public FakeConverterContext createContext() {
        return new FakeConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converter.canConvert(
                        value,
                        type,
                        this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                        value,
                        target,
                        this
                );
            }

            private final Converter<FakeConverterContext> converter = Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                TextToTemplateValueNameConverter.instance(),
                "String to TemplateValueName"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TextToTemplateValueNameConverter<FakeConverterContext>> type() {
        return Cast.to(TextToTemplateValueNameConverter.class);
    }
}
