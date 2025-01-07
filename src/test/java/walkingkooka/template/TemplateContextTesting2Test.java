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

import walkingkooka.template.TemplateContextTesting2Test.TestTemplateContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;

import java.util.Objects;

public final class TemplateContextTesting2Test implements TemplateContextTesting2<TestTemplateContext> {

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
