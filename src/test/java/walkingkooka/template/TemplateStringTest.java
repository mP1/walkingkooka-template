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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TemplateStringTest implements TemplateTesting2<TemplateString> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> TemplateString.with(null)
        );
    }

    @Test
    public void testWithEmptyString() {
        assertSame(
            TemplateString.EMPTY,
            TemplateString.with("")
        );
    }

    // render...........................................................................................................

    @Test
    public void testRender() {
        final String text = "Hello123";

        this.renderAndCheck(
            TemplateString.with(text),
            TemplateContexts.fake(),
            text
        );
    }

    // templateValueNames...............................................................................................

    @Test
    public void testTemplateValueNames() {
        this.templateValueNamesAndCheck();
    }

    @Override
    public TemplateString createTemplate() {
        return TemplateString.EMPTY;
    }

    @Override
    public TemplateContext createContext() {
        return TemplateContexts.fake();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
            TemplateString.with("different")
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            TemplateString.with("Hello"),
            "Hello"
        );
    }

    @Test
    public void testToStringEscapesBackslash() {
        this.toStringAndCheck(
            TemplateString.with("Hello\\"),
            "Hello\\\\"
        );
    }

    @Test
    public void testToStringIncludesCarriageReturn() {
        this.toStringAndCheck(
            TemplateString.with("Hello\r"),
            "Hello\r"
        );
    }

    @Test
    public void testToStringIncludesNewLine() {
        this.toStringAndCheck(
            TemplateString.with("Hello\n"),
            "Hello\n"
        );
    }

    @Test
    public void testToStringIncludesTab() {
        this.toStringAndCheck(
            TemplateString.with("Hello\t"),
            "Hello\t"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            TemplateString.with("Hello"),
            "TemplateString\n" +
                "  \"Hello\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TemplateString> type() {
        return TemplateString.class;
    }
}
