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
import walkingkooka.template.TemplateContextTesting2Test.TestTemplateContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursorLineInfos;
import walkingkooka.text.cursor.TextCursors;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TemplateContextTesting2Test implements TemplateContextTesting2<TestTemplateContext> {

    // parseAndRender...................................................................................................

    @Test
    public void testParseAndRender() {
        final String text = "Hello";
        final Template template = Templates.string(text);

        this.parseAndRenderAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor cursor) {
                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                text
        );
    }

    // parse............................................................................................................

    @Test
    public void testParse() {
        final Template template = Templates.string("Hello");

        this.parseAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor cursor) {
                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                template
        );
    }

    @Test
    public void testParseFails() {
        assertThrows(
                AssertionError.class,
                () -> this.parseAndCheck(
                        new TestTemplateContext() {

                            @Override
                            public Template parse(final TextCursor cursor) {
                                return Templates.string("Different");
                            }
                        },
                        TextCursors.fake(),
                        Templates.string("Hello")
                )
        );
    }

    // expression............................................................................................................

    @Test
    public void testExpression() {
        final Template template = Templates.string("Hello");

        this.expressionAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template expression(final TextCursor cursor) {
                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                template
        );
    }

    @Test
    public void testExpressionFails() {
        assertThrows(
                AssertionError.class,
                () -> this.expressionAndCheck(
                        new TestTemplateContext() {

                            @Override
                            public Template expression(final TextCursor cursor) {
                                return Templates.string("Different");
                            }
                        },
                        TextCursors.fake(),
                        Templates.string("Hello")
                )
        );
    }

    // openBrace............................................................................................................

    @Test
    public void testOpenBrace() {
        final Template template = Templates.string("Hello");

        this.openBraceAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template openBrace(final TextCursorLineInfo i) {
                        return template;
                    }
                },
                TextCursorLineInfos.fake(),
                template
        );
    }

    @Test
    public void testOpenBraceFails() {
        assertThrows(
                AssertionError.class,
                () -> this.openBraceAndCheck(
                        new TestTemplateContext() {

                            @Override
                            public Template openBrace(final TextCursorLineInfo i) {
                                return Templates.string("Different");
                            }
                        },
                        TextCursorLineInfos.fake(),
                        Templates.string("Hello")
                )
        );
    }

    // TemplateContext..................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestTemplateContext createContext() {
        return new TestTemplateContext();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }

    @Override
    public Class<TestTemplateContext> type() {
        return TestTemplateContext.class;
    }

    static class TestTemplateContext implements TemplateContext {

        @Override
        public Template parse(final TextCursor cursor) {
            Objects.requireNonNull(cursor, "cursor");
            throw new UnsupportedOperationException();
        }

        @Override
        public Template expression(final TextCursor cursor) {
            Objects.requireNonNull(cursor, "cursor");

            throw new UnsupportedOperationException();
        }

        @Override
        public Template openBrace(final TextCursorLineInfo at) {
            Objects.requireNonNull(at, "at");

            throw new UnsupportedOperationException();
        }

        @Override
        public String templateValue(final TemplateValueName name) {
            Objects.requireNonNull(name, "name");

            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
