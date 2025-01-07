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
import walkingkooka.InvalidCharacterException;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfos;
import walkingkooka.text.cursor.TextCursors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TemplateDollarHandlerTest implements ClassTesting<TemplateDollarHandler> {

    @Test
    public void testIgnored() {
        assertSame(
                Templates.string(""),
                TemplateDollarHandler.IGNORED.handle(TextCursorLineInfos.fake())
        );
    }

    @Test
    public void testInclude() {
        assertEquals(
                Templates.string("$"),
                TemplateDollarHandler.INCLUDE.handle(TextCursorLineInfos.fake())
        );
    }

    @Test
    public void testThrow() {
        final TextCursor cursor = TextCursors.charSequence("abc$def");
        cursor.next();
        cursor.next();
        cursor.next();

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> TemplateDollarHandler.THROW.handle(cursor.lineInfo())
        );

        this.checkEquals(
                "Invalid character '$' at 3 in \"abc$def\"",
                thrown.getMessage()
        );
    }

    // class............................................................................................................

    @Override
    public Class<TemplateDollarHandler> type() {
        return TemplateDollarHandler.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
